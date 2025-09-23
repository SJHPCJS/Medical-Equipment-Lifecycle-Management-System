package com.example.melms.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginMapper {

    @Select("SELECT account_id FROM tb_account WHERE account_id = #{account} and password = #{password}")
    String login(@Param("account") String account, @Param("password") String password);
}
