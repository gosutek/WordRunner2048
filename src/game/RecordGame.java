package game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.json.*;

public class RecordGame {

    private final String dateTime;
    private final File recordFile = new File("game_records.json");
    private JSONObject jsonObject = new JSONObject();

    public RecordGame(String hiddenWord, String outcome, int numberOfTries, int score) {

        dateTime = java.time.LocalDateTime.now().toString().replaceAll("T", " ");
        jsonObject.put("hidden-word", hiddenWord);
        jsonObject.put("outcome", outcome);
        jsonObject.put("number-of-tries", ((Integer) numberOfTries).toString());
        jsonObject.put("datetime", dateTime);
        jsonObject.put("score", score);
        try {
            if (!recordFile.exists()) {
                FileWriter jsonWriter = new FileWriter(recordFile);
                recordFile.createNewFile();
                jsonWriter.write(createJSON().toString());
                jsonWriter.close();
            }
        } catch (IOException exc) {
            exc.printStackTrace();
        }

    }

    private JSONObject createJSON() {
        JSONObject main = new JSONObject();
        JSONArray mainArr = new JSONArray();
        main.put("records", mainArr);
        mainArr.put(jsonObject);

        return main;
    }

    public JSONObject save() {
        JSONObject main = (JSONObject) new JSONTokener(JSONFileToString()).nextValue();
        JSONArray mainArr = main.getJSONArray("records");
        mainArr.put(jsonObject);

        try {
            FileWriter jsonWriter = new FileWriter(recordFile);
            jsonWriter.write(main.toString());
            jsonWriter.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        return main;
        
    }

    private String JSONFileToString() {
        StringBuilder jsonString = new StringBuilder();
        try {
            Scanner stringScanner = new Scanner(recordFile);
            while(stringScanner.hasNextLine()) {
                jsonString.append(stringScanner.nextLine());
            }
            stringScanner.close();
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
            jsonString = null;
        }
        return jsonString.toString();
    }

}
