
USE sfdc_ticket_monitor;

CREATE TABLE ticket (
  CaseNumber VARCHAR(20),
  PRIMARY KEY (CaseNumber),
  AccountName VARCHAR(100),
  AccountId VARCHAR(20),
  ContactId VARCHAR(20),
  Description TEXT,
  LastViewedDate DATETIME,
  Priority VARCHAR(100),
  Problem_Type__c VARCHAR(100),
  Reason VARCHAR(4000),
  Status VARCHAR(20),
  updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notification (
  CaseNumber VARCHAR(20),
  PRIMARY KEY (CaseNumber),
  FOREIGN KEY (CaseNumber) REFERENCES ticket(CaseNumber),
  NotificationSent BOOLEAN,
  created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
