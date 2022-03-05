package dictionary;

public class Letter {
    
    private String value;
    private Word word;
    private int pos;

    Letter(String letter, int pos, Word word) {
        value = letter;
        this.pos = pos;
        this.word = word;
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
