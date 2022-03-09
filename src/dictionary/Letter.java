package dictionary;

/**
 * Used with the dictionary.Word class to keep track of letters in a word. Basically extends the {@code String} class.
 * @see dictionary.Word
 */

public class Letter {
    
    private String value;

    Letter(String letter, int pos, Word word) {
        value = letter;
    }

    @Override
    public String toString() {
        return value;
    }
    @Override
    public boolean equals(Object o) {
        Letter other = (Letter) o;
        return this.value.equals(other.value);
    }

    public String value() {
        return value;
    }
}
