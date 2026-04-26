/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package My_Form;

import My_Classes.DB_connect;
import My_Classes.FineCalculator;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 *
 * @author Administrator
 */
public class AddLoan1 extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AddLoan1.class.getName());

    private boolean isUpdating = false;
    private boolean isSelectingBorrower = false;

    class Borrower {

        int id;
        String fullName;
        String idNumber;

        public Borrower(int id, String fullName, String idNumber) {
            this.id = id;
            this.fullName = fullName;
            this.idNumber = idNumber;
        }

        public int getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }

        public String getIdNumber() {
            return idNumber;
        }

        @Override
        public String toString() {
            // What appears inside JComboBox
            return fullName;
        }
    }

    public AddLoan1() {
        setUndecorated(true); // REQUIRED for opacity
        initComponents();

        setupSearch();
        loadTransactions();

// ✅ Load borrowers but no default selection
        loadBorrowers();

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"copy_id", "book_id", "Acquisition No.", "Title", "Author", "Category", "Status"}, 0
        );
        tblModel.setModel(model);

// Hide copy_id and book_id
        SwingUtilities.invokeLater(() -> {
            tblModel.getColumnModel().getColumn(0).setMinWidth(0);
            tblModel.getColumnModel().getColumn(0).setMaxWidth(0);
            tblModel.getColumnModel().getColumn(0).setWidth(0);

            tblModel.getColumnModel().getColumn(1).setMinWidth(0);
            tblModel.getColumnModel().getColumn(1).setMaxWidth(0);
            tblModel.getColumnModel().getColumn(1).setWidth(0);
        });

        // ✅ Force clear BOTH tables on startup
        ((DefaultTableModel) tblTransaction.getModel()).setRowCount(0);
        ((DefaultTableModel) tblModel.getModel()).setRowCount(0);

        // ✅ Only loads when librarian manually selects a real borrower
        cmbBorrowerName.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (isUpdating) {
                        return;
                    }

                    Object selectedItem = cmbBorrowerName.getSelectedItem();

                    if (selectedItem instanceof Borrower) {
                        Borrower selected = (Borrower) selectedItem;
                        loadTransactions(selected.getId());
                        ((DefaultTableModel) tblModel.getModel()).setRowCount(0);
                    } else {
                        // ✅ If nothing selected or search is empty, clear both tables
                        ((DefaultTableModel) tblTransaction.getModel()).setRowCount(0);
                        ((DefaultTableModel) tblModel.getModel()).setRowCount(0);
                    }
                }
            }
        });

        JTextField editor = (JTextField) cmbBorrowerName.getEditor().getEditorComponent();
        editor.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (editor.getText().trim().isEmpty()) {
                    // ✅ Clear both tables when search is cleared
                    ((DefaultTableModel) tblTransaction.getModel()).setRowCount(0);
                    ((DefaultTableModel) tblModel.getModel()).setRowCount(0);
                }
            }
        });
    }

    private void clearBorrowerDetails() {
        lblBorrowerId.setText("-");
        lblFullName.setText("-");
        lblMemberType.setText("-");
        lblDob.setText("-");
        lblContactNumber.setText("-");
        lblEmail.setText("-");
        lblIdType.setText("-");
        lblIdNumber.setText("-");
        lblStatus.setText("-");
    }

    private void resetBorrowerSearch() {
        isUpdating = true;

        cmbBorrowerName.setSelectedIndex(-1);

        JTextField txt = (JTextField) cmbBorrowerName.getEditor().getEditorComponent();
        txt.setText("Search borrower...");
        txt.setForeground(java.awt.Color.GRAY);

        cmbBorrowerName.hidePopup();
        clearBorrowerDetails();

        isUpdating = false;
    }

    public void loadBorrowers() {
        try {
            Connection con = DB_connect.getConnection();
            String sql = "SELECT borrower_id, first_name, last_name, Id_number FROM borrower ORDER BY first_name ASC";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            isUpdating = true; // ✅ Prevent ItemListener from firing
            cmbBorrowerName.removeAllItems();

            while (rs.next()) {
                cmbBorrowerName.addItem(new Borrower(
                        rs.getInt("borrower_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("Id_number")
                ));
            }

            cmbBorrowerName.setSelectedIndex(-1); // ✅ No default selection
            isUpdating = false; // ✅ Re-enable ItemListener

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading borrowers: " + e.getMessage());
        }
    }

    public void setupSearch() {
        cmbBorrowerName.setEditable(true);

        JTextField txt = (JTextField) cmbBorrowerName.getEditor().getEditorComponent();
        cmbBorrowerName.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        txt.setText("Search borrower...");
        txt.setForeground(java.awt.Color.GRAY);

        // 👇 PASTE THIS HERE (replace old focus listener)
        txt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txt.getText().equals("Search borrower...")) {
                    SwingUtilities.invokeLater(() -> {
                        isUpdating = true;
                        txt.setText("");
                        txt.setForeground(java.awt.Color.BLACK);
                        isUpdating = false;

                    });
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (txt.getText().isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        isUpdating = true;
                        txt.setText("Search borrower...");
                        txt.setForeground(java.awt.Color.GRAY);
                        isUpdating = false;
                    });
                }
            }
        });
        txt.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {

            void update() {
                if (isUpdating || isSelectingBorrower) {
                    return;
                }

                String input = txt.getText().trim();

                if (input.isEmpty()) {
                    cmbBorrowerName.hidePopup();
                    clearBorrowerDetails();
                    return;
                }

                if (input.equals("Search borrower...")) {
                    cmbBorrowerName.hidePopup();
                    clearBorrowerDetails();
                    return;
                }

                SwingUtilities.invokeLater(() -> filterBorrowers(input));
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }
        });
    }

    // REPLACE your filterBorrowers() method with this corrected version
    public void filterBorrowers(String keyword) {
        try {
            isUpdating = true;

            JTextField editor = (JTextField) cmbBorrowerName.getEditor().getEditorComponent();
            String typedText = editor.getText();

            Connection con = DB_connect.getConnection();

            String sql = "SELECT borrower_id, first_name, last_name, Id_number "
                    + "FROM borrower "
                    + "WHERE CONCAT(first_name, ' ', last_name) LIKE ? "
                    + "OR CONCAT(last_name, ' ', first_name) LIKE ? "
                    + "OR first_name LIKE ? "
                    + "OR last_name LIKE ? "
                    + "OR Id_number LIKE ? "
                    + "ORDER BY first_name ASC";

            PreparedStatement ps = con.prepareStatement(sql);
            String search = "%" + keyword + "%";

            for (int i = 1; i <= 5; i++) {
                ps.setString(i, search);
            }

            ResultSet rs = ps.executeQuery();
            cmbBorrowerName.removeAllItems();

            while (rs.next()) {
                cmbBorrowerName.addItem(new Borrower(
                        rs.getInt("borrower_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("Id_number")
                ));
            }

            if (cmbBorrowerName.getItemCount() > 0) {
                cmbBorrowerName.setSelectedIndex(-1);
            }

            editor.setText(typedText);
            editor.setCaretPosition(typedText.length());
            cmbBorrowerName.setPopupVisible(false);

            if (cmbBorrowerName.getItemCount() > 0) {
                cmbBorrowerName.showPopup();
            } else {
                cmbBorrowerName.hidePopup();
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isUpdating = false;
        }
    }

    // REPLACE your loadBorrowerDetails() method with this corrected version
    public void loadBorrowerDetails(int borrowerId) {
        try {
            Connection con = DB_connect.getConnection();

            String sql = "SELECT * FROM borrower WHERE borrower_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, borrowerId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String fullName = rs.getString("first_name") + " " + rs.getString("last_name");

                // UPDATE THE VALUE LABELS (lbl...)
                lblBorrowerId.setText(String.valueOf(rs.getInt("borrower_id")));
                lblFullName.setText(fullName);
                lblMemberType.setText(rs.getString("borrower_type"));
                lblDob.setText(rs.getString("date_of_birth"));
                lblContactNumber.setText(rs.getString("phone_number"));
                lblEmail.setText(rs.getString("email"));
                lblIdType.setText(rs.getString("id_type"));
                lblIdNumber.setText(rs.getString("Id_number"));
                lblStatus.setText(rs.getString("status"));
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error loading borrower details: " + e.getMessage());
        }
    }

    public void loadTransactions() {
        try {
            Connection conn = DB_connect.getConnection();

            String sql = "SELECT br.first_name, br.last_name, bc.acquisition_number, b.title, "
                    + "t.rental_date, t.due_date, t.status, "
                    + "COALESCE(f.amount, 0) AS fine_amount, "
                    + "COALESCE(f.status, 'N/A') AS fine_status "
                    + "FROM transaction t "
                    + "JOIN borrower br ON t.borrower_id = br.borrower_id "
                    + "JOIN book b ON t.book_id = b.book_id "
                    + "JOIN book_copy bc ON t.copy_id = bc.copy_id "
                    + "LEFT JOIN fine f ON t.transaction_id = f.transaction_id "
                    + "WHERE t.status = 'Borrowed' "
                    + "ORDER BY t.rental_date DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tblTransaction.getModel();
            model.setRowCount(0); // ✅ Clear before reloading

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("first_name") + " " + rs.getString("last_name"), // Borrower Name
                    rs.getString("acquisition_number"), // Acquisition No.
                    rs.getString("title"), // Book Title
                    rs.getString("rental_date"), // Rental Date
                    rs.getString("due_date"), // Due Date
                    rs.getString("status"), // Status
                    rs.getString("fine_amount"), // Fine Amount
                    rs.getString("fine_status") // Fine Status
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void insertQueue() {

        Connection conn = DB_connect.getConnection();

    }

    private int getBorrowerIdFromCombo() throws SQLException {

        if (cmbBorrowerName.getSelectedItem() == null
                || cmbBorrowerName.getSelectedItem().toString().trim().isEmpty()) {
            throw new SQLException("No borrower selected!");
        }

        String fullName = cmbBorrowerName.getSelectedItem().toString().trim();
        String[] parts = fullName.split(" ");
        String firstName = parts[0];
        String lastName = parts[1];

        Connection conn = DB_connect.getConnection();
        String sql = "SELECT borrower_id FROM borrower WHERE first_name = ? AND last_name = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, firstName);
        ps.setString(2, lastName);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getInt("borrower_id");
        }
        throw new SQLException("Borrower not found!");
    }

    public void getBookById(String acquisition) {
        try {
            // ✅ CHECK: Borrower selected before adding to queue
            if (cmbBorrowerName.getSelectedItem() == null
                    || cmbBorrowerName.getSelectedItem().toString().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please select a borrower first before adding books to the queue.",
                        "No Borrower Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // ✅ ADD THIS BLOCK — overdue check
            Borrower selected = (Borrower) cmbBorrowerName.getSelectedItem();
            if (overdue(selected.getId())) {
                JOptionPane.showMessageDialog(this,
                        "❌ This borrower has an overdue book!\n"
                        + "They must return and settle any fines before borrowing again.",
                        "Overdue Book Detected", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Connection conn = DB_connect.getConnection();
            String sql = "SELECT l.copy_id, l.book_id, l.acquisition_number, b.title, b.author, c.category_name, l.status "
                    + "FROM book_copy l "
                    + "JOIN book b ON l.book_id = b.book_id "
                    + "JOIN category c ON b.category_id = c.category_id "
                    + "WHERE l.acquisition_number = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, acquisition);
            ResultSet res = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tblModel.getModel();

            if (!res.isBeforeFirst()) {
                // No book found
                JOptionPane.showMessageDialog(this,
                        "No book found with acquisition number: " + acquisition,
                        "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            while (res.next()) {
                String status = res.getString("status");

                if (status.equalsIgnoreCase("Borrowed")) {
                    JOptionPane.showMessageDialog(this,
                            "This book is already BORROWED and is not available for checkout.",
                            "Book Unavailable", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // ✅ CHECK: Already in queue (duplicate acquisition number)
                for (int i = 0; i < model.getRowCount(); i++) {
                    String existingAcq = model.getValueAt(i, 2).toString();
                    if (existingAcq.equalsIgnoreCase(res.getString("acquisition_number"))) {
                        JOptionPane.showMessageDialog(this,
                                "This book is already in the queue!",
                                "Duplicate Book", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                model.addRow(new Object[]{
                    res.getInt("copy_id"), // col 0 - HIDDEN
                    res.getInt("book_id"), // col 1 - HIDDEN
                    res.getString("acquisition_number"), // col 2
                    res.getString("title"), // col 3
                    res.getString("author"), // col 4
                    res.getString("category_name"), // col 5
                    res.getString("status") // col 6
                });

                // ✅ Clear the text field after adding
                txtAcquisition.setText("");

            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    public void loadBorrowerTransactions(int borrowerID) {
        try {
            Connection con = DB_connect.getConnection();
            String sql = "SELECT CONCAT(br.first_name, ' ', br.last_name) AS full_name, "
                    + "bc.acquisition_number, b.title, t.rental_date, t.due_date, t.returned_date, "
                    + "t.status AS transaction_status, "
                    + "COALESCE(f.amount, 0) AS fine_amount, "
                    + "COALESCE(f.status, 'No Fine') AS fine_status "
                    + "FROM transaction t "
                    + "JOIN borrower br ON t.borrower_id = br.borrower_id "
                    + "JOIN book_copy bc ON t.copy_id = bc.copy_id "
                    + "JOIN book b ON bc.book_id = b.book_id "
                    + "LEFT JOIN fine f ON f.transaction_id = t.transaction_id "
                    + "WHERE t.borrower_id = ? "
                    + "AND t.status = 'Borrowed' "
                    + // ← only this line added
                    "ORDER BY t.rental_date DESC";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, borrowerID);
            ResultSet res = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tblTransaction.getModel();
            model.setRowCount(0);

            while (res.next()) {
                model.addRow(new Object[]{
                    res.getString("full_name"),
                    res.getString("acquisition_number"),
                    res.getString("title"),
                    res.getString("rental_date"),
                    res.getString("due_date"),
                    res.getString("transaction_status"),
                    res.getString("fine_amount"),
                    res.getString("fine_status")
                });
            }

        } catch (SQLException error) {
            JOptionPane.showMessageDialog(null, error);
        }
    }

    public void loadTransactions(int borrowerId) {
        try {
            Connection conn = DB_connect.getConnection();

            String sql = "SELECT br.first_name, br.last_name, bc.acquisition_number, b.title, "
                    + "t.rental_date, t.due_date, t.status "
                    + "FROM transaction t "
                    + "JOIN borrower br ON t.borrower_id = br.borrower_id "
                    + "JOIN book b ON t.book_id = b.book_id "
                    + "JOIN book_copy bc ON t.copy_id = bc.copy_id "
                    + "WHERE t.status = 'Borrowed' AND t.borrower_id = ? "
                    + "ORDER BY t.rental_date DESC";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, borrowerId);
            ResultSet rs = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tblTransaction.getModel();
            model.setRowCount(0);

            while (rs.next()) {  // ← fine calc goes INSIDE here
                java.sql.Date dueDate = rs.getDate("due_date");
                java.sql.Date today = java.sql.Date.valueOf(LocalDate.now());

                double fineAmount = 0.0;
                String fineStatus = "No Fine";

                if (today.after(dueDate)) {
                    fineAmount = FineCalculator.calculateFine(dueDate, today);
                    fineStatus = fineAmount > 0 ? "Unpaid" : "No Fine";
                }

                model.addRow(new Object[]{
                    rs.getString("first_name") + " " + rs.getString("last_name"),
                    rs.getString("acquisition_number"),
                    rs.getString("title"),
                    rs.getString("rental_date"),
                    rs.getString("due_date"),
                    rs.getString("status"),
                    fineAmount > 0 ? String.format("₱%.2f", fineAmount) : "₱0.00",
                    fineStatus
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

//        checkFineStatus(borrowerID); // ← auto checks fine after loading
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
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        lblBorrowerId = new javax.swing.JLabel();
        lblFullName = new javax.swing.JLabel();
        lblMemberType = new javax.swing.JLabel();
        lblDob = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblContactNumber = new javax.swing.JLabel();
        lblIdType = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        lblIdNumber = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cmbBorrowerName = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblModel = new javax.swing.JTable();
        jLabel24 = new javax.swing.JLabel();
        btnFindBorrower = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTransaction = new javax.swing.JTable();
        jLabel25 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtAcquisition = new javax.swing.JTextField();
        btnConfirm = new javax.swing.JButton();
        btnAddBook = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
                .addContainerGap(813, Short.MAX_VALUE))
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
                .addGap(17, 17, 17))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setForeground(new java.awt.Color(0, 0, 0));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 51, 51));
        jLabel15.setText("Borrower ID: ");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 51));
        jLabel12.setText("Full Name: ");

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(51, 51, 51));
        jLabel11.setText("Member Type: ");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(51, 51, 51));
        jLabel17.setText("Date of Birth:");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(51, 51, 51));
        jLabel18.setText("Contact No. : ");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(51, 51, 51));
        jLabel19.setText("Email:");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(51, 51, 51));
        jLabel20.setText("ID Type:");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(51, 51, 51));
        jLabel21.setText("ID Number:");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(51, 51, 51));
        jLabel22.setText("Status:");

        lblBorrowerId.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblBorrowerId.setForeground(new java.awt.Color(51, 51, 51));
        lblBorrowerId.setText("-");

        lblFullName.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblFullName.setForeground(new java.awt.Color(51, 51, 51));
        lblFullName.setText("-");

        lblMemberType.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblMemberType.setForeground(new java.awt.Color(51, 51, 51));
        lblMemberType.setText("-");

        lblDob.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblDob.setForeground(new java.awt.Color(51, 51, 51));
        lblDob.setText("-");

        lblEmail.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(51, 51, 51));
        lblEmail.setText("-");

        lblContactNumber.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblContactNumber.setForeground(new java.awt.Color(51, 51, 51));
        lblContactNumber.setText("-");

        lblIdType.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblIdType.setForeground(new java.awt.Color(51, 51, 51));
        lblIdType.setText("-");

        lblStatus.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(51, 51, 51));
        lblStatus.setText("-");

        lblIdNumber.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lblIdNumber.setForeground(new java.awt.Color(51, 51, 51));
        lblIdNumber.setText("-");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(5, 5, 5)
                        .addComponent(lblMemberType, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblBorrowerId, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblFullName, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(49, 49, 49)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblContactNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblDob, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblIdNumber)
                        .addGap(232, 232, 232))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIdType, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel17)
                    .addComponent(jLabel20)
                    .addComponent(lblBorrowerId)
                    .addComponent(lblDob)
                    .addComponent(lblIdType))
                .addGap(32, 32, 32)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel18)
                    .addComponent(jLabel21)
                    .addComponent(lblFullName)
                    .addComponent(lblContactNumber)
                    .addComponent(lblIdNumber))
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel19)
                    .addComponent(jLabel22)
                    .addComponent(lblMemberType)
                    .addComponent(lblEmail)
                    .addComponent(lblStatus))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setText("BORROWER INFORMATION");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setText("Select Borrower:");

        cmbBorrowerName.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cmbBorrowerName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBorrowerNameActionPerformed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        tblModel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Acquisition Number", "Title", "Author", "Category ", "Availability"
            }
        ));
        tblModel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblModelMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblModel);

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(51, 51, 51));
        jLabel24.setText("BOOKS TO BORROW (QUEUE)");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel24)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 764, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(114, 114, 114))
        );

        btnFindBorrower.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnFindBorrower.setText("Find Borrower");
        btnFindBorrower.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnFindBorrowerMouseClicked(evt);
            }
        });
        btnFindBorrower.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindBorrowerActionPerformed(evt);
            }
        });

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setForeground(new java.awt.Color(0, 0, 0));
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel6MouseClicked(evt);
            }
        });

        tblTransaction.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Borrower Name", "Acquisition No.", "Book Title", "Rental Date", "Due Date", "Status", "Fine Amount", "Fines Status"
            }
        ));
        jScrollPane2.setViewportView(tblTransaction);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(51, 51, 51));
        jLabel25.setText("CURRENT BOOK(S) BORROWED");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(31, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(120, 120, 120))
        );

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(51, 51, 51));
        jLabel23.setText("Book Acquisition:");

        btnConfirm.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnConfirm.setText("CONFIRM");
        btnConfirm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnConfirmMouseClicked(evt);
            }
        });
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        btnAddBook.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnAddBook.setText("Add Book");
        btnAddBook.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAddBookMouseClicked(evt);
            }
        });
        btnAddBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBookActionPerformed(evt);
            }
        });

        btnRemove.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnRemove.setText("Remove Selected");
        btnRemove.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnRemoveMouseClicked(evt);
            }
        });
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        btnClear.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnClear.setText("Clear");
        btnClear.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnClearMouseClicked(evt);
            }
        });
        btnClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnClearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnRemove)
                                .addGap(18, 18, 18)
                                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addGap(31, 31, 31)
                                .addComponent(cmbBorrowerName, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(91, 91, 91)
                                .addComponent(btnFindBorrower))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addGap(18, 18, 18)
                                .addComponent(txtAcquisition, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(42, 42, 42)
                                .addComponent(btnAddBook))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(311, 311, 311)
                        .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(816, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbBorrowerName, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnFindBorrower, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAcquisition, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddBook, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRemove, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(134, Short.MAX_VALUE))
        );

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

    private void cmbBorrowerNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBorrowerNameActionPerformed
        // TODO add your handling code here:
        if (isUpdating) {
            return;
        }

        Object selectedItem = cmbBorrowerName.getSelectedItem();

        if (selectedItem instanceof Borrower) {
            isSelectingBorrower = true;

            Borrower selected = (Borrower) selectedItem;
            loadBorrowerDetails(selected.getId());

            JTextField editor = (JTextField) cmbBorrowerName.getEditor().getEditorComponent();
            isUpdating = true;
            editor.setText(selected.getFullName());
            editor.setForeground(java.awt.Color.BLACK);
            cmbBorrowerName.hidePopup();
            isUpdating = false;

            isSelectingBorrower = false;
        }
    }//GEN-LAST:event_cmbBorrowerNameActionPerformed

    private void btnFindBorrowerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFindBorrowerMouseClicked
        String selectedBorrower = cmbBorrowerName.getSelectedItem().toString();

        try {
            Connection con = DB_connect.getConnection();
            String sql = "SELECT borrower_id FROM borrower "
                    + "WHERE CONCAT(first_name, ' ', last_name) = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, selectedBorrower);
            ResultSet res = ps.executeQuery();

            if (res.next()) {
                int borrowerID = res.getInt("borrower_id");
                loadBorrowerTransactions(borrowerID);
            } else {
                JOptionPane.showMessageDialog(null, "Borrower not found!");
            }

        } catch (SQLException error) {
            JOptionPane.showMessageDialog(null, error);
        }
    }//GEN-LAST:event_btnFindBorrowerMouseClicked

    private void btnFindBorrowerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindBorrowerActionPerformed
        String selectedBorrower = cmbBorrowerName.getSelectedItem().toString();

        try {
            Connection con = DB_connect.getConnection();
            String sql = "SELECT borrower_id FROM borrower "
                    + "WHERE CONCAT(first_name, ' ', last_name) = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, selectedBorrower);
            ResultSet res = ps.executeQuery();

            if (res.next()) {
                int borrowerID = res.getInt("borrower_id");
                loadBorrowerTransactions(borrowerID); // ← this loads to tblTransaction
            } else {
                JOptionPane.showMessageDialog(null, "Borrower not found!");
            }

        } catch (SQLException error) {
            JOptionPane.showMessageDialog(null, error);
        }

    }//GEN-LAST:event_btnFindBorrowerActionPerformed

    private void btnConfirmMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnConfirmMouseClicked

    }//GEN-LAST:event_btnConfirmMouseClicked

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
// ✅ 1. CHECK: Queue is not empty
        if (tblModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No books in the queue!", "Empty Queue", JOptionPane.WARNING_MESSAGE);
            return;
        }

// ✅ 2. CHECK: Borrower is selected
        if (!(cmbBorrowerName.getSelectedItem() instanceof Borrower)) {
            JOptionPane.showMessageDialog(this,
                    "Please select a borrower before proceeding.",
                    "No Borrower Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

// ✅ 3. CHECK: Duplicate in queue (same acquisition number or same title)
        DefaultTableModel queueModel = (DefaultTableModel) tblModel.getModel();
        for (int i = 0; i < queueModel.getRowCount(); i++) {
            String acqI = queueModel.getValueAt(i, 2).toString();
            String titleI = queueModel.getValueAt(i, 3).toString();

            for (int j = i + 1; j < queueModel.getRowCount(); j++) {
                String acqJ = queueModel.getValueAt(j, 2).toString();
                String titleJ = queueModel.getValueAt(j, 3).toString();

                if (acqI.equalsIgnoreCase(acqJ)) {
                    JOptionPane.showMessageDialog(this,
                            "❌ Duplicate found in queue!\n"
                            + "Acquisition Number: " + acqI + " appears more than once.\n\n"
                            + "Please remove the duplicate before proceeding.",
                            "Duplicate Book", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (titleI.equalsIgnoreCase(titleJ)) {
                    JOptionPane.showMessageDialog(this,
                            "❌ Duplicate found in queue!\n"
                            + "Book Title: \"" + titleI + "\" appears more than once.\n\n"
                            + "Please remove the duplicate before proceeding.",
                            "Duplicate Book", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }

        try {
            int borrowerId = getBorrowerIdFromCombo();
            Connection conn = DB_connect.getConnection();

            // ✅ 4. CHECK: Borrower status (Active/Inactive/Blocked)
            String statusSQL = "SELECT status, borrower_type FROM borrower WHERE borrower_id = ?";
            PreparedStatement psStatus = conn.prepareStatement(statusSQL);
            psStatus.setInt(1, borrowerId);
            ResultSet rsStatus = psStatus.executeQuery();

            if (rsStatus.next()) {
                String borrowerStatus = rsStatus.getString("status");
                String borrowerType = rsStatus.getString("borrower_type");

                if (!borrowerStatus.equalsIgnoreCase("Active")) {
                    JOptionPane.showMessageDialog(this,
                            "❌ This borrower is " + borrowerStatus + " and cannot borrow books.",
                            "Borrower Blocked", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // ✅ 5. CHECK: Same title already borrowed in transaction table
                for (int i = 0; i < queueModel.getRowCount(); i++) {
                    String queueTitle = queueModel.getValueAt(i, 3).toString();

                    String dupSQL = "SELECT t.transaction_id FROM transaction t "
                            + "JOIN book b ON t.book_id = b.book_id "
                            + "WHERE t.borrower_id = ? AND t.status = 'Borrowed' "
                            + "AND LOWER(b.title) = LOWER(?)";
                    PreparedStatement psDup = conn.prepareStatement(dupSQL);
                    psDup.setInt(1, borrowerId);
                    psDup.setString(2, queueTitle);
                    ResultSet rsDup = psDup.executeQuery();

                    if (rsDup.next()) {
                        JOptionPane.showMessageDialog(this,
                                "❌ Cannot proceed!\n"
                                + "\"" + queueTitle + "\" is already currently borrowed by this borrower.\n\n"
                                + "A borrower can only borrow one copy of the same title at a time.",
                                "Duplicate Title", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                // ✅ 6. CHECK: Borrow limit based on borrower_type
                int borrowLimit;
                switch (borrowerType.toLowerCase()) {
                    case "teacher":
                        borrowLimit = 5;
                        break;
                    case "guest":
                        borrowLimit = 3;
                        break;
                    case "student":
                    default:
                        borrowLimit = 4;
                        break;
                }

                String countSQL = "SELECT COUNT(*) FROM transaction WHERE borrower_id = ? AND status = 'Borrowed'";
                PreparedStatement psCount = conn.prepareStatement(countSQL);
                psCount.setInt(1, borrowerId);
                ResultSet rsCount = psCount.executeQuery();
                rsCount.next();
                int currentlyBorrowed = rsCount.getInt(1);
                int queueCount = queueModel.getRowCount();
                int totalAfterBorrow = currentlyBorrowed + queueCount;

                if (totalAfterBorrow > borrowLimit) {
                    JOptionPane.showMessageDialog(this,
                            "❌ Borrow limit exceeded!\n"
                            + "Borrower Type      : " + borrowerType + "\n"
                            + "Borrow Limit       : " + borrowLimit + "\n"
                            + "Currently Borrowed : " + currentlyBorrowed + "\n"
                            + "Trying to Borrow   : " + queueCount + "\n\n"
                            + "Please reduce the number of books in the queue.",
                            "Limit Exceeded", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // ✅ 7. BATCH INSERT
            conn.setAutoCommit(false);

            String insertSQL = "INSERT INTO transaction (borrower_id, book_id, copy_id, rental_date, due_date, status) "
                    + "VALUES (?, ?, ?, ?, ?, 'Borrowed')";
            String updateSQL = "UPDATE book_copy SET status = 'Borrowed' WHERE copy_id = ?";

            PreparedStatement psInsert = conn.prepareStatement(insertSQL);
            PreparedStatement psUpdate = conn.prepareStatement(updateSQL);

            // ✅ Save count BEFORE clearing
            int totalBorrowed = queueModel.getRowCount();

            for (int i = 0; i < queueModel.getRowCount(); i++) {
                int copyId = (int) queueModel.getValueAt(i, 0);
                int bookId = (int) queueModel.getValueAt(i, 1);

                psInsert.setInt(1, borrowerId);
                psInsert.setInt(2, bookId);
                psInsert.setInt(3, copyId);
                psInsert.setDate(4, Date.valueOf(LocalDate.now()));
                psInsert.setDate(5, Date.valueOf(LocalDate.now().plusDays(7)));
                psInsert.addBatch();

                psUpdate.setInt(1, copyId);
                psUpdate.addBatch();
            }

            psInsert.executeBatch();
            psUpdate.executeBatch();
            conn.commit();

            // ✅ Use saved count, not queueModel.getRowCount() which is 0 after clear
            queueModel.setRowCount(0);
            loadTransactions(borrowerId);

            JOptionPane.showMessageDialog(this,
                    "✅ " + totalBorrowed + " book(s) successfully borrowed!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnConfirmActionPerformed
    private boolean overdue(int borrowerId) {
        boolean isOverdue = false;

        try {
            Connection conn = DB_connect.getConnection();

            String sql = "SELECT t.due_date FROM transaction t "
                    + "WHERE t.borrower_id = ? AND t.status = 'Borrowed' "
                    + "AND t.due_date < CURDATE()";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, borrowerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                isOverdue = true; // at least one overdue book found
            }

            rs.close();
            ps.close();
            conn.close();

        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, "Overdue check error: " + err.getMessage());
        }

        return isOverdue;
    }

    private void btnAddBookMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAddBookMouseClicked

        String acquisition = txtAcquisition.getText().trim();

        getBookById(acquisition);
    }//GEN-LAST:event_btnAddBookMouseClicked

    private void btnAddBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBookActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddBookActionPerformed

    private void btnRemoveMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRemoveMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoveMouseClicked

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        int selectedRow = tblModel.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a book to remove.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String title = tblModel.getValueAt(selectedRow, 3).toString(); // col 3 = title

        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove \"" + title + "\" from the queue?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            ((DefaultTableModel) tblModel.getModel()).removeRow(selectedRow);
        }
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnClearMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnClearMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnClearMouseClicked

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnClearActionPerformed
        if (tblModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Queue is already empty!",
                    "Empty Queue", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to clear all books in the queue?",
                "Confirm Clear", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            ((DefaultTableModel) tblModel.getModel()).setRowCount(0);
        }
    }//GEN-LAST:event_btnClearActionPerformed

    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel6MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel6MouseClicked

    private void tblModelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblModelMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tblModelMouseClicked

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
        java.awt.EventQueue.invokeLater(() -> new AddLoan1().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBook;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnFindBorrower;
    private javax.swing.JButton btnRemove;
    private javax.swing.JComboBox<Borrower> cmbBorrowerName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblBorrowerId;
    private javax.swing.JLabel lblContactNumber;
    private javax.swing.JLabel lblDob;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblIdNumber;
    private javax.swing.JLabel lblIdType;
    private javax.swing.JLabel lblMemberType;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JTable tblModel;
    private javax.swing.JTable tblTransaction;
    private javax.swing.JTextField txtAcquisition;
    private javax.swing.JLabel txtBooks;
    private javax.swing.JLabel txtDashboard;
    private javax.swing.JLabel txtLogout1;
    private javax.swing.JLabel txtMembers;
    private javax.swing.JLabel txtReports;
    private javax.swing.JLabel txtTransactions;
    // End of variables declaration//GEN-END:variables
}
