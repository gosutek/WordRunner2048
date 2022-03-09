package requesters;

import utils.ErrorHandler;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Scanner;
import java.io.IOException;

import org.json.*;

/**
 * Performs a GET request to the openlibrary.org/subject API with a static work limit query.
 * Randomly selects a work ID from the response which is typically used as input for the {@link requesters.WorksRequester WorksRequester}.
 * Extends abstract class {@link requesters.Requester Requester}
 * @see requesters.Requester
 * @see requesters.WorksRequester
 */

public class SubjectRequester extends Requester {

    private String[] resultURL = new String[2];
    private final int workLimit = 50;

    @Override
    public String[] readFromURL(String subject) {
        String scanResults = new String();

        try {
            System.out.println("Requesting subject...");
            URL url = new URL("https://openlibrary.org/subjects/" + subject + ".json?limit=" + workLimit);

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

            Random rng = new Random();
            JSONArray res = jsonObject.getJSONArray("works");
            int randomSelection = rng.nextInt(workLimit);
            JSONObject work = res.getJSONObject(randomSelection);
            resultURL[0] = work.getString("key");
            resultURL[1] = work.getString("title");

        } catch (IOException | ErrorHandler.ConnectionException exc) {
            exc.printStackTrace();
        }

        return resultURL;
    }

    public String[] getResults() {
        return resultURL;
    }
    
}
