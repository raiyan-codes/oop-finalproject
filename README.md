# Roommate Chore Manager

**Team Members:**
- Naya Haddad (nh2300@nyu.edu)
- Gabriela Terreros (gt2184@nyu.edu)
- Golam Raiyan (gr2257@nyu.edu)

## Description

The Roommate Chore Manager is a Java Swing GUI application that helps roommates manage shared household responsibilities and chores. Each roommate can log in, view their assigned tasks, check off completed ones, and reassign tasks as needed. The system tracks recurring tasks (e.g., cleaning, groceries) and generates weekly summaries to promote accountability and fair distribution of work.

## Features

1. **User Management**
   - User registration and login
   - User authentication

2. **Task Management**
   - Create new tasks with name, description, due date, and assignment
   - Mark tasks as completed
   - Modify existing tasks
   - Delete tasks
   - Reassign tasks to other roommates
   - Search for specific tasks

3. **Household Overview**
   - View all household tasks
   - Filter tasks by status (all, pending, completed)
   - Search across all household tasks

4. **Weekly Reports**
   - View statistics on task completion by roommate
   - Review all tasks for the week
   - Download weekly reports as text files

## Code Structure

The application follows an object-oriented design with these main classes:

- **User.java**: Represents a user/roommate in the system
- **Task.java**: Represents a chore/task with assignment and status info
- **DataManager.java**: Manages the data storage and business logic
- **WeeklyReport.java**: Generates reports on task completion
- **LoginPanel.java**: UI for login and registration
- **TaskPanel.java**: UI for task management
- **HouseholdPanel.java**: UI for household task overview
- **ReportPanel.java**: UI for weekly reports
- **RoommateChoreManager.java**: Main application class

## How to Run

1. Compile all Java files:
   ```
   javac *.java
   ```

2. Run the application:
   ```
   java RoommateChoreManager
   ```

## Demo Accounts

The application comes with three demo users pre-configured:

- Username: `naya`, Password: `password123`
- Username: `gabriela`, Password: `password123`
- Username: `raiyan`, Password: `password123`

These accounts have been pre-populated with sample tasks to demonstrate the application's functionality.

## Data Persistence

The application stores all data (users and tasks) in a file called `choremanager.dat` in the same directory as the application. This ensures that your data persists between sessions.

## System Requirements

- Java 11 or higher
- Java Swing (included in standard JDK)