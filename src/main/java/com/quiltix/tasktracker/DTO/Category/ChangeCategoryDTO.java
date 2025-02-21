package com.quiltix.tasktracker.DTO.Category;

import com.quiltix.tasktracker.model.Category;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChangeCategoryDTO {
    @NotBlank(message = "Id of category cannot be empty")
    private Long id;
    @NotBlank(message = "Name of category cannot be empty")
    private String newName;
}
