package vista;

import modelo.Pruebas;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Vista · Gráficas PNG con Java2D (sin librerías externas), igual que en el APE-05.
 *
 * Genera:
 *   · chi_cuadrado_frecuencias.png  → barras observadas vs esperada,
 *   · kolmogorov_smirnov_cdf.png    → F empírica vs F teórica.
 */
public final class Grafica {

    private Grafica() { }

    private static Path carpetaImg() throws IOException {
        String env = System.getenv("APE6_IMG");
        Path base = (env != null && !env.isEmpty())
                ? Paths.get(env)
                : Paths.get("..", "..", "img").toAbsolutePath().normalize();
        Files.createDirectories(base);
        return base;
    }

    /** Gráfica de barras: frecuencias observadas vs frecuencia esperada. */
    public static String graficarHistograma(Pruebas.ChiCuadrado res, String titulo) throws IOException {
        int w = 900, h = 500;
        var img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        var g = img.createGraphics();
        java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
        configurar(g2, w, h, titulo, "Intervalo", "Frecuencia");

        int k = res.k;
        int x0 = 80, y0 = h - 60, alto = h - 140;
        double maxFrec = Math.max(res.esperada * 1.15,
                java.util.Arrays.stream(res.observadas).max().orElse(1) * 1.10);
        double ancho = (w - x0 - 40) / (double) k;
        for (int i = 0; i < k; i++) {
            int altoBarra = (int) Math.round(res.observadas[i] / maxFrec * alto);
            g2.setColor(new java.awt.Color(43, 123, 186));
            g2.fillRect((int) (x0 + i * ancho + 2), y0 - altoBarra, (int) ancho - 4, altoBarra);
            g2.setColor(java.awt.Color.BLACK);
            g2.drawRect((int) (x0 + i * ancho + 2), y0 - altoBarra, (int) ancho - 4, altoBarra);
            g2.drawString(String.format("[%.1f,%.1f)", i / (double) k, (i + 1) / (double) k),
                    (int) (x0 + i * ancho), y0 + 18);
        }
        int yEsp = y0 - (int) Math.round(res.esperada / maxFrec * alto);
        g2.setColor(new java.awt.Color(214, 39, 40));
        var dash = new java.awt.BasicStroke(2f, java.awt.BasicStroke.CAP_BUTT,
                java.awt.BasicStroke.JOIN_MITER, 10f, new float[]{8f}, 0f);
        g2.setStroke(dash);
        g2.drawLine(x0, yEsp, w - 40, yEsp);
        g2.setStroke(new java.awt.BasicStroke(1f));
        g2.drawString(String.format("Esperada = %.0f", res.esperada), w - 220, yEsp - 8);

        Path ruta = carpetaImg().resolve("chi_cuadrado_frecuencias.png");
        javax.imageio.ImageIO.write(img, "png", ruta.toFile());
        g2.dispose();
        return ruta.toString();
    }

    /** Gráfica de la CDF empírica escalonada contra la CDF teórica F(x)=x. */
    public static String graficarKS(double[] secuencia, Pruebas.KolmogorovSmirnov res,
                                    String titulo) throws IOException {
        int w = 900, h = 500;
        var img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        var g2 = (java.awt.Graphics2D) img.createGraphics();
        configurar(g2, w, h, titulo, "x", "F(x)");

        int x0 = 80, y0 = h - 60, alto = h - 140, ancho = w - x0 - 60;
        // Ejes 0..1
        for (int t = 0; t <= 10; t++) {
            int x = x0 + (int) (ancho * t / 10.0);
            int y = y0 - (int) (alto * t / 10.0);
            g2.setColor(new java.awt.Color(230, 230, 230));
            g2.drawLine(x, y0, x, y0 - alto);
            g2.drawLine(x0, y, x0 + ancho, y);
            g2.setColor(java.awt.Color.BLACK);
            g2.drawString(String.format("%.1f", t / 10.0), x - 12, y0 + 18);
            g2.drawString(String.format("%.1f", t / 10.0), x0 - 40, y + 5);
        }
        // F teórica
        g2.setColor(new java.awt.Color(214, 39, 40));
        var dash = new java.awt.BasicStroke(2.5f, java.awt.BasicStroke.CAP_BUTT,
                java.awt.BasicStroke.JOIN_MITER, 10f, new float[]{10f}, 0f);
        g2.setStroke(dash);
        g2.drawLine(x0, y0, x0 + ancho, y0 - alto);
        // F empírica escalonada (submuestreo para pantallas legibles)
        double[] ordenados = secuencia.clone();
        java.util.Arrays.sort(ordenados);
        int n = ordenados.length;
        g2.setColor(new java.awt.Color(43, 123, 186));
        g2.setStroke(new java.awt.BasicStroke(1.4f));
        int paso = Math.max(1, n / 2000);
        int xPrev = x0, yPrev = y0;
        for (int i = 1; i <= n; i += paso) {
            int x = x0 + (int) Math.min(ancho, ordenados[Math.min(i, n) - 1] * ancho);
            int y = y0 - (int) ((double) i / n * alto);
            g2.drawLine(xPrev, yPrev, x, yPrev);
            g2.drawLine(x, yPrev, x, y);
            xPrev = x; yPrev = y;
        }
        g2.drawString("F empírica (azul) · F teórica = x (roja discontinua) · D = "
                + String.format("%.6f", res.estadistico), x0 + 10, y0 - alto + 20);

        Path ruta = carpetaImg().resolve("kolmogorov_smirnov_cdf.png");
        javax.imageio.ImageIO.write(img, "png", ruta.toFile());
        g2.dispose();
        return ruta.toString();
    }

    /** Marco, título y ejes comunes. */
    private static void configurar(java.awt.Graphics2D g2, int w, int h,
                                   String titulo, String ejeX, String ejeY) {
        g2.setColor(new java.awt.Color(0, 0, 0, 0));
        g2.fillRect(0, 0, w, h);
        g2.setColor(java.awt.Color.WHITE);
        g2.fillRect(0, 0, w, h);
        g2.setColor(java.awt.Color.BLACK);
        g2.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 16));
        g2.drawString(titulo, 60, 32);
        g2.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 12));
        g2.drawLine(80, h - 60, w - 40, h - 60);
        g2.drawLine(80, 60, 80, h - 60);
        g2.drawString(ejeX, w / 2 - 10, h - 25);
        var orig = g2.getFontMetrics();
        var rot = g2.getTransform();
        g2.rotate(-Math.PI / 2, 30, h / 2);
        g2.drawString(ejeY, 30 - orig.stringWidth(ejeY) / 2, h / 2);
        g2.setTransform(rot);
    }

    /** Evita avisos de File sin usar. */
    @SuppressWarnings("unused")
    private static File sinUso() { return null; }
}
