package bank.entity;

import java.util.Objects;

public class User {

    private String username;
    private String password;
    private String contactNum;
    private String role;
    private Double accountBalance;
    private String accountNumber;
    private String email;

    public User(String username, String password, String contactNum, String role, Double accountBalance, String accountNumber,String email) {
        this.username = username;
        this.password = password;
        this.contactNum = contactNum;
        this.role = role;
        this.accountBalance = accountBalance;
        this.accountNumber = accountNumber;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public String getRole() {
        return role;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }


    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", contactNum='" + contactNum + '\'' +
                ", role='" + role + '\'' +
                ", accountBalance=" + accountBalance +
                ", accountNumber='" + accountNumber + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(contactNum, user.contactNum) && Objects.equals(accountNumber, user.accountNumber);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(username);
        result = 31 * result + Objects.hashCode(password);
        result = 31 * result + Objects.hashCode(contactNum);
        result = 31 * result + Objects.hashCode(accountNumber);
        return result;
    }
}
