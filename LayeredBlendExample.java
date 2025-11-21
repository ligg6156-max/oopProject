import java.awt.*;
import javax.swing.*;

public class LayeredBlendExample extends JFrame {

    public LayeredBlendExample() {
        setTitle("Liquid Glass Effect - Glassmorphism");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        // Create a JLayeredPane and set layout to null for manual positioning/sizing
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(600, 400));

        // 1. OUTER LAYER: Background panel with colorful gradient/pattern
        JPanel outerBackground = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                // Paint a vibrant gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 100, 180),
                    getWidth(), getHeight(), new Color(100, 150, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add some decorative circles
                g2d.setColor(new Color(255, 200, 100, 80));
                g2d.fillOval(50, 50, 150, 150);
                g2d.setColor(new Color(100, 255, 200, 80));
                g2d.fillOval(400, 200, 120, 120);
            }
        };
        outerBackground.setBounds(0, 0, 600, 400);
        layeredPane.add(outerBackground, Integer.valueOf(0)); // Layer 0

        // 2. LIQUID GLASS PANEL 1 (only this one has liquid glass effect)
        GlassPanel glassPanel1 = new GlassPanel("Liquid Glass Effect", new Color(255, 255, 255, 40));
        glassPanel1.setBounds(80, 80, 350, 150);
        layeredPane.add(glassPanel1, Integer.valueOf(100));

        // 3. REGULAR PANEL 2 (no glass effect)
        JPanel glassPanel2 = new JPanel();
        glassPanel2.setBounds(200, 180, 320, 140);
        glassPanel2.setOpaque(true);
        glassPanel2.setBackground(new Color(255, 200, 200, 200));
        glassPanel2.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        JLabel label2 = new JLabel("Regular Panel", SwingConstants.CENTER);
        label2.setFont(new Font("Arial", Font.BOLD, 22));
        glassPanel2.add(label2);
        layeredPane.add(glassPanel2, Integer.valueOf(110));

        // 4. TOP REGULAR CARD (no glass effect)
        JPanel topCard = new JPanel();
        topCard.setBounds(150, 30, 300, 80);
        topCard.setOpaque(true);
        topCard.setBackground(new Color(200, 255, 200, 200));
        topCard.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        JLabel labelTop = new JLabel("Regular Top Card", SwingConstants.CENTER);
        labelTop.setFont(new Font("Arial", Font.BOLD, 22));
        topCard.add(labelTop);
        layeredPane.add(topCard, Integer.valueOf(120));

        // Add the layered pane to the frame
        add(layeredPane);
        pack();
    }
    
    // Custom Glass Panel with liquid glass/glassmorphism effect
    class GlassPanel extends JPanel {
        private String text;
        private Color glassColor;
        
        public GlassPanel(String text, Color glassColor) {
            this.text = text;
            this.glassColor = glassColor;
            setOpaque(false);
            setLayout(new BorderLayout());
            
            JLabel label = new JLabel(text, SwingConstants.CENTER);
            label.setOpaque(false);
            label.setForeground(new Color(255, 255, 255, 220));
            label.setFont(new Font("Arial", Font.BOLD, 22));
            add(label, BorderLayout.CENTER);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Create rounded rectangle for glass effect
            int arc = 30; // Rounded corners
            
            // 1. Paint the glass background (semi-transparent white)
            g2d.setColor(glassColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            
            // 2. Add subtle gradient overlay for depth
            GradientPaint glassGradient = new GradientPaint(
                0, 0, new Color(255, 255, 255, 60),
                0, getHeight(), new Color(255, 255, 255, 10)
            );
            g2d.setPaint(glassGradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            
            // 3. Add glass border (slightly brighter)
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, arc, arc);
            
            // 4. Add inner highlight (top-left shine)
            g2d.setColor(new Color(255, 255, 255, 80));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawRoundRect(3, 3, getWidth() - 6, getHeight() / 2, arc - 5, arc - 5);
            
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        // Run the GUI in the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LayeredBlendExample().setVisible(true);
        });
    }
}
