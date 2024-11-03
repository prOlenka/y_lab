package com.y_lab.project.mapper;

import com.y_lab.project.dto.HabitDTO;
import com.y_lab.project.entity.Habit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface HabitMapper {
    HabitDTO toHabitDTO(Habit habit);
    Habit toEntity(HabitDTO habitDTO);
}
