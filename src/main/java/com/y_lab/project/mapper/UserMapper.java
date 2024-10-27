package com.y_lab.project.mapper;

import com.y_lab.project.dto.UserDTO;
import com.y_lab.project.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toUserDTO(User user);
    User toUser(UserDTO userDTO);
    User toEntity(UserDTO userDTO);
}
