INSERT INTO countries (name, region, flag_emoji, description)
VALUES
('Angola', 'Southern Africa', 'AO', 'Known for its rich culinary heritage with Portuguese influences')
ON CONFLICT (name) DO UPDATE SET description = EXCLUDED.description;

-- Insert Angolan categories if they don't exist
INSERT INTO categories (name, description, display_order)
VALUES
('Main Courses', 'Traditional Angolan main dishes', 1),
('Stews', 'Hearty Angolan stews and soups', 2),
('Side Dishes', 'Traditional accompaniments', 3),
('Desserts', 'Sweet Angolan treats', 4)
ON CONFLICT (name) DO NOTHING;

-- Insert traditional Angolan dishes
INSERT INTO dishes (name, description, price, country_id, category_id, preparation_time, spice_level, ingredients, is_vegetarian, is_vegan, available)
VALUES
(
    'Muamba de Galinha',
    'Traditional Angolan chicken stew with palm oil, okra, and garlic',
    22.99,
    (SELECT id FROM countries WHERE name = 'Angola'),
    (SELECT id FROM categories WHERE name = 'Stews'),
    120,
    3,
    '{"chicken", "palm oil", "okra", "garlic", "onion", "tomato", "chili pepper"}',
    false,
    false,
    true
),
(
    'Mufete',
    'Grilled fish with beans, sweet potato, and plantains',
    24.50,
    (SELECT id FROM countries WHERE name = 'Angola'),
    (SELECT id FROM categories WHERE name = 'Main Courses'),
    90,
    2,
    '{"fish", "beans", "sweet potato", "plantains", "palm oil", "lemon"}',
    false,
    false,
    true
),
(
    'Calulu',
    'Dried fish or meat with vegetables and palm oil',
    20.75,
    (SELECT id FROM countries WHERE name = 'Angola'),
    (SELECT id FROM categories WHERE name = 'Stews'),
    100,
    2,
    '{"dried fish", "eggplant", "okra", "tomato", "onion", "palm oil", "spinach"}',
    false,
    false,
    true
),
(
    'Funje',
    'Traditional cassava porridge, staple accompaniment',
    8.99,
    (SELECT id FROM countries WHERE name = 'Angola'),
    (SELECT id FROM categories WHERE name = 'Side Dishes'),
    45,
    1,
    '{"cassava flour", "water", "salt"}',
    true,
    true,
    true
),
(
    'Pirão',
    'Manioc flour porridge served with fish sauces',
    7.50,
    (SELECT id FROM countries WHERE name = 'Angola'),
    (SELECT id FROM categories WHERE name = 'Side Dishes'),
    30,
    1,
    '{"manioc flour", "fish stock", "water"}',
    true,
    false,
    true
),
(
    'Cocada Amarela',
    'Traditional Angolan coconut and egg yolk dessert',
    12.99,
    (SELECT id FROM countries WHERE name = 'Angola'),
    (SELECT id FROM categories WHERE name = 'Desserts'),
    60,
    1,
    '{"coconut", "egg yolks", "sugar", "cinnamon", "cloves"}',
    true,
    false,
    true
),
(
    'Ginguba Torrada',
    'Roasted peanuts, popular Angolan snack',
    6.99,
    (SELECT id FROM countries WHERE name = 'Angola'),
    (SELECT id FROM categories WHERE name = 'Side Dishes'),
    20,
    1,
    '{"peanuts", "salt"}',
    true,
    true,
    true
),
(
    'Muzongué',
    'Angolan bean and pork stew',
    19.99,
    (SELECT id FROM countries WHERE name = 'Angola'),
    (SELECT id FROM categories WHERE name = 'Stews'),
    110,
    2,
    '{"pork", "beans", "palm oil", "onion", "garlic", "bay leaves"}',
    false,
    false,
    true
);

-- Update display order for better organization
UPDATE categories SET display_order = 1 WHERE name = 'Main Courses';
UPDATE categories SET display_order = 2 WHERE name = 'Stews';
UPDATE categories SET display_order = 3 WHERE name = 'Side Dishes';
UPDATE categories SET display_order = 4 WHERE name = 'Desserts';
UPDATE categories SET display_order = 5 WHERE name = 'Beverages';