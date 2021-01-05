package Service.DBUtils;

import Model.Rent;
import Service.DAO.EndpointAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class CommonRentApiCalls {


    /**
     * Performs a filtering of all rents to retrieve last Rent ID
     * Used when adding new Rent to provide uniqueness of ID
     * @return ID of last rent in Database
     */
    public static  int getLastRentID() throws IOException{
        return Integer.parseInt(EndpointAccessor.executeAPICallGet("/api/Rents/getLastRentID").body().string());
    }

    /**
     * Performs a filtering of all rents to retrieve all not returned Rents
     * @return all non returned rents from Database
     */
    public static List<Rent> getAllActiveRents() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        List<Rent> list = mapper.readValue(EndpointAccessor.executeAPICallGet("/api/Rents/GetActiveRents").body().string(),
                new TypeReference<ArrayList<Rent>>() {});
        return list;
    }

    /**
     * Performs a query through DatabaseAccessor to retrieve filtered not returned Rents ( client lastName and book title filters )
     * @param title to filter
     * @param client to filter
     * @return all rents from Database
     */
    public static List<Rent> getAllActiveRentsWithFilter(String title, String client) throws IOException {
        String urlPart = "/api/Rents/GetActiveRents";
                if (title != "" && client !="" ) urlPart = urlPart + "?title=" + title + "&client=" + client;
                else if (title != "" && client =="") urlPart = urlPart + "?title=" + title;
                else if (title =="" && client != "") urlPart = urlPart + "?client=" + client;

        ObjectMapper mapper = new ObjectMapper();
        List<Rent> list = mapper.readValue(EndpointAccessor.executeAPICallGet(urlPart).body().string(),
                new TypeReference<ArrayList<Rent>>() {});
        return list;
    }

    /**
     * Performs a query through DatabaseAccessor to retrieve filtered returned Rents ( client lastName and book title filters )
     * @param title to filter
     * @param client to filter
     * @return all rents from Database
     */
    public static List<Rent> getAllArchiveRentsWithFilter(String title, String client) throws IOException {
        String urlPart = "/api/Rents/GetArchiveRents";
        if (title != "" && client !="" ) urlPart = urlPart + "?title=" + title + "&client=" + client;
        else if (title != "" && client =="") urlPart = urlPart + "?title=" + title;
        else if (title =="" && client != "") urlPart = urlPart + "?client=" + client;

        ObjectMapper mapper = new ObjectMapper();
        List<Rent> list = mapper.readValue(EndpointAccessor.executeAPICallGet(urlPart).body().string(),
                new TypeReference<ArrayList<Rent>>() {});
        return list;
    }



    /**
     * Performs a query through DatabaseAccessor to retrieve all returned Rents
     * @return all rents from Database
     */
    public static List<Rent> getAllArchiveRents() throws IOException {
        String urlPart = "/api/Rents/GetArchiveRents";
        ObjectMapper mapper = new ObjectMapper();
        List<Rent> list = mapper.readValue(EndpointAccessor.executeAPICallGet(urlPart).body().string(),
                new TypeReference<ArrayList<Rent>>() {});
        return list;
    }

    /**
     * Performs a query through DatabaseAccessor to retrieve all returned Rents for given Date
     * @param date - Date for which returns should be returned
     * @return list of rents with return date of given datel
     */
    public static List<Rent> getAllRentsForGivenDate(Date date) throws IOException {
        String urlPart = "/api/Rents/getAllRentsForGivenDate?date=" + date.toString();
        ObjectMapper mapper = new ObjectMapper();
        List<Rent> list = mapper.readValue(EndpointAccessor.executeAPICallGet(urlPart).body().string(),
                new TypeReference<ArrayList<Rent>>() {});
        return list;
    }

    /**
     * Performs a query through DatabaseAccessor to retrieve 15 rents with agreed return date closest to actual one
     * @return all rents from Database
     */
    public static List<Rent> get15RentsWithClosestAgreedReturnDate() throws IOException {
        String urlPart = "/api/Rents/get15RentsWithClosestAgreedReturnDate";
        ObjectMapper mapper = new ObjectMapper();
        List<Rent> list = mapper.readValue(EndpointAccessor.executeAPICallGet(urlPart).body().string(),
                new TypeReference<ArrayList<Rent>>() {});
        return list;
    }

    /**
     * Performs a query through DatabaseAccessor to retrieve not returned rents with agreed return date past actual one
     * @return all rents from Database
     */
    public static List<Rent> getOverKeepedRents() throws IOException {
        String urlPart = "/api/Rents/getOverKeepedRents";
        ObjectMapper mapper = new ObjectMapper();
        List<Rent> list = mapper.readValue(EndpointAccessor.executeAPICallGet(urlPart).body().string(),
                new TypeReference<ArrayList<Rent>>() {});
        return list;
    }

    /**
     * Performs a query through DatabaseAccessor to add New Rent to DB
     * @param clientID of new Rent
     * @param bookID of new Rent
     * @param rentDate of new Rent
     * @param agreedReturnDate of new Rent
     */
    public static Integer addNewRent(int clientID, int bookID, String rentDate, String agreedReturnDate) throws IOException {
        String urlPart = "/api/Rents";
        String jsonRent = "{\"cId\": " + clientID + ",\"bId\": " + bookID + ",\"rentDate\": \"" + rentDate + "\",\"agreedReturnDate\": \"" +agreedReturnDate + "\"}";
        int newId = 9999;
        final ObjectNode node = new ObjectMapper().readValue(EndpointAccessor.executeAPICallPost(urlPart,jsonRent).body().string(), ObjectNode.class);
        if (node.has("rentId")) {
            newId = node.get("rentId").asInt();
        }
        return newId;
    }

    /**
     * Performs a query through DatabaseAccessor to change return date to today.
     * This is returning book operation
     * @param id of rent to be changed
     */
    public static void changeRentReturnDate(int id) {
        String urlPart = "/api/Rents/changeRentReturnDate?id=" + id;
        EndpointAccessor.executeAPICallGet(urlPart);
    }

    /**
     * Performs a query through DatabaseAccessor to retrieve number of rents that should be returned today
     * @return number of returned that should happen today
     */
    public static int numberOfTodayReturns() throws IOException {
        String urlPart = "/api/Rents/numberOfTodayReturns";
        var result =  EndpointAccessor.executeAPICallGet(urlPart).body().string();
        return Integer.parseInt(result);
    }
}
