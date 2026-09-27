import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Digital Certificate Generator - menu driven console application.
 * Standardised participation certificates are produced from participant data.
 */
public class Main {

    private static final Scanner SC = new Scanner(System.in);
    private static final CertificateManager MANAGER = new CertificateManager();
    private static final File OUTPUT_DIR = new File("certificates");

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");   // no GUI window needed
        int choice;
        do {
            printMenu();
            choice = readChoice();
            try {
                switch (choice) {
                    case 1:  setEventDetails();        break;
                    case 2:  addParticipant();         break;
                    case 3:  importCsv();              break;
                    case 4:  viewParticipants();       break;
                    case 5:  searchParticipants();     break;
                    case 6:  generateSingle();         break;
                    case 7:  generateAll();            break;
                    case 8:  removeParticipant();      break;
                    case 9:
                        System.out.println("Thank you for using the Digital Certificate Generator.");
                        break;
                    default: System.out.println("Invalid choice. Please enter a number from 1 to 9.");
                }
            } catch (InvalidInputException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
            }
        } while (choice != 9);
    }

    // ------------------------------------------------------------------ UI
    private static void printMenu() {
        System.out.println();
        System.out.println("===== DIGITAL CERTIFICATE GENERATOR =====");
        System.out.println("1. Set / update event details");
        System.out.println("2. Add participant");
        System.out.println("3. Import participants from CSV file");
        System.out.println("4. View all participants");
        System.out.println("5. Search participant");
        System.out.println("6. Generate certificate for one participant");
        System.out.println("7. Generate certificates for ALL participants");
        System.out.println("8. Remove participant");
        System.out.println("9. Exit");
    }

    private static String prompt(String message) {
        System.out.print(message);
        return SC.hasNextLine() ? SC.nextLine() : "";
    }

    /** Returns the menu choice; -1 for non-numeric input, 9 when input ends. */
    private static int readChoice() {
        System.out.print("Enter your choice: ");
        if (!SC.hasNextLine()) {
            return 9;
        }
        try {
            return Integer.parseInt(SC.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ---------------------------------------------------------- operations
    private static void setEventDetails() throws InvalidInputException {
        String title = Validator.cleanText("Event title", prompt("Event title           : "), 3, 80);
        LocalDate date = Validator.parseDate(prompt("Event date (yyyy-MM-dd): "));
        String venue = Validator.cleanText("Venue", prompt("Venue                 : "), 2, 60);
        String organizer = Validator.cleanText("Organizer", prompt("Organizer             : "), 2, 60);
        String signatory = Validator.cleanText("Signatory", prompt("Signatory name        : "), 2, 40);
        MANAGER.setEvent(new Event(title, date, venue, organizer, signatory));
        System.out.println("Event details saved. All certificates will use these details.");
    }

    private static void addParticipant() throws InvalidInputException {
        String name = prompt("Participant name : ");
        String email = prompt("E-mail           : ");
        String dept = prompt("Department       : ");
        Participant p = MANAGER.addParticipant(name, email, dept);
        System.out.println("Participant added: " + p.getName()
                + "  (Certificate ID: " + p.getCertificateId() + ")");
    }

    private static void importCsv() throws IOException {
        File csv = new File(prompt("CSV file path (name,email,department): ").trim());
        if (!csv.isFile()) {
            throw new IOException("File not found: " + csv);
        }
        CertificateManager.ImportSummary s = MANAGER.importFromCsv(csv);
        System.out.println("Import finished: " + s.getAdded() + " added, "
                + s.getSkipped() + " skipped.");
        for (String err : s.getErrors()) {
            System.out.println("  - " + err);
        }
    }

    private static void viewParticipants() {
        printTable(MANAGER.getParticipants());
    }

    private static void searchParticipants() {
        String key = prompt("Enter name / e-mail / department / ID to search: ");
        List<Participant> found = MANAGER.search(key);
        if (found.isEmpty()) {
            System.out.println("No matching participants found.");
        } else {
            printTable(found);
        }
    }

    private static void generateSingle() throws InvalidInputException, IOException {
        Participant p = MANAGER.findByEmail(prompt("Participant e-mail: "));
        List<CertificateRenderer> renderers = chooseRenderers();
        for (CertificateRenderer r : renderers) {
            File f = MANAGER.generateOne(p, r, OUTPUT_DIR);
            System.out.println("Certificate created (" + r.getFormatName() + "): " + f.getPath());
        }
    }

    private static void generateAll() throws InvalidInputException, IOException {
        if (MANAGER.getEvent() == null) {
            throw new InvalidInputException("Event details are not set. Use option 1 first.");
        }
        if (MANAGER.getParticipantCount() == 0) {
            throw new InvalidInputException("No participants registered.");
        }
        List<CertificateRenderer> renderers = chooseRenderers();
        int created = MANAGER.generateAll(renderers, OUTPUT_DIR);
        System.out.println(created + " certificate file(s) created for "
                + MANAGER.getParticipantCount() + " participant(s) in folder '"
                + OUTPUT_DIR.getPath() + "'.");
        File report = new File(OUTPUT_DIR, CertificateManager.REPORT_NAME);
        System.out.println("Summary report: " + report.getPath());
    }

    private static void removeParticipant() throws InvalidInputException {
        MANAGER.removeParticipant(prompt("E-mail of participant to remove: "));
        System.out.println("Participant removed.");
    }

    // ------------------------------------------------------------- helpers
    private static List<CertificateRenderer> chooseRenderers() throws InvalidInputException {
        System.out.println("Certificate format: 1) PNG image   2) Text file   3) Both");
        switch (prompt("Choose format: ").trim()) {
            case "1": return List.of(new ImageCertificateRenderer());
            case "2": return List.of(new TextCertificateRenderer());
            case "3": return List.of(new ImageCertificateRenderer(), new TextCertificateRenderer());
            default:  throw new InvalidInputException("Invalid format choice. Enter 1, 2 or 3.");
        }
    }

    private static void printTable(List<Participant> list) {
        if (list.isEmpty()) {
            System.out.println("No participants registered.");
            return;
        }
        System.out.printf("%-10s %-22s %-28s %-22s %s%n",
                "CERT ID", "NAME", "EMAIL", "DEPARTMENT", "ISSUED");
        System.out.println("-".repeat(94));
        for (Participant p : list) {
            System.out.printf("%-10s %-22.22s %-28.28s %-22.22s %s%n", p.getCertificateId(), p.getName(),
                    p.getEmail(), p.getDepartment(), p.isGenerated() ? "Yes" : "No");
        }
        System.out.println("Total: " + list.size());
    }
}
