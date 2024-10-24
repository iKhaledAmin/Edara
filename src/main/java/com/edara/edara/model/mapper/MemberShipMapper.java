package com.edara.edara.model.mapper;

import com.edara.edara.model.dto.MemberShipRequest;
import com.edara.edara.model.dto.MemberShipResponse;
import com.edara.edara.model.entity.MemberShip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberShipMapper {

    MemberShip toEntity(MemberShipRequest request);

    @Mapping(target = "uerName" , source = "entity.user.userName")
    @Mapping(target = "userId" , source = "entity.user.id")
    @Mapping(target = "projectName" , source = "entity.project.name")
    MemberShipResponse toResponse(MemberShip entity);


}
