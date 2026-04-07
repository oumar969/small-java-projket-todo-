# Todo App

A simple todo application built with Java Swing and Maven.

## Features

- Add new tasks
- Delete individual tasks
- Clear all tasks at once
- Simple and intuitive UI

## Requirements

- Java 11 or higher
- Maven 3.6 or higher

## Building

```bash
mvn clean package
```

## Running

```bash
mvn exec:java -Dexec.mainClass="com.todoapp.TodoApp"
```

Or after building:

```bash
java -jar target/todo-app-1.0.0.jar
```

## Project Structure

```
small-java-projket-todo/
├── src/
│   ├── main/java/com/todoapp/
│   │   └── TodoApp.java
│   └── test/java/com/todoapp/
├── pom.xml
├── .gitignore
└── README.md
```

## Usage

1. Enter a task in the input field
2. Click "Add" to add the task to the list
3. Select a task and click "Delete Selected" to remove it
4. Click "Clear All" to remove all tasks at once

## License

MIT
