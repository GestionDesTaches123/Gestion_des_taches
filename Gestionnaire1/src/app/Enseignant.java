package app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class Enseignant extends JPanel {

    private JTable coursTable;
    private DefaultTableModel tableModel;
    private static final String CSV_PATH = "cours.csv";

    public Enseignant() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(900, 500));

        // --- En-tête --- (Modifié avec bleu ciel)
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(173, 216, 230)); // Bleu ciel
        header.setPreferredSize(new Dimension(900, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));

       
        
        add(header, BorderLayout.NORTH);

        // --- Tableau de cours ---
        String[] colonnes = {"Code", "Intitulé", "Responsable", "Salle", "PDF du cours"};
        tableModel = new DefaultTableModel(colonnes, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        coursTable = new JTable(tableModel) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    c.setBackground(new Color(220, 240, 255));
                } else {
                    c.setBackground(Color.WHITE);
                }
                if (column == 4 && getValueAt(row, 4) != null && !getValueAt(row, 4).toString().isEmpty()) {
                    JLabel label = new JLabel("📄 Voir le PDF");
                    label.setForeground(new Color(0, 102, 204));
                    label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    return label;
                }
                return c;
            }
        };
        coursTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        coursTable.setRowHeight(25);
        coursTable.setGridColor(new Color(220, 220, 220));
        coursTable.setSelectionBackground(new Color(220, 240, 255));
        coursTable.setShowGrid(true);
        coursTable.setIntercellSpacing(new Dimension(0, 0));

        JScrollPane scrollPane = new JScrollPane(coursTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // --- Panneau des boutons ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

    

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setBackground(Color.WHITE);

        JButton btnAjouter = new JButton("AJOUTER");
        JButton btnModifier = new JButton("MODIFIER");
        JButton btnSupprimer = new JButton("SUPPRIMER");

        styleButton(btnAjouter, new Dimension(90, 28), new Color(0, 128, 0), Color.WHITE);
        styleButton(btnModifier, new Dimension(90, 28), new Color(220, 20, 60), Color.WHITE);
        styleButton(btnSupprimer, new Dimension(90, 28), new Color(139, 0, 0), Color.WHITE);

        actionPanel.add(btnAjouter);
        actionPanel.add(btnModifier);
        actionPanel.add(btnSupprimer);

      
        bottomPanel.add(Box.createHorizontalGlue());
        bottomPanel.add(actionPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        // === Actions ===
        btnAjouter.addActionListener(e -> ajouterCours());
        btnModifier.addActionListener(e -> modifierCours());
        btnSupprimer.addActionListener(e -> supprimerCours());
      

        coursTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = coursTable.rowAtPoint(e.getPoint());
                    if (coursTable.columnAtPoint(e.getPoint()) == 4 && row != -1) {
                        String path = (String) tableModel.getValueAt(row, 4);
                        if (path != null && !path.isEmpty()) {
                            try {
                                Desktop.getDesktop().open(new File(path));
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(null, "Impossible d'ouvrir le fichier PDF.");
                            }
                        }
                    }
                }
            }
        });

        chargerDepuisCSV();
    }

    private void styleButton(JButton button, Dimension size, Color bgColor, Color textColor) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setPreferredSize(size);
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        button.setFocusPainted(false);
    }

    private void ajouterCours() {
        JTextField code = new JTextField();
        JTextField intitule = new JTextField();
        JTextField prof = new JTextField();
        JTextField salle = new JTextField();
        JLabel lblPDF = new JLabel("Aucun fichier sélectionné");
        final String[] path = {""};

        JButton btnPDF = new JButton("📁 Choisir un PDF");
        btnPDF.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                path[0] = f.getAbsolutePath();
                lblPDF.setText(f.getName());
            }
        });

        Object[] fields = {"Code :", code, "Intitulé :", intitule, "Responsable :", prof, "Salle :", salle, btnPDF, lblPDF};
        if (JOptionPane.showConfirmDialog(this, fields, "Ajouter un cours", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            tableModel.addRow(new Object[]{code.getText(), intitule.getText(), prof.getText(), salle.getText(), path[0]});
            sauvegarderDansCSV();
        }
    }

    private void modifierCours() {
        int row = coursTable.getSelectedRow();
        if (row != -1) {
            JTextField code = new JTextField(tableModel.getValueAt(row, 0).toString());
            JTextField intitule = new JTextField(tableModel.getValueAt(row, 1).toString());
            JTextField prof = new JTextField(tableModel.getValueAt(row, 2).toString());
            JTextField salle = new JTextField(tableModel.getValueAt(row, 3).toString());
            JLabel lblPDF = new JLabel(new File(tableModel.getValueAt(row, 4).toString()).getName());
            final String[] path = {tableModel.getValueAt(row, 4).toString()};

            JButton btnPDF = new JButton("📁 Modifier PDF");
            btnPDF.addActionListener(e -> {
                JFileChooser fc = new JFileChooser();
                if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File f = fc.getSelectedFile();
                    path[0] = f.getAbsolutePath();
                    lblPDF.setText(f.getName());
                }
            });

            Object[] fields = {"Code :", code, "Intitulé :", intitule, "Responsable :", prof, "Salle :", salle, btnPDF, lblPDF};
            if (JOptionPane.showConfirmDialog(this, fields, "Modifier le cours", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                tableModel.setValueAt(code.getText(), row, 0);
                tableModel.setValueAt(intitule.getText(), row, 1);
                tableModel.setValueAt(prof.getText(), row, 2);
                tableModel.setValueAt(salle.getText(), row, 3);
                tableModel.setValueAt(path[0], row, 4);
                sauvegarderDansCSV();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un cours à modifier.");
        }
    }

    private void supprimerCours() {
        int row = coursTable.getSelectedRow();
        if (row != -1) {
            if (JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer ce cours ?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                tableModel.removeRow(row);
                sauvegarderDansCSV();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un cours à supprimer.");
        }
    }

    private void sauvegarderDansCSV() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_PATH))) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                StringBuilder row = new StringBuilder();
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    row.append("\"").append(tableModel.getValueAt(i, j).toString().replace("\"", "\"\"")).append("\"");
                    if (j < tableModel.getColumnCount() - 1) row.append(",");
                }
                writer.write(row.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde du fichier CSV.");
        }
    }

    private void chargerDepuisCSV() {
        File csvFile = new File(CSV_PATH);
        if (!csvFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(CSV_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ArrayList<String> values = new ArrayList<>();
                boolean insideQuote = false;
                StringBuilder field = new StringBuilder();
                for (char c : line.toCharArray()) {
                    if (c == '"') {
                        insideQuote = !insideQuote;
                    } else if (c == ',' && !insideQuote) {
                        values.add(field.toString());
                        field.setLength(0);
                    } else {
                        field.append(c);
                    }
                }
                values.add(field.toString());
                tableModel.addRow(values.toArray());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement du fichier CSV.");
        }
    }
}