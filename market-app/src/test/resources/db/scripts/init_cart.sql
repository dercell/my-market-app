delete from cart;
insert into cart(id, item_id, count, user_id) values (1,1,1, 1),
                                                     (2,2,2, 1);
commit;