package com.example.melms.service;

import com.example.melms.mapper.ProcurementMapper;
import com.example.melms.pojo.ProcureOrder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProcurementService {
    @Resource
    private ProcurementMapper procurementMapper;

    public List<ProcureOrder> getArrivedOrders() {
        return procurementMapper.getArrivedOrders();
    }
}
