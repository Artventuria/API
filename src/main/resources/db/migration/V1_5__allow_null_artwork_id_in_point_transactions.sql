-- Modified the point_transactions table to allow NULL values ​​in the artwork_id column
ALTER TABLE point_transactions ALTER COLUMN artwork_id DROP NOT NULL;
