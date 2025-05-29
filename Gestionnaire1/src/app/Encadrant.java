package app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Encadrant extends JPanel {
    private JTable table;
    private JComboBox<String> niveauComboBox;
    private DefaultTableModel tableModel;
    private JSpinner dateSpinner;
    private JLabel notificationLabel;

    private List<Object[]> etudiants;

    private static final String CSV_FILE = "etudiants.csv";

    public Encadrant() {
        setBackground(new Color(173, 216, 230));
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        etudiants = getAllEtudiants();

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(173, 216, 230));

        niveauComboBox = new JComboBox<>(new String[]{"Tous niveaux", "Licence", "Master", "PhD"});
        niveauComboBox.addActionListener(e -> filterTable());

        JButton searchButton = new JButton("Rechercher");
        styleButton(searchButton, new Color(0, 102, 204));
        searchButton.addActionListener(e -> filterTable());

        topPanel.add(new JLabel("🔍 Filtres :"));
        topPanel.add(niveauComboBox);
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Étudiant", "Niveau", "Projet", "Avancement", "Réunion prévue avec"}, 0);
        table = new JTable(tableModel);
        refreshTable(etudiants);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(173, 216, 230));

        JPanel reunionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        reunionPanel.setBackground(new Color(173, 216, 230));
        reunionPanel.add(new JLabel("• Proch. réunion :"));

        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy HH:mm"));
        dateSpinner.setPreferredSize(new Dimension(150, 25));

        JButton notifyButton = new JButton("Planifier");
        styleButton(notifyButton, new Color(0, 153, 76));
        notifyButton.addActionListener(e -> planifierReunion());

        reunionPanel.add(dateSpinner);
        reunionPanel.add(notifyButton);
        bottomPanel.add(reunionPanel);

        notificationLabel = new JLabel(" ");
        notificationLabel.setForeground(Color.BLUE.darker());
        bottomPanel.add(notificationLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(173, 216, 230));

        JButton addButton = new JButton("➕ AJOUTER");
        styleButton(addButton, new Color(0, 102, 204));
        JButton editButton = new JButton("📝 MODIFIER");
        styleButton(editButton, new Color(204, 0, 0));
        JButton deleteButton = new JButton("🗑 SUPPRIMER");
        styleButton(deleteButton, new Color(153, 0, 0));

        addButton.addActionListener(e -> openStudentDialog(null));
        editButton.addActionListener(e -> editStudent());
        deleteButton.addActionListener(e -> deleteStudent());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        bottomPanel.add(buttonPanel);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private List<Object[]> getAllEtudiants() {
        List<Object[]> list = new ArrayList<>();
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE))) {
                bw.write("nom,niveau,projet,avancement,reunion\n"); // En-tête
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            boolean first = true;
            while ((line = br.readLine()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                String[] data = line.split(",", -1);
                list.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void saveAllEtudiants(List<Object[]> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE))) {
            bw.write("nom,niveau,projet,avancement,reunion\n");
            for (Object[] row : data) {
                String line = String.join(",", Arrays.stream(row).map(Object::toString).toArray(String[]::new));
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addEtudiant(Object[] newData) {
        etudiants.add(newData);
        saveAllEtudiants(etudiants);
    }

    private void updateEtudiant(String oldName, Object[] newData) {
        for (int i = 0; i < etudiants.size(); i++) {
            if (etudiants.get(i)[0].equals(oldName)) {
                etudiants.set(i, newData);
                break;
            }
        }
        saveAllEtudiants(etudiants);
    }

    private void deleteEtudiant(String nom) {
        etudiants.removeIf(row -> row[0].equals(nom));
        saveAllEtudiants(etudiants);
    }

    private void refreshTable(List<Object[]> data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    private void filterTable() {
        String selectedNiveau = (String) niveauComboBox.getSelectedItem();
        etudiants = getAllEtudiants();
        tableModel.setRowCount(0);
        for (Object[] etudiant : etudiants) {
            if ("Tous niveaux".equals(selectedNiveau) || etudiant[1].equals(selectedNiveau)) {
                tableModel.addRow(etudiant);
            }
        }
    }

    private void planifierReunion() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un étudiant.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nom = (String) table.getValueAt(selectedRow, 0);
        String withWhom = JOptionPane.showInputDialog(this, "Planifier une réunion avec :", nom);

        if (withWhom != null && !withWhom.trim().isEmpty()) {
            Date selectedDate = (Date) dateSpinner.getValue();
            String dateStr = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(selectedDate);
            String reunionText = withWhom + " (" + dateStr + ")";

            Object[] oldData = etudiants.get(selectedRow);
            Object[] newData = {oldData[0], oldData[1], oldData[2], oldData[3], reunionText};
            updateEtudiant(nom, newData);

            filterTable();
            notificationLabel.setText("📅 Réunion prévue avec " + withWhom + " le " + dateStr);
        }
    }

    private void openStudentDialog(Object[] existingData) {
        JTextField nameField = new JTextField();
        JTextField niveauField = new JTextField();
        JTextField projetField = new JTextField();
        JTextField avancementField = new JTextField();
        JTextField reunionField = new JTextField();

        if (existingData != null) {
            nameField.setText((String) existingData[0]);
            niveauField.setText((String) existingData[1]);
            projetField.setText((String) existingData[2]);
            avancementField.setText((String) existingData[3]);
            reunionField.setText((String) existingData[4]);
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nom étudiant :")); panel.add(nameField);
        panel.add(new JLabel("Niveau :")); panel.add(niveauField);
        panel.add(new JLabel("Projet :")); panel.add(projetField);
        panel.add(new JLabel("Avancement :")); panel.add(avancementField);
        panel.add(new JLabel("Réunion prévue avec :")); panel.add(reunionField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                existingData == null ? "Ajouter Étudiant" : "Modifier Étudiant",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Object[] newData = {
                    nameField.getText(), niveauField.getText(), projetField.getText(),
                    avancementField.getText(), reunionField.getText()
            };

            if (existingData == null) {
                addEtudiant(newData);
            } else {
                String oldName = (String) existingData[0];
                updateEtudiant(oldName, newData);
            }

            filterTable();
        }
    }

    private void editStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un étudiant.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        openStudentDialog(etudiants.get(row));
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un étudiant à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nom = (String) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            deleteEtudiant(nom);
            filterTable();
        }
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }
}