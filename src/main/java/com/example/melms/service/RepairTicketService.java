package com.example.melms.service;

import com.example.melms.mapper.RepairTicketMapper;
import com.example.melms.pojo.RepairTicket;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepairTicketService {
    @Resource
    private RepairTicketMapper mapper;

    public List<RepairTicket> getAll() {
        return mapper.findAll();
    }

    public boolean save(RepairTicket ticket) {
        return mapper.update(ticket) > 0;
    }

    public boolean complete(String ticketId) {
        RepairTicket ticket = mapper.findById(ticketId);
        if (ticket == null) return false;
        ticket.setStatus("Completed");
        ticket.setFinishedAt(new java.util.Date());
        return mapper.update(ticket) > 0;
    }
}
