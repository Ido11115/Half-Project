package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The SearchBookController class handles the functionality for searching books in the library system.
 * It allows users to search for books, view search results, and optionally reserve unavailable books.
 */
public class SearchBookController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Book> bookTable;

    @FXML
    private TableColumn<Book, String> nameColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, Integer> availableCopiesColumn;

    @FXML
    private TableColumn<Book, String> locationColumn;

    @FXML
    private Label errorLabel;

    private ServerCommunicator serverCommunicator;

    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    /**
     * Sets the ServerCommunicator for this controller.
     *
     * @param serverCommunicator the ServerCommunicator used for server communication
     */
    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
    }

    /**
     * Initializes the controller by binding the TableView columns to the properties of the Book class
     * and setting the table's items to the observable book list.
     */
    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        availableCopiesColumn.setCellValueFactory(cellData -> cellData.getValue().availableCopiesProperty().asObject());
        locationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());

        bookTable.setItems(bookList);
    }

    /**
     * Handles the search functionality by sending a search request to the server and updating
     * the TableView with the results. Displays appropriate messages if no results are found
     * or if there is an error communicating with the server.
     */
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            errorLabel.setText("Please enter a search query.");
            return;
        }

        try {
            String response = serverCommunicator.sendRequest("SEARCH_BOOK," + searchText);
            System.out.println("Server Response: " + response); // Debug log
            List<Book> books = parseBooks(response);

            if (books.isEmpty()) {
                errorLabel.setText("No books found matching the search criteria.");
                bookList.clear(); // Clear previous results
            } else {
                errorLabel.setText(""); // Clear any previous error messages
                bookList.setAll(books); // Update the observable list
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error communicating with the server: " + e.getMessage());
        }
    }

    /**
     * Handles the reserve functionality by sending a reserve request to the server for the selected book.
     * Displays appropriate messages if no book is selected or if the book is already available.
     */
    @FXML
    private void handleReserve() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            errorLabel.setText("Please select a book to reserve.");
            return;
        }

        System.out.println("Selected book ID: " + selectedBook.getId()); // Debug log
        if (selectedBook.getAvailableCopies() > 0) {
            errorLabel.setText("This book is available and does not need to be reserved.");
            return;
        }

        try {
            String response = serverCommunicator.sendRequest("RESERVE_BOOK," + selectedBook.getId());
            if ("Reservation successful".equals(response)) {
                errorLabel.setText("Book reserved successfully.");
            } else {
                errorLabel.setText(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error communicating with the server: " + e.getMessage());
        }
    }

    /**
     * Parses the raw data string received from the server into a list of Book objects.
     *
     * @param data the raw data string containing book details
     * @return a list of Book objects parsed from the data
     */
    private List<Book> parseBooks(String data) {
        List<Book> books = new ArrayList<>();
        try {
            String[] entries = data.split(";");
            for (String entry : entries) {
                String[] details = entry.split(",");
                if (details.length == 7) { // Ensure all fields are present
                    books.add(new Book(
                        details[1], // name
                        details[2], // author
                        Integer.parseInt(details[5]), // availableCopies
                        details[6], // location
                        Integer.parseInt(details[0]), // id
                        details[3], // subject
                        details[4]  // description
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error parsing book data.");
        }
        return books;
    }

}
