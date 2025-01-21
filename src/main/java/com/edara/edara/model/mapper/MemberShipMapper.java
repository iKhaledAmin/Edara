package com.edara.edara.model.mapper;

import com.edara.edara.model.dto.MemberShipRequest;
import com.edara.edara.model.dto.MemberShipResponse;
import com.edara.edara.model.entity.MemberShip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberShipMapper {

    MemberShip toEntity(MemberShipRequest request);

    @Mapping(target = "employeeId", source = "entity.user.id")
    @Mapping(target = "employeeName", expression = "java(concatenateUserName(entity.getUser().getFirstName(), entity.getUser().getLastName()))")
    @Mapping(target = "employeeImage", source = "entity.user.image")
    @Mapping(target = "employeeCode", source = "entity.user.userCode")
    @Mapping(target = "joinDate", source = "entity.createdAt")
    @Mapping(target = "title", source = "entity.title.name")
    MemberShipResponse toResponse(MemberShip entity);

    default String concatenateUserName(String firstName, String lastName) {
        if (firstName == null && lastName == null) {
            return null;
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }


}
