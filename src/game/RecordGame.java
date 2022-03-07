package game;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.json.*;

public class RecordGame {

    private final String dateTime, hiddenWord, outcome;
    private final int numberOfTries;
    private final File recordFile = new File("game_records.json");
    private JSONObject jsonObject = new JSONObject();

    RecordGame(String hiddenWord, String outcome, int numberOfTries) {

        this.hiddenWord = hiddenWord;
        this.outcome = outcome;
        this.numberOfTries = numberOfTries;
        dateTime = java.time.LocalDateTime.now().toString().replaceAll("T", " ");
        jsonObject.put("hidden-word", hiddenWord);
        jsonObject.put("outcome", outcome);
        jsonObject.put("number-of-tries", ((Integer) numberOfTries).toString());
        jsonObject.put("datetime", dateTime);
        try {
            if (!recordFile.exists()) {
                FileWriter jsonWriter = new FileWriter(recordFile);
                recordFile.createNewFile();
                jsonWriter.write(createJSON().toString());
                jsonWriter.close();
            } else {
                save();
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

    private JSONObject save() {
        JSONObject main = (JSONObject) new JSONTokener(readJSON()).nextValue();
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

    private String readJSON() {
        StringBuilder jsonString = new StringBuilder();
        try {
            Scanner stringScanner = new Scanner(recordFile);
            while(stringScanner.hasNextLine()) {
                jsonString.append(stringScanner.nextLine());
            }
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
            jsonString = null;
        }
        return jsonString.toString();
    }

}
