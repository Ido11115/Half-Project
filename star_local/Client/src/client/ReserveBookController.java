package client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public void setServerCommunicator(ServerCommunicator serverCommunicator) {
        this.serverCommunicator = serverCommunicator;
    }

    @FXML
    private void initialize() {
        // Bind columns to Book properties
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        authorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());
        closestReturnDateColumn.setCellValueFactory(cellData -> cellData.getValue().closestReturnDateProperty());

        // Set the table items
        bookTable.setItems(bookList);
    }

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
