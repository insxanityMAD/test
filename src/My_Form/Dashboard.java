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
public class Dashboard extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Dashboard.class.getName());

    /**
     * Creates new form NewJFrame
     */
    public Dashboard() {
        setUndecorated(true); // REQUIRED for opacity
        initComponents();
        setFocusableWindowState(true);

        java.awt.EventQueue.invokeLater(() -> {
            this.requestFocusInWindow(); // removes focus from txtSearch
        });
        txtSearch.setText("Search");
        txtSearch.setForeground(java.awt.Color.GRAY);
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {

            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().equals("Search")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(java.awt.Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText("Search");
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
        loadTableData("Books");

    }

    public void loadDashboardStats() {
        try {
            Connection con = DB_connect.getConnection();

            // Total Books (count all book copies)
            String sqlTotal = "SELECT COUNT(*) AS total FROM book_copy";
            PreparedStatement pstTotal = con.prepareStatement(sqlTotal);
            ResultSet rsTotal = pstTotal.executeQuery();
            if (rsTotal.next()) {
                lblTotalBooks.setText(String.valueOf(rsTotal.getInt("total")));
            }

            // Books Available
            String sqlAvailable = "SELECT COUNT(*) AS available FROM book_copy WHERE status='Available'";
            PreparedStatement pstAvailable = con.prepareStatement(sqlAvailable);
            ResultSet rsAvailable = pstAvailable.executeQuery();
            if (rsAvailable.next()) {
                lblBooksAvailable.setText(String.valueOf(rsAvailable.getInt("available")));
            }

            // Borrowed Books
            String sqlBorrowed = "SELECT COUNT(*) AS borrowed FROM transaction WHERE status='Borrowed'";
            PreparedStatement pstBorrowed = con.prepareStatement(sqlBorrowed);
            ResultSet rsBorrowed = pstBorrowed.executeQuery();
            if (rsBorrowed.next()) {
                lblBorrowedBooks.setText(String.valueOf(rsBorrowed.getInt("borrowed")));
            }

            // Overdue Books (borrowed and past due date)
            String sqlOverdue = "SELECT COUNT(*) AS overdue FROM transaction "
                    + "WHERE status='Borrowed' AND due_date < CURDATE()";
            PreparedStatement pstOverdue = con.prepareStatement(sqlOverdue);
            ResultSet rsOverdue = pstOverdue.executeQuery();
            if (rsOverdue.next()) {
                lblOverdueBooks.setText(String.valueOf(rsOverdue.getInt("overdue")));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void loadTableData(String category) {
        try {
            Connection con = DB_connect.getConnection();
            DefaultTableModel model = new DefaultTableModel();
            tblDashboard.setModel(model);
            model.setRowCount(0);

            switch (category) {   

                case "Books": {
                    model.setColumnIdentifiers(new Object[]{
                        "ID", "Class", "Title", "Author", "Category", "Pages",
                        "Source of Fund", "Cost Price", "Publisher",
                        "Publication Year", "Remarks", "ISBN", "Shelf Location"
                    });

                    Statement st = con.createStatement();
                    ResultSet res = st.executeQuery(
                            "SELECT b.book_id, b.title, b.author, c.category_name, b.publisher, "
                            + "b.publication_year, b.isbn, b.shelf_location, b.remarks, b.class, "
                            + "b.pages, b.source_of_fund, b.cost_price "
                            + "FROM book b LEFT JOIN category c ON b.category_id = c.category_id"
                    );

                    while (res.next()) {
                        model.addRow(new Object[]{
                            res.getInt("book_id"),
                            res.getString("class"),
                            res.getString("title"),
                            res.getString("author"),
                            res.getString("category_name"),
                            res.getInt("pages"),
                            res.getString("source_of_fund"),
                            res.getString("cost_price"),
                            res.getString("publisher"),
                            res.getString("publication_year"),
                            res.getString("remarks"),
                            res.getString("isbn"),
                            res.getString("shelf_location")
                        });
                    }
                    break;
                }

                case "Members": {
                    model.setColumnIdentifiers(new Object[]{
                        "ID", "First Name", "Last Name", "Gender", "ID Number", "ID Type",
                        "Email", "Phone Number", "Address", "Borrower Type",
                        "Status", "Date of Birth", "Date Registered"
                    });

                    PreparedStatement ps = con.prepareStatement("SELECT * FROM borrower");
                    ResultSet res = ps.executeQuery();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    while (res.next()) {
                        java.sql.Date date = res.getDate("date_registered");
                        java.sql.Date dob = res.getDate("date_of_birth");

                        model.addRow(new Object[]{
                            res.getInt("borrower_id"),
                            res.getString("first_name"),
                            res.getString("last_name"),
                            res.getString("gender"),
                            res.getString("id_number"),
                            res.getString("id_type"),
                            res.getString("email"),
                            res.getString("phone_number"),
                            res.getString("address"),
                            res.getString("borrower_type"),
                            res.getString("status"),
                            dob != null ? sdf.format(dob) : "",
                            date != null ? sdf.format(date) : ""
                        });
                    }
                    break;
                }

                case "Transactions": {
                    model.setColumnIdentifiers(new Object[]{
                        "ID", "Borrower", "Book Title", "Acquisition No.",
                        "Rental Date", "Due Date", "Status"
                    });

                    PreparedStatement pst = con.prepareStatement(
                            "SELECT t.transaction_id, "
                            + "CONCAT(b.first_name, ' ', b.last_name) AS full_name, "
                            + "bo.title, bc.acquisition_number, t.rental_date, t.due_date, t.status "
                            + "FROM `transaction` t "
                            + "JOIN borrower b ON t.borrower_id = b.borrower_id "
                            + "JOIN book bo ON t.book_id = bo.book_id "
                            + "JOIN book_copy bc ON t.copy_id = bc.copy_id"
                    );
                    ResultSet rs = pst.executeQuery();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    while (rs.next()) {
                        java.sql.Date rent = rs.getDate("rental_date");
                        java.sql.Date due = rs.getDate("due_date");

                        model.addRow(new Object[]{
                            rs.getInt("transaction_id"),
                            rs.getString("full_name"),
                            rs.getString("title"),
                            rs.getString("acquisition_number"),
                            rent != null ? sdf.format(rent) : "",
                            due != null ? sdf.format(due) : "",
                            rs.getString("status")
                        });
                    }
                    break;
                }
            }

            // Reapply styling after model change
            tblDashboard.getTableHeader().setFont(
                    tblDashboard.getTableHeader().getFont().deriveFont(18f));
            tblDashboard.setFont(tblDashboard.getFont().deriveFont(16f));
            tblDashboard.setRowHeight(30);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void searchTableData(String keyword, String category) {
        try {
            Connection con = DB_connect.getConnection();
            DefaultTableModel model = new DefaultTableModel();
            tblDashboard.setModel(model);
            model.setRowCount(0);

            String search = "%" + keyword + "%";

            switch (category) {

                case "Books": {
                    model.setColumnIdentifiers(new Object[]{
                        "ID", "Class", "Title", "Author", "Category", "Pages",
                        "Source of Fund", "Cost Price", "Publisher",
                        "Publication Year", "Remarks", "ISBN", "Shelf Location"
                    });
                    PreparedStatement pst = con.prepareStatement(
                            "SELECT b.book_id, b.title, b.author, c.category_name, b.publisher, "
                            + "b.publication_year, b.isbn, b.shelf_location, b.remarks, b.class, "
                            + "b.pages, b.source_of_fund, b.cost_price "
                            + "FROM book b LEFT JOIN category c ON b.category_id = c.category_id "
                            + "WHERE b.title LIKE ? OR b.author LIKE ? OR c.category_name LIKE ?"
                    );
                    pst.setString(1, search);
                    pst.setString(2, search);
                    pst.setString(3, search);
                    ResultSet res = pst.executeQuery();
                    while (res.next()) {
                        model.addRow(new Object[]{
                            res.getInt("book_id"),
                            res.getString("class"),
                            res.getString("title"),
                            res.getString("author"),
                            res.getString("category_name"),
                            res.getInt("pages"),
                            res.getString("source_of_fund"),
                            res.getString("cost_price"),
                            res.getString("publisher"),
                            res.getString("publication_year"),
                            res.getString("remarks"),
                            res.getString("isbn"),
                            res.getString("shelf_location")
                        });
                    }
                    break;
                }

                case "Members": {
                    model.setColumnIdentifiers(new Object[]{
                        "ID", "First Name", "Last Name", "Gender", "ID Number", "ID Type",
                        "Email", "Phone Number", "Address", "Borrower Type",
                        "Status", "Date of Birth", "Date Registered"
                    });
                    PreparedStatement pst = con.prepareStatement(
                            "SELECT * FROM borrower "
                            + "WHERE first_name LIKE ? OR last_name LIKE ? "
                            + "OR email LIKE ? OR id_number LIKE ? OR borrower_type LIKE ?"
                    );
                    pst.setString(1, search);
                    pst.setString(2, search);
                    pst.setString(3, search);
                    pst.setString(4, search);
                    pst.setString(5, search);
                    ResultSet res = pst.executeQuery();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    while (res.next()) {
                        java.sql.Date date = res.getDate("date_registered");
                        java.sql.Date dob = res.getDate("date_of_birth");
                        model.addRow(new Object[]{
                            res.getInt("borrower_id"),
                            res.getString("first_name"),
                            res.getString("last_name"),
                            res.getString("gender"),
                            res.getString("id_number"),
                            res.getString("id_type"),
                            res.getString("email"),
                            res.getString("phone_number"),
                            res.getString("address"),
                            res.getString("borrower_type"),
                            res.getString("status"),
                            dob != null ? sdf.format(dob) : "",
                            date != null ? sdf.format(date) : ""
                        });
                    }
                    break;
                }

                case "Transactions": {
                    model.setColumnIdentifiers(new Object[]{
                        "ID", "Borrower", "Book Title", "Acquisition No.",
                        "Rental Date", "Due Date", "Status"
                    });
                    PreparedStatement pst = con.prepareStatement(
                            "SELECT t.transaction_id, "
                            + "CONCAT(b.first_name, ' ', b.last_name) AS full_name, "
                            + "bo.title, bc.acquisition_number, t.rental_date, t.due_date, t.status "
                            + "FROM `transaction` t "
                            + "JOIN borrower b ON t.borrower_id = b.borrower_id "
                            + "JOIN book bo ON t.book_id = bo.book_id "
                            + "JOIN book_copy bc ON t.copy_id = bc.copy_id "
                            + "WHERE CONCAT(b.first_name,' ',b.last_name) LIKE ? "
                            + "OR bo.title LIKE ? OR t.status LIKE ? OR bc.acquisition_number LIKE ?"
                    );
                    pst.setString(1, search);
                    pst.setString(2, search);
                    pst.setString(3, search);
                    pst.setString(4, search);
                    ResultSet rs = pst.executeQuery();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    while (rs.next()) {
                        java.sql.Date rent = rs.getDate("rental_date");
                        java.sql.Date due = rs.getDate("due_date");
                        model.addRow(new Object[]{
                            rs.getInt("transaction_id"),
                            rs.getString("full_name"),
                            rs.getString("title"),
                            rs.getString("acquisition_number"),
                            rent != null ? sdf.format(rent) : "",
                            due != null ? sdf.format(due) : "",
                            rs.getString("status")
                        });
                    }
                    break;
                }
            }

            tblDashboard.getTableHeader().setFont(
                    tblDashboard.getTableHeader().getFont().deriveFont(18f));
            tblDashboard.setFont(tblDashboard.getFont().deriveFont(16f));
            tblDashboard.setRowHeight(30);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void resetDashboard() {
        // Reset search box
        txtSearch.setText("Search");
        txtSearch.setForeground(java.awt.Color.GRAY);

        // Reset category dropdown
        cmbCategory.setSelectedItem("Books");

        // Reload default table
        loadTableData("Books");
    }
   private void showOverdueDetails() {
    try {
        Connection con = DB_connect.getConnection();

        String sql = "SELECT t.transaction_id, "
                + "CONCAT(b.first_name, ' ', b.last_name) AS full_name, "
                + "bo.title, bc.acquisition_number, t.rental_date, t.due_date "
                + "FROM `transaction` t "
                + "JOIN borrower b ON t.borrower_id = b.borrower_id "
                + "JOIN book bo ON t.book_id = bo.book_id "
                + "JOIN book_copy bc ON t.copy_id = bc.copy_id "
                + "WHERE t.status = 'Borrowed' AND t.due_date < CURDATE() "
                + "ORDER BY t.due_date ASC";

        PreparedStatement pst = con.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Borrower", "Book Title", "Acq. No.",
                         "Rental Date", "Due Date", "Days Overdue"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

        while (rs.next()) {
            java.sql.Date rent = rs.getDate("rental_date");
            java.sql.Date due = rs.getDate("due_date");

            // ✅ Use weekday-only calculation
            long daysOverdue = My_Classes.FineCalculator.countWeekdaysLate(due, today);

            model.addRow(new Object[]{
                rs.getInt("transaction_id"),
                rs.getString("full_name"),
                rs.getString("title"),
                rs.getString("acquisition_number"),
                rent != null ? sdf.format(rent) : "",
                due != null ? sdf.format(due) : "",
                daysOverdue + " day(s)"
            });
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No overdue books found.",
                "Overdue Books",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        javax.swing.JTable overdueTable = new javax.swing.JTable(model);
        overdueTable.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 14));
        overdueTable.setRowHeight(28);
        overdueTable.getTableHeader().setFont(
            new java.awt.Font("Tahoma", java.awt.Font.BOLD, 14));
        overdueTable.getTableHeader().setBackground(new java.awt.Color(204, 0, 0));
        overdueTable.getTableHeader().setForeground(java.awt.Color.WHITE);

        // ✅ Row color based on CORRECT weekday count
        overdueTable.setDefaultRenderer(Object.class,
            new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String daysStr = table.getValueAt(row, 6).toString();
                    int days = Integer.parseInt(daysStr.replace(" day(s)", "").trim());

                    if (days > 30) {
                        setBackground(new java.awt.Color(255, 100, 100));
                    } else if (days > 7) {
                        setBackground(new java.awt.Color(255, 180, 180));
                    } else {
                        setBackground(new java.awt.Color(255, 230, 230));
                    }
                } else {
                    setBackground(new java.awt.Color(204, 153, 255));
                }

                return this;
            }
        });

        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(overdueTable);
        scrollPane.setPreferredSize(new java.awt.Dimension(900, 400));

        javax.swing.JLabel titleLabel = new javax.swing.JLabel(
            "⚠ Overdue Books — " + model.getRowCount() + " record(s)");
        titleLabel.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 16));
        titleLabel.setForeground(new java.awt.Color(180, 0, 0));
        titleLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0));

        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout(0, 5));
        panel.add(titleLabel, java.awt.BorderLayout.NORTH);
        panel.add(scrollPane, java.awt.BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel,
            "Overdue Books", JOptionPane.WARNING_MESSAGE);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e);
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

        jPopupMenu1 = new javax.swing.JPopupMenu();
        userButton = new javax.swing.JMenuItem();
        userButton1 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtDashboard = new javax.swing.JLabel();
        txtBooks = new javax.swing.JLabel();
        txtMembers = new javax.swing.JLabel();
        txtTransactions = new javax.swing.JLabel();
        txtReports = new javax.swing.JLabel();
        txtLogout1 = new javax.swing.JLabel();
        lblTotalBooks = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        cmbCategory = new javax.swing.JComboBox<>();
        btnRefresh = new javax.swing.JButton();
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
        jPanel1.add(lblTotalBooks, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 200, 225, 151));
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(214, 235, -1, -1));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel12.setText("Total Books");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 160, -1, -1));

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel13.setText("Books Available");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 160, -1, -1));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel14.setText("Borrowed Books");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(1220, 160, -1, -1));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel15.setText("Overdue Books");
        jPanel1.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 160, -1, -1));

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel17.setText("Catalog");
        jPanel1.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 410, -1, -1));

        txtSearch.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        txtSearch.setForeground(new java.awt.Color(102, 102, 102));
        txtSearch.setText("Search title by author or title...");
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
        jPanel1.add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 450, 578, 44));

        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Books", "Members", "Transactions" }));
        cmbCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCategoryActionPerformed(evt);
            }
        });
        jPanel1.add(cmbCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 450, 272, 40));

        btnRefresh.setBackground(new java.awt.Color(204, 204, 204));
        btnRefresh.setText("Refresh");
        btnRefresh.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRefreshMouseClicked(evt);
            }
        });
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });
        jPanel1.add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 450, 183, 40));

        tblDashboard.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title", "Author", "Category    ", "Status", "Actions"
            }
        ));
        jScrollPane1.setViewportView(tblDashboard);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 530, 1360, 343));

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel18.setText("Search Catalog");
        jPanel1.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 410, -1, -1));

        lblBorrowedBooks.setBackground(new java.awt.Color(255, 102, 51));
        lblBorrowedBooks.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblBorrowedBooks.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBorrowedBooks.setText("-");
        lblBorrowedBooks.setOpaque(true);
        jPanel1.add(lblBorrowedBooks, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 200, 225, 151));

        lblBooksAvailable.setBackground(new java.awt.Color(255, 102, 51));
        lblBooksAvailable.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblBooksAvailable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBooksAvailable.setText("-");
        lblBooksAvailable.setOpaque(true);
        jPanel1.add(lblBooksAvailable, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 200, 225, 151));

        lblOverdueBooks.setBackground(new java.awt.Color(255, 102, 51));
        lblOverdueBooks.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        lblOverdueBooks.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOverdueBooks.setText("-");
        lblOverdueBooks.setOpaque(true);
        lblOverdueBooks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblOverdueBooksMouseClicked(evt);
            }
        });
        jPanel1.add(lblOverdueBooks, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 200, 225, 151));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/My_Image/1150612 (2).png"))); // NOI18N
        jPanel1.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 452, -1, 40));

        jLabel2.setText("jLabel2");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 270, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void userButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_userButtonActionPerformed

    private void userButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_userButton1ActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        String currentCategory = cmbCategory.getSelectedItem().toString();

        // Clear search box
        txtSearch.setText("Search");
        txtSearch.setForeground(java.awt.Color.GRAY);

        // Reload only the current category
        loadTableData(currentCategory);
        loadDashboardStats();

        tblDashboard.requestFocusInWindow();
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnRefreshMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRefreshMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRefreshMouseClicked

    private void cmbCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCategoryActionPerformed
        // TODO add your handling code here:
        if (cmbCategory.getSelectedItem() != null) {
            loadTableData(cmbCategory.getSelectedItem().toString());
        }
    }//GEN-LAST:event_cmbCategoryActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        String keyword = txtSearch.getText().trim();
        String category = cmbCategory.getSelectedItem().toString();

        if (keyword.isEmpty() || keyword.equals("Search")) {
            loadTableData(category);
        } else {
            searchTableData(keyword, category);
        }
    }//GEN-LAST:event_txtSearchKeyReleased

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

    private void lblOverdueBooksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblOverdueBooksMouseClicked
        // TODO add your handling code here:
        showOverdueDetails();
    }//GEN-LAST:event_lblOverdueBooksMouseClicked

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
        java.awt.EventQueue.invokeLater(() -> new Dashboard().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRefresh;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblBooksAvailable;
    private javax.swing.JLabel lblBorrowedBooks;
    private javax.swing.JLabel lblOverdueBooks;
    private javax.swing.JLabel lblTotalBooks;
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
