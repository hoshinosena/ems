package com.ems.service;

import com.ems.po.ElectricityPrice;

import java.util.List;

public interface ElectricityPriceService {
    // 查询最新电价
    ElectricityPrice findPrice();
    List<ElectricityPrice> findPrices();
    // 更新电价
    int addPrice(ElectricityPrice electricityPrice);
}
