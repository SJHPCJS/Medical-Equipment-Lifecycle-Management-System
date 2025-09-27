package com.example.melms.mapper;

import com.example.melms.pojo.Log;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LogMapper {
    @Select("SELECT * FROM tb_log")
    Log[] getLogs();

    @Insert("INSERT INTO tb_log (log_action, log_user_id) VALUES (#{log_action}, #{log_user_id})")
    void addNewLog(String log_action, String log_user_id);

    @Select("SELECT COUNT(*) FROM tb_usage_logging l " +
            "JOIN tb_equipment e ON l.target_equipment_id = e.equipment_id " +
            "WHERE DATE(l.time) = CURDATE() " +
            "AND e.department_id = #{departmentId}")
    int countLogsForToday(String departmentId);
}
