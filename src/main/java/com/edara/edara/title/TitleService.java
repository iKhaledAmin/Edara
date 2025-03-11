package com.edara.edara.title;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface TitleService  {

     TitleResponse toResponse(Title title);
     Title toEntity(TitleRequest titleRequest);

     boolean isExistsByNameIgnoreCaseAndProjectId(String titleName, Long projectId);

     Title add(Long projectId,Title newTitle);
     Title add(Long projectId,TitleRequest titleRequest);

     Title update(Long titleId, Title newTitle);
     Title update(Long titleId, TitleRequest titleRequest);

     void delete(Long titleId);


     Optional<Title> getOptionalById(Long titleId);
     Title getById(Long titleId);
     TitleResponse getResponseById(Long titleId);
     List<Title> getAllByProjectId(Long projectId);
     List<TitleResponse> getResponseAllByProjectId(Long projectId);



}
