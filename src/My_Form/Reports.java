/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package My_Form;

import My_Classes.DB_connect;
import java.sql.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Administrator
 */
public class Reports extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Reports.class.getName());

    /**
     * Creates new form NewJFrame
     */
    private int selectedFineId = -1;
    public Reports() {
        setUndecorated(true); // REQUIRED for opacity
        initComponents();
        setFocusableWindowState(true);

        java.awt.EventQueue.invokeLater(() -> {
            this.requestFocusInWindow(); // removes focus from txtSearch
        });
        txtSearch.setText("Search borrower or book...");
        txtSearch.setForeground(java.awt.Color.GRAY);
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().equals("Search borrower or book...")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(java.awt.Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText("Search borrower or book...");
                    txtSearch.setForeground(java.awt.Color.GRAY);
                }
            }
        });
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        tblDashboard.getTableHeader().setPreferredSize(
                new java.awt.Dimension(tblDashboard.getTableHeader().getWidth(), 50)
        );
        tblDashboard.getTableHeader().setFont(
                tblDashboard.getTableHeader().getFont().deriveFont(18f)
        );

        tblDashboard.setFont(tblDashboard.getFont().deriveFont(16f));
             loadDashboardStats();
        loadFinesTable(null, ""); // load all fines on startup

    }

   public void loadDashboardStats() {
        try {
            Connection con = DB_connect.getConnection();
 
            // Total Books
            PreparedStatement pstTotal = con.prepareStatement("SELECT COUNT(*) AS total FROM book_copy");
            ResultSet rsTotal = pstTotal.executeQuery();
            if (rsTotal.next()) lblTotalBooks.setText(String.valueOf(rsTotal.getInt("total")));
 
            // Books Available
            PreparedStatement pstAvailable = con.prepareStatement("SELECT COUNT(*) AS available FROM book_copy WHERE status='Available'");
            ResultSet rsAvailable = pstAvailable.executeQuery();
            if (rsAvailable.next()) lblBooksAvailable.setText(String.valueOf(rsAvailable.getInt("available")));
 
            // Borrowed Books
            PreparedStatement pstBorrowed = con.prepareStatement("SELECT COUNT(*) AS borrowed FROM `transaction` WHERE status='Borrowed'");
            ResultSet rsBorrowed = pstBorrowed.executeQuery();
            if (rsBorrowed.next()) lblBorrowedBooks.setText(String.valueOf(rsBorrowed.getInt("borrowed")));
 
            // Overdue Books
            PreparedStatement pstOverdue = con.prepareStatement(
                    "SELECT COUNT(*) AS overdue FROM `transaction` WHERE status='Borrowed' AND due_date < CURDATE()");
            ResultSet rsOverdue = pstOverdue.executeQuery();
            if (rsOverdue.next()) lblOverdueBooks.setText(String.valueOf(rsOverdue.getInt("overdue")));
 
            // Unpaid Fines total amount
            PreparedStatement pstUnpaid = con.prepareStatement(
                    "SELECT SUM(amount) AS total_unpaid FROM fine WHERE status = 'Unpaid'");
            ResultSet rsUnpaid = pstUnpaid.executeQuery();
            if (rsUnpaid.next()) {
                double total = rsUnpaid.getDouble("total_unpaid");
                lblUnpaidFines.setText(String.format("₱%.0f", total));
            }
 
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
 
    // ─── LOAD FINES TABLE (dynamic: filter by status + search keyword) ─────────
    public void loadFinesTable(String statusFilter, String keyword) {
        try {
            Connection con = DB_connect.getConnection();
 
            DefaultTableModel model = (DefaultTableModel) tblDashboard.getModel();
            model.setRowCount(0);
            model.setColumnIdentifiers(new Object[]{
                "Fine ID", "Borrower", "Book Title", "Acc. #", "Days", "Amount", "Status"
            });
 
            // Build SQL dynamically based on filter and keyword
            StringBuilder sql = new StringBuilder(
                    "SELECT f.fine_id, "
                    + "CONCAT(b.first_name, ' ', b.last_name) AS borrower, "
                    + "bo.title, "
                    + "bc.acquisition_number, "
                    + "f.days_overdue, "
                    + "f.amount, "
                    + "f.status "
                    + "FROM fine f "
                    + "JOIN borrower b ON f.borrower_id = b.borrower_id "
                    + "JOIN `transaction` t ON f.transaction_id = t.transaction_id "
                    + "JOIN book bo ON t.book_id = bo.book_id "
                    + "JOIN book_copy bc ON t.copy_id = bc.copy_id "
                    + "WHERE 1=1 "
            );
 
            // Add status filter if not "All"
            if (statusFilter != null && !statusFilter.equals("All")) {
                sql.append("AND f.status = ? ");
            }
 
            // Add keyword search if not empty
            if (keyword != null && !keyword.trim().isEmpty()
                    && !keyword.equals("Search borrower or book...")) {
                sql.append("AND (CONCAT(b.first_name, ' ', b.last_name) LIKE ? ");
                sql.append("OR bo.title LIKE ?) ");
            }
 
            sql.append("ORDER BY f.fine_date DESC");
 
            PreparedStatement pst = con.prepareStatement(sql.toString());
 
            // Bind parameters in order
            int paramIndex = 1;
            if (statusFilter != null && !statusFilter.equals("All")) {
                pst.setString(paramIndex++, statusFilter);
            }
            if (keyword != null && !keyword.trim().isEmpty()
                    && !keyword.equals("Search borrower or book...")) {
                String search = "%" + keyword.trim() + "%";
                pst.setString(paramIndex++, search);
                pst.setString(paramIndex++, search);
            }
 
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("fine_id"),
                    rs.getString("borrower"),
                    rs.getString("title"),
                    rs.getString("acquisition_number"),
                    rs.getInt("days_overdue"),
                    String.format("₱%.2f", rs.getDouble("amount")),
                    rs.getString("status")
                });
            }
 
            // Update table styling
            tblDashboard.getTableHeader().setFont(
                    tblDashboard.getTableHeader().getFont().deriveFont(18f));
            tblDashboard.setFont(tblDashboard.getFont().deriveFont(16f));
            tblDashboard.setRowHeight(30);
 
            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
 
    // ─── HELPER: get current filter + keyword and reload table ─────────────────
    private void reloadTable() {
        String status = (String) cmbCategory.getSelectedItem();
        String keyword = txtSearch.getText();
        loadFinesTable(status, keyword);
    }

    public void resetDashboard() {
        // Reset search box
        txtSearch.setText("Search title by author or title...");
        txtSearch.setForeground(java.awt.Color.GRAY);

        // Reset category dropdown
        cmbCategory.setSelectedItem("Books");

        // Reload default table
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        userButton = new javax.swing.JMenuItem();
        userButton1 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtMembers = new javax.swing.JLabel();
        txtTransactions = new javax.swing.JLabel();
        txtReports = new javax.swing.JLabel();
        txtLogout1 = new javax.swing.JLabel();
        txtDashboard = new javax.swing.JLabel();
        txtBooks = new javax.swing.JLabel();
        lblTotalBooks = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        cmbCategory = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDashboard = new javax.swing.JTable();
        tblDashboard.getTableHeader().setFont(
            new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16)
        );
        jLabel18 = new javax.swing.JLabel();
        lblBorrowedBooks = new javax.swing.JLabel();
        lblBooksAvailable = new javax.swing.JLabel();
        lblOverdueBooks = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        lblUnpaidFines = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        btnMemberReports = new javax.swing.JButton();
        btnTransaction = new javax.swing.JButton();
        btnBook = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        btnFine = new javax.swing.JButton();
        btnRefresh1 = new javax.swing.JButton();
        btnPay = new javax.swing.JButton();

        userButton.setText("Logout");
        userButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userButtonActionPerformed(evt);
            }
        });
        jPopupMenu1.add(userButton);

        userButton1.setText("Logout");
        userButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userButton1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(userButton1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 0, 153));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
                .addContainerGap(331, Short.MAX_VALUE))
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

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1823, -1));

        lblTotalBooks.setBackground(new java.awt.Color(255, 102, 51));
        lblTotalBooks.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblTotalBooks.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalBooks.setText("-");
        lblTotalBooks.setOpaque(true);
        jPanel1.add(lblTotalBooks, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 190, 160, 110));
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(214, 235, -1, -1));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel12.setText("Total Books");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 150, -1, -1));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel13.setText("Active Members");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 150, -1, -1));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel14.setText("Unpaid Fines");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(1170, 150, -1, 30));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel15.setText("Overdue Books");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 150, -1, -1));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel17.setText("Status: ");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 490, -1, 40));

        txtSearch.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSearch.setForeground(new java.awt.Color(102, 102, 102));
        txtSearch.setText("Search borrower or Book...");
        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });
        jPanel1.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 490, 290, 40));

        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Unpaid", "Paid" }));
        cmbCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCategoryActionPerformed(evt);
            }
        });
        jPanel1.add(cmbCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 490, 160, 40));

        tblDashboard.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Fine ID", "Borrower", "Book Title", "Acc. #", "Days", "Amount", "Status"
            }
        ));
        tblDashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDashboardMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDashboard);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 540, 1360, 320));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel18.setText("OVERDUE FINES REPORT");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 440, -1, -1));

        lblBorrowedBooks.setBackground(new java.awt.Color(255, 102, 51));
        lblBorrowedBooks.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblBorrowedBooks.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBorrowedBooks.setText("-");
        lblBorrowedBooks.setOpaque(true);
        jPanel1.add(lblBorrowedBooks, new org.netbeans.lib.awtextra.AbsoluteConstraints(930, 190, 170, 110));

        lblBooksAvailable.setBackground(new java.awt.Color(255, 102, 51));
        lblBooksAvailable.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblBooksAvailable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBooksAvailable.setText("-");
        lblBooksAvailable.setOpaque(true);
        jPanel1.add(lblBooksAvailable, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 190, 160, 110));

        lblOverdueBooks.setBackground(new java.awt.Color(255, 102, 51));
        lblOverdueBooks.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblOverdueBooks.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOverdueBooks.setText("-");
        lblOverdueBooks.setOpaque(true);
        jPanel1.add(lblOverdueBooks, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 190, 160, 110));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/My_Image/1150612 (2).png"))); // NOI18N
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 490, -1, 40));

        jLabel2.setText("jLabel2");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 220, -1, -1));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jPanel1.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(1520, 160, -1, -1));

        lblUnpaidFines.setBackground(new java.awt.Color(255, 102, 51));
        lblUnpaidFines.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblUnpaidFines.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUnpaidFines.setText("-");
        lblUnpaidFines.setOpaque(true);
        jPanel1.add(lblUnpaidFines, new org.netbeans.lib.awtextra.AbsoluteConstraints(1160, 190, 170, 110));

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel20.setText("Borrowed Books");
        jPanel1.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 150, -1, 30));

        btnMemberReports.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnMemberReports.setText("Member Reports");
        btnMemberReports.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMemberReportsActionPerformed(evt);
            }
        });
        jPanel1.add(btnMemberReports, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 350, 190, 40));

        btnTransaction.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnTransaction.setText("Transaction Reports");
        btnTransaction.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTransactionActionPerformed(evt);
            }
        });
        jPanel1.add(btnTransaction, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 350, 190, 40));

        btnBook.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnBook.setText("Book Reports");
        btnBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBookActionPerformed(evt);
            }
        });
        jPanel1.add(btnBook, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 350, 190, 40));

        btnExport.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnExport.setText("Export");
        btnExport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportActionPerformed(evt);
            }
        });
        jPanel1.add(btnExport, new org.netbeans.lib.awtextra.AbsoluteConstraints(1220, 490, 110, 40));

        btnFine.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnFine.setText("Fine Reports");
        btnFine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFineActionPerformed(evt);
            }
        });
        jPanel1.add(btnFine, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 350, 190, 40));

        btnRefresh1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnRefresh1.setText("Refresh");
        btnRefresh1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefresh1ActionPerformed(evt);
            }
        });
        jPanel1.add(btnRefresh1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1350, 490, 110, 40));

        btnPay.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnPay.setText("Pay");
        btnPay.setEnabled(false);
        btnPay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPayActionPerformed(evt);
            }
        });
        jPanel1.add(btnPay, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 490, 110, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void userButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_userButtonActionPerformed

    private void userButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_userButton1ActionPerformed

    private void cmbCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCategoryActionPerformed
        // TODO add your handling code here:
        reloadTable();
    }//GEN-LAST:event_cmbCategoryActionPerformed

    private void btnMemberReportsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMemberReportsActionPerformed
        // TODO add your handling code here:
        MemberReports mr = new MemberReports();
        mr.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnMemberReportsActionPerformed

    private void btnTransactionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTransactionActionPerformed
        // TODO add your handling code here:
        TransactionReports tr = new TransactionReports();
        tr.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnTransactionActionPerformed

    private void btnBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBookActionPerformed
        // TODO add your handling code here:
        BookReports Br = new BookReports();
        Br.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBookActionPerformed

    private void btnExportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportActionPerformed
        // TODO add your handling code here:
         javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Save Excel File");
        fileChooser.setSelectedFile(new java.io.File("FinesReport.xlsx"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));
 
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != javax.swing.JFileChooser.APPROVE_OPTION) return;
 
        java.io.File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().endsWith(".xlsx")) {
            fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".xlsx");
        }
 
        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Fines Report");
            javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) tblDashboard.getModel();
 
            org.apache.poi.xssf.usermodel.XSSFCellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(
                new byte[]{(byte) 204, (byte) 204, (byte) 255}, null));
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
 
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            for (int col = 0; col < model.getColumnCount(); col++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(col);
                cell.setCellValue(model.getColumnName(col));
                cell.setCellStyle(headerStyle);
            }
 
            for (int row = 0; row < model.getRowCount(); row++) {
                org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(row + 1);
                for (int col = 0; col < model.getColumnCount(); col++) {
                    org.apache.poi.ss.usermodel.Cell cell = dataRow.createCell(col);
                    Object value = model.getValueAt(row, col);
                    if (value != null) cell.setCellValue(value.toString());
                }
            }
 
            for (int col = 0; col < model.getColumnCount(); col++) sheet.autoSizeColumn(col);
 
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fileToSave)) {
                workbook.write(fos);
            }
 
            JOptionPane.showMessageDialog(this,
                "Exported successfully to:\n" + fileToSave.getAbsolutePath(),
                "Export Successful", JOptionPane.INFORMATION_MESSAGE);
 
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(),
                "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnExportActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        reloadTable();

    }//GEN-LAST:event_txtSearchKeyReleased

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnFineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFineActionPerformed
        // TODO add your handling code here:
        FineReports fr = new FineReports();
        fr.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnFineActionPerformed

    private void btnRefresh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefresh1ActionPerformed
        // TODO add your handling code here:
           loadDashboardStats();
        reloadTable();
    }//GEN-LAST:event_btnRefresh1ActionPerformed

    private void tblDashboardMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDashboardMouseClicked
        // TODO add your handling code here:
         int row = tblDashboard.getSelectedRow();

    if (row != -1) {
        selectedFineId = Integer.parseInt(tblDashboard.getValueAt(row, 0).toString());

        String borrower = tblDashboard.getValueAt(row, 1).toString();
        String book = tblDashboard.getValueAt(row, 2).toString();
        String status = tblDashboard.getValueAt(row, 6).toString();

        // Optional: show info
        System.out.println("Selected Fine ID: " + selectedFineId);

        if (status.equalsIgnoreCase("Paid")) {
            JOptionPane.showMessageDialog(this, "This fine is already paid.");
            selectedFineId = -1; // reset
        }
        btnPay.setEnabled(true);
    }
    }//GEN-LAST:event_tblDashboardMouseClicked

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

    private void btnPayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayActionPerformed
        // TODO add your handling code here:
         if (selectedFineId == -1) {
        JOptionPane.showMessageDialog(this, "Please select a fine to pay.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(
            this,
            "Mark this fine as PAID?",
            "Confirm Payment",
            JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            Connection con = DB_connect.getConnection();

            String sql = "UPDATE fine SET status='Paid', payment_date=NOW() WHERE fine_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, selectedFineId);

            int updated = pst.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Payment successful!");

                // Refresh table + stats
                loadDashboardStats();
                reloadTable();

                selectedFineId = -1; // reset
            }
            btnPay.setEnabled(false);
            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e);
        }
    }
    }//GEN-LAST:event_btnPayActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new Reports().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBook;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnFine;
    private javax.swing.JButton btnMemberReports;
    private javax.swing.JButton btnPay;
    private javax.swing.JButton btnRefresh1;
    private javax.swing.JButton btnTransaction;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBooksAvailable;
    private javax.swing.JLabel lblBorrowedBooks;
    private javax.swing.JLabel lblOverdueBooks;
    private javax.swing.JLabel lblTotalBooks;
    private javax.swing.JLabel lblUnpaidFines;
    private javax.swing.JTable tblDashboard;
    private javax.swing.JLabel txtBooks;
    private javax.swing.JLabel txtDashboard;
    private javax.swing.JLabel txtLogout1;
    private javax.swing.JLabel txtMembers;
    private javax.swing.JLabel txtReports;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JLabel txtTransactions;
    private javax.swing.JMenuItem userButton;
    private javax.swing.JMenuItem userButton1;
    // End of variables declaration//GEN-END:variables
}
