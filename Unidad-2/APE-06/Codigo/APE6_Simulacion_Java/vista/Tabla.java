package vista;

import modelo.Pruebas;

import java.util.Locale;

/**
 * Vista · Tablas y mensajes en consola para la Práctica Nro. 06.
 */
public final class Tabla {

    private Tabla() { }

    public static void imprimirTitulo(String texto) {
        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println(texto);
        System.out.println("=".repeat(70));
    }

    public static void imprimirSubtitulo(String texto) {
        System.out.println();
        System.out.println("--- " + texto + " ---");
    }

    public static void imprimirMuestra(double[] secuencia, int cantidad) {
        imprimirSubtitulo("Primeros " + cantidad + " números pseudoaleatorios U(0,1)");
        for (int i = 0; i < cantidad && i < secuencia.length; i++) {
            System.out.printf(Locale.ROOT, "  U(%3d) = %.10f%n", i + 1, secuencia[i]);
        }
        System.out.printf(Locale.ROOT, "  ... (total generados: %,d)%n", secuencia.length);
    }

    /** Paso 14 del algoritmo 2: frecuencias, χ², gl y valor p. */
    public static void imprimirChi(Pruebas.ChiCuadrado res) {
        imprimirSubtitulo(String.format("Prueba Chi-cuadrado (k = %d, gl = %d)", res.k, res.gl));
        System.out.printf(Locale.ROOT, "  %-16s%12s%12s%n",
                "Intervalo", "Observada", "Esperada");
        for (int i = 0; i < res.k; i++) {
            System.out.printf(Locale.ROOT, "  [%.1f, %.1f)   %12d%12.1f%n",
                    i / (double) res.k, (i + 1) / (double) res.k,
                    res.observadas[i], res.esperada);
        }
        System.out.printf(Locale.ROOT, "%n  Estadístico χ²  = %.6f%n", res.estadistico);
        System.out.printf(Locale.ROOT, "  Grados libertad = %d%n", res.gl);
        System.out.printf(Locale.ROOT, "  Valor p         = %.6f%n", res.valorP);
        System.out.printf(Locale.ROOT, "  Decisión (α = %.2f) → %s%n", Pruebas.ALFA, res.decision);
    }

    /** Paso 13 del algoritmo 3: estadístico KS, valor p y decisión. */
    public static void imprimirKs(Pruebas.KolmogorovSmirnov res) {
        imprimirSubtitulo("Prueba Kolmogorov-Smirnov (n = " + res.n + ")");
        System.out.printf(Locale.ROOT, "  D+ = %.6f%n", res.dMas);
        System.out.printf(Locale.ROOT, "  D− = %.6f%n", res.dMenos);
        System.out.printf(Locale.ROOT, "  D  = max(D+, D−) = %.6f%n", res.estadistico);
        System.out.printf(Locale.ROOT, "  Valor p = %.6f%n", res.valorP);
        System.out.printf(Locale.ROOT, "  Decisión (α = %.2f) → %s%n", Pruebas.ALFA, res.decision);
    }

    /** Tabla de Resultados Esperados: π estimado, error y tiempos por lenguaje. */
    public static void imprimirTablaPi(java.util.List<ControladorTabla> filas) {
        imprimirSubtitulo("Tabla de Resultados Esperados: Monte Carlo π (Python vs Java)");
        System.out.printf(Locale.ROOT,
                "  %10s | %12s | %10s | %13s | %12s | %10s | %11s%n",
                "N", "π (Python)", "Error Py", "t Python (s)",
                "π (Java)", "Error Java", "t Java (s)");
        System.out.println("  " + "-".repeat(96));
        for (ControladorTabla f : filas) f.imprimir();
    }

    /** Fila de la tabla π (cada lenguaje aporta sus propios valores). */
    public static class ControladorTabla {
        public final int n;
        public final Double piPy, errPy, tPy;
        public final Double piJava, errJava, tJava;

        public ControladorTabla(int n, Double piPy, Double errPy, Double tPy,
                                Double piJava, Double errJava, Double tJava) {
            this.n = n; this.piPy = piPy; this.errPy = errPy; this.tPy = tPy;
            this.piJava = piJava; this.errJava = errJava; this.tJava = tJava;
        }

        void imprimir() {
            System.out.printf(Locale.ROOT, "  %,10d | %12.6f | %10.6f | %13.4f |",
                    n, piPy, errPy, tPy);
            if (piJava == null) {
                System.out.printf(Locale.ROOT, "%12s | %10s | %11s%n",
                        "—", "—", "—");
            } else {
                System.out.printf(Locale.ROOT, "%12.6f | %10.6f | %11.4f%n",
                        piJava, errJava, tJava);
            }
        }
    }

    /** Comparación de los primeros valores Python vs Java. */
    public static void imprimirComparacion(double[] py, double[] java, boolean identicas) {
        imprimirSubtitulo("Comparación Python vs Java (misma semilla y parámetros)");
        System.out.printf(Locale.ROOT, "  %4s | %18s | %18s%n", "i", "Python U(i)", "Java U(i)");
        int n = Math.min(Math.min(py.length, java.length), 10);
        for (int i = 0; i < n; i++) {
            String marca = Math.abs(py[i] - java[i]) <= 1e-12 ? "" : "  ← difieren";
            System.out.printf(Locale.ROOT, "  %4d | %18.10f | %18.10f%s%n",
                    i + 1, py[i], java[i], marca);
        }
        System.out.println();
        System.out.println("  Secuencias idénticas: " + (identicas ? "SÍ ✔" : "NO ✘"));
    }
}
