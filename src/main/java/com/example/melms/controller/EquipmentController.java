package com.example.melms.controller;

import com.example.melms.pojo.DashboardVO;
import com.example.melms.pojo.Equipment;
import com.example.melms.pojo.ProcureOrder;
import com.example.melms.service.DepartmentService;
import com.example.melms.service.EquipmentService;
import com.example.melms.service.ProcurementService;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/req")
@CrossOrigin
public class EquipmentController {

    @Resource
    private EquipmentService equipmentService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private ProcurementService procurementService;

    // mapping between DB enum code and frontend label
    private static final Map<String, String> codeToLabel = new LinkedHashMap<>();
    private static final Map<String, String> labelToCode = new HashMap<>();
    static {
        // DB codes: assigning, using, repairing, scrapping
        codeToLabel.put("assigning", "Unassigned");
        codeToLabel.put("using", "In Use");
        codeToLabel.put("repairing", "Under Repair");
        codeToLabel.put("scrapping", "Pending Scrap");
        // reverse map
        codeToLabel.forEach((k,v) -> labelToCode.put(v, k));
        // include direct codes as labels fallback (if frontend already sends codes)
        labelToCode.put("assigning","assigning");
        labelToCode.put("using","using");
        labelToCode.put("repairing","repairing");
        labelToCode.put("scrapping","scrapping");
    }

    // GET /api/device-statuses
    @GetMapping("/device-statuses")
    public List<String> getStatuses() {
        return new ArrayList<>(codeToLabel.values());
    }

    // GET /api/devices
    // optional query params for server-side filtering (frontend currently does client-side, but supported)
    @GetMapping("/devices")
    public List<Equipment> getDevices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) List<String> departmentIds,
            @RequestParam(required = false) List<String> vendors
    ) {
        List<Equipment> list = equipmentService.listAll();

        // convert DB status code -> label for each item
        list.forEach(e -> {
            String code = Optional.ofNullable(e.getStatus()).orElse("");
            if (codeToLabel.containsKey(code)) e.setStatus(codeToLabel.get(code));
        });

        // simple server-side filtering (if params provided)
        if ((keyword == null || keyword.isEmpty()) &&
                (types == null || types.isEmpty()) &&
                (statuses == null || statuses.isEmpty()) &&
                (departmentIds == null || departmentIds.isEmpty()) &&
                (vendors == null || vendors.isEmpty())) {
            return list;
        }

        final String kw = keyword == null ? "" : keyword.toLowerCase();
        return list.stream().filter(d -> {
            boolean match = true;
            if (!kw.isEmpty()) {
                String hay = String.join(" ",
                        String.valueOf(d.getEquipmentId()),
                        Optional.ofNullable(d.getEquipmentTypeName()).orElse(""),
                        Optional.ofNullable(d.getSupplierId()).orElse("")
                ).toLowerCase();
                match = match && hay.contains(kw);
            }
            if (types != null && !types.isEmpty()) {
                match = match && types.contains(Optional.ofNullable(d.getEquipmentTypeName()).orElse(d.getEquipmentTypeId()));
            }
            if (statuses != null && !statuses.isEmpty()) {
                // statuses param expected to be frontend labels; d.getStatus() already label
                match = match && statuses.contains(d.getStatus());
            }
            if (departmentIds != null && !departmentIds.isEmpty()) {
                match = match && departmentIds.contains(String.valueOf(d.getDepartmentId()));
            }
            if (vendors != null && !vendors.isEmpty()) {
                match = match && vendors.contains(d.getSupplierId());
            }
            return match;
        }).collect(Collectors.toList());
    }

    // GET /api/devices/{id}
    @GetMapping("/devices/{id}")
    public Equipment getDevice(@PathVariable int id) {
        Equipment e = equipmentService.findById(id);
        if (e == null) return null;
        String code = Optional.ofNullable(e.getStatus()).orElse("");
        if (codeToLabel.containsKey(code)) e.setStatus(codeToLabel.get(code));
        return e;
    }

    // POST /api/devices  add new
    @PostMapping("/devices")
    public Equipment addDevice(@RequestBody Equipment equipment) {
        // convert status label -> code before insert
        if (equipment.getStatus() != null) {
            String provided = equipment.getStatus();
            String code = labelToCode.getOrDefault(provided, provided); // fallback to provided
            equipment.setStatus(code);
        }
        equipmentService.add(equipment);
        // after insert, return inserted item (convert code->label for response)
        Equipment saved = equipmentService.findById(equipment.getEquipmentId());
        if (saved != null && codeToLabel.containsKey(saved.getStatus())) saved.setStatus(codeToLabel.get(saved.getStatus()));
        return saved;
    }

    // PUT /api/devices/{id} update
    @PutMapping("/devices/{id}")
    public Equipment updateDevice(@PathVariable int id, @RequestBody Equipment equipment) {
        // ensure id consistency
        equipment.setEquipmentId(id);
        if (equipment.getStatus() != null) {
            String code = labelToCode.getOrDefault(equipment.getStatus(), equipment.getStatus());
            equipment.setStatus(code);
        }
        equipmentService.update(equipment);
        Equipment saved = equipmentService.findById(id);
        if (saved != null && codeToLabel.containsKey(saved.getStatus())) saved.setStatus(codeToLabel.get(saved.getStatus()));
        return saved;
    }

    @DeleteMapping("/devices/{id}")
    public Map<String,String> deleteDevice(@PathVariable String id) {
        equipmentService.deleteById(id);
        return Collections.singletonMap("result", "ok");
    }

    @GetMapping("/departments")
    public List<?> getDepartments() {
        return departmentService.listAll();
    }

    @GetMapping("/pending")
    public List<ProcureOrder> getPendingOrders() {
        return equipmentService.getPendingOrders();
    }

    @PostMapping("/{id}/accept")
    public String acceptOrder(@PathVariable("id") String id) {
        boolean ok = equipmentService.acceptOrder(id);
        return ok ? "Order accepted" : "Failed to accept";
    }

    @PostMapping("/{id}/reject")
    public String rejectOrder(@PathVariable("id") String id) {
        boolean ok = equipmentService.rejectOrder(id);
        return ok ? "Order rejected" : "Failed to reject";
    }

    @GetMapping("/dashboard/overview")
    public DashboardVO getOverview() {
        return equipmentService.getDashboardOverview();
    }
    @GetMapping("/arrived-orders")
    public List<ProcureOrder> getArrivedOrders() {
        return procurementService.getArrivedOrders();
    }
    @PostMapping("/onboard/{procureId}")
    public String onboard(@PathVariable Integer procureId) {
        List<ProcureOrder> orders = procurementService.getArrivedOrders();
        for (ProcureOrder order : orders) {
            if (order.getProcureId().equals(procureId)) {
                equipmentService.onboardEquipment(order);
                return "The warehousing has been completed";
            }
        }
        return "The order was not found or the status is incorrect";
    }

    @PostMapping("/devices/{id}/assign")
    public String assignDepartment(@PathVariable("id") Integer equipmentId,
                                   @RequestBody AssignRequest req) {
        equipmentService.assignDepartment(equipmentId, req.getDepartmentId());
        return "success";
    }

    @Data
    public static class AssignRequest {
        private Integer departmentId;
    }

    @Value("${upload.base-dir}")
    private String baseDir;

    @PostMapping("/devices/{equipmentId}/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @PathVariable Integer equipmentId,
            @RequestParam("kind") String kind,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        try {
            String dir;
            if ("Manual".equalsIgnoreCase(kind)) {
                dir = "manuals";
            } else if ("Warranty".equalsIgnoreCase(kind)) {
                dir = "warranties";
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Unknown kind"));
            }

            // 确保目录存在
            Path uploadPath = Paths.get(baseDir, dir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 保存文件，防止覆盖
            String originalFilename = file.getOriginalFilename();
            String filename = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(filename);
            file.transferTo(filePath.toFile());

            // 构造前端访问 URL
            String fileUrl = "/" + dir + "/" + filename;

            equipmentService.saveFile(equipmentId, kind, fileUrl);

            return ResponseEntity.ok(Map.of("url", fileUrl));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Upload failed"));
        }
    }
}
