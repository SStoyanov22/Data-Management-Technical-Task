import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TsvHelper {
    public static List<String[]> readTsv(File file) {
        List<String[]> Data = new ArrayList<>(); //initializing a new ArrayList out of String[]'s
        try (BufferedReader TSVReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = TSVReader.readLine()) != null) {
                String[] lineItems = line.split("\t"); //splitting the line and adding its items in String[]
                Data.add(lineItems); //adding the split line array to the ArrayList
            }
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
        return Data;
    }

    public static void writeTsv(String output_filepath,List<ProductsReportItem> outputLines) throws IOException{
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(output_filepath, false))) { //overwrites the file each time
            outputLines.stream()
                    .map(ProductsReportItem::toString)
                    .forEach(pw::println);
        }
    }

}
