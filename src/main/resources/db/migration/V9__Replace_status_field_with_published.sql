ALTER TABLE articles ADD COLUMN published boolean NOT NULL DEFAULT FALSE;
UPDATE articles SET published = TRUE WHERE status = 'PUBLIC';
ALTER TABLE articles DROP COLUMN status;
