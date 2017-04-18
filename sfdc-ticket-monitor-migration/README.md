# migration

The Salesforce ticket monitor uses MySQL to store state (e.g. tickets, notification status, etc...).

The MySQL tables can be created by running the [Flyway](https://flywaydb.org/) migration:

    cd sfdc-ticket-monitor-migration
    mvn clean compile flyway:migrate
