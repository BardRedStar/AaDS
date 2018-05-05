package com.tenxgames.aisd.sqlite;

public class SortRecord {
    public int id;
    public String time;
    public String sortTime;
    public String sequenceStart;
    public String sequenceSorted;

    public SortRecord(int id, String time, String sortTime, String sequenceStart, String sequenceSorted)
    {
        this.id = id;
        this.time = time;
        this.sortTime = sortTime;
        this.sequenceStart = sequenceStart;
        this.sequenceSorted = sequenceSorted;
    }
}
