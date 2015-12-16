/*
BACKLOG-1306: Update all enduser_description_EN fields (field_key = 3), reset their current source from SURFconext (field_source=1) to CSA (field_source=2), reset their value to "".
BACKLOG-1306: Update all enduser_description_NL fields (field_key = 4), reset their current source from SURFconext (field_source=1) to CSA (field_source=2), reset their value to "".
 */
update field_string set field_value = '', field_source = 2 where field_key = 3 and field_source = 1;
update field_string set field_value = '', field_source = 2 where field_key = 4 and field_source = 1;