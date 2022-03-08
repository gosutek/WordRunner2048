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

    public Dictionary(File dictionary) {
        dictionaryID = dictionary.getName().replace("hangman_", "").replace(".txt", "");
        pathToDictionary = "./dictionaries/hangman_" + dictionaryID + ".txt";
        dictionaryBook = "null";
        dictionaryContents = readExistingDictionary(dictionary);
        numberOfWords = dictionaryContents.length;
    }

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
        String[] formattedText = text.replaceAll("\\W", "\s").replaceAll("\\s", "\n")
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
                continue;
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
            errorMessage = exc.getStackTrace().toString();
        }
        return resultsWords.toArray(new Word[resultsWords.size()]);
    }

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

    private void calcDictionaryStatistics(int sixLetterWords, int sevenToNineLetterWords, int tenOrMoreLetterWords) {
        dictionaryStatistics[0] = ((float) sixLetterWords / numberOfWords) * 100;
        dictionaryStatistics[1] = ((float) sevenToNineLetterWords / numberOfWords) * 100;
        dictionaryStatistics[2] = ((float) tenOrMoreLetterWords / numberOfWords) * 100;
    }

}