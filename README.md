# SFDC ticket monitor

I felt it was important, in order to be a proactive Solutions Engineer, to get notified when customers open support cases.

This app uses MySQL to persist ticket/notification statuses, SalesForce.com as the source of tickets, and Sendgrid to email. In order to run, it's necessary to edit a couple of properties files:
1. `sfdc-ticket-monitor-migration/flyway.properties`
2. `sfdc_ticket_monitor/sfdc_ticket_monitor.ini`

To create the MySQL tables, run the Flyway migration:

    cd sfdc-ticket-monitor-migration
    mvn clean compile flyway:migrate

The run the app (preferably via cron, to get regular updates):

    cd sfdc_ticket_monitor
    python sfdc_ticket_monitor.py
