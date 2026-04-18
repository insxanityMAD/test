/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package My_Form;

import My_Classes.DB_connect;
import java.sql.*;
import java.sql.Connection;
import javax.swing.JOptionPane;

/**
 *
 * @author Administrator
 */
public class ReturnBook extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ReturnBook.class.getName());

    /**
     * Creates new form NewJFrame
     */
    private boolean isLoading = false;

    public ReturnBook() {
        setUndecorated(true); // REQUIRED for opacity
        initComponents();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
//        
//        jTable1.getTableHeader().setPreferredSize(
//        new java.awt.Dimension(jTable1.getTableHeader().getWidth(), 50)
//    );
//           jTable1.getTableHeader().setFont(
//        jTable1.getTableHeader().getFont().deriveFont(18f)
//    );
//           
//           jTable1.setFont(jTable1.getFont().deriveFont(16f));
//      
        loadBorrowersToCombo();

    }

    public void loadBorrowersToCombo() {
        try {
            isLoading = true; // ← suppress action events

            Connection con = DB_connect.getConnection();
            String sql = "SELECT DISTINCT CONCAT(b.first_name,' ',b.last_name) AS name "
                    + "FROM transaction t "
                    + "JOIN borrower b ON t.borrower_id = b.borrower_id "
                    + "WHERE t.status = 'Borrowed'";

            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            cmbBorrowerName.removeAllItems();
            while (rs.next()) {
                cmbBorrowerName.addItem(rs.getString("name"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            isLoading = false; // ← always re-enable, even if exception occurs
        }
    }

    public void loadBooksToCombo(String borrowerName) {
        try {
            isLoading = true; // suppress cascade
            Connection con = DB_connect.getConnection();
            String sql = "SELECT DISTINCT bo.title "
                    + "FROM transaction t "
                    + "JOIN borrower b ON t.borrower_id = b.borrower_id "
                    + "JOIN book bo ON t.book_id = bo.book_id "
                    + "WHERE CONCAT(b.first_name,' ',b.last_name)=? "
                    + "AND t.status='Borrowed'";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, borrowerName);
            ResultSet rs = pst.executeQuery();
            cmbBook.removeAllItems();
            while (rs.next()) {
                cmbBook.addItem(rs.getString("title"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            isLoading = false; // always re-enable
        }
    }

    public void loadCopiesToCombo(String borrowerName, String bookTitle) {
        try {
            isLoading = true; // suppress cascade
            Connection con = DB_connect.getConnection();
            String sql = "SELECT bc.acquisition_number "
                    + "FROM transaction t "
                    + "JOIN borrower b ON t.borrower_id = b.borrower_id "
                    + "JOIN book bo ON t.book_id = bo.book_id "
                    + "JOIN book_copy bc ON t.copy_id = bc.copy_id "
                    + "WHERE CONCAT(b.first_name,' ',b.last_name)=? "
                    + "AND bo.title=? AND t.status='Borrowed'";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, borrowerName);
            pst.setString(2, bookTitle);
            ResultSet rs = pst.executeQuery();
            cmbAcquisitionNumber.removeAllItems();
            while (rs.next()) {
                cmbAcquisitionNumber.addItem(rs.getString("acquisition_number"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            isLoading = false; // always re-enable
        }
    }

    public void loadDates(String acquisition) {
        try {
            Connection con = DB_connect.getConnection();

            String sql = "SELECT rental_date, due_date "
                    + "FROM transaction t "
                    + "JOIN book_copy bc ON t.copy_id = bc.copy_id "
                    + "WHERE bc.acquisition_number=? AND t.status='Borrowed'";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, acquisition);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                rentalDate.setDate(rs.getDate("rental_date"));
                dueDate.setDate(rs.getDate("due_date"));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void resetUIState() {

        cmbBook.setEnabled(false);
        cmbAcquisitionNumber.setEnabled(false);
        btnReturnBook.setEnabled(false);

        cmbBook.removeAllItems();
        cmbAcquisitionNumber.removeAllItems();

        rentalDate.setDate(null);
        dueDate.setDate(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtMembers = new javax.swing.JLabel();
        txtTransactions = new javax.swing.JLabel();
        txtReports = new javax.swing.JLabel();
        txtLogout1 = new javax.swing.JLabel();
        txtDashboard = new javax.swing.JLabel();
        txtBooks = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        cmbBorrowerName = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        cmbBook = new javax.swing.JComboBox<>();
        cmbAcquisitionNumber = new javax.swing.JComboBox<>();
        btnReturnBook = new javax.swing.JButton();
        dueDate = new com.toedter.calendar.JDateChooser();
        rentalDate = new com.toedter.calendar.JDateChooser();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel3.setBackground(new java.awt.Color(204, 204, 255));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel1.setText("Library Inventory System");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/My_Image/Adobe_Express_-_file-removebg-preview.png"))); // NOI18N

        txtMembers.setBackground(new java.awt.Color(204, 204, 204));
        txtMembers.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtMembers.setText("Members");
        txtMembers.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtMembersMouseClicked(evt);
            }
        });

        txtTransactions.setBackground(new java.awt.Color(204, 204, 204));
        txtTransactions.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtTransactions.setText("Transactions");
        txtTransactions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtTransactionsMouseClicked(evt);
            }
        });

        txtReports.setBackground(new java.awt.Color(204, 204, 204));
        txtReports.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtReports.setText("Reports");
        txtReports.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtReportsMouseClicked(evt);
            }
        });

        txtLogout1.setBackground(new java.awt.Color(204, 204, 204));
        txtLogout1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtLogout1.setText("Logout");
        txtLogout1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtLogout1MouseClicked(evt);
            }
        });

        txtDashboard.setBackground(new java.awt.Color(255, 255, 255));
        txtDashboard.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtDashboard.setText("Dashboard");
        txtDashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtDashboardMouseClicked(evt);
            }
        });

        txtBooks.setBackground(new java.awt.Color(204, 204, 204));
        txtBooks.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtBooks.setText("Books");
        txtBooks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtBooksMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(120, 120, 120)
                .addComponent(txtDashboard)
                .addGap(30, 30, 30)
                .addComponent(txtBooks)
                .addGap(30, 30, 30)
                .addComponent(txtMembers)
                .addGap(30, 30, 30)
                .addComponent(txtTransactions)
                .addGap(30, 30, 30)
                .addComponent(txtReports)
                .addGap(30, 30, 30)
                .addComponent(txtLogout1)
                .addContainerGap(797, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtBooks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtMembers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtTransactions)
                                .addComponent(txtReports)
                                .addComponent(txtLogout1))
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 2289, -1));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setText("Acquisition Number");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 51));
        jLabel12.setText("Book");

        cmbBorrowerName.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cmbBorrowerName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbBorrowerName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBorrowerNameActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setText("Return Book");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 51, 51));
        jLabel15.setText("Borrower Name: ");

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 51));
        jLabel16.setText("Due Date: ");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(51, 51, 51));
        jLabel19.setText("Rental Date:");

        cmbBook.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cmbBook.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbBook.setEnabled(false);
        cmbBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBookActionPerformed(evt);
            }
        });

        cmbAcquisitionNumber.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cmbAcquisitionNumber.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbAcquisitionNumber.setEnabled(false);
        cmbAcquisitionNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbAcquisitionNumberActionPerformed(evt);
            }
        });

        btnReturnBook.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnReturnBook.setText("Return Book");
        btnReturnBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnBookActionPerformed(evt);
            }
        });

        dueDate.setDateFormatString("yyyy-MM-dd");
        dueDate.setEnabled(false);

        rentalDate.setDateFormatString("yyyy-MM-dd");
        rentalDate.setEnabled(false);

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setText("Back");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jButton1)
                .addGap(417, 417, 417)
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(200, 200, 200)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel15)
                            .addComponent(jLabel12)
                            .addComponent(cmbBook, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbAcquisitionNumber, 0, 301, Short.MAX_VALUE)
                            .addComponent(cmbBorrowerName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 155, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel16)
                            .addComponent(dueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rentalDate, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(203, 203, 203))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnReturnBook)
                .addGap(38, 38, 38))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(51, 51, 51)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(5, 5, 5)
                        .addComponent(cmbBorrowerName, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbBook, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rentalDate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(19, 19, 19)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbAcquisitionNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addComponent(btnReturnBook, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(204, 170, -1, -1));

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 164, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 185, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(1642, 860, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmbBorrowerNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBorrowerNameActionPerformed
        // TODO add your handling code here:
        if (isLoading) {
            return;
        }
        if (cmbBorrowerName.getSelectedItem() != null) {
            String name = cmbBorrowerName.getSelectedItem().toString();

            // reset lower levels first
            cmbBook.removeAllItems();
            cmbBook.setEnabled(false);
            cmbAcquisitionNumber.removeAllItems();
            cmbAcquisitionNumber.setEnabled(false);
            btnReturnBook.setEnabled(false);
            rentalDate.setDate(null);
            dueDate.setDate(null);

            loadBooksToCombo(name);
            cmbBook.setEnabled(true); // ✅ enable AFTER loading
        }
    }//GEN-LAST:event_cmbBorrowerNameActionPerformed

    private void cmbBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBookActionPerformed
        // TODO add your handling code here:

        if (isLoading) {
            return;
        }
        if (cmbBook.getSelectedItem() == null) {
            return;
        }

        String borrower = cmbBorrowerName.getSelectedItem().toString();
        String book = cmbBook.getSelectedItem().toString();

        // reset lower levels first
        cmbAcquisitionNumber.removeAllItems();
        cmbAcquisitionNumber.setEnabled(false);
        btnReturnBook.setEnabled(false);
        rentalDate.setDate(null);
        dueDate.setDate(null);

        loadCopiesToCombo(borrower, book);
        cmbAcquisitionNumber.setEnabled(true); // ✅ enable AFTER loading
    }//GEN-LAST:event_cmbBookActionPerformed

    private void cmbAcquisitionNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAcquisitionNumberActionPerformed
        // TODO add your handling code here:
        if (isLoading) {
            return;
        }
        if (cmbAcquisitionNumber.getSelectedItem() == null) {
            return;
        }

        loadDates(cmbAcquisitionNumber.getSelectedItem().toString());
        btnReturnBook.setEnabled(true); // ✅ only enabled after acquisition chosen
    }//GEN-LAST:event_cmbAcquisitionNumberActionPerformed

    private void btnReturnBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnBookActionPerformed
        // TODO add your handling code here:
        try {
            // ✅ VALIDATION
            if (cmbBorrowerName.getSelectedItem() == null
                    || cmbBook.getSelectedItem() == null
                    || cmbAcquisitionNumber.getSelectedItem() == null) {

                JOptionPane.showMessageDialog(this, "Please complete all selections!");
                return;
            }

            String borrower = cmbBorrowerName.getSelectedItem().toString();
            String book = cmbBook.getSelectedItem().toString();
            String acquisition = cmbAcquisitionNumber.getSelectedItem().toString();

            Connection con = DB_connect.getConnection();
            con.setAutoCommit(false);

            try {
                // ✅ GET TRANSACTION ID + DUE DATE + BORROWER_ID
                String getSql = "SELECT t.transaction_id, t.due_date, t.borrower_id "
                        + "FROM transaction t "
                        + "JOIN borrower b ON t.borrower_id = b.borrower_id "
                        + "JOIN book bo ON t.book_id = bo.book_id "
                        + "JOIN book_copy bc ON t.copy_id = bc.copy_id "
                        + "WHERE CONCAT(b.first_name,' ',b.last_name)=? "
                        + "AND bo.title=? "
                        + "AND bc.acquisition_number=? "
                        + "AND t.status='Borrowed'";

                PreparedStatement pstGet = con.prepareStatement(getSql);
                pstGet.setString(1, borrower);
                pstGet.setString(2, book);
                pstGet.setString(3, acquisition);

                ResultSet rs = pstGet.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Transaction not found!");
                    con.rollback();
                    return;
                }

                int transactionId = rs.getInt("transaction_id");
                int borrowerId = rs.getInt("borrower_id");
                Date due = rs.getDate("due_date");
                Date today = new Date(System.currentTimeMillis());

                // ✅ UPDATE TRANSACTION STATUS
                String updateTransaction = "UPDATE transaction SET status='Returned', returned_date=? WHERE transaction_id=?";
                PreparedStatement pstUpdate = con.prepareStatement(updateTransaction);
                pstUpdate.setDate(1, today);
                pstUpdate.setInt(2, transactionId);
                pstUpdate.executeUpdate();

                // ✅ UPDATE BOOK COPY STATUS
                String updateCopy = "UPDATE book_copy SET status='Available' WHERE acquisition_number=?";
                PreparedStatement pstCopy = con.prepareStatement(updateCopy);
                pstCopy.setString(1, acquisition);
                pstCopy.executeUpdate();

                // ✅ CALCULATE AND SAVE FINES IF OVERDUE (INLINE)
                long diff = today.getTime() - due.getTime();
                long daysLate = diff / (1000 * 60 * 60 * 24);

                if (daysLate > 0) {
                    double fineAmount = daysLate * 10.0; // 10 pesos per day

                    String insertFine = "INSERT INTO fine (transaction_id, borrower_id, amount, days_overdue, fine_date, status) "
                            + "VALUES (?, ?, ?, ?, ?, 'Unpaid')";
                    PreparedStatement pstFine = con.prepareStatement(insertFine);
                    pstFine.setInt(1, transactionId);
                    pstFine.setInt(2, borrowerId);
                    pstFine.setDouble(3, fineAmount);
                    pstFine.setLong(4, daysLate);
                    pstFine.setDate(5, today);
                    pstFine.executeUpdate();

                    con.commit();

                    JOptionPane.showMessageDialog(this,
                            "Book returned successfully!\n\nOVERDUE FINE APPLIED:\nDays late: " + daysLate
                            + "\nFine amount: " + fineAmount + " pesos");
                } else {
                    con.commit();
                    JOptionPane.showMessageDialog(this, "Book returned successfully!");
                }

                // ✅ REFRESH COMBOS
                resetUIState();
                loadBorrowersToCombo();

            } catch (Exception e) {
                con.rollback();
                throw e;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }//GEN-LAST:event_btnReturnBookActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        Transactions trans = new Transactions();
        trans.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtMembersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtMembersMouseClicked
        Members member = new Members();
        member.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_txtMembersMouseClicked

    private void txtTransactionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtTransactionsMouseClicked
        Transactions transaction = new Transactions();
        transaction.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_txtTransactionsMouseClicked

    private void txtReportsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtReportsMouseClicked
        Reports report = new Reports();
        report.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_txtReportsMouseClicked

    private void txtLogout1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtLogout1MouseClicked
        // TODO add your handling code here:
        // TODO add your handling code here:
        LoginForm login = new LoginForm();
        login.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_txtLogout1MouseClicked

    private void txtDashboardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtDashboardMouseClicked
        Dashboard dashboard = new Dashboard();
        dashboard.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_txtDashboardMouseClicked

    private void txtBooksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtBooksMouseClicked
        Books book = new Books(); // create instance
        book.setVisible(true); // show it
        this.dispose();
    }//GEN-LAST:event_txtBooksMouseClicked

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new ReturnBook().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReturnBook;
    private javax.swing.JComboBox<String> cmbAcquisitionNumber;
    private javax.swing.JComboBox<String> cmbBook;
    private javax.swing.JComboBox<String> cmbBorrowerName;
    private com.toedter.calendar.JDateChooser dueDate;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private com.toedter.calendar.JDateChooser rentalDate;
    private javax.swing.JLabel txtBooks;
    private javax.swing.JLabel txtDashboard;
    private javax.swing.JLabel txtLogout1;
    private javax.swing.JLabel txtMembers;
    private javax.swing.JLabel txtReports;
    private javax.swing.JLabel txtTransactions;
    // End of variables declaration//GEN-END:variables
}
