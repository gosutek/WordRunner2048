package dictionary;

import utils.ErrorHandler;
import requesters.WorksRequester;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.json.*;

public class Dictionary {

    private int numberOfWords;
    private String pathToDictionary, dictionaryBook, dictionaryID;
    private Word[] dictionaryContents;
    private String errorMessage = null;

    public Dictionary(){};

    public Dictionary(File dictionary) {
        dictionaryID = dictionary.getName().replace("handman_", "").replace(".txt", "");
        pathToDictionary = "./medialab/hangman_" + dictionaryID + ".txt";
        dictionaryBook = "null";
        dictionaryContents = readExistingDictionary(dictionary);
        numberOfWords = dictionaryContents.length;
    }

    public Dictionary(String url) {
        url = "https://openlibrary.org" + url + ".json";
        dictionaryID = url.replace("https://openlibrary.org/works/", "").replace(".json", "");
        pathToDictionary = "./medialab/hangman_" + dictionaryID + ".txt";

        File dictionaryFile = new File(pathToDictionary);
        if (!dictionaryFile.exists()) {
            WorksRequester worksRequester = new WorksRequester();
            String[] results = worksRequester.readFromURL(url);
            dictionaryBook = results[0];
            dictionaryContents = parseDictionary(results[1]); // this sets words and letters
            if (dictionaryContents != null) {
                createDictionary(dictionaryFile);
            }
        } else {
            dictionaryContents = readExistingDictionary(dictionaryFile);
            numberOfWords = dictionaryContents.length;
        }

    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getWords() {
        return numberOfWords;
    }

    public String getPathToDictionary() {
        return pathToDictionary;
    }

    public void setDictionaryBook(String dictionaryBook) {
        this.dictionaryBook = dictionaryBook;
    }

    public String getDictionaryBook() {
        return dictionaryBook;
    }

    public Word[] getDictionaryContents() {
        return dictionaryContents;
    }

    @Override
    public String toString() {
        JSONObject dictionaryObj = new JSONObject();
        dictionaryObj.put("DictionaryID", dictionaryID);
        dictionaryObj.put("DictionaryBook", dictionaryBook);
        dictionaryObj.put("#ofWords", numberOfWords);
        return dictionaryObj.toString();
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
            for (Word word : dictionaryContents) {
                dictionaryWriter.write(word + "\n");
            }
            dictionaryWriter.close();

        } catch (IOException exc) {
            System.out.println("\n" + exc);
        }
    }

    private Word[] parseDictionary(String text) {
        String[] formattedText = text.replaceAll("\\W", "\s").replaceAll("\\s", "\n").toUpperCase().split("\\s");
        ArrayList<Word> resultsWords = new ArrayList<Word>();
        HashSet<String> dupSet = new HashSet<String>(); /* Checks for dups */
        int bigWords = 0;

        for (String word : formattedText) {
            try {
                if (dupSet.contains(word)) {
                    throw new ErrorHandler.InvalidCountException(word);
                }
                dupSet.add(word);
                if (word.length() < 6) {
                    throw new ErrorHandler.InvalidRangeException(word,
                            String.valueOf(word.length() + " letters"));
                } else if (word.length() >= 9) {
                    bigWords++;
                }
                resultsWords.add(new Word(word));

            } catch (ErrorHandler.InvalidRangeException | ErrorHandler.InvalidCountException exc) {
                //exc.printStackTrace();
                continue;
            }
        }
        numberOfWords = resultsWords.size();
        try {
            if (numberOfWords == 0) {
                throw new ArithmeticException("Zero total length");
            } else if (bigWords < 0.2 * numberOfWords) {
                throw new ErrorHandler.UnbalancedException(dictionaryBook,
                        String.valueOf((bigWords / numberOfWords) * 100));
            } else if (numberOfWords < 20) {
                throw new ErrorHandler.UndersizeException(dictionaryBook, String.valueOf(numberOfWords));
            }
        } catch (ArithmeticException | ErrorHandler.UnbalancedException
                | ErrorHandler.UndersizeException exc) {
            exc.printStackTrace();
            errorMessage = exc.getStackTrace().toString();
            return null;
        }
        return resultsWords.toArray(new Word[resultsWords.size()]);
    }

    private Word[] readExistingDictionary(File dictionaryFile) {
        List<Word> wordList = new ArrayList<Word>();
        try {
            Scanner reader = new Scanner(dictionaryFile);
            while(reader.hasNextLine()) {
                Word word = new Word(reader.nextLine());
                wordList.add(word);
            }
            reader.close();
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
        }
        return wordList.toArray(new Word[wordList.size()]);
    }
}