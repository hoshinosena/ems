package com.ems.service.impl;

import com.ems.mapper.ElectricityPriceMapper;
import com.ems.mapper.RecordMapper;
import com.ems.po.Record;
import com.ems.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class RecordServiceImpl implements RecordService {
    @Autowired
    RecordMapper recordMapper;
    @Autowired
    ElectricityPriceMapper electricityPriceMapper;

    @Override
    public List<Record> findRecords(int uid) {
        return recordMapper.selectRecordsByUid(uid);
    }

    @Override
    public List<Record> findRecords() {
        return recordMapper.selectRecords();
    }

    @Override
    public int addRecord(Record record) {
        if (record.getInfo() == null) {
            record.setInfo("");
        }
        record.setDate(new Timestamp(System.currentTimeMillis()).toString());
        record.setPid(electricityPriceMapper.selectMaxPid());
        return recordMapper.insertRecord(record);
    }

    @Override
    public int delRecord(int uid) {
        return recordMapper.deleteRecord(uid);
    }
}
