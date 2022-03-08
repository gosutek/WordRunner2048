package game;

import dictionary.*;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;



public class Session {
    private Dictionary activeDictionary;
    private Word hiddenWord, guessWord;
    private ArrayList<Word> candidateWords = new ArrayList<Word>();
    private List<Map<String, Double>> probs = new ArrayList<Map<String, Double>>(); // Maps probabilities of letters for all positions
    private int score, tries, correctTries, lives = 0;


    Session(Dictionary dictionary) {
        activeDictionary = dictionary;

        Random rng = new Random();
        Word[] dictCont = activeDictionary.getDictionaryContents();
        hiddenWord = dictCont[rng.nextInt(activeDictionary.getWords())];
        for(Word word : dictCont) {
            if (word.length() == hiddenWord.length()) {
                candidateWords.add(word);
            }
        }
    }

    public int getCorrectTries() {
        return correctTries;
    }

    public int getTries() {
        return tries;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public Word getGuessWord() {
        return guessWord;
    }

    public Dictionary getDictionary() {
        return activeDictionary;
    }

    public Word getHiddenWord() {
        return hiddenWord;
    }

    void calcProb() {
        probs.clear();
        for (int i = 0; i < hiddenWord.length(); i++) {     //For letter at position 0
            Map<String, Double> mapProbs = new HashMap<String, Double>(); // Stores the probabilities of letters for a given position
            for (Word cWord : candidateWords) {     //of each word in candidate words
                Letter currLetter = cWord.getLetters()[i];
                if (mapProbs.containsKey(currLetter.toString())) {  // If its not contained in map then add it with occurance = 1
                    double occur = mapProbs.get(currLetter.toString());
                    mapProbs.replace(currLetter.toString(), occur + 1.);
                } else {    // else increment occurance
                    mapProbs.put(currLetter.toString(), 1.);
                }
            }
            mapProbs.replaceAll((key, value) -> value / candidateWords.size()); // Divide occurance by the number of candidate words = probability of the letter
            probs.add(mapProbs);
        }
    }
    /*
     * Method that updates the candidate set, based on the users chosen letter at a posistion(pos).
     * Returns true if the guess was correct and false when not.
     */
    boolean updateCandidates(String chosenLetter, int pos) {
        if(hiddenWord.getLetters()[pos].value().equals(chosenLetter)) {  // correct choice
            double choosenProb = probs.get(pos).get(chosenLetter);
            if (choosenProb >= 0.6) {
                score += 5;
            } else if (choosenProb >= 0.4) {
                score += 10;
            } else if (choosenProb >= 0.25) {
                score += 15;
            } else {
                score += 30;
            }
            candidateWords.removeIf(w -> (!w.getLetters()[pos].value().equals(chosenLetter)));   // remove words that have different letter at pos
            return true;
        } else {
            if (score >= 15) {
                score -= 15;
            }
            lives += 1;
            candidateWords.removeIf(w -> (w.getLetters()[pos].value().equals(chosenLetter)));   // remove words that have the same letter at pos
            return false;
        }
    }

    public String[] getCandidateLetters(int pos) {
        Object[] candidateLettersObj = probs.get(pos).keySet().toArray();
        String[] candidateLettersArr = new String[candidateLettersObj.length];
        int idx = 0;
        for (Object elem : probs.get(pos).keySet().toArray()) {
            candidateLettersArr[idx] = elem.toString();
            idx++;
        }
        return candidateLettersArr;
    }

    public Word initialize() {
        guessWord = new Word(hiddenWord.toString().replaceAll("[A-Z]", "_"));
        return guessWord;
    }

    public boolean nextState(String input, int pos) {
        tries++;
        if (updateCandidates(input, pos)) {
            correctTries++;
            guessWord.replaceLetter(input, pos);
            return true;
        } else {
            return false;
        }

    }

}
