package com.example.melms.service;

import com.example.melms.mapper.DepartmentMapper;
import com.example.melms.pojo.Department;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    @Resource
    private DepartmentMapper departmentMapper;

    public List<Department> listAll() {
        return departmentMapper.findAll();
    }

    public Department getById(Integer id) {
        return departmentMapper.findById(id);
    }

    public int add(Department department) {
        return departmentMapper.insert(department);
    }

    public int update(Department department) {
        return departmentMapper.update(department);
    }

    public int delete(Integer id) {
        return departmentMapper.delete(id);
    }
}
