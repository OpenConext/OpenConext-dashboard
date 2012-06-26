create table oauth_entry (
  token varchar(255) NOT NULL,
  token_secret varchar(255) DEFAULT NULL,
  app_id varchar(255) DEFAULT NULL,
  callback_url varchar(1024) DEFAULT NULL,
  callback_url_signed boolean DEFAULT NULL,
  user_id varchar(255) DEFAULT NULL,
  authorized boolean DEFAULT NULL,
  consumer_key varchar(255) DEFAULT NULL,
  type varchar(255) DEFAULT NULL,
  issue_time datetime DEFAULT NULL,
  domain varchar(255) DEFAULT NULL,
  container varchar(255) DEFAULT NULL,
  oauth_version varchar(255) DEFAULT NULL,
  callback_token varchar(255) DEFAULT NULL,
  callback_token_attempts integer DEFAULT NULL,
  virtual_organization varchar(255) DEFAULT NULL,
  PRIMARY KEY (token)
);

INSERT INTO oauth_entry (token, token_secret, app_id, callback_url, callback_url_signed, user_id, authorized, consumer_key, type, issue_time, domain, container, oauth_version, callback_token, callback_token_attempts, virtual_organization)
VALUES
  ('cafebabe-babe-cafe-babe-cafebabecafe', 'deadbeef-dead-beef-dead-beefdeadbeef', 'http://mujina-sp', 'https://mujina-sp.example.com/social/oauth-callback.shtml', 1, 'urn:collab:person:example.com:test-user', 1, 'http://mujina-sp', 'ACCESS', '2012-06-14 11:54:32', 'surfnet.nl', NULL, '1.0', '654321', 0, NULL);

INSERT INTO oauth_entry (token, token_secret, app_id, callback_url, callback_url_signed, user_id, authorized, consumer_key, type, issue_time, domain, container, oauth_version, callback_token, callback_token_attempts, virtual_organization)
VALUES
  ('deadbeef-dead-beef-dead-beefdeadbeef', 'deadbeef-dead-beef-dead-beefdeadbeef', 'http://mujina-sp-2', 'https://mujina-sp2.example.com/social/oauth-callback.shtml', 1, 'urn:collab:person:example.com:test-user', 1, 'http://mujina-sp-2', 'ACCESS', '2012-06-14 11:54:32', 'surfnet.nl', NULL, '1.0', '123456', 0, NULL);

INSERT INTO oauth_entry (token, token_secret, app_id, callback_url, callback_url_signed, user_id, authorized, consumer_key, type, issue_time, domain, container, oauth_version, callback_token, callback_token_attempts, virtual_organization)
VALUES
  ('babecafe-babe-cafe-babe-cafebabecafe', 'deadbeef-dead-beef-dead-beefdeadbeef', 'http://mujina-sp', 'https://mujina-sp.example.com/social/oauth-callback.shtml', 1, 'urn:collab:person:example.com:dummy-user', 1, 'http://mujina-sp', 'ACCESS', '2012-06-14 11:54:32', 'surfnet.nl', NULL, '1.0', '987654', 0, NULL);

