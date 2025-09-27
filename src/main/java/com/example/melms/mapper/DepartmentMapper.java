package com.example.melms.mapper;

import com.example.melms.pojo.Department;
import com.example.melms.pojo.UsageLog;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface DepartmentMapper {

    @Select("SELECT department_id, department_name FROM tb_department")
    List<Department> findAll();

    @Select("SELECT department_id, department_name FROM tb_department WHERE department_id = #{id}")
    Department findById(Integer id);

    @Insert("INSERT INTO tb_department(department_name) VALUES(#{departmentName})")
    @Options(useGeneratedKeys = true, keyProperty = "departmentId")
    int insert(Department department);

    @Update("UPDATE tb_department SET department_name = #{departmentName} WHERE department_id = #{departmentId}")
    int update(Department department);

    @Delete("DELETE FROM tb_department WHERE department_id = #{id}")
    int delete(Integer id);

    @Select("SELECT log_id, recorder_id, target_equipment_id, remark, time " +
            "FROM tb_usage_logging WHERE target_equipment_id = #{equipmentId}")
    @Results({
            @Result(property = "logId", column = "log_id"),
            @Result(property = "recorderId", column = "recorder_id"),
            @Result(property = "targetEquipmentId", column = "target_equipment_id"),
            @Result(property = "remark", column = "remark"),
            @Result(property = "time", column = "time")
    })
    List<UsageLog> findByTargetEquipmentId(@Param("equipmentId") String equipmentId);

    @Insert("INSERT INTO tb_usage_logging (recorder_id, target_equipment_id, remark, time) " +
            "VALUES (#{recorderId}, #{targetEquipmentId}, #{remark}, CURRENT_TIMESTAMP)")
    void insertUsageLog(UsageLog usageLog);
}

