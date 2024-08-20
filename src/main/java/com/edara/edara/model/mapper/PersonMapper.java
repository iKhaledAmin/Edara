package com.edara.edara.model.mapper;

import com.edara.edara.model.dto.EditProfileRequest;
import com.edara.edara.model.dto.PersonResponse;
import com.edara.edara.model.entity.Person;
import com.edara.edara.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    public abstract Person toEntity(EditProfileRequest request);
    public abstract PersonResponse toResponse(Person entity);


    @ObjectFactory
    public default Person createPerson(EditProfileRequest request) {
        Person person;

        switch (request.getRole()) {
            case USER:
                person = new User();
                break;
            case ADMIN:
                // return new Admin();
            default:
                throw new IllegalArgumentException("Unknown role of person");
        }

        return person;
    }
}