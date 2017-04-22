
USE sfdc_ticket_monitor;

CREATE TABLE ticket (
  CaseNumber VARCHAR(20),
  PRIMARY KEY (CaseNumber),
  AccountId VARCHAR(20),
  AccountName VARCHAR(100),
  Severity VARCHAR(100),
  ProductComponent TEXT,
  ProblemStatementQuestion TEXT,
  Description TEXT,
  CurrentStatusResolution TEXT,
  ContactId VARCHAR(20),
  ContactName VARCHAR(100),
  Priority VARCHAR(100),
  ProblemType VARCHAR(255),
  ProblemSubType VARCHAR(255),
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
