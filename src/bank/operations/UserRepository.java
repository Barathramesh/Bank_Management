package bank.operations;

import bank.entity.Transaction;
import bank.entity.User;

import java.time.LocalDate;
import java.util.*;

public class UserRepository {

    private static final HashSet<User> users = new HashSet<>();
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final HashMap<String, Boolean> chequebookrequest = new HashMap<>();

    static {
        User user1 = new User("admin","admin", "123456780","admin",0.0);
        User user2 = new User("barath","12345", "9843277225","user",1000.0);
        User user3 = new User("kavin","12346", "9843177225","user",5000.0);
        User user4 = new User("deepak","12347", "9843177225","user",5000.0);

        users.add(user1);
        users.add(user2);
        users.add(user3);
        users.add(user4);

    }

    public void approvechequebook(String username) {
       if(chequebookrequest.containsKey(username)) {
           chequebookrequest.put(username,true);
       }
    }

    public List<String> getAllchequebookrequest(){
       List<String> username = new ArrayList<>();

       for(Map.Entry<String,Boolean> entry : chequebookrequest.entrySet()) {
           if(!entry.getValue()) {
               username.add(entry.getKey());
           }
       }
       return username;
    }

    public Map<String, Boolean> getChequebookrequest() {
         return chequebookrequest;
    }

    public void raiseChequebook(String username) {
       chequebookrequest.put(username,false);
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
        System.out.println(transaction);

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
        System.out.println(transaction);

        return users.add(user);
    }

    public void printTransaction(String username) {
      List<Transaction> res =  transactions.stream()
              .filter(transaction -> transaction.getTransactionUsername().equals(username)).toList();
        System.out.println("Date \t\tUsername \t Amount \t Type \t InitialBalance \t FinalBalance");
        System.out.println("------------------------------------------------------------------------------");
      for(Transaction t : res) {
          System.out.println(
                  t.getTransactionDate()+"\t"
                + t.getTransactionUsername()+"\t\t"
                + t.getTransactionAmount()+"\t\t"
                + t.getTransactionType()+"\t\t"
                + t.getInitialBalance()+"\t\t\t\t"
                + t.getFinalBalance()
          );
      }
        System.out.println("-------------------------------------------------------------------------------");
    }

    public User getUser(String username) {
       List<User> res =  users.stream().filter(user->user.getUsername().equals(username)).toList();
        if(!res.isEmpty()) {
            return res.getFirst();
        } else {
            return null;
        }
    }

    public Double checkAccountBalance(String username) {
       List<User> res = users.stream().filter(user -> user.getUsername().equals(username)).toList();

       if(!res.isEmpty()) {
           return res.getFirst().getAccountBalance();
       } else {
           return null;
       }
    }

    public void printUsers() { System.out.println(users); }

    public User login (String username, String password) {
       List<User> finalList =  users.stream()
               .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
               .toList();

       if(!finalList.isEmpty()) {
           return finalList.getFirst();
       } else {
           return null;
       }
    }

    public boolean addNewCustomer(String username, String password, String contact) {
        User user = new User(username,password,contact,"user",500.0);
        return users.add(user);
    }

}
