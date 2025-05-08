import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a weekly report of tasks
 */
public class WeeklyReport implements Serializable {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Task> weeklyTasks;
    private List<UserStats> userStats;
    
    /**
     * Constructor for the WeeklyReport
     * @param users List of all users
     * @param tasks List of all tasks
     */
    public WeeklyReport(List<User> users, List<Task> tasks) {
        this.endDate = LocalDate.now();
        this.startDate = endDate.minusDays(7);
        
        // Get tasks from this week
        this.weeklyTasks = filterTasksForWeek(tasks);
        
        // Calculate stats for each user
        this.userStats = calculateUserStats(users, weeklyTasks);
    }
    
    /**
     * Gets the start date of the report
     * @return The start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }
    
    /**
     * Gets the end date of the report
     * @return The end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }
    
    /**
     * Gets the tasks for the week
     * @return The list of tasks for the week
     */
    public List<Task> getWeeklyTasks() {
        return weeklyTasks;
    }
    
    /**
     * Gets the user stats for the week
     * @return The list of user stats for the week
     */
    public List<UserStats> getUserStats() {
        return userStats;
    }
    
    /**
     * Filters tasks for the current week
     * @param allTasks List of all tasks
     * @return List of tasks for the current week
     */
    private List<Task> filterTasksForWeek(List<Task> allTasks) {
        List<Task> filteredTasks = new ArrayList<>();
        LocalDateTime weekStart = startDate.atStartOfDay();
        LocalDateTime weekEnd = endDate.plusDays(1).atStartOfDay();
        
        for (Task task : allTasks) {
            LocalDateTime taskDate = task.getCreatedAt();
            if (taskDate.isAfter(weekStart) && taskDate.isBefore(weekEnd)) {
                filteredTasks.add(task);
            }
        }
        
        return filteredTasks;
    }
    
    /**
     * Calculates stats for each user
     * @param users List of all users
     * @param weeklyTasks List of tasks for the week
     * @return List of user stats
     */
    private List<UserStats> calculateUserStats(List<User> users, List<Task> weeklyTasks) {
        Map<String, UserStats> statsMap = new HashMap<>();
        
        // Initialize stats for each user
        for (User user : users) {
            statsMap.put(user.getId(), new UserStats(user.getUsername()));
        }
        
        // Calculate stats based on tasks
        for (Task task : weeklyTasks) {
            String userId = task.getAssignedToUserId();
            if (statsMap.containsKey(userId)) {
                UserStats stats = statsMap.get(userId);
                stats.totalTasks++;
                
                if (task.getStatus() == Task.TaskStatus.COMPLETED) {
                    stats.completedTasks++;
                } else {
                    stats.pendingTasks++;
                }
            }
        }
        
        return new ArrayList<>(statsMap.values());
    }
    
    /**
     * Generates a formatted report as a string
     * @return The formatted report
     */
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("Weekly Chore Report\n");
        report.append("------------------\n");
        report.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n\n");
        
        report.append("User Statistics:\n");
        for (UserStats stats : userStats) {
            report.append(stats.username).append(":\n");
            report.append("  Completed Tasks: ").append(stats.completedTasks).append("\n");
            report.append("  Pending Tasks: ").append(stats.pendingTasks).append("\n");
            report.append("  Total Tasks: ").append(stats.totalTasks).append("\n");
            
            // Calculate completion percentage
            int completionPercentage = 0;
            if (stats.totalTasks > 0) {
                completionPercentage = (stats.completedTasks * 100) / stats.totalTasks;
            }
            report.append("  Completion Rate: ").append(completionPercentage).append("%\n\n");
        }
        
        report.append("Tasks:\n");
        for (Task task : weeklyTasks) {
            report.append("- ").append(task.getName());
            report.append(" (Assigned to: ");
            // Find username for this task
            for (UserStats stats : userStats) {
                if (stats.username.equals(task.getAssignedToUserId())) {
                    report.append(stats.username);
                    break;
                }
            }
            report.append(", Status: ").append(task.getStatus()).append(")\n");
        }
        
        return report.toString();
    }
    
    /**
     * Inner class representing the statistics for a user
     */
    public static class UserStats implements Serializable {
        private String username;
        private int completedTasks;
        private int pendingTasks;
        private int totalTasks;
        
        /**
         * Constructor for UserStats
         * @param username The username
         */
        public UserStats(String username) {
            this.username = username;
            this.completedTasks = 0;
            this.pendingTasks = 0;
            this.totalTasks = 0;
        }
        
        /**
         * Gets the username
         * @return The username
         */
        public String getUsername() {
            return username;
        }
        
        /**
         * Gets the number of completed tasks
         * @return The number of completed tasks
         */
        public int getCompletedTasks() {
            return completedTasks;
        }
        
        /**
         * Gets the number of pending tasks
         * @return The number of pending tasks
         */
        public int getPendingTasks() {
            return pendingTasks;
        }
        
        /**
         * Gets the total number of tasks
         * @return The total number of tasks
         */
        public int getTotalTasks() {
            return totalTasks;
        }
    }
} 