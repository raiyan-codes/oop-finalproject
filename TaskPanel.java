import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Panel for displaying and managing tasks
 */
public class TaskPanel extends JPanel {
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton completeButton;
    private JButton reassignButton;
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<String> filterComboBox;
    private DataManager dataManager;
    
    /**
     * Constructor for the TaskPanel
     * @param dataManager The data manager
     */
    public TaskPanel(DataManager dataManager) {
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
        JLabel titleLabel = new JLabel("My Tasks");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Create control panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
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
        
        // Buttons
        addButton = new JButton("Add Task");
        editButton = new JButton("Edit Task");
        deleteButton = new JButton("Delete Task");
        completeButton = new JButton("Complete Task");
        reassignButton = new JButton("Reassign Task");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(reassignButton);
        
        // Add components to control panel
        controlPanel.add(filterPanel, BorderLayout.WEST);
        controlPanel.add(searchPanel, BorderLayout.CENTER);
        controlPanel.add(buttonPanel, BorderLayout.SOUTH);
        
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
        User currentUser = dataManager.getCurrentUser();
        
        if (currentUser == null) {
            return;
        }
        
        // Get tasks based on filter
        String filter = (String) filterComboBox.getSelectedItem();
        if ("Pending Tasks".equals(filter)) {
            tasks = dataManager.getCurrentUserTasksByStatus(Task.TaskStatus.PENDING);
        } else if ("Completed Tasks".equals(filter)) {
            tasks = dataManager.getCurrentUserTasksByStatus(Task.TaskStatus.COMPLETED);
        } else {
            tasks = dataManager.getCurrentUserTasks();
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
     * Gets the selected task ID
     * @return The selected task ID or null if no task is selected
     */
    public String getSelectedTaskId() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        
        return (String) tableModel.getValueAt(selectedRow, 0);
    }
    
    /**
     * Shows a task dialog for adding or editing a task
     * @param task The task to edit, or null for a new task
     * @param users The list of users to assign the task to
     * @return The task data or null if canceled
     */
    public TaskData showTaskDialog(Task task, List<User> users) {
        // Create dialog
        JDialog dialog = new JDialog();
        dialog.setTitle(task == null ? "Add Task" : "Edit Task");
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Form components
        JLabel nameLabel = new JLabel("Task Name:");
        JTextField nameField = new JTextField(20);
        
        JLabel descriptionLabel = new JLabel("Description:");
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        
        JLabel assignedToLabel = new JLabel("Assigned To:");
        JComboBox<User> assignedToComboBox = new JComboBox<>();
        for (User user : users) {
            assignedToComboBox.addItem(user);
        }
        
        JLabel dueDateLabel = new JLabel("Due Date:");
        JSpinner dueDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dueDateSpinner, "yyyy-MM-dd");
        dueDateSpinner.setEditor(dateEditor);
        
        JCheckBox recurringCheckBox = new JCheckBox("Recurring Weekly");
        
        // If editing, set initial values
        if (task != null) {
            nameField.setText(task.getName());
            descriptionArea.setText(task.getDescription());
            
            for (int i = 0; i < assignedToComboBox.getItemCount(); i++) {
                User user = assignedToComboBox.getItemAt(i);
                if (user.getId().equals(task.getAssignedToUserId())) {
                    assignedToComboBox.setSelectedIndex(i);
                    break;
                }
            }
            
            // Set due date
            java.util.Date date = java.sql.Date.valueOf(task.getDueDate());
            dueDateSpinner.setValue(date);
            
            recurringCheckBox.setSelected(task.isRecurring());
        }
        
        // Add components to form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(descriptionLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(descriptionScrollPane, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(assignedToLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(assignedToComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(dueDateLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(dueDateSpinner, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(recurringCheckBox, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to dialog
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Prepare result holder
        final TaskData[] result = {null};
        
        // Button actions
        saveButton.addActionListener(e -> {
            String name = nameField.getText();
            if (name == null || name.trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Task name is required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            User selectedUser = (User) assignedToComboBox.getSelectedItem();
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a user to assign the task to.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            java.util.Date date = (java.util.Date) dueDateSpinner.getValue();
            LocalDate dueDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            result[0] = new TaskData(
                name,
                descriptionArea.getText(),
                selectedUser.getId(),
                dueDate,
                recurringCheckBox.isSelected()
            );
            
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        // Show dialog
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        return result[0];
    }
    
    /**
     * Shows a dialog for reassigning a task
     * @param task The task to reassign
     * @param users The list of users to choose from
     * @return The ID of the user to reassign the task to, or null if canceled
     */
    public String showReassignDialog(Task task, List<User> users) {
        User currentAssignee = dataManager.getUserById(task.getAssignedToUserId());
        String currentAssigneeName = currentAssignee != null ? currentAssignee.getUsername() : "Unknown";
        
        JComboBox<User> userComboBox = new JComboBox<>();
        for (User user : users) {
            if (!user.getId().equals(task.getAssignedToUserId())) {
                userComboBox.addItem(user);
            }
        }
        
        if (userComboBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No other users available to reassign task.", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        Object[] message = {
            "Current Assignee: " + currentAssigneeName,
            "New Assignee:",
            userComboBox
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Reassign Task", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            User selectedUser = (User) userComboBox.getSelectedItem();
            return selectedUser.getId();
        }
        
        return null;
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
        User currentUser = dataManager.getCurrentUser();
        
        for (Task task : searchResults) {
            // Only show tasks assigned to the current user
            if (task.getAssignedToUserId().equals(currentUser.getId())) {
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
    }
    
    /**
     * Sets the action listener for the add button
     * @param listener The action listener
     */
    public void setAddButtonListener(ActionListener listener) {
        addButton.addActionListener(listener);
    }
    
    /**
     * Sets the action listener for the edit button
     * @param listener The action listener
     */
    public void setEditButtonListener(ActionListener listener) {
        editButton.addActionListener(listener);
    }
    
    /**
     * Sets the action listener for the delete button
     * @param listener The action listener
     */
    public void setDeleteButtonListener(ActionListener listener) {
        deleteButton.addActionListener(listener);
    }
    
    /**
     * Sets the action listener for the complete button
     * @param listener The action listener
     */
    public void setCompleteButtonListener(ActionListener listener) {
        completeButton.addActionListener(listener);
    }
    
    /**
     * Sets the action listener for the reassign button
     * @param listener The action listener
     */
    public void setReassignButtonListener(ActionListener listener) {
        reassignButton.addActionListener(listener);
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
    
    /**
     * Inner class representing task data
     */
    public static class TaskData {
        private String name;
        private String description;
        private String assignedToUserId;
        private LocalDate dueDate;
        private boolean isRecurring;
        
        /**
         * Constructor for TaskData
         * @param name The task name
         * @param description The task description
         * @param assignedToUserId The ID of the user the task is assigned to
         * @param dueDate The due date of the task
         * @param isRecurring Whether the task is recurring
         */
        public TaskData(String name, String description, String assignedToUserId,
                      LocalDate dueDate, boolean isRecurring) {
            this.name = name;
            this.description = description;
            this.assignedToUserId = assignedToUserId;
            this.dueDate = dueDate;
            this.isRecurring = isRecurring;
        }
        
        /**
         * Gets the task name
         * @return The task name
         */
        public String getName() {
            return name;
        }
        
        /**
         * Gets the task description
         * @return The task description
         */
        public String getDescription() {
            return description;
        }
        
        /**
         * Gets the ID of the user the task is assigned to
         * @return The ID of the user the task is assigned to
         */
        public String getAssignedToUserId() {
            return assignedToUserId;
        }
        
        /**
         * Gets the due date of the task
         * @return The due date of the task
         */
        public LocalDate getDueDate() {
            return dueDate;
        }
        
        /**
         * Gets whether the task is recurring
         * @return Whether the task is recurring
         */
        public boolean isRecurring() {
            return isRecurring;
        }
    }
} 