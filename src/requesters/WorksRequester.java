package requesters;

import utils.ErrorHandler;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.io.IOException;

import org.json.*;

/**
 * Performs a GET request to the openlibrary.org/works API.
 * Extends abstract class {@link requesters.Requester Requester}
 * @see requesters.Requester
 */

public class WorksRequester extends Requester {
    private String[] results = new String[2];

    /**
     * 
     */

    @Override
    public String[] readFromURL(String reqURL) throws IOException, ErrorHandler.ConnectionException {
        String scanResults = new String();

        //reqURL = "https://openlibrary.org" + reqURL + ".json";
        URL url = new URL(reqURL);
        System.out.println(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new ErrorHandler.ConnectionException(String.valueOf(responseCode) + " response code");
        }
        Scanner scanner = new Scanner(url.openStream());
        while (scanner.hasNext()) {
            scanResults += scanner.nextLine();
        }
        scanner.close();
        conn.disconnect();
        JSONObject jsonObject = new JSONObject(scanResults);
        results[0] = jsonObject.getString("title");
        if (jsonObject.has("description")) {
            if (jsonObject.get("description") instanceof JSONObject) {
                JSONObject res = jsonObject.getJSONObject("description");
                results[1] = res.getString("value");
            } else {
                results[1] = jsonObject.getString("description");
            }
        } 
        return results;
    }

}
