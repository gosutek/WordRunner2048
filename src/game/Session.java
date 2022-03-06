package game;

import dictionary.*;

import java.util.Random;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;



public class Session {
    private Dictionary activeDictionary;
    private Word hiddenWord, guessWord;
    private ArrayList<Word> candidateWords = new ArrayList<Word>();
    private List<Map<String, Double>> probs = new ArrayList<Map<String, Double>>(); // Maps probabilities of letters for all positions
    private int score, lives = 0;


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

    protected Word getHiddenWord() {
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
        //probs.forEach(elem -> System.out.println(elem.values()));
        //probs.forEach(elem -> System.out.println(elem.keySet()));
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

    void play() {
        String input = new String();
        Integer pos;
        Scanner scanner = new Scanner(System.in);
        guessWord = new Word(hiddenWord.toString().replaceAll("[A-Z]", "_"));
        System.out.println(hiddenWord);
        System.out.println(candidateWords);
        while(!guessWord.equals(hiddenWord)) {
            calcProb();
            getCandidateLetters(0);
            System.out.println("Input letter: ");
            input = scanner.next();
            System.out.println("Input pos: ");
            
            pos = scanner.nextInt();
            while (!(pos instanceof Integer)) {
                System.out.println("Posision must be a number");
                pos = scanner.nextInt();
            }
            if (updateCandidates(input, pos)) {
                System.out.println("Correct!");
                guessWord.replaceLetter(input, pos);

            } else {
                System.out.println("False!");
            }
            if (lives >= 6) {
                System.out.println("Game over");
                System.out.println("Hidden word was: " + hiddenWord);
                break;
            }
            System.out.println(candidateWords);
        }
        if (guessWord.equals(hiddenWord)) {
            System.out.println("You win!");
        }
        scanner.close();
    }

}
