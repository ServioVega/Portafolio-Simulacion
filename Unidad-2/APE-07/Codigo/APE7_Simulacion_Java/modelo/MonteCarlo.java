package modelo;

/**
 * Modelo · Estimación de π por el método de Monte Carlo (Práctica Nro. 07).
 *
 * Método (guía): se generan puntos al azar dentro de un cuadrado y se
 * determina cuáles pertenecen al círculo inscrito de radio 1 centrado en
 * el origen. Como el cociente de áreas es
 *
 *     área círculo / área cuadrado  =  π·r² / (2r)²  =  π / 4,
 *
 * el estimador del método es:
 *
 *     π ≈ 4 · (Nc / N)
 *
 * donde Nc es el número de puntos dentro del círculo y N el número total
 * de puntos generados.
 *
 * Los números pseudoaleatorios U ∈ [0, 1) del LCG se transforman a
 * coordenadas del cuadrado [-1, 1) con  x = 2·u1 − 1  y  y = 2·u2 − 1;
 * un punto pertenece al círculo si  x² + y² ≤ 1.  (Esta variante es
 * equivalente al cuarto de círculo del cuadrado unitario usado en el
 * APE-06: el cuadrado [-1,1]² con el círculo inscrito contiene 4 cuadrantes
 * iguales.)
 */
public final class MonteCarlo {

    public static final double PI_REAL = Math.PI;

    private MonteCarlo() { }

    public static class Resultado {
        public final double piEstimado;
        public final double error;
        Resultado(double pi, double error) { this.piEstimado = pi; this.error = error; }
    }

    /**
     * Estima π a partir de una secuencia de números pseudoaleatorios [0, 1).
     * Cada punto del experimento consume un par (u1, u2), de modo que N
     * puntos requieren 2·N números. Si la secuencia tiene una cantidad
     * impar de valores, se descarta el último.
     */
    public static Resultado estimarPi(double[] secuencia) {
        int nPuntos = secuencia.length / 2;
        if (nPuntos == 0) return new Resultado(0.0, 0.0);
        int dentro = 0;
        for (int i = 0; i < nPuntos; i++) {
            double x = 2.0 * secuencia[2 * i] - 1.0;
            double y = 2.0 * secuencia[2 * i + 1] - 1.0;
            if (x * x + y * y <= 1.0) dentro++;
        }
        double pi = 4.0 * dentro / (double) nPuntos;
        return new Resultado(pi, Math.abs(pi - PI_REAL));
    }

    /**
     * Desviación estándar teórica del estimador π̂ = 4·Nc / N.
     * Si Nc ~ Binomial(N, π/4), entonces Var(π̂) = 16·p·(1−p)/N = π(4−π)/N,
     * de modo que el error esperado del método decrece como σ ≈ 1.6421/√N.
     */
    public static double errorTeorico(int n) {
        if (n <= 0) return 0.0;
        double p = Math.PI / 4.0;
        return Math.sqrt(16.0 * p * (1.0 - p) / n);
    }
}
