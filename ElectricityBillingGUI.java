// Import necessary libraries
import javax.swing.*; // For GUI components
import java.awt.*; // For layout and UI customization
import java.awt.event.*; // For event handling (buttons, actions)
import java.io.*; // For file operations (saving/loading users)
import java.util.ArrayList; // For dynamic user list
import java.util.List; // For managing list of users

// User class implements Serializable for file storage
class User implements Serializable {
    String name; // Stores user's name
    String meterNumber; // Stores meter number
    double unitsConsumed; // Stores the number of units consumed

    // Constructor to initialize user details
    User(String name, String meterNumber, double unitsConsumed) {
        this.name = name;
        this.meterNumber = meterNumber;
        this.unitsConsumed = unitsConsumed;
    }
}

// Splash Screen Class (Displayed for 2 seconds before opening the main application)
class SplashScreen extends JFrame {
    public SplashScreen() {
        setTitle("Electricity Billing System"); // Set window title
        setSize(400, 300); // Set window size
        setLocationRelativeTo(null); // Center the window
        setUndecorated(true); // Remove default frame decorations
        setShape(new java.awt.geom.RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25)); // Rounded corners

        // Panel for splash screen
        JPanel panel = new JPanel();
        panel.setBackground(new Color(11, 143, 47)); // Dark Green background color
        panel.setLayout(new BorderLayout()); // Set layout

        // Welcome Label
        JLabel label = new JLabel("Welcome to Electricity Billing System", SwingConstants.CENTER);
        label.setForeground(Color.WHITE); // White text
        label.setFont(new Font("Serif", Font.BOLD, 22)); // Font style
        panel.add(label, BorderLayout.CENTER); // Add label to center

        // Credits Label
        JLabel credit = new JLabel("- By Group - 17", SwingConstants.CENTER);
        credit.setForeground(Color.WHITE);
        credit.setFont(new Font("Serif", Font.ITALIC, 16));
        panel.add(credit, BorderLayout.SOUTH); // Add credits at bottom

        add(panel); // Add panel to frame
        setVisible(true); // Show splash screen

        // Timer to close splash screen and open main GUI after 2 seconds
        Timer timer = new Timer(2000, e -> {
            dispose(); // Close splash screen
            new ElectricityBillingGUI(); // Open main GUI
        });
        timer.setRepeats(false); // Ensure it runs only once
        timer.start(); // Start timer
    }
}

// Main GUI Class
public class ElectricityBillingGUI extends JFrame {
    private List<User> users = new ArrayList<>(); // List to store users
    private JTextField nameField, meterField, unitsField, searchField; // Input fields
    private JTextArea displayArea; // Display area for users
    private final String FILE_NAME = "users.txt"; // File name for storing data

    // Constructor - Set up the GUI
    public ElectricityBillingGUI() {
        setTitle("Electricity Billing System"); // Set title
        setSize(500, 400); // Set window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close program on exit
        setLayout(new FlowLayout()); // Set layout
        setLocationRelativeTo(null); // Center window

        loadUsers(); // Load users from file at startup
        
        // Input Fields for Name, Meter Number, and Units Consumed
        add(new JLabel("Name:"));
        nameField = new JTextField(10);
        add(nameField);

        add(new JLabel("Meter No:"));
        meterField = new JTextField(10);
        add(meterField);

        add(new JLabel("Units:"));
        unitsField = new JTextField(5);
        add(unitsField);

        // Buttons for various functionalities
        JButton addButton = new JButton("Add User");
        add(addButton);
        addButton.addActionListener(e -> addUser()); // Call addUser() when clicked

        JButton displayButton = new JButton("Display Users");
        add(displayButton);
        displayButton.addActionListener(e -> displayUsers()); // Call displayUsers() when clicked
        
        JButton clearButton = new JButton("Clear Screen");
        add(clearButton);
        clearButton.addActionListener(e -> displayArea.setText("")); // Clear the display area
        
        // Search/Delete Field
        add(new JLabel("Search/Delete Meter No:"));
        searchField = new JTextField(10);
        add(searchField);

        JButton billButton = new JButton("Generate Bill");
        add(billButton);
        billButton.addActionListener(e -> generateBill()); // Call generateBill() when clicked
        
        JButton deleteButton = new JButton("Delete User");
        add(deleteButton);
        deleteButton.addActionListener(e -> deleteUser()); // Call deleteUser() when clicked
        
        // Display Area
        displayArea = new JTextArea(10, 40);
        displayArea.setEditable(false); // Make it read-only
        add(new JScrollPane(displayArea)); // Add scroll support

        setVisible(true); // Show the GUI
    }

    // Add User to the list
    private void addUser() {
        String name = nameField.getText();
        String meterNumber = meterField.getText();
        double units;
        try {
            units = Double.parseDouble(unitsField.getText()); // Convert input to double
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter valid units!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        users.add(new User(name, meterNumber, units)); // Add new user
        saveUsers(); // Save data
        JOptionPane.showMessageDialog(this, "User added successfully!");
        nameField.setText("");
        meterField.setText("");
        unitsField.setText("");
    }

    // Display all users
    private void displayUsers() {
        if (users.isEmpty()) {
            displayArea.setText("No users found!"); // Show message if empty
            return;
        }
        StringBuilder sb = new StringBuilder("User List:\n");
        for (User user : users) {
            sb.append("Name: ").append(user.name)
              .append(", Meter: ").append(user.meterNumber)
              .append(", Units: ").append(user.unitsConsumed).append("\n");
        }
        displayArea.setText(sb.toString()); // Display users
    }

    // Generate electricity bill for a user
    private void generateBill() {
        String meterNumber = searchField.getText().trim();
        
        if (meterNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter meter number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        for (User user : users) {
            if (user.meterNumber.equals(meterNumber)) {
                double billAmount = user.unitsConsumed * 5.0; // Rs. 5 per unit
                JOptionPane.showMessageDialog(this, "Bill for " + user.name + " (Meter: " + user.meterNumber + ")\nRs. " + billAmount);
                return;
            }
        }
    
        JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Delete user from the list
    private void deleteUser() {
        String meterNumber = searchField.getText().trim();
        
        if (meterNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter meter number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    
        boolean removed = users.removeIf(user -> user.meterNumber.equals(meterNumber)); // Remove user
    
        if (removed) {
            saveUsers(); // Save updated data
            JOptionPane.showMessageDialog(this, "User deleted successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Save users to a file
    private void saveUsers() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load users from a file
    private void loadUsers() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            users = (List<User>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            users = new ArrayList<>();
        }
    }

    // Main Method
    public static void main(String[] args) {
        new SplashScreen(); // Start with splash screen
    }
}
