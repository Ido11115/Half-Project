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
    @FXML
    private TableView<Book> booksTable;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> subjectColumn;
    @FXML
    private TableColumn<Book, String> locationColumn;
    @FXML
    private TableColumn<Book, Integer> availableCopiesColumn;
    @FXML
    private TableColumn<Book, String> descriptionColumn;
    
    @FXML
    private TextField nameField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField copiesField;
    @FXML
    private TextArea descriptionField;

    private ObservableList<Book> booksList = FXCollections.observableArrayList();
    private ServerCommunicator serverCommunicator;

    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
        loadBooksData();
    }

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


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nameField.clear();
        authorField.clear();
        subjectField.clear();
        locationField.clear();
        copiesField.clear();
        descriptionField.clear();
    }
}
