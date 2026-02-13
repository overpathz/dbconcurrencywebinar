----
DROP TABLE IF EXISTS queue;
CREATE TABLE queue (id serial, payload text, status text default 'new');
INSERT INTO queue (payload) SELECT 'msg-'||x FROM generate_series(1, 10) x;


----
DROP TABLE IF EXISTS pdf_jobs;
CREATE TABLE pdf_jobs (id serial, status text default 'pending', lease_until timestamp);
INSERT INTO pdf_jobs (status) VALUES ('pending');
INSERT INTO pdf_jobs (status) VALUES ('pending');
INSERT INTO pdf_jobs (status) VALUES ('pending');

--
DROP TABLE IF EXISTS account;
CREATE TABLE account (id serial PRIMARY KEY, name text, balance bigint);
INSERT INTO account (name, balance) VALUES ('Alice', 1000), ('Bob', 1000);