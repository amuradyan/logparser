package logparser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import logparser.exception.LogLineParseException;

/**
 * Created by spectrum on 11/28/2017.
 */
public class Record {
  private LocalDateTime time;
  private String threadId;
  private UserContext userContext;
  private Integer requestDuration;
  private String endpointUrl;
  private List<String> payload = new ArrayList<>();

  private Record() {}

  public static Record parseFromLogLine(String logLine) throws LogLineParseException {
    Record record = new Record();

    List<String> tokens = Arrays.asList(logLine.split(" "));

    if (!tokens.isEmpty()){
      String fullTime = tokens.get(0) + " " + tokens.get(1);
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");
      record.setTime(LocalDateTime.parse(fullTime, formatter));

      record.setThreadId(tokens.get(2).substring(1, tokens.get(2).length() - 1));

      String[] userContext = (tokens.get(3)).split(":");
      if(userContext.length == 2){
        record.setUserContext(new UserContext(userContext[0], userContext[1]));
      } else {
        record.setUserContext(new UserContext());
      }

      record.setEndpointUrl(tokens.get(4));

      if (!tokens.get(5).equals("in"))
        record.setPayload(tokens.subList(5, tokens.size() - 2));

      try {
        Integer requestDuration = Integer.parseInt(tokens.get(tokens.size() - 1));

        record.setRequestDuration(requestDuration);
      } catch (NumberFormatException e) {
        throw new LogLineParseException(e);
      }
    }

    return record;
  }

  public boolean isResource() {
    return endpointUrl.startsWith("get") || endpointUrl.startsWith("update") ||
           endpointUrl.startsWith("/load") || endpointUrl.startsWith("/new") ||
           endpointUrl.contains(".pdf");
  }

  public List<String> getPayload() {
    return payload;
  }

  public void setPayload(List<String> payload) {
    this.payload = payload;
  }

  public String getEndpointUrl() {
    return endpointUrl;
  }

  public void setEndpointUrl(String endpointUrl) {
    this.endpointUrl = endpointUrl;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }

  public String getThreadId() {
    return threadId;
  }

  public void setThreadId(String threadId) {
    this.threadId = threadId;
  }

  public UserContext getUserContext() {
    return userContext;
  }

  public void setUserContext(UserContext userContext) {
    this.userContext = userContext;
  }

  public Integer getRequestDuration() {
    return requestDuration;
  }

  public void setRequestDuration(Integer requestDuration) {
    this.requestDuration = requestDuration;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Record record = (Record) o;

    if (!time.equals(record.time)) return false;
    if (!threadId.equals(record.threadId)) return false;
    if (userContext != null ? !userContext.equals(record.userContext) : record.userContext != null) return false;
    return requestDuration.equals(record.requestDuration);
  }

  @Override
  public int hashCode() {
    int result = time.hashCode();
    result = 31 * result + threadId.hashCode();
    result = 31 * result + (userContext != null ? userContext.hashCode() : 0);
    result = 31 * result + requestDuration.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Record{" +
        "time=" + time +
        ", threadId='" + threadId + '\'' +
        ", userContext=" + userContext +
        ", requestDuration=" + requestDuration +
        '}';
  }
}
