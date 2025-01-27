/**
 * Controller class for managing library books in the application.
 * Handles the display, addition, and deletion of books in the library.
 */
package client;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LibraryBooksController {

    /**
     * Table view for displaying the list of books.
     */
    @FXML
    private TableView<Book> booksTable;

    /**
     * Table column for displaying book titles.
     */
    @FXML
    private TableColumn<Book, String> titleColumn;

    /**
     * Table column for displaying book authors.
     */
    @FXML
    private TableColumn<Book, String> authorColumn;

    /**
     * Table column for displaying book subjects.
     */
    @FXML
    private TableColumn<Book, String> subjectColumn;

    /**
     * Table column for displaying book locations.
     */
    @FXML
    private TableColumn<Book, String> locationColumn;

    /**
     * Table column for displaying the number of available copies of books.
     */
    @FXML
    private TableColumn<Book, Integer> availableCopiesColumn;

    /**
     * Table column for displaying book descriptions.
     */
    @FXML
    private TableColumn<Book, String> descriptionColumn;

    /**
     * Text field for entering the book's name.
     */
    @FXML
    private TextField nameField;

    /**
     * Text field for entering the book's author.
     */
    @FXML
    private TextField authorField;

    /**
     * Text field for entering the book's subject.
     */
    @FXML
    private TextField subjectField;

    /**
     * Text field for entering the book's location.
     */
    @FXML
    private TextField locationField;

    /**
     * Text field for entering the number of available copies of the book.
     */
    @FXML
    private TextField copiesField;

    /**
     * Text area for entering the book's description.
     */
    @FXML
    private TextArea descriptionField;

    /**
     * Observable list of books for binding to the table view.
     */
    private ObservableList<Book> booksList = FXCollections.observableArrayList();

    /**
     * Object for communicating with the server.
     */
    private ServerCommunicator serverCommunicator;

    /**
     * Sets the server communicator and loads books data from the server.
     *
     * @param serverCommunicator The server communicator to be set.
     */
    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
        loadBooksData();
    }

    /**
     * Initializes the controller by setting up table columns and binding data.
     */
    @FXML
    private void initialize() {
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        subjectColumn.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
        locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        availableCopiesColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().availableCopiesProperty().get()));
        descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        booksTable.setItems(booksList); // Bind the books list to the table view
    }

    /**
     * Loads the list of books from the server and populates the table view.
     */
    @FXML
    private void loadBooksData() {
        System.out.println("Loading books data...");
        try {
            String response = serverCommunicator.sendRequest("GET_BOOKS");
            if (response.equals("No books found.")) {
                showError("No books found in the database.");
                return;
            }

            List<Book> books = parseBooks(response);
            booksList.setAll(books);
            System.out.println("Books loaded: " + booksList.size());
            booksTable.setItems(booksList);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading books data: " + e.getMessage());
        }
    }

    /**
     * Handles the addition of a new book to the library.
     * Validates user input, sends a request to the server, and updates the table view.
     */
    @FXML
    private void handleAddBook() {
        String name = nameField.getText().trim();
        String author = authorField.getText().trim();
        String subject = subjectField.getText().trim();
        String location = locationField.getText().trim();
        String description = descriptionField.getText().trim();
        int availableCopies;

        if (name.isEmpty() || author.isEmpty() || location.isEmpty() || copiesField.getText().isEmpty()) {
            showError("All required fields must be filled.");
            return;
        }

        try {
            availableCopies = Integer.parseInt(copiesField.getText().trim());
            if (availableCopies < 0) {
                showError("Available copies cannot be negative.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Available copies must be a valid number.");
            return;
        }

        try {
            String command = String.format("ADD_BOOK,%s,%s,%s,%d,%s,%s", name, author, subject, availableCopies, location, description);
            String response = serverCommunicator.sendRequest(command);
            if ("Book added successfully.".equals(response)) {
                loadBooksData();
                clearFields();
                showInfo("Book added successfully.");
            } else {
                showError(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error adding book: " + e.getMessage());
        }
    }

    /**
     * Handles the deletion of a selected book from the library.
     *
     * @param event The mouse event triggered by selecting a book.
     */
    @FXML
    private void handleDeleteBook(MouseEvent event) {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showError("No book selected.");
            return;
        }

        try {
            String command = String.format("DELETE_BOOK,%s", selectedBook.getName());
            String response = serverCommunicator.sendRequest(command);
            if ("Book deleted successfully.".equals(response)) {
                booksList.remove(selectedBook);
                showInfo("Book deleted successfully.");
            } else {
                showError(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error deleting book: " + e.getMessage());
        }
    }

    /**
     * Parses the server response and converts it into a list of books.
     *
     * @param response The server response containing book data.
     * @return A list of books parsed from the response.
     */
    private List<Book> parseBooks(String response) {
        List<Book> books = new ArrayList<>();
        try {
            Arrays.stream(response.split(";")).forEach(entry -> {
                String[] details = entry.split(",");
                books.add(new Book(
                    details[1], // name
                    details[2], // author
                    Integer.parseInt(details[4]), // available copies
                    details[5], // location
                    Integer.parseInt(details[0]), // id
                    details[3], // subject
                    details[6], // description
                    "" // closest return date (not included in this data)
                ));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }

    /**
     * Displays an error message in an alert dialog.
     *
     * @param message The error message to be displayed.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an informational message in an alert dialog.
     *
     * @param message The informational message to be displayed.
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Clears the input fields in the form.
     */
    private void clearFields() {
        nameField.clear();
        authorField.clear();
        subjectField.clear();
        locationField.clear();
        copiesField.clear();
        descriptionField.clear();
    }
}
