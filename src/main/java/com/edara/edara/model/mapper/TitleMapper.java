package com.edara.edara.model.mapper;

import com.edara.edara.model.dto.TitleRequest;
import com.edara.edara.model.dto.TitleResponse;
import com.edara.edara.model.entity.Title;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TitleMapper {
    Title toEntity(TitleRequest request);

    TitleResponse toResponse(Title entity);
}
