import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * Task 6 (Advanced) - Task Management App
 * Auspify Technologies - Software Development Internship
 *
 * A productivity application that helps users manage tasks and deadlines.
 *
 * Features:
 *  - Add / Update / Delete Tasks
 *  - Mark Tasks Complete
 *  - Filter by Status / Priority
 *  - Sort by Deadline
 *  - Due-Soon & Overdue Alerts
 */
public class TaskManagementApp {

    static final String DATA_FILE = "tasks.txt";
    static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    static List<Task> tasks = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);
    static int nextId = 1;

    public static void main(String[] args) {
        loadData();
        boolean running = true;

        System.out.println("===========================================");
        System.out.println("   TASK MANAGEMENT APP - Auspify Tech      ");
        System.out.println("===========================================");
        showDueSoonAlerts();

        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addTask(); break;
                case "2": viewAllTasks(); break;
                case "3": updateTask(); break;
                case "4": deleteTask(); break;
                case "5": markComplete(); break;
                case "6": filterTasks(); break;
                case "7": viewSortedByDeadline(); break;
                case "8":
                    saveData();
                    System.out.println("Data saved. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.\n");
            }
        }
        sc.close();
    }

    static void printMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1. Add Task");
        System.out.println("2. View All Tasks");
        System.out.println("3. Update Task");
        System.out.println("4. Delete Task");
        System.out.println("5. Mark Task Complete");
        System.out.println("6. Filter Tasks (status/priority)");
        System.out.println("7. View Tasks Sorted by Deadline");
        System.out.println("8. Save & Exit");
        System.out.print("Enter your choice: ");
    }

    static void addTask() {
        int id = nextId++;
        System.out.print("Enter Task Title: ");
        String title = sc.nextLine().trim();
        System.out.print("Enter Description: ");
        String desc = sc.nextLine().trim();

        LocalDate deadline = null;
        while (deadline == null) {
            System.out.print("Enter Deadline (yyyy-MM-dd): ");
            String dateStr = sc.nextLine().trim();
            try {
                deadline = LocalDate.parse(dateStr, DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        System.out.print("Enter Priority (Low/Medium/High): ");
        String priority = sc.nextLine().trim();
        if (priority.isEmpty()) priority = "Medium";

        tasks.add(new Task(id, title, desc, deadline, priority, "Pending"));
        saveData();
        System.out.println("Task added successfully! (ID: " + id + ")");
    }

    static void viewAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        printTaskTable(tasks);
    }

    static void updateTask() {
        Task t = promptFindTask();
        if (t == null) return;

        System.out.print("Enter new Title (leave blank to keep '" + t.title + "'): ");
        String title = sc.nextLine().trim();
        if (!title.isEmpty()) t.title = title;

        System.out.print("Enter new Description (leave blank to keep current): ");
        String desc = sc.nextLine().trim();
        if (!desc.isEmpty()) t.description = desc;

        System.out.print("Enter new Deadline yyyy-MM-dd (leave blank to keep '" +
                t.deadline.format(DATE_FMT) + "'): ");
        String dateStr = sc.nextLine().trim();
        if (!dateStr.isEmpty()) {
            try { t.deadline = LocalDate.parse(dateStr, DATE_FMT); }
            catch (DateTimeParseException e) { System.out.println("Invalid date, keeping old value."); }
        }

        System.out.print("Enter new Priority (leave blank to keep '" + t.priority + "'): ");
        String priority = sc.nextLine().trim();
        if (!priority.isEmpty()) t.priority = priority;

        saveData();
        System.out.println("Task updated successfully!");
    }

    static void deleteTask() {
        Task t = promptFindTask();
        if (t == null) return;
        tasks.remove(t);
        saveData();
        System.out.println("Task deleted successfully!");
    }

    static void markComplete() {
        Task t = promptFindTask();
        if (t == null) return;
        t.status = "Completed";
        saveData();
        System.out.println("Task marked as Completed!");
    }

    static void filterTasks() {
        System.out.println("\n1. Filter by Status (Pending/Completed)");
        System.out.println("2. Filter by Priority (Low/Medium/High)");
        System.out.print("Choose option: ");
        String opt = sc.nextLine().trim();
        List<Task> results = new ArrayList<>();

        if (opt.equals("1")) {
            System.out.print("Enter status: ");
            String status = sc.nextLine().trim();
            for (Task t : tasks) if (t.status.equalsIgnoreCase(status)) results.add(t);
        } else if (opt.equals("2")) {
            System.out.print("Enter priority: ");
            String priority = sc.nextLine().trim();
            for (Task t : tasks) if (t.priority.equalsIgnoreCase(priority)) results.add(t);
        } else {
            System.out.println("Invalid option.");
            return;
        }
        printTaskTable(results);
    }

    static void viewSortedByDeadline() {
        List<Task> sorted = new ArrayList<>(tasks);
        sorted.sort(Comparator.comparing(t -> t.deadline));
        printTaskTable(sorted);
    }

    static void showDueSoonAlerts() {
        LocalDate today = LocalDate.now();
        List<Task> overdue = new ArrayList<>();
        List<Task> dueSoon = new ArrayList<>();
        for (Task t : tasks) {
            if (t.status.equalsIgnoreCase("Completed")) continue;
            long days = today.until(t.deadline).getDays();
            if (t.deadline.isBefore(today)) overdue.add(t);
            else if (days <= 3) dueSoon.add(t);
        }
        if (!overdue.isEmpty()) {
            System.out.println("\n[ALERT] Overdue Tasks:");
            for (Task t : overdue) System.out.println("  - " + t.title + " (Due: " + t.deadline.format(DATE_FMT) + ")");
        }
        if (!dueSoon.isEmpty()) {
            System.out.println("\n[REMINDER] Tasks Due Soon (within 3 days):");
            for (Task t : dueSoon) System.out.println("  - " + t.title + " (Due: " + t.deadline.format(DATE_FMT) + ")");
        }
    }

    static Task promptFindTask() {
        System.out.print("Enter Task ID: ");
        String idStr = sc.nextLine().trim();
        try {
            int id = Integer.parseInt(idStr);
            for (Task t : tasks) if (t.id == id) return t;
            System.out.println("Task not found.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID.");
        }
        return null;
    }

    static void printTaskTable(List<Task> list) {
        if (list.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        System.out.println("\n" + repeat("-", 105));
        System.out.printf("%-5s %-20s %-25s %-12s %-10s %-10s%n",
                "ID", "Title", "Description", "Deadline", "Priority", "Status");
        System.out.println(repeat("-", 105));
        for (Task t : list) {
            System.out.printf("%-5d %-20s %-25s %-12s %-10s %-10s%n",
                    t.id, t.title, t.description, t.deadline.format(DATE_FMT), t.priority, t.status);
        }
        System.out.println(repeat("-", 105));
        System.out.println("Total: " + list.size());
    }

    static void saveData() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Task t : tasks) {
                pw.println(t.id + "|" + t.title + "|" + t.description + "|" +
                        t.deadline.format(DATE_FMT) + "|" + t.priority + "|" + t.status);
            }
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    static void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int maxId = 0;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split("\\|", -1);
                if (p.length == 6) {
                    int id = Integer.parseInt(p[0]);
                    LocalDate deadline = LocalDate.parse(p[3], DATE_FMT);
                    tasks.add(new Task(id, p[1], p[2], deadline, p[4], p[5]));
                    if (id > maxId) maxId = id;
                }
            }
            nextId = maxId + 1;
        } catch (IOException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append(s);
        return sb.toString();
    }

    static class Task {
        int id;
        String title, description, priority, status;
        LocalDate deadline;
        Task(int id, String title, String description, LocalDate deadline, String priority, String status) {
            this.id = id; this.title = title; this.description = description;
            this.deadline = deadline; this.priority = priority; this.status = status;
        }
    }
}
