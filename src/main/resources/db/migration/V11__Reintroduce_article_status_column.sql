ALTER TABLE articles ADD COLUMN status varchar(21) NOT NULL DEFAULT 'DRAFT';
UPDATE articles SET status = 'PUBLISHED' WHERE published = true;
UPDATE articles SET status = 'PUBLICATION_SCHEDULED' WHERE publication_scheduled = true;
ALTER TABLE articles DROP COLUMN published, DROP COLUMN publication_scheduled;
