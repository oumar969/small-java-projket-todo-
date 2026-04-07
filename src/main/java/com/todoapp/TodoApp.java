package com.todoapp;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
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
    private JTable taskTable;
    private TaskTableModel tableModel;
    private JComboBox<String> categoryFilterCombo;
    private JLabel statsLabel;
    private List<Task> tasks;
    private List<String> categories;

    public TodoApp() {
        setTitle("Todo App - Enhanced");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 750);
        setLocationRelativeTo(null);
        setResizable(true);

        tasks = new ArrayList<>();
        categories = new ArrayList<>(Arrays.asList("Generelt", "Arbeid", "Personligt", "Shopping"));
        initializeUI();

        setVisible(true);
    }

    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel inputPanel = createInputPanel();
        JPanel filterPanel = createFilterPanel();
        JPanel listPanel = createListPanel();
        JPanel buttonPanel = createButtonPanel();

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(listPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Tilfoej ny opgave"));

        JLabel taskLabel = new JLabel("Opgave:");
        taskField = new JTextField();
        taskField.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel priorityLabel = new JLabel("Prioritet:");
        priorityCombo = new JComboBox<>(Task.Priority.values());
        priorityCombo.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel categoryLabel = new JLabel("Kategori:");
        categoryCombo = new JComboBox<>();
        for (String cat : categories) {
            categoryCombo.addItem(cat);
        }
        categoryCombo.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel deadlineLabel = new JLabel("Deadline (dd/MM/yyyy):");
        deadlineField = new JTextField();
        deadlineField.setFont(new Font("Arial", Font.PLAIN, 12));

        addButton = new JButton("Add opgave");
        addButton.setFont(new Font("Arial", Font.BOLD, 12));
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Filtre"));

        JLabel filterLabel = new JLabel("Filtrer efter kategori:");
        categoryFilterCombo = new JComboBox<>();
        categoryFilterCombo.addItem("Alle");
        for (String cat : categories) {
            categoryFilterCombo.addItem(cat);
        }
        categoryFilterCombo.addActionListener(e -> updateTaskTable());

        panel.add(filterLabel);
        panel.add(categoryFilterCombo);

        return panel;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Opgaver"));

        tableModel = new TaskTableModel(tasks);
        taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.setRowHeight(30);
        taskTable.setFont(new Font("Arial", Font.PLAIN, 12));
        taskTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        taskTable.getTableHeader().setBackground(new Color(70, 130, 180));
        taskTable.getTableHeader().setForeground(Color.WHITE);

        taskTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        taskTable.setDefaultRenderer(Object.class, new TaskTableCellRenderer());

        JScrollPane scrollPane = new JScrollPane(taskTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Handlinger"));

        JButton markCompleteButton = new JButton("Mark som fuldfoert");
        markCompleteButton.setFont(new Font("Arial", Font.PLAIN, 11));
        markCompleteButton.addActionListener(e -> toggleTaskCompleted());

        JButton editButton = new JButton("Rediger");
        editButton.setFont(new Font("Arial", Font.PLAIN, 11));
        editButton.addActionListener(e -> editTask());

        JButton deleteButton = new JButton("Slet opgave");
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 11));
        deleteButton.addActionListener(e -> deleteTask());

        JButton clearCompletedButton = new JButton("Ryd fuldfoerte");
        clearCompletedButton.setFont(new Font("Arial", Font.PLAIN, 11));
        clearCompletedButton.addActionListener(e -> clearCompletedTasks());

        JButton clearAllButton = new JButton("Ryd alt");
        clearAllButton.setFont(new Font("Arial", Font.PLAIN, 11));
        clearAllButton.addActionListener(e -> clearAllTasks());

        statsLabel = new JLabel("Statistik: 0 total | 0 fuldfoert | 0 ikke-fuldfoert");
        statsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(markCompleteButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(clearCompletedButton);
        panel.add(clearAllButton);
        panel.add(statsLabel);

        return panel;
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
                JOptionPane.showMessageDialog(this, "Ugyldigt datoformat! Brug dd/MM/yyyy", "Fejl",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        Task newTask = new Task(title, priority, category, deadline);
        tasks.add(newTask);
        updateTaskTable();

        taskField.setText("");
        deadlineField.setText("");
        priorityCombo.setSelectedItem(Task.Priority.MEDIUM);
        updateStatsLabel();
    }

    private void toggleTaskCompleted() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
            Task task = tableModel.getTaskAt(selectedRow);
            task.setCompleted(!task.isCompleted());
            tableModel.fireTableRowsUpdated(selectedRow, selectedRow);
            updateStatsLabel();
        } else {
            JOptionPane.showMessageDialog(this, "Venligst vaelg en opgave!", "Advarsel", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Venligst vaelg en opgave!", "Advarsel", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Task task = tableModel.getTaskAt(selectedRow);

        JPanel editPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
        JTextField deadlineBoxField = new JTextField(
                task.getDeadline() != null ? task.getDeadline().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");

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
                    task.setDeadline(LocalDate.parse(deadlineBoxField.getText().trim(),
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Ugyldigt datoformat!", "Fejl", JOptionPane.ERROR_MESSAGE);
                }
            }

            tableModel.fireTableDataChanged();
            updateStatsLabel();
        }
    }

    private void deleteTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow != -1) {
            tasks.remove(selectedRow);
            updateTaskTable();
            updateStatsLabel();
        } else {
            JOptionPane.showMessageDialog(this, "Venligst vaelg en opgave!", "Advarsel", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void clearCompletedTasks() {
        int response = JOptionPane.showConfirmDialog(this, "Slet alle fuldforte opgaver?", "Bekraeft",
                JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            tasks = tasks.stream()
                    .filter(t -> !t.isCompleted())
                    .collect(Collectors.toList());
            updateTaskTable();
            updateStatsLabel();
        }
    }

    private void clearAllTasks() {
        int response = JOptionPane.showConfirmDialog(this, "Slet alle opgaver?", "Bekraeft", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            tasks.clear();
            updateTaskTable();
            updateStatsLabel();
        }
    }

    private void updateTaskTable() {
        List<Task> filteredTasks = new ArrayList<>();
        String selectedFilter = (String) categoryFilterCombo.getSelectedItem();

        for (Task task : tasks) {
            if (selectedFilter.equals("Alle") || task.getCategory().equals(selectedFilter)) {
                filteredTasks.add(task);
            }
        }

        tableModel.setTasks(filteredTasks);
        tableModel.fireTableDataChanged();
    }

    private void updateStatsLabel() {
        long total = tasks.size();
        long completed = tasks.stream().filter(Task::isCompleted).count();
        long notCompleted = total - completed;

        statsLabel.setText(String.format("Statistik: %d total | %d fuldfoert | %d ikke-fuldfoert", total, completed,
                notCompleted));
    }

    private class TaskTableModel extends AbstractTableModel {
        private List<Task> taskList;
        private final String[] columnNames = { "OK", "Titel", "Prioritet", "Kategori", "Deadline" };

        public TaskTableModel(List<Task> tasks) {
            this.taskList = new ArrayList<>(tasks);
        }

        public void setTasks(List<Task> tasks) {
            this.taskList = new ArrayList<>(tasks);
        }

        public Task getTaskAt(int row) {
            return taskList.get(row);
        }

        @Override
        public int getRowCount() {
            return taskList.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int row, int column) {
            Task task = taskList.get(row);
            switch (column) {
                case 0:
                    return task.isCompleted() ? "YES" : "NO";
                case 1:
                    return task.getTitle();
                case 2:
                    return task.getPriority().getDisplayName();
                case 3:
                    return task.getCategory();
                case 4:
                    return task.getDeadlineString();
                default:
                    return "";
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    private class TaskTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            Task task = tableModel.getTaskAt(row);

            if (isSelected) {
                c.setBackground(new Color(70, 130, 180));
                c.setForeground(Color.WHITE);
            } else {
                if (task.isCompleted()) {
                    c.setBackground(new Color(220, 220, 220));
                    c.setForeground(new Color(100, 100, 100));
                    setFont(new Font("Arial", Font.ITALIC, 12));
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                    setFont(new Font("Arial", Font.PLAIN, 12));
                }
            }

            setHorizontalAlignment(column == 0 ? CENTER : LEFT);
            return c;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TodoApp());
    }
}
