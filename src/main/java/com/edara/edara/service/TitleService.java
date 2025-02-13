package com.edara.edara.service;

import com.edara.edara.model.dto.TitleRequest;
import com.edara.edara.model.dto.TitleResponse;
import com.edara.edara.model.entity.Title;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TitleService  {

     TitleResponse toResponse(Title title);
     Title toEntity(TitleRequest titleRequest);

     Title add(Title newTitle, Long projectId);
     Title add(TitleRequest titleRequest, Long projectId);

     Title update(Long titleId, Title newTitle);
     Title update(Long titleId, TitleRequest titleRequest);

     void delete(Long titleId);


     Optional<Title> getEntityById(Long titleId);
     Title getById(Long titleId);
     TitleResponse getResponseById(Long titleId);
     List<Title> getAllByProjectId(Long projectId);
     List<TitleResponse> getResponseAllByProjectId(Long projectId);



}
