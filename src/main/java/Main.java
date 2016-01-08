import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

public class Main {
  public static void main(String... args) throws URISyntaxException, SAXException, IOException, ParserConfigurationException {
    File marcFile = new File(Main.class.getResource("/FASTAll.marcxml/FASTPersonal.marcxml").toURI());
    SAXParserFactory pFac = SAXParserFactory.newInstance();
    pFac.setNamespaceAware(true);
    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(marcFile));
    pFac.newSAXParser().parse(bis, new FASTHandler(
        new File(marcFile.getName().substring(4, marcFile.getName().length()-8)+".tsv")
    ));
  }
}
