package bank.service;

import bank.entity.User;
import bank.operations.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UserService {

    private final UserRepository userrepo = new UserRepository();

    public void printUser() { userrepo.printUsers(); }

    public String login(String username, String password) throws SQLException {
       return userrepo.login( username,password);
    }

    public boolean addNewCustomer(String username, String password, String contact,Double amt,String email) throws SQLException {
        return userrepo.addNewCustomer(username,password,contact,amt,email);
    }

    public Double checkAccountBalance(String username)  throws SQLException{
        return userrepo.checkAccountBalance(username);
    }

    public User getUser(String username) {
        return userrepo.getUser(username);
    }

    public boolean transferAmount(String username, String receivername, Double amt) {
        return userrepo.transferAmount(username,receivername,amt);
    }

    public void printTransaction(String username) throws SQLException {
      userrepo.printTransaction(username);
    }

    public void raiseChequebook(String username) {
       userrepo.raiseChequebook(username);
    }

    public List<String> getAllchequebookrequest() throws  SQLException{
        return userrepo.getAllchequebookrequest();
    }

    public boolean approvechequebook(String username) throws SQLException {
        return userrepo.approvechequebook(username);
    }
}
