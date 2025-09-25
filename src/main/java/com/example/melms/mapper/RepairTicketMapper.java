package com.example.melms.mapper;

import com.example.melms.pojo.RepairTicket;
import org.apache.ibatis.annotations.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface RepairTicketMapper {

    @Select("SELECT t.*, d.department_name AS departmentName " +
            "FROM tb_repair_ticket t " +
            "JOIN tb_department d ON t.department_id = d.department_id")
    List<RepairTicket> findAll();

    @Update("UPDATE tb_repair_ticket " +
            "SET notes=#{notes}, cost=#{cost}, result=#{result}, status=#{status}, finished_at=#{finishedAt} " +
            "WHERE ticket_id=#{ticketId}")
    int update(RepairTicket ticket);

    @Select("SELECT * FROM tb_repair_ticket WHERE ticket_id=#{id}")
    RepairTicket findById(String id);

    @Select("""
        SELECT status, COUNT(*) AS cnt 
        FROM tb_repair_ticket 
        WHERE DATE(created_at) = CURDATE() 
        GROUP BY status
    """)
    java.util.List<Map<String, Object>> countTodayTicketsRaw();

    default Map<String, Integer> countTodayTickets() {
        var list = countTodayTicketsRaw();
        Map<String, Integer> map = new HashMap<>();
        map.put("pendingResponse", 0);
        map.put("inProgress", 0);
        map.put("pendingAcceptance", 0);

        for (Map<String,Object> m : list) {
            String status = (String)m.get("status");
            int count = ((Number)m.get("cnt")).intValue();
            switch(status) {
                case "Pending Review" -> map.put("pendingResponse", count);
                case "In Repair" -> map.put("inProgress", count);
                case "In Acceptance" -> map.put("pendingAcceptance", count);
            }
        }
        return map;
    }
}
