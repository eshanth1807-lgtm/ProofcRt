import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Produces a plain-text certificate framed in a fixed-width box.
 * Useful as a lightweight, printable / e-mail-friendly alternative.
 */
public class TextCertificateRenderer implements CertificateRenderer {

    private static final int WIDTH = 72;               // total width including borders
    private static final int INNER = WIDTH - 2;

    @Override
    public String getFormatName() {
        return "Text file";
    }

    @Override
    public File render(Participant p, Event e, File outputDir) throws IOException {
        List<String> lines = new ArrayList<>();
        String bar = "+" + "=".repeat(INNER) + "+";
        lines.add(bar);
        lines.add(row(""));
        lines.add(row("CERTIFICATE OF PARTICIPATION"));
        lines.add(row("---------------------------"));
        lines.add(row(""));
        lines.add(row("This is to certify that"));
        lines.add(row(""));
        lines.add(row(p.getName().toUpperCase()));
        lines.add(row("of " + p.getDepartment()));
        lines.add(row(""));
        lines.add(row("has successfully participated in"));
        lines.add(row(""));
        for (String part : wrap(e.getTitle(), INNER - 6)) {
            lines.add(row(part));
        }
        lines.add(row(""));
        lines.add(row("held on " + e.getFormattedDate()));
        lines.add(row("at " + e.getVenue()));
        lines.add(row("Organized by " + e.getOrganizer()));
        lines.add(row(""));
        lines.add(row(e.getSignatory() + " (Authorized Signatory)"));
        lines.add(row("Date of Issue: " + LocalDate.now()));
        lines.add(row("Certificate ID: " + p.getCertificateId()));
        lines.add(row(""));
        lines.add(bar);

        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Cannot create output folder: " + outputDir);
        }
        File file = new File(outputDir, p.getCertificateId() + ".txt");
        Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
        return file;
    }

    /** Centres text between two '|' borders. */
    private String row(String text) {
        if (text.length() > INNER) {
            text = text.substring(0, INNER);
        }
        int left = (INNER - text.length()) / 2;
        int right = INNER - text.length() - left;
        return "|" + " ".repeat(left) + text + " ".repeat(right) + "|";
    }

    /** Splits long text into lines of at most maxLen characters (word wrap). */
    private List<String> wrap(String text, int maxLen) {
        List<String> result = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        for (String word : text.split(" ")) {
            if (line.length() > 0 && line.length() + 1 + word.length() > maxLen) {
                result.add(line.toString());
                line.setLength(0);
            }
            if (line.length() > 0) {
                line.append(' ');
            }
            line.append(word);
        }
        if (line.length() > 0) {
            result.add(line.toString());
        }
        return result;
    }
}
