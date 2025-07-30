package bank.service;

import bank.entity.User;
import bank.operations.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class UserService {

    private final UserRepository userrepository = new UserRepository();

    public User login(String username, String password) throws SQLException {
       return userrepository.login(username,password);
    }

    public boolean addNewCustomer(String username, String password, String contact,Double amt,String email) throws SQLException {
        return userrepository.addNewCustomer(username,password,contact,amt,email);
    }

    public void PrintUserDetails() throws SQLException {
        userrepository.PrintUserDetails();
    }

    public Double checkAccountBalance(String username)  throws SQLException{
        return userrepository.checkAccountBalance(username);
    }

    public User getUser(String username) throws SQLException {
        return userrepository.getUser(username);
    }

    public boolean transferAmount(String username, String receiver_name, Double amt) throws SQLException {
        return userrepository.transferAmount(username,receiver_name,amt);
    }

    public void printTransaction(String username) throws SQLException {
      userrepository.printTransaction(username);
    }

    public void raiseChequebook(String username) throws SQLException {
        userrepository.raiseChequebook(username);
    }

    public List<String> getAllchequebookrequest() throws  SQLException{
        return userrepository.getAllchequebookrequest();
    }

    public boolean approvechequebook(String username) throws SQLException {
        return userrepository.approvechequebook(username);
    }

    public Map<String, String> getChequebookrequest(String username) throws SQLException {
        return userrepository.getChequebookrequest(username);
    }
    public boolean checkPassword(String username, String password) throws SQLException {
        return userrepository.checkPassword(username, password);
    }

    public boolean updatePassword(String username, String password) throws SQLException {
        return userrepository.updatePassword(username, password);
    }

    public boolean userExistOrNot(String username, String accountNumber) throws SQLException {
        return userrepository.userExistOrNot(username,accountNumber);
    }

    public boolean deleteUserDetails(String username, String accountNumber) throws SQLException {
        return userrepository.deleteUserDetails(username,accountNumber);
    }
}
