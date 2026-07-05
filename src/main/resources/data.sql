-- ============================
-- Default Categories For the Users
-- ============================

INSERT INTO category(name, icon, color, category_type, is_active, user_user_id)
SELECT 'Food', '🍔', '#FF6B6B', 'DEFAULT', true, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM category
    WHERE name='Food'
      AND category_type='DEFAULT'
);

INSERT INTO category(name, icon, color, category_type, is_active, user_user_id)
SELECT 'Transport', '🚗', '#4ECDC4', 'DEFAULT', true, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM category
    WHERE name='Transport'
      AND category_type='DEFAULT'
);

INSERT INTO category(name, icon, color, category_type, is_active, user_user_id)
SELECT 'Shopping', '🛍️', '#FFEAA7', 'DEFAULT', true, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM category
    WHERE name='Shopping'
      AND category_type='DEFAULT'
);

INSERT INTO category(name, icon, color, category_type, is_active, user_user_id)
SELECT 'Rent', '🏠', '#45B7D1', 'DEFAULT', true, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM category
    WHERE name='Rent'
      AND category_type='DEFAULT'
);

INSERT INTO category(name, icon, color, category_type, is_active, user_user_id)
SELECT 'Bills', '💡', '#96CEB4', 'DEFAULT', true, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM category
    WHERE name='Bills'
      AND category_type='DEFAULT'
);

INSERT INTO category(name, icon, color, category_type, is_active, user_user_id)
SELECT 'Healthcare', '🏥', '#DDA0DD', 'DEFAULT', true, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM category
    WHERE name='Healthcare'
      AND category_type='DEFAULT'
);

INSERT INTO category(name, icon, color, category_type, is_active, user_user_id)
SELECT 'Education', '📚', '#98D8C8', 'DEFAULT', true, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM category
    WHERE name='Education'
      AND category_type='DEFAULT'
);

INSERT INTO category(name, icon, color, category_type, is_active, user_user_id)
SELECT 'Entertainment', '🎬', '#F7DC6F', 'DEFAULT', true, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM category
    WHERE name='Entertainment'
      AND category_type='DEFAULT'
);

INSERT INTO category(name, icon, color, category_type, is_active, user_user_id)
SELECT 'Travel', '✈️', '#BB8FCE', 'DEFAULT', true, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM category
    WHERE name='Travel'
      AND category_type='DEFAULT'
);

INSERT INTO category(name, icon, color, category_type, is_active, user_user_id)
SELECT 'Groceries', '🛒', '#82E0AA', 'DEFAULT', true, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM category
    WHERE name='Groceries'
      AND category_type='DEFAULT'
);

INSERT INTO category(name, icon, color, category_type, is_active, user_user_id)
SELECT 'Subscriptions', '📱', '#F1948A', 'DEFAULT', true, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM category
    WHERE name='Subscriptions'
      AND category_type='DEFAULT'
);

INSERT INTO category(name, icon, color, category_type, is_active, user_user_id)
SELECT 'Personal Care', '💇', '#AED6F1', 'DEFAULT', true, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM category
    WHERE name='Personal Care'
      AND category_type='DEFAULT'
);

INSERT INTO category(name, icon, color, category_type, is_active, user_user_id)
SELECT 'Gifts', '🎁', '#F5B041', 'DEFAULT', true, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM category
    WHERE name='Gifts'
      AND category_type='DEFAULT'
);

INSERT INTO category(name, icon, color, category_type, is_active, user_user_id)
SELECT 'Miscellaneous', '📦', '#BDC3C7', 'DEFAULT', true, NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM category
    WHERE name='Miscellaneous'
      AND category_type='DEFAULT'
);