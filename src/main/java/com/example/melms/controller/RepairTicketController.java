package com.example.melms.controller;

import com.example.melms.pojo.RepairTicket;
import com.example.melms.service.RepairTicketService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/req/repair-tickets")
public class RepairTicketController {
    @Resource
    private RepairTicketService service;

    @GetMapping
    public List<RepairTicket> list() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public String save(@PathVariable String id, @RequestBody RepairTicket ticket) {
        ticket.setTicketId(id);
        return service.save(ticket) ? "Updated" : "Failed";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable String id) {
        return service.complete(id) ? "Completed" : "Failed";
    }
}
