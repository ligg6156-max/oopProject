public class Transfer extends Transaction {
    private double amount;
    private int toAccount;
    private Keypad keypad;
    private BankDatabase bankDatabase;
    private Screen screen;
    private Account account;
    private int accountNumber;
    

    public Transfer(int fromAccountNumber, Screen screen, BankDatabase bankDatabase, Keypad keypad) {
        super(fromAccountNumber, screen, bankDatabase);
        this.keypad = keypad;
        this.bankDatabase = bankDatabase;
        this.screen = screen;
        accountNumber = fromAccountNumber;
    }
    

    @Override
        public void execute() {
        screen.displayMessage("\n(Press 0 to cancel transfer) Enter transfer amount (HKD): ");
        double amount = keypad.getDoubleInput();
        if (amount == 0){
            screen.displayMessageLine("Transfer canceled.");
            return;
        }
        screen.displayMessage("Enter the account number to transfer to: ");
        int toAccount = keypad.getIntInput();
        if (accountNumber == toAccount){
            screen.displayMessageLine("Cannot transfer money to yourself. Transfer canceled.");
            return;
        }
        double availableBalance = bankDatabase.getAvailableBalance(getAccountNumber());

        if (amount <= availableBalance) {
            bankDatabase.transferFunds(getAccountNumber(), toAccount, amount);
            screen.displayMessageLine("Transfer successful.");
        }
        else {
            screen.displayMessageLine("Insufficient funds. Transfer canceled.");
        }
    }
}