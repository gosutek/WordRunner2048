package requesters;

/**
 * Abstract http requester. Implemented by {@link requesters.SubjectRequester SubjectRequester},{@link requesters.WorksRequester WorksRequester}
 * @see requesters.SubjectRequester
 * @see requesters.WorksRequester
 */

abstract class Requester {
    
    abstract String[] readFromURL(String reqURL);
    abstract String[] getResults();
}
