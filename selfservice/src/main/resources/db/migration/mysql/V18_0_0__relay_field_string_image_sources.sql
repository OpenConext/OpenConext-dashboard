UPDATE field_image
SET field_source = 0
WHERE field_source = 1;

UPDATE field_image
SET field_source = 1
WHERE field_source = 2;

UPDATE field_string
SET field_source = 0
WHERE field_source = 1;

UPDATE field_string
SET field_source = 1
WHERE field_source = 2;
