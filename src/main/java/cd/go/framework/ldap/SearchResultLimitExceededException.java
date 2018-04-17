package cd.go.framework.ldap;

import java.text.MessageFormat;

public class SearchResultLimitExceededException extends RuntimeException {
    private final int maxResult;
    private final String searchBase;

    public SearchResultLimitExceededException(int maxResult, String searchBase) {
        super(MessageFormat.format("Search results limit exceeded. Expected search to return {0} results but found more in search base {1}.", maxResult, searchBase));
        this.maxResult = maxResult;
        this.searchBase = searchBase;
    }

    public int getMaxResult() {
        return maxResult;
    }

    public String getSearchBase() {
        return searchBase;
    }
}
