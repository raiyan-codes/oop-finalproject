import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Panel for displaying weekly reports and statistics
 */
public class ReportPanel extends JPanel {
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JButton downloadButton;
    private JPanel statsPanel;
    private DataManager dataManager;
    
    /**
     * Constructor for the ReportPanel
     * @param dataManager The data manager
     */
    public ReportPanel(DataManager dataManager) {
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
        JLabel titleLabel = new JLabel("Weekly Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        downloadButton = new JButton("Download Report");
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(70, 175, 80));
        buttonPanel.add(downloadButton);
        titlePanel.add(buttonPanel, BorderLayout.EAST);
        
        // Create stats panel
        statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        
        // Create table for tasks
        String[] columnNames = {"Name", "Description", "Assigned To", "Due Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        taskTable = new JTable(tableModel);
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        
        JScrollPane scrollPane = new JScrollPane(taskTable);
        
        // Add components to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }
    
    /**
     * Refreshes the report data
     */
    public void refreshReport() {
        // Clear existing data
        statsPanel.removeAll();
        tableModel.setRowCount(0);
        
        // Get weekly report
        WeeklyReport report = dataManager.getWeeklyReport();
        
        // Add period label
        JLabel periodLabel = new JLabel("Period: " + report.getStartDate() + " to " + report.getEndDate());
        periodLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        periodLabel.setFont(new Font("Arial", Font.BOLD, 16));
        periodLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        statsPanel.add(periodLabel);
        
        // Create statistics panel
        JPanel userStatsPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        userStatsPanel.setBorder(BorderFactory.createTitledBorder("User Statistics"));
        
        List<WeeklyReport.UserStats> userStats = report.getUserStats();
        
        for (WeeklyReport.UserStats stats : userStats) {
            JPanel userPanel = new JPanel();
            userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
            userPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            
            JLabel usernameLabel = new JLabel(stats.getUsername());
            usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            JLabel completedLabel = new JLabel("Completed: " + stats.getCompletedTasks());
            completedLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel pendingLabel = new JLabel("Pending: " + stats.getPendingTasks());
            pendingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel totalLabel = new JLabel("Total: " + stats.getTotalTasks());
            totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Calculate completion percentage
            int completionPercentage = 0;
            if (stats.getTotalTasks() > 0) {
                completionPercentage = (stats.getCompletedTasks() * 100) / stats.getTotalTasks();
            }
            
            JLabel percentageLabel = new JLabel("Completion: " + completionPercentage + "%");
            percentageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            userPanel.add(Box.createVerticalStrut(10));
            userPanel.add(usernameLabel);
            userPanel.add(Box.createVerticalStrut(10));
            userPanel.add(completedLabel);
            userPanel.add(pendingLabel);
            userPanel.add(totalLabel);
            userPanel.add(Box.createVerticalStrut(5));
            userPanel.add(percentageLabel);
            userPanel.add(Box.createVerticalStrut(10));
            
            userStatsPanel.add(userPanel);
        }
        
        statsPanel.add(userStatsPanel);
        
        // Add weekly tasks to table
        List<Task> weeklyTasks = report.getWeeklyTasks();
        
        for (Task task : weeklyTasks) {
            User assignedUser = dataManager.getUserById(task.getAssignedToUserId());
            String assignedUsername = assignedUser != null ? assignedUser.getUsername() : "Unknown";
            
            Object[] row = {
                task.getName(),
                task.getDescription(),
                assignedUsername,
                task.getDueDate(),
                task.getStatus()
            };
            
            tableModel.addRow(row);
        }
        
        // Refresh panel
        statsPanel.revalidate();
        statsPanel.repaint();
    }
    
    /**
     * Downloads the report as a text file
     */
    public void downloadReport() {
        WeeklyReport report = dataManager.getWeeklyReport();
        String reportText = report.generateReport();
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        fileChooser.setSelectedFile(new java.io.File("ChoreReport.txt"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(reportText);
                JOptionPane.showMessageDialog(this, "Report saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Sets the action listener for the download button
     * @param listener The action listener
     */
    public void setDownloadButtonListener(ActionListener listener) {
        downloadButton.addActionListener(listener);
    }
} 