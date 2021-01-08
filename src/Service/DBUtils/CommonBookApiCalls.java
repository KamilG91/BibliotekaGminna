package Service.DBUtils;

import Model.Book;
import Service.DAO.EndpointAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommonBookApiCalls {

    /**
     * Performs a query through DatabaseAccessor and return last book ID.
     * Used when adding new Books to provide uniqueness of ID
     * @return ID of last book in Database
     */
    public static int getLastBookID() throws IOException {
        return Integer.parseInt(EndpointAccessor.executeAPICallGet("/api/Books/getLastBookID").body().string());
    }

    /**
     * Performs a query through DatabaseAccessor and return all books from DB
     * @return all books from Database
     */
    public static List<Book> getAllBooks() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return  mapper.readValue(EndpointAccessor.executeAPICallGet("/api/Books").body().string(),
                new TypeReference<ArrayList<Book>>() {});
    }

    /**
     * Performs a query through DatabaseAccessor and return books filtered through title and author
     * @param titleFilter title to be filter
     * @param authorFilter author to be filtered
     * @return list of books
     */
    public static List<Book> getFilteredBooks(String titleFilter, String authorFilter) throws IOException {
        String urlPart = "/api/Books/getFilteredBooks";
        if (!titleFilter.equals("") && !authorFilter.equals("")) urlPart = urlPart + "?title=" + titleFilter + "&authorFilter=" + authorFilter;
        else if (!titleFilter.equals("") && authorFilter.equals("")) urlPart = urlPart + "?title=" + titleFilter;
        else if (titleFilter.equals("") && !authorFilter.equals("")) urlPart = urlPart + "?authorFilter=" + authorFilter;

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(EndpointAccessor.executeAPICallGet(urlPart).body().string(),
                new TypeReference<ArrayList<Book>>() {});
    }

    /**
     * Performs a query through DatabaseAccessor to add a new Book
     * @param pages of new Book
     * @param width of new Book
     * @param height of new Book
     * @param price of new Book
     * @param title of new Book
     * @param author of new Book
     * @param rented of new Book
     */
    public static Integer addNewBook(int pages, double width, double height, double price, String title, String author, int rented) throws IOException {
        Integer newId =999;
        String urlPart = "/api/Books";
        String jsonBook = "{\"pages\": " + pages + ",\"width\": " + width + ",\"height\": \"" + height + "\",\"price\": \"" + price + "\",\"title\": \"" + title + "\",\"author\": \"" + author + "\",\"rented\": " + rented + "}";
        final ObjectNode node = new ObjectMapper().readValue(EndpointAccessor.executeAPICallPost( urlPart, jsonBook).body().string(), ObjectNode.class);
        if (node.has("bookId")) {
            newId = node.get("bookId").asInt();
        }
        return newId;
    }

    /**
     * Performs a query through DatabaseAccessor to updates book status to not rented
     * @param id of a book
     */
    public static void changeStatusToNotRented(int id) {
        String urlPart = "/api/Rents/changeStatusToNotRented?id=" + id;
        EndpointAccessor.executeAPICallGet(urlPart);
    }

    /**
     * Performs a query through DatabaseAccessor to updates book status to rented
     * @param id of a book
     */
    public static void changeStatusToRented(int id) {
        String urlPart = "/api/Rents/changeStatusToRented?id=" + id;
        EndpointAccessor.executeAPICallGet(urlPart);
    }

    /**
     * Performs a query through DatabaseAccessor to updates book status to archived
     * @param id of a book
     */
    public static void changeStatusToArchived(int id) {
        String urlPart = "/api/Rents/changeStatusToArchived?id=" + id;
        EndpointAccessor.executeAPICallGet(urlPart);
    }

}
