package com.example.melms.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;
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

    @Insert("INSERT INTO tb_account (name, password, role, department_id) VALUES (#{name}, #{password}, #{role}, #{department_id})")
    void addNewUser(@Param("name") String name, @Param("password") String password, @Param("role") String role, @Param("department_id") String department_id);

    @Delete("DELETE FROM tb_account WHERE account_id = #{account_id}")
    void deleteUser(@Param("account_id") int account_id);

    @Update("UPDATE tb_account SET name = #{name}, password = #{password}, role = #{role}, department_id = #{department_id} WHERE account_id = #{account_id}")
    void updateUser(@Param("account_id") int account_id, @Param("name") String name, @Param("password") String password, @Param("role") String role, @Param("department_id") String department_id);

    @Update("UPDATE tb_account SET password = #{password} WHERE account_id = #{account_id}")
    void resetPassword(@Param("account_id") int account_id, @Param("password") String password);

    @Update("UPDATE tb_account SET role = #{role} WHERE account_id = #{account_id}")
    void setRole(@Param("account_id") int account_id, @Param("role") String role);

    @Update("UPDATE tb_account SET name = #{name}, role = #{role}, department_id = #{department_id} WHERE account_id = #{account_id}")
    void updateUserMeta(@Param("account_id") int account_id, @Param("name") String name, @Param("department_id") String department_id, @Param("role") String role);

    @Select("SELECT department_id AS id, department_name AS name FROM tb_department")
    List<Map<String, Object>> getDepartments();

    @Select("SELECT account_id AS id, name AS username, role AS roleId, department_id AS departmentId FROM tb_account")
    List<Map<String, Object>> listUsers();

    @Select("SELECT COUNT(*) FROM tb_account WHERE name = #{name}")
    int countByName(@Param("name") String name);

    @Select("SELECT COUNT(*) FROM tb_account WHERE name = #{name} AND account_id <> #{account_id}")
    int countByNameExcludingId(@Param("name") String name, @Param("account_id") int account_id);

    @Insert("INSERT INTO tb_department (department_name) VALUES (#{department_name})")
    void addNewDepartment(@Param("department_name") String department_name);

    @Delete("DELETE FROM tb_department WHERE department_id = #{department_id}")
    void deleteDepartment(@Param("department_id") String department_id);

    @Update("Update tb_department SET department_name = #{department_name} WHERE department_id = #{department_id}")
    void updateDepartmentName(@Param("department_id") String department_id, @Param("department_name") String department_name);

    @Select("SELECT COUNT(*) FROM tb_department WHERE department_name = #{department_name}")
    int countDepartmentByName(@Param("department_name") String department_name);

    @Select("SELECT COUNT(*) FROM tb_department WHERE department_name = #{department_name} AND department_id <> #{department_id}")
    int countDepartmentByNameExcludingId(@Param("department_name") String department_name, @Param("department_id") String department_id);
}

