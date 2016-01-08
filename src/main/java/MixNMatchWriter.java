import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;

public class MixNMatchWriter {
  private BufferedWriter bw;
  private static final Pattern wikipediaPattern = Pattern.compile("\\(uri\\)http://(.*)\\.wikipedia\\.org/wiki/(.*)");
  private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
  private ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 1, TimeUnit.MINUTES, workQueue);
  private int numLinesWritten = 0;

  private boolean saxParserFinished = false;

  public MixNMatchWriter(File outFile) throws FileNotFoundException {
    bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));
  }

  public void writePerson(int id, String fullName, String gender, Set<String> dates, Set<String> affiliation, Set<String> foreignIDs) {
    writePerson(id, fullName, gender, dates, affiliation, foreignIDs, true);
  }
  public synchronized void writePerson(int id, String fullName, String gender, Set<String> dates, Set<String> affiliation, Set<String> foreignIDs, boolean fetchWikidata) {
    if (fetchWikidata) {
      for (String fid : foreignIDs) {
        Matcher wdMatcher = wikipediaPattern.matcher(fid);
        if (wdMatcher.matches()) {
          wikidataTransform(id, fullName, gender, dates, affiliation, foreignIDs);
          return;
        }
      }
    }
    try {
      bw.write(id+"\t"+(fullName.endsWith(",") ? fullName.substring(0, fullName.length()-1) : fullName)+'\t');
      if (gender != null) {
        bw.write("gender: "+gender+' ');
      }
      if (dates.size() >= 1) {
        bw.write(" birth/death: ");
        boolean isFirst = true;
        for (String date : dates) {
          bw.write((isFirst ? "" : " or ") + date);
          isFirst = false;
        }
        bw.write(' ');
      }
      if (affiliation.size() >= 1) {
        bw.write(" affiliation: ");
        boolean isFirst = true;
        for (String a : affiliation) {
          bw.write((isFirst ? "" : ", ")+a);
          isFirst = false;
        }
        bw.write(' ');
      }
      if (foreignIDs.size() >= 1) {
        bw.write(' ');
        boolean isFirst = true;
        for (String fid : foreignIDs) {
          bw.write((isFirst ? "" : ' ')+fid);
          isFirst = false;
        }
      }
      bw.write('\n');
      numLinesWritten++;
      if (numLinesWritten % 1000 == 0) {
        System.out.println(numLinesWritten+" (WD-queue "+workQueue.size()+")");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (saxParserFinished && workQueue.size() <= 0) {
      try {
        bw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void wikidataTransform(final int id, final String fullName, final String gender, final Set<String> dates, final Set<String> affiliation, final Set<String> foreignIDs) {
    Runnable r = new Runnable() {
      @Override
      public void run() {
        Set<String> newFID = new HashSet<>();
        for (String fid : foreignIDs) {
          Matcher wdMatcher = wikipediaPattern.matcher(fid);
          if (wdMatcher.matches()) {
            String wdID;
            try {
              wdID = WikidataUtil.getWikidataID(wdMatcher.group(1)+"wiki", URLDecoder.decode(wdMatcher.group(2), "UTF-8"));
              if (wdID != null && wdID.matches("Q[1-9][0-9]*")) {
                newFID.add("Wikidata[" + wdID+']');
              } else {
                newFID.add(fid);
              }
            } catch (JSONException | IOException e) {
              e.printStackTrace();
            }
          } else {
            newFID.add(fid);
          }
        }
        writePerson(id, fullName, gender, dates, affiliation, newFID, false);
      }
    };
    executor.execute(r);
  }

  public void close() throws IOException {
    saxParserFinished = true;
  }
}
