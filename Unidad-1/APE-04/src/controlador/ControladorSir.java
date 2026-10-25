package controlador;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import modelo.ExportadorCsv;
import modelo.ModeloSir;
import modelo.RegistroSir;
import vista.VistaConsola;

public class ControladorSir {
    private final ModeloSir modelo;
    private final VistaConsola vista;
    private final ExportadorCsv exportador;

    public ControladorSir(ModeloSir modelo, VistaConsola vista,
                          ExportadorCsv exportador) {
        this.modelo = modelo;
        this.vista = vista;
        this.exportador = exportador;
    }

    public void ejecutar() {
        vista.mostrarInicio();
        List<RegistroSir> resultados = modelo.simular();
        vista.mostrarResultados(resultados);

        Path ruta = Paths.get("resultados_java.csv");
        try {
            exportador.guardar(resultados, ruta);
            vista.mostrarArchivo(ruta, resultados.size());
        } catch (IOException error) {
            vista.mostrarError("No se pudo crear el CSV: " + error.getMessage());
        }
    }
}
