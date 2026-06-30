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
public class PayFine1 extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PayFine1.class.getName());

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

    public PayFine1() {
        setUndecorated(true); // REQUIRED for opacity
        initComponents();
        
    loadBorrowers();
    hideFineidColumn();

        
        // ✅ ADD THEM HERE
    tblModel.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            try {
                double total = getSelectedFinesTotal();
                String input = txtAmountTendered.getText().trim();
                if (!input.isEmpty()) {
                    double tendered = Double.parseDouble(input);
                    double change = tendered - total;
                    txtChange.setText(String.format("%.2f", change));
                    if (change < 0) {
                        txtChange.setForeground(java.awt.Color.RED);
                    } else {
                        txtChange.setForeground(java.awt.Color.BLACK);
                    }
                }
            } catch (NumberFormatException ex) {
                txtChange.setText("Invalid");
            }
        }
    });

    
    
    txtAmountTendered.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
       private void calculate() {
        try {
            double total = getSelectedFinesTotal();
            String input = txtAmountTendered.getText().trim();

            if (input.isEmpty()) {
                txtChange.setText("");
                txtChange.setForeground(java.awt.Color.BLACK);
                return;
            }

            double tendered = Double.parseDouble(input);
            double change = tendered - total;

            if (change < 0) {
                txtChange.setForeground(java.awt.Color.RED);
                txtChange.setText("Not enough! Short by ₱" + String.format("%.2f", Math.abs(change)));
            } else {
                txtChange.setForeground(java.awt.Color.BLACK);
                txtChange.setText(String.format("%.2f", change)); // ✅ always positive
            }

        } catch (NumberFormatException e) {
            txtChange.setText("Invalid amount");
            txtChange.setForeground(java.awt.Color.RED);
        }
    }

    @Override
    public void insertUpdate(javax.swing.event.DocumentEvent e) { calculate(); }

    @Override
    public void removeUpdate(javax.swing.event.DocumentEvent e) { calculate(); }

    @Override
    public void changedUpdate(javax.swing.event.DocumentEvent e) { calculate(); }
});

    // ✅ existing code below stays as is
    cmbBorrowerName.addItemListener(new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
        }
    });

    JTextField editor = (JTextField) cmbBorrowerName.getEditor().getEditorComponent();
    editor.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyReleased(java.awt.event.KeyEvent e) {
            if (editor.getText().trim().isEmpty()) {
            }
        }
    });

        
        
        
        

        setupSearch();
//        loadTransactions();

// ✅ Load borrowers but no default selection
        loadBorrowers();
        


        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"copy_id", "book_id", "Acquisition No.", "Title", "Author", "Category", "Status"}, 0
        );
        
     
 

// Hide copy_id and book_id
        SwingUtilities.invokeLater(() -> {
       
        });

        // ✅ Force clear BOTH tables on startup
    
        // ✅ Only loads when librarian manually selects a real borrower
        cmbBorrowerName.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
            
            }
        });

    
        editor.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (editor.getText().trim().isEmpty()) {
                    // ✅ Clear both tables when search is cleared
                  
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
    
    private java.sql.Date calculateDueDate(int workingDays) {
    java.time.LocalDate today = java.time.LocalDate.now();
    java.time.LocalDate dueDate = today;
    int daysAdded = 0;

    while (daysAdded < workingDays) {
        dueDate = dueDate.plusDays(1);

      
        if (dueDate.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) continue;
        if (My_Classes.FineCalculator.HOLIDAYS.contains(dueDate)) continue;

        daysAdded++;
    }

    return java.sql.Date.valueOf(dueDate);
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
    
    private void hideFineidColumn() {
    tblModel.getColumnModel().getColumn(0).setMinWidth(0);
    tblModel.getColumnModel().getColumn(0).setMaxWidth(0);
    tblModel.getColumnModel().getColumn(0).setWidth(0);
    tblModel.getColumnModel().getColumn(0).setPreferredWidth(0);
    tblModel.getColumnModel().getColumn(0).setResizable(false);
}

    
    private double getSelectedFinesTotal() {
    int[] selectedRows = tblModel.getSelectedRows();
    double total = 0.0;

    for (int row : selectedRows) {
        String fineStr = tblModel.getValueAt(row, 6).toString();
        fineStr = fineStr.replace("₱", "").replace(",", "").trim();
        total += Double.parseDouble(fineStr);
    }
    return total;
}
    
   
public void loadTransactions(int borrowerId) {
    DefaultTableModel model = (DefaultTableModel) tblModel.getModel();
    model.setRowCount(0);

    // ✅ Set columns with hidden fine_id at index 0
    model.setColumnCount(0);
    model.addColumn("fine_id");        // index 0 - hidden
    model.addColumn("Acquisition No."); // index 1
    model.addColumn("Book Title");      // index 2
    model.addColumn("Borrowed Date");   // index 3
    model.addColumn("Due Date");        // index 4
    model.addColumn("Status");          // index 5
    model.addColumn("Fine");            // index 6

    try {
        Connection con = DB_connect.getConnection();

        // ✅ First check unreturned overdue books
        String checkSql = "SELECT b.title, t.due_date, "
                        + "DATEDIFF(CURDATE(), t.due_date) AS days_overdue "
                        + "FROM transaction t "
                        + "JOIN book b ON t.book_id = b.book_id "
                        + "WHERE t.borrower_id = ? "
                        + "AND t.status = 'Borrowed' "
                        + "AND t.due_date < CURDATE()";

        PreparedStatement checkPs = con.prepareStatement(checkSql);
        checkPs.setInt(1, borrowerId);
        ResultSet checkRs = checkPs.executeQuery();

        StringBuilder overdueMsg = new StringBuilder();
        while (checkRs.next()) {
            String title    = checkRs.getString("title");
            String dueDate  = checkRs.getString("due_date");
            int daysOverdue = checkRs.getInt("days_overdue");
            overdueMsg.append("• ").append(title)
                      .append(" (Due: ").append(dueDate).append(", ")
                      .append(daysOverdue).append(" day/s overdue)\n");
        }

    // ✅ Just warn, but continue loading available unpaid fines
if (overdueMsg.length() > 0) {
    JOptionPane.showMessageDialog(this,
        "Warning! This borrower still has unreturned overdue book/s:\n\n"
        + overdueMsg.toString()
        + "\nOnly fines from returned books will be shown.",
        "Unreturned Overdue Book/s",
        JOptionPane.WARNING_MESSAGE);
}

        checkRs.close();
        checkPs.close();

        // ✅ Load unpaid fines with fine_id
        String sql = "SELECT f.fine_id, bc.acquisition_number, b.title, t.rental_date, t.due_date, "
                   + "f.amount, f.status, f.days_overdue "
                   + "FROM fine f "
                   + "JOIN transaction t ON f.transaction_id = t.transaction_id "
                   + "JOIN book_copy bc ON t.copy_id = bc.copy_id "
                   + "JOIN book b ON t.book_id = b.book_id "
                   + "WHERE f.borrower_id = ? "
                   + "AND f.status = 'Unpaid'";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, borrowerId);
        ResultSet rs = ps.executeQuery();

        boolean hasUnpaid = false;

        while (rs.next()) {
            hasUnpaid = true;

            int fineId           = rs.getInt("fine_id");
            String acquisitionNo = rs.getString("acquisition_number");
            String title         = rs.getString("title");
            String borrowedDate  = rs.getString("rental_date");
            String dueDate       = rs.getString("due_date");
            double fine          = rs.getDouble("amount");
            int daysOverdue      = rs.getInt("days_overdue");

            String displayStatus = "Unpaid (" + daysOverdue + " day/s overdue)";

            model.addRow(new Object[]{
                fineId,          // index 0 - hidden, used for update
                acquisitionNo,   // index 1
                title,           // index 2
                borrowedDate,    // index 3
                dueDate,         // index 4
                displayStatus,   // index 5
                "₱" + String.format("%.2f", fine)  // index 6
            });
        }

        if (!hasUnpaid) {
            JOptionPane.showMessageDialog(this,
                "This borrower has no unpaid fines.",
                "No Unpaid Fines",
                JOptionPane.INFORMATION_MESSAGE);
        }

        rs.close();
        ps.close();
        con.close();

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error loading fines: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }

    // ✅ Hide the fine_id column from view
    tblModel.getColumnModel().getColumn(0).setMinWidth(0);
    tblModel.getColumnModel().getColumn(0).setMaxWidth(0);
    tblModel.getColumnModel().getColumn(0).setWidth(0);
    tblModel.getColumnModel().getColumn(0).setPreferredWidth(0);
}

private void generateReceipt(double total, double tendered, double change) {
//    try {
//        // ✅ Get borrower info from labels
//        String borrowerId  = lblBorrowerId.getText();
//        String fullName    = lblFullName.getText();
//        String memberType  = lblMemberType.getText();
//        String idNumber    = lblIdNumber.getText();
//        String receiptDate = new java.text.SimpleDateFormat("MMMM dd, yyyy").format(new java.util.Date());
//        String receiptTime = new java.text.SimpleDateFormat("hh:mm a").format(new java.util.Date());
//
//        org.apache.poi.xwpf.usermodel.XWPFDocument doc = new org.apache.poi.xwpf.usermodel.XWPFDocument();
//
//        // ✅ Helper to add centered paragraph
//        java.util.function.BiConsumer<String, Boolean> addCenteredParagraph = (text, bold) -> {
//            org.apache.poi.xwpf.usermodel.XWPFParagraph p = doc.createParagraph();
//            p.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
//            org.apache.poi.xwpf.usermodel.XWPFRun r = p.createRun();
//            r.setText(text);
//            r.setBold(bold);
//            r.setFontSize(bold ? 14 : 11);
//            r.setFontFamily("Arial");
//        };
//
//        // ✅ Helper to add label: value paragraph
//        java.util.function.BiConsumer<String, String> addInfoRow = (label, value) -> {
//            org.apache.poi.xwpf.usermodel.XWPFParagraph p = doc.createParagraph();
//            org.apache.poi.xwpf.usermodel.XWPFRun rLabel = p.createRun();
//            rLabel.setText(label + ": ");
//            rLabel.setBold(true);
//            rLabel.setFontSize(11);
//            rLabel.setFontFamily("Arial");
//            org.apache.poi.xwpf.usermodel.XWPFRun rValue = p.createRun();
//            rValue.setText(value);
//            rValue.setFontSize(11);
//            rValue.setFontFamily("Arial");
//        };
//
//        // ✅ Helper to add separator
//        Runnable addSeparator = () -> {
//            org.apache.poi.xwpf.usermodel.XWPFParagraph p = doc.createParagraph();
//            p.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);
//            org.apache.poi.xwpf.usermodel.XWPFRun r = p.createRun();
//            r.setText("------------------------------------------------");
//            r.setFontFamily("Arial");
//            r.setFontSize(11);
//        };
//
//        // ✅ HEADER
//        addCenteredParagraph.accept("Ormoc City Public Library", true);
//        addCenteredParagraph.accept("Riza O. Rodriguez", false);
//        addCenteredParagraph.accept("Fine Payment Receipt", true);
//        addCenteredParagraph.accept("Date: " + receiptDate + "  |  Time: " + receiptTime, false);
//        addSeparator.run();
//
//        // ✅ BORROWER INFO
//        addInfoRow.accept("Borrower ID", borrowerId);
//        addInfoRow.accept("Full Name", fullName);
//        addInfoRow.accept("Member Type", memberType);
//        addInfoRow.accept("ID Number", idNumber);
//        addSeparator.run();
//
//        // ✅ FINE DETAILS HEADER
//        org.apache.poi.xwpf.usermodel.XWPFParagraph tableHeader = doc.createParagraph();
//        org.apache.poi.xwpf.usermodel.XWPFRun thRun = tableHeader.createRun();
//        thRun.setText("Fine Details:");
//        thRun.setBold(true);
//        thRun.setFontSize(12);
//        thRun.setFontFamily("Arial");
//
//        // ✅ FINE DETAILS TABLE
//        org.apache.poi.xwpf.usermodel.XWPFTable table = doc.createTable();
//        table.setWidth("100%");
//
//        // Table header row
//        org.apache.poi.xwpf.usermodel.XWPFTableRow headerRow = table.getRow(0);
//        headerRow.getCell(0).setText("Acquisition No.");
//        headerRow.addNewTableCell().setText("Book Title");
//        headerRow.addNewTableCell().setText("Days Overdue");
//        headerRow.addNewTableCell().setText("Fine Amount");
//
//        // Make header bold
//        for (org.apache.poi.xwpf.usermodel.XWPFTableCell cell : headerRow.getTableCells()) {
//            org.apache.poi.xwpf.usermodel.XWPFRun run = cell.getParagraphs().get(0).getRuns().get(0);
//            run.setBold(true);
//            run.setFontFamily("Arial");
//            run.setFontSize(10);
//        }
//
//        // ✅ Add selected rows from tblModel
//        DefaultTableModel model = (DefaultTableModel) tblModel.getModel();
//        int[] selectedRows = tblModel.getSelectedRows();
//
//        for (int row : selectedRows) {
//            String acquisitionNo = model.getValueAt(row, 1).toString();
//            String bookTitle     = model.getValueAt(row, 2).toString();
//            String status        = model.getValueAt(row, 5).toString();
//            String fineAmount    = model.getValueAt(row, 6).toString();
//
//            // Extract days overdue from status string "Unpaid (X day/s overdue)"
//            String daysOverdue = "N/A";
//            if (status.contains("(") && status.contains(" day")) {
//                daysOverdue = status.substring(status.indexOf("(") + 1, status.indexOf(" day")) + " day/s";
//            }
//
//            org.apache.poi.xwpf.usermodel.XWPFTableRow dataRow = table.createRow();
//            dataRow.getCell(0).setText(acquisitionNo);
//            dataRow.getCell(1).setText(bookTitle);
//            dataRow.getCell(2).setText(daysOverdue);
//            dataRow.getCell(3).setText(fineAmount);
//
//            for (org.apache.poi.xwpf.usermodel.XWPFTableCell cell : dataRow.getTableCells()) {
//                org.apache.poi.xwpf.usermodel.XWPFRun run = cell.getParagraphs().get(0).getRuns().get(0);
//                run.setFontFamily("Arial");
//                run.setFontSize(10);
//            }
//        }
//
//        addSeparator.run();
//
//        // ✅ PAYMENT SUMMARY
//        addInfoRow.accept("Total Fine", "PHP " + String.format("%.2f", total));
//        addInfoRow.accept("Amount Tendered", "PHP " + String.format("%.2f", tendered));
//        addInfoRow.accept("Change", "PHP " + String.format("%.2f", change));
//        addSeparator.run();
//
//        // ✅ FOOTER
//        addCenteredParagraph.accept("Thank you for your payment!", false);
//        addCenteredParagraph.accept("Please keep this receipt for your records.", false);
//        addCenteredParagraph.accept("Ormoc City Public Library", false);
//
//        // ✅ Save to temp file then auto-print
//        java.io.File tempFile = java.io.File.createTempFile("Receipt_" + fullName.replace(" ", "_"), ".docx");
//        tempFile.deleteOnExit();
//
//        try (java.io.FileOutputStream out = new java.io.FileOutputStream(tempFile)) {
//            doc.write(out);
//        }
//
//        // ✅ Auto-print - silently return if no printer
//        if (java.awt.Desktop.isDesktopSupported()) {
//            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
//            if (desktop.isSupported(java.awt.Desktop.Action.PRINT)) {
//                desktop.print(tempFile);
//            }
//            // no printer = silently do nothing ✅
//        }
//
//        doc.close();
//
//    } catch (Exception e) {
//        e.printStackTrace();
//        JOptionPane.showMessageDialog(this,
//            "Error generating receipt: " + e.getMessage(),
//            "Error",
//            JOptionPane.ERROR_MESSAGE);
//    }
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
        txtUser = new javax.swing.JLabel();
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
        btnFindBorrower = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblModel = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        txtTotalFines = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        txtAmountPaid = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        txtRemainingBalance = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel10 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtAmountTendered = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        btnPrint = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        btnProcess = new javax.swing.JButton();
        txtChange = new javax.swing.JTextField();

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

        txtUser.setBackground(new java.awt.Color(255, 255, 255));
        txtUser.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        txtUser.setText("User");
        txtUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtUserMouseClicked(evt);
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(759, 759, 759)
                    .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(30, 30, 30)
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
                    .addContainerGap(760, Short.MAX_VALUE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(17, 17, 17))
            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel3Layout.createSequentialGroup()
                    .addGap(40, 40, 40)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtBooks, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtMembers, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTransactions)
                        .addComponent(txtReports)
                        .addComponent(txtLogout1)
                        .addComponent(txtUser, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGap(41, 41, 41)))
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
                .addGap(125, 125, 125)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblIdNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblIdType, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(56, 56, 56))
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
                .addContainerGap(19, Short.MAX_VALUE))
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

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));

        tblModel.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Acquisition No.", "Book Title", "Borrowed Date", "Due Date", "Status", "Fine"
            }
        ));
        jScrollPane1.setViewportView(tblModel);

        txtTotalFines.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtTotalFines.setForeground(new java.awt.Color(51, 51, 51));
        txtTotalFines.setText("Total Fines");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(51, 51, 51));
        jLabel14.setText("Total Fines");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(72, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14)
                    .addComponent(txtTotalFines))
                .addGap(92, 92, 92))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(txtTotalFines)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(51, 51, 51));
        jLabel28.setText("Amount Paid");

        txtAmountPaid.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtAmountPaid.setForeground(new java.awt.Color(51, 51, 51));
        txtAmountPaid.setText("Total Fines");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(69, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel28)
                    .addComponent(txtAmountPaid))
                .addGap(65, 65, 65))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28)
                .addGap(18, 18, 18)
                .addComponent(txtAmountPaid)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel30.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(51, 51, 51));
        jLabel30.setText("Remaining Bal.");

        txtRemainingBalance.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtRemainingBalance.setForeground(new java.awt.Color(51, 51, 51));
        txtRemainingBalance.setText("Remaining");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addComponent(txtRemainingBalance)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtRemainingBalance)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(51, 51, 51));
        jLabel26.setText("Overdue Books (P10 fine /day per book)");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel26)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 976, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                            .addGap(6, 6, 6)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(70, 70, 70)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(70, 70, 70)
                            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 918, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel26)
                .addGap(5, 5, 5)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(11, Short.MAX_VALUE))
        );

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(51, 51, 51));
        jLabel16.setText("Payment Details");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(51, 51, 51));
        jLabel23.setText("Amount Tendered");

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(51, 51, 51));
        jLabel24.setText("Change ");

        txtAmountTendered.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtAmountTenderedActionPerformed(evt);
            }
        });

        btnPrint.setText("Print Receipt");

        btnClear.setText("Clear");

        btnProcess.setText("Process Payment");
        btnProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnPrint, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnProcess, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnProcess, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPrint, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtChange.setEnabled(false);
        txtChange.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtChangeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtAmountTendered)
                    .addComponent(txtChange, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(jLabel23))
                        .addGap(266, 266, 266)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addGap(39, 39, 39)
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtAmountTendered, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtChange, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addGap(31, 31, 31)
                        .addComponent(cmbBorrowerName, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(91, 91, 91)
                        .addComponent(btnFindBorrower))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(798, Short.MAX_VALUE))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(89, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
     
    }//GEN-LAST:event_btnFindBorrowerMouseClicked

    private void btnFindBorrowerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindBorrowerActionPerformed
 Object selectedItem = cmbBorrowerName.getSelectedItem();

    if (!(selectedItem instanceof Borrower)) {
        JOptionPane.showMessageDialog(this, "Please select a valid borrower.", "No Borrower Selected", JOptionPane.WARNING_MESSAGE);
        return;
    }

    Borrower selected = (Borrower) selectedItem;
    loadBorrowerDetails(selected.getId());
    loadTransactions(selected.getId());       
    }//GEN-LAST:event_btnFindBorrowerActionPerformed

    private void txtAmountTenderedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAmountTenderedActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAmountTenderedActionPerformed

    private void txtChangeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtChangeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtChangeActionPerformed

    private void btnProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessActionPerformed

       int[] selectedRows = tblModel.getSelectedRows();

    // ✅ Check if any row is selected
    if (selectedRows.length == 0) {
        JOptionPane.showMessageDialog(this,
            "Please select at least one fine to process.",
            "No Fine Selected",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    // ✅ Check amount tendered
    String input = txtAmountTendered.getText().trim();
    if (input.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Please enter the amount tendered.",
            "No Amount Entered",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    double tendered;
    try {
        tendered = Double.parseDouble(input);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "Invalid amount entered.",
            "Invalid Amount",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    double total = getSelectedFinesTotal();

    // ✅ Check if amount is enough
    if (tendered < total) {
        JOptionPane.showMessageDialog(this,
            "Amount tendered is not enough!\n"
            + "Total Fine: ₱" + String.format("%.2f", total) + "\n"
            + "Amount Tendered: ₱" + String.format("%.2f", tendered) + "\n"
            + "Short by: ₱" + String.format("%.2f", Math.abs(tendered - total)),
            "Insufficient Amount",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    // ✅ Confirm payment
    int confirm = JOptionPane.showConfirmDialog(this,
        "Total Fine: ₱" + String.format("%.2f", total) + "\n"
        + "Amount Tendered: ₱" + String.format("%.2f", tendered) + "\n"
        + "Change: ₱" + String.format("%.2f", (tendered - total)) + "\n\n"
        + "Confirm payment?",
        "Confirm Payment",
        JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) return;

    try {
        Connection con = DB_connect.getConnection();

        String updateSql = "UPDATE fine SET status = 'Paid', payment_date = CURDATE() "
                         + "WHERE fine_id = ?";
        PreparedStatement ps = con.prepareStatement(updateSql);

        DefaultTableModel model = (DefaultTableModel) tblModel.getModel();

        for (int row : selectedRows) {
            int fineId = (int) model.getValueAt(row, 0); // ✅ fine_id from hidden column
            ps.setInt(1, fineId);
            ps.addBatch();
        }

        ps.executeBatch();
        ps.close();
        con.close();

        JOptionPane.showMessageDialog(this,
            "Payment processed successfully!\n"
            + "Change: ₱" + String.format("%.2f", (tendered - total)),
            "Payment Successful",
            JOptionPane.INFORMATION_MESSAGE);


// ✅ Generate and auto-print receipt
generateReceipt(total, tendered, tendered - total);

// ✅ Reload table - reuse already declared selectedItem
if (cmbBorrowerName.getSelectedItem() instanceof Borrower) {
    loadTransactions(((Borrower) cmbBorrowerName.getSelectedItem()).getId());
}

// ✅ Clear fields
txtAmountTendered.setText("");
txtChange.setText("");

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this,
            "Error processing payment: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    }//GEN-LAST:event_btnProcessActionPerformed

    private void txtMembersMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtMembersMouseClicked
        Members member = new Members();
        member.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_txtMembersMouseClicked

    private void txtUserMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtUserMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserMouseClicked

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
        java.awt.EventQueue.invokeLater(() -> new PayFine1().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnFindBorrower;
    private javax.swing.JButton btnPrint;
    private javax.swing.JButton btnProcess;
    private javax.swing.JComboBox<Borrower> cmbBorrowerName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
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
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
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
    private javax.swing.JLabel txtAmountPaid;
    private javax.swing.JTextField txtAmountTendered;
    private javax.swing.JLabel txtBooks;
    private javax.swing.JTextField txtChange;
    private javax.swing.JLabel txtDashboard;
    private javax.swing.JLabel txtLogout1;
    private javax.swing.JLabel txtMembers;
    private javax.swing.JLabel txtRemainingBalance;
    private javax.swing.JLabel txtReports;
    private javax.swing.JLabel txtTotalFines;
    private javax.swing.JLabel txtTransactions;
    private javax.swing.JLabel txtUser;
    // End of variables declaration//GEN-END:variables
}
