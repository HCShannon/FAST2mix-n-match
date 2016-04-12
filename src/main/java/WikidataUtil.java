import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

public final class WikidataUtil {
  private static final String[] WIKIS = new String[]{
    "aawikibooks", "afwikibooks", "afwikiquote", "akwikibooks", "alswikibooks", "alswikiquote", "amwikiquote", "angwikibooks",
    "angwikiquote", "angwikisource", "arwikibooks", "arwikinews", "arwikiquote", "arwikisource", "astwikibooks", "astwikiquote",
    "aswikibooks", "aswikisource", "aywikibooks", "azwikibooks", "azwikiquote", "azwikisource", "bawikibooks", "bewikibooks",
    "bewikiquote", "bewikisource", "bgwikibooks", "bgwikinews", "bgwikiquote", "bgwikisource", "biwikibooks", "bmwikibooks",
    "bmwikiquote", "bnwikibooks", "bnwikisource", "bowikibooks", "brwikiquote", "brwikisource", "bswikibooks", "bswikinews",
    "bswikiquote", "bswikisource", "cawikibooks", "cawikinews", "cawikiquote", "cawikisource", "chwikibooks", "commonswiki",
    "cowikibooks", "cowikiquote", "crwikiquote", "cswikibooks", "cswikinews", "cswikiquote", "cswikisource", "cvwikibooks",
    "cywikibooks", "cywikiquote", "cywikisource", "dawikibooks", "dawikiquote", "dawikisource", "dewikibooks", "dewikinews",
    "dewikiquote", "dewikisource", "dewikivoyage", "elwikibooks", "elwikinews", "elwikiquote", "elwikisource", "elwikivoyage",
    "enwikibooks", "enwikinews", "enwikiquote", "enwikisource", "enwikivoyage", "eowikibooks", "eowikinews", "eowikiquote",
    "eowikisource", "eswikibooks", "eswikinews", "eswikiquote", "eswikisource", "eswikivoyage", "etwikibooks", "etwikiquote",
    "etwikisource", "euwikibooks", "euwikiquote", "fawikibooks", "fawikinews", "fawikiquote", "fawikisource", "fawikivoyage",
    "fiwikibooks", "fiwikinews", "fiwikiquote", "fiwikisource", "fowikisource", "frwikibooks", "frwikinews", "frwikiquote",
    "frwikisource", "frwikivoyage", "fywikibooks", "gawikibooks", "gawikiquote", "glwikibooks", "glwikiquote", "glwikisource",
    "gnwikibooks", "gotwikibooks", "guwikibooks", "guwikiquote", "guwikisource", "hewikibooks", "hewikinews", "hewikiquote",
    "hewikisource", "hewikivoyage", "hiwikibooks", "hiwikiquote", "hrwikibooks", "hrwikiquote", "hrwikisource", "htwikisource",
    "huwikibooks", "huwikinews", "huwikiquote", "huwikisource", "hywikibooks", "hywikiquote", "hywikisource", "iawikibooks",
    "idwikibooks", "idwikiquote", "idwikisource", "iewikibooks", "iswikibooks", "iswikiquote", "iswikisource", "itwikibooks",
    "itwikinews", "itwikiquote", "itwikisource", "itwikivoyage", "jawikibooks", "jawikinews", "jawikiquote", "jawikisource",
    "kawikibooks", "kawikiquote", "kkwikibooks", "kkwikiquote", "kmwikibooks", "knwikibooks", "knwikiquote", "knwikisource",
    "kowikibooks", "kowikinews", "kowikiquote", "kowikisource", "krwikiquote", "kswikibooks", "kswikiquote", "kuwikibooks",
    "kuwikiquote", "kwwikiquote", "kywikibooks", "kywikiquote", "lawikibooks", "lawikiquote", "lawikisource", "lbwikibooks",
    "lbwikiquote", "liwikibooks", "liwikiquote", "liwikisource", "lnwikibooks", "ltwikibooks", "ltwikiquote", "ltwikisource",
    "lvwikibooks", "mediawikiwiki", "metawiki", "mgwikibooks", "miwikibooks", "mkwikibooks", "mkwikisource", "mlwikibooks",
    "mlwikiquote", "mlwikisource", "mnwikibooks", "mrwikibooks", "mrwikiquote", "mrwikisource", "mswikibooks", "mywikibooks",
    "nahwikibooks", "nawikibooks", "nawikiquote", "ndswikibooks", "ndswikiquote", "newikibooks", "nlwikibooks", "nlwikinews",
    "nlwikiquote", "nlwikisource", "nlwikivoyage", "nnwikiquote", "nowikibooks", "nowikinews", "nowikiquote", "nowikisource",
    "ocwikibooks", "orwikisource", "pawikibooks", "plwikibooks", "plwikinews", "plwikiquote", "plwikisource", "plwikivoyage",
    "pswikibooks", "ptwikibooks", "ptwikinews", "ptwikiquote", "ptwikisource", "ptwikivoyage", "quwikibooks", "quwikiquote",
    "rmwikibooks", "rowikibooks", "rowikinews", "rowikiquote", "rowikisource", "rowikivoyage", "ruwikibooks", "ruwikinews",
    "ruwikiquote", "ruwikisource", "ruwikivoyage", "sahwikisource", "sawikibooks", "sawikiquote", "sawikisource", "sdwikinews",
    "sewikibooks", "simplewikibooks", "simplewikiquote", "siwikibooks", "skwikibooks", "skwikiquote", "skwikisource",
    "slwikibooks", "slwikiquote", "slwikisource", "specieswiki", "sqwikibooks", "sqwikinews", "sqwikiquote", "srwikibooks",
    "srwikinews", "srwikiquote", "srwikisource", "suwikibooks", "suwikiquote", "svwikibooks", "svwikinews", "svwikiquote",
    "svwikisource", "svwikivoyage", "swwikibooks", "tawikibooks", "tawikinews", "tawikiquote", "tawikisource", "tewikibooks",
    "tewikiquote", "tewikisource", "tgwikibooks", "thwikibooks", "thwikinews", "thwikiquote", "thwikisource", "tkwikibooks",
    "tkwikiquote", "tlwikibooks", "trwikibooks", "trwikinews", "trwikiquote", "trwikisource", "ttwikibooks", "ttwikiquote",
    "ugwikibooks", "ugwikiquote", "ukwikibooks", "ukwikinews", "ukwikiquote", "ukwikisource", "ukwikivoyage", "urwikibooks",
    "urwikiquote", "uzwikibooks", "uzwikiquote", "vecwikisource", "viwikibooks", "viwikiquote", "viwikisource", "viwikivoyage",
    "vowikibooks", "vowikiquote", "wawikibooks", "wikidatawiki", "wowikiquote", "xhwikibooks", "yiwikisource", "yowikibooks",
    "zawikibooks", "zawikiquote", "zh_min_nanwikibooks", "zh_min_nanwikiquote", "zh_min_nanwikisource", "zhwikibooks",
    "zhwikinews", "zhwikiquote", "zhwikisource", "zhwikivoyage", "zuwikibooks", "aawiki", "abwiki", "acewiki", "afwiki", "akwiki",
    "alswiki", "amwiki", "anwiki", "angwiki", "arwiki", "arcwiki", "arzwiki", "aswiki", "astwiki", "avwiki", "aywiki", "azwiki",
    "bawiki", "barwiki", "bat_smgwiki", "bclwiki", "bewiki", "be_x_oldwiki", "bgwiki", "bhwiki", "biwiki", "bjnwiki", "bmwiki",
    "bnwiki", "bowiki", "bpywiki", "brwiki", "bswiki", "bugwiki", "bxrwiki", "cawiki", "cbk_zamwiki", "cdowiki", "cewiki", "cebwiki",
    "chwiki", "chowiki", "chrwiki", "chywiki", "ckbwiki", "cowiki", "crwiki", "crhwiki", "cswiki", "csbwiki", "cuwiki", "cvwiki",
    "cywiki", "dawiki", "dewiki", "diqwiki", "dsbwiki", "dvwiki", "dzwiki", "eewiki", "elwiki", "emlwiki", "enwiki", "eowiki", "eswiki",
    "etwiki", "euwiki", "extwiki", "fawiki", "ffwiki", "fiwiki", "fiu_vrowiki", "fjwiki", "fowiki", "frwiki", "frpwiki", "frrwiki",
    "furwiki", "fywiki", "gawiki", "gagwiki", "ganwiki", "gdwiki", "glwiki", "glkwiki", "gnwiki", "gotwiki", "guwiki", "gvwiki",
    "hawiki", "hakwiki", "hawwiki", "hewiki", "hiwiki", "hifwiki", "howiki", "hrwiki", "hsbwiki", "htwiki", "huwiki", "hywiki",
    "hzwiki", "iawiki", "idwiki", "iewiki", "igwiki", "iiwiki", "ikwiki", "ilowiki", "iowiki", "iswiki", "itwiki", "iuwiki", "jawiki",
    "jbowiki", "jvwiki", "kawiki", "kaawiki", "kabwiki", "kbdwiki", "kgwiki", "kiwiki", "kjwiki", "kkwiki", "klwiki", "kmwiki",
    "knwiki", "kowiki", "koiwiki", "krwiki", "krcwiki", "kswiki", "kshwiki", "kuwiki", "kvwiki", "kwwiki", "kywiki", "lawiki",
    "ladwiki", "lbwiki", "lbewiki", "lezwiki", "lgwiki", "liwiki", "lijwiki", "lmowiki", "lnwiki", "lowiki", "ltwiki", "ltgwiki",
    "lvwiki", "maiwiki", "map_bmswiki", "mdfwiki", "mgwiki", "mhwiki", "mhrwiki", "miwiki", "minwiki", "mkwiki", "mlwiki", "mnwiki",
    "mowiki", "mrwiki", "mrjwiki", "mswiki", "mtwiki", "muswiki", "mwlwiki", "mywiki", "myvwiki", "mznwiki", "nawiki", "nahwiki",
    "napwiki", "ndswiki", "nds_nlwiki", "newiki", "newwiki", "ngwiki", "nlwiki", "nnwiki", "nowiki", "novwiki", "nrmwiki", "nsowiki",
    "nvwiki", "nywiki", "ocwiki", "omwiki", "orwiki", "oswiki", "pawiki", "pagwiki", "pamwiki", "papwiki", "pcdwiki", "pdcwiki",
    "pflwiki", "piwiki", "pihwiki", "plwiki", "pmswiki", "pnbwiki", "pntwiki", "pswiki", "ptwiki", "quwiki", "rmwiki", "rmywiki",
    "rnwiki", "rowiki", "roa_rupwiki", "roa_tarawiki", "ruwiki", "ruewiki", "rwwiki", "sawiki", "sahwiki", "scwiki", "scnwiki",
    "scowiki", "sdwiki", "sewiki", "sgwiki", "shwiki", "siwiki", "simplewiki", "skwiki", "slwiki", "smwiki", "snwiki", "sowiki",
    "sqwiki", "srwiki", "srnwiki", "sswiki", "stwiki", "stqwiki", "suwiki", "svwiki", "swwiki", "szlwiki", "tawiki", "tewiki",
    "tetwiki", "tgwiki", "thwiki", "tiwiki", "tkwiki", "tlwiki", "tnwiki", "towiki", "tpiwiki", "trwiki", "tswiki", "ttwiki", "tumwiki",
    "twwiki", "tywiki", "tyvwiki", "udmwiki", "ugwiki", "ukwiki", "urwiki", "uzwiki", "vewiki", "vecwiki", "vepwiki", "viwiki",
    "vlswiki", "vowiki", "wawiki", "warwiki", "wowiki", "wuuwiki", "xalwiki", "xhwiki", "xmfwiki", "yiwiki", "yowiki", "zawiki",
    "zeawiki", "zhwiki", "zh_classicalwiki", "zh_min_nanwiki", "zh_yuewiki", "zuwiki", "lrcwiki", "gomwiki", "azbwiki",
  };
  public static final String getWikidataID(final String wikiName, final String pageTitle) throws IOException, JSONException {
    if (wikiName == null || !Arrays.asList(WIKIS).contains(wikiName)) {
      throw new IllegalArgumentException(String.format("Invalid wiki name '%s'", wikiName));
    }
    try {
      URL u = new URL("https://www.wikidata.org/w/api.php?action=wbgetentities&format=json&sites="+wikiName+"&titles="+URLEncoder.encode(""+pageTitle, "UTF-8")+"&props=info&redirects=yes&normalize=");
      URLConnection c = u.openConnection();
      c.setRequestProperty("User-Agent", "FAST2mixnmatch/0.1");
      BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(c.getInputStream())));
      StringBuilder stringResult = new StringBuilder();
      String buffer = "";
      while ((buffer = reader.readLine()) != null) {
        stringResult.append(buffer);
      }
      JSONObject jsonResult = new JSONObject(stringResult.toString());
      if (jsonResult.getInt("success") == 1) {
        String[] ids =  JSONObject.getNames(jsonResult.getJSONObject("entities"));
        if (ids != null && ids.length >= 1) {
          if (ids.length > 1) {
            throw new IllegalArgumentException("More than one Wikidata ID for '"+pageTitle+"' found");
          }
          return ids[0];
        }
      } else {
        System.out.println("Query to wikidata server was unsuccessful!");
      }

    } catch (UnsupportedEncodingException | MalformedURLException e) {
      System.out.println("This should normally not happen! Either the encoding is unsupported or the URL malformed: "+e.getClass().getName());
    }
    return null;
  }
}
