package com.example.melms.pojo;
import lombok.Data;
@Data
public class Equipment {
    private String equipmentId;
    private String equipmentTypeId;          // 设备类型ID
    private String status;                   // 状态 (assigning, using, repairing, scrapping)
    private String userManualPath;           // 用户手册路径
    private String warrantyCertificatePath;  // 保修凭证路径
    private String supplierId;               // 供应商ID
    private String departmentId;             // 部门ID
    private String picUrl;                   // 图片URL

    private String equipmentTypeName;        // 设备类型名称
    private String departmentName;           // 部门名称
}


