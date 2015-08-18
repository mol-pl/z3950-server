package org.jzkit.search.util.QueryFormatter;

public class QueryFormatterException extends Exception {

  public QueryFormatterException() {
    super();
  }

  public QueryFormatterException(String reason) {
    super(reason);
  }

  public QueryFormatterException(String reason, Throwable cause) {
    super(reason,cause);
  }
}
