package com.ems.mapper;

import org.apache.ibatis.annotations.*;

import com.ems.po.Record;

import java.util.List;

@Mapper
public interface RecordMapper {
    @Select("select * from record where uid=#{uid} order by rid desc")
    List<Record> selectRecordsByUid(int uid);
    @Select("select * from record order by rid desc")
    List<Record> selectRecords();
    @Insert("insert into record(op,changes,date,info,pid,uid) "
            + "values(#{op},#{changes},#{date},#{info},#{pid},#{uid})")
    int insertRecord(Record record);
    @Delete("delete from record where uid=#{uid}")
    int deleteRecord(int uid);
}
