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

    public boolean approvechequebook(String username) throws SQLException {
        String query = "UPDATE chequebook_requests SET status = ?, chequebook_request = ?, approved_by = ?, approved_date = CURRENT_TIMESTAMP WHERE username = ?";

        Connection con = DbConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(query);

        pst.setString(1, "approved");
        pst.setBoolean(2, true);
        pst.setString(3,"admin");
        pst.setString(4, username);

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

    public Map<String, String> getChequebookrequest(String username) throws SQLException {
        Map<String, String> chequeStatus = new HashMap<>();
        String query = "SELECT chequebook_request, status FROM chequebook_requests WHERE username = ?";
        Connection con = DbConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            boolean requestRaised = rs.getBoolean("chequebook_request");
            String status = rs.getString("status");
            if (requestRaised) {
                chequeStatus.put(username, status);
            }
        }
        return chequeStatus;
    }




    public boolean raiseChequebook(String username) {
        try {
            Connection con = DbConnection.getConnection();

            String checkQuery = "SELECT status FROM chequebook_requests WHERE username = ?";
            PreparedStatement checkPst = con.prepareStatement(checkQuery);
            checkPst.setString(1, username);
            ResultSet rs = checkPst.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");

                if ("Pending".equalsIgnoreCase(status)) {
                    System.out.println("You have already raised a request. It is pending for approval.");
                    return false;
                } else if ("Approved".equalsIgnoreCase(status)) {
                    System.out.println("You have already raised a request and it is approved.");
                    return false;
                } else {
                    return false;
                }

            } else {
                String insertQuery = "INSERT INTO chequebook_requests (username, chequebook_request) VALUES (?, ?)";
                PreparedStatement insertPst = con.prepareStatement(insertQuery);
                insertPst.setString(1, username);
                insertPst.setBoolean(2, true);
                insertPst.executeUpdate();

                System.out.println("Request raised successfully...");
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean transferAmount(String sender, String receiver, Double amt) throws SQLException {
        boolean isDebit = debit(sender, amt, receiver);
        boolean isCredit = credit(receiver, amt, sender);
        return isDebit && isCredit;
    }

    public User getUser(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        Connection con = DbConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, username);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("contact_number"),
                    rs.getString("role"),
                    rs.getDouble("account_balance"),
                    rs.getString("account_number"),
                    rs.getString("email")
            );
        }
        return null;
    }

    private boolean updateBalance(String username, double newBalance) throws SQLException {
        String query = "UPDATE users SET account_balance = ? WHERE username = ?";
        Connection con = DbConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(query);
        pst.setDouble(1, newBalance);
        pst.setString(2, username);
        int rows = pst.executeUpdate();
        return rows > 0;
    }

    private boolean insertTransaction(Transaction txn) throws SQLException {
        String query = "INSERT INTO transactions (transaction_date, sender_username, amount, transaction_type, initial_balance, final_balance, receiver_username) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection con = DbConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(query);
        pst.setDate(1, java.sql.Date.valueOf(txn.getTransactionDate()));
        pst.setString(2, txn.getTransactionUsername());
        pst.setDouble(3, txn.getTransactionAmount());
        pst.setString(4, txn.getTransactionType());
        pst.setDouble(5, txn.getInitialBalance());
        pst.setDouble(6, txn.getFinalBalance());
        pst.setString(7, txn.getTransactionPerformedBy());
        return pst.executeUpdate() > 0;
    }

    private boolean debit(String username, Double amt, String receivername) throws SQLException {
        User user = getUser(username);
        if (user == null) return false;

        Double accountBalance = user.getAccountBalance();
        double finalBalance = accountBalance - amt;

        boolean updated = updateBalance(username, finalBalance);
        if (!updated) return false;

        Transaction txn = new Transaction(
                LocalDate.now(),
                username,
                amt,
                "Debit",
                accountBalance,
                finalBalance,
                receivername
        );

        return insertTransaction(txn);
    }


    private boolean credit(String receivername, Double amt, String sendername) throws SQLException {
        User user = getUser(receivername);
        if (user == null) return false;

        Double accountBalance = user.getAccountBalance();
        double finalBalance = accountBalance + amt;

        boolean updated = updateBalance(receivername, finalBalance);
        if (!updated) return false;

        Transaction txn = new Transaction(
                LocalDate.now(),
                receivername,
                amt,
                "Credit",
                accountBalance,
                finalBalance,
                sendername
        );

        return insertTransaction(txn);
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

    public User login (String username, String password) throws SQLException {
        String query = "select * from users where username = ? and password = ?";
        Connection con = DbConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1, username);
        pst.setString(2, password);
        ResultSet rs = pst.executeQuery();
        if(rs.next()) {

           String AccountNumber = rs.getString("account_number");
            String Username = rs.getString("username");
           String Password = rs.getString("password");
            String ContactNum = rs.getString("contact_number");
           String Email = rs.getString("email");
            Double AccountBalance = rs.getDouble("account_balance");
            String Role = rs.getString("role");
            return new User(Username, Password, ContactNum,Role,AccountBalance,AccountNumber,Email);
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
