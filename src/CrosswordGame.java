import java.util.Random;

public class CrosswordGame {
    
    private CrosswordDictionary activeDictionary;

    CrosswordGame(CrosswordDictionary activeDictionary) {
        this.activeDictionary = activeDictionary;
    }

    private static String pickHiddenWord(CrosswordDictionary dictionary) {
        Random rng = new Random();
        String hiddenWord = new String(dictionary.getDictionaryContents()[rng.nextInt(dictionary.getWords())]);
        return hiddenWord;
    }

    public static void main(String[] args) {
        CrosswordDictionary dictionary_1 = new CrosswordDictionary("https://openlibrary.org/works/OL152268W.json");
        CrosswordGame session = new CrosswordGame(dictionary_1);
    }
}
