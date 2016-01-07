drop table if exists ss_actions;
create table ss_actions (
  id int(12) not null auto_increment,
  jiraKey varchar(255),
  institutionId varchar(255),
  userId varchar(255),
  userName varchar(255),
  actionType varchar(255),
  actionStatus varchar(255),
  body blob,
  idp varchar(255),
  sp varchar(255),
  requestDate timestamp default current_timestamp,
  primary key (id)
);
