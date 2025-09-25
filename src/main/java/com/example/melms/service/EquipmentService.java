package com.example.melms.service;

import com.example.melms.mapper.RepairTicketMapper;
import com.example.melms.mapper.ScrapApplicationMapper;
import com.example.melms.pojo.DashboardVO;
import com.example.melms.pojo.Equipment;
import com.example.melms.mapper.EquipmentMapper;
import com.example.melms.pojo.ProcureOrder;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EquipmentService {
    @Resource
    private EquipmentMapper equipmentMapper;

    @Resource
    private RepairTicketMapper repairTicketMapper;

    @Resource
    private ScrapApplicationMapper scrapApplicationMapper;

    public List<Equipment> listAll() {
        return equipmentMapper.findAll();
    }

    public int add(Equipment equipment) {
        return equipmentMapper.insert(equipment);
    }

    public int update(Equipment equipment) {
        return equipmentMapper.update(equipment);
    }

    public int deleteById(String id) {
        return equipmentMapper.delete(id);
    }

    public Equipment findById(String id) {
        return equipmentMapper.findById(id);
    }

    public List<ProcureOrder> getPendingOrders() {
        return equipmentMapper.findPendingOrders();
    }

    public boolean acceptOrder(String procureId) {
        return equipmentMapper.updateStatus(procureId, "procuring") > 0;
    }

    public boolean rejectOrder(String procureId) {
        return equipmentMapper.updateStatus(procureId, "terminated") > 0;
    }

    public DashboardVO getDashboardOverview() {
        DashboardVO vo = new DashboardVO();

        Map<String,Integer> counts = equipmentMapper.countByStatus();

        counts.put("pendingScrap", scrapApplicationMapper.countPendingScrap());

        vo.setEquipmentCounts(counts);
        vo.setTodayTickets(repairTicketMapper.countTodayTickets());

        return vo;
    }

}
