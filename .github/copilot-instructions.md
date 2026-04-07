# Copilot Instructions - Todo App

## Project Overview

- **Language**: Java 11
- **Build Tool**: Maven
- **Framework**: Swing (Java built-in GUI framework)
- **Purpose**: Simple todo list application with Swing UI

## Development Guidelines

### Code Style

- Follow Java naming conventions (camelCase for methods/variables, PascalCase for classes)
- Use proper indentation (4 spaces)
- Add meaningful comments for complex logic
- Keep methods focused and concise

### Project Structure

- Main application class: `src/main/java/com/todoapp/TodoApp.java`
- Test files: `src/test/java/com/todoapp/`
- Build configuration: `pom.xml`

### Building & Running

- **Compile**: `mvn clean compile`
- **Run**: `mvn exec:java -Dexec.mainClass="com.todoapp.TodoApp"`
- **Package**: `mvn clean package`
- **Run JAR**: `java -jar target/todo-app-1.0.0.jar`

### Swing UI Development

- All UI components should be created in the `initializeUI()` method
- Use `SwingUtilities.invokeLater()` to ensure thread-safe UI updates
- Follow Swing best practices for event handling

### Common Tasks

- Adding new features: Create new methods in `TodoApp.java` or separate UI panel classes
- Adding dependencies: Update `pom.xml` with new `<dependency>` entries
- Running tests: `mvn test`

### GitHub Integration

- Repository: https://github.com/oumar969/small-java-projket-todo-.git
- Commit changes regularly with meaningful messages
- Follow conventional commit messages (feat:, fix:, docs:, etc.)
