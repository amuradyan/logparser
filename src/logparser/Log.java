package logparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by spectrum on 11/28/2017.
 */
public class Log {
  private File log;
  private List<Record> records = new ArrayList<>();

  private Log() {
  }

  public static Log create(String logFilePath){

    File logFile = new File(logFilePath);

    Log log = new Log();
    log.setLog(logFile);

    return log;
  }

  public void setLog(File log) {
    this.log = log;
  }

  public void process() throws IOException {
    readFileByLine();
  }

  private void readFileByLine() throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(log))) {
      String line;
      Integer lineNumber = 0;

      while ((line = br.readLine()) != null) {
        lineNumber++;
        try {
          records.add(Record.parseFromLogLine(line));
        } catch (LogLineParseException e) {
          System.err.println("Unable to convert line " + lineNumber + " to record");
        }

        System.out.println(line);
      }

      records.sort((o1, o2) -> o1.getTime().compareTo(o2.getTime()));

      drawHistogram();
    }
  }

  private void drawHistogram(){
    System.out.println("\n\n2. Histogram");

    drawHistogramHeader();
    drawHistogramRows();
  }

  private void drawHistogramRows() {
    LocalDateTime currentTime = null;
    LinkedList<Record> recordInSameHour = new LinkedList<>();

    for (Record record : records) {
      if (currentTime == null) {
        currentTime = record.getTime();
        recordInSameHour.addLast(record);
      }
      else if(currentTime.toLocalDate().equals(record.getTime().toLocalDate()) &&
              currentTime.getHour() == record.getTime().getHour())
      {
        recordInSameHour.addLast(record);
      }
      else {
        renderHistogramRow(recordInSameHour);

        currentTime = record.getTime();
        recordInSameHour.clear();
        recordInSameHour.addLast(record);
      }
    }

    renderHistogramRow(recordInSameHour);
  }

  private void drawHistogramHeader() {
    System.out.println("Date and Time           | Number of requests / 10 ");
    System.out.println("yyyy-MM-dd HH:(mm - mm) |                         ");
    System.out.println("--------------------------------------------------");
  }

  private void renderHistogramRow(LinkedList<Record> recordInSameHour) {
    String dateTime = String.format("%tY-%td-%td %tH:(%tM - %tM)", recordInSameHour.getFirst().getTime(),
                                                                  recordInSameHour.getFirst().getTime(),
                                                                  recordInSameHour.getFirst().getTime(),
                                                                  recordInSameHour.getFirst().getTime(),
                                                                  recordInSameHour.getFirst().getTime(),
                                                                  recordInSameHour.getLast().getTime());

    Long requestsDiv10 = Math.round(recordInSameHour.size() / 10 + 0.5);

    String requestDiv10Chart = String.join("", Collections.nCopies(requestsDiv10.intValue(), "|"));

    System.out.println(String.format("%s | %s", dateTime, requestDiv10Chart));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Log log1 = (Log) o;

    return log.equals(log1.log);
  }

  @Override
  public int hashCode() {
    return log.hashCode();
  }

  @Override
  public String toString() {
    return "Log{" +
        "log=" + log +
        '}';
  }
}
