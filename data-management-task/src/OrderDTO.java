import java.time.LocalDate;
import java.util.Date;

public class OrderDTO {
    public final String order_id;
    public final LocalDate date;
    public final String customer_id;
    public final String product_id;

    public OrderDTO(String order_id, LocalDate date, String customer_id, String product_id) {
        this.order_id = order_id;
        this.date = date;
        this.customer_id = customer_id;
        this.product_id = product_id;
    }
}
