package io.woolford.database.entity;

import java.util.Date;

public class RunStats {

    private Date startRun;
    private Date endRun;
    private int sfdcQueries = 0;
    private int cacheHits = 0;
    private int exceptions = 0;
    private int emails = 0;

    public void initialize(){
        this.startRun = new Date();
        this.endRun = null;
        this.sfdcQueries = 0;
        this.cacheHits = 0;
        this.exceptions = 0;
        this.emails = 0;
    }

    public Date getStartRun() {
        return startRun;
    }

    public void setStartRun() {
        this.startRun = new Date();
    }

    public Date getEndRun() {
        return endRun;
    }

    public void setEndRun() {
        this.endRun = new Date();
    }

    public int getSfdcQueries() {
        return sfdcQueries;
    }

    public void incrementSfdcQueries() {
        this.sfdcQueries++;
    }

    public int getCacheHits() {
        return cacheHits;
    }

    public void incrementCacheHits() {
        this.cacheHits++;
    }

    public int getExceptions() {
        return exceptions;
    }

    public void incrementExceptions() {
        this.exceptions++;
    }

    public int getEmails() {
        return emails;
    }

    public void incrementEmails() {
        this.emails++;
    }


    @Override
    public String toString() {
        return "RunStats{" +
                "startRun=" + startRun +
                ", endRun=" + endRun +
                ", sfdcQueries=" + sfdcQueries +
                ", cacheHits=" + cacheHits +
                ", exceptions=" + exceptions +
                ", emails=" + emails +
                '}';
    }

}
