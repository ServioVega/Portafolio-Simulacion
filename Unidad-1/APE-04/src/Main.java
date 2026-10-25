import controlador.ControladorSir;
import modelo.ExportadorCsv;
import modelo.ModeloSir;
import vista.VistaConsola;

public class Main {
    public static void main(String[] args) {
        // Parametros indicados en la guia de la practica.
        ModeloSir modelo = new ModeloSir(
                100,   // Poblacion
                5,     // Infectados iniciales
                0.30,  // Beta: probabilidad de contagio por contacto
                0.10,  // Gamma: probabilidad de recuperacion por paso
                100,   // Tiempo maximo
                2026L  // Semilla aleatoria para repetir el experimento
        );

        VistaConsola vista = new VistaConsola();
        ExportadorCsv exportador = new ExportadorCsv();
        ControladorSir controlador = new ControladorSir(modelo, vista, exportador);
        controlador.ejecutar();
    }
}
