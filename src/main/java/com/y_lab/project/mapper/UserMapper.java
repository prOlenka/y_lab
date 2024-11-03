package com.y_lab.project.mapper;

import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toUserDTO(User user);
    User toEntity(UserDTO userDTO);
}
