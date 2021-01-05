package Service;

import Model.Book;
import Model.Client;
import Model.Rent;
import Service.DAO.LibraryDataHandler;
import Service.DBUtils.CommonBookApiCalls;
import Service.DBUtils.CommonClientApiCalls;
import Service.DBUtils.CommonRentApiCalls;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class Library {
    private ArrayList<Book> booksOnStock = new ArrayList<>();
    private ArrayList<Client> registeredClients = new ArrayList<>();
    private ArrayList<Rent> activeRents = new ArrayList<>();
    private ArrayList<Rent> archiveRents = new ArrayList<>();

    /**
     * During instatntiation gets all the data from DB ( books, clients, rents )
     */
    public Library() throws IOException {
        this.booksOnStock.addAll(CommonBookApiCalls.getAllBooks());
        this.registeredClients.addAll(CommonClientApiCalls.getAllClients());
        this.activeRents.addAll(CommonRentApiCalls.getAllActiveRents());
        this.archiveRents.addAll(CommonRentApiCalls.getAllArchiveRents());
    }

    /**
     *
     * @return List of books in library
     */
    public ArrayList<Book> getBooksOnStock() {
        return booksOnStock;
    }

    /**
     *
     * @return List of clients in the Library
     */
    public ArrayList<Client> getRegisteredClients() {
        return registeredClients;
    }

    /**
     *
     * @return list of active rents
     */
    public ArrayList<Rent> getActiveRents() {
        return activeRents;
    }

    /**
     *
     * @return List of archive rents
     */
    public ArrayList<Rent> getArchiveRents() {
        return archiveRents;
    }

    /**
     * Calls commonQueries to retrieve rents for future and returns them
     *
     * @return list of future rents
     */
    public ArrayList<Rent> getFutureRents() throws IOException {
        var futureRents = new ArrayList<Rent>();
        futureRents.addAll(CommonRentApiCalls.get15RentsWithClosestAgreedReturnDate());
        return futureRents;
    }

    /**
     * Calls commonQueries to retrieve rents by date and returns them
     * @param date day for rents to be returned
     * @return list of rent for given date
     */
    public ArrayList<Rent> getRentsByDate(Date date) throws IOException {
        var rentByDate = new ArrayList<Rent>();
        rentByDate.addAll(CommonRentApiCalls.getAllRentsForGivenDate(date));
        return rentByDate;
    }

    /**
     * @return Calls commonQueries to retrieve rents that should be returned and returns them
     */
    public ArrayList<Rent> getPastReturnDates() throws IOException {
        var pastReturnDateRents = new ArrayList<Rent>();
        pastReturnDateRents.addAll(CommonRentApiCalls.getOverKeepedRents());
        return pastReturnDateRents;
    }

    /**
     * Calls commonQueries to retrieve last Rent ID and returns it
     *
     * @return number of rents for today
     */
    public int getNumberOfreturnRentsForToday() throws IOException {
        return CommonRentApiCalls.numberOfTodayReturns();
    }


    /**
     * Adds a book to library object and database table
     *
     * @param book to be added to DB and local liibrary service data
     */
    public void addBookToLibrary(Book book) {
        booksOnStock.add(book);
        CommonBookApiCalls.addNewBook(book.getPages(), book.getWidth(), book.getHeight(), book.getPrice(), book.getTitle(), book.getAuthor(), book.getRented());
    }

    /**
     * Archives book and keep is synchronized with database
     *
     * @param id of book to be archived
     */
    public void archiveBook(int id) {
        for (var position : booksOnStock) {
            if (position.getBookId() == id) {
                position.setRented(2);
            }
        }
        CommonBookApiCalls.changeStatusToArchived(id);
    }

    /**
     * Adds a client to library object and database table
     *
     * @param client to be registered
     */
    public void registerClient(Client client) throws IOException {
        client.setClientId(CommonClientApiCalls.addNewClient(client.getFirstName(), client.getLastName(), client.getEmailAddress(), client.getTelephoneNumber()));
        registeredClients.add(client);
    }

    /**
     * Validates a rent, adds it to its Object collections and DB
     * Adds a rent to library object and database table
     *
     * @param rent to be created
     */
    public void addRent(Rent rent) throws IOException {
        var bookToBeRented = getBookByIndex(String.valueOf(rent.getbId()));
        if (bookToBeRented.getRented() == 1) {
            throw new IllegalArgumentException("Książka już wypożyczona");
        } else if (bookToBeRented.getRented() == 2) {
            throw new IllegalArgumentException("Książka jest zarchiwizowana !!");
        }

        rent.setRentId(CommonRentApiCalls.addNewRent(rent.getcId(), rent.getbId(), rent.getRentDate().toString(), rent.getAgreedReturnDate().toString()));
        activeRents.add(rent);
        bookToBeRented = getBookByIndex(String.valueOf(rent.getbId()));
        bookToBeRented.setRented(1);
        CommonBookApiCalls.changeStatusToRented(bookToBeRented.getBookId());
    }

    /**
     * Retrieves book by index from library local collections
     *
     * @param index of book
     * @return Book from local collections
     */
    public Book getBookByIndex(String index) {
        var integerIdex = Integer.valueOf(index);
        for (var book : booksOnStock) {
            if (book.getBookId() == integerIdex) {
                return book;
            }
        }
        return null;
    }

    /**
     * Retrieves client by index from library local collections
     *
     * @param index of book
     * @return Book from local collections
     */
    public Client getClientById(String index) {
        var integerIdex = Integer.valueOf(index);
        for (var client : registeredClients) {
            if (client.getId() == integerIdex) {
                return client;
            }
        }
        return null;
    }

    /**
     * Retrieves rent by index from library local collections
     *
     * @param index of rent
     * @return rent with given id
     * @return rent with given id
     */
    public Rent getRentByIndex(String index) {
        var integerIdex = Integer.valueOf(index);
        for (var rent : activeRents) {
            if (rent.getRentId() == integerIdex) {
                return rent;
            }
        }
        for (var rent : archiveRents) {
            if (rent.getRentId() == integerIdex) {
                return rent;
            }
        }
        return null;
    }

    /**
     * Manages book returning : Validates book, sets its status to not rented, sets return date of rent
     *
     * @param rentID id of the rent to be returned
     */
    public void handleBookReturning(String rentID) {
        var rent = getRentByIndex(rentID);
        var book = getBookByIndex(String.valueOf(rent.getbId()));
        if (book == null || book.getRented() == 0 || book.getRented() == 2) {
            throw new IllegalArgumentException("Książka zwrócona bądź archiwizowana");
        }
        book.setRented(0);
        CommonBookApiCalls.changeStatusToNotRented(book.getBookId());
        rent.setReturnDate(Date.valueOf(LocalDate.now()));
        activeRents.remove(rent);
        archiveRents.add(rent);
        CommonRentApiCalls.changeRentReturnDate(Integer.valueOf(rentID));

    }

    /**
     * Exports library data to XML file
     */
    public void exportLibraryDataToXML() {
        LibraryDataHandler.writeLibraryDataToXml(this);
    }

    /**
     * Calls CommonQueries to filter books collection, sets local collection to retrieved objects
     *
     * @param titleFilter  title filter
     * @param authorFilter author filter
     * @return list of filtered books
     */
    public ArrayList<Book> setAndReturnBooksWithFilter(String titleFilter, String authorFilter) throws IOException {
        booksOnStock.clear();
        booksOnStock.addAll(CommonBookApiCalls.getFilteredBooks(titleFilter, authorFilter));
        return booksOnStock;
    }

    /**
     * Calls CommonQueries to filter active rents collection, sets local collection to retrieved objects
     *
     * @param titleFilter  title filter
     * @param clientFilter client filter
     * @return list of filtered rents
     */
    public ArrayList<Rent> setAndReturnActiveRentsWithFilter(String titleFilter, String clientFilter) throws IOException {
        activeRents.clear();
        activeRents.addAll(CommonRentApiCalls.getAllActiveRentsWithFilter(titleFilter, clientFilter));
        return activeRents;
    }

    /**
     * Calls CommonQueries to filter archive rents collection, sets local collection to retrieved objects
     *
     * @param titleFilter  title filter
     * @param clientFilter client filter
     * @return list of filtered rents
     */
    public ArrayList<Rent> setAndReturnArchiveRentsWithFilter(String titleFilter, String clientFilter) throws IOException {
        archiveRents.clear();
        archiveRents.addAll(CommonRentApiCalls.getAllArchiveRentsWithFilter(titleFilter, clientFilter));
        return archiveRents;
    }

    /**
     * Generates descriptions for given rents, replaces bookId and ClientID with bookTitle and Client Name + LastName
     *
     * @param rents rents to return description for
     * @return description list
     */
    public ArrayList<String> generateDescriptionsForGivenRents(ArrayList<Rent> rents) {
        var descriptions = new ArrayList<String>();
        for (var rent : rents) {
            descriptions.add(generateDescriptionForSingleRent(rent));
        }
        return descriptions;
    }

    /**
     * Generate description for single rent, replaces bookId and ClientID with bookTitle and Client Name + LastName
     *
     * @param rent to return description for
     * @return description
     */
    public String generateDescriptionForSingleRent(Rent rent) {
        var client = getClientById(String.valueOf(rent.getcId()));
        var book = getBookByIndex(String.valueOf(rent.getbId()));
        var descToAdd = rent.getReturnDate() == null ?
                rent.getRentId() + ". Klient - " + client.getFirstName() + " " + client.getLastName() + ". Książka : " + book.getTitle() + ". Początek wypożyczenia : " + rent.getRentDate() + ", uzgodniony koniec wypożyczenia : " + rent.getAgreedReturnDate()
                : rent.getRentId() + ". Klient - " + client.getFirstName() + " " + client.getLastName() + ". Książka : " + book.getTitle() + ". Początek wypożyczenia : " + rent.getRentDate() + ", uzgodniony koniec wypożyczenia : " + rent.getAgreedReturnDate() + " zwrócono : " + rent.getReturnDate();

        return descToAdd;
    }

    /**
     * Generates short descriptions for given rents, replaces bookId and ClientID with bookTitle and Client Name + LastName.
     * Description contains book title, client data and agreed return date
     *
     * @param rents rents to return description for
     * @return description list
     */
    public ArrayList<String> rentsShortdescriptions(ArrayList<Rent> rents) {
        var shortDescriptions = new ArrayList<String>();
        for (var rent : rents) {
            shortDescriptions.add(generateRentShortDescription(rent));
        }
        return shortDescriptions;
    }

    /**
     * Generates short description for single rent, replaces bookId and ClientID with bookTitle and Client Name + LastName.
     * Description contains book title, client data and agreed return date
     *
     * @param rent to return description for
     * @return description
     */
    public String generateRentShortDescription(Rent rent) {
        var client = getClientById(String.valueOf(rent.getcId()));
        var book = getBookByIndex(String.valueOf(rent.getbId()));
        var descToAdd = rent.getRentId() + ". Client - " + client.getFirstName() + " " + client.getLastName() + ". Book : " + book.getTitle() + ". AgreedReturnDate : " + rent.getAgreedReturnDate();
        return descToAdd;
    }

    /**
     * Generates shortest descriptions for given rents, replaces bookId and ClientID with bookTitle and Client Name + LastName.
     * Description contains book title and client data only
     *
     * @param rents rents to return description for
     * @return description list
     */
    public ArrayList<String> rentsClientBookDescriptions(ArrayList<Rent> rents) {
        var shortDescriptions = new ArrayList<String>();
        for (var rent : rents) {
            var client = getClientById(String.valueOf(rent.getcId()));
            var book = getBookByIndex(String.valueOf(rent.getbId()));
            var descToAdd = rent.getRentId() + ". Client - " + client.getFirstName() + " " + client.getLastName() + ". Book : " + book.getTitle();
            shortDescriptions.add(descToAdd);
        }
        return shortDescriptions;
    }


    @Override
    public String toString() {
        var sb = new StringBuilder();
        var index = 1;
        sb.append("Library positions On Stock : ");
        for (Book bookPosition : booksOnStock) {
            if (bookPosition != null) {
                sb.append("\n")
                        .append(index++)
                        .append(". ")
                        .append(bookPosition.getAuthor())
                        .append(" - ")
                        .append(bookPosition.getTitle())
                        .append(". Price :")
                        .append(bookPosition.getPrice())
                        .append("$. Pages : ")
                        .append(bookPosition.getPages())
                        .append(". Size : ")
                        .append(bookPosition.getHeight())
                        .append("cm x ")
                        .append(bookPosition.getWidth())
                        .append("cm");
            }
        }
        return sb.toString();
    }
}
