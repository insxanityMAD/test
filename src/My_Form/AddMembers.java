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

/**
 *
 * @author Administrator
 */
public class AddMembers extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AddMembers.class.getName());

    /**
     * Creates new form NewJFrame
     */
    private int borrowerId;
    private String check = "";

    public AddMembers() {
        setUndecorated(true); // REQUIRED for opacity
        initComponents();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        populateTable();

        tblModel.getTableHeader().setPreferredSize(
                new java.awt.Dimension(tblModel.getTableHeader().getWidth(), 50)
        );
        tblModel.getTableHeader().setFont(
                tblModel.getTableHeader().getFont().deriveFont(18f)
        );

        tblModel.setFont(tblModel.getFont().deriveFont(18f));
        tblModel.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 18));

        // 2. Increase the row height so the large text isn't cut off
        tblModel.setRowHeight(35);
        dateRegistered.setDate(new java.util.Date());
        dateBirthOfDate.setDate(new java.util.Date());
    }

    private void populateTable() {

        try {
            java.sql.Connection con = DB_connect.getConnection();
            String sql = "SELECT * FROM borrower";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet res = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tblModel.getModel();
            model.setRowCount(0);

            while (res.next()) {

                java.sql.Date date = res.getDate("date_registered");
                String dateFormat = "";
                if (date != null) {
                    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    dateFormat = format.format(date);
                }
                java.sql.Date dob = res.getDate("date_of_birth");
                String dobFormat = "";

                if (dob != null) {
                    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    dobFormat = format.format(dob);
                }

                model.addRow(new Object[]{
                    res.getInt("borrower_id"),
                    res.getString("first_name"),
                    res.getString("last_name"),
                    res.getString("gender"), // ✅ added
                    res.getString("id_number"),
                    res.getString("id_type"),
                    res.getString("email"),
                    res.getString("phone_number"),
                    res.getString("address"),
                    res.getString("borrower_type"),
                    res.getString("status"),
                    dobFormat, // ✅ added
                    dateFormat
                });
            }

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, error);
        }
    }

    private boolean borrowerExist(String email) {
        boolean checker = false;
        try {
            Connection con = DB_connect.getConnection();
            String query = "Select borrower_id FROM borrower WHERE email = ?";

            PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                checker = true;
            }

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, error);
        }

        return checker;
    }

    private void setDefault() {
        btnAdd.setEnabled(true);
        btnSave.setEnabled(false);
        btnDelete.setEnabled(false);
        btnUpdate.setEnabled(false);
        cmbStatus.setEnabled(false);
        txtAdress.setEnabled(false);
        cmbBorrowerType.setEnabled(false);
        cmbIdType.setEnabled(false);
        txtLastName.setEnabled(false);
        txtIdNumber.setEnabled(false);
        txtEmail.setEnabled(false);
        txtFirstName.setEnabled(false);
        txtPhoneNumber.setEnabled(false);
        dateRegistered.setEnabled(false);
        btnClose.setText("Close");
        cmbGender.setEnabled(false);
        dateBirthOfDate.setEnabled(false);
        dateRegistered.setDate(new java.util.Date());
        txtAdress.setText("");
        txtEmail.setText("");
        txtFirstName.setText("");
        txtPhoneNumber.setText("");
        cmbStatus.setSelectedIndex(0);
        cmbBorrowerType.setSelectedIndex(0);
        txtLastName.setText("");
        txtIdNumber.setText("");
        cmbGender.setSelectedIndex(0);
        dateBirthOfDate.setDate(new java.util.Date());
        cmbIdType.setSelectedIndex(0);
    }

    private void makeEnabled() {
        btnAdd.setEnabled(false);
        cmbStatus.setEnabled(true);
        txtAdress.setEnabled(true);

        cmbBorrowerType.setEnabled(true);
        txtEmail.setEnabled(true);
        txtFirstName.setEnabled(true);
        txtPhoneNumber.setEnabled(true);
        dateRegistered.setEnabled(true);
        cmbIdType.setEnabled(true);
        txtLastName.setEnabled(true);
        txtIdNumber.setEnabled(true);
        cmbGender.setEnabled(true);
        dateBirthOfDate.setEnabled(true);
    }

    private String safeValue(DefaultTableModel model, int row, int col) {
        Object val = model.getValueAt(row, col);
        return (val == null) ? "" : val.toString();
    }

    // borrower status getter:
    public String getStatus(int borrowerId) {
        String status = "";

        try {

            Connection con = DB_connect.getConnection();
            String query = "SELECT status FROM borrower WHERE borrower_id = ?";
            PreparedStatement pst = con.prepareStatement(query);

            pst.setInt(1, borrowerId);

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                status = rs.getString("status");
            }

        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err);
        }

        return status;
    }

    private boolean borrowerIdExist(String idNumber) {

        boolean exist = false;

        try {
            Connection con = DB_connect.getConnection();
            String sql = "SELECT 1 FROM borrower WHERE id_number = ?";
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, idNumber);

            ResultSet res = pst.executeQuery();

            while (res.next()) {
                exist = true;
            }

        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err);
        }

        return exist;
    }

    private boolean borrowerIdTypeExists(String idType) {
        boolean exisit = false;

        try {
            Connection con = DB_connect.getConnection();
            String sql = "SELECT 1 FROM borrower WHERE id_type = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, idType);

            ResultSet res = pst.executeQuery();

            while (res.next()) {
                exisit = true;
            }

        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err);
        }

        return exisit;
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
        txtsearch = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblModel = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtPhoneNumber = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtLastName = new javax.swing.JTextField();
        cmbGender = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtFirstName = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtAdress = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        cmbIdType = new javax.swing.JComboBox<>();
        cmbStatus = new javax.swing.JComboBox<>();
        cmbBorrowerType = new javax.swing.JComboBox<>();
        txtIdNumber = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        dateRegistered = new com.toedter.calendar.JDateChooser();
        jLabel11 = new javax.swing.JLabel();
        dateBirthOfDate = new com.toedter.calendar.JDateChooser();
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
                .addContainerGap(506, Short.MAX_VALUE))
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

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1998, -1));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/My_Image/1150612 (2).png"))); // NOI18N
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 580, -1, -1));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setText("SEARCH: ");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 590, -1, -1));

        txtsearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtsearchActionPerformed(evt);
            }
        });
        txtsearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtsearchKeyReleased(evt);
            }
        });
        getContentPane().add(txtsearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 580, 231, 37));

        tblModel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        tblModel.setModel(new javax.swing.table.DefaultTableModel(
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
                "Borrower ID", "First Name", "Last Name", "Gender", "ID Number", "ID Type", "Email", "Phone Number", "Address", "Borrower Type", "Status", "Date Of Birth", "Date Registered"
            }
        ));
        tblModel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblModelMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblModel);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 630, 1450, 400));

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setText("Membership Start Date");

        txtPhoneNumber.setEnabled(false);
        txtPhoneNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneNumberActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 51));
        jLabel12.setText("Last Name:");

        txtLastName.setEnabled(false);
        txtLastName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtLastNameActionPerformed(evt);
            }
        });

        cmbGender.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cmbGender.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female", "Other" }));
        cmbGender.setEnabled(false);
        cmbGender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbGenderActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(51, 51, 51));
        jLabel13.setText("Gender:");

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setText("Members Maintenance");

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 51, 51));
        jLabel15.setText("First Name: ");

        txtFirstName.setEnabled(false);
        txtFirstName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFirstNameActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 51));
        jLabel16.setText("Phone Number");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(51, 51, 51));
        jLabel17.setText("Email Address: ");

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(51, 51, 51));
        jLabel18.setText("ID Type:");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(51, 51, 51));
        jLabel19.setText("Address:");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(51, 51, 51));
        jLabel20.setText("Membership Type: ");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(51, 51, 51));
        jLabel22.setText("Status");

        txtAdress.setEnabled(false);
        txtAdress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAdressActionPerformed(evt);
            }
        });

        txtEmail.setEnabled(false);
        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });

        cmbIdType.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cmbIdType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "e.g", "Student ID", "Driver’s License", "Passport", "Other" }));
        cmbIdType.setEnabled(false);
        cmbIdType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbIdTypeActionPerformed(evt);
            }
        });

        cmbStatus.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Active", "Blocked", "Inactive", " " }));
        cmbStatus.setEnabled(false);
        cmbStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbStatusActionPerformed(evt);
            }
        });

        cmbBorrowerType.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cmbBorrowerType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Student", "Teacher", "Guest" }));
        cmbBorrowerType.setEnabled(false);
        cmbBorrowerType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBorrowerTypeActionPerformed(evt);
            }
        });

        txtIdNumber.setEnabled(false);
        txtIdNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdNumberActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(51, 51, 51));
        jLabel21.setText("ID Number:");

        dateRegistered.setDateFormatString("yyyy-MM-dd");
        dateRegistered.setEnabled(false);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(51, 51, 51));
        jLabel11.setText("Birth Of Date:");

        dateBirthOfDate.setDateFormatString("yyyy-MM-dd");
        dateBirthOfDate.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel10)
                            .addComponent(txtLastName, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                            .addComponent(txtFirstName, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(cmbGender, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dateRegistered, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel18)
                            .addComponent(cmbIdType, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19)
                            .addComponent(jLabel17)
                            .addComponent(txtAdress, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel9)))
                .addGap(68, 68, 68)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel20)
                    .addComponent(jLabel22)
                    .addComponent(cmbStatus, 0, 301, Short.MAX_VALUE)
                    .addComponent(cmbBorrowerType, 0, 301, Short.MAX_VALUE)
                    .addComponent(jLabel21)
                    .addComponent(txtIdNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                    .addComponent(jLabel11)
                    .addComponent(dateBirthOfDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(85, 85, 85))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(7, 7, 7)
                        .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addGap(8, 8, 8)
                        .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(dateRegistered, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(cmbGender, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addGap(7, 7, 7)
                                .addComponent(txtAdress, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16)
                                .addGap(8, 8, 8)
                                .addComponent(txtPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addGap(7, 7, 7)
                                .addComponent(txtIdNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel20)
                                .addGap(8, 8, 8)
                                .addComponent(cmbBorrowerType, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(13, 13, 13)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel18)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbIdType, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dateBirthOfDate, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, 1220, -1));

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
                .addContainerGap(47, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClose, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 47, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(1300, 150, 190, 390));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtsearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtsearchActionPerformed

    private void txtPhoneNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneNumberActionPerformed

    private void txtLastNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtLastNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtLastNameActionPerformed

    private void cmbGenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbGenderActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbGenderActionPerformed

    private void txtFirstNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFirstNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFirstNameActionPerformed

    private void txtAdressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAdressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAdressActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void cmbIdTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbIdTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbIdTypeActionPerformed

    private void cmbStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbStatusActionPerformed

    private void cmbBorrowerTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBorrowerTypeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbBorrowerTypeActionPerformed

    private void txtIdNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdNumberActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdNumberActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        makeEnabled();

        check = "add";
        btnClose.setText("Cancel");
        btnSave.setEnabled(true);        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        java.util.Date date = dateRegistered.getDate();
        java.util.Date dob = dateBirthOfDate.getDate();

        if (date == null || dob == null) {
            JOptionPane.showMessageDialog(null, "Please select both Date Registered and Date of Birth");
            return;
        }

        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        java.sql.Date sqlDateDob = new java.sql.Date(dob.getTime());

        String borrowerType = cmbBorrowerType.getSelectedItem().toString();

        int limit = 0;
        if (borrowerType.equalsIgnoreCase("Student")) {
            limit = 4;
        } else if (borrowerType.equalsIgnoreCase("Teacher")) {
            limit = 5;
        } else {
            limit = 3;
        }

        java.time.LocalDate picked = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        java.time.LocalDate dobpicked = dob.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        java.time.LocalDate today = java.time.LocalDate.now();

        String email = txtEmail.getText().trim();
        String idNumber = txtIdNumber.getText().trim();
        String idType = cmbIdType.getSelectedItem().toString();
        try {
            Connection con = DB_connect.getConnection();

            if (check.equalsIgnoreCase("add")) {
                if (picked.isAfter(today)) {
                    JOptionPane.showMessageDialog(null, "Please chose a date that is not in the future.");
                    return;
                }
                
                if (!dobpicked.isBefore(today)) {
                    JOptionPane.showMessageDialog(null, "The member cannot  be born today.");
                    return;
                }
                
                if (borrowerExist(email) && check.equalsIgnoreCase("add")) {
                    JOptionPane.showMessageDialog(null, "This email is arleady taken.");
                    return;
                }

                if (borrowerIdExist(idNumber) && borrowerIdTypeExists(idType)) {
                    JOptionPane.showMessageDialog(null, "The ID is already been used.");
                    return;
                }

                if (txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Fill the textField");
                    return;

                }
                
                if(idNumber.isEmpty() || idType.equals("e.g")){
                    JOptionPane.showMessageDialog(null, "Please fill the id number or the id type");
                    return;
                }
                
                if(txtEmail.getText().isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please fill the email");
                    return;
                }
                    
                String query = "INSERT INTO borrower(first_name, last_name, gender, id_number, id_type, email, phone_number, address, borrower_type, status, borrow_limit, date_of_birth, date_registered) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(query);

                ps.setString(1, txtFirstName.getText().trim());
                ps.setString(2, txtLastName.getText().trim());
                ps.setString(3, cmbGender.getSelectedItem().toString().trim()); // ✅ gender
                ps.setString(4, txtIdNumber.getText().trim());
                ps.setString(5, cmbIdType.getSelectedItem().toString());
                ps.setString(6, txtEmail.getText().trim());
                ps.setString(7, txtPhoneNumber.getText().trim());
                ps.setString(8, txtAdress.getText().trim());
                ps.setString(9, cmbBorrowerType.getSelectedItem().toString().trim());
                ps.setString(10, cmbStatus.getSelectedItem().toString());
                ps.setInt(11, limit);

                // ✅ Date of Birth
                ps.setDate(12, sqlDateDob);

                // Date Registered
                ps.setDate(13, sqlDate);

                ps.executeUpdate();
                setDefault();
                populateTable();
                JOptionPane.showMessageDialog(null, "borrower added.");
            } else if (check.equalsIgnoreCase("update")) {

                // dont forget to put a code in update and create a onlclic  action on jtable latest task for borrower maintenance;
                String query = "UPDATE borrower SET first_name = ?, last_name = ?, gender = ?, id_number = ?, id_type = ?, email = ?, phone_number = ?, address = ?, borrower_type = ?, status = ?, borrow_limit = ?, date_of_birth = ?, date_registered = ? WHERE borrower_id = ?";
                PreparedStatement ps = con.prepareStatement(query);

                ps.setString(1, txtFirstName.getText().trim());
                ps.setString(2, txtLastName.getText().trim());
                ps.setString(3, cmbGender.getSelectedItem().toString()); // ✅ gender
                ps.setString(4, txtIdNumber.getText().trim());
                ps.setString(5, cmbIdType.getSelectedItem().toString());
                ps.setString(6, txtEmail.getText().trim());
                ps.setString(7, txtPhoneNumber.getText().trim());
                ps.setString(8, txtAdress.getText().trim());
                ps.setString(9, cmbBorrowerType.getSelectedItem().toString().trim());
                ps.setString(10, cmbStatus.getSelectedItem().toString());
                ps.setInt(11, limit);

                // ✅ Date of Birth
                ps.setDate(12, sqlDateDob);

                ps.setDate(13, sqlDate);
                ps.setInt(14, borrowerId);

                ps.executeUpdate();
                setDefault();
                populateTable();
                JOptionPane.showMessageDialog(null, "Updated");
            }

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, error);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        check = "update";
        btnUpdate.setEnabled(false);
        btnClose.setText("Cancel");
        btnSave.setEnabled(true);

    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        if (JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete this member?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            try {
                Connection cn = DB_connect.getConnection();
                String query = "DELETE FROM borrower WHERE borrower_id = ?";
                PreparedStatement pst = cn.prepareStatement(query);

                pst.setInt(1, borrowerId);

                int rowsAffected = pst.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Member successfully deleted.");
                } else {
                    JOptionPane.showMessageDialog(null, "No record found.");
                }

                setDefault();
                populateTable();

            } catch (SQLException err) {
                JOptionPane.showMessageDialog(null, err);
            }

        } else {
            // Optional: you don’t really need to reset here
            JOptionPane.showMessageDialog(null, "Delete cancelled.");
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseActionPerformed
        // TODO add your handling code here:
        if (btnClose.getText().equalsIgnoreCase("Cancel")) {
            setDefault();
        } else {
            Members member = new Members();
            member.setVisible(true);
            this.dispose();
        }

    }//GEN-LAST:event_btnCloseActionPerformed

    private void tblModelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblModelMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) tblModel.getModel();
        int row = tblModel.getSelectedRow();

        if (row == -1) {
            return;
        }

// ID
        borrowerId = Integer.parseInt(safeValue(model, row, 0));

// TEXT FIELDS
        txtFirstName.setText(safeValue(model, row, 1));
        txtLastName.setText(safeValue(model, row, 2));

// ✅ GENDER (NEW)
        cmbGender.setSelectedItem(safeValue(model, row, 3));

// SHIFTED INDEXES ↓
        txtIdNumber.setText(safeValue(model, row, 4));
        cmbIdType.setSelectedItem(safeValue(model, row, 5));
        txtEmail.setText(safeValue(model, row, 6));
        txtPhoneNumber.setText(safeValue(model, row, 7));
        txtAdress.setText(safeValue(model, row, 8));
        cmbBorrowerType.setSelectedItem(safeValue(model, row, 9).trim());
        cmbStatus.setSelectedItem(safeValue(model, row, 10));

// ✅ DATE OF BIRTH
        try {
            String dobStr = safeValue(model, row, 11);
            if (!dobStr.isEmpty()) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.util.Date dob = sdf.parse(dobStr);
                dateBirthOfDate.setDate(dob);
            }
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err);
        }

// ✅ DATE REGISTERED
        try {
            String dateStr = safeValue(model, row, 12);
            if (!dateStr.isEmpty()) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = sdf.parse(dateStr);
                dateRegistered.setDate(date);
            }
        } catch (Exception err) {
            JOptionPane.showMessageDialog(null, err);
        }

        makeEnabled();
        btnDelete.setEnabled(true);
        btnUpdate.setEnabled(true);
        btnClose.setText("Cancel");
    }//GEN-LAST:event_tblModelMouseClicked

    private void txtsearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtsearchKeyReleased
        // TODO add your handling code here:
        String search = txtsearch.getText().trim();

        try {
            Connection con = DB_connect.getConnection();

            String sql = "SELECT borrower_id, first_name, last_name, gender, id_number, id_type, email, phone_number, address, borrower_type, status, date_of_birth, date_registered "
                    + "FROM borrower WHERE "
                    + "first_name LIKE ? OR "
                    + "last_name LIKE ? OR "
                    + "email LIKE ? OR "
                    + "id_number LIKE ? OR "
                    + "phone_number LIKE ? OR "
                    + "gender LIKE ?";

            PreparedStatement ps = con.prepareStatement(sql);

            String value = "%" + search + "%";

            ps.setString(1, value);
            ps.setString(2, value);
            ps.setString(3, value);
            ps.setString(4, value);
            ps.setString(5, value);
            ps.setString(6, value);

            ResultSet res = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tblModel.getModel();
            model.setRowCount(0);

            while (res.next()) {

                // DATE REGISTERED
                java.sql.Date regDate = res.getDate("date_registered");
                String regFormat = "";

                if (regDate != null) {
                    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    regFormat = format.format(regDate);
                }

                // DATE OF BIRTH
                java.sql.Date dob = res.getDate("date_of_birth");
                String dobFormat = "";

                if (dob != null) {
                    java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    dobFormat = format.format(dob);
                }

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
                    dobFormat,
                    regFormat
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }//GEN-LAST:event_txtsearchKeyReleased

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
        java.awt.EventQueue.invokeLater(() -> new AddMembers().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cmbBorrowerType;
    private javax.swing.JComboBox<String> cmbGender;
    private javax.swing.JComboBox<String> cmbIdType;
    private javax.swing.JComboBox<String> cmbStatus;
    private com.toedter.calendar.JDateChooser dateBirthOfDate;
    private com.toedter.calendar.JDateChooser dateRegistered;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblModel;
    private javax.swing.JTextField txtAdress;
    private javax.swing.JLabel txtBooks;
    private javax.swing.JLabel txtDashboard;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFirstName;
    private javax.swing.JTextField txtIdNumber;
    private javax.swing.JTextField txtLastName;
    private javax.swing.JLabel txtLogout1;
    private javax.swing.JLabel txtMembers;
    private javax.swing.JTextField txtPhoneNumber;
    private javax.swing.JLabel txtReports;
    private javax.swing.JLabel txtTransactions;
    private javax.swing.JTextField txtsearch;
    // End of variables declaration//GEN-END:variables

}
