package logparser;

/**
 * Created by spectrum on 11/28/2017.
 */
public class LogLineParseException extends Exception {
  public LogLineParseException(Throwable cause) {
    super("Unable to create request record from log line", cause);
  }
}
