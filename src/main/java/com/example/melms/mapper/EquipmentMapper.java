package com.example.melms.mapper;

import com.example.melms.pojo.Equipment;
import com.example.melms.pojo.ProcureOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface EquipmentMapper {

    @Select("SELECT e.*, t.equipment_type_name, d.department_name " +
            "FROM tb_equipment e " +
            "LEFT JOIN tb_equipment_type t ON e.equipment_type_id = t.equipment_type_id " +
            "LEFT JOIN tb_department d ON e.department_id = d.department_id")
    List<Equipment> findAll();

    @Select("SELECT e.*, t.equipment_type_name, d.department_name " +
            "FROM tb_equipment e " +
            "LEFT JOIN tb_equipment_type t ON e.equipment_type_id = t.equipment_type_id " +
            "LEFT JOIN tb_department d ON e.department_id = d.department_id " +
            "WHERE e.department_id = #{departmentId}")
    List<Equipment> findByDepartmentId(String departmentId);

    @Select("SELECT e.*, t.equipment_type_name, d.department_name " +
            "FROM tb_equipment e " +
            "LEFT JOIN tb_equipment_type t ON e.equipment_type_id = t.equipment_type_id " +
            "LEFT JOIN tb_department d ON e.department_id = d.department_id " +
            "WHERE e.equipment_id = #{equipmentId}")
    Equipment findById(String equipmentId);

    @Insert("INSERT INTO tb_equipment(equipment_id, equipment_type_id, status, user_manual_path, warranty_certificate_path, supplier_id, department_id, pic_url) " +
            "VALUES(#{equipmentId}, #{equipmentTypeId}, #{status}, #{userManualPath}, #{warrantyCertificatePath}, #{supplierId}, #{departmentId}, #{picUrl})")
    int insert(Equipment equipment);

    @Update("UPDATE tb_equipment SET equipment_type_id=#{equipmentTypeId}, status=#{status}, user_manual_path=#{userManualPath}, " +
            "warranty_certificate_path=#{warrantyCertificatePath}, supplier_id=#{supplierId}, department_id=#{departmentId}, pic_url=#{picUrl} " +
            "WHERE equipment_id=#{equipmentId}")
    int update(Equipment equipment);

    @Delete("DELETE FROM tb_equipment WHERE equipment_id=#{equipmentId}")
    int delete(String equipmentId);

    @Select("SELECT * FROM tb_procure_order WHERE status = 'arrived'")
    List<ProcureOrder> findPendingOrders();

    @Update("UPDATE tb_procure_order SET status = #{status} WHERE procure_id = #{procureId}")
    int updateStatus(@Param("procureId") String procureId, @Param("status") String status);

    @Select("SELECT * FROM tb_procure_order WHERE procure_id = #{procureId}")
    ProcureOrder findProById(String procureId);

    @Select("""
        SELECT status, COUNT(*) AS count 
        FROM tb_equipment 
        GROUP BY status
    """)
    List<Map<String, Object>> countGroupByStatus();

    default Map<String, Integer> countByStatus() {
        List<Map<String, Object>> list = countGroupByStatus();
        return list.stream().collect(
                java.util.stream.Collectors.toMap(
                        m -> switch ((String)m.get("status")) {
                            case "assigning" -> "unassigned";
                            case "using" -> "inUse";
                            case "repairing" -> "underRepair";
                            case "scrapping" -> "pendingScrap";
                            default -> "unknown";
                        },
                        m -> ((Number)m.get("count")).intValue()
                )
        );
    }
}
