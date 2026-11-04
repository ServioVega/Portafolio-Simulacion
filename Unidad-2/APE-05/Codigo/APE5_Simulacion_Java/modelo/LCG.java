package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modelo · Lógica matemática del Generador Congruencial Lineal (LCG).
 *
 * Relación recursiva:   X(n+1) = (a · X(n) + c) mod m
 * Valor normalizado:    U(n)   = X(n) / m   ∈ [0, 1)
 *
 * Con a = 1664525, c = 1013904223 y m = 2^32, el producto a·X + c alcanza
 * como máximo ~7,2 · 10^15, muy por debajo de Long.MAX_VALUE (9,2 · 10^18):
 * la aritmética de 64 bits (long) es EXACTA y coincide bit a bit con la de
 * Python (que usa enteros de precisión arbitraria). Por eso ambos lenguajes
 * generan exactamente la misma secuencia.
 */
public class LCG {

    /** Parámetros indicados en la guía de la práctica Nro. 05. */
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
        if (m <= 0) {
            throw new IllegalArgumentException("m debe ser mayor que 0");
        }
        if (a < 0 || a >= m) {
            throw new IllegalArgumentException("a debe cumplir 0 <= a < m");
        }
        if (c < 0 || c >= m) {
            throw new IllegalArgumentException("c debe cumplir 0 <= c < m");
        }
        if (semilla < 0 || semilla >= m) {
            throw new IllegalArgumentException("la semilla debe cumplir 0 <= X(0) < m");
        }
        if (m > 1 && a > (Long.MAX_VALUE - c) / (m - 1)) {
            throw new IllegalArgumentException(
                    "parámetros demasiado grandes: a·X + c desbordaría un long de 64 bits");
        }
        this.a = a;
        this.c = c;
        this.m = m;
        this.semillaInicial = semilla;
        this.x = semilla;
    }

    /** Aplica la recursión X(n+1) = (a·X(n) + c) mod m y devuelve X(n+1). */
    public long siguiente() {
        x = (a * x + c) % m;
        return x;
    }

    /** Normaliza un estado: U = X/m ∈ [0, 1). */
    public double normalizar(long estado) {
        return (double) estado / (double) m;
    }

    /**
     * Genera n estados X(1..n) desde la semilla inicial.
     * La semilla X(0) NO se cuenta como valor generado (igual que en Python).
     */
    public long[] generar(int n) {
        long[] xs = new long[n];
        long estado = semillaInicial;
        for (int i = 0; i < n; i++) {
            estado = (a * estado + c) % m;
            xs[i] = estado;
        }
        return xs;
    }

    public long getA() { return a; }
    public long getC() { return c; }
    public long getM() { return m; }
    public long getSemillaInicial() { return semillaInicial; }

    // ------------------------------------------------------------------
    // Análisis del período: teorema de Hull-Dobell (1962)
    // ------------------------------------------------------------------

    /** Factores primos de m (división por tanteo). */
    public List<Long> factoresPrimos() {
        List<Long> factores = new ArrayList<Long>();
        long resto = m;
        long d = 2;
        while (d * d <= resto) {
            while (resto % d == 0) {
                if (factores.isEmpty() || factores.get(factores.size() - 1) != d) {
                    factores.add(d);
                }
                resto /= d;
            }
            d += (d == 2) ? 1 : 2;
        }
        if (resto > 1 && (factores.isEmpty() || factores.get(factores.size() - 1) != resto)) {
            factores.add(resto);
        }
        return factores;
    }

    /** Máximo común divisor (algoritmo de Euclides). */
    public static long mcd(long u, long v) {
        while (v != 0) {
            long t = u % v;
            u = v;
            v = t;
        }
        return u;
    }

    /** Resultado de la verificación del teorema de Hull-Dobell. */
    public static class Verificacion {
        public final List<String> nombres = new ArrayList<String>();
        public final List<String> calculos = new ArrayList<String>();
        public final List<Boolean> cumple = new ArrayList<Boolean>();

        public boolean cumpleTodas() {
            for (boolean b : cumple) {
                if (!b) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Verifica las tres condiciones del teorema de Hull-Dobell (1962):
     * el LCG tiene período máximo τ = m SI Y SOLO SI:
     *   1) gcd(c, m) = 1 (c y m son primos relativos);
     *   2) todo factor primo p de m divide a (a − 1);
     *   3) si 4 divide a m, entonces 4 divide a (a − 1).
     */
    public Verificacion verificarHullDobell() {
        Verificacion v = new Verificacion();

        long g = mcd(c, m);
        v.nombres.add("1) gcd(c, m) = 1  (c y m primos relativos)");
        v.calculos.add("gcd(" + c + ", " + m + ") = " + g);
        v.cumple.add(g == 1L);

        for (long p : factoresPrimos()) {
            v.nombres.add("2) todo primo p de m divide a (a − 1)  [p = " + p + "]");
            v.calculos.add("(" + a + " − 1) mod " + p + " = " + ((a - 1) % p));
            v.cumple.add((a - 1) % p == 0);
        }

        if (m % 4 == 0) {
            v.nombres.add("3) 4 divide a m  ⇒  4 divide a (a − 1)");
            v.calculos.add("(" + a + " − 1) mod 4 = " + ((a - 1) % 4));
            v.cumple.add((a - 1) % 4 == 0);
        }
        return v;
    }

    /**
     * Cuenta empíricamente el período: número de pasos hasta que el estado
     * vuelve a la semilla (fuerza bruta). Para m = 2^32 son 4 294 967 296
     * iteraciones, que Java ejecuta en segundos.
     *
     * Cuando m es potencia de dos, el módulo se calcula con una máscara de
     * bits (estado &amp; (m−1)): el procesador lo evalúa en un solo ciclo,
     * por lo que el conteo es mucho más rápido que con el operador %.
     *
     * @return el período τ, o −1 si la semilla está fuera del ciclo.
     */
    public long contarPeriodoEmpirico() {
        long inicio = semillaInicial;
        long estado = inicio;
        long contador = 0;
        if ((m & (m - 1)) == 0) {          // m es potencia de 2
            long mascara = m - 1;
            do {
                estado = (a * estado + c) & mascara;
                contador++;
                if (contador > m) {
                    return -1;             // la semilla nunca vuelve: hay cola
                }
            } while (estado != inicio);
        } else {
            do {
                estado = (a * estado + c) % m;
                contador++;
                if (contador > m) {
                    return -1;
                }
            } while (estado != inicio);
        }
        return contador;
    }

    /** Estructura de ciclos de un LCG pequeño: cola (mu) y período (lambda). */
    public static class Ciclo {
        public final long mu;
        public final long lambda;

        public Ciclo(long mu, long lambda) {
            this.mu = mu;
            this.lambda = lambda;
        }
    }

    /**
     * Detección empírica de ciclos para módulos pequeños.
     * Recorre la secuencia anotando el paso en que aparece cada estado; al
     * reencontrar uno ya visto obtiene la cola μ y el período λ.
     */
    public static Ciclo detectarCiclo(long a, long c, long m, long semilla) {
        if (m > 1_000_000L) {
            throw new IllegalArgumentException("detección de ciclo solo para m pequeños");
        }
        Map<Long, Long> pasoDelEstado = new HashMap<Long, Long>();
        long estado = semilla;
        long n = 0;
        while (!pasoDelEstado.containsKey(estado)) {
            pasoDelEstado.put(estado, n);
            estado = (a * estado + c) % m;
            n++;
        }
        long mu = pasoDelEstado.get(estado);
        return new Ciclo(mu, n - mu);
    }

    // ------------------------------------------------------------------
    // Estadísticas descriptivas
    // ------------------------------------------------------------------

    /**
     * Estadísticas de los valores U ∈ [0, 1).
     * @return {media, desviación (poblacional), mínimo, máximo}
     */
    public static double[] estadisticas(double[] us) {
        double suma = 0.0;
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (double u : us) {
            suma += u;
            if (u < min) min = u;
            if (u > max) max = u;
        }
        double media = suma / us.length;
        double suma2 = 0.0;
        for (double u : us) {
            suma2 += (u - media) * (u - media);
        }
        double desviacion = Math.sqrt(suma2 / us.length);
        return new double[]{media, desviacion, min, max};
    }
}
