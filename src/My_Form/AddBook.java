/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package My_Form;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import My_Classes.DB_connect;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.sql.Connection;

/**
 *
 * @author Administrator
 */
public class AddBook extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AddBook.class.getName());
    public static String check = "";
    public static int bookid;

    /**
     * Creates new form NewJFrame
     */
    public AddBook() {
        setUndecorated(true); // REQUIRED for opacity
        initComponents();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        tbl.getTableHeader().setPreferredSize(
                new java.awt.Dimension(tbl.getTableHeader().getWidth(), 50)
        );
        tbl.getTableHeader().setFont(
                tbl.getTableHeader().getFont().deriveFont(18f)
        );

        tbl.setFont(tbl.getFont().deriveFont(16f));

        populateTable();
        loadCategories(cmbCategory);
        yearPublished.setYear(java.time.Year.now().getValue());
        yearPublished.setMaximum(java.time.Year.now().getValue());

    }

    private void populateTable() {
        int colCount;

        try {

            java.sql.Connection cn = DB_connect.getConnection();
            Statement st = cn.createStatement();
            ResultSet res = st.executeQuery(
                    "SELECT b.book_id, b.title, b.author, c.category_name, b.publisher, b.publication_year, b.isbn, b.shelf_location, b.remarks, b.class, b.pages, b.source_of_fund, b.cost_price "
                    + "FROM book b LEFT JOIN category c ON b.category_id = c.category_id"
            );

            DefaultTableModel tblModel = (DefaultTableModel) tbl.getModel();
            tblModel.setRowCount(0);

            while (res.next()) {
                Vector<Object> row = new Vector<>();

                row.add(res.getInt("book_id"));
                row.add(res.getString("class"));
                row.add(res.getString("title"));
                row.add(res.getString("author"));
                row.add(res.getString("category_name"));
                row.add(res.getInt("pages"));
                row.add(res.getString("source_of_fund"));
                row.add(res.getString("cost_price"));
                row.add(res.getString("publisher"));
                row.add(res.getString("publication_year"));
                row.add(res.getString("remarks"));
                row.add(res.getString("isbn"));
                row.add(res.getString("shelf_location"));
                tblModel.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, ex);
        }

    }

    public void loadCategories(JComboBox<String> cmbCategory) {
        try {
            java.sql.Connection con = DB_connect.getConnection();
            String sql = "SELECT category_name FROM category";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            cmbCategory.removeAllItems();

            while (rs.next()) {
                cmbCategory.addItem(rs.getString("category_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeEnabled() {

        txtTitle.setEnabled(true);
        cmbCategory.setEnabled(true);
        txtAuthor.setEnabled(true);
        txtPublisher.setEnabled(true);
        yearPublished.setEnabled(true);
        txtClass.setEnabled(true);
        txtIsbn.setEnabled(true);
        txtRemarks.setEnabled(true);
        txtSourceOfFunds.setEnabled(true);
        txtPages.setEnabled(true);
        txtCostPrice.setEnabled(true);
        txtShelfLoc.setEnabled(true);
    }

    private void setDefault() {
        txtTitle.setText("");
        txtAuthor.setText("");
        yearPublished.setYear(java.time.Year.now().getValue());
        txtPublisher.setText("");
        txtClass.setText("");
        txtCostPrice.setText("");
        txtIsbn.setText("");
        txtPages.setText("");
        txtSourceOfFunds.setText("");
        txtShelfLoc.setText("");
        txtRemarks.setText("");
        txtTitle.setEnabled(false);
        cmbCategory.setEnabled(false);
        txtAuthor.setEnabled(false);
        txtPublisher.setEnabled(false);
        yearPublished.setEnabled(false);
        txtClass.setEnabled(false);
        txtIsbn.setEnabled(false);
        txtRemarks.setEnabled(false);
        txtSourceOfFunds.setEnabled(false);
        txtPages.setEnabled(false);
        txtCostPrice.setEnabled(false);
        txtShelfLoc.setEnabled(false);
        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnSave.setEnabled(false);
        btnDelete.setEnabled(false);
        cmbCategory.setSelectedIndex(0);
        populateTable();
    }

    private String getStatus(int bookId) {

        String status = "";

        try {
            java.sql.Connection cn = DB_connect.getConnection();
            String sql = "SELECT status FROM book_copy WHERE book_id = ? AND status = 'borrowed' ";
            PreparedStatement ps = cn.prepareStatement(sql);

            ps.setInt(1, bookId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                status = rs.getString("status");
            }

        } catch (Exception err) {

        }

        return status;
    }

    private int getCategoryIdByName(String categoryName) {
        int id = 0;

        try {
            java.sql.Connection con = DB_connect.getConnection();
            String sql = "SELECT category_id FROM category WHERE category_name = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, categoryName);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                id = rs.getInt("category_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }
    
    private int getBook(String bookTitle){
        int  id = 0;
        
        try{
            Connection con = DB_connect.getConnection();
            String sql = "SELECT book_id FROM book WHERE title = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setString(1, bookTitle);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                id = rs.getInt("book_id");
            }
            
        }catch(Exception err){
            JOptionPane.showMessageDialog(null, err);
        }
        return id;
    }
      private String existingBook(int bookid){
        String existingTitle = "";
        
        try{
            Connection con = DB_connect.getConnection();
            String sql = "SELECT title FROM book WHERE book_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            pst.setInt(1, bookid);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                existingTitle = rs.getString("title");
            }
            
        }catch(Exception err){
            JOptionPane.showMessageDialog(null, err);
        }
        return existingTitle;
    }

    private String safeValue(DefaultTableModel model, int row, int col) {
        Object val = model.getValueAt(row, col);
        return (val == null) ? "" : val.toString();
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
        txtCostPrice = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtAuthor = new javax.swing.JTextField();
        cmbCategory = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtTitle = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtIsbn = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtClass = new javax.swing.JTextField();
        txtPublisher = new javax.swing.JTextField();
        txtPages = new javax.swing.JTextField();
        txtShelfLoc = new javax.swing.JTextField();
        txtSourceOfFunds = new javax.swing.JTextField();
        yearPublished = new com.toedter.calendar.JYearChooser();
        txtRemarks = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

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
                .addGap(32, 32, 32)
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
                .addContainerGap(1127, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtBooks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtMembers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtTransactions)
                                .addComponent(txtReports)
                                .addComponent(txtLogout1)))))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 2573, -1));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setText("Year Published:");

        txtCostPrice.setEnabled(false);
        txtCostPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCostPriceActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 51));
        jLabel12.setText("Author:");

        txtAuthor.setEnabled(false);
        txtAuthor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAuthorActionPerformed(evt);
            }
        });

        cmbCategory.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbCategory.setEnabled(false);
        cmbCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCategoryActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(51, 51, 51));
        jLabel13.setText("Book Category");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setText("BOOK MAINTENANCE");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 51, 51));
        jLabel15.setText("Book Title:");

        txtTitle.setEnabled(false);
        txtTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTitleActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 51));
        jLabel16.setText("Cost Price:");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(51, 51, 51));
        jLabel17.setText("Pages:");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(51, 51, 51));
        jLabel18.setText("Class:");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(51, 51, 51));
        jLabel19.setText("Publisher:");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(51, 51, 51));
        jLabel20.setText("ISBN:");

        txtIsbn.setEnabled(false);
        txtIsbn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIsbnActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(51, 51, 51));
        jLabel21.setText("Shelf Location: ");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(51, 51, 51));
        jLabel22.setText("Source of Funds:");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(51, 51, 51));
        jLabel23.setText("Remarks: ");

        txtClass.setEnabled(false);
        txtClass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClassActionPerformed(evt);
            }
        });

        txtPublisher.setEnabled(false);
        txtPublisher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPublisherActionPerformed(evt);
            }
        });

        txtPages.setEnabled(false);
        txtPages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPagesActionPerformed(evt);
            }
        });

        txtShelfLoc.setEnabled(false);
        txtShelfLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtShelfLocActionPerformed(evt);
            }
        });

        txtSourceOfFunds.setEnabled(false);
        txtSourceOfFunds.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSourceOfFundsActionPerformed(evt);
            }
        });

        yearPublished.setEnabled(false);

        txtRemarks.setEnabled(false);
        txtRemarks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRemarksActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel10)
                    .addComponent(txtAuthor, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                    .addComponent(txtTitle)
                    .addComponent(jLabel13)
                    .addComponent(jLabel15)
                    .addComponent(jLabel12)
                    .addComponent(cmbCategory, 0, 301, Short.MAX_VALUE)
                    .addComponent(yearPublished, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCostPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19)
                    .addComponent(txtClass, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17)
                    .addComponent(txtPublisher, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(txtPages, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addGap(72, 72, 72)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(txtSourceOfFunds)
                    .addComponent(txtIsbn)
                    .addComponent(txtShelfLoc)
                    .addComponent(txtRemarks, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(81, 81, 81))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(468, 468, 468))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addGap(7, 7, 7)
                        .addComponent(txtIsbn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtSourceOfFunds, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel23)
                        .addGap(18, 18, 18)
                        .addComponent(txtRemarks, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtShelfLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(7, 7, 7)
                        .addComponent(txtTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addGap(12, 12, 12)
                        .addComponent(txtAuthor, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(yearPublished, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addGap(7, 7, 7)
                        .addComponent(txtPublisher, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel16)
                        .addGap(12, 12, 12)
                        .addComponent(txtCostPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPages, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClass, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, 1230, 390));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/My_Image/1150612 (2).png"))); // NOI18N
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 540, 40, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setText("SEARCH: ");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 550, 100, -1));

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
        getContentPane().add(txtSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 540, 240, 37));

        tbl.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        tbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Book_ID", "Class", "Title", "Author", "Category", "Pages", "Source of Funds", "Cost Price", "Publisher", "Year Published", "Remarks", "ISBN", "Shelf Location"
            }
        ));
        tbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tbl);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 590, 1450, 400));

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));

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

        btnDelete.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnClose.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnClose.setText("Close");
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCloseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1330, 140, 180, 390));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtCostPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCostPriceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCostPriceActionPerformed

    private void txtAuthorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAuthorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAuthorActionPerformed

    private void cmbCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCategoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCategoryActionPerformed

    private void txtTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTitleActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_txtTitleActionPerformed

    private void txtIsbnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIsbnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIsbnActionPerformed

    private void txtClassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClassActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtClassActionPerformed

    private void txtPublisherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPublisherActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPublisherActionPerformed

    private void txtPagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPagesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPagesActionPerformed

    private void txtShelfLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtShelfLocActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtShelfLocActionPerformed

    private void txtSourceOfFundsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSourceOfFundsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSourceOfFundsActionPerformed

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        // TODO add your handling code here:
        if (btnClose.getText().equalsIgnoreCase("Cancel")) {
            setDefault();
            btnClose.setText("Close");
        } else {
            Books bok = new Books();
            bok.setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnCloseActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        makeEnabled();
        btnAdd.setEnabled(false);
        btnSave.setEnabled(true);
        btnClose.setText("Cancel");

        check = "Add";
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        try {
            java.sql.Connection cn = DB_connect.getConnection();
            PreparedStatement pst;

            // --- Validate Year Published ---
            if (check.equalsIgnoreCase("Add")) {
                pst = cn.prepareStatement(
                        "INSERT INTO book (title, author, category_id, publisher, publication_year, isbn, shelf_location, remarks, class, pages, source_of_fund , cost_price ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                );
                String bookTitle = txtTitle.getText();
                bookid = getBook(bookTitle);
                String existingTitle = existingBook(bookid);
                
                if(bookTitle.equalsIgnoreCase(existingTitle)){
                    JOptionPane.showMessageDialog(null, "This book is already recorded.");
                    return;
                }
                
                String categoryName = cmbCategory.getSelectedItem().toString();
                int categoryId = getCategoryIdByName(categoryName);
                String priceText = txtCostPrice.getText().replace(",", "").trim();

                int year = yearPublished.getYear();

                pst.setString(1, txtTitle.getText());
                pst.setString(2, txtAuthor.getText());
                pst.setInt(3, categoryId);
                pst.setString(4, txtPublisher.getText());
                pst.setInt(5, year);
                pst.setString(6, txtIsbn.getText());
                pst.setString(7, txtShelfLoc.getText());
                pst.setString(8, txtRemarks.getText());
                pst.setString(9, txtClass.getText());
                pst.setInt(10, Integer.parseInt(txtPages.getText()));
                pst.setString(11, txtSourceOfFunds.getText());
                pst.setBigDecimal(12, new BigDecimal(priceText));
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Added successfully");

            } else if (check.equalsIgnoreCase("Update")) {
                pst = cn.prepareStatement(
                        "UPDATE book SET title = ?, author = ?, category_id = ?, publisher = ?, publication_year = ?, isbn = ?, shelf_location = ?, remarks = ?, class = ?, pages = ?, source_of_fund = ?, cost_price = ? WHERE book_id = ?"
                );

                String categoryName = cmbCategory.getSelectedItem().toString();
                int categoryId = getCategoryIdByName(categoryName);
                int year = yearPublished.getYear();
                pst.setString(1, txtTitle.getText());
                pst.setString(2, txtAuthor.getText());
                pst.setInt(3, categoryId);
                pst.setString(4, txtPublisher.getText());
                pst.setInt(5, year);
                pst.setString(6, txtIsbn.getText());
                pst.setString(7, txtShelfLoc.getText());
                pst.setString(8, txtRemarks.getText());
                pst.setString(9, txtClass.getText());
                pst.setInt(10, Integer.parseInt(txtPages.getText()));
                pst.setString(11, txtSourceOfFunds.getText());
                pst.setBigDecimal(12, new java.math.BigDecimal(txtCostPrice.getText()));
                pst.setInt(13, bookid); // ✅ WHERE book_id

                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Updated successfully");
            }

            setDefault();
            populateTable();

        } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, err.getMessage());
    }//GEN-LAST:event_btnSaveActionPerformed
    }
    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        check = "Update";
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnSave.setEnabled(true);
        makeEnabled();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this book?",
                "Confirm Delete?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            setDefault();
            populateTable();
            return;
        }

        try {
            java.sql.Connection cn = DB_connect.getConnection();
            cn.setAutoCommit(false); // Start transaction

            try {
                // Step 1: Check if ANY copy is currently borrowed
                String checkBorrowedSql = "SELECT COUNT(*) FROM book_copy WHERE book_id = ? AND status = 'borrowed'";
                PreparedStatement pstCheck = cn.prepareStatement(checkBorrowedSql);
                pstCheck.setInt(1, bookid);
                ResultSet rs = pstCheck.executeQuery();

                int borrowedCount = 0;
                if (rs.next()) {
                    borrowedCount = rs.getInt(1);
                }

                if (borrowedCount > 0) {
                    JOptionPane.showMessageDialog(null,
                            "Cannot delete: " + borrowedCount + " copy/copies are currently borrowed.");
                    cn.rollback();
                    return;
                }

                // Step 2: Delete all copies first (child records)
                String deleteCopiesQuery = "DELETE FROM book_copy WHERE book_id = ?";
                PreparedStatement pstCopies = cn.prepareStatement(deleteCopiesQuery);
                pstCopies.setInt(1, bookid);
                int copiesDeleted = pstCopies.executeUpdate();

                // Step 3: Delete the book (parent record)
                String deleteBookQuery = "DELETE FROM book WHERE book_id = ?";
                PreparedStatement pstBook = cn.prepareStatement(deleteBookQuery);
                pstBook.setInt(1, bookid);
                pstBook.executeUpdate();

                cn.commit(); // Commit transaction

                JOptionPane.showMessageDialog(null,
                        "Book and " + copiesDeleted + " copies successfully deleted");

            } catch (SQLException err) {
                cn.rollback(); // Rollback on error
                throw err;
            } finally {
                cn.setAutoCommit(true);
            }

            setDefault();
            populateTable();

        } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, "Error: " + err.getMessage());
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void txtRemarksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRemarksActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRemarksActionPerformed

    private void tblMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tbl.getModel();
        int row = tbl.getSelectedRow();

        if (row == -1) {
            return;
        }

        try {

            // Safe values from table
            bookid = Integer.parseInt(safeValue(model, row, 0));

            txtClass.setText(safeValue(model, row, 1));
            txtTitle.setText(safeValue(model, row, 2));
            txtAuthor.setText(safeValue(model, row, 3));

            // =========================
            // FIXED COMBOBOX POPULATION
            // =========================
            String category = safeValue(model, row, 4);

            boolean found = false;
            for (int i = 0; i < cmbCategory.getItemCount(); i++) {
                String item = cmbCategory.getItemAt(i).toString();

                if (item.equalsIgnoreCase(category.trim())) {
                    cmbCategory.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }

            // Optional fallback (debug help)
            if (!found) {
                System.out.println("⚠ Category not found in combo: " + category);
            }

            txtPages.setText(safeValue(model, row, 5));
            txtSourceOfFunds.setText(safeValue(model, row, 6));
            txtCostPrice.setText(safeValue(model, row, 7));
            txtPublisher.setText(safeValue(model, row, 8));

            // Year chooser safe set
            String year = safeValue(model, row, 9);
            if (year != null && !year.isEmpty()) {
                yearPublished.setYear(Integer.parseInt(year));
            }

            txtRemarks.setText(safeValue(model, row, 10));
            txtIsbn.setText(safeValue(model, row, 11));
            txtShelfLoc.setText(safeValue(model, row, 12));

            // Button states
            btnAdd.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
            btnClose.setText("Cancel");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading table data!");
        }

    }//GEN-LAST:event_tblMouseClicked

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        String search = txtSearch.getText().trim();

        try {
            Connection con = DB_connect.getConnection();

            String sql
                    = "SELECT b.book_id, b.title, b.author, c.category_name, b.publisher, "
                    + "b.publication_year, b.isbn, b.shelf_location, b.remarks, b.class, "
                    + "b.pages, b.source_of_fund, b.cost_price "
                    + "FROM book b LEFT JOIN category c ON b.category_id = c.category_id "
                    + "WHERE b.title LIKE ? OR "
                    + "b.author LIKE ? OR "
                    + "c.category_name LIKE ? OR "
                    + "b.publisher LIKE ? OR "
                    + "b.isbn LIKE ? OR "
                    + "b.shelf_location LIKE ?";

            PreparedStatement ps = con.prepareStatement(sql);

            String value = "%" + search + "%";

            ps.setString(1, value);
            ps.setString(2, value);
            ps.setString(3, value);
            ps.setString(4, value);
            ps.setString(5, value);
            ps.setString(6, value);

            ResultSet res = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tbl.getModel();
            model.setRowCount(0);

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

        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err);
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
        java.awt.EventQueue.invokeLater(() -> new AddBook().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbl;
    private javax.swing.JTextField txtAuthor;
    private javax.swing.JLabel txtBooks;
    private javax.swing.JTextField txtClass;
    private javax.swing.JTextField txtCostPrice;
    private javax.swing.JLabel txtDashboard;
    private javax.swing.JTextField txtIsbn;
    private javax.swing.JLabel txtLogout1;
    private javax.swing.JLabel txtMembers;
    private javax.swing.JTextField txtPages;
    private javax.swing.JTextField txtPublisher;
    private javax.swing.JTextField txtRemarks;
    private javax.swing.JLabel txtReports;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtShelfLoc;
    private javax.swing.JTextField txtSourceOfFunds;
    private javax.swing.JTextField txtTitle;
    private javax.swing.JLabel txtTransactions;
    private com.toedter.calendar.JYearChooser yearPublished;
    // End of variables declaration//GEN-END:variables
}
