package com.example.melms.service;

import com.example.melms.mapper.RepairTicketMapper;
import com.example.melms.mapper.ScrapApplicationMapper;
import com.example.melms.pojo.DashboardVO;
import com.example.melms.pojo.Equipment;
import com.example.melms.mapper.EquipmentMapper;
import com.example.melms.pojo.ProcureOrder;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    public Equipment findById(int id) {
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

    public void onboardEquipment(ProcureOrder order) {
        for (int i = 0; i < order.getCount(); i++) {
            Equipment equipment = new Equipment();
            equipment.setEquipmentTypeId(order.getEquipmentTypeId());
            equipment.setSupplierId(order.getSupplierId());
            equipment.setStatus("assigning");
            equipmentMapper.insertEquipment(equipment);
        }
        equipmentMapper.markOrderFinished(order.getProcureId());
    }

    public void assignDepartment(Integer equipmentId, Integer departmentId) {
        Equipment eq = equipmentMapper.findById(equipmentId);
        if (eq == null) {
            throw new RuntimeException("Device does not exist");
        }
        if (!"assigning".equals(eq.getStatus())) {
            throw new RuntimeException("The device status is not \"assigning\", unable to allocate");
        }
        equipmentMapper.updateDepartmentAndStatus(equipmentId, departmentId, "using");
    }

    public void saveFile(Integer equipmentId, String kind, String url) throws IOException {

        if ("Manual".equals(kind)) {
            equipmentMapper.updateManualPath(equipmentId, url);
        } else if ("Warranty".equals(kind)) {
            equipmentMapper.updateWarrantyPath(equipmentId, url);
        }

    }
}
