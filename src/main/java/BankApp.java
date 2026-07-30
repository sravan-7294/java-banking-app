import java.sql.SQLException;
import java.util.*;

public class BankApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BankService bank = new BankService();
        long loggedInAcc = -1;

        while (true) {
            if (loggedInAcc == -1) {
                System.out.println("\n1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");
                int ch = sc.nextInt();
                sc.nextLine();

                try {
                    switch (ch) {
                        case 1:
                            System.out.print("Name: ");
                            String name = sc.nextLine();
                            System.out.print("Phone: ");
                            String phone = sc.nextLine();
                            System.out.print("Password: ");
                            String pwd = sc.nextLine();
                            loggedInAcc = bank.createAccount(name, phone, pwd);
                            if(loggedInAcc!=-1)
                                System.out.println("Account created! Your Account Number is: " + loggedInAcc);
                            else
                                System.out.println("Unable to Account created may be Invalid credentials!");
                            break;
                        case 2:
                            System.out.print("Account Number: ");
                            long loginAcc = sc.nextLong();
                            sc.nextLine();
                            System.out.print("Password: ");
                            String loginPwd = sc.nextLine();
                            loggedInAcc = bank.login(loginAcc, loginPwd);
                            if (loggedInAcc != -1) System.out.println("Login successful! Account: " + loggedInAcc);
                            else System.out.println("Invalid credentials!");
                            break;
                        case 3:
                            System.exit(0);
                        default:
                            System.out.println("Choose Valid Option.");
                            break;
                    }
                } catch (SQLException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("\n1. Deposit");
                System.out.println("2. Withdraw");
                System.out.println("3. Balance");
                System.out.println("4. Transactions");
                System.out.println("5. Logout");
                System.out.print("Enter choice: ");
                int ch = sc.nextInt();
                sc.nextLine(); 

                try {
                    switch (ch) {
                        case 1:
                            System.out.print("Amount: ");
                            double dep = sc.nextDouble();
                            bank.deposit(loggedInAcc, dep);
                            break;
                        case 2:
                            System.out.print("Amount: ");
                            double wd = sc.nextDouble();
                            bank.withdraw(loggedInAcc, wd);
                            break;
                        case 3:
                            double balance = bank.getBalance(loggedInAcc);
                            System.out.println("Current Balance: " + balance);
                            break;
                        case 4:
                            bank.showTransactions(loggedInAcc);
                            break;
                        case 5:
                            loggedInAcc = -1;
                            System.out.println("Logged out successfully!");
                            break;
                        default:
                            System.out.println("Choose Valid Option.");
                            break;
                    }
                } catch (SQLException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }
}

