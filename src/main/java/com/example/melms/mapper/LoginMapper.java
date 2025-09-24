package com.example.melms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface LoginMapper {

    @Select("SELECT account_id, role FROM tb_account WHERE name = #{name} and password = #{password}")
    Map<String, Object> login(@Param("name") String name, @Param("password") String password);

    @Select("SELECT account_id, name, role, department_id FROM tb_account WHERE account_id = #{id}")
    Map<String, Object> findById(@Param("id") int id);
}
