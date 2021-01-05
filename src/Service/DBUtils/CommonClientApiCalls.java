package Service.DBUtils;

import Model.Client;
import Service.DAO.EndpointAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommonClientApiCalls {

    /**
     * Performs a query through DatabaseAccessor to retrieve last client ID
     * Used when adding new Client to provide uniqueness of ID
     * @return ID of last client in Database
     */
    public static int getLastClientID() throws IOException {
        return Integer.parseInt(EndpointAccessor.executeAPICallGet("/api/Clients/getLastClientID").body().string());
    }

    /**
     * Performs a query through DatabaseAccessor to retrieve all client from DB
     * @return all clients from Database
     */
    public static List<Client> getAllClients() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<Client> list = mapper.readValue(EndpointAccessor.executeAPICallGet("/api/Clients").body().string(),
                new TypeReference<ArrayList<Client>>() {});
        return list;
    }


    /**
     * Performs a query through DatabaseAccessor to add a new CLient to DB
     * @param firstName - first Name of New Client
     * @param lastName - Last Name of New Client
     * @param emailAddress - Email of new client
     * @param telephoneNumber - Telephone number of new client
     */
    public static Integer addNewClient(String firstName, String lastName, String emailAddress, String telephoneNumber) throws IOException {
        int newId=0;
        String urlPart = "/api/Clients";
        String jsonBook = "{\"firstName\": \"" + firstName + "\",\"lastName\": \"" + lastName + "\",\"emailAddress\": \"" + emailAddress + "\",\"telephoneNumber\": \"" + telephoneNumber + "\"}";
        final ObjectNode node = new ObjectMapper().readValue(EndpointAccessor.executeAPICallPost( urlPart, jsonBook).body().string(), ObjectNode.class);
        if (node.has("clientId")) {
            newId = node.get("clientId").asInt();
        }
        return  newId;
    }
}
