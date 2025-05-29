package app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Autho extends JFrame {
private JTextField emailField;
private JPasswordField passwordField;
private JButton loginButton, signUpButton;
private JLabel forgotPasswordLabel;


static final String URL = "jdbc:mysql://localhost:3306/projet1";
static final String USER = "root";
static final String PASSWORD = "";

public Autho() {
    setTitle("Authentification");
    setSize(400, 500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setResizable(false);
    initializeUI();
    setVisible(true);
}

private void initializeUI() {
    JPanel panel = new JPanel();
    panel.setBackground(new Color(0x001F3F)); // Bleu foncé
    panel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    add(panel);

    // Titre
    JLabel title = new JLabel("Authentification");
    title.setFont(new Font("Segoe UI", Font.BOLD, 28));
    title.setForeground(Color.WHITE);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(20, 0, 20, 0);
    panel.add(title, gbc);

    // Label Email
    JLabel emailLabel = new JLabel("Email");
    emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    emailLabel.setForeground(Color.WHITE);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(10, 0, 5, 0);
    panel.add(emailLabel, gbc);

    // Champ Email
    emailField = new JTextField();
    emailField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.insets = new Insets(0, 20, 10, 20);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(emailField, gbc);

    // Label Mot de passe
    JLabel passwordLabel = new JLabel("Mot de passe");
    passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    passwordLabel.setForeground(Color.WHITE);
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.insets = new Insets(10, 0, 5, 0);
    gbc.fill = GridBagConstraints.NONE;
    panel.add(passwordLabel, gbc);

    // Champ Mot de passe
    passwordField = new JPasswordField();
    passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.insets = new Insets(0, 20, 10, 20);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(passwordField, gbc);

    // Bouton Connexion
    loginButton = new JButton("Connexion");
    loginButton.setBackground(new Color(0x03A9F4)); // Bleu clair
    loginButton.setForeground(Color.WHITE);
    loginButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
    loginButton.setFocusPainted(false);
    loginButton.setBorderPainted(false);
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(20, 20, 10, 20);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    panel.add(loginButton, gbc);

    // Bouton Inscription
    signUpButton = new JButton("S'inscrire");
    signUpButton.setBackground(new Color(0xFFC107)); // Jaune
    signUpButton.setForeground(Color.BLACK);
    signUpButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
    signUpButton.setFocusPainted(false);
    signUpButton.setBorderPainted(false);
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.insets = new Insets(10, 20, 20, 20);
    panel.add(signUpButton, gbc);

    // Label mot de passe oublié
    forgotPasswordLabel = new JLabel("Mot de passe oublié ?");
    forgotPasswordLabel.setForeground(Color.WHITE);
    forgotPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    forgotPasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    gbc.gridx = 0;
    gbc.gridy = 7;
    gbc.insets = new Insets(0, 0, 20, 0);
    gbc.fill = GridBagConstraints.NONE;
    panel.add(forgotPasswordLabel, gbc);

    // Actions
    loginButton.addActionListener(e -> handleLogin());
    signUpButton.addActionListener(e -> {
        new Inscription(); // Redirige vers Inscription
        dispose();
    });

    forgotPasswordLabel.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            handleForgotPassword();
        }
    });
}

private void handleLogin() {
    String email = emailField.getText().trim();
    String password = new String(passwordField.getPassword());

    if (email.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Veuillez remplir tous les champs", 
            "Erreur", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        if (isValidUser(email, password)) {
            JOptionPane.showMessageDialog(this, 
                "Connexion réussie.", 
                "Succès", 
                JOptionPane.INFORMATION_MESSAGE);
            new Role1(); // Redirige vers Role1
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Email ou mot de passe incorrect", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Erreur technique: " + e.getMessage(), 
            "Erreur", 
            JOptionPane.ERROR_MESSAGE);
    }
}

private boolean isValidUser(String email, String password) {
    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
        String query = "SELECT * FROM Authentification WHERE email = ? AND password = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, email);
        stmt.setString(2, password);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Erreur de connexion: " + e.getMessage(), 
            "Erreur", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
}

private void handleForgotPassword() {
    String email = JOptionPane.showInputDialog(this, 
        "Entrez votre adresse email :", 
        "Mot de passe oublié", 
        JOptionPane.PLAIN_MESSAGE);

    if (email == null || email.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Email non fourni.", 
            "Erreur", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
        String query = "SELECT password FROM Authentification WHERE email = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, email);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String password = rs.getString("password");

            JOptionPane.showMessageDialog(this, 
                "Votre mot de passe est : " + password, 
                "Mot de passe récupéré", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Aucun compte trouvé avec cet email.", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, 
            "Erreur de récupération: " + e.getMessage(), 
            "Erreur", 
            JOptionPane.ERROR_MESSAGE);
    }
}

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new Autho());
}


}