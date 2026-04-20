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
public class MemberReports extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MemberReports.class.getName());

    /**
     * Creates new form NewJFrame
     */
    public MemberReports() {
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

        populateTable();
    }
// Add this method to filter table by borrower type

    private void populateTableByType(String borrowerType) {
        String sql;
        if (borrowerType.equals("All")) {
            sql = "SELECT * FROM borrower";
        } else {
            sql = "SELECT * FROM borrower WHERE borrower_type = '" + borrowerType + "'";
        }
        populateTable(sql, false, false, false);  // ✅ FIXED: Added 4th parameter
    }

    private void populateTable(String sql, boolean showTransactionCount, boolean isOverdueFormat, boolean showBooksCount) {
        try {
            java.sql.Connection con = DB_connect.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet res = ps.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tblModel.getModel();
            model.setRowCount(0); // Clear existing rows

            // Define columns based on format type
            String[] columns;
            if (showBooksCount) {
                // Overdue with Books Count columns
                columns = new String[]{"ID", "Member Name", "Type", "Contact", "Books Overdue", "First Due Date", "Days", "Total Fine"};
            } else if (isOverdueFormat) {
                // Original overdue format (single book)
                columns = new String[]{"ID", "Member Name", "Type", "Contact", "Overdue", "Days", "Fine"};
            } else if (showTransactionCount) {
                // Top Borrowers columns
                columns = new String[]{
                    "Borrower ID", "First Name", "Last Name", "Gender",
                    "ID Number", "ID Type", "Email", "Phone Number",
                    "Address", "Borrower Type", "Status", "Date Of Birth",
                    "Date Registered", "Total Transactions"
                };
            } else {
                // Standard Member Directory columns
                columns = new String[]{
                    "Borrower ID", "First Name", "Last Name", "Gender",
                    "ID Number", "ID Type", "Email", "Phone Number",
                    "Address", "Borrower Type", "Status", "Date Of Birth",
                    "Date Registered"
                };
            }
            model.setColumnIdentifiers(columns);

            while (res.next()) {

                if (showBooksCount) {
                    // OVERDUE WITH BOOKS COUNT FORMAT
                    int borrowerId = res.getInt("borrower_id");
                    String memberName = res.getString("member_name");
                    String type = res.getString("borrower_type");
                    String contact = res.getString("phone_number");
                    int booksOverdue = res.getInt("books_overdue");
                    java.sql.Date firstDueDate = res.getDate("first_due_date");
                    int daysOverdue = res.getInt("days_overdue");
                    double totalFine = res.getDouble("total_fine");

                    Object[] row = new Object[]{
                        borrowerId,
                        memberName,
                        type,
                        contact,
                        booksOverdue,
                        firstDueDate != null ? firstDueDate.toString() : "N/A",
                        daysOverdue,
                        String.format("PHP %.2f", totalFine)
                    };
                    model.addRow(row);

                } else if (isOverdueFormat) {
                    // SINGLE BOOK OVERDUE FORMAT (previous version)
                    int borrowerId = res.getInt("borrower_id");
                    String memberName = res.getString("member_name");
                    String type = res.getString("borrower_type");
                    String contact = res.getString("phone_number");
                    java.sql.Date dueDate = res.getDate("due_date");
                    int daysOverdue = res.getInt("days_overdue");
                    double fine = res.getDouble("fine");

                    Object[] row = new Object[]{
                        borrowerId,
                        memberName,
                        type,
                        contact,
                        dueDate != null ? dueDate.toString() : "N/A",
                        daysOverdue,
                        String.format("PHP %.2f", fine)
                    };
                    model.addRow(row);

                } else if (showTransactionCount) {
                    // TOP BORROWERS FORMAT
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

                    Object[] row = new Object[]{
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
                        dateFormat
                    };

                    // Add transaction count at the end
                    int transactionCount = res.getInt("transaction_count");
                    row = java.util.Arrays.copyOf(row, row.length + 1);
                    row[row.length - 1] = transactionCount;

                    model.addRow(row);

                } else {
                    // STANDARD FORMAT: All borrower columns
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

                    Object[] row = new Object[]{
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
                        dateFormat
                    };

                    model.addRow(row);
                }
            }

            ps.close();
            con.close();

        } catch (Exception error) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + error.getMessage());
            logger.log(java.util.logging.Level.SEVERE, null, error);
        }
    }

// Default populate for initial load (all members)
    private void populateTable() {
        populateTable("SELECT * FROM borrower", false, false, false);
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
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblModel = new javax.swing.JTable();
        btnExport = new javax.swing.JButton();
        btnInactive = new javax.swing.JButton();
        btnOverdueAccounts = new javax.swing.JButton();
        btnTopBorrowers = new javax.swing.JButton();
        cmbCategory = new javax.swing.JComboBox<>();
        btnMemberDirectory1 = new javax.swing.JButton();

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
                .addContainerGap(697, Short.MAX_VALUE))
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

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setText("Member Type: ");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 340, 180, 34));

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel9.setText("MEMBER REPORTS");
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 140, -1, -1));

        tblModel.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
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

        btnInactive.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnInactive.setText("Inactive");
        btnInactive.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInactiveActionPerformed(evt);
            }
        });
        getContentPane().add(btnInactive, new org.netbeans.lib.awtextra.AbsoluteConstraints(1010, 240, 200, 44));

        btnOverdueAccounts.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnOverdueAccounts.setText("Overdue Accounts");
        btnOverdueAccounts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOverdueAccountsActionPerformed(evt);
            }
        });
        getContentPane().add(btnOverdueAccounts, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 240, 200, 44));

        btnTopBorrowers.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnTopBorrowers.setText("Top Borrowers");
        btnTopBorrowers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTopBorrowersActionPerformed(evt);
            }
        });
        getContentPane().add(btnTopBorrowers, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 240, 200, 44));

        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Student", "Teacher", "Guest" }));
        cmbCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCategoryActionPerformed(evt);
            }
        });
        getContentPane().add(cmbCategory, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 340, 160, 40));

        btnMemberDirectory1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btnMemberDirectory1.setText("Member Directory");
        btnMemberDirectory1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnMemberDirectory1MouseClicked(evt);
            }
        });
        btnMemberDirectory1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMemberDirectory1ActionPerformed(evt);
            }
        });
        getContentPane().add(btnMemberDirectory1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 240, 200, 44));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtMembersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtMembersMouseClicked
        MemberReports member = new MemberReports();
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

        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Save Excel File");
        fileChooser.setSelectedFile(new java.io.File("MemberReport.xlsx"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel Files", "xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }

        java.io.File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().endsWith(".xlsx")) {
            fileToSave = new java.io.File(fileToSave.getAbsolutePath() + ".xlsx");
        }

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {

            org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Member Report");
            javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) tblModel.getModel();

            // Header style
            org.apache.poi.xssf.usermodel.XSSFCellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.xssf.usermodel.XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(new org.apache.poi.xssf.usermodel.XSSFColor(
                    new byte[]{(byte) 204, (byte) 204, (byte) 255}, null));
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            // Write header row
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            for (int col = 0; col < model.getColumnCount(); col++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(col);
                cell.setCellValue(model.getColumnName(col));
                cell.setCellStyle(headerStyle);
            }

            // Write data rows
            for (int row = 0; row < model.getRowCount(); row++) {
                org.apache.poi.ss.usermodel.Row dataRow = sheet.createRow(row + 1);
                for (int col = 0; col < model.getColumnCount(); col++) {
                    org.apache.poi.ss.usermodel.Cell cell = dataRow.createCell(col);
                    Object value = model.getValueAt(row, col);
                    if (value != null) {
                        cell.setCellValue(value.toString());
                    }
                }
            }

            // Auto-size columns
            for (int col = 0; col < model.getColumnCount(); col++) {
                sheet.autoSizeColumn(col);
            }

            // Save file
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fileToSave)) {
                workbook.write(fos);
            }

            JOptionPane.showMessageDialog(this,
                    "Exported successfully to:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Export failed: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_btnExportActionPerformed

    private void cmbCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCategoryActionPerformed
        // TODO add your handling code here:
        String selectedType = (String) cmbCategory.getSelectedItem();
        populateTableByType(selectedType);


    }//GEN-LAST:event_cmbCategoryActionPerformed

    private void btnOverdueAccountsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOverdueAccountsActionPerformed
       String sql = "SELECT b.borrower_id, "
            + "CONCAT(b.first_name, ' ', b.last_name) as member_name, "
            + "b.borrower_type, "
            + "b.phone_number, "
            + "COUNT(t.transaction_id) as books_overdue, "
            + "MIN(t.due_date) as first_due_date, "
            + "GROUP_CONCAT(t.due_date) as all_due_dates "
            + "FROM borrower b "
            + "JOIN `transaction` t ON b.borrower_id = t.borrower_id "
            + "WHERE t.due_date < CURRENT_DATE "
            + "AND t.status = 'Borrowed' "
            + "GROUP BY b.borrower_id, b.first_name, b.last_name, b.borrower_type, b.phone_number";

    try {
        java.sql.Connection con = DB_connect.getConnection();
        java.sql.PreparedStatement ps = con.prepareStatement(sql);
        java.sql.ResultSet rs = ps.executeQuery();

        String[] columns = {"ID", "Member Name", "Type", "Contact", "Books Overdue", "First Due Date", "Days Overdue (Weekdays)", "Total Fine"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(new Object[][]{}, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblModel.setModel(model);

        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

        while (rs.next()) {
            java.sql.Date firstDue = rs.getDate("first_due_date");

            // ✅ Calculate weekday-only days and fine across all due dates
            String allDueDates = rs.getString("all_due_dates");
            double totalFine = 0;
            long maxDaysOverdue = 0;

            if (allDueDates != null) {
                for (String dateStr : allDueDates.split(",")) {
                    try {
                        java.sql.Date due = java.sql.Date.valueOf(dateStr.trim());
                        long days = My_Classes.FineCalculator.countWeekdaysLate(due, today);
                        totalFine += My_Classes.FineCalculator.calculateFine(due, today);
                        if (days > maxDaysOverdue) maxDaysOverdue = days;
                    } catch (Exception ignored) {}
                }
            }

            model.addRow(new Object[]{
                rs.getInt("borrower_id"),
                rs.getString("member_name"),
                rs.getString("borrower_type"),
                rs.getString("phone_number"),
                rs.getInt("books_overdue"),
                firstDue != null ? firstDue.toString() : "N/A",
                maxDaysOverdue,
                String.format("₱%.2f", totalFine)
            });
        }

        ps.close();
        con.close();

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
    }
    }//GEN-LAST:event_btnOverdueAccountsActionPerformed

    private void btnTopBorrowersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTopBorrowersActionPerformed
        // TODO add your handling code here:
        String sql = "SELECT b.*, COUNT(t.transaction_id) as transaction_count "
                + "FROM borrower b "
                + "LEFT JOIN transaction t ON b.borrower_id = t.borrower_id "
                + "GROUP BY b.borrower_id "
                + "ORDER BY transaction_count DESC "
                + "LIMIT 10";

        populateTable(sql, true, false, false); // Added 4th parameter
    }//GEN-LAST:event_btnTopBorrowersActionPerformed

    private void btnInactiveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInactiveActionPerformed
        // TODO add your handling code here:
        String sql = "SELECT * FROM borrower WHERE status = 'Inactive'";
        populateTable(sql, false, false, false); // Added 4th parameter
    }//GEN-LAST:event_btnInactiveActionPerformed

    private void btnMemberDirectory1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMemberDirectory1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnMemberDirectory1MouseClicked

    private void btnMemberDirectory1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMemberDirectory1ActionPerformed
        // TODO add your handling code here:
        String sql = "SELECT * FROM borrower";
        populateTable(sql, false, false, false); // Added 4th parameter
    }//GEN-LAST:event_btnMemberDirectory1ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new MemberReports().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnInactive;
    private javax.swing.JButton btnMemberDirectory1;
    private javax.swing.JButton btnOverdueAccounts;
    private javax.swing.JButton btnTopBorrowers;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
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
