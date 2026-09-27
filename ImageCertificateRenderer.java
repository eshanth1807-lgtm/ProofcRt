import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.imageio.ImageIO;

/**
 * Draws a certificate as a PNG image using only the standard Java 2D API.
 * Every certificate uses the SAME template (size, colours, fonts, positions),
 * so all output is visually identical except for the participant data.
 */
public class ImageCertificateRenderer implements CertificateRenderer {

    private static final int WIDTH = 1400;      // A4 landscape ratio
    private static final int HEIGHT = 990;
    private static final Color NAVY = new Color(20, 40, 90);
    private static final Color GOLD = new Color(184, 148, 60);
    private static final Color PAPER = new Color(253, 250, 240);
    private static final Color TEXT = new Color(50, 50, 50);

    @Override
    public String getFormatName() {
        return "PNG image";
    }

    @Override
    public File render(Participant p, Event e, File outputDir) throws IOException {
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background and double border
        g.setColor(PAPER);
        g.fillRect(0, 0, WIDTH, HEIGHT);
        g.setColor(NAVY);
        g.setStroke(new BasicStroke(10));
        g.drawRect(30, 30, WIDTH - 60, HEIGHT - 60);
        g.setColor(GOLD);
        g.setStroke(new BasicStroke(3));
        g.drawRect(52, 52, WIDTH - 104, HEIGHT - 104);

        // Heading
        centered(g, "CERTIFICATE", new Font(Font.SERIF, Font.BOLD, 78), NAVY, 190);
        centered(g, "OF PARTICIPATION", new Font(Font.SERIF, Font.PLAIN, 34), GOLD, 245);
        g.setColor(GOLD);
        g.drawLine(500, 275, WIDTH - 500, 275);

        // Body text
        centered(g, "This is to certify that", new Font(Font.SERIF, Font.ITALIC, 28), TEXT, 340);
        Font nameFont = fit(g, p.getName(), Font.BOLD | Font.ITALIC, 66, 30, 1000);
        centered(g, p.getName(), nameFont, NAVY, 425);
        g.setColor(GOLD);
        g.drawLine(300, 448, WIDTH - 300, 448);

        centered(g, "of " + p.getDepartment(), new Font(Font.SERIF, Font.PLAIN, 28), TEXT, 500);
        centered(g, "has successfully participated in",
                new Font(Font.SERIF, Font.ITALIC, 28), TEXT, 565);
        Font titleFont = fit(g, e.getTitle(), Font.BOLD, 48, 26, 1100);
        centered(g, e.getTitle(), titleFont, NAVY, 630);
        centered(g, "held on " + e.getFormattedDate() + " at " + e.getVenue(),
                new Font(Font.SERIF, Font.PLAIN, 26), TEXT, 690);
        centered(g, "Organized by " + e.getOrganizer(),
                new Font(Font.SERIF, Font.PLAIN, 26), TEXT, 735);

        // Seal in the centre
        g.setColor(GOLD);
        g.fillOval(WIDTH / 2 - 55, 770, 110, 110);
        g.setColor(PAPER);
        g.setStroke(new BasicStroke(2));
        g.drawOval(WIDTH / 2 - 46, 779, 92, 92);
        centered(g, "CERTIFIED", new Font(Font.SERIF, Font.BOLD, 14), PAPER, 831);

        // Signature blocks
        String issued = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH));
        block(g, issued, "Date of Issue", 260, 830);
        block(g, e.getSignatory(), "Authorized Signatory", WIDTH - 260, 830);

        // Certificate ID (unique per participant)
        centered(g, "Certificate ID: " + p.getCertificateId(),
                new Font(Font.MONOSPACED, Font.PLAIN, 20), TEXT, 915);
        g.dispose();

        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Cannot create output folder: " + outputDir);
        }
        File file = new File(outputDir, p.getCertificateId() + ".png");
        ImageIO.write(img, "png", file);
        return file;
    }

    /** Draws text horizontally centred on the page at baseline y. */
    private void centered(Graphics2D g, String text, Font font, Color color, int y) {
        g.setFont(font);
        g.setColor(color);
        int x = (WIDTH - g.getFontMetrics().stringWidth(text)) / 2;
        g.drawString(text, x, y);
    }

    /** Reduces the font size until the text fits inside maxWidth pixels. */
    private Font fit(Graphics2D g, String text, int style, int maxSize, int minSize, int maxWidth) {
        for (int size = maxSize; size > minSize; size -= 2) {
            Font f = new Font(Font.SERIF, style, size);
            if (g.getFontMetrics(f).stringWidth(text) <= maxWidth) {
                return f;
            }
        }
        return new Font(Font.SERIF, style, minSize);
    }

    /** A line with a value above it and a caption below it (date / signature). */
    private void block(Graphics2D g, String value, String caption, int centerX, int lineY) {
        g.setColor(TEXT);
        g.setStroke(new BasicStroke(2));
        g.drawLine(centerX - 170, lineY, centerX + 170, lineY);
        g.setFont(new Font(Font.SERIF, Font.BOLD, 24));
        g.drawString(value, centerX - g.getFontMetrics().stringWidth(value) / 2, lineY - 12);
        g.setFont(new Font(Font.SERIF, Font.ITALIC, 20));
        g.drawString(caption, centerX - g.getFontMetrics().stringWidth(caption) / 2, lineY + 30);
    }
}
