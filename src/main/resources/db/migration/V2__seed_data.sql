-- Тестовые/справочные данные. Явные id у товаров и пользователей — чтобы золотой датасет
-- ссылался на стабильные идентификаторы. Последовательности перематываются в конце,
-- чтобы рантайм-вставки (заказы, оплаты) не конфликтовали.

-- Пользователи (id 1..12)
INSERT INTO users (id, name, email, phone) VALUES
 (1,  'Иван Петров',      'ivan@example.com',    '+79000000001'),
 (2,  'Мария Сидорова',   'maria@example.com',   '+79000000002'),
 (3,  'Алексей Смирнов',  'alexey@example.com',  '+79000000003'),
 (4,  'Ольга Кузнецова',  'olga@example.com',    '+79000000004'),
 (5,  'Дмитрий Волков',   'dmitry@example.com',  '+79000000005'),
 (6,  'Елена Морозова',   'elena@example.com',   '+79000000006'),
 (7,  'Сергей Новиков',   'sergey@example.com',  '+79000000007'),
 (8,  'Анна Фёдорова',    'anna@example.com',    '+79000000008'),
 (9,  'Павел Орлов',      'pavel@example.com',   '+79000000009'),
 (10, 'Наталья Зайцева',  'natalia@example.com', '+79000000010'),
 (11, 'Виктор Лебедев',   'viktor@example.com',  '+79000000011'),
 (12, 'Юлия Соколова',    'yulia@example.com',   '+79000000012');

-- Товары (id 1..16): разнообразные параметры под критерий C2
INSERT INTO product (id, name, category, brand, price, memory_gb, screen_diagonal, color, stock_quantity) VALUES
 (1,  'Смартфон Galaxy A55',      'electronics', 'Samsung', 34990.00, 256, 6.6,  'black',  40),
 (2,  'Смартфон iPhone 15',       'electronics', 'Apple',   79990.00, 128, 6.1,  'blue',   25),
 (3,  'Смартфон Redmi Note 13',   'electronics', 'Xiaomi',  17990.00, 128, 6.67, 'green',  60),
 (4,  'Наушники AirPods Pro 2',   'audio',       'Apple',   19990.00, NULL, NULL, 'white',  50),
 (5,  'Наушники WH-1000XM5',      'audio',       'Sony',    29990.00, NULL, NULL, 'black',  30),
 (6,  'Ноутбук MacBook Air 13',   'laptops',     'Apple',  109990.00, 512, 13.6, 'silver', 15),
 (7,  'Ноутбук ThinkPad E14',     'laptops',     'Lenovo',  64990.00, 512, 14.0, 'black',  20),
 (8,  'Ноутбук Zenbook 14',       'laptops',     'Asus',    74990.00, 1024, 14.0, 'grey',  12),
 (9,  'Планшет iPad Air',         'electronics', 'Apple',   59990.00, 256, 10.9, 'grey',   18),
 (10, 'Планшет Galaxy Tab S9',    'electronics', 'Samsung', 69990.00, 256, 11.0, 'black',  14),
 (11, 'Умные часы Watch GT 4',    'wearables',   'Huawei',  16990.00, NULL, 1.4,  'black',  35),
 (12, 'Умные часы Apple Watch 9', 'wearables',   'Apple',   41990.00, NULL, 1.9,  'red',    22),
 (13, 'Монитор UltraSharp 27',    'monitors',    'Dell',    44990.00, NULL, 27.0, 'black',  16),
 (14, 'Монитор Odyssey G5',       'monitors',    'Samsung', 27990.00, NULL, 32.0, 'black',  10),
 (15, 'Клавиатура MX Keys',       'accessories', 'Logitech', 9990.00, NULL, NULL, 'grey',   80),
 (16, 'Мышь MX Master 3S',        'accessories', 'Logitech', 8990.00, NULL, NULL, 'black',  75);

-- Заказы (id 1..3)
INSERT INTO orders (id, user_id, status, total_amount, created_at) VALUES
 (1, 1,  'PAID',    79990.00, now() - INTERVAL '3 day'),
 (2, 12, 'NEW',     17990.00, now() - INTERVAL '1 day'),
 (3, 5,  'SHIPPED', 49980.00, now() - INTERVAL '2 day');

INSERT INTO order_item (order_id, product_id, quantity, price_at_purchase) VALUES
 (1, 2,  1, 79990.00),
 (2, 3,  1, 17990.00),
 (3, 5,  1, 29990.00),
 (3, 4,  1, 19990.00);

-- Оплата заказа 1
INSERT INTO payment (order_id, amount, status, method, paid_at) VALUES
 (1, 79990.00, 'SUCCESS', 'CARD', now() - INTERVAL '3 day');

-- Перемотка последовательностей под рантайм-вставки
SELECT setval('users_id_seq',      (SELECT MAX(id) FROM users));
SELECT setval('product_id_seq',    (SELECT MAX(id) FROM product));
SELECT setval('orders_id_seq',     (SELECT MAX(id) FROM orders));
SELECT setval('order_item_id_seq', (SELECT COALESCE(MAX(id), 1) FROM order_item));
SELECT setval('payment_id_seq',    (SELECT COALESCE(MAX(id), 1) FROM payment));
