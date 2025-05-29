package app;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;

public class Chercheur extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JComboBox<String> filtre1, filtre2, filtre3;
    private ArrayList<Object[]> allProjects = new ArrayList<>();
    private final String FILE_NAME = "projets.csv";

    public Chercheur() {
        setLayout(new BorderLayout());
        setBackground(new Color(173, 216, 230));

        // Titre en haut
        JLabel titleLabel = new JLabel("PROJETS DE RECHERCHE", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);

        // Panel central avec filtres et table
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.setBackground(new Color(173, 216, 230));
        add(centerPanel, BorderLayout.CENTER);

        // Panel filtres en haut du centrePanel
        JPanel filtrePanel = new JPanel();
        filtrePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        filtrePanel.setBackground(new Color(173, 216, 230));

        JLabel filterLabel = new JLabel("🔍 Filtres :");
        filtrePanel.add(filterLabel);

        filtre1 = new JComboBox<>(new String[]{"non defini", "Informatique", "Intelligence Artificielle", "Génie Civil", "Cybersécurité", "Big Data", "Physique appliquée"});
        filtrePanel.add(filtre1);

        filtre2 = new JComboBox<>(new String[]{"non defini", "En cours", "Terminé"});
        filtrePanel.add(filtre2);

        filtre3 = new JComboBox<>(new String[]{
                "non defini", "Université Mohammed V – Rabat", "Université Hassan II – Casablanca",
                "Université Cadi Ayyad – Marrakech", "Université Ibn Zohr – Agadir",
                "UM6P Ventures", "OCP Group", "Cosumar"
        });
        filtrePanel.add(filtre3);

        JButton searchButton = new JButton("🔍 RECHERCHER");
        filtrePanel.add(searchButton);

        centerPanel.add(filtrePanel, BorderLayout.NORTH);

        // Table dans le centre
        String[] columnNames = {"Sélectionner", "Projet", "Statut", "Équipe", "Membres", "Université"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : super.getColumnClass(columnIndex);
            }
        };
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel boutons en bas
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setBackground(new Color(173, 216, 230));
        add(buttonPanel, BorderLayout.SOUTH);

        JButton addButton = new JButton("➕ NOUVEAU PROJET");
        addButton.setBackground(new Color(0, 153, 0));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(addButton);

        JButton deleteButton = new JButton("🗑 SUPPRIMER PROJET");
        deleteButton.setBackground(new Color(255, 69, 0));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        buttonPanel.add(deleteButton);

        // Charger depuis fichier et afficher
        chargerDepuisFichier();
        actualiserTableau();

        // Actions
        addButton.addActionListener((ActionEvent e) -> {
            JTextField nomProjet = new JTextField();
            JTextField statut = new JTextField();
            JTextField nbMembres = new JTextField();
            JTextField nomsMembres = new JTextField();
            JTextField universite = new JTextField();
            JTextField filiere = new JTextField();

            JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
            panel.add(new JLabel("Nom du projet :"));
            panel.add(nomProjet);
            panel.add(new JLabel("Statut (En cours / Terminé) :"));
            panel.add(statut);
            panel.add(new JLabel("Nombre de membres :"));
            panel.add(nbMembres);
            panel.add(new JLabel("Noms des membres (séparés par virgule) :"));
            panel.add(nomsMembres);
            panel.add(new JLabel("Université / Organisation :"));
            panel.add(universite);
            panel.add(new JLabel("Nouvelle filière (facultatif) :"));
            panel.add(filiere);

            int result = JOptionPane.showConfirmDialog(this, panel, "Ajouter un nouveau projet",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String nom = nomProjet.getText().trim();
                String s = statut.getText().trim();
                String equipe = nbMembres.getText().trim() + " membres";
                String noms = nomsMembres.getText().trim();
                String univ = universite.getText().trim();
                String newFiliere = filiere.getText().trim();

                if (!nom.isEmpty() && !s.isEmpty() && !nbMembres.getText().trim().isEmpty() && !univ.isEmpty()) {
                    Object[] projet = new Object[]{false, nom, s, equipe, noms, univ};
                    allProjects.add(projet);
                    sauvegarderProjet(projet);
                    actualiserTableau();

                    if (!isItemInComboBox(filtre3, univ)) {
                        filtre3.addItem(univ);
                    }

                    if (!newFiliere.isEmpty() && !isItemInComboBox(filtre1, newFiliere)) {
                        filtre1.addItem(newFiliere);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteButton.addActionListener(e -> {
            ArrayList<Object[]> toRemove = new ArrayList<>();
            for (int i = 0; i < model.getRowCount(); i++) {
                Boolean selected = (Boolean) model.getValueAt(i, 0);
                if (selected != null && selected) {
                    toRemove.add(allProjects.get(i));
                }
            }
            allProjects.removeAll(toRemove);
            sauvegarderTous();
            actualiserTableau();
        });

        searchButton.addActionListener(e -> actualiserTableau());
    }

    private void chargerDepuisFichier() {
        allProjects.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] parts = ligne.split(";", -1);
                if (parts.length == 5) {
                    Object[] projet = new Object[]{false, parts[0], parts[1], parts[2], parts[3], parts[4]};
                    allProjects.add(projet);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sauvegarderProjet(Object[] projet) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(projet[1] + ";" + projet[2] + ";" + projet[3] + ";" + projet[4] + ";" + projet[5]);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sauvegarderTous() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Object[] projet : allProjects) {
                writer.write(projet[1] + ";" + projet[2] + ";" + projet[3] + ";" + projet[4] + ";" + projet[5]);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void actualiserTableau() {
        model.setRowCount(0);
        String domaine = (String) filtre1.getSelectedItem();
        String statut = (String) filtre2.getSelectedItem();
        String universite = (String) filtre3.getSelectedItem();

        for (Object[] projet : allProjects) {
            boolean matchDomaine = domaine.equals("non defini") || projet[1].toString().equalsIgnoreCase(domaine);
            boolean matchStatut = statut.equals("non defini") || projet[2].toString().equalsIgnoreCase(statut);
            boolean matchUniv = universite.equals("non defini") || projet[5].toString().contains(universite);

            if (matchDomaine && matchStatut && matchUniv) {
                model.addRow(projet);
            }
        }
    }

    private boolean isItemInComboBox(JComboBox<String> comboBox, String item) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(item)) {
                return true;
            }
        }
        return false;
    }
}
