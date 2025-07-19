package bank.main;

import bank.entity.User;
import bank.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final Scanner scan = new Scanner(System.in);
    static Main main = new Main();
    static UserService userService = new UserService();

    public static void main(String[] args) {

        while(true) {
            System.out.println("Enter your username:");
            String username = scan.next();
            System.out.println("Enter your password:");
            String password = scan.next();

            User user = userService.login(username,password);
            if(user != null && user.getRole().equals("admin")) {
                System.out.println("You are logged into the system successfully");
                main.initAdmin();
            } else if (user != null && user.getRole().equals("user")) {
                System.out.println("You are logged into the system successfully");
                main.initCustomer(user);
            } else {
                System.out.println("Login failed !!!");
            }
        }


    }

    private  void initAdmin() {
        boolean flag = true;
        String username = "";

        while(flag) {
            System.out.println("1. Logout");
            System.out.println("2. Create a customer account");
            System.out.println("3. See customer's all Transactions");
            System.out.println("4. Check account balance");
            System.out.println("5. Approve cheque book request ");

            int opt = scan.nextInt();

            switch (opt) {
                case 1:
                    flag = false;
                    System.out.println("You have successfully logged out...");
                    break;
                case 2:
                    main.addNewCustomer();
                    break;
                case 3:
                    System.out.println("Enter username:");
                    username = scan.next();
                    main.printTransaction(username);
                    break;
                case 4:
                    System.out.println("Enter username:");
                    username = scan.next();
                    Double res = main.checkAccountBalance(username);
                    System.out.println(username+"'s balance is  "+res);
                    break;
                case 5:
                  List<String> listofusers = getAllchequebookrequest();
                    System.out.println("Select the username from the below");
                    System.out.println(listofusers);

                     username = scan.next();
                     approvechequebook(username);

                    System.out.println("Cheque book request is approved..");

                    break;
                default:
                    System.out.println("Wrong choice");
            }
        }

    }
    private void approvechequebook(String username) {
        userService.approvechequebook(username);
    }

    private List<String> getAllchequebookrequest(){
       return userService.getAllchequebookrequest();
    }

    private void addNewCustomer() {
        System.out.println("Enter username:");
        String username = scan.next();
        System.out.println("Enter password:");
        String password = scan.next();
        System.out.println("Enter contact number:");
        String contact = scan.next();

        boolean res = userService.addNewCustomer(username,password,contact);

        if(res) {
            System.out.println("Customer account is created...");
        } else {
            System.out.println("Customer account is creation failed !!!");
        }

    }

    private void initCustomer(User user) {
        boolean flag = true;

        while(flag) {
            System.out.println("1. Logout");
            System.out.println("2. Check account balance:");
            System.out.println("3. Amount transfer:");
            System.out.println("4. Transaction History:");
            System.out.println("5. Raise chequebook rquest:");

            int opt = scan.nextInt();

            switch (opt) {
                case 1:
                    flag = false;
                    System.out.println("You have successfully logged out...");
                    break;
                case 2:
                    Double balance = main.checkAccountBalance(user.getUsername());
                    if(balance != null) {
                        System.out.println("Your Bank Balance is : "+balance);
                    } else {
                        System.out.println("Check your username");
                    }
                    break;
                case 3:
                    main.AmountTransfer(user);
                    break;
                case 4:
                    main.printTransaction(user.getUsername());
                    break;
                case 5:
                    String username = user.getUsername();
                    Map<String, Boolean> map = getChequebookrequest();

                    if(map.containsKey(username) && map.get(username)) {
                        System.out.println("You have already raised a request and it is already approved.");
                    } else if(map.containsKey(username) && !map.get(username)) {
                        System.out.println("You have already raised a request and it is pending for approval !!");
                    } else {
                        main.raiseChequebook(username);
                        System.out.println("Request raised successfully...");
                    }
                    break;
                default:
                    System.out.println("Wrong choice");
            }
        }

    }

    private Map<String, Boolean> getChequebookrequest() {
      return userService.getChequebookrequest();
    }

    private void raiseChequebook(String username) {
        userService.raiseChequebook(username);
    }


    private void printTransaction(String username) {
        userService.printTransaction(username);
    }


    private void AmountTransfer(User userDetails) {
        System.out.println("Enter receiver account username:");
        String receiver = scan.next();

        User user = getUser(receiver);
        if(user != null) {
            System.out.println("Please enter amount to transfer:");
            Double amt = scan.nextDouble();

            Double userbalance = checkAccountBalance(userDetails.getUsername());
            if(userbalance >= amt) {
                   boolean res = userService.transferAmount(userDetails.getUsername(),receiver,amt);
                   if(res) {
                       System.out.println("Amount Transferred successfully.");
                   } else {
                       System.out.println("Transfer failed !!!");
                   }
            } else {
                System.out.println("Your balance is insufficient: "+userbalance);
            }

        } else {
            System.out.println("Please enter a valid username.");
        }
    }

    private User getUser(String username) {
        return userService.getUser(username);
    }

    private Double checkAccountBalance(String username) {
        return userService.checkAccountBalance(username);
    }


}
