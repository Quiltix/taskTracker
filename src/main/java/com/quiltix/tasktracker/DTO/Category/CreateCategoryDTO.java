package com.quiltix.tasktracker.DTO.Category;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateCategoryDTO {

    @NotBlank(message = "Name of category cannot be empty")
    private String name;
}
