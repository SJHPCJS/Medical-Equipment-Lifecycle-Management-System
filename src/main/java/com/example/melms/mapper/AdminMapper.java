package com.example.melms.mapper;

import org.apache.ibatis.annotations.*;
import java.util.Map;

@Mapper
public interface AdminMapper {
    @Select("SELECT count(*) FROM tb_account")
    int getEmployeeCount();

    @Select("SELECT count(*) FROM tb_department")
    int getDepartmentCount();

    @Select("SELECT count(*) FROM tb_work_order")
    int getWorkOrderCount();

    @Select("SELECT count(*) FROM tb_procure_order")
    int getProcureOrderCount();

    @Insert("INSERT INTO tb_account (account_id, password, role, department_id) VALUES (#{account_id}, #{password}, #{role}, #{department_id})")
    void addNewUser(String account_id, String password, String role, String department_id);

    @Delete("DELETE FROM tb_account WHERE account_id = #{account_id}")
    void deleteUser(String account_id);

    @Update("UPDATE tb_account SET password = #{password}, role = #{role}, department_id = #{department_id} WHERE account_id = #{account_id}")
    void updateUser(String account_id, String password, String role, String department_id);

    @Select("SELECT * FROM tb_department")
    Map<String, String> getDepartments();

    @Insert("INSERT INTO tb_department (department_id, department_name) VALUES (#{department_id}, #{department_name})")
    void addNewDepartment(String department_id, String department_name);

    @Delete("DELETE FROM tb_department WHERE department_id = #{department_id}")
    void deleteDepartment(String department_id);

    @Update("Update tb_department SET department_name = #{department_name} WHERE department_id = #{department_id}")
    void updateDepartmentName(String department_id, String department_name);
}

