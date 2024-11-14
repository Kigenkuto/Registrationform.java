import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistrationForm extends JFrame {
    private JTextField txtName, txtMobile, txtDOB, txtAddress;
    private JRadioButton rbMale, rbFemale;
    private JButton btnRegister;
    private JTable table;
    private DefaultTableModel tableModel;

    public RegistrationForm() {
        setTitle("Registration Form");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Setting up the layout
        setLayout(new BorderLayout());
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        
        formPanel.add(new JLabel("Name:"));
        txtName = new JTextField();
        formPanel.add(txtName);
        
        formPanel.add(new JLabel("Mobile:"));
        txtMobile = new JTextField();
        formPanel.add(txtMobile);
        
        formPanel.add(new JLabel("Gender:"));
        rbMale = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        JPanel genderPanel = new JPanel();
        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        formPanel.add(genderPanel);
        
        formPanel.add(new JLabel("DOB (YYYY-MM-DD):"));
        txtDOB = new JTextField();
        formPanel.add(txtDOB);
        
        formPanel.add(new JLabel("Address:"));
        txtAddress = new JTextField();
        formPanel.add(txtAddress);
        
        // Register button
        btnRegister = new JButton("Register");
        formPanel.add(btnRegister);
        
        add(formPanel, BorderLayout.WEST);

        // Table to display data
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Name");
        tableModel.addColumn("Mobile");
        tableModel.addColumn("Gender");
        tableModel.addColumn("DOB");
        tableModel.addColumn("Address");
        
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Action listener for register button
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        setVisible(true);
        connectDatabase();
        loadData();
    }

    private Connection connectDatabase() {
        try {
            // Connect to database (change URL, username, password accordingly)
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/yourdatabase", "username", "password");
            return conn;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e.getMessage());
            return null;
        }
    }

    private void registerUser() {
        String name = txtName.getText();
        String mobile = txtMobile.getText();
        String gender = rbMale.isSelected() ? "Male" : "Female";
        String dob = txtDOB.getText();
        String address = txtAddress.getText();

        try (Connection conn = connectDatabase()) {
            if (conn != null) {
                String sql = "INSERT INTO users (name, mobile, gender, dob, address) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name);
                pstmt.setString(2, mobile);
                pstmt.setString(3, gender);
                pstmt.setString(4, dob);
                pstmt.setString(5, address);
                
                int rowsInserted = pstmt.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Registration successful!");
                    loadData();
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);  // Clear existing data
        try (Connection conn = connectDatabase()) {
            if (conn != null) {
                String sql = "SELECT * FROM users";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("mobile"),
                        rs.getString("gender"),
                        rs.getString("dob"),
                        rs.getString("address")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new RegistrationForm();
    }
          }
