import java.net.HttpURLConnection;
import java.net.URL;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

import org.json.*;

public class CrosswordDictionary {

    private int numberOfWords;
    private String pathToDictionary, dictionaryBook, url, dictionaryID;
    private String[] dictionaryContents;

    CrosswordDictionary(String url) {
        this.url = url;
        dictionaryID = url.replace("https://openlibrary.org/works/", "").replace(".json", "");
        pathToDictionary = "./medialab/hangman_" + dictionaryID + ".txt";

        File dictionaryFile = new File(pathToDictionary);
        String[] results = readFromURL(url);

        dictionaryBook = results[0];
        dictionaryContents = parseDictionary(results[1]); // this sets words

        createDictionary(dictionaryFile);
    }

    public int getWords() {
        return numberOfWords;
    }

    public String getPathToDictionary() {
        return pathToDictionary;
    }

    public String getDictionaryBook() {
        return dictionaryBook;
    }

    public String[] getDictionaryContents() {
        return dictionaryContents;
    }

    public String getURL() {
        return url;
    }

    @Override
    public String toString() {
        JSONObject dictionaryObj = new JSONObject();
        dictionaryObj.put("DictionaryID", dictionaryID);
        dictionaryObj.put("DictionaryBook", dictionaryBook);
        dictionaryObj.put("URL", url);
        dictionaryObj.put("#ofWords", numberOfWords);
        return dictionaryObj.toString();
    }

    private static String[] readFromURL(String urlString) {

        String[] results = new String[2];
        String test = new String();

        try {

            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            System.out.println("Establishing connection...");
            conn.connect();

            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                throw new CrosswordErrorHandler.ConnectionException(String.valueOf(responseCode) + " response code");
            }

            System.out.println("Reading json...");
            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNext()) {
                test += scanner.nextLine();
            }
            scanner.close();
            System.out.println("Closing connection...");
            conn.disconnect();

            JSONObject jsonObject = new JSONObject(test);

            JSONObject res = jsonObject.getJSONObject("description");
            results[0] = jsonObject.getString("title");
            results[1] = res.getString("value");

        } catch (IOException | CrosswordErrorHandler.ConnectionException exc) {
            exc.printStackTrace();
        }
        return results;
    }

    private void createDictionary(File dictionaryFile) {

        try {
            if (dictionaryFile.exists()) {
                dictionaryFile.delete();
            }
            dictionaryFile.createNewFile();
            System.out.println("Successfully created dictionary at " +
                    dictionaryFile.getPath());

            FileWriter dictionaryWriter = new FileWriter(dictionaryFile);
            for (String word : dictionaryContents) {
                dictionaryWriter.write(word + "\n");
            }
            dictionaryWriter.close();

        } catch (IOException exc) {
            System.out.println("\n" + exc);
        }
    }

    private String[] parseDictionary(String text) {
        String[] formattedText = text.replaceAll("\\W", "\s").replaceAll("\\s", "\n").toUpperCase().split("\\s");
        ArrayList<String> results = new ArrayList<String>();
        int bigWords, totalLength;
        bigWords = totalLength = 0;

        for (String word : formattedText) {
            try {
                if (word.length() < 6) {
                    throw new CrosswordErrorHandler.InvalidRangeException(word,
                            String.valueOf(word.length() + " letters"));
                } else if (word.length() >= 9) {
                    bigWords++;
                }
                totalLength++;
                results.add(word);

            } catch (CrosswordErrorHandler.InvalidRangeException invalidRangeException) {
                continue;
            }
        }
        try {
            if (totalLength == 0) {
                throw new ArithmeticException("Zero total length");
            } else if (bigWords < 0.2 * totalLength) {
                throw new CrosswordErrorHandler.UnbalancedException(dictionaryBook,
                        String.valueOf((bigWords / totalLength) * 100));
            } else if (totalLength < 20) {
                throw new CrosswordErrorHandler.UndersizeException(dictionaryBook, String.valueOf(totalLength));
            }
        } catch (ArithmeticException | CrosswordErrorHandler.UnbalancedException
                | CrosswordErrorHandler.UndersizeException exc) {
            exc.printStackTrace();
        }
        numberOfWords = totalLength;
        return results.toArray(new String[results.size()]);
    }
}