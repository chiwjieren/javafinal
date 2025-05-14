public class User {
    String username;
    String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    
    protected static boolean cleanInput(String input) {
        if (input.trim().isEmpty()) return false;
        return !(input.contains(",") || input.contains("\"") || input.contains("\n") || input.contains("\r"));
    }


}
