import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.groupingBy;

public class ProductsReport {
    public static void main(String[] args) {
        //Get filenames
        var products_filename = args[0];
        var orders_filename = args[1];
        var customers_filename = args[2];
        var output_filename = args[3];

        //Collect data into data structures
        var products_data = TsvHelper.readTsv(new File(products_filename));
        var orders_data = TsvHelper.readTsv(new File(orders_filename));
        var customers_data = TsvHelper.readTsv(new File(customers_filename));

        //Extract product report items to list
        var extractedProductReportList = extract(customers_data, orders_data, products_data);

        //Write product report items to list
        try {
            TsvHelper.writeTsv(output_filename,extractedProductReportList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<ProductsReportItem> extract(List<String[]> customers_data, List<String[]> orders_data, List<String[]> products_data) {
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
        var outputList = new ArrayList<ProductsReportItem>();
        for ( String product_id: productTotalPurchases.keySet()) {
            var product_name = products.stream().filter(p -> p.product_id.equals(product_id)).findFirst().orElse(null).name;
            var productTotalPurchase = productTotalPurchases.get(product_id);
            var productTotalCustomer = productTotalCustomers.get(product_id);
            var productEarliestOrderDate = productEarliestOrders.get(product_id);
            var productLatestOrderDate = productLatestOrders.get(product_id);
            var productCustomerWithMinimumId = productCustomerMinimumIds.get(product_id);
            var firstCustomer = customers.stream().filter(c -> {
                return c.customer_id.equals(productCustomerWithMinimumId);
            }).findFirst().orElse(null);
            var daysBetween = DAYS.between(productEarliestOrderDate, productLatestOrderDate);

            //Create new product output
            var newProductOutput = new ProductsReportItem(product_id,product_name,productTotalPurchase,productTotalCustomer,firstCustomer.name,daysBetween);

            outputList.add(newProductOutput);
        }

        outputList.sort((i1,i2)->(i1.total_customers > i2.total_customers) ? 1 : -1);

        return outputList;
    }


}



