import java.awt.*;
import javax.swing.*;

public class Transfer extends Transaction {
    private Keypad keypad;
    private BankDatabase bankDatabase;
    private Screen screen;
    private ATM atm;
    private JPanel screenPanel;
    private Thread takeCardthread;

    public Transfer(int fromAccountNumber, Screen screen, BankDatabase bankDatabase, Keypad keypad, ATM atm) {
        super(fromAccountNumber, screen, bankDatabase);
        this.keypad = keypad;
        this.bankDatabase = bankDatabase;
        this.screen = screen;
        this.atm = atm;
        this.screenPanel = atm.screen_panel;
    }

    @Override
    public void execute() {
        screen.clear();
        screen.displayMessageLine("=== Transfer Funds ===");
        screen.displayMessage("\n(Press 0 to cancel transfer) Enter transfer amount (HKD): ");
        double amount = keypad.getDoubleInput();

        if (amount == 0) {
            screen.displayMessageLine("Transfer canceled.");
            screen.MessagePopup("You have canceled the transfer.");
            return;
        }

        Account account = bankDatabase.getAccount(getAccountNumber());

        if (account instanceof Cheque_Account) {
            Cheque_Account chequeAccount = (Cheque_Account) account;
            if (amount > chequeAccount.getLimit_per_cheque()) {
                screen.displayMessageLine(" Amount exceeds cheque limit.");
                screen.displayDollarAmount(chequeAccount.getLimit_per_cheque());
                screen.displayMessageLine("\nTransfer canceled.");
                screen.MessagePopup("Amount exceeds cheque limit (HK$50,000). Please try a smaller amount.");
                return;
            }
        }

        screen.displayMessage("Enter the account number to transfer to: ");
        int toAccount = keypad.getIntInput();

        if (getAccountNumber() == toAccount) {
            screen.displayMessageLine(" Cannot transfer to your own account.");
            screen.MessagePopup("Transfer canceled. You cannot transfer to your own account.");
            return;
        }

        double availableBalance = bankDatabase.getAvailableBalance(getAccountNumber());

        if (amount <= availableBalance) {
            boolean success = bankDatabase.transferFunds(getAccountNumber(), toAccount, amount);
            if (success) {
                screen.displayMessageLine(" Transfer successful.");
                screen.displayMessage("Transferred Amount: ");
                screen.displayDollarAmount(amount);
                screen.displayMessageLine("");
                screen.displayMessageLine("To Account: " + toAccount);
                screen.MessagePopup("Transfer successful!\nHK$" + amount + " sent to account " + toAccount);
                showReceiptChoiceUI(amount, toAccount);
            } else {
                screen.displayMessageLine(" Target account not found.");
                screen.MessagePopup("Transfer failed.\nTarget account does not exist.");
            }
        } else {
            screen.displayMessageLine(" Insufficient funds.");
            screen.MessagePopup("Transfer canceled.\nYour balance is not enough.");
        }
    }

    private void showReceiptChoiceUI(double amount, int toAccount) {
        screenPanel.removeAll();
        screenPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1.0;
        c.weighty = 1.0;

        JLabel title = new JLabel("Transfer Successful", SwingConstants.CENTER);
        title.setFont(atm.MODERN_FONT);
        title.setOpaque(true);
        title.setBackground(atm.GREEN_COLOR);
        title.setForeground(Color.BLUE);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        screenPanel.add(title, c);

        JLabel prompt = new JLabel("Do you want a receipt?", SwingConstants.CENTER);
        prompt.setForeground(Color.WHITE);
        prompt.setFont(atm.MODERN_FONT);
        c.gridy = 1;
        screenPanel.add(prompt, c);

        c.gridwidth = 1;
        c.gridy = 2;
        c.gridx = 0;
        JLabel option1 = new JLabel("Print advice & take card", SwingConstants.CENTER);
        option1.setForeground(Color.WHITE);
        option1.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4));
        option1.setFont(atm.MODERN_FONT);
        screenPanel.add(option1, c);

        c.gridx = 1;
        JLabel option2 = new JLabel("Take card only", SwingConstants.CENTER);
        option2.setForeground(Color.WHITE);
        option2.setBorder(BorderFactory.createLineBorder(Color.WHITE, 4));
        option2.setFont(atm.MODERN_FONT);
        screenPanel.add(option2, c);

        screenPanel.revalidate();
        screenPanel.repaint();

        screen.displayMessageLine("Press button 7 or 8 to continue.");
        keypad.waitAction();
        int pressed = keypad.getButtonPressed();
        boolean wait = true;
            while (wait) {
                if (pressed == 7 || pressed == 8) {
                    wait = false;
                }
                else
                keypad.waitAction();
            }
            System.out.println("Button pressed: " + pressed);
        if (pressed == 8){
            showTakeCardUI(amount, toAccount, false);
            try {
                takeCardthread.join();
                    } catch (InterruptedException e) {
                        System.out.println("Main thread interrupted while waiting.");
                        Thread.currentThread().interrupt();
                    } // end if
                }
        else {
            showTakeCardUI(amount, toAccount, true);
            try {
                takeCardthread.join();
                    } catch (InterruptedException e) {
                        System.out.println("Main thread interrupted while waiting.");
                        Thread.currentThread().interrupt();
                    } // end if
        }

    }

    private void showTakeCardUI(double amount, int toAccount, boolean printReceipt) {
        takeCardthread = new Thread(() -> {
            screenPanel.removeAll();
            screenPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            c.insets = new Insets(10, 10, 10, 10);
            c.weightx = 1.0;
            c.weighty = 1.0;
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 2;

            JLabel thankYou = new JLabel("Thank you for using ATM", SwingConstants.CENTER);
            thankYou.setOpaque(true);
            thankYou.setBackground(atm.GREEN_COLOR);
            thankYou.setForeground(Color.BLUE);
            thankYou.setFont(atm.MODERN_FONT);
            screenPanel.add(thankYou, c);

            c.gridy = 1;
            JLabel takeCard = new JLabel("Please take your card", SwingConstants.CENTER);
            takeCard.setForeground(Color.WHITE);
            takeCard.setFont(atm.MODERN_FONT);
            screenPanel.add(takeCard, c);

            c.gridy = 2;
            try {
                Icon icon = new ImageIcon(getClass().getResource("takeoutCard.png"));
                Image img = ((ImageIcon) icon).getImage();
                Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                Icon scaledIcon = new ImageIcon(scaledImg);
                screenPanel.add(new JLabel(scaledIcon), c);
            } catch (Exception e) {
                screenPanel.add(new JLabel("<< Card Image Missing >>", JLabel.CENTER), c);
            }
            c.gridy = 3;
            if (printReceipt) {
                JLabel receipt = new JLabel("<html><center>Receipt:<br>HK$" +
                        amount + " to Account " + toAccount + "</center></html>", SwingConstants.CENTER);
                receipt.setForeground(Color.WHITE);
                receipt.setFont(atm.MODERN_FONT);
                screenPanel.add(receipt, c);

            }
            screenPanel.revalidate();
            screenPanel.repaint();

            screen.displayMessageLine("Please take your card to finish.");
            keypad.waitAction();
        });
        takeCardthread.start();

    }
}