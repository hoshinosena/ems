package com.ems.service.impl;

import com.ems.mapper.ElectricityPriceMapper;
import com.ems.po.ElectricityPrice;
import com.ems.service.ElectricityPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ElectricityPriceServiceImpl implements ElectricityPriceService {
    @Autowired
    ElectricityPriceMapper electricityPriceMapper;

    @Override
    public ElectricityPrice findPrice() {
        return electricityPriceMapper.selectPrice(electricityPriceMapper.selectMaxPid());
    }

    @Override
    public List<ElectricityPrice> findPrices() {
        return electricityPriceMapper.selectPrices();
    }

    @Override
    public int addPrice(ElectricityPrice electricityPrice) {
        if (electricityPrice.getInfo() == null) {
            electricityPrice.setInfo("");
        }
        electricityPrice.setDate(new Timestamp(System.currentTimeMillis()).toString());
        return electricityPriceMapper.insertPrice(electricityPrice);
    }
}
