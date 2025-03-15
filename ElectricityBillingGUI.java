import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class User implements Serializable {
    String name;
    String meterNumber;
    double unitsConsumed;

    User(String name, String meterNumber, double unitsConsumed) {
        this.name = name;
        this.meterNumber = meterNumber;
        this.unitsConsumed = unitsConsumed;
    }
}

// Splash Screen Class
class SplashScreen extends JFrame {
    public SplashScreen() {
        setTitle("Electricity Billing System");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));

        JPanel panel = new JPanel();
        panel.setBackground(new Color(11, 143, 47)); // Dark Green Color
        panel.setLayout(new BorderLayout());
        
        JLabel label = new JLabel("Welcome to Electricity Billing System", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Serif", Font.BOLD, 22));
        panel.add(label, BorderLayout.CENTER);
        
        JLabel credit = new JLabel("-By Monotosh Ghosh", SwingConstants.CENTER);
        credit.setForeground(Color.WHITE);
        credit.setFont(new Font("Serif", Font.ITALIC, 16));
        panel.add(credit, BorderLayout.SOUTH);
        
        add(panel);
        setVisible(true);

        Timer timer = new Timer(2000, e -> {
            dispose();
            new ElectricityBillingGUI();
        });
        timer.setRepeats(false);
        timer.start();
    }
}

public class ElectricityBillingGUI extends JFrame {
    private List<User> users = new ArrayList<>();
    private JTextField nameField, meterField, unitsField, searchField;
    private JTextArea displayArea;
    private final String FILE_NAME = "users.txt";

    public ElectricityBillingGUI() {
        setTitle("Electricity Billing System");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        setLocationRelativeTo(null);

        loadUsers(); // Load users from file
        
        // First Line
        add(new JLabel("Name:"));
        nameField = new JTextField(10);
        add(nameField);

        add(new JLabel("Meter No:"));
        meterField = new JTextField(10);
        add(meterField);

        add(new JLabel("Units:"));
        unitsField = new JTextField(5);
        add(unitsField);

        // Second Line
        JButton addButton = new JButton("Add User");
        add(addButton);
        addButton.addActionListener(e -> addUser());

        JButton displayButton = new JButton("Display Users");
        add(displayButton);
        displayButton.addActionListener(e -> displayUsers());
        
        JButton clearButton = new JButton("Clear Screen"); // Moved up
        add(clearButton);
        clearButton.addActionListener(e -> displayArea.setText(""));
        
        // Third Line
        add(new JLabel("Search/Delete Meter No:"));
        searchField = new JTextField(10);
        add(searchField);

        JButton billButton = new JButton("Generate Bill");
        add(billButton);
        billButton.addActionListener(e -> generateBill());
        
        JButton deleteButton = new JButton("Delete User"); // Moved down
        add(deleteButton);
        deleteButton.addActionListener(e -> deleteUser());
        
        // Display Area
        displayArea = new JTextArea(10, 40);
        displayArea.setEditable(false);
        add(new JScrollPane(displayArea));

        setVisible(true);
    }

    private void addUser() {
        String name = nameField.getText();
        String meterNumber = meterField.getText();
        double units;
        try {
            units = Double.parseDouble(unitsField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter valid units!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        users.add(new User(name, meterNumber, units));
        saveUsers();
        JOptionPane.showMessageDialog(this, "User added successfully!");
        nameField.setText("");
        meterField.setText("");
        unitsField.setText("");
    }

    private void displayUsers() {
        if (users.isEmpty()) {
            displayArea.setText("No users found!");
            return;
        }
        StringBuilder sb = new StringBuilder("User List:\n");
        for (User user : users) {
            sb.append("Name: ").append(user.name)
              .append(", Meter: ").append(user.meterNumber)
              .append(", Units: ").append(user.unitsConsumed).append("\n");
        }
        displayArea.setText(sb.toString());
    }

    private void generateBill() {
        String meterNumber = searchField.getText().trim();
        
        if (meterNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter meter number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        for (User user : users) {
            if (user.meterNumber.equals(meterNumber)) {
                double billAmount = user.unitsConsumed * 5.0;
                JOptionPane.showMessageDialog(this, "Bill for " + user.name + " (Meter: " + user.meterNumber + ")\nRs. " + billAmount);
                return;
            }
        }
    
        JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void deleteUser() {
        String meterNumber = searchField.getText().trim();
        
        if (meterNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter meter number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        boolean removed = users.removeIf(user -> user.meterNumber.equals(meterNumber));
    
        if (removed) {
            saveUsers();
            JOptionPane.showMessageDialog(this, "User deleted successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            users = (List<User>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            users = new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        new SplashScreen();
    }
}
