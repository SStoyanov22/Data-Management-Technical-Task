public class ProductsReportItem {
    public final String product_id;
    public final String product_name;
    public final long total_purchases;
    public final int total_customers;
    public final String firstCustomerByName;
    public final long daysBetween;

    public ProductsReportItem(String product_id, String product_name, long total_purchases, int total_customers, String firstCustomerByName, long daysBetween) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.total_purchases = total_purchases;
        this.total_customers = total_customers;
        this.firstCustomerByName = firstCustomerByName;
        this.daysBetween = daysBetween;
    }

    @Override
    public String toString() {
        var str = new StringBuilder(
                this.product_id + "\t"
                        + this.product_name +  "\t"
                        + this.total_purchases + "\t"
                        + this.total_customers + "\t"
                        + this.firstCustomerByName + "\t"
                        + this.daysBetween + "\t");
        return str.toString();
    }
}