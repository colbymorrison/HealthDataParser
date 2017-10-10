/**
 * @author Colby Morrison
 */ 
package parsehealthxml;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class XMLtoSpreadsheet{
    
    private final static String TYPENAME = "HKQuantityTypeIdentifierDistanceWalkingRunning";
    private final static String DATESDOC = "dates.txt";
    private final static String VALUESDOC = "values.txt";
    
    /**
     * Takes an XML file and returns a DOM tree.
     * @param f an XML File.
     * @return A Document type DOM tree. 
     */
    private static Document parseXML(File f){
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
            return dbBuilder.parse(f);
        }
        catch (IOException | ParserConfigurationException | SAXException e){
            System.out.println(e);
            return null;
        }
    }
    
    /**
     * Returns the text of an attribute of the DOM tree.
     * @param n the node that contains the attribute.
     * @param namedItem the name of the attribute.
     * @return The text of the attribute.
     */
    private static String attributeText(Node n, String namedItem){
        return n.getAttributes().getNamedItem(namedItem).getTextContent();
    }  
    
    /**
     * Writes the contents of an Object to a file.
     * @param file pathname to the file.
     * @param content the content to be written.
     * @throws IOException 
     */
    private static void stringToFile(String file, Object content)
            throws IOException{   
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(content + "\n");

        }
    }

    private static void valuesToFiles(NodeList list) throws IOException{
        Double count = 0.0;

        for(int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            String startDateText = attributeText(n, "startDate");
            if (attributeText(n, "type").equals(TYPENAME)) {
                if (startDateText.regionMatches(false, 0,
                        attributeText(list.item(i + 1), "startDate"), 0, 10))
                    count += Double.parseDouble(attributeText(n, "value"));
                else {
                    count += Double.parseDouble(attributeText(n, "value"));
                    stringToFile(DATESDOC,startDateText.substring(0, 10));
                    double count_round = (double) Math.round(count * 100d) / 100d;
                    stringToFile(VALUESDOC, count_round);
                    count = 0.0;
                }
            }
        }
    }
    
    /**
     * Writes the desired words to the desired files.
     * @param args the command line arguments
     * @throws IOException, NullPointerException
     */
    public static void main(String[] args) throws IOException, NullPointerException {
        Document doc = parseXML(new File(
                "/Users/Colby/Documents/Java/export.xml"));

            doc.getDocumentElement().normalize();
            NodeList list = doc.getElementsByTagName("Record");
            valuesToFiles(list);
        }
    }

    
    

