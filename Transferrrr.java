import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Transferrrr extends JPanel {

    private JTextField amountField;
    private JTextField toAccountField;
    private JTextArea statusArea;

    public Transferrrr() {
        setLayout(new BorderLayout());
        setBackground(Color.DARK_GRAY);

       
        JLabel titleLabel = new JLabel("Transfer Funds", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(20, 120, 180));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

   
        statusArea = new JTextArea("Welcome to ATM Transfer\nPlease enter amount and account number.");
        statusArea.setEditable(false);
        statusArea.setBackground(new Color(0, 51, 102)); 
        statusArea.setForeground(Color.WHITE);
        statusArea.setFont(new Font("Monospaced", Font.BOLD, 16));
        statusArea.setMargin(new Insets(10, 10, 10, 10));
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        add(statusArea, BorderLayout.CENTER);

   
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        inputPanel.setBackground(Color.DARK_GRAY);

        inputPanel.add(createLabel("Amount (HKD):"));
        amountField = new JTextField();
        inputPanel.add(amountField);

        inputPanel.add(createLabel("To Account #:"));
        toAccountField = new JTextField();
        inputPanel.add(toAccountField);

        JButton confirmButton = new JButton(" Transfer");
        JButton cancelButton = new JButton(" Cancel");
        inputPanel.add(confirmButton);
        inputPanel.add(cancelButton);

        add(inputPanel, BorderLayout.SOUTH);

 
        add(createSideButtons("➤"), BorderLayout.WEST);
        add(createSideButtons("◀"), BorderLayout.EAST);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleTransfer();
            }
        });

        cancelButton.addActionListener(e -> {
            amountField.setText("");
            toAccountField.setText("");
            statusArea.setText("Transfer cancelled. Returning to main menu...");
        });
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return label;
    }

    private JPanel createSideButtons(String icon) {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBackground(Color.DARK_GRAY);
        for (int i = 0; i < 4; i++) {
            JButton sideBtn = new JButton(icon);
            sideBtn.setFocusable(false);
            sideBtn.setBackground(new Color(70, 70, 90));
            sideBtn.setForeground(Color.WHITE);
            sideBtn.setFont(new Font("SansSerif", Font.BOLD, 20));
            panel.add(sideBtn);
        }
        return panel;
    }

    private void handleTransfer() {
        String amountText = amountField.getText().trim();
        String accountText = toAccountField.getText().trim();

        if (amountText.isEmpty() || accountText.isEmpty()) {
            statusArea.setText("Please fill in both fields.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            int toAccount = Integer.parseInt(accountText);

            if (amount <= 0 || amount % 100 != 0) {
                statusArea.setText("Amount must be greater than 0 and a multiple of HK$100.");
                return;
            }

            if (toAccount == 123456) {
                statusArea.setText("Cannot transfer to your own account.");
                return;
            }

       
            statusArea.setText(" Transfer successful!\n\n" +
                    "Amount: HK$" + amount + "\n" +
                    "To Account: " + toAccount + "\n\n" +
                    "Please take your receipt.\n Please take your card.");
        } catch (NumberFormatException ex) {
            statusArea.setText(" Invalid input. Please enter valid numeric values.");
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ATM Transfer GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(720, 440);
        frame.setContentPane(new Transferrrr());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}