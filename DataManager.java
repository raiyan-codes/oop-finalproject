import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the data for the application, including users and tasks
 */
public class DataManager {
    private List<User> users;
    private List<Task> tasks;
    private User currentUser;
    private static final String DATA_FILE = "choremanager.dat";
    
    /**
     * Constructor for the DataManager
     */
    public DataManager() {
        this.users = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.loadData();
    }
    
    /**
     * Registers a new user
     * @param username The username for the new user
     * @param password The password for the new user
     * @return True if the registration was successful, false otherwise
     */
    public boolean registerUser(String username, String password) {
        if (getUserByUsername(username) != null) {
            return false; // Username already exists
        }
        
        User newUser = new User(username, password);
        users.add(newUser);
        saveData();
        return true;
    }
    
    /**
     * Logs in a user
     * @param username The username of the user
     * @param password The password of the user
     * @return True if the login was successful, false otherwise
     */
    public boolean loginUser(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            return false;
        }
        
        this.currentUser = user;
        return true;
    }
    
    /**
     * Logs out the current user
     */
    public void logoutUser() {
        this.currentUser = null;
    }
    
    /**
     * Gets a user by username
     * @param username The username to search for
     * @return The User object if found, null otherwise
     */
    public User getUserByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Gets a user by ID
     * @param userId The ID to search for
     * @return The User object if found, null otherwise
     */
    public User getUserById(String userId) {
        return users.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Gets all users
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
    
    /**
     * Gets the current user
     * @return The current user
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Creates a new task
     * @param name The name of the task
     * @param description The description of the task
     * @param assignedToUserId The ID of the user the task is assigned to
     * @param dueDate The due date of the task
     * @param isRecurring Whether the task is recurring
     * @return The created Task object
     */
    public Task createTask(String name, String description, String assignedToUserId, 
                         LocalDate dueDate, boolean isRecurring) {
        if (currentUser == null) {
            return null;
        }
        
        Task newTask = new Task(name, description, assignedToUserId, 
                              currentUser.getId(), dueDate, isRecurring);
        tasks.add(newTask);
        saveData();
        return newTask;
    }
    
    /**
     * Updates an existing task
     * @param taskId The ID of the task to update
     * @param name The new name of the task
     * @param description The new description of the task
     * @param assignedToUserId The new ID of the user the task is assigned to
     * @param dueDate The new due date of the task
     * @param isRecurring Whether the task is recurring
     * @return True if the update was successful, false otherwise
     */
    public boolean updateTask(String taskId, String name, String description, 
                           String assignedToUserId, LocalDate dueDate, boolean isRecurring) {
        Task task = getTaskById(taskId);
        if (task == null) {
            return false;
        }
        
        task.setName(name);
        task.setDescription(description);
        task.setAssignedToUserId(assignedToUserId);
        task.setDueDate(dueDate);
        task.setRecurring(isRecurring);
        saveData();
        return true;
    }
    
    /**
     * Deletes a task
     * @param taskId The ID of the task to delete
     * @return True if the deletion was successful, false otherwise
     */
    public boolean deleteTask(String taskId) {
        Task task = getTaskById(taskId);
        if (task == null) {
            return false;
        }
        
        tasks.remove(task);
        saveData();
        return true;
    }
    
    /**
     * Marks a task as completed
     * @param taskId The ID of the task to mark as completed
     * @return True if the operation was successful, false otherwise
     */
    public boolean markTaskAsCompleted(String taskId) {
        if (currentUser == null) {
            return false;
        }
        
        Task task = getTaskById(taskId);
        if (task == null) {
            return false;
        }
        
        task.markAsCompleted(currentUser.getId());
        
        // If the task is recurring, create a new task for next week
        if (task.isRecurring()) {
            Task nextWeekTask = task.createRecurringCopy();
            if (nextWeekTask != null) {
                tasks.add(nextWeekTask);
            }
        }
        
        saveData();
        return true;
    }
    
    /**
     * Reassigns a task to a different user
     * @param taskId The ID of the task to reassign
     * @param newUserId The ID of the user to reassign the task to
     * @return True if the reassignment was successful, false otherwise
     */
    public boolean reassignTask(String taskId, String newUserId) {
        Task task = getTaskById(taskId);
        if (task == null || getUserById(newUserId) == null) {
            return false;
        }
        
        task.setAssignedToUserId(newUserId);
        saveData();
        return true;
    }
    
    /**
     * Gets a task by ID
     * @param taskId The ID to search for
     * @return The Task object if found, null otherwise
     */
    public Task getTaskById(String taskId) {
        return tasks.stream()
                .filter(task -> task.getId().equals(taskId))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Gets all tasks
     * @return A list of all tasks
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }
    
    /**
     * Gets tasks assigned to a specific user
     * @param userId The ID of the user
     * @return A list of tasks assigned to the specified user
     */
    public List<Task> getTasksByUser(String userId) {
        return tasks.stream()
                .filter(task -> task.getAssignedToUserId().equals(userId))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets tasks with a specific status
     * @param status The status to filter by
     * @return A list of tasks with the specified status
     */
    public List<Task> getTasksByStatus(Task.TaskStatus status) {
        return tasks.stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Searches for tasks containing the specified query in their name or description
     * @param query The search query
     * @return A list of tasks matching the query
     */
    public List<Task> searchTasks(String query) {
        String lowerQuery = query.toLowerCase();
        return tasks.stream()
                .filter(task -> 
                    task.getName().toLowerCase().contains(lowerQuery) || 
                    task.getDescription().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets tasks assigned to the current user
     * @return A list of tasks assigned to the current user
     */
    public List<Task> getCurrentUserTasks() {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        return getTasksByUser(currentUser.getId());
    }
    
    /**
     * Gets tasks assigned to the current user with a specific status
     * @param status The status to filter by
     * @return A list of tasks assigned to the current user with the specified status
     */
    public List<Task> getCurrentUserTasksByStatus(Task.TaskStatus status) {
        if (currentUser == null) {
            return new ArrayList<>();
        }
        
        return tasks.stream()
                .filter(task -> task.getAssignedToUserId().equals(currentUser.getId()) 
                        && task.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets weekly report data
     * @return A WeeklyReport object containing report data
     */
    public WeeklyReport getWeeklyReport() {
        return new WeeklyReport(users, tasks);
    }
    
    /**
     * Saves data to file
     */
    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
            oos.writeObject(tasks);
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }
    
    /**
     * Loads data from file
     */
    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            initializeDemoData();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            users = (List<User>) ois.readObject();
            tasks = (List<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data: " + e.getMessage());
            initializeDemoData();
        }
    }
    
    /**
     * Initializes demo data
     */
    private void initializeDemoData() {
        // Create demo users
        registerUser("naya", "password123");
        registerUser("gabriela", "password123");
        registerUser("raiyan", "password123");
        
        // Get user IDs
        String nayaId = getUserByUsername("naya").getId();
        String gabrielaId = getUserByUsername("gabriela").getId();
        String raiyanId = getUserByUsername("raiyan").getId();
        
        // Temporarily set current user to create tasks
        currentUser = getUserByUsername("naya");
        
        // Create demo tasks
        createTask("Clean Kitchen", "Clean counters, sink, and floor", 
                  nayaId, LocalDate.now().plusDays(2), true);
        
        createTask("Take out Trash", "Take all trash to dumpster", 
                  gabrielaId, LocalDate.now().plusDays(1), true);
        
        createTask("Buy Groceries", "Get milk, eggs, bread, and vegetables", 
                  raiyanId, LocalDate.now().plusDays(3), false);
        
        createTask("Clean Bathroom", "Clean toilet, shower, and sink", 
                  nayaId, LocalDate.now().plusDays(4), true);
        
        createTask("Pay Rent", "Send payment to landlord", 
                  gabrielaId, LocalDate.now().plusDays(10), false);
        
        // Mark a task as completed
        markTaskAsCompleted(tasks.get(1).getId());
        
        // Reset current user
        currentUser = null;
        
        // Save the demo data
        saveData();
    }
} 