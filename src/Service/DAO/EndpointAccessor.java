package Service.DAO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;

import java.io.IOException;

public class EndpointAccessor {
    private static EndpointAccessor uniqueInstance = new EndpointAccessor();
    private static OkHttpClient client = new OkHttpClient().newBuilder().build();;
    private static String bearerToken;

    /**
     * Based on a singleton pattern, we don't need multiple connections in one application.
     */
    private EndpointAccessor() {

    }
    public static boolean Authenticate (String username, String password) {
        boolean success = false ;
        try {
            MediaType mediaType = MediaType.parse("application/json");
            String jsonBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
            RequestBody body = RequestBody.create(mediaType, jsonBody);
            Request request = new Request.Builder()
                    .url("https://gawotzychrestapi.azurewebsites.net/Users/authenticate")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            Response response = client.newCall(request).execute();
            String responseString = response.body().string();
            final ObjectNode node = new ObjectMapper().readValue(responseString, ObjectNode.class);
            if (node.has("token")) {
                bearerToken = node.get("token").textValue();
                success = true;
            }
        } catch (Exception ex) {
            System.out.println("Exception ! - Could not connect to the DB Server + Error Message " + ex.getMessage());
        }
        return success;
    }
    /**
     * Singleton pattern implementation, always returns the same instance of databaseAccessor.
     * @return an instance of a EndpointAccessor
     */
    public static EndpointAccessor getInstance() {
        return uniqueInstance;
    }

    /**
     * Execute a query and return a resultSet.
     * @param urlPart,json  passed in String
     * @return resultSet based on a query
     */
    public static Response executeAPICallPost( String urlPart, String json) {
        try {

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url("https://gawotzychrestapi.azurewebsites.net" + urlPart)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + bearerToken)
                    .build();
            Response response = client.newCall(request).execute();
            return response;
        } catch (IOException  exception) {
            System.out.println("Error making API Call - Exception message - " + exception.getMessage());
        }
        return null;
    }
    public static Response executeAPICallGet(String urlPart) {
        try {
            Request request = new Request.Builder()
                    .url("https://gawotzychrestapi.azurewebsites.net" + urlPart)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + bearerToken)
                    .build();
            Response response = client.newCall(request).execute();
            return response;
        } catch (IOException  exception) {
            System.out.println("Error making API Call - Exception message - " + exception.getMessage());
        }
        return null;
    }
}
