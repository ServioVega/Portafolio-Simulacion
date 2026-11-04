import controlador.Controlador;

/**
 * APE-05 · Implementación y análisis del Generador Congruencial Lineal (LCG)
 * Asignatura: Simulación · Unidad 2 · Práctica Nro. 05
 *
 * Punto de entrada del programa en Java (patrón MVC):
 *     Modelo      → modelo/LCG.java              : lógica matemática
 *     Vista       → vista/Tabla.java             : tablas en consola
 *                   vista/Grafica.java           : gráficas PNG (Java2D)
 *     Controlador → controlador/Controlador.java : coordinación de los pasos 1 a 8
 *
 * Uso:
 *     java Main                            (ejecución completa, guía)
 *     java Main --semilla 67890            (cambia la semilla principal)
 *     java Main --n 5000                   (cambia la cantidad de valores)
 *     java Main --sin-periodo-empirico     (omite el conteo exhaustivo de 2^32)
 *     java Main --solo-comparar            (solo compara los CSV Python/Java)
 */
public class Main {

    public static void main(String[] args) {
        long semilla = 12345L;
        long semillaAlternativa = 987654321L;
        int n = 10000;
        boolean soloComparar = false;
        boolean conPeriodoEmpirico = true;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--semilla":
                    semilla = Long.parseLong(args[++i]);
                    break;
                case "--semilla-alternativa":
                    semillaAlternativa = Long.parseLong(args[++i]);
                    break;
                case "--n":
                    n = Integer.parseInt(args[++i]);
                    break;
                case "--solo-comparar":
                    soloComparar = true;
                    break;
                case "--sin-periodo-empirico":
                    conPeriodoEmpirico = false;
                    break;
                default:
                    System.err.println("Opción no reconocida: " + args[i]);
                    System.exit(1);
            }
        }

        Controlador controlador = new Controlador(semilla, semillaAlternativa, n,
                soloComparar, conPeriodoEmpirico);
        controlador.ejecutar();
    }
}
