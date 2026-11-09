import controlador.Controlador;

/**
 * APE-06 · Validación estadística de números pseudoaleatorios (Chi-cuadrado y
 * Kolmogorov-Smirnov) y estimación de π por Monte Carlo con el LCG.
 * Asignatura: Simulación · Unidad 2 · Práctica Nro. 06
 *
 * Punto de entrada del programa (patrón MVC):
 *
 *     Modelo       → modelo/LCG.java            : generador congruencial lineal
 *                    modelo/Pruebas.java        : Chi-cuadrado y Kolmogorov-Smirnov
 *                    modelo/MonteCarlo.java     : estimación de π
 *     Vista        → vista/Tabla.java           : tablas y mensajes en consola
 *                    vista/Grafica.java         : gráficas PNG (Java2D)
 *     Controlador  → controlador/Controlador.java
 *
 * Uso:
 *     ./ejecutar.sh                        # ejecución completa (guía)
 *     java Main --semilla 67890            # cambia la semilla
 *     java Main --n 20000                  # cambia la cantidad de valores
 *     java Main --sin-graficas             # no genera las imágenes PNG
 */
public class Main {

    public static void main(String[] args) {
        long semilla = modelo.LCG.SEMILLA_GUIA;
        int n = controlador.Controlador.N_GUIA;
        boolean sinGraficas = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--semilla" -> semilla = Long.parseLong(args[++i]);
                case "--n" -> n = Integer.parseInt(args[++i]);
                case "--sin-graficas" -> sinGraficas = true;
                default -> {
                    System.out.println("Argumento no reconocido: " + args[i]);
                    System.out.println("Uso: java Main [--semilla X] [--n N] [--sin-graficas]");
                    return;
                }
            }
        }

        new controlador.Controlador(semilla, n).ejecutar(sinGraficas);
    }
}
