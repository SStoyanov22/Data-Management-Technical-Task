/*

CREATE TABLE products (id INTEGER, name VARCHAR(255));

CREATE TABLE orders (order_id VARCHAR(255), date DATE, customer_id VARCHAR(255), product_id INTEGER);

CREATE TABLE customers (customer_id VARCHAR(255), name VARCHAR(255));

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
*/
with product_stats as(
		select product_id, count(*) total_purchases, count(distinct customer_id) total_customers  
        from orders 
		group by product_id),

	 product_customer_stats as(
		select product_id, customer_id, count(*) times_purchased, min(date) earliest_date
	    from orders
		group by  product_id, customer_id
	 ),

	 product_top_customer as (
		select product_id,customer_id as top_customer, times_purchased,earliest_date 
		from (
			select product_customer_stats.*,
			ROW_NUMBER() over(partition by product_id 
							  order by times_purchased desc,
									   earliest_date asc,
									   customer_id asc)  rn
			from product_customer_stats) prod_cust_ranked
			where rn =1),

	days_between_purchases as(
		select o.order_id,o.customer_id,o.product_id,date,
		  lag(date) over(partition by o.customer_id,o.product_id order by o.date) prev_date
		from orders o
		inner join product_customer_stats pcs on pcs.product_id = o.product_id and pcs.times_purchased > 1
		
	)
	select p.id,p.name,ptp.total_purchases,ptp.total_customers,ptopc.top_customer from products p
	inner join product_stats ptp on ptp.product_id = p.id
	inner join product_top_customer ptopc  on ptopc.product_id = p.id



