/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package My_Form;

import My_Classes.DB_connect;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Administrator
 */
public class AddLoans extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AddLoans.class.getName());

    /**
     * Creates new form NewJFrame
     */
    private String check = "";
    private String action = "";
    private int loanId;
    private int borrowerId = 0;
    private boolean isLoading = false;

    public AddLoans() {
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
        populateTable();
        loadBorrowerName(cmbBorrowerName);
        cmbBorrowerName.addActionListener(e -> {
            if (cmbBorrowerName.getSelectedItem() != null) {
                String name = cmbBorrowerName.getSelectedItem().toString();
                borrowerId = getBorrowerId(name);
            }
        });

        loodBooks(cmbBook);
        cmbBook.addActionListener(e -> {
            if (!isLoading) {
                loadAcquisitionNumber(cmbAcquisitionNumber);
            }
        });
        loadAcquisitionNumber(cmbAcquisitionNumber);
        rentalDate.addPropertyChangeListener("date", e -> {
            java.util.Date selectedRental = rentalDate.getDate();
            if (selectedRental != null) {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(selectedRental);
                cal.add(java.util.Calendar.DAY_OF_MONTH, 7);
                dueDate.setDate(cal.getTime());
            }
        });
    }

    private void makeEnabled() {
        cmbBook.setEnabled(true);
        btnAdd.setEnabled(false);
        btnClose.setText("Cancel");
        cmbAcquisitionNumber.setEnabled(true);
        cmbBorrowerName.setEnabled(true);
        rentalDate.setEnabled(true);  // ← ADD
        dueDate.setEnabled(true);     // ← ADD
    }

    private void setDefault() {
        cmbBook.setEnabled(false);
        rentalDate.setEnabled(false);
        dueDate.setEnabled(false);
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnCancel.setEnabled(false);
        cmbAcquisitionNumber.setEnabled(false);
        cmbBorrowerName.setEnabled(false);
        rentalDate.setDate(new java.util.Date());
        dueDate.setDate(new java.util.Date());
        btnClose.setText("Close");

        loodBooks(cmbBook);
        if (cmbBorrowerName.getItemCount() > 0) {
            cmbBorrowerName.setSelectedIndex(0);
        }

        if (cmbBook.getItemCount() > 0) {
            cmbBook.setSelectedIndex(0);
        }

        if (cmbAcquisitionNumber.getItemCount() > 0) {
            cmbAcquisitionNumber.setSelectedIndex(0);
        }

        tblTransactions.clearSelection(); // ← ADD
    }

    private int getBorrowerId(String name) {
        int id = 0;

        try {
            Connection con = DB_connect.getConnection();
            String sql = "SELECT borrower_id FROM borrower WHERE CONCAT(first_name, ' ', last_name) = ?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, name);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                id = rs.getInt("borrower_id");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

        return id;
    }

    public void populateTable() {

        try {

            Connection con = DB_connect.getConnection();

            String sql = "SELECT t.transaction_id, "
                    + "CONCAT(b.first_name, ' ', b.last_name) AS full_name, "
                    + "bo.title, bc.acquisition_number, t.rental_date, t.due_date, t.status "
                    + "FROM `transaction` t "
                    + "JOIN borrower b ON t.borrower_id = b.borrower_id "
                    + "JOIN book bo ON t.book_id = bo.book_id "
                    + "JOIN book_copy bc ON t.copy_id = bc.copy_id";

            PreparedStatement pst = con.prepareStatement(sql);

            DefaultTableModel model = (DefaultTableModel) tblTransactions.getModel();
            model.setRowCount(0);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                Date rent = rs.getDate("rental_date");
                String rentFormatDate = "";
                if (rent != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    rentFormatDate = sdf.format(rent);
                }

                Date due = rs.getDate("due_date");
                String dueFormatDate = "";
                if (due != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    dueFormatDate = sdf.format(due);
                }

                model.addRow(new Object[]{
                    rs.getInt("transaction_id"),
                    rs.getString("full_name"),
                    rs.getString("title"),
                    rs.getString("acquisition_number"),
                    rentFormatDate,
                    dueFormatDate,
                    rs.getString("status")
                });
            }

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, error);
        }
    }

    // getting the cmbBorrowName;
    public void loadBorrowerName(JComboBox<String> cmbBorrowerName) {

        try {
            Connection con = DB_connect.getConnection();

            String sql = "SELECT borrower_id, CONCAT(first_name, ' ', last_name) AS full_name "
                    + "FROM borrower";

            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            cmbBorrowerName.removeAllItems();

            while (rs.next()) {
                cmbBorrowerName.addItem(rs.getString("full_name"));
            }

            // Optional auto-select first item
            if (cmbBorrowerName.getItemCount() > 0) {
                cmbBorrowerName.setSelectedIndex(0);
                String name = cmbBorrowerName.getSelectedItem().toString();
                borrowerId = getBorrowerId(name);
            }

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, error);
        }
    }

    public void loodBooks(JComboBox<String> cmbBook) {
        isLoading = true;
        try {
            Connection con = DB_connect.getConnection();
            String sql = "SELECT DISTINCT b.title "
                    + "FROM book b "
                    + "WHERE EXISTS ("
                    + "   SELECT 1 FROM book_copy bc "
                    + "   WHERE bc.book_id = b.book_id "
                    + "   AND LOWER(TRIM(bc.status)) = 'available'"
                    + ")";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            cmbBook.removeAllItems();

            while (rs.next()) {
                cmbBook.addItem(rs.getString("title"));
            }

            if (cmbBook.getItemCount() > 0) {
                cmbBook.setSelectedIndex(0);
            }

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, error);
        } finally {
            isLoading = false;
        }
    }

    public int getBookNameById(String bookTitle) {
        int id = 0;

        try {
            Connection con = DB_connect.getConnection();
            String sql = "SELECT book_id FROM book WHERE title = ?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, bookTitle);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                id = rs.getInt("book_id");
            }
        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, error);
        }
        return id;
    }

    // getter of bookid;
    private int getBookId(String bookTitle) {
        int id = 0;

        try {
            Connection con = DB_connect.getConnection();
            String sql = "SELECT book_id FROM book WHERE title = ?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, bookTitle);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                id = rs.getInt("book_id");
            }

        } catch (Exception err) {

        }
        return id;
    }

    // generator for acquisition number;
    public void loadAcquisitionNumber(JComboBox<String> cmbAcquisitionNumber) {

        if (cmbBook.getSelectedItem() == null) {
            return; // ✅ prevent crash
        }
        String bookTitle = cmbBook.getSelectedItem().toString();
        int id = getBookId(bookTitle);

        if (id == 0) {
            return; // ✅ prevent empty query
        }
        try {
            Connection con = DB_connect.getConnection();

            String sql = "SELECT acquisition_number FROM book_copy "
                    + "WHERE book_id = ? "
                    + "AND LOWER(TRIM(status)) = 'available'";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, id);

            ResultSet rs = pst.executeQuery();

            cmbAcquisitionNumber.removeAllItems();

            while (rs.next()) {
                cmbAcquisitionNumber.addItem(rs.getString("acquisition_number"));
            }

            // ✅ Optional: auto-select first item
            if (cmbAcquisitionNumber.getItemCount() > 0) {
                cmbAcquisitionNumber.setSelectedIndex(0);
            }

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, error);
        }
    }

    //copyid getter:
    public int getCopyId(String acquisition) {
        int id = 0;

        try {

            Connection con = DB_connect.getConnection();
            String sql = "SELECT copy_id FROM book_copy WHERE acquisition_number = ?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, acquisition);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                id = rs.getInt("copy_id");
            }

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, error);
        }

        return id;

    }

    // safevalue indicator:
    private String safeValue(DefaultTableModel model, int row, int col) {
        Object val = model.getValueAt(row, col);
        return (val == null) ? "" : val.toString();
    }

    // getter borrower status:
    private int getLimit(int borrowerId) {
        int lim = 0;

        try {

            Connection con = DB_connect.getConnection();
            String query = "SELECT borrow_limit FROM borrower WHERE borrower_id = ?";
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, borrowerId);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                lim = rs.getInt("borrow_limit");
            }

        } catch (Exception err) {

        }
        return lim;
    }
    // getter for book_copies status:

    private String getBookStatus(int copyId) {

        String status = "";

        try {
            Connection con = DB_connect.getConnection();
            String sql = "SELECT status FROM book_copy WHERE copy_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, copyId);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                status = rs.getString("status");
            }

        } catch (Exception err) {

        }

        return status;
    }

    // get count:
    private int getCount(int borrowerId) {
        int count = 0;
        try {

            Connection con = DB_connect.getConnection();
            String sql = "SELECT COUNT(*) FROM transaction WHERE borrower_id = ? AND status = 'Borrowed'";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setInt(1, borrowerId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err);
        }

        return count;
    }

    public void loadBooksForUpdate(JComboBox<String> cmbBook, String currentBookTitle) {
        isLoading = true;
        try {
            Connection con = DB_connect.getConnection();
            String sql = "SELECT DISTINCT b.title "
                    + "FROM book b "
                    + "WHERE EXISTS ("
                    + "   SELECT 1 FROM book_copy bc "
                    + "   WHERE bc.book_id = b.book_id "
                    + "   AND LOWER(TRIM(bc.status)) = 'available'"
                    + ") OR b.title = ?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, currentBookTitle);
            ResultSet rs = pst.executeQuery();
            cmbBook.removeAllItems();

            while (rs.next()) {
                cmbBook.addItem(rs.getString("title"));
            }

            cmbBook.setSelectedItem(currentBookTitle);

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, error);
        } finally {
            isLoading = false;
        }
    }

    public void loadAcquisitionNumberForUpdate(JComboBox<String> cmbAcquisitionNumber, String bookTitle, String currentAcquisition) {
        int bookId = getBookId(bookTitle);
        if (bookId == 0) {
            return;
        }

        try {
            Connection con = DB_connect.getConnection();

            // Show available copies OR the currently borrowed acquisition number
            String sql = "SELECT acquisition_number FROM book_copy "
                    + "WHERE book_id = ? "
                    + "AND (LOWER(TRIM(status)) = 'available' OR acquisition_number = ?)";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, bookId);
            pst.setString(2, currentAcquisition);

            ResultSet rs = pst.executeQuery();
            cmbAcquisitionNumber.removeAllItems();

            while (rs.next()) {
                cmbAcquisitionNumber.addItem(rs.getString("acquisition_number"));
            }

            cmbAcquisitionNumber.setSelectedItem(currentAcquisition);

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, error);
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
        jLabel14 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTransactions = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        cmbBorrowerName = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        cmbBook = new javax.swing.JComboBox<>();
        cmbAcquisitionNumber = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        dueDate = new com.toedter.calendar.JDateChooser();
        rentalDate = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        btnClose = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();

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
                .addGap(17, 17, 17))
        );

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/My_Image/1150612 (2).png"))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setText("SEARCH: ");

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

        tblTransactions.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tblTransactions.setModel(new javax.swing.table.DefaultTableModel(
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
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Loan_ID", "Borrower Name", "Book", "Acquisition Number", "Rental Date", "Due Date", "Status"
            }
        ));
        tblTransactions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTransactionsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblTransactions);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setText("Acquisition Number");

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 51));
        jLabel12.setText("Book");

        cmbBorrowerName.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cmbBorrowerName.setEnabled(false);
        cmbBorrowerName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBorrowerNameActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setText("Loan Book");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 51, 51));
        jLabel15.setText("Borrower Name: ");

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

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 51));
        jLabel16.setText("Due Date: ");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(51, 51, 51));
        jLabel19.setText("Rental Date:");

        dueDate.setDateFormatString("yyyy-MM-dd");
        dueDate.setEnabled(false);

        rentalDate.setDateFormatString("yyyy-MM-dd");
        rentalDate.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(444, 444, 444)
                        .addComponent(jLabel9))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(128, 128, 128)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel15)
                                .addComponent(jLabel12)
                                .addComponent(cmbBorrowerName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cmbBook, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cmbAcquisitionNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 148, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel16)
                            .addComponent(dueDate, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rentalDate, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(110, 110, 110))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(5, 5, 5)
                        .addComponent(cmbBorrowerName, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbBook, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rentalDate, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dueDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(19, 19, 19)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbAcquisitionNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

        btnClose.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        btnAdd.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnAdd.setText("Add");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnSave.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnSave.setText("Save");
        btnSave.setEnabled(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnUpdate.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnCancel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.setEnabled(false);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnCancel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnUpdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(54, 54, 54))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(151, 151, 151)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1225, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(281, 281, 281)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(485, 485, 485))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 210, Short.MAX_VALUE)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(168, 168, 168))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel8)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void cmbBorrowerNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBorrowerNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbBorrowerNameActionPerformed

    private void cmbBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBookActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbBookActionPerformed

    private void cmbAcquisitionNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAcquisitionNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbAcquisitionNumberActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        // TODO add your handling code here:
        if (btnClose.getText().equalsIgnoreCase("Close")) {
            Transactions transactions = new Transactions();
            transactions.setVisible(true);
            this.dispose();
        } else {
            setDefault();
        }
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        check = "add";
        loodBooks(cmbBook);
        makeEnabled();

        btnSave.setEnabled(true);
        rentalDate.setDate(new java.util.Date());

    }//GEN-LAST:event_btnAddActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        try {
            // null checks
            if (cmbBook.getSelectedItem() == null || cmbAcquisitionNumber.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(null, "Please select a book and acquisition number.");
                return;
            }

            java.util.Date rent = rentalDate.getDate();
            java.util.Date due = dueDate.getDate();
            if (rent == null || due == null) {
                JOptionPane.showMessageDialog(null, "Please select rental and due dates.");
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String rentStr = sdf.format(rent);
            String dueStr = sdf.format(due);
            String todayStr = sdf.format(new java.util.Date());

            if (rentStr.compareTo(todayStr) < 0) {
                JOptionPane.showMessageDialog(null, "Put a proper rental date.");
                return;
            }
            if (dueStr.compareTo(todayStr) < 0) {
                JOptionPane.showMessageDialog(null, "Put a proper due date.");
                return;
            }

            String bookTitle = cmbBook.getSelectedItem().toString();
            String acquisition = cmbAcquisitionNumber.getSelectedItem().toString();
            int bookid = getBookId(bookTitle);
            int copyid = getCopyId(acquisition);

            Date renta = new Date(rent.getTime());
            Date dateDue = new Date(due.getTime());

            Connection con = DB_connect.getConnection();

            if (check.equalsIgnoreCase("add")) {
                // ─── INSERT ───
                int limit = getLimit(borrowerId);
                int count = getCount(borrowerId);

                if (count >= limit) {
                    JOptionPane.showMessageDialog(null, "Borrower limit reached.");
                    return;
                }
                if (getBookStatus(copyid).equalsIgnoreCase("Borrowed")) {
                    JOptionPane.showMessageDialog(null, "This book is already borrowed.");
                    return;
                }

                String sql = "INSERT INTO transaction (borrower_id, book_id, copy_id, rental_date, due_date, status) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, borrowerId);
                pst.setInt(2, bookid);
                pst.setInt(3, copyid);
                pst.setDate(4, renta);
                pst.setDate(5, dateDue);
                pst.setString(6, "Borrowed");
                pst.executeUpdate();

                String bookUpdate = "UPDATE book_copy SET status = 'Borrowed' WHERE copy_id = ?";
                PreparedStatement ps = con.prepareStatement(bookUpdate);
                ps.setInt(1, copyid);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(null, "The book has been borrowed.");

            } else if (check.equalsIgnoreCase("update")) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to update this transaction?",
                        "Confirm Update",
                        JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

                // ── Get the OLD copy_id from the transaction BEFORE updating ──
                int oldCopyId = 0;
                String getOldCopy = "SELECT copy_id FROM transaction WHERE transaction_id = ?";
                PreparedStatement getOld = con.prepareStatement(getOldCopy);
                getOld.setInt(1, loanId);
                ResultSet oldRs = getOld.executeQuery();
                if (oldRs.next()) {
                    oldCopyId = oldRs.getInt("copy_id");
                }

                // ── Update the transaction ──
                String sql = "UPDATE transaction SET borrower_id=?, book_id=?, copy_id=?, rental_date=?, due_date=? WHERE transaction_id=?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setInt(1, borrowerId);
                pst.setInt(2, bookid);
                pst.setInt(3, copyid);
                pst.setDate(4, renta);
                pst.setDate(5, dateDue);
                pst.setInt(6, loanId);
                pst.executeUpdate();

                // ── If the book copy changed, update both old and new ──
                if (oldCopyId != copyid) {
                    // Free the old copy
                    String freeOld = "UPDATE book_copy SET status = 'Available' WHERE copy_id = ?";
                    PreparedStatement psFree = con.prepareStatement(freeOld);
                    psFree.setInt(1, oldCopyId);
                    psFree.executeUpdate();

                    // Mark the new copy as Borrowed
                    String markNew = "UPDATE book_copy SET status = 'Borrowed' WHERE copy_id = ?";
                    PreparedStatement psNew = con.prepareStatement(markNew);
                    psNew.setInt(1, copyid);
                    psNew.executeUpdate();
                }

                JOptionPane.showMessageDialog(null, "Transaction updated successfully.");
            }

            con.close();
            populateTable();
            setDefault();

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, error);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        btnClose.setText("Cancel");
        check = "update";
        btnSave.setEnabled(true);
        btnUpdate.setEnabled(false);
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        try {
            int row = tblTransactions.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Please select a transaction to cancel.");
                return;
            }

            DefaultTableModel model = (DefaultTableModel) tblTransactions.getModel();

            String status = safeValue(model, row, 6);
            if (!status.equalsIgnoreCase("Borrowed")) {
                JOptionPane.showMessageDialog(null, "Only borrowed transactions can be cancelled.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to cancel this borrow?",
                    "Confirm Cancel",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            int transactionId = Integer.parseInt(safeValue(model, row, 0));
            int copyId = getCopyId(safeValue(model, row, 3));

            Connection con = DB_connect.getConnection();

            // ✅ UPDATED: include cancelled_date
            String sql = """
        UPDATE transaction
        SET status = 'Cancelled',
            cancelled_date = CURDATE()
        WHERE transaction_id = ?
    """;

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, transactionId);
            pst.executeUpdate();

            // update book copy
            String bookSql = "UPDATE book_copy SET status = 'Available' WHERE copy_id = ?";
            PreparedStatement ps = con.prepareStatement(bookSql);
            ps.setInt(1, copyId);
            ps.executeUpdate();

            con.close();

            populateTable();
            loadBorrowerName(cmbBorrowerName);
            loodBooks(cmbBook);

            JOptionPane.showMessageDialog(null, "Borrow has been cancelled successfully.");
            setDefault();

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, error);
        }
    }//GEN-LAST:event_btnCancelActionPerformed

    private void tblTransactionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTransactionsMouseClicked
        // TODO add your handling code here:
        int row = tblTransactions.getSelectedRow();
        if (row != -1) {
            DefaultTableModel model = (DefaultTableModel) tblTransactions.getModel();

            loanId = Integer.parseInt(safeValue(model, row, 0));
            String borrowerName = safeValue(model, row, 1);
            String bookTitle = safeValue(model, row, 2);
            String acquisition = safeValue(model, row, 3);
            String rentalDateStr = safeValue(model, row, 4);
            String dueDateStr = safeValue(model, row, 5);
            String status = safeValue(model, row, 6);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            try {
                if (!rentalDateStr.isEmpty()) {
                    rentalDate.setDate(sdf.parse(rentalDateStr));
                }
                if (!dueDateStr.isEmpty()) {
                    dueDate.setDate(sdf.parse(dueDateStr));
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error parsing date: " + e.getMessage());
            }

            // enable fields so setSelectedItem works
            cmbBorrowerName.setEnabled(true);
            cmbBook.setEnabled(true);
            cmbAcquisitionNumber.setEnabled(true);
            rentalDate.setEnabled(true);

            // populate borrower
            cmbBorrowerName.setSelectedItem(borrowerName);

            // ── Load books: current book's copy is borrowed so use special loader ──
            loadBooksForUpdate(cmbBook, bookTitle);

            // ── Load acquisition: include current borrowed copy ──
            loadAcquisitionNumberForUpdate(cmbAcquisitionNumber, bookTitle, acquisition);

            // only allow Update and Cancel if status is Borrowed
            if (status.equalsIgnoreCase("Borrowed")) {
                btnUpdate.setEnabled(true);
                btnCancel.setEnabled(true);
            } else {
                btnUpdate.setEnabled(false);
                btnCancel.setEnabled(false);
            }

            btnClose.setText("Cancel");
            btnAdd.setEnabled(false);
            btnSave.setEnabled(false);
        }
    }//GEN-LAST:event_tblTransactionsMouseClicked

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:

        String keyword = txtSearch.getText().trim().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) tblTransactions.getModel();

        if (keyword.isEmpty()) {
            populateTable();
            return;
        }

        try {
            Connection con = DB_connect.getConnection();
            String sql = "SELECT t.transaction_id, "
                    + "CONCAT(b.first_name, ' ', b.last_name) AS full_name, "
                    + "bo.title, bc.acquisition_number, t.rental_date, t.due_date, t.status "
                    + "FROM `transaction` t "
                    + "JOIN borrower b ON t.borrower_id = b.borrower_id "
                    + "JOIN book bo ON t.book_id = bo.book_id "
                    + "JOIN book_copy bc ON t.copy_id = bc.copy_id "
                    + "WHERE LOWER(CONCAT(b.first_name, ' ', b.last_name)) LIKE ? "
                    + "OR LOWER(bo.title) LIKE ? "
                    + "OR LOWER(bc.acquisition_number) LIKE ? "
                    + "OR LOWER(t.status) LIKE ?";

            PreparedStatement pst = con.prepareStatement(sql);
            String pattern = "%" + keyword + "%";
            pst.setString(1, pattern);
            pst.setString(2, pattern);
            pst.setString(3, pattern);
            pst.setString(4, pattern);

            ResultSet rs = pst.executeQuery();
            model.setRowCount(0);

            while (rs.next()) {
                Date rent = rs.getDate("rental_date");
                String rentFormatDate = "";
                if (rent != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    rentFormatDate = sdf.format(rent);
                }

                Date due = rs.getDate("due_date");
                String dueFormatDate = "";
                if (due != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    dueFormatDate = sdf.format(due);
                }

                model.addRow(new Object[]{
                    rs.getInt("transaction_id"),
                    rs.getString("full_name"),
                    rs.getString("title"),
                    rs.getString("acquisition_number"),
                    rentFormatDate,
                    dueFormatDate,
                    rs.getString("status")
                });
            }
            con.close();

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, error);
        }

    }//GEN-LAST:event_txtSearchKeyReleased

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
        java.awt.EventQueue.invokeLater(() -> new AddLoans().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cmbAcquisitionNumber;
    private javax.swing.JComboBox<String> cmbBook;
    private javax.swing.JComboBox<String> cmbBorrowerName;
    private com.toedter.calendar.JDateChooser dueDate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser rentalDate;
    private javax.swing.JTable tblTransactions;
    private javax.swing.JLabel txtBooks;
    private javax.swing.JLabel txtDashboard;
    private javax.swing.JLabel txtLogout1;
    private javax.swing.JLabel txtMembers;
    private javax.swing.JLabel txtReports;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JLabel txtTransactions;
    // End of variables declaration//GEN-END:variables
}
