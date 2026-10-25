package modelo;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ExportadorCsv {
    public void guardar(List<RegistroSir> resultados, Path ruta) throws IOException {
        try (BufferedWriter archivo = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            archivo.write("tiempo,susceptibles,infectados,recuperados");
            archivo.newLine();

            for (RegistroSir registro : resultados) {
                archivo.write(registro.getTiempo() + ","
                        + registro.getSusceptibles() + ","
                        + registro.getInfectados() + ","
                        + registro.getRecuperados());
                archivo.newLine();
            }
        }
    }
}
