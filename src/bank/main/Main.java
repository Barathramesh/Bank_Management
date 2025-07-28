package bank.main;

import bank.entity.User;
import bank.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.sql.SQLException;

public class Main {

    private static final Scanner scan = new Scanner(System.in);
    static Main main = new Main();
    static UserService userService = new UserService();

    public static void main(String[] args) throws SQLException{

        while(true) {
            System.out.println("Enter your username:");
            String username = scan.next();
            System.out.println("Enter your password:");
            String password = scan.next();

            User role = userService.login(username,password);

            if(role != null && role.getRole().equals("admin")) {
                System.out.println("You are logged into the system successfully");
                main.initAdmin();
            } else if (role != null && role.getRole().equals("user")) {
                System.out.println("You are logged into the system successfully");
                main.initCustomer(role);
            } else {
                System.out.println("Login failed !!!");
            }
        }


    }

    private  void initAdmin()  throws SQLException{
        boolean flag = true;
        String username = "";

        while(flag) {
            System.out.println("1. Create a customer account");
            System.out.println("2. See customer's all Transactions");
            System.out.println("3. Check account balance");
            System.out.println("4. Approve cheque book request ");
            System.out.println("5. Logout");

            int opt = scan.nextInt();

            switch (opt) {
                case 1:
                    main.addNewCustomer();
                    break;
                case 2:
                    System.out.println("Enter username:");
                    username = scan.next();
                    main.printTransaction(username);
                    break;
                case 3:
                    System.out.println("Enter username:");
                    username = scan.next();
                    Double res = main.checkAccountBalance(username);
                    if(res != null) {
                        System.out.println(username + "'s balance is  " + res);
                    } else {
                        System.out.println("Invalid username!!!");
                    }
                    break;
                case 4:
                    List<String> listofusers = getAllchequebookrequest();
                    if(!listofusers.isEmpty()) {
                        System.out.println("Select the username from the below");
                        System.out.println(listofusers);
                        username = scan.next();
                        if (approvechequebook(username)) {
                            System.out.println("Cheque book request is approved...");
                        } else {
                            System.out.println("Some techincal error!!!");
                        }
                    } else {
                        System.out.println("There is no more applicants for chequebook..");
                    }
                    break;
                case 5:
                    flag = false;
                    System.out.println("You have successfully logged out...");
                    break;
                default:
                    System.out.println("Wrong choice");
            }
        }

    }

    private void addNewCustomer() {
        System.out.println("Enter username:");
        String username = scan.next();
        System.out.println("Enter password:");
        String password = scan.next();
        System.out.println("Enter contact number:");
        String contact = scan.next();
        System.out.println("Enter email address:");
        String email = scan.next();
        System.out.println("Enter initial amount to deposit:");
        Double amt = scan.nextDouble();

        try {
            boolean res = userService.addNewCustomer(username, password, contact, amt,email);
            if (res) {
                System.out.println("Customer account is created...");
            } else {
                System.out.println("Customer account creation failed !!!");
            }
        } catch (SQLException e) {
            System.out.println("Error while creating customer account: " + e.getMessage());
        }

    }

    private boolean approvechequebook(String username) throws SQLException {
        return userService.approvechequebook(username);
    }

    private List<String> getAllchequebookrequest() throws SQLException{
        return userService.getAllchequebookrequest();
    }

    private void initCustomer(User user) throws SQLException{
        String username = user.getUsername();
        boolean flag = true;

        while(flag) {
            System.out.println("1. Check account balance:");
            System.out.println("2. Amount transfer:");
            System.out.println("3. Transaction History:");
            System.out.println("4. Raise chequebook rquest:");
            System.out.println("5. Logout");

            int opt = scan.nextInt();

            switch (opt) {
                case 1:
                    Double balance = main.checkAccountBalance(username);
                    if(balance != null) {
                        System.out.println("Your Bank Balance is : "+balance);
                    } else {
                        System.out.println("Check your username");
                    }
                    break;
                case 2:
                    main.AmountTransfer(user);
                    break;
                case 3:
                    main.printTransaction(user.getUsername());
                    break;
                case 4:
                    String userS = user.getUsername();
                    Map<String, String> map = userService.getChequebookrequest(userS);

                    if (map.containsKey(userS)) {
                        String status = map.get(userS);
                        if ("Approved".equalsIgnoreCase(status)) {
                            System.out.println("You have already raised a request and it is already approved.");
                        } else {
                            System.out.println("You have already raised a request and it is pending for approval !!");
                        }
                    } else {
                        main.raiseChequebook(userS);
                    }
                     break;
                case 5:
                    flag = false;
                    System.out.println("You have successfully logged out...");
                    break;
                default:
                    System.out.println("Wrong choice");
            }
        }

    }

    private void raiseChequebook(String username) {
        userService.raiseChequebook(username);
    }

    private void printTransaction(String username) throws SQLException {
        userService.printTransaction(username);
    }

    private void AmountTransfer(User userDetails) {
        try {
            System.out.println("Enter receiver account username:");
            String receiver = scan.next();

            User receiverUser = getUser(receiver);
            if(receiverUser != null) {
                System.out.println("Please enter amount to transfer:");
                Double amt = scan.nextDouble();

                Double userbalance = checkAccountBalance(userDetails.getUsername());
                if(userbalance >= amt) {
                    boolean res = userService.transferAmount(userDetails.getUsername(), receiver, amt);
                    System.out.println(res ? "Amount Transferred successfully." : "Transfer failed !!!");
                } else {
                    System.out.println("Your balance is insufficient: " + userbalance);
                }
            } else {
                System.out.println("Please enter a valid username.");
            }
        } catch (SQLException e) {
            System.out.println("DB Error: " + e.getMessage());
        }
    }


    private User getUser(String username) throws  SQLException{
        return userService.getUser(username);
    }

    private Double checkAccountBalance(String username) throws  SQLException {
        return userService.checkAccountBalance(username);
    }

}
