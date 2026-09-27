import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Utility class that validates AND normalises user input.
 * Normalisation (trim, collapse spaces, Title Case) is what keeps the
 * formatting of all certificates consistent.
 */
public final class Validator {

    private static final Pattern NAME_PATTERN =
            Pattern.compile("[A-Za-z][A-Za-z .'-]{1,49}");               // 2 to 50 characters
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}");

    private Validator() { }   // static utility class - no objects

    /** Removes leading/trailing spaces and collapses repeated spaces. */
    private static String collapse(String raw) {
        return raw == null ? "" : raw.trim().replaceAll("\\s+", " ");
    }

    /** "  aRUN   kumar " -> "Arun Kumar",  "o'brien" -> "O'Brien". */
    private static String toTitleCase(String s) {
        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                sb.append(capitalizeNext ? Character.toUpperCase(c) : Character.toLowerCase(c));
                capitalizeNext = false;
            } else {
                sb.append(c);
                capitalizeNext = true;
            }
        }
        return sb.toString();
    }

    public static String cleanName(String raw) throws InvalidInputException {
        String s = collapse(raw);
        if (s.isEmpty()) {
            throw new InvalidInputException("Name cannot be empty.");
        }
        if (!NAME_PATTERN.matcher(s).matches()) {
            throw new InvalidInputException(
                    "Name must be 2-50 characters (letters, spaces, '.', '-' or apostrophe only).");
        }
        return toTitleCase(s);
    }

    public static String cleanEmail(String raw) throws InvalidInputException {
        String s = collapse(raw).toLowerCase();
        if (s.isEmpty()) {
            throw new InvalidInputException("Email cannot be empty.");
        }
        if (s.length() > 100 || !EMAIL_PATTERN.matcher(s).matches()) {
            throw new InvalidInputException("Invalid email format (example: name@college.edu).");
        }
        return s;
    }

    public static String cleanText(String field, String raw, int min, int max)
            throws InvalidInputException {
        String s = collapse(raw);
        if (s.isEmpty()) {
            throw new InvalidInputException(field + " cannot be empty.");
        }
        if (s.length() < min || s.length() > max) {
            throw new InvalidInputException(
                    field + " must be between " + min + " and " + max + " characters.");
        }
        return s;
    }

    public static LocalDate parseDate(String raw) throws InvalidInputException {
        LocalDate date;
        try {
            date = LocalDate.parse(collapse(raw));           // ISO format yyyy-MM-dd
        } catch (DateTimeParseException e) {
            throw new InvalidInputException(
                    "Invalid date. Use the format yyyy-MM-dd (e.g. 2026-08-21).");
        }
        if (date.isAfter(LocalDate.now())) {
            throw new InvalidInputException("Event date cannot be in the future.");
        }
        if (date.getYear() < 2000) {
            throw new InvalidInputException("Event date must be in the year 2000 or later.");
        }
        return date;
    }
}
