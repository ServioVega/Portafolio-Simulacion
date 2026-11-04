package vista;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Vista · Gráficas del LCG dibujadas con Java2D y guardadas como PNG.
 *
 * No requiere librerías externas: usa exclusivamente el JDK (java.awt),
 * por lo que funciona incluso en modo headless (sin interfaz gráfica).
 */
public class Grafica {

    private static final Color SERIE = new Color(31, 119, 180);
    private static final Color SERIE_2 = new Color(214, 39, 40);
    private static final Color EJES = new Color(55, 55, 55);
    private static final Color REJILLA = new Color(225, 225, 225);
    private static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 19);
    private static final Font FUENTE_EJES = new Font("SansSerif", Font.PLAIN, 12);

    /** Paso 5: gráfica de la secuencia U(n) frente a n. */
    public void secuencia(double[] us, long semilla, File salida) throws IOException {
        int ancho = 1000;
        int alto = 540;
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics();
        preparar(g);

        double x0 = 85, x1 = ancho - 30, y0 = 85, y1 = alto - 80;
        marco(g, x0, y0, x1, y1, 1, us.length, 0.0, 1.0, "n", "U(n)", 10, false, true);

        g.setColor(SERIE);
        g.setStroke(new BasicStroke(0.9f));
        double paso = (x1 - x0) / Math.max(1, us.length - 1);
        for (int i = 1; i < us.length; i++) {
            g.draw(new Line2D.Double(
                    x0 + (i - 1) * paso, y1 - us[i - 1] * (y1 - y0),
                    x0 + i * paso, y1 - us[i] * (y1 - y0)));
        }

        titulo(g, "Secuencia U(n) generada por el LCG (Java, semilla " + semilla + ")",
                ancho / 2, 40);
        pie(g, "Los " + us.length + " valores caen en [0, 1) sin patrón visible", ancho, alto - 28);
        guardar(imagen, g, salida);
    }

    /** Dispersión de pares consecutivos (U(n), U(n+1)): prueba visual de Marsaglia. */
    public void dispersion(double[] us, long semilla, File salida) throws IOException {
        int lado = 640;
        BufferedImage imagen = new BufferedImage(lado, lado, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics();
        preparar(g);

        double x0 = 85, x1 = lado - 45, y0 = 90, y1 = lado - 75;
        marco(g, x0, y0, x1, y1, 0.0, 1.0, 0.0, 1.0, "U(n)", "U(n+1)", 4, true, true);

        g.setColor(new Color(31, 119, 180, 110));
        for (int i = 1; i < us.length; i++) {
            int px = (int) Math.round(x0 + us[i - 1] * (x1 - x0));
            int py = (int) Math.round(y1 - us[i] * (y1 - y0));
            g.fillRect(px - 1, py - 1, 3, 3);
        }

        titulo(g, "Dispersión (U(n), U(n+1)) · Java · semilla " + semilla, lado / 2, 42);
        pie(g, (us.length - 1) + " pares consecutivos: nube uniforme, sin hiperplanos",
                lado, lado - 24);
        guardar(imagen, g, salida);
    }

    /** Histograma de los valores U: uniformidad de la secuencia. */
    public void histograma(double[] us, long semilla, File salida) throws IOException {
        int ancho = 1000;
        int alto = 540;
        int clases = 20;
        int[] cuentas = new int[clases];
        for (double u : us) {
            int b = (int) Math.floor(u * clases);
            if (b >= clases) {
                b = clases - 1;
            }
            cuentas[b]++;
        }
        int maximo = 0;
        for (int c : cuentas) {
            if (c > maximo) {
                maximo = c;
            }
        }
        int techo = (int) (Math.ceil(maximo / 100.0) * 100);   // escala "redonda"
        if (techo < maximo + 1) {
            techo = maximo + 1;
        }

        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imagen.createGraphics();
        preparar(g);

        double x0 = 85, x1 = ancho - 30, y0 = 85, y1 = alto - 80;
        marco(g, x0, y0, x1, y1, 0.0, 1.0, 0, techo, "U(n)", "frecuencia", 10, true, false);

        double anchoBarra = (x1 - x0) / clases;
        g.setColor(SERIE);
        for (int b = 0; b < clases; b++) {
            int px = (int) Math.round(x0 + b * anchoBarra);
            int py = (int) Math.round(y1 - cuentas[b] / (double) techo * (y1 - y0));
            int w = Math.max(2, (int) Math.round(anchoBarra) - 3);
            g.fillRect(px + 1, py, w, (int) Math.round(y1) - py);
        }

        double esperado = us.length / (double) clases;
        int ye = (int) Math.round(y1 - esperado / techo * (y1 - y0));
        g.setColor(SERIE_2);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0f, new float[]{7f, 5f}, 0f));
        g.draw(new Line2D.Double(x0, ye, x1, ye));
        g.setFont(FUENTE_EJES);
        g.drawString("esperado = " + String.format(Locale.ROOT, "%.0f", esperado),
                (int) x0 + 10, ye - 8);

        titulo(g, "Histograma de U(n) · Java · semilla " + semilla
                + " · " + us.length + " valores", ancho / 2, 40);
        pie(g, "20 clases en [0, 1): las barras oscilan alrededor de la frecuencia esperada",
                ancho, alto - 28);
        guardar(imagen, g, salida);
    }

    // ------------------------------------------------------------------
    // Utilidades de dibujo
    // ------------------------------------------------------------------
    private static void preparar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 2000, 2000);
    }

    /**
     * Dibuja el marco del área de trazado: rejilla, marcas, números y títulos
     * de los ejes. minX/maxX y minY/maxY son los rangos de datos;
     * divX/divY la cantidad de divisiones; decX/decY si se muestran decimales.
     */
    private void marco(Graphics2D g, double x0, double y0, double x1, double y1,
                       double minX, double maxX, double minY, double maxY,
                       String etiquetaX, String etiquetaY,
                       int divX, boolean decX, boolean decY) {
        g.setFont(FUENTE_EJES);

        // rejilla y marcas del eje Y
        for (int i = 0; i <= 4; i++) {
            double fy = minY + (maxY - minY) * i / 4.0;
            double yy = y1 - (fy - minY) / (maxY - minY) * (y1 - y0);
            g.setColor(REJILLA);
            g.draw(new Line2D.Double(x0, yy, x1, yy));
            g.setColor(EJES);
            String et = decY
                    ? String.format(Locale.ROOT, "%.2f", fy)
                    : String.format(Locale.ROOT, "%.0f", fy);
            g.drawString(et, (int) x0 - 44, (int) (yy + 4));
        }

        // rejilla y marcas del eje X
        for (int i = 0; i <= divX; i++) {
            double fx = minX + (maxX - minX) * i / (double) divX;
            double xx = x0 + (fx - minX) / (maxX - minX) * (x1 - x0);
            g.setColor(REJILLA);
            g.draw(new Line2D.Double(xx, y0, xx, y1));
            g.setColor(EJES);
            String et = decX
                    ? String.format(Locale.ROOT, "%.2f", fx)
                    : String.format(Locale.ROOT, "%.0f", fx);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(et, (int) (xx - fm.stringWidth(et) / 2.0), (int) (y1 + 22));
        }

        // borde del área
        g.setColor(EJES);
        g.draw(new Line2D.Double(x0, y0, x1, y0));
        g.draw(new Line2D.Double(x0, y1, x1, y1));
        g.draw(new Line2D.Double(x0, y0, x0, y1));
        g.draw(new Line2D.Double(x1, y0, x1, y1));

        // título del eje X
        FontMetrics fm = g.getFontMetrics();
        g.drawString(etiquetaX,
                (int) ((x0 + x1) / 2 - fm.stringWidth(etiquetaX) / 2.0), (int) (y1 + 46));

        // título del eje Y (rotado 90°)
        AffineTransform original = g.getTransform();
        double py = (y0 + y1) / 2 + fm.stringWidth(etiquetaY) / 2.0;
        g.rotate(-Math.PI / 2.0, 30, py);
        g.drawString(etiquetaY, 30, (int) py);
        g.setTransform(original);
    }

    private static void titulo(Graphics2D g, String texto, int cx, int y) {
        g.setFont(FUENTE_TITULO);
        g.setColor(EJES);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(texto, cx - fm.stringWidth(texto) / 2, y);
    }

    private static void pie(Graphics2D g, String texto, int ancho, int y) {
        g.setFont(FUENTE_EJES);
        g.setColor(new Color(110, 110, 110));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(texto, ancho / 2 - fm.stringWidth(texto) / 2, y);
    }

    private static void guardar(BufferedImage imagen, Graphics2D g, File salida)
            throws IOException {
        g.dispose();
        ImageIO.write(imagen, "png", salida);
    }
}
