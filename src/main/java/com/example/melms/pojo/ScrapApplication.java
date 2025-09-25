package com.example.melms.pojo;
import lombok.Data;

@Data
public class ScrapApplication {
    private String scrapId;
    private String deviceId;
    private String reason;
    private String status;
    private String record;
    private String photoUrl;
}