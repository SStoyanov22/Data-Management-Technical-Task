import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
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

        var productTotalPurchases = orders
                .stream()
                .collect(
                        groupingBy(
                                order -> order.product_id,
                                Collectors.counting()));

        var productTotalCustomers =  orders.stream()
                .collect(
                        Collectors.groupingBy(
                                order -> order.product_id,
                                Collectors.mapping(
                                        order -> order.customer_id,
                                        Collectors.collectingAndThen(
                                                Collectors.toSet(),
                                                set->set.size()))));

        var  productEarliestOrders= orders.stream()
                .collect(
                        Collectors.groupingBy(
                                order -> order.product_id,
                                        Collectors.mapping(order -> order.date,
                                                Collectors.collectingAndThen(
                                                        Collectors.reducing((LocalDate d1, LocalDate d2) -> d1.isBefore(d2) ? d1 : d2),
                                                        Optional::get))));
        var  productLatestOrders= orders.stream()
                .collect(
                        Collectors.groupingBy(
                                order -> order.product_id,
                                        Collectors.mapping(order -> order.date,
                                                Collectors.collectingAndThen(
                                                        Collectors.reducing((LocalDate d1, LocalDate d2) -> d1.isBefore(d2) ? d1 : d2),
                                                        Optional::get))));
        var  productCustomerMinimumIds= orders.stream()
                .collect(
                        Collectors.groupingBy(
                                order -> order.product_id,
                                Collectors.mapping(
                                        order -> order.customer_id,
                                        Collectors.collectingAndThen(
                                                Collectors.reducing((String d1, String d2) -> d1.compareTo(d2)<0 ? d1 : d2),
                                                Optional::get))));
        var outputList = new ArrayList<ProductOutput>();
        for ( String product_id: productTotalPurchases.keySet()) {
            var product_name = products.stream().filter(p -> p.product_id.equals(product_id)).findFirst().orElse(null).name;
            var productTotalPurchase = productTotalPurchases.get(product_id);
            var productTotalCustomer = productTotalCustomers.get(product_id);
            var productEarliestOrderDate = productEarliestOrders.get(product_id);
            var productLatestOrderDate = productLatestOrders.get(product_id);
            var productCustomerWithMinimumId = productCustomerMinimumIds.get(product_id);
            var firstCustomer = customers.stream().filter(c -> c.customer_id.equals(productCustomerWithMinimumId)).findFirst().orElse(null);
            var daysBetween = DAYS.between(productEarliestOrderDate, productLatestOrderDate);

            //Create new product output
            var newProductOutput = new ProductOutput(product_id,product_name,productTotalPurchase,productTotalCustomer,firstCustomer.name,daysBetween);

            outputList.add(newProductOutput);
        }


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



