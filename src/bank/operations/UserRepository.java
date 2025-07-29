package bank.operations;

import bank.entity.Transaction;
import bank.entity.User;
import bank.service.DbConnection;

import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;
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

    public void raiseChequebook(String username) throws SQLException {
            String query = "SELECT status FROM chequebook_requests WHERE username = ?";

            Connection con = DbConnection.getConnection();
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");

                if ("Pending".equalsIgnoreCase(status)) {
                    System.out.println("You have already raised a request. It is pending for approval.");
                } else if ("Approved".equalsIgnoreCase(status)) {
                    System.out.println("You have already raised a request and it is approved.");
                }
            } else {
                String insertQuery = "INSERT INTO chequebook_requests (username, chequebook_request) VALUES (?, ?)";
                PreparedStatement insertPst = con.prepareStatement(insertQuery);
                insertPst.setString(1, username);
                insertPst.setBoolean(2, true);
                insertPst.executeUpdate();

                System.out.println("Request raised successfully...");
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
        String query = "INSERT INTO transactions (transaction_date, sender_username, amount, transaction_type, " +
                "initial_balance, final_balance, receiver_username) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection con = DbConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(query);
        pst.setTimestamp(1, java.sql.Timestamp.valueOf(txn.getTransactionDate()));
        pst.setString(2, txn.getTransactionUsername());
        pst.setDouble(3, txn.getTransactionAmount());
        pst.setString(4, txn.getTransactionType());
        pst.setDouble(5, txn.getInitialBalance());
        pst.setDouble(6, txn.getFinalBalance());
        pst.setString(7, txn.getTransactionPerformedBy());
        return pst.executeUpdate() > 0;
    }

    private boolean debit(String username, Double amt, String receiver_name) throws SQLException {
        User user = getUser(username);
        if (user == null) return false;

        Double accountBalance = user.getAccountBalance();
        double finalBalance = accountBalance - amt;

        boolean updated = updateBalance(username, finalBalance);
        if (!updated) return false;

        Transaction txn = new Transaction(
                LocalDateTime.now(),
                username,
                amt,
                "Debited",
                accountBalance,
                finalBalance,
                receiver_name
        );

        return insertTransaction(txn);
    }


    private boolean credit(String receiver_name, Double amt, String sender_name) throws SQLException {
        User user = getUser(receiver_name);
        if (user == null) return false;

        Double accountBalance = user.getAccountBalance();
        double finalBalance = accountBalance + amt;

        boolean updated = updateBalance(receiver_name, finalBalance);
        if (!updated) return false;

        Transaction txn = new Transaction(
                LocalDateTime.now(),
                receiver_name,
                amt,
                "Credited",
                accountBalance,
                finalBalance,
                sender_name
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
                System.out.println("Date \t\t\t\t\tSender name \tAmount \t\t Type  \t\tReceiver name \tInitialBalance \t FinalBalance");
                System.out.println("-------------------------------------------------------------------------------------------------------------");
                hasData = true;
            }

            System.out.println(
                    rs.getString(8) + "\t\t"
                    + rs.getString(2) + "\t\t\t"
                    + rs.getDouble(4) + "\t\t"
                    + rs.getString(7) + "\t\t"
                    + rs.getString(3)+ "\t\t\t"
                    + rs.getDouble(5) + "\t\t\t "
                    + rs.getDouble(6)
            );
        }

        if (!hasData) {
            System.out.println("No transactions found for user Or invalid username");
        } else {
            System.out.println("-------------------------------------------------------------------------------------------------------------");
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
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        sb.append(random.nextInt(9) + 1);

        for (int i = 0; i < 15; i++) {
            sb.append(random.nextInt(10));
        }

        String accountNumber = sb.toString();
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

    public void PrintUserDetails() throws SQLException {
        String query = "Select * from users ORDER BY created_at DESC Limit 1";
        Connection con = DbConnection.getConnection();
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);

        while(rs.next()) {
            System.out.println();
            System.out.println("Account Number: "+rs.getString(2));
            System.out.println("Username: "+rs.getString(3));
            System.out.println("Password: "+rs.getString(4));
            System.out.println("Account Balance: "+rs.getString(7));
            System.out.println("Email Id: "+rs.getString(6));
            System.out.println("Contact Number: "+rs.getString(5));
            System.out.println();
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

   public  boolean checkPassword(String username, String password) throws SQLException {
        String query = "Select * from users where username = ?";
        Connection con = DbConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1,username);
        ResultSet rs = pst.executeQuery();

        while(rs.next()) {
            String currentPassword = rs.getString(4);
            if(currentPassword.equals(password)) {
                return true;
            }
        }
       return false;
   }

    public boolean updatePassword(String username, String password) throws SQLException {
       String query = "Update users SET password = ? where username = ?";
        Connection con = DbConnection.getConnection();
        PreparedStatement pst = con.prepareStatement(query);
        pst.setString(1,password);
        pst.setString(2,username);
        int rows = pst.executeUpdate();
        return rows > 0;
    }

}
