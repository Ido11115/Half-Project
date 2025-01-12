public class Subscriber {
    private int id;
    private String name;
    private String phone;
    private String email;
    private int history;   

    public Subscriber(int id, String name, String phone, String email, int history) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.history = history;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public int getHistory() {  
        return history;
    }
}
