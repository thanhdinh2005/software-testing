package com.software.backend.service.impl;

import com.software.backend.entity.Category;
import com.software.backend.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Override
    public List<Category> getAllCategories() {
        return List.of();
    }
}
