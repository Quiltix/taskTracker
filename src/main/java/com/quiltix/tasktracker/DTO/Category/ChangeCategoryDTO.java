package com.quiltix.tasktracker.DTO.Category;

import com.quiltix.tasktracker.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChangeCategoryDTO {

    @NotNull(message = "Id of categoryId cannot be empty")
    private Long id;

    @Size(min = 3, message = "name must be at least 3 characters long")
    @NotBlank(message = "Name of categoryId cannot be empty")
    private String newName;
}
