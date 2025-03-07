CREATE TABLE tasks (
   id SERIAL PRIMARY KEY,
   title TEXT,
   description TEXT,
   created TIMESTAMP,
   done BOOLEAN
);