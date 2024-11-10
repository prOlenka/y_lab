package com.y_lab.project.dto;

import com.y_lab.project.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class HabitDTO {
    private Long id;
    private User userId;

    @NotBlank(message = "Name cannot be empty")
    @Size(max = 50, message = "Name must be up to 50 characters")
    private String name;

    @Size(max = 200, message = "Description must be up to 200 characters")
    private String description;

    @NotNull(message = "Frequency cannot be null")
    private String frequency;
}