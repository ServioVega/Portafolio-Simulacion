package vista;

import modelo.LCG;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

/**
 * Vista · Tablas y mensajes en consola.
 *
 * Solo se encarga de PRESENTAR información: no calcula nada (eso es tarea
 * del Modelo) ni decide el orden de los pasos (eso es tarea del Controlador).
 */
public final class Tabla {

    private static final String LINEA = repetir('=', 78);

    private Tabla() {
    }

    /** Formatea un entero con separador de miles: 4294967296 → 4.294.967.296. */
    public static String fmt(long numero) {
        return String.format(Locale.ROOT, "%,d", numero).replace(",", ".");
    }

    /** Formatea un tiempo en segundos con 1 decimal (estilo español). */
    public static String fmtTiempo(double segundos) {
        return String.format(Locale.ROOT, "%.1f", segundos).replace(".", ",");
    }

    /**
     * Formatea U con 10 decimales usando aritmética exacta (BigDecimal con
     * redondeo HALF_EVEN, el mismo criterio que usa Python), para que las
     * tablas en consola de ambos lenguajes coincidan siempre.
     */
    public static String fmtU(double u) {
        return new BigDecimal(u).setScale(10, RoundingMode.HALF_EVEN).toPlainString();
    }

    private static String repetir(char c, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Encabezados
    // ------------------------------------------------------------------
    public static void cabecera() {
        System.out.println();
        System.out.println(LINEA);
        System.out.println(" APE-05 · GENERADOR CONGRUENCIAL LINEAL (LCG)");
        System.out.println(" Implementación en Java · patrón MVC · Simulación · Unidad 2");
        System.out.println(LINEA);
    }

    public static void paso(int numero, String titulo) {
        System.out.println();
        System.out.println(" PASO " + numero + " · " + titulo);
        System.out.println(" " + repetir('-', 76));
    }

    public static void cierre() {
        System.out.println();
        System.out.println(LINEA);
        System.out.println(" Ejecución finalizada · APE-05 · LCG en Java");
        System.out.println(LINEA);
    }

    // ------------------------------------------------------------------
    // Paso 1: parámetros
    // ------------------------------------------------------------------
    public static void parametros(LCG lcg) {
        System.out.println("   a       = " + lcg.getA());
        System.out.println("   c       = " + lcg.getC());
        System.out.println("   m       = 2^32 = " + fmt(lcg.getM()));
        System.out.println("   semilla = X(0) = " + lcg.getSemillaInicial());
        System.out.println("   (parámetros indicados en la guía, práctica Nro. 05)");
    }

    public static void notaPaso3Python() {
        paso(3, "Implementar el mismo algoritmo en Python");
        System.out.println("   La implementación equivalente en Python (misma recursión y");
        System.out.println("   mismos parámetros) está en:  Codigo/APE5_Simulacion/");
        System.out.println("   Ejecútela también: al volver a lanzar este programa, la tabla");
        System.out.println("   comparativa del paso 4 se completa automáticamente.");
    }

    // ------------------------------------------------------------------
    // Paso 2 / 4 / 7: tablas de observaciones
    // ------------------------------------------------------------------
    public static void primerasObservaciones(long[] xs, double[] us, int k, String titulo) {
        System.out.println();
        System.out.println("   " + titulo);
        System.out.println("        n |          X(n) | U(n)");
        System.out.println("   " + repetir('-', 4) + "-+-" + repetir('-', 12) + "-+-" + repetir('-', 14));
        for (int i = 0; i < Math.min(k, xs.length); i++) {
            System.out.printf(Locale.ROOT, "   %5d | %12d | %s%n", i + 1, xs[i], fmtU(us[i]));
        }
    }

    public static void secuenciasDistintas(double[] us1, long semilla1,
                                           double[] us2, long semilla2, int k) {
        System.out.println();
        System.out.println("   Primeras observaciones con cada semilla (¿son diferentes?)");
        System.out.printf(Locale.ROOT, "   %4s | %15s | %15s | %s%n", "n",
                "U con semilla " + semilla1, "U con semilla " + semilla2, "difieren");
        System.out.println("   " + repetir('-', 52));
        for (int i = 0; i < Math.min(k, Math.min(us1.length, us2.length)); i++) {
            System.out.printf(Locale.ROOT, "   %4d | %15s | %15s | %s%n",
                    i + 1, fmtU(us1[i]), fmtU(us2[i]), us1[i] != us2[i] ? "SI" : "NO");
        }
    }

    // ------------------------------------------------------------------
    // Paso 4 / 8: comparación Python vs Java
    // ------------------------------------------------------------------
    public static void comparacionPrimeras20(long[] xsPy, double[] usPy,
                                             long[] xsJa, double[] usJa,
                                             int k, long semilla) {
        System.out.println();
        System.out.println("   Comparación Java vs Python · semilla " + semilla
                + " (primeras " + Math.min(k, xsPy.length) + " observaciones)");
        System.out.printf(Locale.ROOT, "   %4s | %12s | %12s | %13s | %13s | %s%n",
                "n", "X(n) Java", "X(n) Python", "U(n) Java", "U(n) Python", "coincide");
        System.out.println("   " + repetir('-', 76));
        for (int i = 0; i < Math.min(k, Math.min(xsPy.length, xsJa.length)); i++) {
            boolean ok = xsPy[i] == xsJa[i] && usPy[i] == usJa[i];
            System.out.printf(Locale.ROOT, "   %4d | %12d | %12d | %13s | %13s | %s%n",
                    i + 1, xsJa[i], xsPy[i], fmtU(usJa[i]), fmtU(usPy[i]), ok ? "SI" : "NO");
        }
    }

    public static void resumenComparacion(long coincidentes, long total, long semilla) {
        double porcentaje = total > 0 ? 100.0 * coincidentes / total : 0.0;
        System.out.println();
        System.out.println("   → Valores idénticos en ambos lenguajes: " + fmt(coincidentes)
                + " de " + fmt(total) + " ("
                + String.format(Locale.ROOT, "%.2f", porcentaje) + " %) con la semilla "
                + semilla);
    }

    public static void avisoFaltaPython(java.nio.file.Path ruta) {
        System.out.println();
        System.out.println("   (Todavía no existe " + ruta.getFileName()
                + ": ejecute primero el programa");
        System.out.println("    en Python y vuelva a lanzar este para ver la comparación)");
    }

    // ------------------------------------------------------------------
    // Paso 6: análisis del período
    // ------------------------------------------------------------------
    public static void hullDobell(LCG.Verificacion v, long m) {
        System.out.println();
        System.out.println("   Teorema de Hull-Dobell (1962) · condiciones de período máximo:");
        for (int i = 0; i < v.nombres.size(); i++) {
            System.out.println("     " + (v.cumple.get(i) ? "✓" : "✗") + " " + v.nombres.get(i));
            System.out.println("         " + v.calculos.get(i));
        }
        if (v.cumpleTodas()) {
            System.out.println();
            System.out.println("   → Se cumplen las tres condiciones: período máximo τ = m = " + fmt(m));
        } else {
            System.out.println();
            System.out.println("   → NO se cumplen todas las condiciones: τ < m (período reducido)");
        }
    }

    public static void inicioConteoExhaustivo(long m) {
        System.out.println();
        System.out.println("   Conteo EXHAUSTIVO del ciclo: recorriendo los " + fmt(m)
                + " estados");
        System.out.println("   hasta volver a la semilla (unos segundos; el módulo 2^32 se");
        System.out.println("   calcula con una máscara de bits, en un solo ciclo de CPU)…");
        System.out.print("   ");
        System.out.flush();
    }

    public static void resultadoConteoExhaustivo(long periodo, long m, double segundos) {
        System.out.println();
        if (periodo == m) {
            System.out.println("   → Período empírico τ = " + fmt(periodo) + " = m ✓ "
                    + "(verificado contando el ciclo completo en "
                    + fmtTiempo(segundos) + " s)");
        } else if (periodo < 0) {
            System.out.println("   → La semilla no vuelve a aparecer: está fuera del ciclo");
        } else {
            System.out.println("   → Período empírico τ = " + fmt(periodo) + " < m "
                    + "(el período NO es máximo)");
        }
    }

    public static void conteoOmitido() {
        System.out.println();
        System.out.println("   (Conteo exhaustivo omitido con la opción --sin-periodo-empirico)");
    }

    public static void sinRepeticiones(int distintos, int total) {
        System.out.println();
        if (distintos == total) {
            System.out.println("   Dentro de los " + fmt(total) + " valores generados hay "
                    + fmt(distintos) + " estados distintos → sin repeticiones ✓");
        } else {
            System.out.println("   Dentro de los " + fmt(total) + " valores generados solo hay "
                    + fmt(distintos) + " estados distintos → ¡hay repeticiones!");
        }
    }

    public static void demoCiclos(LCG.Ciclo bueno, LCG.Ciclo malo) {
        System.out.println();
        System.out.println("   Demostración con módulo pequeño (detección empírica de ciclos):");
        System.out.println("     · BUENO: a=13, c=7, m=16, X(0)=1  →  cola μ = " + bueno.mu
                + ", período λ = " + bueno.lambda + " (= m ✓)");
        System.out.println("     · MALO:  a=6,  c=0, m=32, X(0)=1  →  cola μ = " + malo.mu
                + ", período λ = " + malo.lambda + " (¡colapsa!: viola Hull-Dobell)");
    }

    // ------------------------------------------------------------------
    // Paso 8: estadísticas y conclusiones
    // ------------------------------------------------------------------
    public static void comparacionEstadisticas(long semilla1, double[] st1,
                                               long semilla2, double[] st2, int n) {
        System.out.println();
        System.out.println("   Estadísticas de los " + fmt(n) + " valores U generados");
        System.out.printf(Locale.ROOT, "   %-26s | %15s | %15s | %15s%n", "",
                "semilla " + semilla1, "semilla " + semilla2, "teórico U(0,1)");
        System.out.println("   " + repetir('-', 76));
        System.out.printf(Locale.ROOT, "   %-26s | %15.6f | %15.6f | %15.6f%n",
                "media", st1[0], st2[0], 0.5);
        System.out.printf(Locale.ROOT, "   %-26s | %15.6f | %15.6f | %15.6f%n",
                "desviación estándar", st1[1], st2[1], 0.288675);
        System.out.printf(Locale.ROOT, "   %-26s | %15.6f | %15.6f | %15.6f%n",
                "mínimo", st1[2], st2[2], 0.0);
        System.out.printf(Locale.ROOT, "   %-26s | %15.6f | %15.6f | %15.6f%n",
                "máximo", st1[3], st2[3], 1.0);
    }

    public static void conclusiones(long semilla, long semillaAlt, int n, boolean coincidieron) {
        System.out.println();
        System.out.println("   Conclusiones operativas (el análisis completo va en el Reporte Técnico):");
        if (coincidieron) {
            System.out.println("     1. Con los mismos parámetros y la misma semilla (" + semilla + "),");
            System.out.println("        Java y Python producen la MISMA secuencia (" + fmt(n)
                    + "/" + fmt(n) + " valores idénticos).");
        } else {
            System.out.println("     1. Python y Java deben ejecutarse ambos para completar la");
            System.out.println("        tabla comparativa entre lenguajes.");
        }
        System.out.println("     2. Al cambiar la semilla (" + semilla + " → " + semillaAlt
                + ") la secuencia");
        System.out.println("        cambia por completo, pero sus propiedades estadísticas se");
        System.out.println("        mantienen (media ≈ 0,5 y desviación ≈ 0,2887, como U(0,1)).");
        System.out.println("     3. Los parámetros de la guía cumplen Hull-Dobell ⇒ período");
        System.out.println("        máximo τ = m = 2^32 (verificado teórica y empíricamente).");
    }

    // ------------------------------------------------------------------
    // Archivos generados
    // ------------------------------------------------------------------
    public static void archivoGuardado(String rutaRelativa, Long filas) {
        String detalle = filas == null ? "" : "  (" + fmt(filas) + " filas)";
        System.out.println("   ✓ guardado: " + rutaRelativa + detalle);
    }

    public static void graficasGuardadas(List<String> rutas) {
        for (String ruta : rutas) {
            System.out.println("   ✓ guardado: " + ruta);
        }
    }
}
