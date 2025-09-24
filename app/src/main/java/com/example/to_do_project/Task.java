package com.example.to_do_project;

public class Task {
    private String text;
    private boolean isCompleted;
    private long id;

    // Constructor
    public Task(String text, boolean isCompleted) {
        this.text = text;
        this.isCompleted = isCompleted;
        this.id = System.currentTimeMillis(); // ID único basado en timestamp
    }

    // Constructor con ID (para cargar desde SharedPreferences)
    public Task(String text, boolean isCompleted, long id) {
        this.text = text;
        this.isCompleted = isCompleted;
        this.id = id;
    }

    // Getters y Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Método para convertir a String para SharedPreferences
    public String toStorageString() {
        return text + "|" + isCompleted + "|" + id;
    }

    // Método estático para crear Task desde String de SharedPreferences
    public static Task fromStorageString(String storageString) {
        String[] parts = storageString.split("\\|");
        if (parts.length == 3) {
            String text = parts[0];
            boolean isCompleted = Boolean.parseBoolean(parts[1]);
            long id = Long.parseLong(parts[2]);
            return new Task(text, isCompleted, id);
        }
        return null;
    }
}