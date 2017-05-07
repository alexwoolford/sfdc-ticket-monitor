CREATE TABLE bundle(
  bundleName VARCHAR(255),
  bundleDate DATETIME,
  clusterId VARCHAR(64),
  PRIMARY KEY (bundleName)
);

CREATE TABLE cluster(
  clusterId VARCHAR(30),
  clusterName VARCHAR(255),
  numMasters FLOAT,
  numSlaves FLOAT,
  usedStorage BIGINT,
  totalStorage BIGINT,
  PRIMARY KEY (clusterId)
);
