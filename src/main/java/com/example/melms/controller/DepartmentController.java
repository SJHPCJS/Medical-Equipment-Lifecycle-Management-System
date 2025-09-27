package com.example.melms.controller;

import com.example.melms.pojo.Equipment;
import com.example.melms.pojo.UsageLog;
import com.example.melms.service.DepartmentService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/req")
@CrossOrigin
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @GetMapping("/dept/dashboard/stats")
    public Object getDashboardStats() {
        return departmentService.getDashboardStats();
    }

    @GetMapping("/dept/usage/logs")
    public List<UsageLog> getUsageLogs(@RequestParam("equipmentId") String equipmentId) {
        return departmentService.getUsageLogsByEquipmentId(equipmentId);
    }

    @PostMapping("/dept/usage/logs")
    public String addUsageLog(@RequestBody UsageLog usageLog) {
        boolean success = departmentService.addUsageLog(usageLog);
        if (success) {
            return "Usage log added successfully!";
        } else {
            return "Failed to add usage log.";
        }
    }
}
