package com.edara.edara.service;

import com.edara.edara.model.dto.TitleRequest;
import com.edara.edara.model.dto.TitleResponse;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.Title;
import org.springframework.stereotype.Service;

@Service
public interface TitleService extends CrudService<TitleRequest, Title, TitleResponse, Long> {
     Title add(TitleRequest titleRequest , Project project);
}
