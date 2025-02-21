package com.edara.edara.title;

import com.edara.edara.exception.ConflictException;
import com.edara.edara.global.ServiceLocator;
import com.edara.edara.global.utils.NonNullBeanUtils;
import com.edara.edara.member.MemberService;
import com.edara.edara.project.Project;
import com.edara.edara.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TitleServiceImplTest {

    @InjectMocks
    private TitleServiceImpl titleService;

    @Mock
    private TitleRepo titleRepo;

    @Mock
    private TitleMapper titleMapper;

    @Mock
    private ServiceLocator serviceLocator;

    @Mock
    private ProjectService projectService;

    @Mock
    private MemberService memberService;

    @Mock
    private NonNullBeanUtils nonNullBeanUtils;  // Mocking NonNullBeanUtils

    private Title title;
    private Title title2;
    private Title updatedTitle;


    private TitleRequest titleRequest;
    private TitleRequest updatTitleRequest;


    private TitleResponse titleResponse;
    private Project project;


    @BeforeEach
    void setUp() {
        titleRequest = TitleRequest.builder()
                .name("Title name")
                .description("Title description.")
                .build();

        updatTitleRequest = TitleRequest.builder()
                .name("Updated title name")
                .description("Updated title description.")
                .build();

        titleResponse = TitleResponse.builder()
                .id(1L)
                .name("Title name")
                .description("Title description.")
                .build();

        project = Project.builder()
                .id(1L)
                .name("Project name")
                .titles(new ArrayList<>()) // Initialize empty list
                .build();


        title = Title.builder()
                .id(1L)
                .name("Title name")
                .description("Title description.")
                .project(project)
                .build();

        project.getTitles().add(title);

        updatedTitle = Title.builder()
                .id(1L)
                .name("Updated title name")
                .description("Updated title description.")
                .project(project)
                .build();
    }

    @Test
    void toResponse_ShouldReturnTitleResponse_WhenGivenValidTitle() {
        // Arrange : Mock behavior of titleMapper
        when(titleMapper.toResponse(title)).thenReturn(titleResponse);

        // Act : Call the method
        TitleResponse result = titleService.toResponse(title);

        // Assert : Verify correct response
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(titleResponse.getId());
        assertThat(result.getName()).isEqualTo(titleResponse.getName());
        assertThat(result.getDescription()).isEqualTo(titleResponse.getDescription());

        // Verify interactions
        verify(titleMapper, times(1)).toResponse(title);
    }

    @Test
    void toEntity_ShouldReturnTitle_WhenGivenValidTitleRequest() {
        // Arrange : Mock behavior of titleMapper
        when(titleMapper.toEntity(titleRequest)).thenReturn(title);

        // Act : Call the method
        Title result = titleService.toEntity(titleRequest);

        // Assert : Verify correct entity
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(title.getName());
        assertThat(result.getDescription()).isEqualTo(title.getDescription());

        // Verify interactions
        verify(titleMapper, times(1)).toEntity(titleRequest);
    }

    @Test
    void isExistsByNameIgnoreCaseAndProjectId_ShouldReturnTrue_WhenTitleExists() {
        // Arrange : Mock repository method to return true
        when(titleRepo.existsByNameIgnoreCaseAndProjectId(title.getName(), project.getId())).thenReturn(true);

        // Act : Call the method
        boolean result = titleService.isExistsByNameIgnoreCaseAndProjectId(title.getName(), project.getId());

        // Assert : Verify result is true
        assertThat(result).isTrue();

        // Verify interactions
        verify(titleRepo, times(1)).existsByNameIgnoreCaseAndProjectId(title.getName(), project.getId());
    }

    @Test
    void isExistsByNameIgnoreCaseAndProjectId_ShouldReturnFalse_WhenTitleDoesNotExist() {
        // Arrange : Mock repository method to return false
        when(titleRepo.existsByNameIgnoreCaseAndProjectId(title.getName(), project.getId())).thenReturn(false);

        // Act : Call the method
        boolean result = titleService.isExistsByNameIgnoreCaseAndProjectId(title.getName(), project.getId());

        // Assert : Verify result is false
        assertThat(result).isFalse();

        // Verify interactions
        verify(titleRepo, times(1)).existsByNameIgnoreCaseAndProjectId(title.getName(), project.getId());
    }

    @Test
    void addMethod_ShouldAddTitleSuccessfully_WhenProjectExistsAndNoExistingTitleWithSameName() {
        //Arrange : Mock dependencies
        when(serviceLocator.getService(ProjectService.class)).thenReturn(projectService);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(titleRepo.existsByNameIgnoreCaseAndProjectId(titleRequest.getName(), project.getId())).thenReturn(false);
        when(titleMapper.toEntity(titleRequest)).thenReturn(title);
        when(titleRepo.save(title)).thenReturn(title);

        //Act : Call the method
        Title result = titleService.add(titleRequest, project.getId());

        //Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(title.getName());
        assertThat(result.getDescription()).isEqualTo(title.getDescription());
        assertThat(result.getProject()).isEqualTo(project);

        // Verify interactions
        verify(projectService, times(1)).getById(project.getId());
        verify(titleRepo, times(1)).existsByNameIgnoreCaseAndProjectId(titleRequest.getName(), 1L);
        verify(titleRepo, times(1)).save(title);
    }

    @Test
    void addMethod_ShouldThrowConflictException_WhenTitleWithSameNameAlreadyExists() {
        //Arrange : Mock dependencies
        when(serviceLocator.getService(ProjectService.class)).thenReturn(projectService);
        when(projectService.getById(project.getId())).thenReturn(project);
        when(titleMapper.toEntity(titleRequest)).thenReturn(title);
        when(titleRepo.existsByNameIgnoreCaseAndProjectId(titleRequest.getName(), project.getId())).thenReturn(true);

        //Act and Assert : Call the method and expect exception
        assertThatThrownBy(() -> titleService.add(titleRequest, project.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Title with the same name already exists in this project.");

        // Verify interactions
        verify(projectService, times(1)).getById(project.getId());
        verify(titleRepo, times(1)).existsByNameIgnoreCaseAndProjectId(titleRequest.getName(), project.getId());
        verify(titleRepo, never()).save(any(Title.class));
    }

    @Test
    void addMethod_ShouldThrowNoSuchElementException_WhenProjectDoesNotExist() {
        // Mock dependencies
        when(serviceLocator.getService(ProjectService.class)).thenReturn(projectService);
        when(projectService.getById(project.getId())).thenThrow(new NoSuchElementException("No project found with id = 1"));

        // Call the method and expect exception
        assertThatThrownBy(() -> titleService.add(titleRequest, project.getId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No project found with id = 1");

        // Verify interactions
        verify(projectService, times(1)).getById(project.getId());
        verify(titleRepo, never()).existsByNameIgnoreCaseAndProjectId(anyString(), anyLong());
        verify(titleRepo, never()).save(any(Title.class));
    }
    @Test
    void updateMethod_ShouldReturnUpdatedTitleSuccessfully_WhenTitleExistsAndNoConflict() {
        // Arrange: Mock dependencies
        when(titleRepo.findById(title.getId())).thenReturn(Optional.of(title)); // Simulating existing title
        when(titleMapper.toEntity(updatTitleRequest)).thenReturn(Title.builder()
                .name(updatTitleRequest.getName())
                .description(updatTitleRequest.getDescription())
                .build());
        when(titleRepo.save(any(Title.class))).thenReturn(updatedTitle); // Simulating save

        // Act: Call update method
        Title result = titleService.update(title.getId(), updatTitleRequest);

        // Assert: Verify update correctness
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(updatedTitle.getId());
        assertThat(result.getName()).isEqualTo(updatedTitle.getName());
        assertThat(result.getDescription()).isEqualTo(updatedTitle.getDescription());
        assertThat(result.getProject()).isEqualTo(updatedTitle.getProject());


        // Verify interactions
        verify(titleRepo, times(1)).findById(title.getId());
        verify(titleRepo, times(1)).save(any(Title.class));
        verify(nonNullBeanUtils, times(1)).copyProperties(any(), any(), eq("id"), eq("project")); // Verify copyProperties was called
    }

    @Test
    void updateMethod_ShouldThrowConflictException_WhenTitleWithSameNameExists() {
        // Arrange: Mock dependencies
        when(titleRepo.findById(title.getId())).thenReturn(Optional.of(title)); // Simulating existing title
        when(titleRepo.existsByNameIgnoreCaseAndProjectId(updatTitleRequest.getName(), project.getId())).thenReturn(true);
        when(titleMapper.toEntity(updatTitleRequest)).thenReturn(Title.builder()
                .name(updatTitleRequest.getName())
                .description(updatTitleRequest.getDescription())
                .build());

        // Act & Assert: Expect ConflictException
        assertThatThrownBy(() -> titleService.update(title.getId(), updatTitleRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Title with the same name already exists in this project.");

        // Verify interactions
        verify(titleRepo, times(1)).findById(title.getId());
        verify(titleRepo, times(1)).existsByNameIgnoreCaseAndProjectId(updatTitleRequest.getName(), project.getId());
        verify(titleRepo, never()).save(any(Title.class)); // Ensure that save is not called

    }

    @Test
    void updateMethod_ShouldThrowNoSuchElementException_WhenTitleDoesNotExist() {
        // Arrange: Mock dependencies
        when(titleRepo.findById(title.getId())).thenReturn(Optional.empty()); // Simulating missing title

        // Act & Assert: Expect NoSuchElementException
        assertThatThrownBy(() -> titleService.update(title.getId(), updatTitleRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("There is no title with id = " + title.getId());

        // Verify interactions
        verify(titleRepo, times(1)).findById(title.getId());
        verify(titleRepo, never()).save(any(Title.class));
    }



    @Test
    void delete_ShouldRemoveTitleSuccessfully_WhenTitleIsExistsAndNotAssignedToMembers() {
        // Arrange: Mock dependencies
        when(titleRepo.findById(title.getId())).thenReturn(Optional.of(title));
        when(serviceLocator.getService(MemberService.class)).thenReturn(memberService);
        when(memberService.isExistsByTitleIdAndProjectId(title.getId(), project.getId())).thenReturn(false);

        // Act &: Expect NoSuchElementException
        titleService.delete(title.getId());

        // Assert
        assertThat(project.getTitles()).doesNotContain(title); // Ensure title is removed from project

        // Verify interactions
        verify(titleRepo, times(1)).findById(title.getId());
        verify(memberService, times(1)).isExistsByTitleIdAndProjectId(title.getId(), project.getId());
    }

    @Test
    void delete_ShouldThrowConflictException_WhenTitleStillAssignedToMembers() {
        // Arrange
        when(titleRepo.findById(title.getId())).thenReturn(Optional.of(title));
        when(serviceLocator.getService(MemberService.class)).thenReturn(memberService);
        when(memberService.isExistsByTitleIdAndProjectId(title.getId(), project.getId())).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> titleService.delete(title.getId()))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Title is still assigned to members.");
        assertThat(project.getTitles()).contains(title); // Ensure title is not removed from project

        // Verify interactions
        verify(titleRepo, times(1)).findById(title.getId());
        verify(memberService, times(1)).isExistsByTitleIdAndProjectId(title.getId(), project.getId());
        verify(titleRepo, never()).delete(any(Title.class)); // Ensure delete() is NOT called
    }

    @Test
    void delete_ShouldThrowNoSuchElementException_WhenTitleDoesNotExist() {
        // Arrange: Simulate title not existing
        Long nonExistentTitleId = 999999L;
        when(titleRepo.findById(nonExistentTitleId)).thenReturn(Optional.empty());

        // Act & Assert: Expect NoSuchElementException
        assertThatThrownBy(() -> titleService.delete(nonExistentTitleId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("There is no title with id = " + nonExistentTitleId);

        // Verify interactions
        verify(titleRepo, times(1)).findById(nonExistentTitleId);
        verify(titleRepo, never()).delete(any(Title.class)); // Ensure delete() is NOT called
    }


    @Test
    void getOptionalById_ShouldReturnOptionalTitle_WhenTitleExists() {
        // Arrange
        Long titleId = title.getId();
        when(titleRepo.findById(titleId)).thenReturn(Optional.of(title));

        // Act
        Optional<Title> result = titleService.getOptionalById(titleId);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(title);

        // Verify interactions
        verify(titleRepo, times(1)).findById(titleId);
    }

    @Test
    void getOptionalById_ShouldReturnEmptyOptional_WhenTitleDoesNotExist() {
        // Arrange
        Long titleId = 2L; // Non-existent title
        when(titleRepo.findById(titleId)).thenReturn(Optional.empty());

        // Act
        Optional<Title> result = titleService.getOptionalById(titleId);

        // Assert
        assertThat(result).isEmpty();

        // Verify interactions
        verify(titleRepo, times(1)).findById(titleId);
    }

    @Test
    void getById_ShouldReturnTitle_WhenTitleExists() {
        // Arrange
        Long titleId = title.getId();
        when(titleRepo.findById(titleId)).thenReturn(Optional.of(title));

        // Act
        Title result = titleService.getById(titleId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(title);

        // Verify interactions
        verify(titleRepo, times(1)).findById(titleId);
    }

    @Test
    void getById_ShouldThrowNoSuchElementException_WhenTitleDoesNotExist() {
        // Arrange
        Long titleId = 2L; // Non-existent title
        when(titleRepo.findById(titleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> titleService.getById(titleId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("There is no title with id = " + titleId);

        // Verify interactions
        verify(titleRepo, times(1)).findById(titleId);
    }

    @Test
    void getAllByProjectId_ShouldReturnTitles_WhenProjectExists() {
        // Arrange: Mock ProjectService to ensure project exists
        when(serviceLocator.getService(ProjectService.class)).thenReturn(projectService);
        when(projectService.getById(project.getId())).thenReturn(project);

        // Mock title repository to return a list of titles
        List<Title> expectedTitles = List.of(title, updatedTitle);
        when(titleRepo.findAllByProjectId(project.getId())).thenReturn(expectedTitles);

        // Act: Call the method
        List<Title> actualTitles = titleService.getAllByProjectId(project.getId());

        // Assert: Verify that the correct titles are returned
        assertThat(actualTitles)
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrder(title, updatedTitle);

        // Verify interactions
        verify(serviceLocator, times(1)).getService(ProjectService.class);
        verify(projectService, times(1)).getById(project.getId());
        verify(titleRepo, times(1)).findAllByProjectId(project.getId());
    }

    @Test
    void getAllByProjectId_ShouldThrowNoSuchElementException_WhenProjectDoesNotExist() {
        // Arrange: Mock ProjectService to throw NoSuchElementException when project is not found
        Long projectId = 999L;
        when(serviceLocator.getService(ProjectService.class)).thenReturn(projectService);
        when(projectService.getById(anyLong())).thenThrow(new NoSuchElementException("Project not found"));

        // Act & Assert: Expect NoSuchElementException
        assertThatThrownBy(() -> titleService.getAllByProjectId(projectId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Project not found");

        // Verify interactions
        verify(serviceLocator, times(1)).getService(ProjectService.class);
        verify(projectService, times(1)).getById(anyLong());
        verify(titleRepo, never()).findAllByProjectId(anyLong()); // Ensure repository is never called
    }




}
