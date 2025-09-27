package com.example.melms.service;

import com.example.melms.mapper.DepartmentMapper;
import com.example.melms.pojo.Department;
import com.example.melms.mapper.EquipmentMapper;
import com.example.melms.mapper.LogMapper;
import com.example.melms.mapper.RepairTicketMapper;
import com.example.melms.pojo.Equipment;
import com.example.melms.pojo.UsageLog;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DepartmentService {
    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private EquipmentMapper equipmentMapper;

    @Resource
    private LogMapper logMapper;

    @Resource
    private RepairTicketMapper repairTicketMapper;

    public List<Department> listAll() {
        return departmentMapper.findAll();
    }

    public Department getById(Integer id) {
        return departmentMapper.findById(id);
    }

    public int add(Department department) {
        return departmentMapper.insert(department);
    }

    public int update(Department department) {
        return departmentMapper.update(department);
    }

    public int delete(Integer id) {
        return departmentMapper.delete(id);
    }

    public Map<String, Object> getDashboardStats() {

        // 获取尚未确认的维修单数量（针对当前部门）
        // 假设当前部门ID在用户信息或上下文中获取
        String departmentId = "0001";  // 这里可以通过实际的上下文获取当前用户所在的部门
        int todosRepair = repairTicketMapper.countPendingRepairTickets(departmentId);
        // 获取设备统计信息
        int inUse = equipmentMapper.countDevicesByStatus("using", departmentId);  // 正在使用的设备数量
        int underRepair = equipmentMapper.countDevicesByStatus("repairing", departmentId);  // 维修中的设备数量

        // 获取今天的使用日志数量
        int todosUsage = logMapper.countLogsForToday(departmentId);

        // 返回统计数据
        return Map.of(
                "inUse", inUse,
                "underRepair", underRepair,
                "todosUsage", todosUsage,
                "todosRepair", todosRepair
        );
    }

    public List<UsageLog> getUsageLogsByEquipmentId(String equipmentId) {
        return departmentMapper.findByTargetEquipmentId(equipmentId);
    }

    public boolean addUsageLog(UsageLog usageLog) {
        try {
            // 使用 MyBatis 或 JPA 进行插入
            departmentMapper.insertUsageLog(usageLog);  // 如果使用 MyBatis
            // 或者
            // usageLoggingRepository.save(usageLog);  // 如果使用 JPA
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}




