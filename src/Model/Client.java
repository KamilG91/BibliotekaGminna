package Model;

public class Client {
    private int clientId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String telephoneNumber;

    public Client() {

    }
    public Client(int id, String firstName, String lastName, String emailAddress, String telephoneNumber) {
        this.clientId = id;
        this.firstName = firstName;
        this.lastName = lastName;
        if (!emailAddress.contains("@")) {
            throw new IllegalArgumentException("Niepoprawny email");
        }
        this.emailAddress = emailAddress;
        if (telephoneNumber.length() != 9) {
            throw new IllegalArgumentException("Numer telefonu nie ma 9 cyfr");
        }
        this.telephoneNumber = telephoneNumber;

    }

    public int getId() {
        return clientId;
    }
    public void setClientId(int id) {
        clientId = id;
    }
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    @Override
    public String toString() {
        return clientId +". " + firstName + " " + lastName + ". Email - " + emailAddress + ", Phone number - " + telephoneNumber;
    }
}
