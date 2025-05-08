import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application class for the Roommate Chore Manager
 */
public class RoommateChoreManager extends JFrame {
    private DataManager dataManager;
    private LoginPanel loginPanel;
    private TaskPanel taskPanel;
    private HouseholdPanel householdPanel;
    private ReportPanel reportPanel;
    private JPanel contentPanel;
    private JPanel mainPanel;
    private JLabel statusLabel;
    
    /**
     * Constructor for the RoommateChoreManager
     */
    public RoommateChoreManager() {
        dataManager = new DataManager();
        initializeUI();
        setupListeners();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        setTitle("Roommate Chore Manager");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create panels
        loginPanel = new LoginPanel();
        taskPanel = new TaskPanel(dataManager);
        householdPanel = new HouseholdPanel(dataManager);
        reportPanel = new ReportPanel(dataManager);
        
        // Create main container panel
        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(loginPanel, "login");
        
        // Create main application panel
        mainPanel = new JPanel(new BorderLayout());
        
        // Create tabs panel
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("My Tasks", taskPanel);
        tabbedPane.addTab("Household Tasks", householdPanel);
        tabbedPane.addTab("Reports", reportPanel);
        
        // Create status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel = new JLabel("Not logged in");
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        JPanel logoutPanel = new JPanel();
        logoutPanel.add(logoutButton);
        statusPanel.add(logoutPanel, BorderLayout.EAST);
        
        // Add components to main panel
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);
        
        contentPanel.add(mainPanel, "main");
        
        // Add content panel to frame
        add(contentPanel);
        
        // Show login panel by default
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "login");
        
        // Set up window close listener to save data
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dataManager.saveData();
            }
        });
    }
    
    /**
     * Sets up event listeners
     */
    private void setupListeners() {
        // Login panel listeners
        loginPanel.setLoginButtonListener(e -> login());
        loginPanel.setRegisterButtonListener(e -> register());
        
        // Task panel listeners
        taskPanel.setAddButtonListener(e -> addTask());
        taskPanel.setEditButtonListener(e -> editTask());
        taskPanel.setDeleteButtonListener(e -> deleteTask());
        taskPanel.setCompleteButtonListener(e -> completeTask());
        taskPanel.setReassignButtonListener(e -> reassignTask());
        taskPanel.setSearchButtonListener(e -> taskPanel.searchTasks());
        taskPanel.setFilterComboBoxListener(e -> taskPanel.refreshTaskTable());
        
        // Household panel listeners
        householdPanel.setSearchButtonListener(e -> householdPanel.searchTasks());
        householdPanel.setFilterComboBoxListener(e -> householdPanel.refreshTaskTable());
        
        // Report panel listeners
        reportPanel.setDownloadButtonListener(e -> reportPanel.downloadReport());
    }
    
    /**
     * Handles login
     */
    private void login() {
        String username = loginPanel.getUsername();
        String password = loginPanel.getPassword();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = dataManager.loginUser(username, password);
        
        if (success) {
            User user = dataManager.getCurrentUser();
            statusLabel.setText("Logged in as: " + user.getUsername());
            
            // Show main panel
            ((CardLayout) contentPanel.getLayout()).show(contentPanel, "main");
            
            // Refresh data
            taskPanel.refreshTaskTable();
            householdPanel.refreshTaskTable();
            reportPanel.refreshReport();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handles registration
     */
    private void register() {
        String username = loginPanel.getUsername();
        String password = loginPanel.getPassword();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = dataManager.registerUser(username, password);
        
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful. You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loginPanel.clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Handles logout
     */
    private void logout() {
        dataManager.logoutUser();
        statusLabel.setText("Not logged in");
        loginPanel.clearFields();
        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "login");
    }
    
    /**
     * Handles adding a task
     */
    private void addTask() {
        TaskPanel.TaskData taskData = taskPanel.showTaskDialog(null, dataManager.getAllUsers());
        
        if (taskData != null) {
            dataManager.createTask(
                taskData.getName(),
                taskData.getDescription(),
                taskData.getAssignedToUserId(),
                taskData.getDueDate(),
                taskData.isRecurring()
            );
            
            taskPanel.refreshTaskTable();
            householdPanel.refreshTaskTable();
        }
    }
    
    /**
     * Handles editing a task
     */
    private void editTask() {
        String taskId = taskPanel.getSelectedTaskId();
        
        if (taskId == null) {
            JOptionPane.showMessageDialog(this, "Please select a task to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Task task = dataManager.getTaskById(taskId);
        
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Task not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        TaskPanel.TaskData taskData = taskPanel.showTaskDialog(task, dataManager.getAllUsers());
        
        if (taskData != null) {
            dataManager.updateTask(
                taskId,
                taskData.getName(),
                taskData.getDescription(),
                taskData.getAssignedToUserId(),
                taskData.getDueDate(),
                taskData.isRecurring()
            );
            
            taskPanel.refreshTaskTable();
            householdPanel.refreshTaskTable();
        }
    }
    
    /**
     * Handles deleting a task
     */
    private void deleteTask() {
        String taskId = taskPanel.getSelectedTaskId();
        
        if (taskId == null) {
            JOptionPane.showMessageDialog(this, "Please select a task to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Task task = dataManager.getTaskById(taskId);
        
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Task not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this task?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            dataManager.deleteTask(taskId);
            taskPanel.refreshTaskTable();
            householdPanel.refreshTaskTable();
        }
    }
    
    /**
     * Handles marking a task as complete
     */
    private void completeTask() {
        String taskId = taskPanel.getSelectedTaskId();
        
        if (taskId == null) {
            JOptionPane.showMessageDialog(this, "Please select a task to mark as complete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Task task = dataManager.getTaskById(taskId);
        
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Task not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (task.getStatus() == Task.TaskStatus.COMPLETED) {
            JOptionPane.showMessageDialog(this, "Task is already completed.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        dataManager.markTaskAsCompleted(taskId);
        
        taskPanel.refreshTaskTable();
        householdPanel.refreshTaskTable();
        reportPanel.refreshReport();
    }
    
    /**
     * Handles reassigning a task
     */
    private void reassignTask() {
        String taskId = taskPanel.getSelectedTaskId();
        
        if (taskId == null) {
            JOptionPane.showMessageDialog(this, "Please select a task to reassign.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Task task = dataManager.getTaskById(taskId);
        
        if (task == null) {
            JOptionPane.showMessageDialog(this, "Task not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String newUserId = taskPanel.showReassignDialog(task, dataManager.getAllUsers());
        
        if (newUserId != null) {
            dataManager.reassignTask(taskId, newUserId);
            taskPanel.refreshTaskTable();
            householdPanel.refreshTaskTable();
        }
    }
    
    /**
     * Main method to start the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Set look and feel to system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Start application on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            RoommateChoreManager app = new RoommateChoreManager();
            app.setVisible(true);
        });
    }
} 