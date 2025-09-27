package com.example.melms.pojo;
import lombok.Data;

import java.time.LocalDateTime;

@Data

public class ProcureOrder {
    private Integer procureId;
    private String equipmentTypeId;
    private Integer count;
    private String supplierId;
    private String status;//'under-review' | 'procuring' | 'arrived' | 'terminated' | 'finished'
    private String requesterId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
