import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Holds the details of the technical event / workshop. The same Event object
 * is used for every certificate, which guarantees consistent content.
 */
public class Event {
    private final String title;
    private final LocalDate date;
    private final String venue;
    private final String organizer;
    private final String signatory;

    public Event(String title, LocalDate date, String venue, String organizer, String signatory) {
        this.title = title;
        this.date = date;
        this.venue = venue;
        this.organizer = organizer;
        this.signatory = signatory;
    }

    public String getTitle()     { return title; }
    public LocalDate getDate()   { return date; }
    public String getVenue()     { return venue; }
    public String getOrganizer() { return organizer; }
    public String getSignatory() { return signatory; }

    /** Date in a fixed, standard format, e.g. "21 August 2026". */
    public String getFormattedDate() {
        return date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH));
    }
}
