package modelo;

/**
 * Modelo · Pruebas estadísticas de bondad de ajuste (Práctica Nro. 06).
 *
 *  · Chi-cuadrado: uniformidad de las frecuencias en k = 10 subintervalos.
 *  · Kolmogorov-Smirnov: comparación de la F empírica con la F teórica U(0,1).
 *
 * Hipótesis:  H0: X ~ U(0,1)    H1: X ≁ U(0,1)    α = 0.05
 *
 * Los valores p se calculan con las mismas fórmulas que en Python
 * (log-gamma de Lanczos, serie de potencias, fracción continua de Lentz
 * y distribución de Kolmogorov) para resultados reproducibles.
 */
public final class Pruebas {

    public static final double ALFA = 0.05;
    public static final int K_INTERVALOS = 10;      // la guía fija k = 10 → gl = 9

    private Pruebas() { }

    // ------------------------------------------------------------------
    // Resultados como estructuras simples
    // ------------------------------------------------------------------
    public static class ChiCuadrado {
        public final int k;
        public final int[] observadas;
        public final double esperada;
        public final double estadistico;
        public final int gl;
        public final double valorP;
        public final String decision;

        ChiCuadrado(int k, int[] observadas, double esperada, double estadistico,
                    int gl, double valorP, String decision) {
            this.k = k; this.observadas = observadas; this.esperada = esperada;
            this.estadistico = estadistico; this.gl = gl; this.valorP = valorP;
            this.decision = decision;
        }
    }

    public static class KolmogorovSmirnov {
        public final double dMas;
        public final double dMenos;
        public final double estadistico;
        public final int n;
        public final double valorP;
        public final String decision;

        KolmogorovSmirnov(double dMas, double dMenos, double estadistico,
                          int n, double valorP, String decision) {
            this.dMas = dMas; this.dMenos = dMenos; this.estadistico = estadistico;
            this.n = n; this.valorP = valorP; this.decision = decision;
        }
    }

    // ------------------------------------------------------------------
    // Funciones especiales compartidas (idénticas en Python)
    // ------------------------------------------------------------------
    /** Logaritmo natural de Γ(x) (aproximación de Lanczos). */
    public static double logGamma(double x) {
        int g = 7;
        double[] coef = {
            0.99999999999980993, 676.5203681218851, -1259.1392167224028,
            771.32342877765313, -176.61502916214059, 12.507343278686905,
            -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7
        };
        if (x < 0.5) {
            // Reflexión: Γ(x)·Γ(1−x) = π / sin(πx)
            return Math.log(Math.PI / Math.sin(Math.PI * x)) - logGamma(1.0 - x);
        }
        x -= 1.0;
        double a = coef[0];
        double t = x + g + 0.5;
        for (int i = 1; i < g + 2; i++) a += coef[i] / (x + i);
        return 0.5 * Math.log(2.0 * Math.PI) + (x + 0.5) * Math.log(t) - t + Math.log(a);
    }

    /** P(a, x) por la serie de potencias (para x < a + 1). */
    private static double gammaInferiorSerie(double a, double x) {
        double ap = a;
        double suma = 1.0 / a;
        double delta = suma;
        for (int i = 0; i < 1000; i++) {
            ap += 1.0;
            delta *= x / ap;
            suma += delta;
            if (Math.abs(delta) < Math.abs(suma) * 1e-15) break;
        }
        return suma * Math.exp(-x + a * Math.log(x) - logGamma(a));
    }

    /** P(a, x) por la fracción continua de Lentz (para x >= a + 1). */
    private static double gammaInferiorFraccion(double a, double x) {
        double tiny = 1e-300;
        double b = x + 1.0 - a;
        double c = 1.0 / tiny;
        double d = 1.0 / b;
        double h = d;
        for (int i = 1; i < 1000; i++) {
            double an = -i * (i - a);
            b += 2.0;
            d = an * d + b;
            if (Math.abs(d) < tiny) d = tiny;
            c = b + an / c;
            if (Math.abs(c) < tiny) c = tiny;
            d = 1.0 / d;
            double delta = d * c;
            h *= delta;
            if (Math.abs(delta - 1.0) < 1e-15) break;
        }
        return Math.exp(-x + a * Math.log(x) - logGamma(a)) * h;
    }

    /** Función gamma incompleta inferior regularizada P(a, x). */
    public static double pGamma(double a, double x) {
        if (x == 0.0) return 0.0;
        if (x < a + 1.0) return gammaInferiorSerie(a, x);
        return 1.0 - gammaInferiorFraccion(a, x);
    }

    /** Valor p de la distribución Chi-cuadrado: P(χ² > x) con 'gl' gdl. */
    public static double pChi2(double x, int gl) {
        return 1.0 - pGamma(gl / 2.0, x / 2.0);
    }

    /** Valor p de la distribución de Kolmogorov (serie asintótica). */
    public static double pKs(double d, int n) {
        if (d <= 0) return 1.0;
        double t = Math.sqrt(n) * d;
        if (t < 0.2) return 1.0;
        double suma = 0.0;
        for (int j = 1; j <= 100; j++) {
            double termino = 2.0 * ((j % 2 == 1) ? 1.0 : -1.0)
                    * Math.exp(-2.0 * ((double) j * t) * ((double) j * t));
            suma += termino;
            if (Math.abs(termino) < 1e-15) break;
        }
        return Math.min(1.0, Math.max(0.0, suma));
    }

    // ------------------------------------------------------------------
    // Algoritmo 2 · Prueba Chi-cuadrado
    // ------------------------------------------------------------------
    public static ChiCuadrado chiCuadrado(double[] secuencia, int k) {
        int n = secuencia.length;
        double amplitud = 1.0 / k;
        int[] observadas = new int[k];
        for (double u : secuencia) {
            int i = (int) Math.min(u / amplitud, k - 1);   // intervalo al que pertenece
            observadas[i]++;
        }
        double esperada = (double) n / k;                  // paso 4 de la guía
        double chi2 = 0.0;
        for (int o : observadas) {
            chi2 += (o - esperada) * (o - esperada) / esperada;   // paso 8 de la guía
        }
        int gl = k - 1;                                    // paso 9 de la guía: gl = 9
        double valorP = pChi2(chi2, gl);                   // paso 10 de la guía
        String decision = valorP >= ALFA
                ? "No se rechaza H0: los datos son compatibles con U(0,1)"
                : "Se rechaza H0: los datos no siguen una U(0,1)";
        return new ChiCuadrado(k, observadas, esperada, chi2, gl, valorP, decision);
    }

    // ------------------------------------------------------------------
    // Algoritmo 3 · Prueba Kolmogorov-Smirnov
    // ------------------------------------------------------------------
    public static KolmogorovSmirnov kolmogorovSmirnov(double[] secuencia) {
        double[] ordenados = secuencia.clone();
        java.util.Arrays.sort(ordenados);                  // paso 2 de la guía
        int n = ordenados.length;

        double dMas = 0.0, dMenos = 0.0;
        for (int i = 1; i <= n; i++) {
            double fEmpSup = (double) i / n;               // i / n
            double fEmpInf = (double) (i - 1) / n;         // (i − 1) / n
            double fTeorica = ordenados[i - 1];            // F(x) = x para U(0,1)
            dMas = Math.max(dMas, fEmpSup - fTeorica);
            dMenos = Math.max(dMenos, fTeorica - fEmpInf);
        }
        double d = Math.max(dMas, dMenos);                 // paso 8 de la guía
        double valorP = pKs(d, n);                         // paso 9 de la guía
        String decision = valorP >= ALFA
                ? "No se rechaza H0: la F empírica es compatible con U(0,1)"
                : "Se rechaza H0: la F empírica no corresponde a una U(0,1)";
        return new KolmogorovSmirnov(dMas, dMenos, d, n, valorP, decision);
    }
}
