package com.edara.edara.title;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TitleMapper {
    Title toEntity(TitleRequest request);

    TitleResponse toResponse(Title entity);
}
