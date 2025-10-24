public class Transfer extends Transaction {
    private double amount;
    private int toAccount;
    private Keypad keypad;
    private BankDatabase bankDatabase;
    private Screen screen;
    
    

    public Transfer(int fromAccountNumber, Screen screen, BankDatabase bankDatabase, Keypad keypad) {
        super(fromAccountNumber, screen, bankDatabase);
        this.keypad = keypad;
        this.bankDatabase = bankDatabase;
        this.screen = screen;
    }
    

    @Override
        public void execute() {
        screen.displayMessage("\nEnter transfer amount (HKD): ");
        double amount = keypad.getDoubleInput();

        screen.displayMessage("Enter the account number to transfer to: ");
        int toAccount = keypad.getIntInput();

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