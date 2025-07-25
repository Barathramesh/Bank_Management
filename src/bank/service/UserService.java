package bank.service;

import bank.entity.User;
import bank.operations.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UserService {

    private final UserRepository userrepo = new UserRepository();

    public void printUser() { userrepo.printUsers(); }

    public User login(String username, String password) {
       return userrepo.login( username,password);
    }

    public boolean addNewCustomer(String username, String password, String contact,Double amt) throws SQLException {
        return userrepo.addNewCustomer(username,password,contact,amt);
    }

    public Double checkAccountBalance(String username) {
        return userrepo.checkAccountBalance(username);
    }

    public User getUser(String username) {
        return userrepo.getUser(username);
    }

    public boolean transferAmount(String username, String receivername, Double amt) {
        return userrepo.transferAmount(username,receivername,amt);
    }

    public void printTransaction(String username) {
      userrepo.printTransaction(username);
    }

    public void raiseChequebook(String username) {
       userrepo.raiseChequebook(username);
    }

    public Map<String, Boolean> getChequebookrequest() {
        return userrepo.getChequebookrequest();
    }

    public List<String> getAllchequebookrequest(){
        return userrepo.getAllchequebookrequest();
    }

    public void approvechequebook(String username) {
        userrepo.approvechequebook(username);
    }
}
