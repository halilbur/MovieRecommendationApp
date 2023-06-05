/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * @author halil
 */
public class MovieRecommendationDesign extends javax.swing.JFrame {

    private static final String moviesFile = "/movies.csv";
    private static final String targetUserFile = "/target_user.csv";


    private static Double[] userVector;
    private static DefaultListModel<String> listModel_tab2 = new DefaultListModel<>();
    private static DefaultListModel<String> listModel_tab1 = new DefaultListModel<>();

    /**
     * Creates new form AppDesign
     */
    public MovieRecommendationDesign() {
        initComponents();
        vectorCreator();
        readUserIDs(targetUserFile);
        fillComboBox();
    }

    public void vectorCreator() {
        userVector = new Double[9018];
        Arrays.fill(userVector, 0.0);
    }

    public void updateUserVectorMulti() {
        updateUserVector(cmbx_movieSet1, txt_movieSet1);
        updateUserVector(cmbx_movieSet2, txt_movieSet2);
        updateUserVector(cmbx_movieSet3, txt_movieSet3);
        updateUserVector(cmbx_movieSet4, txt_movieSet4);
        updateUserVector(cmbx_movieSet5, txt_movieSet5);

    }

    // this function for create a user vector with giving rating in text field and movie id in combo box.
    // Store that user in double array
    // you can use this function for all 5 combo box and text field
    // also you need to use getMovieIdByName function to get movie id from movie name
    // fill the unused index with 0.0
    public void updateUserVector(JComboBox cmbx_movieSet, JTextField txt_movieSet) {
        String movieName = cmbx_movieSet.getSelectedItem().toString();
        int movieId = CSVRead.getMovieIdByName(movieName);

        double rating = Double.parseDouble(txt_movieSet.getText());
        userVector[movieId] = rating;
    }

    //function for filling the combo box with the 10 random movies in tab2 section
    public void fillComboBox() {
        List<String> movieNames = new ArrayList<>();
        try (CSVReader reader = new CSVReader(CSVRead.getBufferedReader(moviesFile))) {
            String[] nextLine;
            boolean isFirstLine = true; // Flag to track the first line (headers)
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the first line
                }
                String movieName = nextLine[1]; // Get the value from the first column (index 0)
                movieNames.add(movieName);
            }
            if (movieNames.size() == 0) {
                System.out.println("No movies found.");
            }

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        //fill the combo box with the movie names
        for (int i = 0; i < 10; i++) {
            int randomIndex = (int) (Math.random() * movieNames.size());
            cmbx_movieSet1.addItem(movieNames.get(randomIndex));
            cmbx_movieSet2.addItem(movieNames.get(randomIndex + 1));
            cmbx_movieSet3.addItem(movieNames.get(randomIndex + 2));
            cmbx_movieSet4.addItem(movieNames.get(randomIndex + 3));
            cmbx_movieSet5.addItem(movieNames.get(randomIndex + 4));
        }
    }

    public void getRecommendationsOnTab2() {
        if (!Pattern.matches("[0-9]+", txt_tab2_K.getText()) || !Pattern.matches("[0-9]+", txt_tab2_X.getText())) {
            JOptionPane.showMessageDialog(null, "Please enter an integer value for K and X.");
            return;
        }
        int K_value = Integer.parseInt(txt_tab2_K.getText());
        int X_value = Integer.parseInt(txt_tab2_X.getText());
        int[] mostSimilarUserIds = CSVRead.calculateCosineSimilarityBetweenUsers(userVector, K_value);
        DefaultListModel<String> listModel = new DefaultListModel<>();

        for (int mostSimilarUserId : mostSimilarUserIds) {
            int[] topRatedMovies = CSVRead.getTopRatedMoviesForUser(mostSimilarUserId, X_value);
            // add the movie names to the list
            for (int movieId : topRatedMovies) {
                String movieName = CSVRead.getMovieNameById(movieId);
                if (movieName != null) {
                    listModel_tab2.addElement(movieName);
                } else {
                    listModel_tab2.addElement("Movie ID not found.");
                }
            }
        }
        list_reco_tab2.setModel(listModel_tab2);
    }

    public void getRecommendations() {
        if (!Pattern.matches("[0-9]+", txt_K.getText()) || !Pattern.matches("[0-9]+", txt_X.getText())) {
            JOptionPane.showMessageDialog(null, "Please enter an integer value for K and X.");
            return;
        }
        String targetUserId = (cmbx_targetUser.getSelectedItem().toString());
        int targetUserIdInt = Integer.parseInt(targetUserId);


        int K_value = Integer.parseInt(txt_K.getText());
        int X_value = Integer.parseInt(txt_X.getText());

        int[] mostSimilarUserIds = CSVRead.calculateCosineSimilarityBetweenUsers(targetUserIdInt, K_value);

        for (int mostSimilarUserId : mostSimilarUserIds) {
            int[] topRatedMovies = CSVRead.getTopRatedMoviesForUser(mostSimilarUserId, X_value);
            // add the movie names to the list
            for (int movieId : topRatedMovies) {
                String movieName = CSVRead.getMovieNameById(movieId);
                if (movieName != null) {
                    listModel_tab1.addElement(movieName);
                } else {
                    listModel_tab1.addElement("Movie ID not found.");
                }
            }
        }
        list_reco.setModel(listModel_tab1);
    }

    public void readUserIDs(String filename) {
        List<String> userIds = new ArrayList<>();
        try (CSVReader reader = new CSVReader(CSVRead.getBufferedReader(filename))) {
            String[] nextLine;
            boolean isFirstLine = true; // Flag to track the first line (headers)
            while ((nextLine = reader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip the first line
                }
                String userId = nextLine[0]; // Get the value from the first column (index 0)
                userIds.add(userId);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        cmbx_targetUser.setModel(new javax.swing.DefaultComboBoxModel<>(userIds.toArray(new String[0])));
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        tabPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cmbx_targetUser = new javax.swing.JComboBox<>();
        txt_K = new javax.swing.JTextField();
        txt_X = new javax.swing.JTextField();
        btn_reco = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        list_reco = new javax.swing.JList<>();
        tabPanel2 = new javax.swing.JPanel();
        cmbx_movieSet1 = new javax.swing.JComboBox<>();
        cmbx_movieSet2 = new javax.swing.JComboBox<>();
        cmbx_movieSet3 = new javax.swing.JComboBox<>();
        cmbx_movieSet4 = new javax.swing.JComboBox<>();
        cmbx_movieSet5 = new javax.swing.JComboBox<>();
        txt_tab2_K = new javax.swing.JTextField();
        txt_movieSet1 = new javax.swing.JTextField();
        txt_movieSet2 = new javax.swing.JTextField();
        txt_movieSet3 = new javax.swing.JTextField();
        txt_movieSet4 = new javax.swing.JTextField();
        txt_movieSet5 = new javax.swing.JTextField();
        txt_tab2_X = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btn_recoTab2 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        list_reco_tab2 = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(734, 605));
        setResizable(false);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        tabPanel1.setBackground(new java.awt.Color(102, 153, 255));
        tabPanel1.setPreferredSize(new java.awt.Dimension(734, 605));
        tabPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        jLabel1.setText("Top X * K Recommendations:");
        tabPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 190, 33));

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        jLabel2.setText("Target User:");
        tabPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 83, 33));

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        jLabel3.setText("X:");
        tabPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 20, 30, 33));

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        jLabel4.setText("K:");
        tabPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 20, 40, 33));

        cmbx_targetUser.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));
        tabPanel1.add(cmbx_targetUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 22, 100, 30));
        tabPanel1.add(txt_K, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 20, 120, 30));
        tabPanel1.add(txt_X, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 20, 120, 30));

        btn_reco.setText("Get Recommendations");
        btn_reco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recoActionPerformed(evt);
            }
        });
        tabPanel1.add(btn_reco, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 20, 190, 30));

        list_reco.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        jScrollPane1.setViewportView(list_reco);

        tabPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 680, 410));

        jTabbedPane1.addTab("Part A", tabPanel1);

        tabPanel2.setBackground(new java.awt.Color(204, 255, 153));
        tabPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tabPanel2.add(cmbx_movieSet1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 160, 31));

        tabPanel2.add(cmbx_movieSet2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 160, 31));

        tabPanel2.add(cmbx_movieSet3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 160, 31));

        tabPanel2.add(cmbx_movieSet4, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 160, 31));

        tabPanel2.add(cmbx_movieSet5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 160, 31));
        tabPanel2.add(txt_tab2_K, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 260, 150, 30));
        tabPanel2.add(txt_movieSet1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 20, 150, 30));
        tabPanel2.add(txt_movieSet2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 60, 150, 30));
        tabPanel2.add(txt_movieSet3, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 100, 150, 30));
        tabPanel2.add(txt_movieSet4, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 140, 150, 30));
        tabPanel2.add(txt_movieSet5, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 180, 150, 30));
        tabPanel2.add(txt_tab2_X, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 220, 150, 30));

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        jLabel5.setText("X:");
        tabPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 220, 30, 33));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        jLabel6.setText("K:");
        tabPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 260, 40, 33));

        btn_recoTab2.setText("Get Recommendations");
        btn_recoTab2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recoTab2ActionPerformed(evt);
            }
        });
        tabPanel2.add(btn_recoTab2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 300, 300, 30));

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        jLabel7.setText("Recommendations:");
        tabPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 20, -1, -1));

        list_reco_tab2.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 14)); // NOI18N
        jScrollPane2.setViewportView(list_reco_tab2);

        tabPanel2.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 50, 370, 470));

        jTabbedPane1.addTab("Part B", tabPanel2);

        getContentPane().add(jTabbedPane1);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btn_recoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recoActionPerformed
        // TODO add your handling code here:
        listModel_tab1.removeAllElements();
        if (txt_X.getText().equals("") || txt_K.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Please fill all the fields");
            listModel_tab1.removeAllElements();
            return;
        }
        getRecommendations();
    }//GEN-LAST:event_btn_recoActionPerformed

    private void btn_recoTab2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recoTab2ActionPerformed
        // TODO add your handling code here:
        listModel_tab2.removeAllElements();
        if (txt_tab2_X.getText().equals("") || txt_tab2_K.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Please fill all the fields");
            return;
        }
        if (!Pattern.matches("^[0-5]$", txt_movieSet1.getText())
                || !Pattern.matches("^[0-5]$", txt_movieSet2.getText())
                || !Pattern.matches("^[0-5]$", txt_movieSet3.getText())
                || !Pattern.matches("^[0-5]$", txt_movieSet4.getText())
                || !Pattern.matches("^[0-5]$", txt_movieSet5.getText())) {
            JOptionPane.showMessageDialog(null, "Please enter a valid rating between 0 and 5");

            listModel_tab2.removeAllElements();
            return;
        }
        updateUserVectorMulti();
        getRecommendationsOnTab2();

    }//GEN-LAST:event_btn_recoTab2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MovieRecommendationDesign.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MovieRecommendationDesign.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MovieRecommendationDesign.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MovieRecommendationDesign.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MovieRecommendationDesign().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_reco;
    private javax.swing.JButton btn_recoTab2;
    private javax.swing.JComboBox<String> cmbx_movieSet1;
    private javax.swing.JComboBox<String> cmbx_movieSet2;
    private javax.swing.JComboBox<String> cmbx_movieSet3;
    private javax.swing.JComboBox<String> cmbx_movieSet4;
    private javax.swing.JComboBox<String> cmbx_movieSet5;
    private javax.swing.JComboBox<String> cmbx_targetUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JList<String> list_reco;
    private javax.swing.JList<String> list_reco_tab2;
    private javax.swing.JPanel tabPanel1;
    private javax.swing.JPanel tabPanel2;
    private javax.swing.JTextField txt_K;
    private javax.swing.JTextField txt_X;
    private javax.swing.JTextField txt_movieSet1;
    private javax.swing.JTextField txt_movieSet2;
    private javax.swing.JTextField txt_movieSet3;
    private javax.swing.JTextField txt_movieSet4;
    private javax.swing.JTextField txt_movieSet5;
    private javax.swing.JTextField txt_tab2_K;
    private javax.swing.JTextField txt_tab2_X;
    // End of variables declaration//GEN-END:variables
}
