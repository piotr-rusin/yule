CREATE FUNCTION alter_timestamps() RETURNS void AS $$
DECLARE
	columns varchar[] := ARRAY['creation_date',
	'modification_date', 'publication_date'];
	col varchar;
BEGIN
	SET LOCAL timezone='UTC';
	FOREACH col IN ARRAY columns
	LOOP
		EXECUTE format('ALTER TABLE articles '
			'ALTER COLUMN %I TYPE timestamp with time zone',
			col);
	END LOOP;
END;
$$ LANGUAGE plpgsql;

SELECT alter_timestamps();
