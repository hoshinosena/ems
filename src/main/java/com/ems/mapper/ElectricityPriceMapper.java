package com.ems.mapper;

import com.ems.po.ElectricityPrice;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ElectricityPriceMapper {
    @Select("select max(pid) from eprice")
    int selectMaxPid();
    @Select("select * from eprice where pid=#{pid}")
    ElectricityPrice selectPrice(int pid);
    @Select("select * from eprice order by pid desc")
    List<ElectricityPrice> selectPrices();
    @Insert("insert into eprice(price,date,info) "
            + "values(#{price},#{date},#{info})")
    int insertPrice(ElectricityPrice electricityPrice);
}
