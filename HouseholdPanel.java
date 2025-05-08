import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for displaying and managing household tasks
 */
public class HouseholdPanel extends JPanel {
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<String> filterComboBox;
    private DataManager dataManager;
    
    /**
     * Constructor for the HouseholdPanel
     * @param dataManager The data manager
     */
    public HouseholdPanel(DataManager dataManager) {
        this.dataManager = dataManager;
        initializeUI();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(70, 175, 80));
        JLabel titleLabel = new JLabel("Household Tasks");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Create control panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Filter components
        JLabel filterLabel = new JLabel("Filter: ");
        filterComboBox = new JComboBox<>(new String[]{"All Tasks", "Pending Tasks", "Completed Tasks"});
        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);
        
        // Search components
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Add components to control panel
        controlPanel.add(filterPanel, BorderLayout.WEST);
        controlPanel.add(searchPanel, BorderLayout.CENTER);
        
        // Create table
        String[] columnNames = {"ID", "Name", "Description", "Assigned To", "Due Date", "Status", "Recurring"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        taskTable = new JTable(tableModel);
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        taskTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(taskTable);
        
        // Add components to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }
    
    /**
     * Refreshes the task table with current task data
     */
    public void refreshTaskTable() {
        tableModel.setRowCount(0);
        
        List<Task> tasks;
        
        // Get tasks based on filter
        String filter = (String) filterComboBox.getSelectedItem();
        if ("Pending Tasks".equals(filter)) {
            tasks = dataManager.getTasksByStatus(Task.TaskStatus.PENDING);
        } else if ("Completed Tasks".equals(filter)) {
            tasks = dataManager.getTasksByStatus(Task.TaskStatus.COMPLETED);
        } else {
            tasks = dataManager.getAllTasks();
        }
        
        // Populate table
        for (Task task : tasks) {
            User assignedUser = dataManager.getUserById(task.getAssignedToUserId());
            String assignedUsername = assignedUser != null ? assignedUser.getUsername() : "Unknown";
            
            Object[] row = {
                task.getId(),
                task.getName(),
                task.getDescription(),
                assignedUsername,
                task.getDueDate(),
                task.getStatus(),
                task.isRecurring() ? "Yes" : "No"
            };
            
            tableModel.addRow(row);
        }
    }
    
    /**
     * Searches for tasks
     */
    public void searchTasks() {
        String query = searchField.getText();
        if (query == null || query.trim().isEmpty()) {
            refreshTaskTable();
            return;
        }
        
        tableModel.setRowCount(0);
        
        List<Task> searchResults = dataManager.searchTasks(query);
        
        for (Task task : searchResults) {
            User assignedUser = dataManager.getUserById(task.getAssignedToUserId());
            String assignedUsername = assignedUser != null ? assignedUser.getUsername() : "Unknown";
            
            Object[] row = {
                task.getId(),
                task.getName(),
                task.getDescription(),
                assignedUsername,
                task.getDueDate(),
                task.getStatus(),
                task.isRecurring() ? "Yes" : "No"
            };
            
            tableModel.addRow(row);
        }
    }
    
    /**
     * Sets the action listener for the search button
     * @param listener The action listener
     */
    public void setSearchButtonListener(ActionListener listener) {
        searchButton.addActionListener(listener);
    }
    
    /**
     * Sets the action listener for the filter combo box
     * @param listener The action listener
     */
    public void setFilterComboBoxListener(ActionListener listener) {
        filterComboBox.addActionListener(listener);
    }
} 