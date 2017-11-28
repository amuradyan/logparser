package logparser;

import java.io.IOException;

public class Main {

  public static void main(String[] args) {
    long then = System.currentTimeMillis();

    if (args.length != 2) {
      System.out.println("Run -h for help");
    } else {
      try {
        String logFilePath = args[0];
        Integer topN = Integer.parseInt(args[1]);

        Log log  = Log.create(logFilePath);

        try {
          log.process();
        } catch (IOException e) {
          System.out.println(e.getLocalizedMessage());
        }
      } catch (NumberFormatException e) {
        System.out.println("Illegal argument. Run -h for help");
      }
    }

    System.out.println("___________________________________");
    long now = System.currentTimeMillis();
    System.out.println("\n\n3. Execution time: ~" + (now - then) / 1000 + " seconds");
  }
}
