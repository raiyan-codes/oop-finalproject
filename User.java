import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a user (roommate) in the system
 */
public class User implements Serializable {
    private String id;
    private String username;
    private String password;
    private LocalDateTime createdAt;
    
    /**
     * Constructor for creating a new user
     * @param username The username for the user
     * @param password The password for the user
     */
    public User(String username, String password) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Returns the ID of the user
     * @return The ID as a String
     */
    public String getId() {
        return id;
    }
    
    /**
     * Returns the username of the user
     * @return The username as a String
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username of the user
     * @param username The new username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Returns the password of the user
     * @return The password as a String
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password of the user
     * @param password The new password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Returns the date and time when the user was created
     * @return The creation date and time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Compares this user with the specified user for equality
     * @param o The object to compare with
     * @return true if the users are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        User user = (User) o;
        return id.equals(user.id);
    }
    
    /**
     * Returns a hash code value for the user
     * @return A hash code value for the user
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    /**
     * Returns a string representation of the user
     * @return A string representation of the user
     */
    @Override
    public String toString() {
        return username;
    }
} 