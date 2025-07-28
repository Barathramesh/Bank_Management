package bank.operations;

import bank.entity.Transaction;
import bank.entity.User;
import bank.service.DbConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UserRepository {

    private static final HashSet<User> users = new HashSet<>();
    private static final ArrayList<Transaction> transactions = new ArrayList<>();


    static {
        User user1 = new User("admin","admin", "123456780","admin",0.0,"0.0","admin@gmail.com");
        User user2 = new User("barath","12345", "9843277225","user",1000.0,"AC2507192013442169", "bar@gmail.com");
        User user3 = new User("kavin","12346", "9843177225","user",5000.0,"AC2507192014383706", "ka@gmail.com");
        User user4 = new User("deepak","12347", "9843177225","user",5000.0,"AC2507192014499168", "de@gmail.com");

        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);

    }

    public boolean approvechequebook (String username) throws SQLException {
        String query = "UPDATE chequebook_requests SET status = ? WHERE username = ?";
        Connection con = DbConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(query);

        pst.setString(1,"Approved");
        pst.setString(2,username);

        int rowsAffected = pst.executeUpdate();
        return rowsAffected > 0;
    }

    public List<String> getAllchequebookrequest() throws SQLException {

      List<String> username = new ArrayList<>();
      String query = "Select username from chequebook_requests where status = 'pending'";
      Connection con = DbConnection.getConnection();
      Statement st = con.createStatement();
      ResultSet rs = st.executeQuery(query);

      while(rs.next()) {
          username.add(rs.getString(1));
      }
      return username;
    }


    public void raiseChequebook(String username) {
      // chequebookrequest.put(username,false);
    }

    public boolean transferAmount(String username, String receivername, Double amt) {

        boolean isDebit = debit(username, amt, receivername);
        boolean isCredit = credit(receivername, amt, username);

        return isDebit && isCredit;
    }

    private boolean debit(String username, Double amt,String receivername) {
        User user = getUser(username);
        Double accountBalance = user.getAccountBalance();

        users.remove(user);

        Double finalBalance = accountBalance - amt;
        user.setAccountBalance(finalBalance);

        Transaction transaction = new Transaction(
                LocalDate.now(),
                username,
                amt,
                "Debit",
                accountBalance,
                finalBalance,
                receivername
        );

        transactions.add(transaction);


        return users.add(user);
    }

    private boolean credit(String receivername, Double amt, String username) {
        User user = getUser(receivername);
        Double accountBalance = user.getAccountBalance();

        users.remove(user);

        Double finalBalance = accountBalance + amt;
        user.setAccountBalance(finalBalance);

        Transaction transaction = new Transaction(
                LocalDate.now(),
                receivername,
                amt,
                "Credit",
                accountBalance,
                finalBalance,
                username
        );

        transactions.add(transaction);


        return users.add(user);
    }

    public void printTransaction(String username) throws SQLException {
        String query = "SELECT * FROM transactions WHERE sender_username = ? OR receiver_username = ?";

        Connection con = DbConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, username);
        pst.setString(2, username);
        ResultSet rs = pst.executeQuery();

        boolean hasData = false;

        while(rs.next()) {
            if (!hasData) {
                System.out.println("Date \t\tUsername \t Amount \t Type \t InitialBalance \t FinalBalance");
                System.out.println("------------------------------------------------------------------------------");
                hasData = true;
            }

            System.out.println(
                    rs.getString(8) + "\t"
                    + rs.getString(2) + "\t\t"
                    + rs.getDouble(4) + "\t\t"
                    + rs.getString(7) + "\t\t"
                    + rs.getDouble(5) + "\t\t\t\t"
                    + rs.getDouble(6)
            );
        }

        if (!hasData) {
            System.out.println("No transactions found for user Or invalid username");
        } else {
            System.out.println("-------------------------------------------------------------------------------");
        }
    }


    public User getUser(String username) {
       List<User> res =  users.stream().filter(user->user.getUsername().equals(username)).toList();
        if(!res.isEmpty()) {
            return res.getFirst();
        } else {
            return null;
        }
    }

    public Double checkAccountBalance(String username) throws SQLException {
       String query = "Select account_balance from users where username = ?";
       Connection con = DbConnection.getConnection();
       PreparedStatement pst = con.prepareStatement(query);
       pst.setString(1,username);
       ResultSet rs = pst.executeQuery();

       if(rs.next()) {
           return rs.getDouble(1);
       } else {
           return null;
       }
    }

    public void printUsers() { System.out.println(users); }

    public String login (String username, String password) throws SQLException {
        String query = "select * from users where username = ? and password = ?";
        Connection con = DbConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, username);
        pst.setString(2, password);
        ResultSet rs = pst.executeQuery();
        if(rs.next()) {
          return rs.getString(10);
        } else {
            return null;
        }
    }

    public boolean addNewCustomer(String username, String password, String contact,Double amt,String email) throws SQLException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(dtf);
        int random = new Random().nextInt(9000) + 1000;
        String accountNumber = "AC"+timestamp+random;
        String role = "user";

        User user = new User(username,password,contact,role,amt,accountNumber,email);
        String query = "INSERT INTO users (account_number, username, password, contact_number, email, account_balance,role) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            int rowsAffected = getRowsAffected(query, user);
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int getRowsAffected(String query, User user) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, user.getAccountNumber());
        pst.setString(2, user.getUsername());
        pst.setString(3, user.getPassword());
        pst.setString(4, user.getContactNum());
        pst.setString(5, user.getEmail());
        pst.setDouble(6, user.getAccountBalance());
        pst.setString(7, user.getRole());
        return pst.executeUpdate();
    }

}
