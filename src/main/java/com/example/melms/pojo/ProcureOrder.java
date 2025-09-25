package com.example.melms.pojo;
import lombok.Data;

@Data

public class ProcureOrder {
    private String procureId;
    private String equipmentTypeId;
    private Integer count;
    private String supplierId;
    private String status;
}
