package com.ems.service;

import com.ems.po.Record;
import java.util.List;

public interface RecordService {
    List<Record> findRecords(int uid);
    List<Record> findRecords();
    int addRecord(Record record);
    int delRecord(int uid);
}
