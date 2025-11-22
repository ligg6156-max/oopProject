// Screen.java
// Represents the screen of the ATM

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.TextArea;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Screen extends JPanel {

    private TextArea textArea; // Reference to the TextArea in ATM
    private JTextArea jTextArea; // Reference to the JTextArea in ATM
    private ATM atm; // Reference to the ATM instance
    private Keypad keypad; // Reference to the Keypad instance
    // Constructor that accepts TextArea from ATM

    public Screen(TextArea textArea, ATM atm) {
        this.textArea = textArea;
        this.atm = atm;
    }

    public Screen(JTextArea textArea, ATM atm) {
        this.jTextArea = textArea;
        this.atm = atm;
    }

    // Setter for keypad reference
    public void setKeypad(Keypad keypad) {
        this.keypad = keypad;
    }
    // displays a message without a carriage return

    public void displayMessage(String message) {
        System.out.print(message);
        if (textArea != null || jTextArea != null) {
            updateGUI(() -> {
                if (textArea != null) {
                    textArea.append(message);
                } else {
                    jTextArea.append(message);
                }
            });
        }
    } // end method displayMessage

    // display a message with a carriage return
    public void displayMessageLine(String message) {
        System.out.println(message);
        if (textArea != null || jTextArea != null) {
            if (textArea != null) {
                SwingUtilities.invokeLater(() -> textArea.append(message + "\n"));
            } else {
                SwingUtilities.invokeLater(() -> jTextArea.append(message + "\n"));
            }
        }
    } // end method displayMessageLine

    public void updateGUI(Runnable action) {
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                action.run();
            } else {
                SwingUtilities.invokeAndWait(action);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // display a dollar amount

    public void displayDollarAmount(double amount) {
        System.out.printf("HK$%,.2f", amount);
        if (textArea != null || jTextArea != null) {
            String formatted = String.format("HK$%,.2f", amount);
            if (textArea != null) {
                SwingUtilities.invokeLater(() -> textArea.append(formatted));
            } else {
                SwingUtilities.invokeLater(() -> jTextArea.append(formatted));
            }
        }
    } // end method displayDollarAmount 

    // clear the screen
    public void clear() {
        if (textArea != null || jTextArea != null) {
            updateGUI(() -> {
                if (textArea != null) {
                    textArea.setText("");
                } else {
                    jTextArea.setText("");
                }
            });
        }
    } // end method clear

    public void MessagePopup(String message) {
        // Create semi-transparent overlay using custom paintComponent
        JPanel MessagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                // Paint semi-transparent white background
                g2d.setColor(new Color(0, 0, 0, 150)); // Semi-transparent white
                g2d.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        Boolean state;
        state = keypad.buttonPressState;
        keypad.buttonPressState = false;
        MessagePanel.setLayout(new GridBagLayout());
        MessagePanel.setOpaque(false); // Required for transparency
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new java.awt.Insets(10, 10, 10, 10);
        c.weightx = 1.0;
        c.weighty = 10.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        MessagePanel.add(Box.createVerticalStrut(50), c);
        c.gridy = 1;
        MessagePanel.add(Box.createVerticalStrut(50), c);
        c.gridy = 2;
        MessagePanel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.BLACK, 3));
        MessagePanel.setOpaque(false);

        JLabel MessageLabel = new JLabel(message);
        MessageLabel.setHorizontalAlignment(JLabel.CENTER);
        MessageLabel.setForeground(java.awt.Color.WHITE);
        MessageLabel.setBackground(ATM.SCREEN_PANEL_COLOR);
        MessagePanel.add(MessageLabel, c);
        MessageLabel.setOpaque(true);
        c.gridy = 3;
        MessagePanel.add(Box.createVerticalStrut(50), c);
        JLabel OK = new JLabel("Press ENTER to continue", JLabel.CENTER);
        OK.setHorizontalAlignment(JLabel.CENTER);
        OK.setForeground(java.awt.Color.WHITE);
        OK.setBackground(ATM.SCREEN_PANEL_COLOR);
        OK.setOpaque(true);
        OK.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        c.gridy = 4;
        MessagePanel.add(OK, c);
        
        // Set bounds to cover the entire screen_panel area        
        atm.layeredPane.add(MessagePanel, JLayeredPane.POPUP_LAYER);
        atm.layeredPane.revalidate();
        atm.layeredPane.repaint();
        keypad.waitAction();
        atm.layeredPane.remove(MessagePanel);
        atm.layeredPane.revalidate();
        atm.layeredPane.repaint();
        keypad.buttonPressState = state;
    }

} // end class Screen



/**************************************************************************
 * (C) Copyright 1992-2007 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/
