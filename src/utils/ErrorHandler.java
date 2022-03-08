package utils;
public abstract class ErrorHandler extends Exception{

    public ErrorHandler(String str) {
        super(str);
    }
    
    public static class InvalidCountException extends ErrorHandler {

        public InvalidCountException(String str) {
            super("Word " + str + "is contained more than once");
        }
    }

    public static class UndersizeException extends ErrorHandler {

        public UndersizeException(String book, String numberOfWords) {
            super("Dictionary " + book + " contains less than 20 words: " + numberOfWords + " words.");
        }
    }

    public static class InvalidRangeException extends ErrorHandler {

        public InvalidRangeException(String word, String length) {
            super("Word " + word + " contains less than 6 letters: " + length);
        }
    }

    public static class UnbalancedException extends ErrorHandler {

        public UnbalancedException(String book, String percent) {
            super("Dictionary " + book + " contains " + percent + "% words with less than 9 letters.");
        }
    }

    public static class ConnectionException extends ErrorHandler {

        public ConnectionException(String str) {
            super(str);
        }
    }

    public static class BannedWordException extends ErrorHandler {

        public BannedWordException(String book, String str) {
            super("Dictionary " + book + " contains a banned word: " + str);
        }
    }

}