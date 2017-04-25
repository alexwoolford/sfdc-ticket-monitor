
CREATE TABLE run_stats (
  id MEDIUMINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (id),
  startRun DATETIME,
  endRun DATETIME,
  sfdcQueries INT,
  cacheHits INT,
  exceptions INT,
  emails INT
);
