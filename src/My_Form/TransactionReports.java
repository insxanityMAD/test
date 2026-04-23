/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package My_Form;

import javax.swing.JOptionPane;
import java.sql.*;
import My_Classes.DB_connect;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import javax.swing.JComboBox;

/**
 *
 * @author Administrator
 */
public class TransactionReports extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TransactionReports.class.getName());

    /**
     * Creates new form NewJFrame
     */
    private boolean isLoading = false;
    private String currentView = "current_loans";

    public TransactionReports() {
        setUndecorated(true); // REQUIRED for opacity
        initComponents();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        tblModel.getTableHeader().setPreferredSize(
                new java.awt.Dimension(tblModel.getTableHeader().getWidth(), 50)
        );
        tblModel.getTableHeader().setFont(
                tblModel.getTableHeader().getFont().deriveFont(24f)
        );

        tblModel.setFont(tblModel.getFont().deriveFont(18f));
        tblModel.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 24));

        // 2. Increase the row height so the large text isn't cut off
        tblModel.setRowHeight(35);

        // Load Current Loans by default on startup
        loadCurrentLoans();
// 
    }

    private DefaultTableModel getEmptyModel(String[] columns) {
        return new DefaultTableModel(new Object[][]{}, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void loadCurrentLoans() {
        currentView = "current_loans";
        jLabel10.setText("Current Loans");

        String[] columns = {"Trans ID", "Borrower", "Book Title", "Acc. #", "Rental Date", "Due Date", "Days Left", "Status"};
        DefaultTableModel model = getEmptyModel(columns);
        tblModel.setModel(model);

        String sql = """
        SELECT
            t.transaction_id,
            CONCAT(m.first_name, ' ', m.last_name) AS borrower,
            b.title,
            bc.acquisition_number,
            t.rental_date,
            t.due_date,
            t.status
        FROM transaction t
        JOIN borrower m ON t.borrower_id = m.borrower_id
        JOIN book b ON t.book_id = b.book_id
        JOIN book_copy bc ON t.copy_id = bc.copy_id
        WHERE t.status = 'Borrowed'
        ORDER BY t.due_date ASC
        """;

        try (Connection conn = DB_connect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            while (rs.next()) {
                java.sql.Date due = rs.getDate("due_date");

                long daysLeft;

                if (due != null) {

                    if (due.before(today)) {
                        long overdueDays = My_Classes.FineCalculator.countWeekdaysLate(due, today);
                        daysLeft = -overdueDays;
                    } else {
                       
                       java.time.LocalDate start = today.toLocalDate();
                        java.time.LocalDate end = due.toLocalDate();
                        long count = 0;
                        while (!start.isAfter(end)) {
                            java.time.DayOfWeek day = start.getDayOfWeek();
                            if (day != java.time.DayOfWeek.SUNDAY) {
                                count++;
                            }
                            start = start.plusDays(1);
                        }
                        daysLeft = count;
                    }
                } else {
                    daysLeft = 0;
                }

                String status = daysLeft < 0 ? "OVERDUE" : rs.getString("status");

                model.addRow(new Object[]{
                    rs.getInt("transaction_id"),
                    rs.getString("borrower"),
                    rs.getString("title"),
                    rs.getString("acquisition_number"),
                    rs.getDate("rental_date"),
                    due,
                    daysLeft,
                    status
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading current loans:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────
    //  2. RETURN HISTORY
    // ─────────────────────────────────────────────
    private void loadReturnHistory() {
        currentView = "return_history";
        jLabel10.setText("Return History");

        String[] columns = {
            "Trans ID", "Borrower", "Book Title", "Acc. #",
            "Rental Date", "Return Date", "Days Used", "Fine", "Status"
        };

        DefaultTableModel model = getEmptyModel(columns);
        tblModel.setModel(model);

        String sql = """
SELECT
    t.transaction_id,
    CONCAT(m.first_name, ' ', m.last_name) AS borrower,
    b.title,
    bc.acquisition_number,
    t.rental_date,
    t.returned_date,
    DATEDIFF(t.returned_date, t.rental_date) AS days_used,
    IFNULL(f.amount, 0.00) AS fine,
    t.status
FROM transaction t
JOIN borrower m ON t.borrower_id = m.borrower_id
JOIN book b ON t.book_id = b.book_id
JOIN book_copy bc ON t.copy_id = bc.copy_id
LEFT JOIN fine f ON t.transaction_id = f.transaction_id
WHERE t.status = 'Returned'
  AND t.returned_date IS NOT NULL
ORDER BY t.returned_date DESC
""";

        try (Connection conn = DB_connect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("transaction_id"),
                    rs.getString("borrower"),
                    rs.getString("title"),
                    rs.getString("acquisition_number"),
                    rs.getDate("rental_date"),
                    rs.getDate("returned_date"), // ✅ FIXED HERE
                    rs.getInt("days_used"),
                    String.format("₱%.2f", rs.getDouble("fine")),
                    rs.getString("status")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error loading return history:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────
    //  3. CANCELLED
    // ─────────────────────────────────────────────
    private void loadCancelled() {
        currentView = "cancelled";
        jLabel10.setText("Cancelled Transactions");

        String[] columns = {"Trans ID", "Borrower", "Book Title", "Acc. #", "Rental Date", "Cancelled Date", "Status"};
        DefaultTableModel model = getEmptyModel(columns);
        tblModel.setModel(model);

        String sql = """
        SELECT
            t.transaction_id,
            CONCAT(m.first_name, ' ', m.last_name) AS borrower,
            b.title,
            bc.acquisition_number,
            t.rental_date,
            t.cancelled_date,
            t.status
        FROM transaction t
        JOIN borrower m ON t.borrower_id = m.borrower_id
        JOIN book b ON t.book_id = b.book_id
        JOIN book_copy bc ON t.copy_id = bc.copy_id
        WHERE t.status = 'Cancelled'
        ORDER BY t.cancelled_date DESC
        """;

        try (Connection conn = DB_connect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("transaction_id"),
                    rs.getString("borrower"),
                    rs.getString("title"),
                    rs.getString("acquisition_number"),
                    rs.getDate("rental_date"),
                    rs.getDate("cancelled_date"),
                    rs.getString("status")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading cancelled transactions:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────
    //  4. OVERDUE LIST
    // ─────────────────────────────────────────────
    private void loadOverdueList() {
        currentView = "overdue";
        jLabel10.setText("Overdue List");

        String[] columns = {"Trans ID", "Borrower", "Book Title", "Acc. #", "Rental Date", "Due Date", "Days Overdue", "Fine"};
        DefaultTableModel model = getEmptyModel(columns);
        tblModel.setModel(model);

        String sql = """
        SELECT
            t.transaction_id,
            CONCAT(m.first_name, ' ', m.last_name) AS borrower,
            b.title,
            bc.acquisition_number,
            t.rental_date,
            t.due_date
        FROM transaction t
        JOIN borrower m ON t.borrower_id = m.borrower_id
        JOIN book b ON t.book_id = b.book_id
        JOIN book_copy bc ON t.copy_id = bc.copy_id
        WHERE t.status = 'Borrowed' AND t.due_date < CURDATE()
        ORDER BY t.due_date ASC
        """;

        try (Connection conn = DB_connect.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

            while (rs.next()) {
                java.sql.Date due = rs.getDate("due_date");

                // ✅ Weekday-only calculation
                long daysOverdue = My_Classes.FineCalculator.countWeekdaysLate(due, today);
                double fine = My_Classes.FineCalculator.calculateFine(due, today);

                model.addRow(new Object[]{
                    rs.getInt("transaction_id"),
                    rs.getString("borrower"),
                    rs.getString("title"),
                    rs.getString("acquisition_number"),
                    rs.getDate("rental_date"),
                    due,
                    daysOverdue,
                    String.format("₱%.2f", fine)
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading overdue list:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ─────────────────────────────────────────────
    //  DATE FILTER (applies to current view)
    // ─────────────────────────────────────────────
    private void reloadCurrentView() {
        switch (currentView) {
            case "current_loans" ->
                loadCurrentLoans();
            case "return_history" ->
                loadReturnHistory();
            case "cancelled" ->
                loadCancelled();
            case "overdue" ->
                loadOverdueList();
        }
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
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblModel = new javax.swing.JTable();
        btnExport = new javax.swing.JButton();
        btnOverdueList = new javax.swing.JButton();
        btnReturnHistory = new javax.swing.JButton();
        btnCancelled = new javax.swing.JButton();
        btnCurrentLoans = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 2189, -1));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel9.setText("Transaction Reports");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 140, -1, -1));

        tblModel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        tblModel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Trans ID", "Borrower", "Book Title", "Acc. #", "Rental", "Due", "Days Left", "Status"
            }
        ));
        jScrollPane1.setViewportView(tblModel);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 390, 1440, 600));

        btnExport.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnExport.setText("Export");
        btnExport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnExportMouseClicked(evt);
            }
        });
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });
        getContentPane().add(btnExport, new org.netbeans.lib.awtextra.AbsoluteConstraints(1380, 340, 100, 44));

        btnOverdueList.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnOverdueList.setText("Overdue List");
        btnOverdueList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOverdueListActionPerformed(evt);
            }
        });
        getContentPane().add(btnOverdueList, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 220, 200, 44));

        btnReturnHistory.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnReturnHistory.setText("Return History");
        btnReturnHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReturnHistoryActionPerformed(evt);
            }
        });
        getContentPane().add(btnReturnHistory, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 220, 200, 44));

        btnCancelled.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnCancelled.setText("Cancelled");
        btnCancelled.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelledActionPerformed(evt);
            }
        });
        getContentPane().add(btnCancelled, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 220, 200, 44));

        btnCurrentLoans.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnCurrentLoans.setText("Current Loans");
        btnCurrentLoans.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnCurrentLoansMouseClicked(evt);
            }
        });
        btnCurrentLoans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCurrentLoansActionPerformed(evt);
            }
        });
        getContentPane().add(btnCurrentLoans, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 220, 220, 44));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setText("Current Loans");
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 350, 240, 34));

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void btnExportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnExportMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnExportMouseClicked

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        // TODO add your handling code here:
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setSelectedFile(new java.io.File(currentView + "_export.csv"));
        if (fc.showSaveDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }

        try (java.io.PrintWriter pw = new java.io.PrintWriter(fc.getSelectedFile())) {
            DefaultTableModel model = (DefaultTableModel) tblModel.getModel();

            // Header row
            for (int c = 0; c < model.getColumnCount(); c++) {
                pw.print(model.getColumnName(c));
                if (c < model.getColumnCount() - 1) {
                    pw.print(",");
                }
            }
            pw.println();

            // Data rows
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    Object val = model.getValueAt(r, c);
                    pw.print(val == null ? "" : val.toString().replace(",", ";"));
                    if (c < model.getColumnCount() - 1) {
                        pw.print(",");
                    }
                }
                pw.println();
            }

            JOptionPane.showMessageDialog(this, "Exported successfully!", "Export", JOptionPane.INFORMATION_MESSAGE);

        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(this, "Export failed:\n" + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_btnExportActionPerformed

    private void btnReturnHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReturnHistoryActionPerformed
        loadReturnHistory();

    }//GEN-LAST:event_btnReturnHistoryActionPerformed

    private void btnCancelledActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelledActionPerformed
        loadCancelled();
    }//GEN-LAST:event_btnCancelledActionPerformed

    private void btnOverdueListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOverdueListActionPerformed
        // TODO add your handling code here:
        loadOverdueList();

    }//GEN-LAST:event_btnOverdueListActionPerformed

    private void btnCurrentLoansMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCurrentLoansMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCurrentLoansMouseClicked

    private void btnCurrentLoansActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCurrentLoansActionPerformed
        // TODO add your handling code here:
        loadCurrentLoans();


    }//GEN-LAST:event_btnCurrentLoansActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new TransactionReports().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancelled;
    private javax.swing.JButton btnCurrentLoans;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnOverdueList;
    private javax.swing.JButton btnReturnHistory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblModel;
    private javax.swing.JLabel txtBooks;
    private javax.swing.JLabel txtDashboard;
    private javax.swing.JLabel txtLogout1;
    private javax.swing.JLabel txtMembers;
    private javax.swing.JLabel txtReports;
    private javax.swing.JLabel txtTransactions;
    // End of variables declaration//GEN-END:variables
}
