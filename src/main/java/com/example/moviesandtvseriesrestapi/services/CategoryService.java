package com.example.moviesandtvseriesrestapi.services;

import com.example.moviesandtvseriesrestapi.dtos.CategoryDto;
import com.example.moviesandtvseriesrestapi.models.Category;
import com.example.moviesandtvseriesrestapi.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> findAll() {

        return categoryRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<Category> findByName(String name) {
        return categoryRepository.findByName(name);
    }

    private CategoryDto convertToDto(Category category) {

        CategoryDto dto = new CategoryDto();

        dto.setName(category.getName());
        dto.setAvailableContent(category.getAvailableContent());
        dto.setPrice(category.getPrice());

        return dto;
    }
}
