CREATE TABLE "posts" (
	id bigserial primary key,
	title varchar(255) NOT NULL,
	slug varchar(255),
	content text NOT NULL,
	creation_date timestamp NOT NULL,
	modification_date timestamp
);
