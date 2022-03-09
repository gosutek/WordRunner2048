package dictionary;

import java.util.ArrayList;

/**
 * Used with the dictionary.Letter class to form the words of the dictionary
 * @see dictionary.Letter
 */

public class Word {

    private Letter[] letters;
    private int wordLength;

    public Word(String str) {
        ArrayList<Letter> letters = new ArrayList<Letter>();
        wordLength = str.length();
        String[] splitStr = str.split(""); /* Split at every character */

        for (int i = 0; i < wordLength; i++) {
            letters.add(new Letter(splitStr[i], i, this)); /* Pass value , position and word obj */
        }
        this.letters = letters.toArray(new Letter[wordLength]);
    }

    @Override
    public String toString() {
        String str = "";
        for (Letter letter : letters) {
            str += letter.value();
        }
        return str;
    }
    @Override
    public boolean equals(Object o) {
        Word other = (Word) o;
        for (int i = 0; i < this.wordLength; i++) {
            if (!this.letters[i].equals(other.letters[i])) {
                return false;
            }
        }
        return true;
    }

    public int length() {
        return wordLength;
    }

    public Letter[] getLetters() {
        return letters;
    }

    /**
     * Replaces the letter of {@code this} word at a given pos.
     * @param letter the letter to replace.
     * @param pos the position of the letter to be replaced.
     */

    public void replaceLetter(String letter, int pos) {
        letters[pos] = new Letter(letter, pos, this);
    }
}
