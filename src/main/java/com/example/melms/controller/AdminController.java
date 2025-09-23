package com.example.melms.controller;

import com.example.melms.pojo.Result;
import org.springframework.web.bind.annotation.*;

@RestController
public class AdminController {
    @GetMapping("/req/admin/overall")
    public Result getStats() {
        return Result.fail("","",null);
    }

    @PostMapping("/req/admin/resetPwd")
    public Result resetPwd(@RequestParam String accountId, @RequestBody String newPwd) {
        return Result.fail("","",null);
    }

    @PostMapping("/req/admin/setRole")
    public Result setRole(@RequestParam String accountId, @RequestBody int roleId) {
        return Result.fail("","",null);
    }

    @PostMapping("/req/admin/newDepartment")
    public Result newDepartment(@RequestBody String departmentName) {
        return Result.fail("","",null);
    }

    @PostMapping("/req/admin/changeDepartmentName")
    public Result changeDepartmentName(@RequestParam String departmentId,@RequestBody String departmentName) {
        return Result.fail("","",null);
    }

    @DeleteMapping("/req/admin/delDepartment")
    public Result delDepartment(@RequestParam String departmentId) {
        return Result.fail("","",null);
    }

    @GetMapping("/req/admin/getMaintenanceModeStatus")
    public Result getMaintenanceModeStatus() {
        return Result.fail("","",null);
    }

    @GetMapping("/req/admin/changeMaintenanceModeStatus")
    public Result changeMaintenanceModeStatus() {
        return Result.fail("","",null);
    }
}
