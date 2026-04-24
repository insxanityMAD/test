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
        loadBorrowers();
        setupSearch();

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

            // Since your table uses first_name and last_name (not "name")
            String sql = "SELECT borrower_id, first_name, last_name, Id_number FROM borrower ORDER BY first_name ASC";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            isUpdating = true;

            cmbBorrowerName.removeAllItems();

            while (rs.next()) {
                cmbBorrowerName.addItem(new Borrower(
                        rs.getInt("borrower_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("Id_number")
                ));
            }

            isUpdating = false;
            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error loading borrowers: " + e.getMessage());
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
        jTable1 = new javax.swing.JTable();
        jLabel24 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel25 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

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
                        .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblIdNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/My_Image/icon/id-badge.png"))); // NOI18N
        jLabel9.setText("Select Borrower:");

        cmbBorrowerName.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cmbBorrowerName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBorrowerNameActionPerformed(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Acquisition Number", "Title", "Author", "Category ", "Availability"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

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

        jButton2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton2.setText("Find Borrower");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton2MouseClicked(evt);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setForeground(new java.awt.Color(0, 0, 0));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Acquisition Number", "Title"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(51, 51, 51));
        jLabel25.setText("SELECTED BOOK PREVIEW ");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
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

        jButton3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton3.setText("Clear");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton3MouseClicked(evt);
            }
        });
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton4.setText("Add Book");
        jButton4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton4MouseClicked(evt);
            }
        });
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jButton5.setText("Remove Selected");
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton5MouseClicked(evt);
            }
        });
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton5)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel9)
                            .addGap(31, 31, 31)
                            .addComponent(cmbBorrowerName, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(91, 91, 91)
                            .addComponent(jButton2))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel23)
                            .addGap(18, 18, 18)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(42, 42, 42)
                            .addComponent(jButton4))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(162, 162, 162))
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

    private void jButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseClicked
        AddMembers members = new AddMembers();
        members.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton2MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        AddMembers addmember = new AddMembers();
        addmember.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3MouseClicked

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4MouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5MouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton5ActionPerformed

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
    private javax.swing.JComboBox<Borrower> cmbBorrowerName;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
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
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblBorrowerId;
    private javax.swing.JLabel lblContactNumber;
    private javax.swing.JLabel lblDob;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblIdNumber;
    private javax.swing.JLabel lblIdType;
    private javax.swing.JLabel lblMemberType;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel txtBooks;
    private javax.swing.JLabel txtDashboard;
    private javax.swing.JLabel txtLogout1;
    private javax.swing.JLabel txtMembers;
    private javax.swing.JLabel txtReports;
    private javax.swing.JLabel txtTransactions;
    // End of variables declaration//GEN-END:variables
}
