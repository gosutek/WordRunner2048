package requesters;

abstract class Requester {
    
    abstract String[] readFromURL(String reqURL);
    abstract String[] getResults();
}
