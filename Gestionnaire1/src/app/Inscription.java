package app;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;

public class Inscription extends JFrame {
    private JTextField emailField, firstNameField, lastNameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JRadioButton maleRadio, femaleRadio;
    private JButton submitButton;

    static final String URL = "jdbc:mysql://localhost:3306/projet1";
    static final String USER = "root";
    static final String PASSWORD = "";

    public Inscription() {
        setTitle("Inscription Form");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0, 51, 102)); // bleu foncé
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Color labelColor = Color.WHITE; // blanc
        Color fieldBackground = new Color(245, 245, 245); // gris clair

        JLabel titleLabel = new JLabel("Inscription Form");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(labelColor);

        JLabel emailLabel = new JLabel("Email:");
        JLabel firstNameLabel = new JLabel("First Name:");
        JLabel lastNameLabel = new JLabel("Last Name:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JLabel genderLabel = new JLabel("Gender:");

        for (JLabel label : new JLabel[]{emailLabel, firstNameLabel, lastNameLabel, passwordLabel, confirmPasswordLabel, genderLabel}) {
            label.setForeground(labelColor);
        }

        emailField = new JTextField(15);
        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        confirmPasswordField = new JPasswordField(15);

        JTextField[] fields = {emailField, firstNameField, lastNameField, passwordField, confirmPasswordField};
        for (JTextField field : fields) {
            field.setBackground(fieldBackground);
        }

        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        maleRadio.setBackground(panel.getBackground());
        femaleRadio.setBackground(panel.getBackground());
        maleRadio.setForeground(labelColor);
        femaleRadio.setForeground(labelColor);

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);

        submitButton = new JButton("S'inscrire");
        submitButton.setBackground(new Color(255, 204, 0)); // jaune
        submitButton.setForeground(Color.BLACK); // texte noir
        submitButton.setFocusPainted(false);
        submitButton.setFont(new Font("Arial", Font.BOLD, 13));

        // Layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy++;
        panel.add(emailLabel, gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        panel.add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        panel.add(lastNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(confirmPasswordLabel, gbc);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(genderLabel, gbc);
        gbc.gridx = 1;
        panel.add(maleRadio, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        panel.add(femaleRadio, gbc);

        gbc.gridx = 1;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String gender = maleRadio.isSelected() ? "Male" : (femaleRadio.isSelected() ? "Female" : "Not selected");

                if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || gender.equals("Not selected")) {
                    JOptionPane.showMessageDialog(Inscription.this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(Inscription.this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    String sql = "INSERT INTO Authentification (email, firstname, password, lastname, gender) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, email);
                    stmt.setString(2, firstName);
                    stmt.setString(3, password);
                    stmt.setString(4, lastName);
                    stmt.setString(5, gender);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(Inscription.this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new Autho();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(Inscription.this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Inscription::new);
    }
}
