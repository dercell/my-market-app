insert into orders (id, total_sum, user_id)
values (1, 5000, 1),
       (2, 15000, 1);

insert into order_items (id, order_id, item_id, count)
values (1, 1, 1, 1),
       (2, 2, 3, 1),
       (3, 2, 4, 2);
commit;