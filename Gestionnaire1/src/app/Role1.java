package app;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Role1 {
    private JFrame frame;
    private JPanel mainPanel;
    private java.util.List<JButton> roleButtons = new ArrayList<>();

    public Role1() {
        frame = new JFrame("Gestion Universitaire");
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame.setSize(1100, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 32, 63));
        headerPanel.setPreferredSize(new Dimension(1100, 60));

        JLabel headerLabel = new JLabel("Espace Personnel");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        headerPanel.add(headerLabel, BorderLayout.WEST);
        frame.add(headerPanel, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(230, 230, 250));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, 700));

        // Accueil button
        JButton accueilButton = createSidebarButton("Accueil");
        accueilButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        accueilButton.addActionListener(e -> {
            setActiveButton(accueilButton);
            showAccueilPanel();
        });
        roleButtons.add(accueilButton);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(accueilButton);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));

        // Other role buttons
        String[] roles = {"Encadrant", "Enseignant", "Chercheur", "Jury"};
        for (String role : roles) {
            JButton roleButton = createSidebarButton(role);
            roleButton.addActionListener(e -> {
                setActiveButton(roleButton);
                redirectAccordingToRole(role);
            });
            roleButtons.add(roleButton);
            sidebar.add(roleButton);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton logoutButton = createSidebarButton("Se Déconnecter");
        logoutButton.addActionListener(e -> {
            frame.dispose();
            new Autho(); // Classe d'authentification
        });
        sidebar.add(logoutButton);

        frame.add(sidebar, BorderLayout.WEST);

        // Main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);

        showAccueilPanel(); // Page par défaut
        setActiveButton(accueilButton);
        frame.setVisible(true);
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setFocusPainted(false);
        button.setBackground(new Color(0, 32, 63));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.getBackground().equals(new Color(0, 32, 63))) {
                    button.setBackground(new Color(0, 45, 80));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.getBackground().equals(Color.ORANGE)) {
                    button.setBackground(new Color(0, 32, 63));
                }
            }
        });

        return button;
    }

    private void setActiveButton(JButton activeButton) {
        for (JButton btn : roleButtons) {
            btn.setBackground(new Color(0, 32, 63));
        }
        activeButton.setBackground(Color.ORANGE);
    }

    private void redirectAccordingToRole(String role) {
        switch (role) {
            case "Encadrant":
                showEncadrantPanel();
                break;
            case "Enseignant":
                showEnseignantPanel();
                break;
            case "Chercheur":
                showChercheurPanel();
                break;
            case "Jury":
                showJuryPanel();
                break;
        }
    }

    private void showAccueilPanel() {
        mainPanel.removeAll();

        // Image de fond
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/images/ill.jpg"));
        Image img = backgroundIcon.getImage();
        Image newImg = img.getScaledInstance(frame.getWidth(), frame.getHeight(), Image.SCALE_SMOOTH);
        backgroundIcon = new ImageIcon(newImg);

        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        backgroundLabel.setLayout(null);

        // Message de bienvenue
        JLabel welcomeLabel = new JLabel("Bienvenue sur votre espace de gestion !");
        welcomeLabel.setBounds(300, 100, 600, 30);
        welcomeLabel.setFont(new Font("Verdana", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.BLACK);
        backgroundLabel.add(welcomeLabel);

        JLabel subLabel = new JLabel("Sélectionnez une section à gauche pour commencer.");
        subLabel.setBounds(320, 150, 500, 30);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subLabel.setForeground(Color.BLACK);
        backgroundLabel.add(subLabel);

        mainPanel.setLayout(null);
        mainPanel.add(backgroundLabel);
        mainPanel.repaint();
        mainPanel.revalidate();
    }

    private void showEncadrantPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        Encadrant encadrantPanel = new Encadrant();
        mainPanel.add(encadrantPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showEnseignantPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        Enseignant enseignantPanel = new Enseignant();
        mainPanel.add(enseignantPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showJuryPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        Deliberation juryPanel = new Deliberation();
        mainPanel.add(juryPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showChercheurPanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        Chercheur chercheurPanel = new Chercheur();
        mainPanel.add(chercheurPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Role1::new);
    }
}