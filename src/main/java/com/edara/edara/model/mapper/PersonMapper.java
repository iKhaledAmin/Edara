package com.edara.edara.model.mapper;

//@Mapper(componentModel = "spring")
//public interface PersonMapper {
//    PersonMapper INSTANCE = Mappers.getMapper(PersonMapper.class);
//
//    @Mapping(source = "firstName", target = "firstName")
//    @Mapping(source = "lastName", target = "lastName")
//    public abstract Person toEntity(PersonRequest request);
//    PersonResponse toResponse(Person entity);
//
//
//
//    @ObjectFactory
//    public Person createPerson(PersonRequest request) {
//        if (request.isUser()) {
//            return new User();
//        }
//        throw new IllegalArgumentException("Unknown type of PersonRequest");
//    }
//}


import com.edara.edara.model.dto.PersonRequest;
import com.edara.edara.model.dto.PersonResponse;
import com.edara.edara.model.entity.Person;
import com.edara.edara.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

@Mapper(componentModel = "spring")
public abstract class PersonMapper {

    public abstract Person toEntity(PersonRequest request);

    public abstract PersonResponse toResponse(Person entity);


    @ObjectFactory
    public Person createPerson(PersonRequest request) {
        switch (request.getRole()) {
            case USER:
                return new User();
            case ADMIN:
               // return new Admin();
            default:
                throw new IllegalArgumentException("Unknown type of PersonRequest");
        }
    }
}