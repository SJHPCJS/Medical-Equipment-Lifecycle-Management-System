package com.example.melms.pojo;
import lombok.Data;
import java.util.Date;

@Data
public class UsageLog {
    private Integer logId;
    private String recorderId;
    private String targetEquipmentId;
    private String remark;
    private Date time;
}