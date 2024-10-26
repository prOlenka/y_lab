package com.y_lab.project.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class HabitDTO {
    @NotBlank(message = "Name cannot be empty")
    @Size(max = 50, message = "Name must be up to 50 characters")
    private String name;

    @Size(max = 200, message = "Description must be up to 200 characters")
    private String description;

    @NotNull(message = "Frequency cannot be null")
    private String frequency;
}