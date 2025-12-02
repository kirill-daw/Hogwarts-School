SELECT
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE tablename IN ('students', 'faculties')
ORDER BY tablename, indexname;

\d students;
\d faculties;