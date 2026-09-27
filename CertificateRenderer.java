import java.io.File;
import java.io.IOException;

/**
 * Interface implemented by every certificate format (PNG, TXT, ...).
 * The manager works with this interface only (polymorphism), so a new
 * format can be added without changing any existing code.
 */
public interface CertificateRenderer {
    /** Creates the certificate file for one participant and returns it. */
    File render(Participant participant, Event event, File outputDir) throws IOException;

    /** Short name of the format, used in messages and reports. */
    String getFormatName();
}
