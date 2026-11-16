package controlador;

import modelo.LCG;
import modelo.MonteCarlo;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Controlador · Coordinación de la Práctica Nro. 07 (patrón MVC, sin vista).
 *
 * A diferencia de los APE anteriores, la guía de esta práctica no exige
 * visualización gráfica: la «pequeña tabla» para observar los datos se
 * imprime en consola desde este controlador y los resultados completos se
 * exportan en CSV a la carpeta Resultados/.
 *
 * Pasos (guía):
 *  1. Ejecuta el experimento Monte Carlo para estimar π con el LCG,
 *     para N = 1 000, 10 000, 100 000 y 1 000 000 de puntos.
 *  2. Registra por nivel: π estimado, error absoluto y tiempo de ejecución
 *     (generación de la secuencia + conteo de puntos, sin escritura de CSV).
 *  3. Exporta los primeros 1 000 valores U(i) para comparar la secuencia
 *     con la versión Python (mismos parámetros → misma secuencia).
 *  4. Repite el experimento con varias semillas (N = 10 000) para analizar
 *     cómo varía el error de una ejecución a otra.
 *  5. Si detecta los CSV de la versión Python, mezcla la tabla cruzada
 *     Python vs Java y compara las secuencias valor a valor.
 *  6. Exporta todos los CSV a la carpeta Resultados/.
 */
public class Controlador {

    public static final int[] NIVEL_N = {1_000, 10_000, 100_000, 1_000_000};
    public static final int[] SEMILLAS_EXTRA = {12345, 67890, 246810, 13579, 999983};
    public static final int N_SEMILLAS = 10_000;
    public static final int PRIMEROS = 1_000;

    private final long semilla;
    private final Path carpetaResultados;

    public Controlador(long semilla) {
        this.semilla = semilla;
        String env = System.getenv("APE7_RES");
        this.carpetaResultados = (env != null && !env.isEmpty())
                ? Paths.get(env)
                : Paths.get("..", "..", "Resultados").toAbsolutePath().normalize();
        try {
            Files.createDirectories(carpetaResultados);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear Resultados/: " + e.getMessage());
        }
    }

    public void ejecutar() {
        List<Fila> tabla = experimentos();
        guardarTablaJava(tabla);
        guardarSecuencia();
        compararConPython();
        mezclarPython(tabla);
        experimentoSemillas();

        imprimirTabla(tabla);
        guardarTablaMezclada(tabla);
        System.out.println("\nResultados Java guardados en: " + carpetaResultados);
        System.out.println("=".repeat(70));
        System.out.println("Fin de la ejecución (Java)");
        System.out.println("=".repeat(70));
    }

    // ------------------------------------------------------------------
    /** Fila de la tabla de resultados por nivel de N. */
    static class Fila {
        final int n;
        final double piJava, errJava, tJava;
        Double piPy, errPy, tPy;
        Fila(int n, double pi, double err, double t) {
            this.n = n; this.piJava = pi; this.errJava = err; this.tJava = t;
        }
    }

    private List<Fila> experimentos() {
        System.out.println("=".repeat(70));
        System.out.println("APE-07 · Práctica 07 · Estimación de π por Monte Carlo (JAVA)");
        System.out.println("=".repeat(70));
        System.out.printf(Locale.ROOT,
                "Parámetros LCG: a=%d, c=%d, m=%d, X(0)=%d%n",
                LCG.A_GUIA, LCG.C_GUIA, LCG.M_GUIA, semilla);
        System.out.println("Método: π ≈ 4 · Nc / N (cuadrado [-1,1]² con círculo inscrito)");
        System.out.println("N puntos → 2N números pseudoaleatorios (cada punto usa un par u1, u2)");

        List<Fila> filas = new ArrayList<>();
        for (int nPuntos : NIVEL_N) {
            LCG lcg = new LCG(semilla, LCG.A_GUIA, LCG.C_GUIA, LCG.M_GUIA);
            long t0 = System.nanoTime();
            double[] secuencia = lcg.generarSecuencia(2 * nPuntos);   // 2N números → N puntos
            MonteCarlo.Resultado r = MonteCarlo.estimarPi(secuencia);
            double tJava = (System.nanoTime() - t0) / 1e9;
            filas.add(new Fila(nPuntos, r.piEstimado, r.error, tJava));
            System.out.printf(Locale.ROOT,
                    "  Java · N = %,9d puntos → π ≈ %.6f  error = %.6f  t = %.4f s%n",
                    nPuntos, r.piEstimado, r.error, tJava);
        }
        return filas;
    }

    /** Pequeña tabla en consola con los datos de ambos programas. */
    private void imprimirTabla(List<Fila> filas) {
        System.out.println();
        System.out.println("Tabla de Resultados Esperados: Monte Carlo π (Python vs Java)");
        String cab = String.format(Locale.ROOT, "  %10s | %12s | %10s | %13s | %11s",
                "N", "π estimado", "Error", "t Java (s)", "t Python (s)");
        System.out.println(cab);
        System.out.println("  " + "-".repeat(cab.length() - 2));
        for (Fila f : filas) {
            String tPy = (f.tPy == null)
                    ? String.format(Locale.ROOT, "%11s", "— (ejecutar Python)")
                    : String.format(Locale.ROOT, "%11.4f", f.tPy);
            System.out.printf(Locale.ROOT, "  %,10d | %12.6f | %10.6f | %13.4f | %s%n",
                    f.n, f.piJava, f.errJava, f.tJava, tPy);
        }
        System.out.println();
        System.out.println("  Referencia teórica del error: σ(N) = √(π(4−π)/N) ≈ 1.6421/√N");
        for (Fila f : filas) {
            System.out.printf(Locale.ROOT, "    N = %,10d → σ teórico = %.6f%n",
                    f.n, MonteCarlo.errorTeorico(f.n));
        }
    }

    // ------------------------------------------------------------------
    // Exportación de CSV
    // ------------------------------------------------------------------
    private void guardarTablaJava(List<Fila> filas) {
        List<String> lineas = new ArrayList<>();
        lineas.add("n,pi_estimado,error,tiempo_seg");
        for (Fila f : filas) {
            lineas.add(String.format(Locale.ROOT, "%d,%.6f,%.6f,%.4f",
                    f.n, f.piJava, f.errJava, f.tJava));
        }
        escribir(carpetaResultados.resolve("java_tabla_pi.csv"), lineas);
    }

    private void guardarSecuencia() {
        LCG lcg = new LCG(semilla, LCG.A_GUIA, LCG.C_GUIA, LCG.M_GUIA);
        List<String> lineas = new ArrayList<>();
        lineas.add("i,u_i");
        for (int i = 0; i < PRIMEROS; i++) {
            lineas.add(String.format(Locale.ROOT, "%d,%.15f", i + 1, lcg.siguienteU()));
        }
        escribir(carpetaResultados.resolve("java_secuencia.csv"), lineas);
    }

    private void guardarTablaMezclada(List<Fila> filas) {
        List<String> lineas = new ArrayList<>();
        lineas.add("n,pi_python,error_python,tiempo_python_s,pi_java,error_java,tiempo_java_s,error_teorico");
        for (Fila f : filas) {
            String pyPi  = (f.piPy  == null) ? "" : String.format(Locale.ROOT, "%.6f", f.piPy);
            String pyErr = (f.errPy == null) ? "" : String.format(Locale.ROOT, "%.6f", f.errPy);
            String pyT   = (f.tPy   == null) ? "" : String.format(Locale.ROOT, "%.4f", f.tPy);
            lineas.add(String.format(Locale.ROOT, "%d,%s,%s,%s,%.6f,%.6f,%.4f,%.6f",
                    f.n, pyPi, pyErr, pyT, f.piJava, f.errJava, f.tJava,
                    MonteCarlo.errorTeorico(f.n)));
        }
        escribir(carpetaResultados.resolve("tabla_pi_python_java.csv"), lineas);
    }

    // ------------------------------------------------------------------
    // Comparación e integración con la versión Python
    // ------------------------------------------------------------------
    private void compararConPython() {
        Path ruta = carpetaResultados.resolve("python_secuencia.csv");
        if (!Files.exists(ruta)) {
            System.out.println("\n(Aviso) Ejecuta primero la versión Python para comparar secuencias:");
            System.out.println("        cd ../APE7_Simulacion && python3 main.py");
            return;
        }
        double[] valoresPython = leerCsvU(ruta);
        LCG lcg = new LCG(semilla, LCG.A_GUIA, LCG.C_GUIA, LCG.M_GUIA);
        int nCmp = Math.min(PRIMEROS, Math.min(valoresPython.length, PRIMEROS));
        int iguales = 0;
        List<String> lineas = new ArrayList<>();
        lineas.add("i,python_u_i,java_u_i,iguales");
        for (int i = 0; i < nCmp; i++) {
            double uJava = lcg.siguienteU();
            boolean ig = Math.abs(uJava - valoresPython[i]) <= 1e-12;
            if (ig) iguales++;
            lineas.add(String.format(Locale.ROOT, "%d,%.10f,%.10f,%s",
                    i + 1, valoresPython[i], uJava, ig));
        }
        boolean identicas = iguales == nCmp;

        System.out.println();
        System.out.println("Comparación Python vs Java (misma semilla y parámetros)");
        System.out.printf(Locale.ROOT, "  %4s | %18s | %18s%n", "i", "Python U(i)", "Java U(i)");
        for (int i = 0; i < Math.min(nCmp, 10); i++) {
            String[] p = lineas.get(i + 1).split(",");
            System.out.printf(Locale.ROOT, "  %4s | %18s | %18s%n", p[0], p[1], p[2]);
        }
        System.out.printf(Locale.ROOT,
                "%n  Secuencias idénticas: %s (%d de %d valores iguales)%n",
                identicas ? "SÍ ✔" : "NO ✘", iguales, nCmp);

        escribir(carpetaResultados.resolve("comparacion_secuencias.csv"), lineas);
        System.out.println("  Comparación completa guardada en: "
                + carpetaResultados.resolve("comparacion_secuencias.csv"));
    }

    /** Añade a la tabla los resultados Python si ya existen en Resultados/. */
    private void mezclarPython(List<Fila> filas) {
        Path ruta = carpetaResultados.resolve("python_tabla_pi.csv");
        if (!Files.exists(ruta)) {
            System.out.println("\n(Aviso) La tabla cruzada se completa al ejecutar también la versión Python.");
            return;
        }
        try (BufferedReader br = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea = br.readLine();                    // cabecera
            Map<Integer, String[]> datos = new HashMap<>();
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(",");
                datos.put(Integer.parseInt(p[0]), p);
            }
            for (Fila f : filas) {
                String[] p = datos.get(f.n);
                if (p != null) {
                    f.piPy = Double.parseDouble(p[1]);
                    f.errPy = Double.parseDouble(p[2]);
                    f.tPy = Double.parseDouble(p[3]);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("(Aviso) No se pudo leer python_tabla_pi.csv: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Experimento complementario: variación del error según la semilla
    // ------------------------------------------------------------------
    private void experimentoSemillas() {
        System.out.println();
        System.out.println("Experimento complementario: N = 10 000 puntos con distintas semillas");
        List<String> lineas = new ArrayList<>();
        lineas.add("semilla,n_puntos,pi_estimado,error,error_teorico");
        for (int semillaExtra : SEMILLAS_EXTRA) {
            LCG lcg = new LCG(semillaExtra, LCG.A_GUIA, LCG.C_GUIA, LCG.M_GUIA);
            double[] s = lcg.generarSecuencia(2 * N_SEMILLAS);
            MonteCarlo.Resultado r = MonteCarlo.estimarPi(s);
            lineas.add(String.format(Locale.ROOT, "%d,%d,%.6f,%.6f,%.6f",
                    semillaExtra, N_SEMILLAS, r.piEstimado, r.error,
                    MonteCarlo.errorTeorico(N_SEMILLAS)));
            System.out.printf(Locale.ROOT,
                    "  Semilla = %,9d → π ≈ %.6f  error = %.6f%n",
                    semillaExtra, r.piEstimado, r.error);
        }
        escribir(carpetaResultados.resolve("java_error_por_semilla.csv"), lineas);
        System.out.println("  Guardado en: " + carpetaResultados.resolve("java_error_por_semilla.csv"));
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
