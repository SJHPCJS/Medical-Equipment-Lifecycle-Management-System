package com.example.melms.pojo;
import lombok.Data;

@Data
public class TicketSummary {
    private int pendingResponse;
    private int inProgress;
    private int pendingAcceptance;
}