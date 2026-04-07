package com.todoapp;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.StringSelection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class TodoApp extends JFrame {
    private JTextField taskField;
    private JComboBox<Task.Priority> priorityCombo;
    private JComboBox<String> categoryCombo;
    private JTextField deadlineField;
    private JButton addButton;
    private JList<Task> taskList;
    private DefaultListModel<Task> listModel;
    private JButton deleteButton;
    private JButton markCompleteButton;
    private JComboBox<String> categoryFilterCombo;
    private JLabel statsLabel;
    private List<Task> tasks;
    private List<String> categories;
    private int draggedIndex = -1;

    public TodoApp() {
        setTitle("Todo App - Enhanced");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 650);
        setLocationRelativeTo(null);
        setResizable(true);

        tasks = new ArrayList<>();
        categories = new ArrayList<>(Arrays.asList("Generelt", "Arbeid", "Personligt", "Shopping"));
        initializeUI();

        setVisible(true);
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = createInputPanel();
        JPanel filterPanel = createFilterPanel();
        JPanel listPanel = createListPanel();
        JPanel buttonPanel = createButtonPanel();
        JPanel statsPanel = createStatsPanel();

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(listPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Tilføj ny opgave"));

        JLabel taskLabel = new JLabel("Opgave:");
        taskField = new JTextField();

        JLabel priorityLabel = new JLabel("Prioritet:");
        priorityCombo = new JComboBox<>(Task.Priority.values());

        JLabel categoryLabel = new JLabel("Kategori:");
        categoryCombo = new JComboBox<>();
        for (String cat : categories) {
            categoryCombo.addItem(cat);
        }

        JLabel deadlineLabel = new JLabel("Deadline (dd/MM/yyyy):");
        deadlineField = new JTextField();

        addButton = new JButton("Tilføj opgave");
        addButton.addActionListener(e -> addTask());

        panel.add(taskLabel);
        panel.add(taskField);
        panel.add(priorityLabel);
        panel.add(priorityCombo);
        panel.add(categoryLabel);
        panel.add(categoryCombo);
        panel.add(deadlineLabel);
        panel.add(deadlineField);

        JPanel wrapPanel = new JPanel(new BorderLayout());
        wrapPanel.add(panel, BorderLayout.CENTER);
        JPanel buttonWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonWrap.add(addButton);
        wrapPanel.add(buttonWrap, BorderLayout.SOUTH);

        return wrapPanel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Filtre"));

        JLabel filterLabel = new JLabel("Filtrer efter kategori:");
        categoryFilterCombo = new JComboBox<>();
        categoryFilterCombo.addItem("Alle");
        for (String cat : categories) {
            categoryFilterCombo.addItem(cat);
        }
        categoryFilterCombo.addActionListener(e -> updateTaskList());

        panel.add(filterLabel);
        panel.add(categoryFilterCombo);

        return panel;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Opgaver"));

        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setCellRenderer(new TaskListCellRenderer());
        
        setupDragAndDrop();
        taskList.addListSelectionListener(e -> updateStatsLabel());

        JScrollPane scrollPane = new JScrollPane(taskList);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Handlinger"));

        markCompleteButton = new JButton("✓ Mark som fuldført");
        markCompleteButton.addActionListener(e -> toggleTaskCompleted());

        JButton editButton = new JButton("✎ Rediger");
        editButton.addActionListener(e -> editTask());

        deleteButton = new JButton("✕ Slet opgave");
        deleteButton.addActionListener(e -> deleteTask());

        JButton clearCompletedButton = new JButton("Ryd fuldførte");
        clearCompletedButton.addActionListener(e -> clearCompletedTasks());

        JButton clearAllButton = new JButton("Ryd alt");
        clearAllButton.addActionListener(e -> clearAllTasks());

        statsLabel = new JLabel("Statistik: 0 total, 0 fuldført");
        statsLabel.setFont(new Font("Arial", Font.BOLD, 12));

        panel.add(markCompleteButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(clearCompletedButton);
        panel.add(clearAllButton);
        panel.add(statsLabel);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statsLabel = new JLabel("Statistik: 0 total, 0 fuldført, 0 ikke-fuldført");
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(statsLabel);
        return panel;
    }

    private void setupDragAndDrop() {
        taskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                draggedIndex = taskList.locationToIndex(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int targetIndex = taskList.locationToIndex(e.getPoint());
                if (draggedIndex != -1 && targetIndex != -1 && draggedIndex != targetIndex) {
                    Task draggedTask = listModel.getElementAt(draggedIndex);
                    listModel.remove(draggedIndex);
                    listModel.insertElementAt(draggedTask, targetIndex);
                    tasks.remove(draggedIndex);
                    tasks.add(targetIndex, draggedTask);
                }
                draggedIndex = -1;
            }
        });
    }

    private void addTask() {
        String title = taskField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Venligst enter en opgave!", "Advarsel", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Task.Priority priority = (Task.Priority) priorityCombo.getSelectedItem();
        String category = (String) categoryCombo.getSelectedItem();
        LocalDate deadline = null;

        if (!deadlineField.getText().trim().isEmpty()) {
            try {
                deadline = LocalDate.parse(deadlineField.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Ugyldigt datoformat! Brug dd/MM/yyyy", "Fejl", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Task newTask = new Task(title, priority, category, deadline);
        tasks.add(newTask);
        updateTaskList();

        taskField.setText("");
        deadlineField.setText("");
        priorityCombo.setSelectedItem(Task.Priority.MEDIUM);
        updateStatsLabel();
    }

    private void toggleTaskCompleted() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            Task task = listModel.getElementAt(selectedIndex);
            task.setCompleted(!task.isCompleted());
            listModel.setElementAt(task, selectedIndex);
            updateStatsLabel();
        } else {
            JOptionPane.showMessageDialog(this, "Venligst vælg en opgave!", "Advarsel", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "Venligst vælg en opgave!", "Advarsel", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Task task = listModel.getElementAt(selectedIndex);
        
        JPanel editPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        JLabel titleLabel = new JLabel("Titel:");
        JTextField titleField = new JTextField(task.getTitle());
        
        JLabel priorityLabel = new JLabel("Prioritet:");
        JComboBox<Task.Priority> priorityBox = new JComboBox<>(Task.Priority.values());
        priorityBox.setSelectedItem(task.getPriority());
        
        JLabel categoryLabel = new JLabel("Kategori:");
        JComboBox<String> categoryBox = new JComboBox<>();
        for (String cat : categories) {
            categoryBox.addItem(cat);
        }
        categoryBox.setSelectedItem(task.getCategory());
        
        JLabel deadlineLabel = new JLabel("Deadline (dd/MM/yyyy):");
        JTextField deadlineBoxField = new JTextField(task.getDeadline() != null ? task.getDeadline().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        
        editPanel.add(titleLabel);
        editPanel.add(titleField);
        editPanel.add(priorityLabel);
        editPanel.add(priorityBox);
        editPanel.add(categoryLabel);
        editPanel.add(categoryBox);
        editPanel.add(deadlineLabel);
        editPanel.add(deadlineBoxField);
        
        int result = JOptionPane.showConfirmDialog(this, editPanel, "Rediger opgave", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            task.setTitle(titleField.getText().trim());
            task.setPriority((Task.Priority) priorityBox.getSelectedItem());
            task.setCategory((String) categoryBox.getSelectedItem());
            
            if (!deadlineBoxField.getText().trim().isEmpty()) {
                try {
                    task.setDeadline(LocalDate.parse(deadlineBoxField.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Ugyldigt datoformat!", "Fejl", JOptionPane.ERROR_MESSAGE);
                }
            }
            
            listModel.setElementAt(task, selectedIndex);
            updateStatsLabel();
        }
    }

    private void deleteTask() {
        int selectedIndex = taskList.getSelectedIndex();
        if (selectedIndex != -1) {
            tasks.remove(selectedIndex);
            updateTaskList();
            updateStatsLabel();
        } else {
            JOptionPane.showMessageDialog(this, "Venligst vælg en opgave!", "Advarsel", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearCompletedTasks() {
        int response = JOptionPane.showConfirmDialog(this, "Slet alle fuldførte opgaver?", "Bekræft", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            tasks = tasks.stream()
                    .filter(t -> !t.isCompleted())
                    .collect(Collectors.toList());
            updateTaskList();
            updateStatsLabel();
        }
    }

    private void clearAllTasks() {
        int response = JOptionPane.showConfirmDialog(this, "Slet alle opgaver?", "Bekræft", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            tasks.clear();
            updateTaskList();
            updateStatsLabel();
        }
    }

    private void updateTaskList() {
        listModel.clear();
        String selectedFilter = (String) categoryFilterCombo.getSelectedItem();

        for (Task task : tasks) {
            if (selectedFilter.equals("Alle") || task.getCategory().equals(selectedFilter)) {
                listModel.addElement(task);
            }
        }
    }

    private void updateStatsLabel() {
        long total = tasks.size();
        long completed = tasks.stream().filter(Task::isCompleted).count();
        long notCompleted = total - completed;

        statsLabel.setText(String.format("Statistik: %d total | %d fuldført | %d ikke-fuldført", total, completed, notCompleted));
    }

    // Custom ListCellRenderer for styling completed tasks
    private class TaskListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof Task) {
                Task task = (Task) value;
                setText(task.toString());
                
                if (task.isCompleted()) {
                    c.setForeground(new Color(100, 100, 100));
                    setFont(new Font("Arial", Font.ITALIC, 12));
                } else {
                    c.setForeground(Color.BLACK);
                    setFont(new Font("Arial", Font.PLAIN, 12));
                }
            }
            
            return c;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TodoApp());
    }
}
