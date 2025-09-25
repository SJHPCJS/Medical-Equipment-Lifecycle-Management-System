package com.example.melms.controller;

import com.example.melms.pojo.ScrapApplication;
import com.example.melms.service.ScrapApplicationService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/req/scrap")
public class ScrapApplicationController {
    @Resource
    private ScrapApplicationService service;

    @GetMapping
    public List<ScrapApplication> list() {
        return service.getAll();
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable String id) {
        return service.approve(id) ? "Approved" : "Failed";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable String id) {
        return service.reject(id) ? "Rejected" : "Failed";
    }

    @PutMapping("/{id}")
    public String update(@PathVariable String id, @RequestBody ScrapApplication scrap) {
        scrap.setScrapId(id);
        return service.update(scrap) ? "Updated" : "Failed";
    }

    @PostMapping("/{id}/upload")
    public String uploadPhoto(@PathVariable String id, @RequestParam("file") MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String path = "uploads/" + fileName;
        File dest = new File(path);
        dest.getParentFile().mkdirs();
        file.transferTo(dest);

        ScrapApplication scrap = new ScrapApplication();
        scrap.setScrapId(id);
        scrap.setPhotoUrl("/" + path);
        service.update(scrap);
        return scrap.getPhotoUrl();
    }
}

