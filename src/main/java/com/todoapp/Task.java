package com.todoapp;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Priority {
        HIGH("Høj"), MEDIUM("Mellem"), LOW("Lav");

        private final String displayName;

        Priority(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private String title;
    private Priority priority;
    private String category;
    private LocalDate deadline;
    private boolean completed;

    public Task(String title) {
        this.title = title;
        this.priority = Priority.MEDIUM;
        this.category = "Generelt";
        this.deadline = null;
        this.completed = false;
    }

    public Task(String title, Priority priority, String category, LocalDate deadline) {
        this.title = title;
        this.priority = priority;
        this.category = category;
        this.deadline = deadline;
        this.completed = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getDeadlineString() {
        if (deadline == null) {
            return "Ingen deadline";
        }
        return deadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        if (completed) {
            sb.append("[✓] ");
        } else {
            sb.append("[ ] ");
        }
        
        sb.append(title);
        sb.append(" | ").append(priority.getDisplayName());
        sb.append(" | ").append(category);
        
        if (deadline != null) {
            sb.append(" | Deadline: ").append(getDeadlineString());
        }
        
        return sb.toString();
    }
}
