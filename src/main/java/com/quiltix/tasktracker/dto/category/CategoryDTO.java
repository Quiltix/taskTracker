package com.quiltix.tasktracker.dto.category;


import com.quiltix.tasktracker.model.Category;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CategoryDTO implements Serializable {

    private Long id;

    @Size(min = 3, message = "name must be at least 3 characters long")
    private String name;

    public CategoryDTO(Category category) {
        id = category.getId();
        name = category.getName();
    }
}
