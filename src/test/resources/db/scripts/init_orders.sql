insert into orders (id, total_sum)
values (1, 5000),
       (2, 15000);

insert into order_items (id, order_id, item_id, count)
values (1, 1, 1, 1),
       (2, 2, 3, 1),
       (3, 2, 4, 2);
commit;