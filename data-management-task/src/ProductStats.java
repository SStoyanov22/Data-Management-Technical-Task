public class ProductStats {
    public final String product_id;
    public final int total_purchases;
    public final int total_orders;
    public final int total_customers;

    public ProductStats(String product_id, int total_purchases, int total_orders, int total_customers) {
        this.product_id = product_id;
        this.total_purchases = total_purchases;
        this.total_orders = total_orders;
        this.total_customers = total_customers;
    }
}
