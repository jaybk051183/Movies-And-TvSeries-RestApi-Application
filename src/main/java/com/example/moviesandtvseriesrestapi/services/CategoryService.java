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
        //ROEP de findAll methode van de categoryRepository om ALLE categorieën te verkrijgen:
        return categoryRepository.findAll().stream()
                //CONVERTER elk categorie-object naar een CategoryDto:
                .map(this::convertToDto)
                //VERZAMEL alle geconverteerde CategoryDto objecten in een nieuwe lijst:
                .collect(Collectors.toList());
    }

    //ROEP de findByName methode aan van categoryRepository met de gegeven naam als parameter:
    public Optional<Category> findByName(String name) {

        //RETOURNEER het resultaat, wat een Optional van Category is:
        return categoryRepository.findByName(name);
    }

    private CategoryDto convertToDto(Category category) {
        //CREEER een nieuw CategoryDto OBJECT genaamd dto:
        CategoryDto dto = new CategoryDto();

        //Instellen van de waarden van naam, beschikbare content en prijs:
        dto.setName(category.getName());
        dto.setAvailableContent(category.getAvailableContent());
        dto.setPrice(category.getPrice());

        return dto;
    }
}
