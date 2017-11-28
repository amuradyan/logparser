package logparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import logparser.exception.LogLineParseException;

/**
 * Created by spectrum on 11/28/2017.
 */
public class Log {
  private List<Record> allRecords = new ArrayList<>();
  Map<Record, Integer> resourcesSortedByAverageRequestDuration = new HashMap<>();

  private Log() {}

  public static Log create(String logFilePath) throws IOException {
    List<Record> allRecords = new ArrayList<>();
    Map<Record, Integer>  resourcesToAverageRequestDuration = new HashMap<>();
    Map<Record, Integer>  resourcesToOccurrence = new HashMap<>();

    File logFile = new File(logFilePath);

    try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
      String line;
      Integer lineNumber = 0;

      while ((line = br.readLine()) != null) {
        lineNumber++;
        try {
          Record record = Record.parseFromLogLine(line);
          allRecords.add(record);

          if (record.isResource()) {
            Integer pastOccurrences = resourcesToOccurrence.get(record);

            if(pastOccurrences == null){
              resourcesToAverageRequestDuration.put(record, record.getRequestDuration());
              resourcesToOccurrence.put(record, 1);
            } else {
              Integer computedAverage = resourcesToAverageRequestDuration.get(record);
              Integer updatedAverage =
                  computedAverage + (record.getRequestDuration() - computedAverage) / pastOccurrences;

              resourcesToAverageRequestDuration.put(record, updatedAverage);
              resourcesToOccurrence.put(record, pastOccurrences++);
            }
          }

        } catch (LogLineParseException e) {
          System.err.println("Unable to convert line " + lineNumber + " to record");
        }
      }

      allRecords.sort((o1, o2) -> o1.getTime().compareTo(o2.getTime()));
    }

    Log log = new Log();
    log.setAllRecords(allRecords);
    log.setResourcesSortedByAverageRequestDuration(sortByValue(resourcesToAverageRequestDuration));

    return log;
  }

  public void setAllRecords(List<Record> allRecords) {
    this.allRecords = allRecords;
  }

  public void setResourcesSortedByAverageRequestDuration(Map<Record, Integer> resourcesSortedByAverageRequestDuration) {
    this.resourcesSortedByAverageRequestDuration = resourcesSortedByAverageRequestDuration;
  }

  public void printTopNResourcesWithHighestAverageRequestDuration(Integer topN) {
    System.out.println("\n\n1. Top " + topN + " resources with highest average request duration");
    System.out.println();

    Integer threshold = topN > resourcesSortedByAverageRequestDuration.size() ?
        resourcesSortedByAverageRequestDuration.size() : topN;

    Integer counter = 0;
    for (Map.Entry<Record, Integer> entry : resourcesSortedByAverageRequestDuration.entrySet()) {
      if (counter == threshold)
        break;

      counter++;
      System.out.println(entry.getValue() + " ms - " + entry.getKey());
    }
  }

  public void drawHistogram(){
    System.out.println("\n\n2. Histogram");

    drawHistogramHeader();
    drawHistogramRows();
  }

  private void drawHistogramHeader() {
    System.out.println("Date and Time           | Number of requests / 10 ");
    System.out.println("yyyy-MM-dd HH:(mm - mm) |                         ");
    System.out.println("--------------------------------------------------");
  }

  private void drawHistogramRows() {
    LocalDateTime currentTime = null;
    LinkedList<Record> recordInSameHour = new LinkedList<>();

    for (Record record : allRecords) {
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

  private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
    return map.entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }
}
