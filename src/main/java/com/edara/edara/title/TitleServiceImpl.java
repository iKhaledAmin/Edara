package com.edara.edara.title;

import com.edara.edara.exception.ConflictException;
import com.edara.edara.global.ServiceLocator;
import com.edara.edara.member.MemberService;
import com.edara.edara.project.Project;
import com.edara.edara.project.ProjectService;
import com.edara.edara.global.utils.NonNullBeanUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TitleServiceImpl implements TitleService{
    private final TitleRepo titleRepo;
    private final TitleMapper titleMapper;
    private final ServiceLocator serviceLocator;
    private final NonNullBeanUtils nonNullBeanUtils;

    @Override
    public TitleResponse toResponse(Title title) {
        return titleMapper.toResponse(title);
    }

    @Override
    public Title toEntity(TitleRequest titleRequest) {
        return titleMapper.toEntity(titleRequest);
    }


    private Title create() {
        Title newTitle = new Title();
        return newTitle;
    }
    private Title create(TitleRequest titleRequest) {
        Title newTitle = toEntity(titleRequest);
        return newTitle;
    }
    private Title save(Title title) {
        return titleRepo.save(title);
    }

    private void throwExceptionIfProjectIncludeTitleWithSameName(String titleName, Long projectId) {
        if (titleRepo.existsByNameIgnoreCaseAndProjectId(titleName, projectId)) {
            throw new ConflictException("Title with the same name already exists in this project.");
        }
    }

    @Override
    public Title add(Title newTitle, Long projectId) {
        Project project = serviceLocator.getService(ProjectService.class).getById(projectId);
        throwExceptionIfProjectIncludeTitleWithSameName(newTitle.getName(), projectId);

        newTitle.setProject(project);
        project.getTitles().add(newTitle);

        return save(newTitle);
    }

    @Override
    public Title add(TitleRequest titleRequest, Long projectId) {
        Title newTitle = create(titleRequest);
        return add(newTitle, projectId);
    }


    @Override
    public Title update(Long titleId, Title newTitle) {
        Title existingTitle = getById(titleId);

        throwExceptionIfProjectIncludeTitleWithSameName(newTitle.getName(), existingTitle.getProject().getId());

        // Copy properties from newTitle to existedTitle, excluding the "id", "project"
        nonNullBeanUtils.copyProperties(newTitle, existingTitle, "id","project");

        return save(existingTitle);
    }

    @Override
    public Title update(Long titleId, TitleRequest titleRequest) {
        Title newTitle = toEntity(titleRequest);
        return update(titleId, newTitle);
    }

    private void throwExceptionIfTitleStillAssignedToMembers(Long titleId, Long projectId) {
        boolean exists = serviceLocator.getService(MemberService.class)
                .isExistsByTitleIdAndProjectId(titleId, projectId);

        if (exists) {
            throw new ConflictException("Title is still assigned to members.");
        }
    }
    @Transactional
    @Override
    public void delete(Long titleId) {
        Title title = getById(titleId);
        Project project = title.getProject();
        throwExceptionIfTitleStillAssignedToMembers(titleId, project.getId());

        // Remove the title from the project's title list to trigger orphan removal
        // JPA will automatically delete the title from the database
        if (project.getTitles().contains(title))
            project.getTitles().remove(title); // Triggers orphan removal

        //titleRepo.delete(title);
    }

    @Override
    public Optional<Title> getOptionalById(Long titleId) {
        return titleRepo.findById(titleId);
    }

    @Override
    public Title getById(Long titleId) {
        return getOptionalById(titleId).orElseThrow(
                () -> new NoSuchElementException("There is no title with id  = " + titleId)
        );
    }

    @Override
    public TitleResponse getResponseById(Long titleId) {
        return toResponse(getById(titleId));
    }

    @Override
    public List<Title> getAllByProjectId(Long projectId) {
        serviceLocator.getService(ProjectService.class).getById(projectId);
        return titleRepo.findAllByProjectId(projectId);
    }

    @Override
    public List<TitleResponse> getResponseAllByProjectId(Long projectId) {
        return getAllByProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }


}
