package com.example.melms.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class RepairTicket {
    private String ticketId;
    private Date createdAt;
    private Date finishedAt;
    private String notes;
    private Double cost;
    private String result;
    private String status;
    private Integer departmentId;
    private String departmentName;
}