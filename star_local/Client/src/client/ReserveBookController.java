package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The ReserveBookController class manages the functionality for searching and reserving books.
 * It allows users to search for reservable books, view available options, and reserve selected books.
 */
public class ReserveBookController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Book> bookTable;

    @FXML
    private TableColumn<Book, String> nameColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, String> closestReturnDateColumn;

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
        closestReturnDateColumn.setCellValueFactory(cellData -> cellData.getValue().closestReturnDateProperty());

        bookTable.setItems(bookList);
    }

    /**
     * Handles the search functionality by sending a request to the server with the search text
     * and updating the TableView with the returned book data.
     */
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            errorLabel.setText("Please enter a book name or author.");
            return;
        }

        try {
            String response = serverCommunicator.sendRequest("SEARCH_RESERVABLE_BOOKS," + searchText);
            List<Book> books = parseBooks(response);

            if (books.isEmpty()) {
                errorLabel.setText("No books found matching the search criteria.");
            } else {
                errorLabel.setText("");
                bookList.setAll(books);
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error communicating with the server: " + e.getMessage());
        }
    }

    /**
     * Handles the reserve functionality by sending a reserve request to the server for the selected book.
     */
    @FXML
    private void handleReserve() {
        Book selectedBook = bookTable.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            errorLabel.setText("Please select a book to reserve.");
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
                if (details.length == 4) { // 4 fields for ReserveBook: name, author, closestReturnDate, id
                    books.add(new Book(details[0], details[1], details[2], Integer.parseInt(details[3])));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error parsing book data.");
        }
        return books;
    }

}
