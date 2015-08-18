package org.jzkit.search.util.RecordBuilder;

public class RecordBuilderException extends Exception
{
  public RecordBuilderException(String reason) {
    super(reason);
  }

  public RecordBuilderException(String reason, Throwable cause) {
    super(reason,cause);
  }
}
