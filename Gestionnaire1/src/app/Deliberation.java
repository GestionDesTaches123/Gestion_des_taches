package app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Deliberation extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<String> niveauComboBox;
    private List<Object[]> deliberations;

    private static final String CSV_FILE = "deliberations.csv";

    public Deliberation() {
        setLayout(new BorderLayout());
        setBackground(new Color(173, 216, 230));
        initComponents();
    }

    private void initComponents() {
        deliberations = loadDeliberations();

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBackground(new Color(173, 216, 230));
        topPanel.add(new JLabel("🎓 Niveau :"));

        niveauComboBox = new JComboBox<>(new String[]{"Tous", "Licence", "Master", "PhD"});
        niveauComboBox.addActionListener(e -> filterTable());
        topPanel.add(niveauComboBox);

        JButton searchBtn = new JButton("🔍 Filtrer");
        styleButton(searchBtn, new Color(0, 102, 204));
        searchBtn.addActionListener(e -> filterTable());
        topPanel.add(searchBtn);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Étudiant", "Niveau", "Note Projet", "Note Présentation", "Moyenne"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshTable(deliberations);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(255, 255, 204));

        JButton addBtn = new JButton("➕ Ajouter");
        JButton editBtn = new JButton("📝 Modifier");
        JButton deleteBtn = new JButton("🗑 Supprimer");

        styleButton(addBtn, new Color(0, 153, 76));
        styleButton(editBtn, new Color(204, 102, 0));
        styleButton(deleteBtn, new Color(204, 0, 0));

        addBtn.addActionListener(e -> openDialog(null));
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private List<Object[]> loadDeliberations() {
        List<Object[]> list = new ArrayList<>();
        File file = new File(CSV_FILE);
        if (!file.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("nom,niveau,note_projet,note_presentation,moyenne\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                String[] parts = line.split(",", -1);
                list.add(parts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void saveDeliberations() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CSV_FILE))) {
            bw.write("nom,niveau,note_projet,note_presentation,moyenne\n");
            for (Object[] row : deliberations) {
                String line = String.join(",", Arrays.stream(row).map(Object::toString).toArray(String[]::new));
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshTable(List<Object[]> data) {
        tableModel.setRowCount(0);
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }

    private void filterTable() {
        String niveau = (String) niveauComboBox.getSelectedItem();
        List<Object[]> filtered = new ArrayList<>();
        for (Object[] row : deliberations) {
            if ("Tous".equals(niveau) || row[1].equals(niveau)) {
                filtered.add(row);
            }
        }
        refreshTable(filtered);
    }

    private void openDialog(Object[] existingData) {
        JTextField nomField = new JTextField();
        JTextField niveauField = new JTextField();
        JTextField noteProjetField = new JTextField();
        JTextField notePresField = new JTextField();

        if (existingData != null) {
            nomField.setText((String) existingData[0]);
            niveauField.setText((String) existingData[1]);
            noteProjetField.setText((String) existingData[2]);
            notePresField.setText((String) existingData[3]);
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Nom :")); panel.add(nomField);
        panel.add(new JLabel("Niveau :")); panel.add(niveauField);
        panel.add(new JLabel("Note Projet :")); panel.add(noteProjetField);
        panel.add(new JLabel("Note Présentation :")); panel.add(notePresField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                existingData == null ? "Ajouter Délibération" : "Modifier Délibération",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double n1 = Double.parseDouble(noteProjetField.getText());
                double n2 = Double.parseDouble(notePresField.getText());
                double moyenne = (n1 + n2) / 2.0;

                Object[] newRow = {
                        nomField.getText(), niveauField.getText(),
                        noteProjetField.getText(), notePresField.getText(),
                        String.format("%.2f", moyenne)
                };

                if (existingData != null) {
                    for (int i = 0; i < deliberations.size(); i++) {
                        if (deliberations.get(i)[0].equals(existingData[0])) {
                            deliberations.set(i, newRow);
                            break;
                        }
                    }
                } else {
                    deliberations.add(newRow);
                }

                saveDeliberations();
                filterTable();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer des notes valides.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une ligne à modifier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        openDialog(deliberations.get(row));
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une ligne à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nom = (String) table.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Supprimer " + nom + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            deliberations.removeIf(r -> r[0].equals(nom));
            saveDeliberations();
            filterTable();
        }
    }

    private void styleButton(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Délibérations");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new Deliberation());
            frame.setVisible(true);
        });
    }
}