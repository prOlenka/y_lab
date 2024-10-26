package com.y_lab.project.mapper;

import com.y_lab.project.dto.HabitDTO;
import com.y_lab.project.entity.Habit;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HabitMapper {
    HabitMapper INSTANCE = Mappers.getMapper(HabitMapper.class);

    HabitDTO toHabitDTO(Habit habit);
    Habit toHabit(HabitDTO habitDTO);
}

