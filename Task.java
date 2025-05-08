import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a task in the system that can be assigned to a roommate
 */
public class Task implements Serializable {
    private String id;
    private String name;
    private String description;
    private String assignedToUserId;
    private String createdByUserId;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String completedByUserId;
    private boolean isRecurring;
    private TaskStatus status;
    
    /**
     * Enum representing the status of a task
     */
    public enum TaskStatus {
        PENDING,
        COMPLETED
    }
    
    /**
     * Constructor for creating a new task
     * @param name The name of the task
     * @param description The description of the task
     * @param assignedToUserId The ID of the user the task is assigned to
     * @param createdByUserId The ID of the user who created the task
     * @param dueDate The due date of the task
     * @param isRecurring Whether the task is recurring
     */
    public Task(String name, String description, String assignedToUserId, 
                String createdByUserId, LocalDate dueDate, boolean isRecurring) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.assignedToUserId = assignedToUserId;
        this.createdByUserId = createdByUserId;
        this.dueDate = dueDate;
        this.isRecurring = isRecurring;
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Returns the ID of the task
     * @return The ID as a String
     */
    public String getId() {
        return id;
    }
    
    /**
     * Returns the name of the task
     * @return The name as a String
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the task
     * @param name The new name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns the description of the task
     * @return The description as a String
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description of the task
     * @param description The new description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Returns the ID of the user the task is assigned to
     * @return The ID of the assigned user
     */
    public String getAssignedToUserId() {
        return assignedToUserId;
    }
    
    /**
     * Sets the ID of the user the task is assigned to
     * @param assignedToUserId The new user ID
     */
    public void setAssignedToUserId(String assignedToUserId) {
        this.assignedToUserId = assignedToUserId;
    }
    
    /**
     * Returns the ID of the user who created the task
     * @return The ID of the user who created the task
     */
    public String getCreatedByUserId() {
        return createdByUserId;
    }
    
    /**
     * Returns the due date of the task
     * @return The due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    /**
     * Sets the due date of the task
     * @param dueDate The new due date
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
    
    /**
     * Returns the date and time when the task was created
     * @return The creation date and time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Returns the date and time when the task was completed
     * @return The completion date and time
     */
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    /**
     * Returns the ID of the user who completed the task
     * @return The ID of the user who completed the task
     */
    public String getCompletedByUserId() {
        return completedByUserId;
    }
    
    /**
     * Returns whether the task is recurring
     * @return true if the task is recurring, false otherwise
     */
    public boolean isRecurring() {
        return isRecurring;
    }
    
    /**
     * Sets whether the task is recurring
     * @param recurring Whether the task is recurring
     */
    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }
    
    /**
     * Returns the status of the task
     * @return The status of the task
     */
    public TaskStatus getStatus() {
        return status;
    }
    
    /**
     * Marks the task as completed by the specified user
     * @param completedByUserId The ID of the user who completed the task
     */
    public void markAsCompleted(String completedByUserId) {
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.completedByUserId = completedByUserId;
    }
    
    /**
     * Marks the task as pending
     */
    public void markAsPending() {
        this.status = TaskStatus.PENDING;
        this.completedAt = null;
        this.completedByUserId = null;
    }
    
    /**
     * Creates a recurring copy of this task for the next week
     * @return A new Task object with updated due date
     */
    public Task createRecurringCopy() {
        if (!isRecurring) {
            return null;
        }
        
        Task copy = new Task(
            this.name,
            this.description,
            this.assignedToUserId,
            this.createdByUserId,
            this.dueDate.plusWeeks(1),
            true
        );
        
        return copy;
    }
    
    /**
     * Compares this task with the specified task for equality
     * @param o The object to compare with
     * @return true if the tasks are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Task task = (Task) o;
        return id.equals(task.id);
    }
    
    /**
     * Returns a hash code value for the task
     * @return A hash code value for the task
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    /**
     * Returns a string representation of the task
     * @return A string representation of the task
     */
    @Override
    public String toString() {
        return name + " (Due: " + dueDate + ")";
    }
} 