# Task Management App

A Java console productivity application for managing tasks and deadlines — built as part of the Auspify Technologies Software Development Internship (Task 6, Advanced level).

## Features

- Add / update / delete tasks
- Mark tasks as complete
- Filter tasks by status / priority
- Sort tasks by deadline
- Due-soon and overdue alerts
- File-based data persistence (`tasks.txt`) — data survives between runs

## Tech Stack

- Java (core, `java.time` for date handling)
- Plain-text file storage (pipe-delimited), no database server required

## How to Run

```bash
javac TaskManagementApp.java
java TaskManagementApp
```

Requires Java 11+ (uses `java.time`).

## Skills Demonstrated

- Secure input handling and validation
- Sorting and filtering logic
- System integration (deadlines, priorities, alerts)

## Note

The original task brief had the workflow descriptions for "Online Examination System" and "Task Management App" swapped between Task 5 and Task 6. This app was built to match its actual title — a task/productivity manager — which best reflects the internship's intent.
