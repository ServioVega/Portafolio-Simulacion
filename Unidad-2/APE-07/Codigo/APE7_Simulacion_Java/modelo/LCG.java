package modelo;

/**
 * Modelo · Generador Congruencial Lineal (LCG) de la Práctica Nro. 07.
 *
 * Relación recursiva:  X(n+1) = (a·X(n) + c) mod m
 * Valor normalizado:   U(n)   = X(n) / m  ∈ [0, 1)
 *
 * Implementación idéntica a la versión Python para que ambas produzcan
 * exactamente la misma secuencia con los mismos parámetros. El método de
 * Monte Carlo consume los valores por pares (u1, u2) como coordenadas de
 * los puntos del experimento.
 */
public class LCG {

    // Parámetros de la práctica (los mismos usados en el APE-05 y APE-06).
    public static final long A_GUIA = 1664525L;
    public static final long C_GUIA = 1013904223L;
    public static final long M_GUIA = 4294967296L;   // 2^32
    public static final long SEMILLA_GUIA = 12345L;

    private final long a;
    private final long c;
    private final long m;
    private final long semillaInicial;
    private long x;

    public LCG(long semilla, long a, long c, long m) {
        validar(semilla, a, c, m);
        this.semillaInicial = semilla;
        this.a = a;
        this.c = c;
        this.m = m;
        this.x = semilla;
    }

    public LCG() {
        this(SEMILLA_GUIA, A_GUIA, C_GUIA, M_GUIA);
    }

    private static void validar(long semilla, long a, long c, long m) {
        if (m <= 0) throw new IllegalArgumentException("m debe ser mayor que 0");
        if (a < 0 || a >= m) throw new IllegalArgumentException("a debe cumplir 0 <= a < m");
        if (c < 0 || c >= m) throw new IllegalArgumentException("c debe cumplir 0 <= c < m");
        if (semilla < 0 || semilla >= m) throw new IllegalArgumentException("la semilla debe cumplir 0 <= X(0) < m");
    }

    /** Multiplicar, sumar y calcular el residuo (recursión del LCG). */
    public long siguienteX() {
        // (a·X + c) no desborda long: a·X < 1664525·2^32 ≈ 7.15·10^15 < 9.22·10^18
        x = (a * x + c) % m;
        return x;
    }

    /** Dividir entre m para obtener un número en [0, 1). */
    public double siguienteU() {
        return siguienteX() / (double) m;
    }

    /** Genera n números pseudoaleatorios normalizados en [0, 1). */
    public double[] generarSecuencia(int n) {
        double[] s = new double[n];
        for (int i = 0; i < n; i++) s[i] = siguienteU();
        return s;
    }

    public long getSemillaInicial() { return semillaInicial; }
    public long getA() { return a; }
    public long getC() { return c; }
    public long getM() { return m; }
}
