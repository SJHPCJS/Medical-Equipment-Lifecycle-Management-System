package com.example.melms.service;

import com.example.melms.mapper.ScrapApplicationMapper;
import com.example.melms.pojo.ScrapApplication;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScrapApplicationService {
    @Resource
    private ScrapApplicationMapper mapper;

    public List<ScrapApplication> getAll() {
        return mapper.findAll();
    }

    public boolean approve(String id) {
        ScrapApplication app = mapper.findById(id);
        if (app == null) return false;
        app.setStatus("Approved");
        return mapper.update(app) > 0;
    }

    public boolean reject(String id) {
        ScrapApplication app = mapper.findById(id);
        if (app == null) return false;
        app.setStatus("Rejected");
        return mapper.update(app) > 0;
    }

    public boolean update(ScrapApplication app) {
        return mapper.update(app) > 0;
    }
}
