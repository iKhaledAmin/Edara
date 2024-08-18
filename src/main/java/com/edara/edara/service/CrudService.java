package com.edara.edara.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface CrudService <Request, Entity, Response, ID> {
    public Response add(Request request);

    public Entity updateEntity(ID id , Entity newEntity);
    public Response update(ID id , Request request);

    public void delete(ID id);

    public Optional<Entity> getEntityById(ID id);

    public Entity getById(ID id);

    public Response getResponseById(ID id);

    public List<Response> getAll();

}
