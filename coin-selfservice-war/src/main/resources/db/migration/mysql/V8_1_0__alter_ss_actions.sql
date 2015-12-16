ALTER TABLE `ss_actions` CHANGE COLUMN `institutionId` `institutionId` varchar(255) not null;
ALTER TABLE `ss_actions` CHANGE COLUMN `userId` `userId` varchar(255) not null;
ALTER TABLE `ss_actions` CHANGE COLUMN `body` `body` blob not null;
ALTER TABLE `ss_actions` CHANGE COLUMN `idp` `idp` varchar(255) not null;
ALTER TABLE `ss_actions` CHANGE COLUMN `sp` `sp` varchar(255) not null;
ALTER TABLE `ss_actions` CHANGE COLUMN `actionType` `actionType` varchar(255) not null;