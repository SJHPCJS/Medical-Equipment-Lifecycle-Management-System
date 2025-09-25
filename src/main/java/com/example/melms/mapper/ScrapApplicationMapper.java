package com.example.melms.mapper;

import com.example.melms.pojo.ScrapApplication;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScrapApplicationMapper {

    @Select("SELECT * FROM tb_scrap_application")
    List<ScrapApplication> findAll();

    @Select("SELECT * FROM tb_scrap_application WHERE scrap_id=#{id}")
    ScrapApplication findById(String id);

    @Update("UPDATE tb_scrap_application SET status=#{status}, record=#{record}, photo_url=#{photoUrl} WHERE scrap_id=#{scrapId}")
    int update(ScrapApplication scrap);

    @Insert("INSERT INTO tb_scrap_application(scrap_id, device_id, reason, status) VALUES(#{scrapId}, #{deviceId}, #{reason}, #{status})")
    int insert(ScrapApplication scrap);

    @Select("SELECT COUNT(*) FROM tb_scrap_application WHERE status='Pending'")
    int countPendingScrap();
}
