# SFDC ticket monitor

I felt it was important, as a Solutions Engineer, to get notified when customers open support cases.

This app uses MySQL to persist ticket/notification statuses, SalesForce.com as the source of tickets, and Sendgrid to email. In order to run, it's necessary to edit a couple of properties files:
1. `sfdc-ticket-monitor-migration/flyway.properties`
3. `sfdc-ticket-monitor/src/main/resources/application.properties`

To create the MySQL tables, run the Flyway migration:

    cd sfdc-ticket-monitor-migration
    mvn clean compile flyway:migrate

This is a long-running Java Spring application: it checks for new tickets every 20 minutes. To run:

    cd sfdc-ticket-monitor
    mvn clean package
    nohup java -jar target/sfdc-ticket-monitor-1.0-SNAPSHOT.jar &
