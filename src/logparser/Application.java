package logparser;

import java.io.IOException;

public class Application {

  public static void main(String[] args) {
    long then = System.currentTimeMillis();

    printHeader();

    if (args.length == 2) {
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
    System.out.println("\n\n3. Execution time: ~" + (now - then) + " milliseconds");
  }

  private static void printHelp() {
    System.out.println("LogParser");
    System.out.println();
    System.out.println("Usage: java -jar logparser.jar <log file path> <n>");
    System.out.println("n - Number of top resources with highest average request duration to be printed");
  }

  private static void printHeader(){
    System.out.println(" __        ______     _______ .______      ___      .______          _______. _______ .______      ");
    System.out.println("|  |      /  __  \\   /  _____||   _  \\    /   \\     |   _  \\        /       ||   ____||   _  \\     ");
    System.out.println("|  |     |  |  |  | |  |  __  |  |_)  |  /  ^  \\    |  |_)  |      |   (----`|  |__   |  |_)  |    ");
    System.out.println("|  |     |  |  |  | |  | |_ | |   ___/  /  /_\\  \\   |      /        \\   \\    |   __|  |      /     ");
    System.out.println("|  `----.|  `--'  | |  |__| | |  |     /  _____  \\  |  |\\  \\----.----)   |   |  |____ |  |\\  \\----.");
    System.out.println("|_______| \\______/   \\______| | _|    /__/     \\__\\ | _| `._____|_______/    |_______|| _| `._____|");
  }
}
