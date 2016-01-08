import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FASTHandler extends DefaultHandler {
  private MixNMatchWriter writer;

  boolean insideRecord = false;

  // Record properties. Reset after each record!
  Integer id;
  String name;
  String nameNumeration;
  String nameTitles;
  String gender;
  Set<String> dates;
  Set<String> foreignIDs;
  Set<String> affiliation;

  String controlfieldTag = null;
  String datafieldTag = null;
  String subfieldCode = null;

  public FASTHandler(File outFile) throws FileNotFoundException {
    writer = new MixNMatchWriter(outFile);
  }

  public void startElement(String uri, String localName, String qName, Attributes attributes) {
    if ("record".equals(localName)) {
      insideRecord = true;
      id = null;
      name = null;
      nameNumeration = null;
      nameTitles = null;
      gender = null;
      dates = new HashSet<>();
      foreignIDs = new HashSet<>();
      affiliation = new HashSet<>();
    } else if ("controlfield".equals(localName)) {
      controlfieldTag = attributes.getValue("tag");
    } else if ("datafield".equals(localName)) {
      datafieldTag = attributes.getValue("tag");
    } else if ("subfield".equals(localName)) {
      subfieldCode = attributes.getValue("code");
    }
  }

  public void characters(char[] ch, int start, int length) {
    if (insideRecord) {
      if ("001".equals(controlfieldTag) && length >= 4) {
        String idString = new String(ch, start + 3, length - 3);
        if (idString.matches("[0-9]+")) {
          id = Integer.parseInt(idString);
        } else {
          System.out.println("Invalid ID: "+id);
          System.exit(0);
        }
      }
      if ("100".equals(datafieldTag)) {
        if ("a".equals(subfieldCode)) {
          name = new String(ch, start, length);
        }
        if ("b".equals(subfieldCode)) {
          nameNumeration = new String(ch, start, length);
        }
        if ("c".equals(subfieldCode)) {
          nameTitles = new String(ch, start, length);
        }
        if ("d".equals(subfieldCode)) {
          dates.add(new String(ch, start, length).trim());
        }
      }
      if ("373".equals(datafieldTag)) {
        if ("a".equals(subfieldCode)) {
          affiliation.add(new String(ch, start, length));
        }
      }
      if ("375".equals(datafieldTag)) {
        if ("a".equals(subfieldCode)) {
          gender = new String(ch, start, length);
        }
      }
      if ("400".equals(datafieldTag)) {
        if ("d".equals(subfieldCode)) {
          dates.add(new String(ch, start, length).trim());
        }
      }
      if ("700".equals(datafieldTag)) {
        if ("0".equals(subfieldCode)) {
          String id = new String(ch, start, length).trim();
          if (id.startsWith("(viaf)")) {
            id = "VIAF[" + Integer.parseInt(id.substring(6))+']';
          } else if (id.startsWith("(uri)http://viaf.org/viaf/")) {
            id = "VIAF[" + Integer.parseInt(id.substring(26))+']';
          } else if (id.startsWith("(DLC)")) {
            id = "LCAuth[" + id.substring(5).replaceAll("\\s+", "")+']';
          } else if (id.startsWith("(OCoLC)fst")) {
            id = "(replaced by FAST["+Integer.parseInt(id.substring(10))+"])";
          }
          if (id != null) {
            foreignIDs.add(id);
          }
        }
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("record".equals(localName)) {
      insideRecord = false;

      String fullName = name + (nameNumeration == null ? "" :' ' + nameNumeration) + (nameTitles == null ? "" :' ' + nameTitles);
      writer.writePerson(id, fullName, gender, dates, affiliation, foreignIDs);
    } else if ("subfield".equals(localName)) {
      subfieldCode = null;
    } else if ("datafield".equals(localName)) {
      datafieldTag = null;
    } else if ("controlfield".equals(localName)) {
      controlfieldTag = null;
    }
  }

  @Override
  public void endDocument() throws SAXException {
    try {
      writer.close();
    } catch (IOException e) {
      throw new SAXException(e);
    }
  }
}
