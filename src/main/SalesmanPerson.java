// base class for all people in the system
public class SalesmanPerson {
    protected String id;
    protected String name;
    protected String phone;

    // create a new person
    public SalesmanPerson(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    // get person's profile info
    public String getProfile() {
        return "ID: " + id + "\nName: " + name + "\nPhone: " + phone;
    }
} 