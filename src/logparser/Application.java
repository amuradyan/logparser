package logparser;

import java.io.IOException;

public class Application {

  public static void main(String[] args) {
    long then = System.currentTimeMillis();

    if(args.length == 1){
      switch (args[0]){
        case "-sw":
          System.out.println("Star wars");
          break;
        default:
          printHelp();
      }
    } else if (args.length == 2) {
      try {
        String logFilePath = args[0];
        Integer topN = Integer.parseInt(args[1]);

        try {
          Log log  = Log.create(logFilePath);

          log.printTopNResourcesWithHighestAverageRequestDuration(topN);
          log.drawHistogram();
        } catch (IOException e) {
          System.out.println(e.getLocalizedMessage());
        }
      } catch (NumberFormatException e) {
        System.err.println("Illegal argument. Run with -h for help");
      }
    } else {
      printHelp();
    }

    long now = System.currentTimeMillis();
    System.out.println("\n\n3. Execution time: ~" + (now - then) / 1000 + " seconds");
  }

  private static void printHelp() {
    System.out.println("LogParser");
    System.out.println();
    System.out.println("Usage: java -jar logparser.jar <log file path> <n>");
    System.out.println("n - Number of top resources with highest average request duration to be printed");
  }
}
