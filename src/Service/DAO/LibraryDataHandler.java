package Service.DAO;

import Model.Book;
import Model.Client;
import Model.Rent;
import Service.Library;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import Interface.Config.Config;

import java.io.*;
import java.util.ArrayList;

/**
 * Class used to manage XML / CSV files read and write operations.
 */
public class LibraryDataHandler {
    /**
     * Reads library data from an CSV file
     * @return List of books
     */
    @Deprecated
    public static ArrayList<Book> readLibraryDataFromFile() {
        var arrayListToBeReturned = new ArrayList<Book>();
        FileReader in = null;
        BufferedReader br = null;
        try {
            var file = new File("resources\\data\\libraryData.txt");
            in = new FileReader(file);
            br = new BufferedReader(in);

            String lineFromInput;
            while ((lineFromInput = br.readLine()) != null) {
                String fields[] = lineFromInput.split(";");
                arrayListToBeReturned.add(new Book(
                        Integer.valueOf(fields[0]),
                        Integer.valueOf(fields[1]),
                        Double.valueOf(fields[2]),
                        Double.valueOf(fields[3]),
                        Double.valueOf(fields[4]),
                        fields[5],
                        fields[6],
                        Integer.valueOf(fields[7])
                ));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return arrayListToBeReturned;
        }
    }

    /**
     * Writes library data to CSV file
     * @param library to be written to CSV
     */
    @Deprecated
    public static void writeLibraryDataToFile(Library library) {
        FileWriter out = null;
        BufferedWriter br = null;
        try {
            var file = new File("resources\\data\\libraryData.txt");
            out = new FileWriter(file);
            br = new BufferedWriter(out);
            br.flush();
            for (Book book : library.getBooksOnStock()) {
                br.write(book.getBookId()
                        + ";"
                        + book.getPages()
                        + ";"
                        + book.getWidth()
                        + ";"
                        + book.getHeight()
                        + ";"
                        + book.getPrice()
                        + ";"
                        + book.getTitle()
                        + ";"
                        + book.getAuthor()
                        + ";");
                br.newLine();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Reads library data from xml
     * @return Library from XML
     */
    public static Library readLibraryDataFromXml() {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("Book",Book.class);
        xstream.alias("Library",Library.class);
        xstream.alias("Client", Client.class);
        xstream.alias("Rent", Rent.class);
        var xml = new File("resources\\data\\libraryXMLData.xml");
        return (Library) xstream.fromXML(xml);
    }

    /**
     * Writes library data to xml
     * @param library to be written to XML
     */
    public static void writeLibraryDataToXml(Library library) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("Book",Book.class);
        xstream.alias("Library",Library.class);
        xstream.alias("Client", Client.class);
        xstream.alias("Rent", Rent.class);
        FileOutputStream out = null;
        try {
            var file = new File("resources\\data\\libraryXMLData.xml");
            out = new FileOutputStream(file);
            String xml = xstream.toXML(library);
            out.write("<?xml version=\"1.0\"?>".getBytes("UTF-8"));
            out.write(xstream.toXML(library).getBytes("UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Reads library config from xml
     * @return Config from config.xml file
     */
    public static Config readConfigDataFromXml() {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("Config",Config.class);
        var xml = new File("resources\\data\\config.xml");
        return (Config) xstream.fromXML(xml);
    }

    /**
     * Writes library config to xml
     * @param config to be written to config.xml
     */
    public static void writeConfigDataToXml(Config config) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("Config",Config.class);
        FileOutputStream out = null;
        try {
            var file = new File("resources\\data\\config.xml");
            out = new FileOutputStream(file);
            String xml = xstream.toXML(config);
            out.write("<?xml version=\"1.0\"?>".getBytes("UTF-8"));
            out.write(xstream.toXML(config).getBytes("UTF-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


}
