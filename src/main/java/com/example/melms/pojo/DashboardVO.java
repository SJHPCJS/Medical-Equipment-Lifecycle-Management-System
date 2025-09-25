package com.example.melms.pojo;

import lombok.Data;
import java.util.Map;

@Data
public class DashboardVO {
    private Map<String, Integer> equipmentCounts; // unassigned, inUse, underRepair, pendingScrap
    private Map<String, Integer> todayTickets; // pendingResponse, inProgress, pendingAcceptance
}

