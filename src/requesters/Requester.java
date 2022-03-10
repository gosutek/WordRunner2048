package requesters;

import java.io.IOException;

import utils.ErrorHandler;

/**
 * Abstract http requester. Implemented by {@link requesters.SubjectRequester SubjectRequester},{@link requesters.WorksRequester WorksRequester}
 * @see requesters.SubjectRequester
 * @see requesters.WorksRequester
 */

abstract class Requester {
    abstract String[] readFromURL(String reqURL) throws IOException, ErrorHandler.ConnectionException;
}
