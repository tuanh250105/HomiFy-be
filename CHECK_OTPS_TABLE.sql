-- Kiểm tra cấu trúc bảng otps hiện tại
SELECT 
    column_name,
    data_type,
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'otps'
ORDER BY ordinal_position;

-- Kiểm tra các constraints
SELECT 
    constraint_name,
    constraint_type
FROM information_schema.table_constraints
WHERE table_name = 'otps';

-- Kiểm tra các index
SELECT 
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename = 'otps';

