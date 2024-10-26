package com.edara.edara.service;

import java.util.List;
import java.util.Optional;

public interface CrudService <Request, Entity, Response, ID> {

     Response toResponse(Entity entity);
     Entity toEntity(Request request);

     Entity create(Request request);
     Entity save(Entity entity);

     Entity updateEntity(ID id , Entity newEntity);
     Response update(ID id , Request request);

     void delete(ID id);

     Optional<Entity> getEntityById(ID id);
     Entity getById(ID id);
     Response getResponseById(ID id);
     List<Response> getAll();

}
