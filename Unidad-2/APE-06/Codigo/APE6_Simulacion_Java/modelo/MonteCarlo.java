package modelo;

/**
 * Modelo · Estimación de π por Monte Carlo usando el LCG (Práctica Nro. 06).
 *
 * Método: se generan pares (u1, u2) uniformes en [0, 1)². Un punto cae
 * dentro del cuarto de círculo de radio 1 si u1² + u2² ≤ 1. Entonces:
 *
 *     π ≈ 4 · (puntos dentro) / (puntos totales)
 *
 * Error absoluto = |π estimado − π real|.
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
     * Usa los números en pares: (u[0], u[1]), (u[2], u[3]), ...
     * Si la secuencia tiene una cantidad impar, se descarta el último valor.
     */
    public static Resultado estimarPi(double[] secuencia) {
        int nPares = secuencia.length / 2;
        if (nPares == 0) return new Resultado(0.0, 0.0);
        int dentro = 0;
        for (int i = 0; i < nPares; i++) {
            double u1 = secuencia[2 * i];
            double u2 = secuencia[2 * i + 1];
            if (u1 * u1 + u2 * u2 <= 1.0) dentro++;
        }
        double pi = 4.0 * dentro / (double) nPares;
        return new Resultado(pi, Math.abs(pi - PI_REAL));
    }
}
