/*
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

import java.io.*;

public class XMLtoSpreadsheet{

    private static String valuesDoc;
    
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
     * Appends the contents of an Object to a file.
     * @param content the content to be written.
     * @param line indicates weather or not to put a new line.
     * @throws IOException
     */
    private static void stringToFile(Object content, Boolean line) throws IOException{
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(valuesDoc, true))) {
            if(line) bw.write(content.toString() + "\n");
            else bw.write(content.toString());

        }
    }

    /**
     * Writes a date representation and a count value to the file
     * @param date representation of a date
     * @param count the count
     * @throws IOException
     */
    private static void dateCountToFile(String date, Double count) throws IOException{
        stringToFile(date.substring(0, 10) + "   ", false);
        double count_round = (double) Math.round(count * 100d) / 100d;
        stringToFile(count_round, true);
    }

    /**
     * Method for writing values, types, and dates to the file
     * @param list the NodeList to parse
     * @throws IOException
     */
    private static void valuesToFiles(NodeList list) throws IOException{
        Double count = 0.0;

        for(int i = 0; i < list.getLength(); i++){
            Node n = list.item(i);

            String startDateText = attributeText(n, "startDate");
            String activityType = attributeText(n, "type");

            //First element overwrites file
            if (i == 0){
                try(BufferedWriter bw = new BufferedWriter(new FileWriter(valuesDoc))){
                    bw.write(activityType + "\n");
                }
            }

            count += Double.parseDouble(attributeText(n, "value"));

            try {
                String nextStartDate = attributeText(list.item(i + 1), "startDate");
                String nextActivity = attributeText(list.item(i + 1), "type");


                if (!(startDateText.regionMatches(false, 0,
                        nextStartDate, 0, 10) && activityType.equals(nextActivity))){
                    dateCountToFile(startDateText, count);
                    count = 0.0;
                    if (!activityType.equals(nextActivity)) stringToFile(nextActivity, true);
                }
            }
            catch(NullPointerException e){
                dateCountToFile(startDateText, count);
            }
        }
    }

    /**
     * Writes the desired data to the desired files, given as command line arguments.
     * @param args the command line arguments
     * @throws IOException, NullPointerException
     */
    public static void main(String[] args) throws IOException, NullPointerException {
        String filePath = args[0];
        valuesDoc = args[1];
        Document doc;

        doc  = parseXML(new File(filePath));

        doc.getDocumentElement().normalize();
        NodeList list = doc.getElementsByTagName("Record");
        valuesToFiles(list);
        }
    }

    
    

