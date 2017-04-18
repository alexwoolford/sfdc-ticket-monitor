#!/usr/bin/env python

from simple_salesforce import Salesforce
from dateutil import parser

from sqlalchemy.ext.automap import automap_base
from sqlalchemy.orm import Session
from sqlalchemy import create_engine

from jinja2 import Environment, PackageLoader

import sendgrid
from sendgrid.helpers.mail import *

import configparser


class SfdcTicketMonitor:

    """SfdcTicketMonitor emails any new SalesForce.com support cases.

    The .run method of this class upserts all the tickets for the SE into a table and then emails any tickets that have
    not previously been sent to the SE.

    TODO:
        * add logging; error handling
        * include/capture the person who opened the support case in the email
    """

    def __init__(self):
        self.config = configparser.ConfigParser()
        self.config.read('sfdc_ticket_monitor.ini')

        self.Base = automap_base()
        self.engine = create_engine("mysql://{0}:{1}@{2}:{3}/{4}".format(self.config['mysql']['username'],
                                                                         self.config['mysql']['password'],
                                                                         self.config['mysql']['host'],
                                                                         self.config['mysql']['port'],
                                                                         self.config['mysql']['dbname']))
        self.Base.prepare(self.engine, reflect=True)
        self.Ticket = self.Base.classes.ticket
        self.Notification = self.Base.classes.notification
        self.session = Session(self.engine)
        self.env = Environment(loader=PackageLoader('sfdc_ticket_monitor', 'templates'))

    def capture_tickets(self):
        sf = Salesforce(username=self.config['sfdc']['email'], password=self.config['sfdc']['password'], security_token=self.config['sfdc']['token'])
        sql_accounts = "SELECT accountid FROM opportunity WHERE opportunity.lead_se__c = '{0}' GROUP BY accountid".format(self.config['sfdc']['name'])
        accounts = sf.query(sql_accounts)

        for account in accounts['records']:
            account_name = sf.query("SELECT name FROM account WHERE id='{0}'".format(account['AccountId']))['records'][0]['Name']
            sql_cases = "SELECT casenumber, contactid, description, reason, status, priority, problem_type__c, lastvieweddate FROM case WHERE accountId='{0}'".format(account['AccountId'])
            cases = sf.query(sql_cases)
            for case in cases['records']:

                # parse LastViewedDate
                try:
                    last_viewed_date = parser.parse(case['LastViewedDate'], ignoretz=True)
                except:
                    last_viewed_date = None

                self.session.merge(self.Ticket(CaseNumber=case['CaseNumber'],
                                               AccountName=account_name,
                                               AccountId=account['AccountId'],
                                               ContactId=case['ContactId'],
                                               Description=case['Description'],
                                               LastViewedDate=last_viewed_date,
                                               Priority=case['Priority'],
                                               Problem_Type__c=case['Problem_Type__c'],
                                               Reason=case['Reason'],
                                               Status=case['Status']))
                self.session.commit()

    def notify(self):

        unnotified_tickets = self.session.query(self.Ticket).outerjoin(self.Notification).filter(self.Notification.CaseNumber == None).filter(self.Ticket.Status == "Open").all()

        template = self.env.get_template('ticket_email.html')

        for ticket in unnotified_tickets:
            subject = "case {0} opened by {1}".format(ticket.CaseNumber, ticket.AccountName)
            html_content = template.render(ticket=ticket)
            self.email_html(subject=subject, html_content=html_content)
            self.session.add(self.Notification(CaseNumber=ticket.CaseNumber, NotificationSent=True))
            self.session.commit()

    def email_html(self, subject, html_content):
        sg = sendgrid.SendGridAPIClient(apikey=self.config['sendgrid']['apikey'])
        from_email = Email(self.config['sendgrid']['from'])
        subject = subject
        to_email = Email(self.config['sendgrid']['to'])
        content = Content("text/html", html_content)
        mail = Mail(from_email, subject, to_email, content)
        response = sg.client.mail.send.post(request_body=mail.get())
        # TODO: check that message was sent

    def run(self):
        self.capture_tickets()
        self.notify()

    def __del__(self):
        self.session.close()

if __name__ == "__main__":
    sfdc_ticket_monitor = SfdcTicketMonitor()
    sfdc_ticket_monitor.run()
    del sfdc_ticket_monitor



