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
}
