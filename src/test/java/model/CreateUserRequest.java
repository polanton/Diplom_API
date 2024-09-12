package model;

public class CreateUserRequest {
    private String email;
    private String password;
    private String name;

    public String getEmail() {
        return email;
    }

    public CreateUserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public CreateUserRequest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public CreateUserRequest() {
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

