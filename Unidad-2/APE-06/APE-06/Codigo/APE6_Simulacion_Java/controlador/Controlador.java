package controlador;

import modelo.LCG;
import modelo.MonteCarlo;
import modelo.Pruebas;
import vista.Grafica;
import vista.Tabla;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Controlador · Coordinación de la Práctica Nro. 06 (patrón MVC).
 *
 * Pasos (guía):
 *  1. Genera 10 000 números pseudoaleatorios con el LCG (Algoritmo 1).
 *  2. Aplica la prueba Chi-cuadrado (Algoritmo 2).
 *  3. Aplica la prueba Kolmogorov-Smirnov + gráficas (Algoritmo 3).
 *  4. Compara la secuencia Java contra la secuencia Python (mismos parámetros).
 *  5. Construye la tabla de Resultados Esperados: π por Monte Carlo,
 *     error y tiempos, para N = 1 000 … 1 000 000, en Java y Python.
 *  6. Exporta los CSV a la carpeta Resultados/.
 */
public class Controlador {

    public static final int N_GUIA = 10_000;
    public static final int[] NIVEL_N = {1_000, 10_000, 100_000, 1_000_000};
    public static final int PRIMEROS = 1_000;

    private final long semilla;
    private final int n;
    private final Path carpetaResultados;

    public Controlador(long semilla, int n) {
        this.semilla = semilla;
        this.n = n;
        String env = System.getenv("APE6_RES");
        this.carpetaResultados = (env != null && !env.isEmpty())
                ? Paths.get(env)
                : Paths.get("..", "..", "Resultados").toAbsolutePath().normalize();
        try {
            Files.createDirectories(carpetaResultados);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear Resultados/: " + e.getMessage());
        }
    }

    public void ejecutar(boolean sinGraficas) {
        LCG lcg = new LCG(semilla, LCG.A_GUIA, LCG.C_GUIA, LCG.M_GUIA);

        // ---------- Algoritmo 1: generación ----------
        Tabla.imprimirTitulo("APE-06 · Práctica 06 · Validación Chi-cuadrado y "
                + "Kolmogorov-Smirnov (JAVA)");
        System.out.printf(Locale.ROOT, "Parámetros LCG: a=%d, c=%d, m=%d, X(0)=%d%n",
                lcg.getA(), lcg.getC(), lcg.getM(), lcg.getSemillaInicial());
        long t0 = System.nanoTime();
        double[] secuencia = lcg.generarSecuencia(n);
        double tGen = (System.nanoTime() - t0) / 1e9;
        Tabla.imprimirMuestra(secuencia, 10);
        System.out.printf(Locale.ROOT,
                "  Tiempo de generación de %,d números: %.4f s%n", n, tGen);

        // ---------- Algoritmo 2: Chi-cuadrado ----------
        Pruebas.ChiCuadrado resChi = Pruebas.chiCuadrado(secuencia, Pruebas.K_INTERVALOS);
        Tabla.imprimirChi(resChi);

        // ---------- Algoritmo 3: Kolmogorov-Smirnov ----------
        Pruebas.KolmogorovSmirnov resKs = Pruebas.kolmogorovSmirnov(secuencia);
        Tabla.imprimirKs(resKs);

        guardarPruebas(resChi, resKs, secuencia);

        // ---------- Gráficas ----------
        if (!sinGraficas) {
            try {
                String r1 = Grafica.graficarHistograma(resChi,
                        "Histograma de frecuencias (Chi-cuadrado)");
                String r2 = Grafica.graficarKS(secuencia, resKs,
                        "Kolmogorov-Smirnov: F empírica vs F teórica");
                System.out.println("\nGráficas guardadas en:\n  " + r1 + "\n  " + r2);
            } catch (Exception e) {
                System.out.println("\n(Aviso) No se pudieron generar las gráficas: " + e.getMessage());
            }
        }

        // ---------- Comparación Java vs Python ----------
        compararConPython(secuencia);

        // ---------- Tabla de Resultados Esperados (Monte Carlo π) ----------
        List<Tabla.ControladorTabla> filas = tablaPi();
        Tabla.imprimirTablaPi(filas);
        guardarTablaPi(filas);

        Tabla.imprimirTitulo("Fin de la ejecución (Java)");
    }

    // ------------------------------------------------------------------
    private void compararConPython(double[] secuenciaJava) {
        Path ruta = carpetaResultados.resolve("python_secuencia.csv");
        if (!Files.exists(ruta)) {
            System.out.println("\n(Aviso) Ejecuta primero la versión Python para comparar secuencias:");
            System.out.println("        cd ../APE6_Simulacion && python3 main.py");
            return;
        }
        double[] valoresPython = leerCsvU(ruta);
        int nCmp = Math.min(PRIMEROS, Math.min(valoresPython.length, secuenciaJava.length));
        boolean identicas = true;
        for (int i = 0; i < nCmp; i++) {
            if (Math.abs(secuenciaJava[i] - valoresPython[i]) > 1e-12) { identicas = false; break; }
        }
        Tabla.imprimirComparacion(valoresPython, secuenciaJava, identicas);

        Path salida = carpetaResultados.resolve("comparacion_secuencias.csv");
        List<String> lineas = new ArrayList<>();
        lineas.add("i,python_u_i,java_u_i,iguales");
        for (int i = 0; i < nCmp; i++) {
            boolean ig = Math.abs(secuenciaJava[i] - valoresPython[i]) <= 1e-12;
            lineas.add(String.format(Locale.ROOT, "%d,%.15f,%.15f,%s",
                    i + 1, valoresPython[i], secuenciaJava[i], ig));
        }
        escribir(salida, lineas);
        System.out.println("  Comparación completa guardada en: " + salida);
    }

    private List<Tabla.ControladorTabla> tablaPi() {
        List<Tabla.ControladorTabla> filas = new ArrayList<>();
        System.out.println();
        for (int nPuntos : NIVEL_N) {
            LCG lcg = new LCG(semilla, LCG.A_GUIA, LCG.C_GUIA, LCG.M_GUIA);
            long t0 = System.nanoTime();
            double[] s = lcg.generarSecuencia(nPuntos);
            MonteCarlo.Resultado r = MonteCarlo.estimarPi(s);
            double tJava = (System.nanoTime() - t0) / 1e9;
            filas.add(new Tabla.ControladorTabla(nPuntos, r.piEstimado, r.error, tJava,
                    null, null, null));
            System.out.printf(Locale.ROOT,
                    "  Java · N = %,9d → π ≈ %.6f  error = %.6f  t = %.4f s%n",
                    nPuntos, r.piEstimado, r.error, tJava);
        }
        return mezclarPython(filas);
    }

    /** Añade a la tabla los resultados Python si ya existen en Resultados/. */
    private List<Tabla.ControladorTabla> mezclarPython(List<Tabla.ControladorTabla> filas) {
        Path ruta = carpetaResultados.resolve("python_tabla_pi.csv");
        if (!Files.exists(ruta)) return filas;
        try (BufferedReader br = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea = br.readLine();                    // cabecera
            var datos = new java.util.HashMap<Integer, String[]>();
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(",");
                datos.put(Integer.parseInt(p[0]), p);
            }
            List<Tabla.ControladorTabla> mezcladas = new ArrayList<>();
            for (Tabla.ControladorTabla f : filas) {
                String[] p = datos.get(f.n);
                if (p != null) {
                    mezcladas.add(new Tabla.ControladorTabla(f.n,
                            f.piPy, f.errPy, f.tPy,
                            Double.parseDouble(p[1]), Double.parseDouble(p[2]),
                            Double.parseDouble(p[3])));
                } else {
                    mezcladas.add(f);
                }
            }
            return mezcladas;
        } catch (IOException | NumberFormatException e) {
            System.out.println("(Aviso) No se pudo leer python_tabla_pi.csv: " + e.getMessage());
            return filas;
        }
    }

    // ------------------------------------------------------------------
    private void guardarPruebas(Pruebas.ChiCuadrado chi, Pruebas.KolmogorovSmirnov ks,
                                double[] secuencia) {
        List<String> lineas = new ArrayList<>();
        lineas.add("prueba,estadistico,parametro,valor_p,decision");
        lineas.add(String.format(Locale.ROOT, "Chi-cuadrado,%.6f,gl=%d,%.6f,%s",
                chi.estadistico, chi.gl, chi.valorP, chi.decision));
        lineas.add(String.format(Locale.ROOT, "Kolmogorov-Smirnov,%.6f,n=%d,%.6f,%s",
                ks.estadistico, ks.n, ks.valorP, ks.decision));
        escribir(carpetaResultados.resolve("java_resultados_pruebas.csv"), lineas);

        List<String> frec = new ArrayList<>();
        frec.add("intervalo,lim_inf,lim_sup,observada,esperada");
        for (int i = 0; i < chi.k; i++) {
            frec.add(String.format(Locale.ROOT, "%d,%.1f,%.1f,%d,%.1f",
                    i + 1, i / (double) chi.k, (i + 1) / (double) chi.k,
                    chi.observadas[i], chi.esperada));
        }
        escribir(carpetaResultados.resolve("java_frecuencias_chi.csv"), frec);

        List<String> sec = new ArrayList<>();
        sec.add("i,u_i");
        for (int i = 0; i < PRIMEROS; i++) {
            sec.add(String.format(Locale.ROOT, "%d,%.15f", i + 1, secuencia[i]));
        }
        escribir(carpetaResultados.resolve("java_secuencia.csv"), sec);
        System.out.println("\nResultados Java guardados en: " + carpetaResultados);
    }

    private void guardarTablaPi(List<Tabla.ControladorTabla> filas) {
        List<String> lineas = new ArrayList<>();
        lineas.add("n,pi_estimado,error,tiempo_seg");
        for (Tabla.ControladorTabla f : filas) {
            lineas.add(String.format(Locale.ROOT, "%d,%.6f,%.6f,%.4f",
                    f.n, f.piPy, f.errPy, f.tPy));
        }
        escribir(carpetaResultados.resolve("java_tabla_pi.csv"), lineas);
    }

    // ------------------------------------------------------------------
    private double[] leerCsvU(Path ruta) {
        try (BufferedReader br = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            List<Double> vals = new ArrayList<>();
            String linea = br.readLine();                    // cabecera
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(",");
                vals.add(Double.parseDouble(p[1]));
            }
            double[] out = new double[vals.size()];
            for (int i = 0; i < out.length; i++) out[i] = vals.get(i);
            return out;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + ruta + ": " + e.getMessage());
        }
    }

    private void escribir(Path ruta, List<String> lineas) {
        try {
            Files.write(ruta, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("(Aviso) No se pudo escribir " + ruta + ": " + e.getMessage());
        }
    }
}
