
CREATE TABLE account (
  AccountId VARCHAR(20),
  PRIMARY KEY (AccountId),
  AccountName VARCHAR(255),
  CustomerRecordId2 VARCHAR(20)
);

CREATE TABLE contact (
  ContactId VARCHAR(20),
  PRIMARY KEY (ContactId),
  ContactName VARCHAR(255)
);
