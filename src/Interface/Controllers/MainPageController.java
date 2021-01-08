package Interface.Controllers;

import Interface.Config.Config;
import Model.Book;
import Model.Client;
import Model.Rent;
import Service.DAO.EndpointAccessor;
import Service.Library;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;
/**
 * Controller for GUI application of Library
 */
public class MainPageController implements Initializable {
    private Library library;
    private String currentlySelectedBookID;
    private String currentlySelectedClientID;
    private String currentlySelectedRentID;
    @FXML
    private ListView books;
    @FXML
    private ListView clients;
    @FXML
    private ListView activeRents;
    @FXML
    private ListView archiveRents;
    @FXML
    private ListView futureReturns;
    @FXML
    private ListView dateReturns;
    @FXML
    private ListView pastReturns;

    ObservableList<String> booksList = FXCollections.observableArrayList();
    ObservableList<String> clientsList = FXCollections.observableArrayList();
    ObservableList<String> activeRentsList = FXCollections.observableArrayList();
    ObservableList<String> archiveRentsList = FXCollections.observableArrayList();
    ObservableList<String> futureRentsList = FXCollections.observableArrayList();
    ObservableList<String> dateReturnRentsList = FXCollections.observableArrayList();
    ObservableList<String> pastReturnDate = FXCollections.observableArrayList();

    @FXML
    private Button closeButton;

    @FXML
    private Button addBook;

    @FXML
    private Button archiveBook;

    @FXML
    private Button addClient;

    @FXML
    private Button removeClient;

    @FXML
    private Button addRent;

    @FXML
    private Button returnRent;

    @FXML
    private DatePicker dateRentPicker;

    public MainPageController() throws IOException {
    }

    /**
     * Fired at the start of the program, Fills all the ListViews with Data and shows the reminder about today's rent returns.
     * Also it sets up action listeners like clicking certain button, selecting a row in list view, which sets buttons availability (or disability)
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            showLogInPopUp();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            library = new Library();
        } catch (IOException e) {
            e.printStackTrace();
        }
        addRent.setDisable(true);

        for (var book : library.getBooksOnStock()) {
            booksList.add(book.toString());
        }
        for (var client : library.getRegisteredClients()) {
            clientsList.add(client.toString());
        }
        activeRentsList.addAll(library.generateDescriptionsForGivenRents(library.getActiveRents()));
        archiveRentsList.addAll(library.generateDescriptionsForGivenRents(library.getArchiveRents()));
        try {
            futureRentsList.addAll(library.rentsShortdescriptions(library.getFutureRents()));
            pastReturnDate.addAll(library.rentsShortdescriptions(library.getPastReturnDates()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        books.setItems(booksList);
        clients.setItems(clientsList);
        activeRents.setItems(activeRentsList);
        archiveRents.setItems(archiveRentsList);
        futureReturns.setItems(futureRentsList);
        dateReturns.setItems(dateReturnRentsList);
        pastReturns.setItems(pastReturnDate);


        books.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentlySelectedBookID = newValue.substring(0, newValue.indexOf("."));
                addRent.setDisable(currentlySelectedBookID == null
                        || currentlySelectedClientID == null
                        || library.getBookByIndex(currentlySelectedBookID).getRented() == 1
                        || library.getBookByIndex(currentlySelectedBookID).getRented() == 2);
                archiveBook.setDisable(library.getBookByIndex(currentlySelectedBookID).getRented() == 1
                        || library.getBookByIndex(currentlySelectedBookID).getRented() == 2);
            }
        });
        clients.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentlySelectedClientID = newValue.substring(0, newValue.indexOf("."));
                addRent.setDisable(currentlySelectedBookID == null
                        || library.getBookByIndex(currentlySelectedBookID).getRented() == 1
                        || library.getBookByIndex(currentlySelectedBookID).getRented() == 2
                        || currentlySelectedClientID == null);
            }
        });
        activeRents.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentlySelectedRentID = newValue.substring(0, newValue.indexOf("."));
                returnRent.setDisable(library.getRentByIndex(currentlySelectedRentID).getReturnDate() != null);
            }
        });
        archiveRents.getSelectionModel().selectedItemProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentlySelectedRentID = newValue.substring(0, newValue.indexOf("."));
                returnRent.setDisable(library.getRentByIndex(currentlySelectedRentID).getReturnDate() != null);
            }
        });
        dateRentPicker.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (newValue != null) {
                dateReturnRentsList.clear();
                try {
                    dateReturnRentsList.addAll(library.rentsClientBookDescriptions(library.getRentsByDate(Date.valueOf(newValue))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        try {
            showReminderPopup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Shows information alert with amount of book returns for current day.
     */
    private void showLogInPopUp() throws IOException {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Logowanie");
        dialog.setHeaderText("Logowanie");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField user = new TextField();
        user.setPromptText("Użytkownik");
        PasswordField password = new PasswordField();
        password.setPromptText("Hasło");

        grid.add(new Label("Użytkownik :"), 0, 0);
        grid.add(user, 1, 0);
        grid.add(new Label("Hasło :"), 0, 1);
        grid.add(password, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                if (EndpointAccessor.Authenticate(user.getText(), password.getText())) {}
                else {
                    wrongLoginData();
                    System.exit(0);
                }

                return null;
            }
            return null;
        });
        dialog.showAndWait();
    }
    /**
     * Shows information alert with amount of book returns for current day.
     */
    private void showReminderPopup() throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dzisiejsze przewidywania");
        var numberOfRents = library.getNumberOfreturnRentsForToday();
        switch (numberOfRents) {
            case 1:
                alert.setHeaderText("Dzisiaj powinien odbyć się " + library.getNumberOfreturnRentsForToday() + " zwrot");
                break;
            case 2:
            case 3:
            case 4:
                alert.setHeaderText("Dzisiaj powinny odbyć się " + library.getNumberOfreturnRentsForToday() + " zwroty");
                break;
            default:
                alert.setHeaderText("Dzisiaj powinno odbyć się " + library.getNumberOfreturnRentsForToday() + " zwrotów");
                break;
        }
        ButtonType btnOK = new ButtonType("OK");
        alert.getButtonTypes().setAll(btnOK);
        alert.showAndWait();
    }

    /**
     * Shows "About" information popup with info about used components and authors of this application
     */
    public void showAboutPopup() throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Wszyscy użytkownicy");
        alert.setHeaderText(EndpointAccessor.executeAPICallGet("/Users").body().string());
        ButtonType btnOK = new ButtonType("OK");
        alert.getButtonTypes().setAll(btnOK);
        alert.showAndWait();
    }
    public void wrongLoginData() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Zły login i/lub hasło!");
        alert.setHeaderText("Zły login i hasło! \n Program się zakończy.");
        ButtonType btnOK = new ButtonType("OK");
        alert.getButtonTypes().setAll(btnOK);
        alert.showAndWait();
    }
    /**
     * Export of all data ( rents , books, clients ) to xml file
     */
    public void exportToXML() {
        library.exportLibraryDataToXML();
    }

    /**
     * Action after clicking addButton on Main GUI screen. It's made of popup screen after which new book is added to both local library and database.
     * It does the data validation, and after action which adds book to both GUI and Library service.
     * @param event
     */
    public void addBook(ActionEvent event) throws IOException {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Dodawanie książki");
        dialog.setHeaderText("Nowa książka : ");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField pages = new TextField();
        pages.setPromptText("xxx");
        TextField width = new TextField();
        width.setPromptText("xx.xx");
        TextField height = new TextField();
        height.setPromptText("xx.xx");
        TextField price = new TextField();
        price.setPromptText("xx.xx");
        TextField title = new TextField();
        title.setPromptText("Tytuł");
        TextField author = new TextField();
        author.setPromptText("Autor");

        grid.add(new Label("Ilość stron :"), 0, 0);
        grid.add(pages, 1, 0);
        grid.add(new Label("Szerokość :"), 0, 1);
        grid.add(width, 1, 1);
        grid.add(new Label("Wysokość :"), 0, 2);
        grid.add(height, 1, 2);
        grid.add(new Label("Cena :"), 0, 3);
        grid.add(price, 1, 3);
        grid.add(new Label("Tytuł :"), 0, 4);
        grid.add(title, 1, 4);
        grid.add(new Label("Autor :"), 0, 5);
        grid.add(author, 1, 5);

        dialog.getDialogPane().setContent(grid);

        final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(
                ActionEvent.ACTION,
                event1 -> {
                    StringJoiner validationResult = new StringJoiner("\n");
                    try {
                        Integer.valueOf(pages.getText());
                    } catch (NullPointerException e) {
                        validationResult.add("Ilość stron nie może być pusta !!");
                    } catch (NumberFormatException e) {
                        validationResult.add("W polu liczba strona proszę wpisać liczbę całkowitą");
                    } catch (Exception e) {
                        validationResult.add("Proszę sprawdź wartość w polu liczba stron" );
                    }

                    try {
                        Double.valueOf(width.getText());
                    } catch (NullPointerException e) {
                        validationResult.add("Szerokość nie może być pusta !!");
                    } catch (NumberFormatException e) {
                        validationResult.add("W polu szerokość proszę wpisać liczbę w formacie 00.00");
                    } catch (Exception e) {
                        validationResult.add("Proszę sprawdź wartość w polu szerokość");
                    }

                    try {
                        Double.valueOf(height.getText());
                    } catch (NullPointerException e) {
                        validationResult.add("Wysokość nie może być pusta !!");
                    } catch (NumberFormatException e) {
                        validationResult.add("W polu wysokość proszę wpisać liczbę w formacie 00.00");
                    } catch (Exception e) {
                        validationResult.add("Proszę sprawdź wartość w polu wysokość");
                    }

                    try {
                        Double.valueOf(price.getText());
                    } catch (NullPointerException e) {
                        validationResult.add("Cena nie może być pusta !!");
                    } catch (NumberFormatException e) {
                        validationResult.add("W polu cena proszę wpisać liczbę w formacie 00.00");
                    } catch (Exception e) {
                        validationResult.add("Proszę sprawdź wartość w polu cena");
                    }

                    if (title.getText() == null || title.getText().equals("")) {
                        validationResult.add("Tytuł nie może być pusty !!");
                    }

                    if (author.getText() == null || author.getText().equals("")) {
                        validationResult.add("Autor nie może być pusty !!");
                    }
                    if (validationResult.toString() != "") {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Walidacja książki ! ");
                        alert.setHeaderText(validationResult.toString());
                        ButtonType btnOK = new ButtonType("OK");
                        alert.getButtonTypes().setAll(btnOK);
                        alert.showAndWait();
                        validationResult = null;
                        event1.consume();
                    }
                }
        );


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Book(0,
                        Integer.valueOf(pages.getText())
                        , Double.valueOf(width.getText())
                        , Double.valueOf(height.getText())
                        , Double.valueOf(price.getText())
                        , title.getText()
                        , author.getText()
                        , 0);
            }
            return null;
        });

        Optional<Book> result = dialog.showAndWait();

        result.ifPresent(book -> {
            try {
                library.addBookToLibrary(book);
            } catch (IOException e) {
                e.printStackTrace();
            }
            booksList.add(book.toString());
        });
    }

    /**
     * After clicking archiveBook button, currently selected book is archived in local library and the database, its status is changed to Archived
     * Library synchronized this archiving with DB
     * @param event
     */
    public void archiveBook(ActionEvent event) {
        var selectedBookID = Integer.valueOf(books.getSelectionModel().getSelectedItem().toString().substring(0, books.getSelectionModel().getSelectedItem().toString().indexOf(".")));
        library.archiveBook(selectedBookID);
        booksList.remove(books.getSelectionModel().getSelectedItem());
        booksList.add(library.getBookByIndex(String.valueOf(selectedBookID)).toString());
    }


    /**
     * Action after clicking addButton on Main GUI screen. It's made of popup screen after which new book is added to both GUI and Library Service.
     * It also does validation of new Client
     * @param event
     */

    public void addClient(ActionEvent event) throws IOException {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Dodawanie klienta");
        dialog.setHeaderText("Nowy klient");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField firstName = new TextField();
        firstName.setPromptText("Imię");
        TextField lastName = new TextField();
        lastName.setPromptText("Nazwisko");
        TextField email = new TextField();
        email.setPromptText("xys@xys");
        TextField phone = new TextField();
        phone.setPromptText("xyzxyzxyz");

        grid.add(new Label("Imię :"), 0, 0);
        grid.add(firstName, 1, 0);
        grid.add(new Label("Nazwisko :"), 0, 1);
        grid.add(lastName, 1, 1);
        grid.add(new Label("Adres Email :"), 0, 2);
        grid.add(email, 1, 2);
        grid.add(new Label("Numer telefonu :"), 0, 3);
        grid.add(phone, 1, 3);

        dialog.getDialogPane().setContent(grid);

        final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(
                ActionEvent.ACTION,
                event1 -> {
                    StringJoiner validationResult = new StringJoiner("\n");

                    if (firstName.getText() == null || firstName.getText().equals("")) {
                        validationResult.add("Imie nie może być puste !!");
                    }

                    if (lastName.getText() == null || lastName.getText().equals("")) {
                        validationResult.add("Nazwisko nie może być puste !!");
                    }

                    if (email.getText() == null || email.getText().equals("")) {
                        validationResult.add("Adres E-mail nie może być pusty !!");
                    } else {
                        if (!email.getText().contains("@")) {
                            validationResult.add("Email nie jest w poprawnym formacie : xyz@xyz");
                        }
                    }

                    if (phone.getText() == null || phone.getText().equals("")) {
                        validationResult.add("Numer telefonu nie może być pusty !!");
                    } else {
                        if (phone.getText().length() != 9) {
                            validationResult.add("Numer telefonu powinien posiadać 9 znaków!");
                        }
                    }

                    if (validationResult.toString() != "") {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Walidacja klienta ! ");
                        alert.setHeaderText(validationResult.toString());
                        ButtonType btnOK = new ButtonType("OK");
                        alert.getButtonTypes().setAll(btnOK);
                        alert.showAndWait();
                        validationResult = null;
                        event1.consume();
                    }
                }
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Client(0,
                        firstName.getText()
                        , lastName.getText()
                        , email.getText()
                        , phone.getText());
            }
            return null;
        });

        Optional<Client> result = dialog.showAndWait();

        result.ifPresent(client -> {
            try {
                library.registerClient(client);
            } catch (IOException e) {
                e.printStackTrace();
            }
            clientsList.add(client.toString());
        });
    }

    /**
     * Managed an rent creation. On clicking addRent ( Wypożyczenie ) button on Main Screen it opens dialog box,
     * validates the data and updates all of the GUI List Views and Library Service Data
     * @param event
     */
    public void addRent(ActionEvent event) throws IOException {
        Dialog<Rent> dialog = new Dialog<>();
        dialog.setTitle("Tworzenie wypożyczenia");
        dialog.setHeaderText("Nowe wypożyczenie");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker rentDate = new DatePicker();
        rentDate.setPromptText("yyyy-mm-dd");
        DatePicker agreedReturnDate = new DatePicker();
        agreedReturnDate.setPromptText("yyyy-mm-dd");

        grid.add(new Label("Data wypożyczenia :"), 0, 0);
        grid.add(rentDate, 1, 0);
        grid.add(new Label("Umówiona data zwrotu :"), 0, 1);
        grid.add(agreedReturnDate, 1, 1);


        dialog.getDialogPane().setContent(grid);

        final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(
                ActionEvent.ACTION,
                event1 -> {
                    StringJoiner validationResult = new StringJoiner("\n");

                    try {
                        Date.valueOf(rentDate.getValue());
                    } catch (NullPointerException e) {
                        validationResult.add("Data wypożyczenia nie może być pusta.");
                    } catch (Exception ex) {
                        validationResult.add("Błąd w dacie wypożyczenia");
                    }

                    try {
                        Date.valueOf(agreedReturnDate.getValue());
                    } catch (NullPointerException e) {
                        validationResult.add("Ustalona data zwrotu nie może być pusta.");
                    } catch (Exception ex) {
                        validationResult.add("Błąd w ustalonej dacie zwrotu");
                    }

                    if (validationResult.toString() != "") {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Walidacja wypożyczenia ! ");
                        alert.setHeaderText(validationResult.toString());
                        ButtonType btnOK = new ButtonType("OK");
                        alert.getButtonTypes().setAll(btnOK);
                        alert.showAndWait();
                        validationResult = null;
                        event1.consume();
                    }
                }
        );

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new Rent(0,
                        Integer.valueOf(currentlySelectedClientID),
                        Integer.valueOf(currentlySelectedBookID),
                        Date.valueOf(rentDate.getValue()),
                        Date.valueOf(agreedReturnDate.getValue()));
            }
            return null;
        });

        Optional<Rent> result = dialog.showAndWait();

        result.ifPresent(rent -> {
            try {
                library.addRent(rent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            activeRentsList.add(library.generateDescriptionForSingleRent(rent));

            var book = library.getBookByIndex(String.valueOf(rent.getbId()));
            for (var bookFromBookList : booksList) {
                if (Integer.valueOf(bookFromBookList.substring(0, bookFromBookList.indexOf("."))) == book.getBookId()) {
                    booksList.remove(bookFromBookList);
                    booksList.add(book.toString());
                    break;
                }
            }
            futureRentsList.clear();
            try {
                for (var futureRent : library.getFutureRents()) {
                    futureRentsList.add(library.generateRentShortDescription(futureRent));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            pastReturnDate.clear();
            try {
                for (var pastRent : library.getPastReturnDates()) {
                    pastReturnDate.add(library.generateRentShortDescription(pastRent));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    /**
     * Exits the app, closes connection with database
     *
     * @param event
     */
    public void exit(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Manages returning an rent, Updates book status, GUI list views, library service data
     * @param actionEvent
     */
    public void returnRent(ActionEvent actionEvent) throws IOException {
        library.handleBookReturning(currentlySelectedRentID);
        var rent = library.getRentByIndex(currentlySelectedRentID);
        var book = library.getBookByIndex(String.valueOf(rent.getbId()));
        for (var bookFromBookList : booksList) {
            if (Integer.valueOf(bookFromBookList.substring(0, bookFromBookList.indexOf("."))) == book.getBookId()) {
                booksList.remove(bookFromBookList);
                booksList.add(book.toString());
                break;
            }
        }
        activeRentsList.remove(activeRents.getSelectionModel().getSelectedItem());
        archiveRentsList.add(library.generateDescriptionForSingleRent(rent));
        futureRentsList.clear();
        for (var futureRent : library.getFutureRents()) {
            futureRentsList.add(library.generateRentShortDescription(rent));
        }
        pastReturnDate.clear();
        for (var pastRent : library.getPastReturnDates()) {
            pastReturnDate.add(library.generateRentShortDescription(pastRent));
        }
    }

    /**
     * Management of book filtering, Dialog box with Author And Book title to do search on. Later on updates Library service Data and GUI
     * @param actionEvent
     */
    public void filterBooks(ActionEvent actionEvent) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Filtrowanie Książek");
        dialog.setHeaderText("Filtrowanie Książek");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField title = new TextField();
        title.setPromptText("Tytuł");
        TextField author = new TextField();
        author.setPromptText("Autor");

        grid.add(new Label("Tytuł :"), 0, 0);
        grid.add(title, 1, 0);
        grid.add(new Label("Autor :"), 0, 1);
        grid.add(author, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                booksList.clear();
                ArrayList<Book> tempBooks = null;
                try {
                    tempBooks = library.setAndReturnBooksWithFilter(title.getText(),author.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (var book : tempBooks) {
                    booksList.add(book.toString());
                }
                return null;
            }
            return null;
        });
        dialog.show();
    }

    /**
     * Opens Dialog box (Popup) to enable filtering through Clients LastName and Book Title. Updates ListViews
     * @param actionEvent
     */
    public void filterRents(ActionEvent actionEvent) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Filtrowanie Wypożyczeń");
        dialog.setHeaderText("Filtrowanie Wypożyczeń");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField title = new TextField();
        title.setPromptText("Tytuł Książki");

        TextField author = new TextField();
        author.setPromptText("Wypożyczający");

        grid.add(new Label("Tytuł Książki :"), 0, 0);
        grid.add(title, 1, 0);
        grid.add(new Label("Wypożyczający :"), 0, 1);
        grid.add(author, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                activeRentsList.clear();
                archiveRentsList.clear();
                try {
                    activeRentsList.addAll(library.generateDescriptionsForGivenRents(library.setAndReturnActiveRentsWithFilter(title.getText(),author.getText())));
                    archiveRentsList.addAll(library.generateDescriptionsForGivenRents(library.setAndReturnArchiveRentsWithFilter(title.getText(),author.getText())));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
            return null;
        });
        dialog.show();
    }

    /**
     * Dialog box with file picker to be able to select a new Icon. Instantly change programs icon and updates xml data.
     */
    public void pickIcon() {
        List<Window> open = Stage.getWindows().stream().filter(Window::isShowing).collect(Collectors.toList());
        var current = (Stage) open.get(0);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("C:\\Users\\Tomek\\Desktop\\Komponent6_7\\resources\\icons"));
        fileChooser.setTitle("Wybór ikony");
        var file = fileChooser.showOpenDialog(current);
        if (file != null) {
            try {
                var config = Config.getInstance();
                config.setIconPath(file.getPath());
                current.getIcons().remove(0);
                current.getIcons().add(new Image(file.toURI().toURL().toExternalForm()));
            } catch (MalformedURLException e) {
                System.out.println("Zniekształcony URl podczas zmiany ikony");
            }
        }
    }
}
