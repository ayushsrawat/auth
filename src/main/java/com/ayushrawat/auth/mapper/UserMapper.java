package com.ayushrawat.auth.mapper;

import com.ayushrawat.auth.payload.request.UserDTO;
import com.ayushrawat.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
  componentModel = MappingConstants.ComponentModel.SPRING,
  unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {

  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "role", ignore = true)
  User toEntity(UserDTO userDTO);

}
