import controlador.Controlador;

/**
 * APE-07 · Estimación de resultados mediante el método de Monte Carlo
 * (estimación de π con puntos dentro de un círculo inscrito en un cuadrado).
 * Asignatura: Simulación · Unidad 2 · Práctica Nro. 07
 *
 * Punto de entrada del programa (patrón MVC, sin vista):
 *
 *     Modelo       → modelo/LCG.java            : generador congruencial lineal
 *                    modelo/MonteCarlo.java     : estimación de π y error teórico
 *     Controlador  → controlador/Controlador.java
 *                   (la pequeña tabla de resultados se imprime en consola)
 *
 * Uso:
 *     ./ejecutar.sh                        # ejecución completa (guía)
 *     java Main --semilla 67890            # cambia la semilla del experimento
 */
public class Main {

    public static void main(String[] args) {
        long semilla = modelo.LCG.SEMILLA_GUIA;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--semilla" -> semilla = Long.parseLong(args[++i]);
                default -> {
                    System.out.println("Argumento no reconocido: " + args[i]);
                    System.out.println("Uso: java Main [--semilla X]");
                    return;
                }
            }
        }

        new controlador.Controlador(semilla).ejecutar();
    }
}
