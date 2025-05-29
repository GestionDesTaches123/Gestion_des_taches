package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class WelcomeUI extends JFrame {

    private Image backgroundImage; // 👈 Attribut de classe

    public WelcomeUI() {
        setTitle("Bienvenue - Gestion des Tâches");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 👇 Charger l’image de fond dans l'attribut
        try {
           backgroundImage = ImageIO.read(getClass().getResourceAsStream("/images/ily.jpg"));
        } catch (IOException e) {
            System.out.println("Erreur de chargement de l'image : " + e.getMessage());
        }

        // Panel avec image de fond
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        // Overlay
        JPanel overlay = new JPanel();
        overlay.setBackground(new Color(0, 0, 0, 140));
        overlay.setLayout(new BoxLayout(overlay, BoxLayout.Y_AXIS));
        overlay.setBorder(BorderFactory.createEmptyBorder(80, 60, 60, 60));

        JLabel titre = new JLabel("Bienvenue dans votre plateforme");
        titre.setFont(new Font("SansSerif", Font.BOLD, 28));
        titre.setForeground(Color.WHITE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sousTitre = new JLabel("<html><div style='text-align:center;'>"
                + "Gérez vos cours, projets, recherches et délibérations<br>"
                + "dans un seul espace intelligent et moderne."
                + "</div></html>");
        sousTitre.setFont(new Font("SansSerif", Font.PLAIN, 16));
        sousTitre.setForeground(Color.WHITE);
        sousTitre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton commencerBtn = new JButton("Commencer ➜");
        commencerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        commencerBtn.setBackground(new Color(108, 99, 255));
        commencerBtn.setForeground(Color.WHITE);
        commencerBtn.setFocusPainted(false);
        commencerBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        commencerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        commencerBtn.addActionListener((ActionEvent e) -> {
            new Autho(); // Redirection
            dispose();   // Fermer la page actuelle
        });

        // Ajout composants dans overlay
        overlay.add(titre);
        overlay.add(Box.createVerticalStrut(20));
        overlay.add(sousTitre);
        overlay.add(Box.createVerticalStrut(30));
        overlay.add(commencerBtn);

        backgroundPanel.add(overlay);
        setContentPane(backgroundPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WelcomeUI().setVisible(true));
    }
}