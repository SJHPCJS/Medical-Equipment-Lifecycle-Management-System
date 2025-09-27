package com.example.melms.controller;

import com.example.melms.mapper.ProcurementMapper;
import com.example.melms.pojo.ProcureOrder;
import com.example.melms.pojo.Result;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ProcurementController {

    @Resource
    private ProcurementMapper mapper;

    /* ====================== Suppliers ====================== */

    // 供应商列表
    @GetMapping("/req/proc/vendors")
    public Result listVendors() {
        try {
            List<Map<String, Object>> data = mapper.listVendors();
            return Result.success("ok", data);
        }
        catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // 新增供应商（自动生成4位 supplier_id）
    @PostMapping("/req/proc/vendor")
    public Result createVendor(@RequestBody Map<String, String> payload) {
        try {
            String name = payload.get("supplier_name");
            String contact = payload.getOrDefault("contact", "");
            if (name == null || name.trim().isEmpty()) {
                return Result.fail("400", "Supplier name is required", null);
            }
            String newId = mapper.nextSupplierId();
            mapper.addVendor(newId, name.trim(), contact);
            return Result.success("ok", Map.of("supplier_id", newId));
        }
        catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    /* ================== Equipment Types ==================== */

    // 设备类型列表
    @GetMapping("/req/proc/equipmentTypes")
    public Result listEquipmentTypes() {
        try {
            return Result.success("ok", mapper.listEquipmentTypes());
        }
        catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    /* ======================== Quotes ======================= */

    // 报价（供应商 × 设备类型）列表
    @GetMapping("/req/proc/quotes")
    public Result listQuotes() {
        try {
            return Result.success("ok", mapper.listQuotes());
        }
        catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // 新建或覆盖报价（若存在则更新）
    @PostMapping("/req/proc/quote")
    public Result addOrUpsertQuote(@RequestBody Map<String, Object> payload) {
        try {
            String supplierId = String.valueOf(payload.get("supplier_id"));
            String typeId = String.valueOf(payload.get("equipment_type_id"));
            Number priceNum = (Number) payload.getOrDefault("price", 0);
            int price = priceNum == null ? 0 : priceNum.intValue();

            if (supplierId == null || supplierId.isBlank() || typeId == null || typeId.isBlank()) {
                return Result.fail("400", "supplier_id & equipment_type_id are required", null);
            }

            Integer cnt = mapper.countQuote(supplierId, typeId);
            if (cnt != null && cnt > 0) {
                mapper.updateQuote(supplierId, typeId, price);
            }
            else {
                mapper.addQuote(supplierId, typeId, price);
            }
            return Result.success("ok", null);
        }
        catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // 更新报价
    @PutMapping("/req/proc/quote")
    public Result updateQuote(@RequestBody Map<String, Object> payload) {
        try {
            String supplierId = String.valueOf(payload.get("supplier_id"));
            String typeId = String.valueOf(payload.get("equipment_type_id"));
            Number priceNum = (Number) payload.getOrDefault("price", 0);
            int price = priceNum == null ? 0 : priceNum.intValue();
            mapper.updateQuote(supplierId, typeId, price);
            return Result.success("ok", null);
        }
        catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // 删除报价
    @DeleteMapping("/req/proc/quote")
    public Result deleteQuote(@RequestParam("supplierId") String supplierId,
                              @RequestParam("equipmentTypeId") String typeId) {
        try {
            mapper.deleteQuote(supplierId, typeId);
            return Result.success("ok", null);
        }
        catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    /* ======================== Orders ======================= */

    // 采购订单列表（含 under-review / procuring / arrived / terminated）
    @GetMapping("/req/proc/orders")
    public Result listOrders() {
        try {
            return Result.success("ok", mapper.listOrders());
        }
        catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // 更新订单状态
    @PutMapping("/req/proc/order/status")
    public Result changeOrderStatus(@RequestBody Map<String, String> payload) {
        try {
            int id = Integer.parseInt(payload.getOrDefault("procure_id", "0"));
            String status = payload.get("status");
            mapper.updateOrderStatus(id, status);
            return Result.success("ok", null);
        }
        catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }

    // 指派供应商并更新数量（请购流转为采购中）
    @PutMapping("/req/proc/order/assign")
    public Result assignOrder(@RequestBody Map<String, Object> payload) {
        try {
            int id = Integer.parseInt(String.valueOf(payload.getOrDefault("procure_id", "0")));
            String supplierId = String.valueOf(payload.get("supplier_id"));
            int count = Integer.parseInt(String.valueOf(payload.getOrDefault("count", "0")));
            mapper.assignOrder(id, supplierId, count);
            return Result.success("ok", null);
        }
        catch (Exception e) {
            return Result.fail("500", e.getMessage(), null);
        }
    }
}
