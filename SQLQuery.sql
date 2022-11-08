/*



Write a query that returns the following for each product that exists in at least one order, sorted by the number of purchasing customers, descending:
-The id of the product
-The name of the product
-The number of customers who purchased the product
-The total number of times the product was purchased
-The average number of times the product was purchased by each customer
-The average number of days between purchases, across all customers who purchased the product more than once
-The name of the customer who has purchased the product the most times, breaking ties by choosing the customer with the earliest purchase date
 and breaking any ties there by choosing the customer with the minimum customer id lexicographically

Sample data files are available here. Data is in TSV format and corresponds exactly to the table definitions above. Dates are in YYYY-MM-DD format.
Your deliverables:
SQL query
Index definitions (or other) and rationale


-------- Table creation --------
CREATE TABLE products (id INTEGER, name VARCHAR(255));

CREATE TABLE orders (order_id VARCHAR(255), date DATE, customer_id VARCHAR(255), product_id INTEGER);

CREATE TABLE customers (customer_id VARCHAR(255), name VARCHAR(255));

------- Indexes---------
CREATE INDEX ix_products_id ON products (id);
CREATE INDEX ix_orders_order_id ON orders (order_id);
CREATE INDEX ix_orders_customer_id ON orders (customer_id);
CREATE INDEX ix_orders_product_id ON orders(product_id);
CREATE INDEX ix_customers_customer_id ON customers(customer_id);

Rationale:
Indexes are created on the primary/foreign key columns to speed up the query.
------- Query ----------
*/

with product_stats as(
		select product_id, count(*) total_purchases, count(distinct order_id) total_orders, count(distinct customer_id) total_customers  
        from orders 
		group by product_id
		),

	 product_customer_stats as(
		select product_id, customer_id, count(*) times_purchased_by_customer, count(distinct order_id) times_ordered_by_customer, min(date) earliest_date
	    from orders
		group by product_id, customer_id
	 ),

	 product_top_customer as (
		select product_id,customer_id as top_customer, times_purchased_by_customer
		from (
			select product_customer_stats.*,
			ROW_NUMBER() over(partition by product_id 
							  order by times_purchased_by_customer desc,
									   earliest_date asc,
									   customer_id asc)  rn
			from product_customer_stats) prod_cust_ranked
			where rn =1),

	avg_times_purchased_by_customers as(
		select product_id, avg(times_purchased_by_customer) avg_times_purchased
		from product_customer_stats
		group by product_id
	),

	avg_days_between_purchases as(
		select product_id, avg(diff) as avg_days_between_purchases
		from (
			select o.order_id,
				   o.customer_id,
				   o.product_id,
				   pcs.times_ordered_by_customer,
				   pcs.times_purchased_by_customer,
				   datediff(day,
							lag(date) over(partition by o.customer_id,
													    o.product_id 
										   order by o.date),
							date) diff
			  
					from (select distinct order_id, product_id, customer_id, date 
						  from orders) o
					inner join product_customer_stats pcs on o.customer_id = pcs.customer_id and o.product_id = pcs.product_id  and times_ordered_by_customer > 1
					
					) a
		where diff is not NULL --skip earliest order
		group by product_id
	)

	select 
	[Product Id] = p.id,
	[Product Name] = p.name,
	[Total Purchases] = ptp.total_purchases,
	[Total Orders] = ptp.total_orders,
	[Total Customers] = ptp.total_customers,
	[Top Customer] = ptopc.top_customer,
	[Avg Times Purchased] = apbc.avg_times_purchased,
	[Avg Days Btw Purchases] = adbp.avg_days_between_purchases 
	from products p
	inner join product_stats ptp on ptp.product_id = p.id
	inner join product_top_customer ptopc  on ptopc.product_id = p.id
	inner join avg_times_purchased_by_customers apbc on apbc.product_id = p.id
	left outer join avg_days_between_purchases adbp on adbp.product_id = p.id --there might be orders with products that do not fullfill the condition times ordered > 1
	order by total_customers desc



