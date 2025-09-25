package com.example.melms.mapper;

import com.example.melms.pojo.Department;
import org.apache.ibatis.annotations.*;

import java.util.List;

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
}

