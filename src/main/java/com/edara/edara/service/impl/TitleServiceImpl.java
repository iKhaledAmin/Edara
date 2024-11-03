package com.edara.edara.service.impl;

import com.edara.edara.model.dto.TitleRequest;
import com.edara.edara.model.dto.TitleResponse;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.entity.Title;
import com.edara.edara.model.mapper.TitleMapper;
import com.edara.edara.repository.TitleRepo;
import com.edara.edara.service.TitleService;
import com.edara.edara.utils.NonNullBeanUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TitleServiceImpl implements TitleService {
    private final TitleRepo titleRepo;
    private final TitleMapper titleMapper;
    private final NonNullBeanUtils nonNullBeanUtils;

    @Override
    public TitleResponse toResponse(Title title) {
        return titleMapper.toResponse(title);
    }

    @Override
    public Title toEntity(TitleRequest titleRequest) {
        return titleMapper.toEntity(titleRequest);
    }

    @Override
    public Title create(TitleRequest titleRequest) {
        Title newTitle = titleMapper.toEntity(titleRequest);
        return newTitle;
    }

    @Override
    public Title save(Title title) {
        return titleRepo.save(title);
    }

    @Override
    public Title add(TitleRequest titleRequest, Project project) {
        Title newTitle = create(titleRequest);
        newTitle.setProject(project);
        return save(newTitle);
    }

    @Override
    public Title updateEntity(Long titleId, Title newTitle) {
        Title existedTitle = getById(titleId);

        // Copy properties from newTitle to existedTitle, excluding the "id", "code", "project"
        nonNullBeanUtils.copyProperties(newTitle, existedTitle, "id","project");

        return save(existedTitle);
    }

    @Override
    public TitleResponse update(Long titleId, TitleRequest titleRequest) {
        Title newTitle = toEntity(titleRequest);
        Title updatedTitle = updateEntity(titleId, newTitle);
        return toResponse(updatedTitle);
    }

    @Override
    public void delete(Long titleId) {
        getById(titleId);
        titleRepo.deleteById(titleId);
    }

    @Override
    public Optional<Title> getEntityById(Long titleId) {
        return titleRepo.findById(titleId);
    }

    @Override
    public Title getById(Long titleId) {
        return getEntityById(titleId).orElseThrow(
                () -> new NoSuchElementException("There is no title with id  = " + titleId)
        );
    }

    @Override
    public TitleResponse getResponseById(Long titleId) {
        return toResponse(getById(titleId));
    }

    @Override
    public List<TitleResponse> getAll() {
        return titleRepo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


}
