package controlador;

import modelo.LCG;
import vista.Grafica;
import vista.Tabla;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Controlador · Coordinación del patrón MVC para el APE-05 (Java).
 *
 * Sigue, en orden, los 8 pasos de la sección 5.1 "Implementación
 * computacional" de la guía de la práctica:
 *
 *     1. Seleccionar a, c, m y semilla.
 *     2. Generar 10 000 valores (aquí en Java; en Python está el paso 2).
 *     3. Implementar el mismo algoritmo en Python (carpeta APE5_Simulacion).
 *     4. Comparar las primeras 20 observaciones.
 *     5. Graficar la secuencia.
 *     6. Analizar el período (incluye el conteo exhaustivo de los 2^32 estados).
 *     7. Cambiar la semilla.
 *     8. Comparar resultados.
 */
public class Controlador {

    /** Semilla alternativa usada en el paso 7 (cambio de semilla). */
    public static final long SEMILLA_ALTERNATIVA = 987654321L;

    private final long semilla;
    private final long semillaAlternativa;
    private final int n;
    private final boolean soloComparar;
    private final boolean conPeriodoEmpirico;

    private final Path raiz;
    private final Path carpetaImg;
    private final Path carpetaResultados;

    private long[] xs;        // estados de la semilla principal
    private double[] us;      // valores U de la semilla principal

    public Controlador(long semilla, long semillaAlternativa, int n,
                       boolean soloComparar, boolean conPeriodoEmpirico) {
        this.semilla = semilla;
        this.semillaAlternativa = semillaAlternativa;
        this.n = n;
        this.soloComparar = soloComparar;
        this.conPeriodoEmpirico = conPeriodoEmpirico;

        this.raiz = localizarRaiz();
        this.carpetaImg = raiz.resolve("img");
        this.carpetaResultados = raiz.resolve("Resultados");
        try {
            Files.createDirectories(carpetaImg);
            Files.createDirectories(carpetaResultados);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudieron crear las carpetas de salida", e);
        }
    }

    /**
     * Localiza la raíz de la práctica (la carpeta APE-05 que contiene img/ y
     * Codigo/) subiendo desde la ubicación de las clases compiladas, de modo
     * que el programa funciona desde cualquier carpeta del repositorio.
     */
    private static Path localizarRaiz() {
        try {
            Path clases = Paths.get(Controlador.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            Path p = clases;
            for (int i = 0; i < 6 && p != null; i++) {
                if (Files.isDirectory(p.resolve("img"))
                        && Files.isDirectory(p.resolve("Codigo"))) {
                    return p;
                }
                p = p.getParent();
            }
        } catch (Exception e) {
            // se usa la carpeta actual como respaldo
        }
        return Paths.get(System.getProperty("user.dir"));
    }

    // ------------------------------------------------------------------
    // Flujo principal
    // ------------------------------------------------------------------
    public void ejecutar() {
        Tabla.cabecera();

        if (soloComparar) {
            compararConPython(semilla);
            compararConPython(semillaAlternativa);
            Tabla.cierre();
            return;
        }

        LCG lcg = new LCG(semilla, LCG.A_GUIA, LCG.C_GUIA, LCG.M_GUIA);

        paso1Parametros(lcg);
        paso2Generar(lcg);
        Tabla.notaPaso3Python();
        paso4Primeras20();
        paso5Graficas();
        paso6Periodo(lcg);
        paso7CambiarSemilla();
        paso8Comparar();
        Tabla.cierre();
    }

    // ------------------------------------------------------------------
    // Pasos de la guía
    // ------------------------------------------------------------------
    private void paso1Parametros(LCG lcg) {
        Tabla.paso(1, "Seleccionar parámetros (a, c, m y semilla)");
        Tabla.parametros(lcg);
    }

    private void paso2Generar(LCG lcg) {
        Tabla.paso(2, "Generar " + Tabla.fmt(n) + " valores en Java");
        xs = lcg.generar(n);
        us = new double[xs.length];
        for (int i = 0; i < xs.length; i++) {
            us[i] = lcg.normalizar(xs[i]);
        }
        Path ruta = rutaResultado("java", semilla);
        guardarCsv(ruta, xs, us);
        Tabla.archivoGuardado(relativa(ruta), (long) xs.length);
    }

    private void paso4Primeras20() {
        Tabla.paso(4, "Comparar las primeras 20 observaciones");
        Tabla.primerasObservaciones(xs, us, 20, "Java · semilla " + semilla);
        compararConPython(semilla);
    }

    private void paso5Graficas() {
        Tabla.paso(5, "Graficar la secuencia");
        List<String> generadas = new ArrayList<String>();
        try {
            Grafica grafica = new Grafica();
            Path r1 = carpetaImg.resolve("secuencia_java_semilla_" + semilla + ".png");
            grafica.secuencia(us, semilla, r1.toFile());
            Path r2 = carpetaImg.resolve("dispersion_java_semilla_" + semilla + ".png");
            grafica.dispersion(us, semilla, r2.toFile());
            Path r3 = carpetaImg.resolve("histograma_java_semilla_" + semilla + ".png");
            grafica.histograma(us, semilla, r3.toFile());
            generadas.add(relativa(r1));
            generadas.add(relativa(r2));
            generadas.add(relativa(r3));
        } catch (IOException e) {
            System.out.println("   ✗ No se pudieron generar las gráficas: " + e.getMessage());
        }
        Tabla.graficasGuardadas(generadas);
    }

    private void paso6Periodo(LCG lcg) {
        Tabla.paso(6, "Analizar el período");

        // a) Análisis teórico: teorema de Hull-Dobell (1962)
        LCG.Verificacion verificacion = lcg.verificarHullDobell();
        Tabla.hullDobell(verificacion, lcg.getM());

        // b) Verificación empírica EXHAUSTIVA: contar el ciclo completo
        if (conPeriodoEmpirico) {
            Tabla.inicioConteoExhaustivo(lcg.getM());
            long t0 = System.nanoTime();
            long periodo = lcg.contarPeriodoEmpirico();
            double segundos = (System.nanoTime() - t0) / 1e9;
            Tabla.resultadoConteoExhaustivo(periodo, lcg.getM(), segundos);
        } else {
            Tabla.conteoOmitido();
        }

        // c) Sin repeticiones dentro de los valores generados
        Set<Long> distintos = new HashSet<Long>();
        for (long x : xs) {
            distintos.add(x);
        }
        Tabla.sinRepeticiones(distintos.size(), xs.length);

        // d) Demostración con módulo pequeño (detección empírica de ciclos)
        LCG.Ciclo bueno = LCG.detectarCiclo(13, 7, 16, 1);
        LCG.Ciclo malo = LCG.detectarCiclo(6, 0, 32, 1);
        Tabla.demoCiclos(bueno, malo);
    }

    private void paso7CambiarSemilla() {
        Tabla.paso(7, "Cambiar la semilla: " + semilla + " → " + semillaAlternativa);
        LCG lcg2 = new LCG(semillaAlternativa, LCG.A_GUIA, LCG.C_GUIA, LCG.M_GUIA);
        long[] xs2 = lcg2.generar(n);
        double[] us2 = new double[xs2.length];
        for (int i = 0; i < xs2.length; i++) {
            us2[i] = lcg2.normalizar(xs2[i]);
        }
        Path ruta = rutaResultado("java", semillaAlternativa);
        guardarCsv(ruta, xs2, us2);
        Tabla.archivoGuardado(relativa(ruta), (long) xs2.length);
        Tabla.primerasObservaciones(xs2, us2, 10, "Java · semilla " + semillaAlternativa);
        Tabla.secuenciasDistintas(us, semilla, us2, semillaAlternativa, 10);
    }

    private void paso8Comparar() {
        Tabla.paso(8, "Comparar resultados");

        LCG lcg = new LCG(semilla, LCG.A_GUIA, LCG.C_GUIA, LCG.M_GUIA);
        long[] xs2 = new LCG(semillaAlternativa, LCG.A_GUIA, LCG.C_GUIA, LCG.M_GUIA).generar(n);
        double[] us2 = new double[xs2.length];
        for (int i = 0; i < xs2.length; i++) {
            us2[i] = lcg.normalizar(xs2[i]);
        }
        Tabla.comparacionEstadisticas(semilla, LCG.estadisticas(us),
                semillaAlternativa, LCG.estadisticas(us2), n);

        boolean coincidieron = compararConPython(semilla);
        compararConPython(semillaAlternativa);
        Tabla.conclusiones(semilla, semillaAlternativa, n, coincidieron);
    }

    // ------------------------------------------------------------------
    // Comparación con Python (pasos 4 y 8)
    // ------------------------------------------------------------------
    /** @return true si la comparación pudo realizarse y coincidieron todos. */
    private boolean compararConPython(long semillaComparar) {
        Path rutaPy = rutaResultado("python", semillaComparar);
        Path rutaJa = rutaResultado("java", semillaComparar);
        if (!Files.exists(rutaPy)) {
            Tabla.avisoFaltaPython(rutaPy);
            return false;
        }

        Serie py = leerCsv(rutaPy);
        Serie ja = leerCsv(rutaJa);
        int cantidad = Math.min(py.xs.length, ja.xs.length);

        long coincidentes = 0;
        for (int i = 0; i < cantidad; i++) {
            if (py.xs[i] == ja.xs[i] && py.us[i] == ja.us[i]) {
                coincidentes++;
            }
        }

        Tabla.comparacionPrimeras20(py.xs, py.us, ja.xs, ja.us, 20, semillaComparar);
        Tabla.resumenComparacion(coincidentes, cantidad, semillaComparar);

        Path rutaComp = carpetaResultados.resolve(
                "comparacion_python_java_semilla_" + semillaComparar + ".csv");
        try {
            guardarCsvComparacion(rutaComp, py, ja, cantidad);
            Tabla.archivoGuardado(relativa(rutaComp), (long) cantidad);
        } catch (IOException e) {
            System.out.println("   ✗ No se pudo guardar " + rutaComp.getFileName());
        }
        return coincidentes == cantidad && cantidad > 0;
    }

    // ------------------------------------------------------------------
    // Utilidades de archivos
    // ------------------------------------------------------------------
    private Path rutaResultado(String lenguaje, long semillaUsada) {
        return carpetaResultados.resolve(
                lenguaje + "_lcg_semilla_" + semillaUsada + ".csv");
    }

    private String relativa(Path ruta) {
        return raiz.relativize(ruta).toString().replace('\\', '/');
    }

    /** Una serie leída/guardada en CSV: estados X y valores U. */
    private static class Serie {
        final long[] xs;
        final double[] us;

        Serie(long[] xs, double[] us) {
            this.xs = xs;
            this.us = us;
        }
    }

    /**
     * Formatea U con 12 decimales usando aritmética exacta (BigDecimal con
     * redondeo HALF_EVEN, el mismo criterio de IEEE 754 que usa Python).
     * String.format("%.12f") no siempre redondea correctamente los casos
     * límite, y para esta práctica los CSV de Java y Python deben ser
     * idénticos byte a byte.
     */
    private static String formatearU(double u) {
        return new BigDecimal(u).setScale(12, RoundingMode.HALF_EVEN).toPlainString();
    }

    private void guardarCsv(Path ruta, long[] xs, double[] us) {
        try (PrintWriter w = new PrintWriter(
                Files.newBufferedWriter(ruta, StandardCharsets.UTF_8))) {
            w.println("n,X(n),U(n)");
            for (int i = 0; i < xs.length; i++) {
                w.printf(Locale.ROOT, "%d,%d,%s%n", i + 1, xs[i], formatearU(us[i]));
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo escribir " + ruta, e);
        }
    }

    private void guardarCsvComparacion(Path ruta, Serie py, Serie ja, int cantidad)
            throws IOException {
        try (PrintWriter w = new PrintWriter(
                Files.newBufferedWriter(ruta, StandardCharsets.UTF_8))) {
            w.println("n,X(n) Python,U(n) Python,X(n) Java,U(n) Java,coincide");
            for (int i = 0; i < cantidad; i++) {
                boolean ok = py.xs[i] == ja.xs[i] && py.us[i] == ja.us[i];
                w.printf(Locale.ROOT, "%d,%d,%s,%d,%s,%s%n",
                        i + 1, py.xs[i], formatearU(py.us[i]), ja.xs[i],
                        formatearU(ja.us[i]), ok ? "SI" : "NO");
            }
        }
    }

    private Serie leerCsv(Path ruta) {
        List<Long> listaX = new ArrayList<Long>();
        List<Double> listaU = new ArrayList<Double>();
        try (BufferedReader br = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            String linea = br.readLine();               // encabezado
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] campos = linea.split(",");
                listaX.add(Long.parseLong(campos[1].trim()));
                listaU.add(Double.parseDouble(campos[2].trim()));
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + ruta, e);
        }
        long[] xs = new long[listaX.size()];
        double[] us = new double[listaU.size()];
        for (int i = 0; i < xs.length; i++) {
            xs[i] = listaX.get(i);
            us[i] = listaU.get(i);
        }
        return new Serie(xs, us);
    }
}
