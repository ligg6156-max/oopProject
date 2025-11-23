// ATM.java
// Represents an automated teller machine

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.TextArea;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.OverlayLayout;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class ATM {

    private boolean userAuthenticated; // whether user is authenticated
    private int currentAccountNumber; // current user's account number
    private Screen screen; // ATM's screen
    private Keypad keypad; // ATM's keypad
    private CashDispenser cashDispenser; // ATM's cash dispenser
    private BankDatabase bankDatabase; // account information database

    // constants corresponding to main menu options
    private static final int BALANCE_INQUIRY = 1;
    private static final int WITHDRAWAL = 2;
    private static final int TRANSFER = 3;
    private static final int EXIT = 4;
    private static final int CANCELED = -1;
    protected boolean userExited = false; // user has not chosen to exit

    protected JFrame frame;
    protected JButton[] Sidebuttons;
    protected JLabel label;
    protected JPanel screen_panel;
    private TextArea displayArea; // For both displaying messages and input
    protected JProgressBar progressBar;
    protected JLayeredPane layeredPane;

    protected static final Color BACKGROUND = new Color(30, 30, 40);
    protected static final Color SCREEN_PANEL_COLOR = new Color(20, 120, 180);
    protected static final Color BUTTON_COLOR = new Color(70, 70, 90);
    protected static final Color MODERN_TEXT = new Color(255, 255, 255);
    protected static final Color GREEN_COLOR = new Color(50, 150, 50);
    protected static final Font MODERN_FONT = new Font("Consolas", Font.BOLD, 25);
    private Thread TakeCardThread;
    // no-argument ATM constructor initializes instance variables

    public ATM() {
        userAuthenticated = false; // user is not authenticated to start
        currentAccountNumber = 0; // no current account number to start
        // Create single TextArea for display and input (4:3 aspect ratio)
        displayArea = new TextArea("HK$", 30, 80, TextArea.SCROLLBARS_NONE); // Approximately 4:3 ratio for inner screen, no scrollbars
        displayArea.setEditable(false); // Make it non-editable - use KeyListener for input
        displayArea.setBackground(SCREEN_PANEL_COLOR); // Modern dark background
        displayArea.setForeground(MODERN_TEXT); // Modern cyan text
        displayArea.setFont(MODERN_FONT);
        screen = new Screen(displayArea, this); // create screen with display TextArea
        cashDispenser = new CashDispenser(); // create cash dispenser
        bankDatabase = new BankDatabase(); // create acct info database
        JPanel mainpanel = new JPanel(new BorderLayout(15, 15));
        mainpanel.setBackground(new Color(30, 30, 40)); // Modern dark gray
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));

        screen_panel = new JPanel(new BorderLayout());
        screen_panel.setPreferredSize(new Dimension(800, 600));
        screen_panel.setBackground(new Color(20, 20, 30)); // Match display area
        screen_panel.add(new JLabel("Loading"));
        frame = new JFrame("ATM Machine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1120, 720); // 5:3 aspect ratio (1000 width, 600 height)
        frame.setLayout(new BorderLayout(10, 10));
        frame.setBackground(Color.BLACK); // Set frame background to black
        UIManager.put("Button.font", new Font("Consolas", Font.BOLD, 25));
        UIManager.put("Label.font", new Font("Consolas", Font.BOLD, 25));
        Font customFont = new Font("Consolas", Font.BOLD, 25);
        displayArea.setFont(customFont);
        UIManager.put("TextArea.font", MODERN_FONT);
        // Create keypad after frame and TextArea are created
        keypad = new Keypad(displayArea); // create keypad with TextArea for GUI input
        screen.setKeypad(keypad); // pass keypad reference to screen
        keypad.setScreen(screen); // pass screen reference to keypad
        frame.addKeyListener(keypad); // register keypad as listener for frame
        displayArea.addKeyListener(keypad); // register keypad as listener for TextArea
        frame.setFocusable(true); // Make frame focusable to receive key events
        mainpanel.add(layeredPane, BorderLayout.CENTER);
        layeredPane.add(screen_panel, 0);
        // Create side button panels. BorderLayout only accepts one component
        // per region, so use a panel with GridLayout for multiple buttons.
        Sidebuttons = new JButton[8];
        JPanel leftPanel = new JPanel(new GridBagLayout());
        JPanel rightPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.CENTER;
        c.weightx = 50;
        c.weighty = 50;
        c.gridx = 0;
        c.gridy = 0;
        leftPanel.add(Box.createVerticalStrut(100), c);
        leftPanel.add(Box.createVerticalStrut(100), c);
        rightPanel.add(Box.createVerticalStrut(100), c);
        rightPanel.add(Box.createVerticalStrut(100), c);
        leftPanel.setPreferredSize(new Dimension(180, 0)); // Set width to 150 pixels for reasonable button size
        rightPanel.setPreferredSize(new Dimension(150, 0)); // Set width to 150 pixels for reasonable button size
        rightPanel.setBackground(new Color(30, 30, 40));
        leftPanel.setBackground(new Color(30, 30, 40));
        for (int i = 0; i < 4; i++) {
            c.gridy = i + 1;
            Sidebuttons[i] = new JButton("►");
            Sidebuttons[i].setPreferredSize(new Dimension(100, 100));
            Sidebuttons[i].setBackground(BUTTON_COLOR); // Modern cyan
            Sidebuttons[i].setForeground(Color.WHITE);
            Sidebuttons[i].setFocusable(false); // Prevent button from taking focus
            final int buttonIndex = i + 1; // Button 1-4 for left side
            Sidebuttons[i].addActionListener(e -> {
                keypad.setButtonInput(buttonIndex); // Set the button value
            });
            leftPanel.add(Sidebuttons[i], c);
        }
        c.gridy = 0;
        for (int i = 4; i < 8; i++) {
            c.gridy = i + 1;
            Sidebuttons[i] = new JButton("◄");
            Sidebuttons[i].setPreferredSize(new Dimension(100, 100));
            Sidebuttons[i].setBackground(BUTTON_COLOR); // Modern cyan
            Sidebuttons[i].setForeground(Color.WHITE);
            Sidebuttons[i].setFocusable(false); // Prevent button from taking focus
            final int buttonIndex = i + 1; // Button 5-8 for right side
            Sidebuttons[i].addActionListener(e -> {
                keypad.setButtonInput(buttonIndex); // Set the button value
            });
            rightPanel.add(Sidebuttons[i], c);
        }

        mainpanel.add(leftPanel, BorderLayout.WEST);
        mainpanel.add(rightPanel, BorderLayout.EAST);
        frame.add(mainpanel);
        frame.setVisible(true);
        frame.requestFocus(); // Request focus so key events are received
    } // end no-argument ATM constructor

    // start ATM 
    public void wellcome() {
        screen_panel.removeAll();
        screen_panel.setLayout(new BorderLayout());
        screen_panel.setBackground(SCREEN_PANEL_COLOR); // Keep modern dark background
        // Create main container
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(SCREEN_PANEL_COLOR);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Top panel with time and date
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(SCREEN_PANEL_COLOR);

        // Current time and date
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentTime = timeFormat.format(new Date());
        String currentDate = dateFormat.format(new Date());

        JLabel timeLabel = new JLabel(currentTime, JLabel.RIGHT);
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("CONSOLAS", Font.BOLD, 20));

        JLabel dateLabel = new JLabel(currentDate, JLabel.RIGHT);
        dateLabel.setForeground(Color.WHITE);
        dateLabel.setFont(new Font("CONSOLAS", Font.PLAIN, 16));
        JPanel timePanel = new JPanel(new BorderLayout());
        timePanel.setBackground(SCREEN_PANEL_COLOR);
        timePanel.add(timeLabel, BorderLayout.NORTH);
        timePanel.add(dateLabel, BorderLayout.SOUTH);

        topPanel.add(timePanel, BorderLayout.EAST);

        // Bank logo area
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(SCREEN_PANEL_COLOR);

        JLabel bankLogo = new JLabel("OOP BANK", JLabel.CENTER);
        bankLogo.setForeground(Color.WHITE);
        bankLogo.setFont(new Font("CONSOLAS", Font.BOLD, 36));
        bankLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        logoPanel.add(bankLogo);

        topPanel.add(logoPanel, BorderLayout.CENTER);

        // Welcome message area
        JPanel welcomePanel = new JPanel(new GridLayout(4, 1, 10, 10));
        welcomePanel.setBackground(SCREEN_PANEL_COLOR);

        JLabel welcomeLabel1 = new JLabel("Welcome to", JLabel.CENTER);
        welcomeLabel1.setForeground(Color.WHITE);
        welcomeLabel1.setFont(new Font("CONSOLAS", Font.BOLD, 28));

        JLabel welcomeLabel2 = new JLabel("ATM SERVICE", JLabel.CENTER);
        welcomeLabel2.setForeground(Color.YELLOW);
        welcomeLabel2.setFont(new Font("CONSOLAS", Font.BOLD, 32));

        JLabel cardLabel = new JLabel("Insert Card", JLabel.CENTER);
        cardLabel.setForeground(Color.CYAN);
        cardLabel.setFont(new Font("CONSOLAS", Font.BOLD, 20));
        cardLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        Icon InsertcardIcon = new ImageIcon(getClass().getResource("welcome.png"));
        Image img = ((ImageIcon) InsertcardIcon).getImage();
        Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        Icon scaledIcon = new ImageIcon(scaledImg);
        JLabel instructionLabel = new JLabel(scaledIcon, JLabel.CENTER);
        instructionLabel.setForeground(new Color(200, 200, 200));
        instructionLabel.setFont(new Font("CONSOLAS", Font.BOLD, 18));

        welcomePanel.add(welcomeLabel1);
        welcomePanel.add(welcomeLabel2);
        welcomePanel.add(cardLabel);
        welcomePanel.add(instructionLabel);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(SCREEN_PANEL_COLOR);

        JLabel GoodLabel = new JLabel("", JLabel.CENTER);
        GoodLabel.setForeground(Color.WHITE);
        if (LocalTime.now().getHour() < 12 && LocalTime.now().getHour() >= 5) {
            GoodLabel.setText("Good Morning!");

            screen_panel.add(GoodLabel, BorderLayout.CENTER);
        } else if (LocalTime.now().getHour() < 18 && LocalTime.now().getHour() >= 13) {
            GoodLabel.setText("Good Afternoon!");
            screen_panel.add(GoodLabel, BorderLayout.CENTER);
        } else if(LocalTime.now().getHour() < 20){
            GoodLabel.setText("Good Evening!");
            screen_panel.add(GoodLabel, BorderLayout.CENTER);
        } else {
            GoodLabel.setText("Good Night!");
            screen_panel.add(GoodLabel, BorderLayout.CENTER);
        }
        bottomPanel.add(GoodLabel);

        mainContainer.add(topPanel, BorderLayout.NORTH);
        mainContainer.add(welcomePanel, BorderLayout.CENTER);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);
        screen_panel.add(mainContainer, BorderLayout.CENTER);
        screen_panel.revalidate();
        screen_panel.repaint();
        // Wait for Enter key press (blocking)
        do {
            if (keypad.keypressed == 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentTime = timeFormat.format(new Date());
                timeLabel.setText(currentTime);
                timeLabel.repaint();
            } else {
                break;
            }
        } while (true);
        // Enter was pressed, show login screen
        setProcessingUI(2);
        runProcessingUI();
        loginScreen();
    }

    public void loginScreen() {
        screen_panel.removeAll();
        screen_panel.setLayout(new BorderLayout()); // Reset to BorderLayout for proper stretching
        screen_panel.add(displayArea, BorderLayout.CENTER); // Add to CENTER for full coverage
        screen_panel.revalidate();
        screen_panel.repaint();
        displayArea.requestFocus(); // Focus on TextArea
        // Restore keypad to main displayArea
        keypad.setTextArea(displayArea);
    }

    public void timeshow() {
        screen_panel.removeAll();
        screen_panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JLabel welcome = new JLabel("Welcome User " + currentAccountNumber, JLabel.CENTER);
        welcome.setForeground(Color.WHITE);
        screen_panel.add(welcome, BorderLayout.CENTER);
        screen_panel.revalidate();
        screen_panel.repaint();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        keypad.buttonPressState = false;
        // Start wellcome in a separate thread so GUI doesn't freeze
        Thread atmThread = new Thread(() -> {
            wellcome();
            // welcome and authenticate user; perform transactions
            while (true) {
                // loop while user is not yet authenticated
                while (!userAuthenticated) {
                    screen.displayMessageLine("\nWelcome!");
                    authenticateUser(); // authenticate user
                } // end while

                performTransactions(); // user is now authenticated 
                userAuthenticated = false; // reset before next ATM session
                currentAccountNumber = 0; // reset before next ATM session 
                screen.displayMessageLine("\nThank you! Goodbye!");

                // Show welcome screen again for next session
                try {
                    Thread.sleep(1000); // Wait 1 second before showing welcome screen
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                wellcome(); // Show welcome screen again
            } // end while
        });
        atmThread.start();
    } // end method run

    // attempts to authenticate user against database
    private void authenticateUser() {
        screen.clear();
        screen.displayMessage("\n\n> Please enter your account number: ");
        int accountNumber = keypad.getIntInput(); // input account number
        if (accountNumber == CANCELED) {
            screen.clear();
            screen.displayMessageLine("Invalid account number or PIN. Please try again.");
            return;
        }
        screen.clear();
        screen.displayMessage("\n>Enter your PIN: "); // prompt for PIN
        keypad.setPasswordMode(true); // Enable password masking
        int pin = keypad.getIntInput(); // input PIN
        keypad.setPasswordMode(false); // Disable password masking
        if (accountNumber == CANCELED) {
            screen.clear();
            screen.displayMessageLine("Invalid account number or PIN. Please try again.");
            return;
        }

        // set userAuthenticated to boolean value returned by database
        userAuthenticated
                = bankDatabase.authenticateUser(accountNumber, pin);

        // check whether authentication succeeded
        if (userAuthenticated) {
            currentAccountNumber = accountNumber; // save user's account #
            timeshow();
        } // end if
        else {
            screen.clear();
            screen.MessagePopup(
                    "Invalid account number or PIN. Please try again.");
        }
    } // end method authenticateUser

    private Transaction createTransaction(int type) {
        loginScreen();
        Transaction temp = null;

        switch (type) {
            case BALANCE_INQUIRY:
                temp = new BalanceInquiry(currentAccountNumber, screen, bankDatabase, keypad, this);
                break;
            case WITHDRAWAL:
                temp = new Withdrawal(currentAccountNumber, screen, bankDatabase, keypad, cashDispenser, screen_panel, this);
                break;
            case TRANSFER:
                temp = new Transfer(currentAccountNumber, screen, bankDatabase, keypad, this);
                break;
        }

        return temp;
    }
    // display the main menu and perform transactions

    private void performTransactions() {
        // local variable to store transaction currently being processed
        Transaction currentTransaction = null;

        // loop while user has not chosen option to exit system
        while (!userExited) {
            // show main menu and get user selection
            int mainMenuSelection = displayMainMenu();

            // decide how to proceed based on user's menu selection
            switch (mainMenuSelection) {
                // user chose to perform one of three transaction types
                case BALANCE_INQUIRY:
                case WITHDRAWAL:
                case TRANSFER:
                    // initialize as new object of chosen type
                    currentTransaction
                            = createTransaction(mainMenuSelection);

                    currentTransaction.execute(); // execute transaction
                    break;
                case EXIT: // user chose to terminate session
                    setProcessingUI(1);
                    runProcessingUI();
                    TakeCardUI();
                    try {
                        TakeCardThread.join(); // Wait for TakeCardThread to finish
                        System.out.println("Main thread continues after TakeCardThread finished.");
                    } catch (InterruptedException e) {
                        System.out.println("Main thread interrupted while waiting.");
                        Thread.currentThread().interrupt();
                    } // end if
                    userExited = true; // this ATM session should end
                    break;
                case CANCELED:
                    screen.displayMessageLine("\nExiting the system...");
                    userExited = true; // this ATM session should end
                    break;
                default: // user did not enter an integer from 1-4
                    System.out.printf("%s", mainMenuSelection);
                    screen.displayMessageLine(
                            "\nYou did not enter a valid selection. Try again.");
                    break;
            } // end switch
        } // end while
        userExited = false; // Reset for next session
    } // end method performTransactions

    // display the main menu and return an input selection
    private int displayMainMenu() {
        int input;
        keypad.buttonPressState = true;
        keypad.ButtonPressed = 0;
        MainmenuUI();
        keypad.waitAction();
        input = keypad.getButtonPressed(); // return user's selection
        if (input == 1 || input == 5) {
            input = 1;
        } else if (input == 2 || input == 6) {
            input = 2;
        } else if (input == 3 || input == 7) {
            input = 3;
        } else if (input == 4 || input == 8) {
            input = 4;
        } else {
            // No button pressed, keep input as is
        }
        return input;

    } // end method displayMainMenu

    private void MainmenuUI() {
        screen_panel.removeAll();
        screen_panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(10, 10, 10, 10);
        c.weightx = 1.0;
        c.weighty = 10.0;
        // Add title
        Border lineborder = javax.swing.BorderFactory.createLineBorder(new Color(255, 255, 255), 5);
        JLabel title = new JLabel("Main menu", JLabel.CENTER);
        title.setForeground(Color.WHITE);
        title.setPreferredSize(new Dimension(50, 50));
        title.setBorder(lineborder);
        title.setBackground(GREEN_COLOR);
        title.setOpaque(true);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        screen_panel.add(title, c);

        // Reset gridwidth for options
        c.gridwidth = 1;
        c.gridy = 1;

        c.gridy = 1;
        c.gridx = 0;
        c.gridwidth = 1;
        JLabel option1 = new JLabel("View my balance", JLabel.CENTER);
        option1.setForeground(new Color(255, 255, 255));
        option1.setPreferredSize(new Dimension(50, 50));
        option1.setBorder(lineborder);
        screen_panel.add(option1, c);

        c.gridy = 2;
        c.gridx = 0;
        c.gridwidth = 1;
        JLabel option2 = new JLabel("Withdraw cash", JLabel.CENTER);
        option2.setForeground(new Color(255, 255, 255));
        option2.setPreferredSize(new Dimension(50, 50));
        option2.setBorder(lineborder);
        screen_panel.add(option2, c);

        c.gridy = 3;
        c.gridx = 0;
        c.gridwidth = 1;
        JLabel option3 = new JLabel("Transfer funds", JLabel.CENTER);
        option3.setForeground(new Color(255, 255, 255));
        option3.setPreferredSize(new Dimension(50, 50));
        option3.setBorder(lineborder);
        screen_panel.add(option3, c);

        c.gridy = 4;
        c.gridx = 0;
        c.gridwidth = 1;
        JLabel option4 = new JLabel("Exit", JLabel.CENTER);
        option4.setForeground(new Color(255, 255, 255));
        option4.setPreferredSize(new Dimension(50, 50));
        option4.setBorder(lineborder);
        screen_panel.add(option4, c);
        screen_panel.revalidate();
        screen_panel.repaint();

    }

    public void runProcessingUI() {
        int tmp = 6000;
        while (tmp > 0) {
            if (tmp >= 1000) {
                progressBar.setValue(progressBar.getValue() + 10);
                tmp -= 1000;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                } // Delay to show progress
            } else if (tmp >= 500) {
                progressBar.setValue(progressBar.getValue() + 5);
                tmp -= 500;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                } // Delay to show progress
            } else if (tmp >= 100) {
                progressBar.setValue(progressBar.getValue() + 1);
                tmp -= 100;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                } // Delay to show progress
            }
        }
    }

    public void setProcessingUI(int a) {

        screen_panel.removeAll();
        screen_panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(10, 10, 10, 10);
        c.weightx = 1.0;
        c.weighty = 10.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        progressBar = new JProgressBar(0, 50);
        screen_panel.add(progressBar, c);
        c.gridy = 1;
        JLabel processingLabel = new JLabel("_____",JLabel.CENTER);
        switch (a){
            case 1:
                processingLabel.setText("<html><b align=center>Exiting with your Account</b><br><b align=center>Please wait...</b></html>");
                break;
            case 2:
                processingLabel.setText("<html><b align=center>reading with the Card</b><br><b align=center>Please wait...</b></html>");
                break;
            default:
                processingLabel.setText("<br><b align=center>Please wait...</b></html>");
                break;
        }
        screen_panel.add(Box.createVerticalStrut(200), c);
        processingLabel.setBackground(GREEN_COLOR);
        processingLabel.setForeground(new Color(0, 0, 0));
        processingLabel.setPreferredSize(new Dimension(50, 50));
        screen_panel.add(processingLabel, c);
        c.gridy = 2;
        c.gridheight = 1;
        c.gridy = 3;
        c.gridheight = 2;
        screen_panel.add(Box.createVerticalStrut(200), c);
        screen_panel.revalidate();
        screen_panel.repaint();
    }

    public void TakeCardUI() {
        TakeCardThread = new Thread(() -> {
            keypad.buttonPressState = false;
            screen_panel.removeAll();
            screen_panel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c = new GridBagConstraints();  // Reset constraints
            c.fill = GridBagConstraints.BOTH;
            c.insets = new java.awt.Insets(10, 10, 10, 10);
            c.weightx = 1.0;
            c.weighty = 10.0;
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 1;
            JLabel takeCardLabel = new JLabel("<html><b>Thank you for choosing ATM</b></html>", JLabel.CENTER);
            takeCardLabel.setBackground(GREEN_COLOR);
            takeCardLabel.setForeground(new Color(0, 0, 0));
            takeCardLabel.setPreferredSize(new Dimension(50, 50));
            takeCardLabel.setOpaque(true);
            screen_panel.add(takeCardLabel, c);
            c.gridy = 1;
            screen_panel.add(Box.createVerticalStrut(100), c);
            c.gridy = 2;
            JLabel takeyourCard = new JLabel("<html><b>Please take your card</b></html>", JLabel.CENTER);
            takeyourCard.setForeground(new Color(255, 255, 255));
            screen_panel.add(takeyourCard, c);
            c.gridy = 3;
            Icon takecardIcon = new ImageIcon(getClass().getResource("takeoutCard.png"));
            Image img = ((ImageIcon) takecardIcon).getImage();
            Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            Icon scaledIcon = new ImageIcon(scaledImg);
            screen_panel.add(new JLabel(scaledIcon), c);
            c.gridy = 4;
            screen_panel.add(Box.createVerticalStrut(100), c);
            c.gridy = 4;
            screen_panel.add(Box.createVerticalStrut(100), c);
            screen_panel.revalidate();
            screen_panel.repaint();
            keypad.waitAction();

        });
        TakeCardThread.start();
    }
} // end class ATM

/**
 * ************************************************************************
 * (C) Copyright 1992-2007 by Deitel & Associates, Inc. and * Pearson Education,
 * Inc. All Rights Reserved. * * DISCLAIMER: The authors and publisher of this
 * book have used their * best efforts in preparing the book. These efforts
 * include the * development, research, and testing of the theories and programs
 * * to determine their effectiveness. The authors and publisher make * no
 * warranty of any kind, expressed or implied, with regard to these * programs
 * or to the documentation contained in these books. The authors * and publisher
 * shall not be liable in any event for incidental or * consequential damages in
 * connection with, or arising out of, the * furnishing, performance, or use of
 * these programs. *
 ************************************************************************
 */
