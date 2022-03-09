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
import java.util.Set;

import org.json.*;

public class Dictionary {

    private int numberOfWords;
    private String pathToDictionary, dictionaryBook, dictionaryID;
    private Word[] dictionaryContents;
    private float[] dictionaryStatistics = new float[3];
    private String errorMessage = null;
    private final Set<String> bannedWordsSet = Set.<String>of("OPENLIBRARY");

    public Dictionary(){};

    /**
     * Used for loading a local dictionary.
     * @param dictionary the {@code File} object of the dictionary. Path must be ./dictionaries/hangman_{dictionaryID}.txt
     */

    public Dictionary(File dictionary) {
        dictionaryID = dictionary.getName().replace("hangman_", "").replace(".txt", ""); // hangman_ + dictionaryID(ex.OL77795W) + .txt
        pathToDictionary = "./dictionaries/hangman_" + dictionaryID + ".txt";
        dictionaryBook = "null"; // local dictionary files dont keep track of book names.
        dictionaryContents = readExistingDictionary(dictionary);
        numberOfWords = dictionaryContents.length;
    }

    /**
     * Used for loading a remote dictionary.
     * @param url the works ID for requesting the openlibrary.org/works API. Typically provided by user or by the {@code SubjectRequester}.
     * @see requesters.SubjectRequester
     */

    public Dictionary(String url) {
        url = "https://openlibrary.org" + url + ".json";
        dictionaryID = url.replace("https://openlibrary.org/works/", "").replace(".json", "");
        pathToDictionary = "./dictionaries/hangman_" + dictionaryID + ".txt";

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
        }

    }

    /**
     * dictionaryStatistics[0] = % of 6 letter words
     * dictionaryStatistics[1] = % of 7 to 9 letter words
     * dictinaryStatistics[2] = % of 10 or more letter words
     */

    public float[] getDictionaryStatistics() {
        return dictionaryStatistics;
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

    /**
     * Creates .txt file at ./dictionaries/FILE_PATH
     * @param dictionaryFile the {@code File} object of the dictionary
     */

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
                dictionaryWriter.write(word + "\n"); // each word will be followed by a \n
            }
            dictionaryWriter.close();

        } catch (IOException exc) {
            System.out.println("\n" + exc);
        }
    }

    /**
     * Parses the openlibrary.org/works API response for exceptions and statistic calculation.
     * @param text the raw {@code String} parsed with org.json.
     * @return array of dictionary.Word containing the filtered and formatted words of the dictionary.
     * @throws ErrorHandler.InvalidCountException if the method finds a duplicate word while parsing.
     * @throws ErrorHandler.BannedWordException if the method find a predefined banned word while parsing.
     * @see org.json
     * @see dictionary.Word
     */

    private Word[] parseDictionary(String text) {
        /* Replaces all non word characters with a whitespace
        * all whitespaces with a new line 
        * all underscores with a whitespace
        * converts to uppercase
        * splits at whitespace
        */
        if (text == null) {
            errorMessage = "Selected dictionary had no description";
            return null;
        }
        String[] formattedText = text.replaceAll("\\W", " ").replaceAll("\\s", "\n")
        .replaceAll("_", " ").toUpperCase().split("\\s");
        ArrayList<Word> resultsWords = new ArrayList<Word>();
        HashSet<String> dupSet = new HashSet<String>(); /* Checks for dups */
        int bigWords = 0;
        int sixLetterWords, sevenToNineLetterWords, tenOrMoreLetterWords;
        sixLetterWords = sevenToNineLetterWords = tenOrMoreLetterWords = 0;

        for (String word : formattedText) {
            try {
                if (dupSet.contains(word)) {
                    throw new ErrorHandler.InvalidCountException(word);
                }
                if (bannedWordsSet.contains(word)) {
                    throw new ErrorHandler.BannedWordException(dictionaryBook, word);
                }
                if (word.matches("OL[0-9]+[a-z]*[A-Z]*") || word.matches("[0-9]+")) {
                    throw new ErrorHandler.BannedWordException(dictionaryBook, word);
                }
                dupSet.add(word);
                if (word.length() < 6) {
                    throw new ErrorHandler.InvalidRangeException(word,
                            String.valueOf(word.length() + " letters"));
                } else if (word.length() >= 9) {
                    bigWords++;
                }
                if (word.length() == 6) {
                    sixLetterWords++;
                } else if (word.length() >= 7 && word.length() <= 9) {
                    sevenToNineLetterWords++;
                } else if (word.length() >= 10) {
                    tenOrMoreLetterWords++;
                }
                resultsWords.add(new Word(word));

            } catch (ErrorHandler.InvalidRangeException | ErrorHandler.InvalidCountException | ErrorHandler.BannedWordException exc) {
                continue; // just skips the word and does not add it to the dictionary.
            }
        }
        numberOfWords = resultsWords.size();
        calcDictionaryStatistics(sixLetterWords, sevenToNineLetterWords, tenOrMoreLetterWords);
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
            errorMessage = exc.getMessage();
        }
        return resultsWords.toArray(new Word[resultsWords.size()]);
    }
    /**
     * @param dictionaryFile the {@code File} to read.
     * @return a array of dictionary.Word of the dictionary
     */
    private Word[] readExistingDictionary(File dictionaryFile) {
        List<Word> wordList = new ArrayList<Word>();
        int sixLetterWords, sevenToNineLetterWords, tenOrMoreLetterWords;
        sixLetterWords = sevenToNineLetterWords = tenOrMoreLetterWords = 0;
        try {
            Scanner reader = new Scanner(dictionaryFile);
            while(reader.hasNextLine()) {
                Word word = new Word(reader.nextLine());
                if (word.length() == 6) {
                    sixLetterWords++;
                } else if (word.length() >= 7 && word.length() <= 9) {
                    sevenToNineLetterWords++;
                } else if (word.length() >= 10) {
                    tenOrMoreLetterWords++;
                }
                wordList.add(word);
            }
            reader.close();
            numberOfWords = wordList.size();
            calcDictionaryStatistics(sixLetterWords, sevenToNineLetterWords, tenOrMoreLetterWords);
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
        }
        return wordList.toArray(new Word[wordList.size()]);
    }

    /**
     * @param sixLetterWords the number of words with exactly 6 letters.
     * @param sevenToNineLetterWords the number of words with 7 to 9 letters.
     * @param tenOrMoreLetterWords the number of words with 10 or more letters.
     */

    private void calcDictionaryStatistics(int sixLetterWords, int sevenToNineLetterWords, int tenOrMoreLetterWords) {
        dictionaryStatistics[0] = ((float) sixLetterWords / numberOfWords) * 100;
        dictionaryStatistics[1] = ((float) sevenToNineLetterWords / numberOfWords) * 100;
        dictionaryStatistics[2] = ((float) tenOrMoreLetterWords / numberOfWords) * 100;
    }

}