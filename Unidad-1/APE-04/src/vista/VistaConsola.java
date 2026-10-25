package vista;

import java.nio.file.Path;
import java.util.List;
import modelo.RegistroSir;

public class VistaConsola {
    public void mostrarInicio() {
        System.out.println("=== SIMULACION SIR EN JAVA ===");
        System.out.println("Poblacion: 100 | Infectados iniciales: 5");
        System.out.println("Beta: 0.30 | Gamma: 0.10 | Tiempo maximo: 100");
        System.out.println();
    }

    public void mostrarResultados(List<RegistroSir> resultados) {
        System.out.printf("%8s %14s %12s %13s%n",
                "Tiempo", "Susceptibles", "Infectados", "Recuperados");

        // En consola se muestra una fila cada 10 pasos.
        // En el CSV se guardan todos los pasos, desde 0 hasta 100.
        for (RegistroSir registro : resultados) {
            if (registro.getTiempo() % 10 == 0) {
                System.out.printf("%8d %14d %12d %13d%n",
                        registro.getTiempo(), registro.getSusceptibles(),
                        registro.getInfectados(), registro.getRecuperados());
            }
        }
    }

    public void mostrarArchivo(Path ruta, int cantidadRegistros) {
        System.out.println();
        System.out.println("Registros guardados: " + cantidadRegistros);
        System.out.println("CSV generado: " + ruta.toAbsolutePath());
        System.out.println("Coloca este CSV junto al archivo modelo_sir_netlogo.nlogo");
    }

    public void mostrarError(String mensaje) {
        System.err.println("Error: " + mensaje);
    }
}
