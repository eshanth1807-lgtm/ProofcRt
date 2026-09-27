/**
 * Represents one participant of a technical event / workshop.
 * All fields are private (encapsulation) and exposed only through getters.
 */
public class Participant {
    private final String name;
    private final String email;
    private final String department;
    private final String certificateId;   // unique ID, e.g. CERT-0001
    private boolean generated;             // true once a certificate has been produced

    public Participant(String name, String email, String department, String certificateId) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.certificateId = certificateId;
        this.generated = false;
    }

    public String getName()          { return name; }
    public String getEmail()         { return email; }
    public String getDepartment()    { return department; }
    public String getCertificateId() { return certificateId; }
    public boolean isGenerated()     { return generated; }
    public void markGenerated()      { this.generated = true; }
}
