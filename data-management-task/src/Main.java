import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

public class Main {
    public static void main(String[] args) {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        var customers_data = readTsv(new File("sample_data/customers.tsv"));
        var orders_data = readTsv(new File("sample_data/orders.tsv"));
        var products_data = readTsv(new File("sample_data/products.tsv"));

        List<OrderDTO> orders = orders_data.
                stream().
                map((row) -> new OrderDTO(row[0], LocalDate.parse(row[1]),row[2],row[3]))
                .collect(Collectors
                        .toList());
        List<CustomerDTO> customers = customers_data
                .stream()
                .map((row) -> new CustomerDTO(row[0],row[1]))
                .collect(Collectors
                        .toList());
        List<ProductDTO> products = products_data
                .stream()
                .map((row) -> new ProductDTO(row[0],row[1]))
                .collect(Collectors.toList());

        /*var productStatsMap = orders
                .stream()
                .collect(
                        groupingBy(
                                order -> order.product_id,
                                Collectors.collectingAndThen(
                                        groupingBy(order -> order.order_id,Collectors.counting()),
                                Map::size)))

                );*/
        orders.forEach(c -> System.out.println(c.order_id + " " +c.customer_id + " " + c.product_id + " " + c.date));
    }

   public static List<String[]>  readTsv(File file) {
        List<String[]> Data = new ArrayList<>(); //initializing a new ArrayList out of String[]'s
        try (BufferedReader TSVReader = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = TSVReader.readLine()) != null) {
                String[] lineItems = line.split("\t"); //splitting the line and adding its items in String[]
                Data.add(lineItems); //adding the splitted line array to the ArrayList
            }
        } catch (Exception e) {
            System.out.println("Something went wrong");
        }
        return Data;
    }
}



