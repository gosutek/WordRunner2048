public abstract class CrosswordErrorHandler extends Exception{

    CrosswordErrorHandler(String str) {
        super(str);
    }
    
    public static class InvalidCountException extends CrosswordErrorHandler {

        InvalidCountException(String str) {
            super(str);
        }
    }

    public static class UndersizeException extends CrosswordErrorHandler {

        UndersizeException(String book, String numberOfWords) {
            super("Dictionary " + book + " contains less than 20 words: " + numberOfWords + " words.");
        }
    }

    public static class InvalidRangeException extends CrosswordErrorHandler {

        InvalidRangeException(String word, String length) {
            super("Word " + word + " contains less than 6 letters: " + length);
        }
    }

    public static class UnbalancedException extends CrosswordErrorHandler {

        UnbalancedException(String book, String percent) {
            super("Dictionary " + book + " contains " + percent + "% words with less than 9 letters.");
        }
    }

    public static class ConnectionException extends CrosswordErrorHandler {

        ConnectionException(String str) {
            super(str);
        }
    }

}