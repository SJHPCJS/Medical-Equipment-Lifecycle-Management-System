package com.example.melms.controller;

import com.example.melms.mapper.AdminMapper;
import com.example.melms.mapper.LogMapper;
import com.example.melms.pojo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AdminController {

    @Resource
    private AdminMapper adminMapper;

    @Resource
    private LogMapper logMapper;

    // Dashboard - overall stats
    @GetMapping("/req/admin/overall")
    public Result getStats() {
        try {
            int employeeCount = adminMapper.getEmployeeCount();
            int departmentCount = adminMapper.getDepartmentCount();
            int ticketCount = adminMapper.getWorkOrderCount() + adminMapper.getProcureOrderCount();
            Map<String, Object> data = new HashMap<>();
            data.put("employeeCount", employeeCount);
            data.put("departmentCount", departmentCount);
            data.put("ticketCount", ticketCount);
            return Result.success("ok", data);
        } catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // User Management - list
    @GetMapping("/req/admin/users")
    public Result listUsers() {
        try {
            List<Map<String, Object>> users = adminMapper.listUsers();
            return Result.success("ok", users);
        } catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // User Management - create
    @PostMapping("/req/admin/user")
    public Result createUser(@RequestBody Map<String, String> payload) {
        try {
            String name = payload.get("name");
            String password = payload.get("password");
            String role = payload.get("role");
            String departmentId = payload.get("department_id");
            if (name == null || name.trim().isEmpty()) return Result.fail("400","Name is required", null);
            if (password == null || password.trim().isEmpty()) return Result.fail("400","Password is required", null);
            if (adminMapper.countByName(name) > 0) return Result.fail("409","Name already exists", null);
            adminMapper.addNewUser(name, password, role, departmentId);
            return Result.success("ok", null);
        } catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // User Management - update
    @PutMapping("/req/admin/user")
    public Result updateUser(@RequestBody Map<String, String> payload) {
        try {
            int accountId = Integer.parseInt(payload.getOrDefault("account_id", "0"));
            String name = payload.get("name");
            String password = payload.get("password");
            String role = payload.get("role");
            String departmentId = payload.get("department_id");
            if (name == null || name.trim().isEmpty()) return Result.fail("400","Name is required", null);
            if (adminMapper.countByNameExcludingId(name, accountId) > 0) return Result.fail("409","Name already exists", null);
            if (password == null || password.trim().isEmpty()) {
                adminMapper.updateUserMeta(accountId, name, departmentId, role);
            } else {
                adminMapper.updateUser(accountId, name, password, role, departmentId);
            }
            return Result.success("ok", null);
        } catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // User Management - delete
    @DeleteMapping("/req/admin/user")
    public Result deleteUser(@RequestParam int accountId) {
        try {
            adminMapper.deleteUser(accountId);
            return Result.success("ok", null);
        } catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // User Management - reset password
    @PostMapping("/req/admin/resetPwd")
    public Result resetPwd(@RequestBody Map<String, String> payload) {
        try {
            int accountId = Integer.parseInt(payload.getOrDefault("accountId", "0"));
            String newPwd = payload.get("newPwd");
            adminMapper.resetPassword(accountId, newPwd);
            return Result.success("ok", null);
        } catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // User Management - set role
    @PostMapping("/req/admin/setRole")
    public Result setRole(@RequestBody Map<String, String> payload) {
        try {
            int accountId = Integer.parseInt(payload.getOrDefault("accountId", "0"));
            String role = payload.get("role");
            adminMapper.setRole(accountId, role);
            return Result.success("ok", null);
        } catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // Departments - list
    @GetMapping("/req/admin/departments")
    public Result listDepartments() {
        try {
            List<Map<String, Object>> deps = adminMapper.getDepartments();
            return Result.success("ok", deps);
        } catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // Departments - create (auto-increment id by DB)
    @PostMapping("/req/admin/newDepartment")
    public Result newDepartment(@RequestBody Map<String, String> payload) {
        try {
            String departmentName = payload.get("department_name");
            if (departmentName == null || departmentName.trim().isEmpty()) return Result.fail("400","Department name is required", null);
            if (adminMapper.countDepartmentByName(departmentName) > 0) return Result.fail("409","Department name already exists", null);
            adminMapper.addNewDepartment(departmentName);
            return Result.success("ok", null);
        } catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // Departments - update name
    @PostMapping("/req/admin/changeDepartmentName")
    public Result changeDepartmentName(@RequestBody Map<String, String> payload) {
        try {
            String departmentId = payload.get("department_id");
            String departmentName = payload.get("department_name");
            if (departmentName == null || departmentName.trim().isEmpty()) return Result.fail("400","Department name is required", null);
            if (adminMapper.countDepartmentByNameExcludingId(departmentName, departmentId) > 0) return Result.fail("409","Department name already exists", null);
            adminMapper.updateDepartmentName(departmentId, departmentName);
            return Result.success("ok", null);
        } catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // Departments - delete
    @DeleteMapping("/req/admin/delDepartment")
    public Result delDepartment(@RequestParam String departmentId) {
        try {
            adminMapper.deleteDepartment(departmentId);
            return Result.success("ok", null);
        } catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // Logs - list
    @GetMapping("/req/admin/logs")
    public Result getLogs() {
        try {
            return Result.success("ok", logMapper.getLogs());
        } catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // Logs - create
    @PostMapping("/req/admin/log")
    public Result addLog(@RequestBody Map<String, String> payload) {
        try {
            String action = payload.get("log_action");
            String userId = payload.get("log_user_id");
            logMapper.addNewLog(action, userId);
            return Result.success("ok", null);
        } catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // System setting - skip for now
    @GetMapping("/req/admin/getMaintenanceModeStatus")
    public Result getMaintenanceModeStatus() {
        return Result.fail("501","Not implemented",null);
    }

    @GetMapping("/req/admin/changeMaintenanceModeStatus")
    public Result changeMaintenanceModeStatus() {
        return Result.fail("501","Not implemented",null);
    }
}
