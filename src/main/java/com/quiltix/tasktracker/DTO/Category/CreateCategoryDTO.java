package com.quiltix.tasktracker.DTO.Category;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateCategoryDTO {

    @Size(min = 3, message = "name must be at least 3 characters long")
    @NotBlank(message = "Name of categoryId cannot be empty")
    private String name;
}
