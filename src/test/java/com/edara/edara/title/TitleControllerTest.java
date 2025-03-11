package com.edara.edara.title;

import com.edara.edara.global.ServiceLocator;
import com.edara.edara.member.Member;
import com.edara.edara.member.MemberRole;
import com.edara.edara.member.MemberService;
import com.edara.edara.person.Role;
import com.edara.edara.project.Project;
import com.edara.edara.project.ProjectService;
import com.edara.edara.project.ProjectType;
import com.edara.edara.user.User;
import com.edara.edara.user.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;




@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
@Transactional  // Ensures rollback after each test
class TitleControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ProjectService projectService;
    @Autowired
    private UserService userService;

    @Autowired
    private TitleService titleService;

    @Autowired
    private ServiceLocator serviceLocator;

    @Autowired
    private MemberService memberService;

    private Project project;
    private Title title;
    private static final String TITLE_NAME = "Title name";
    private static final String TITLE_DESCRIPTION = "Title description";

    @BeforeEach
    void setUp() {
        // Common test values
        User user = userService.add(User.builder()
                .firstName("John")
                .lastName("Doe")
                .account("johndoe@edare")
                .password("password")
                .role(Role.USER)
                .userCode("132873")
                .build());

        project = projectService.add(
                user.getUserCode()
                ,Project.builder()
                        .name("Test Project")
                        .description("Project Description")
                        .code("026197")
                        .type(ProjectType.OTHER)
                        .aggregationHour(12)
                        .startedDate(LocalDate.now())
                        .members(new ArrayList<>())
                        .tasks(new ArrayList<>())
                        .titles(new ArrayList<>())
                        .dailyAttendances(new ArrayList<>())
                        .build()

        );
         title = titleService.add(
                 project.getId()
                 ,Title.builder()
                .name(TITLE_NAME)
                .description(TITLE_DESCRIPTION)
                .project(project)
                .build());

        Member member = memberService.add(
                user
                , project
                , MemberRole.WORKER
                , title
        );
    }

    @AfterEach
    void tearDown() {
        //projectService.delete(project.getId()); // Delete the project and its associated titles
    }

    @Test
    void add_ShouldAddTitleAndReturnTitleResponseSuccessfully() throws Exception {
        // Arrange
        String newTitleName = "New Title Name";
        String newTitleDescription = "New Title Description";

        TitleRequest request = TitleRequest.builder()
                .name(newTitleName)
                .description(newTitleDescription)
                .build();

        // Act
        var result = mockMvc.perform(post("/titles/add/{projectId}", project.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(Matchers.greaterThan(0)))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.description").isNotEmpty())
                .andExpect(jsonPath("$.name").value(newTitleName))
                .andExpect(jsonPath("$.description").value(newTitleDescription));

    }
    @Test
    void add_ShouldReturn400_WhenNameIsNull() throws Exception {

        // Arrange
        String nullTitleName = null;
        String newTitleDescription = "New Title Description";

        TitleRequest request = TitleRequest.builder()
                .name(nullTitleName)
                .description(newTitleDescription)
                .build();

        // Act
        var result = mockMvc.perform(post("/titles/add/{projectId}", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Name must not be null"));
    }
    @Test
    void add_ShouldReturn400_WhenNameIsInvalid() throws Exception {
        // Arrange
        String invalidTitleName = "a"; // Too short
        String nowTitleDescription = "Now Title Description";

        TitleRequest request = TitleRequest.builder()
                .name(invalidTitleName)
                .description(nowTitleDescription)
                .build();

        // Act
        var result = mockMvc.perform(post("/titles/add/{projectId}", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Name must be between 3 and 50 characters"));
    }
    @Test
    void add_ShouldReturn400_WhenDescriptionIsNull() throws Exception {
        // Arrange
        String newTitleName = "New Title Name";
        String nullTitleDescription = null;

        TitleRequest request = TitleRequest.builder()
                .name(newTitleName)
                .description(nullTitleDescription)
                .build();

        // Act
        var result = mockMvc.perform(post("/titles/add/{projectId}", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Description must not be null"));
    }
    @Test
    void add_ShouldReturn400_WhenDescriptionIsInvalid() throws Exception {
        // Arrange
        String newTitleName = "New Title Name";
        String invalidTitleDescription = "a".repeat(65536); // Too long

        TitleRequest request = TitleRequest.builder()
                .name(newTitleName)
                .description(invalidTitleDescription)
                .build();

        // Act
        var result = mockMvc.perform(post("/titles/add/{projectId}", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Description must not exceed 65535 characters"));
    }
    @Test
    void add_ShouldReturn404_WhenProjectNotFound() throws Exception {
        // Arrange
        String newTitleName = "New Title Name";
        String newTitleDescription = "New Title Description";
        Long nonExistingProjectId = 9999999L; // Non-existing project ID
        TitleRequest request = TitleRequest.builder()
                .name(newTitleName)
                .description(newTitleDescription)
                .build();

        // Act
        var result = mockMvc.perform(post("/titles/add/{projectId}", nonExistingProjectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format("There is no project with id = %d", nonExistingProjectId)));    }
    @Test
    void add_ShouldReturn409_WhenTitleAlreadyExists() throws Exception {
        // Arrange
        // Pass the same name and description
        TitleRequest request = TitleRequest.builder()
                .name(title.getName())
                .description(title.getDescription())
                .build();

        //Act

        var result = mockMvc.perform(post("/titles/add/{projectId}", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));


        // Assert
        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Title with the same name already exists in this project."));

    }
    @Test
    void update_ShouldUpdateTitleAndReturnTitleResponseSuccessfully() throws Exception {
        // Arrange
        String updatedName = "Updated Title Name";
        String updatedDescription = "Updated Title Description";

        TitleRequest request = TitleRequest.builder()
                .name(updatedName)
                .description(updatedDescription)
                .build();

        // Act
        var result = mockMvc.perform(put("/titles/update/{titleId}", title.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(title.getId()))
                .andExpect(jsonPath("$.name").isNotEmpty())
                .andExpect(jsonPath("$.description").isNotEmpty())
                .andExpect(jsonPath("$.name").value(updatedName))
                .andExpect(jsonPath("$.description").value(updatedDescription));
    }
    @Test
    void update_ShouldReturn400_WhenNameIsNull() throws Exception {
        // Arrange
        String nullTitleName = null;
        String updatedDescription = "Updated Title Description";

        TitleRequest request = TitleRequest.builder()
                .name(nullTitleName)
                .description(updatedDescription)
                .build();

        // Act
        var result = mockMvc.perform(put("/titles/update/{titleId}", title.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Name must not be null"));
    }
    @Test
    void update_ShouldReturn400_WhenNameIsInvalid() throws Exception {
        // Arrange
        String invalidTitleName = "a".repeat(51); // Too long
        String updatedDescription = "Updated Title Description";

        TitleRequest request = TitleRequest.builder()
                .name(invalidTitleName)
                .description(updatedDescription)
                .build();

        // Act
        var result = mockMvc.perform(put("/titles/update/{titleId}", title.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Name must be between 3 and 50 characters"));
    }
    @Test
    void update_ShouldReturn400_WhenDescriptionIsNull() throws Exception {
        // Arrange
        String updatedName = "Updated Title Name";
        String nullTitleDescription = null;

        TitleRequest request = TitleRequest.builder()
                .name(updatedName)
                .description(nullTitleDescription)
                .build();

        // Act
        var result = mockMvc.perform(put("/titles/update/{titleId}", title.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Description must not be null"));
    }
    @Test
    void update_ShouldReturn400_WhenDescriptionIsInvalid() throws Exception {
        // Arrange
        String updatedName = "Updated Title Name";
        String invalidTitleDescription = "a".repeat(65536); // Too long

        TitleRequest request = TitleRequest.builder()
                .name(updatedName)
                .description(invalidTitleDescription)
                .build();

        // Act
        var result = mockMvc.perform(put("/titles/update/{titleId}", title.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Description must not exceed 65535 characters"));
    }
    @Test
    void update_ShouldReturn404_WhenTitleNotFound() throws Exception {
        // Arrange
        String updatedName = "Updated Title Name";
        String updatedDescription = "Updated Title Description";
        Long nonExistingTitleId = 99999L; // Non-existing title ID

        TitleRequest request = TitleRequest.builder()
                .name(updatedName)
                .description(updatedDescription)
                .build();

        // Act
        var result = mockMvc.perform(put("/titles/update/{titleId}", nonExistingTitleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format("There is no title with id = %d", nonExistingTitleId)));    }
    @Test
    void update_ShouldReturn409_WhenTitleNameAlreadyExists() throws Exception {
        // Arrange
        // Pass the same name and description
        TitleRequest request = TitleRequest.builder()
                .name(title.getName())
                .description(title.getDescription())
                .build();

        // Act
        var result = mockMvc.perform(put("/titles/update/{titleId}", title.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        // Assert
        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Title with the same name already exists in this project."));
    }

    @Test
    void deleteTitle_ShouldReturn204_WhenTitleDeletedSuccessfully() throws Exception {
        // Arrange
        Title newTitle = titleService.add(
                project.getId()
                ,Title.builder()
                .name("Deletable Title")
                .description("Can be deleted")
                .project(project)
                .build());

        // Act
        var result = mockMvc.perform(delete("/titles/delete/{titleId}", newTitle.getId()));

        // Assert
        result.andExpect(status().isNoContent());

    }

    @Test
    void deleteTitle_ShouldReturn404_WhenTitleNotFound() throws Exception {
        // Arrange
        Long nonExistingTitleId = 999999L;

        // Act
        var result = mockMvc.perform(delete("/titles/delete/{titleId}", nonExistingTitleId));

        // Assert
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format("There is no title with id = %d", nonExistingTitleId)));
    }

    @Test
    void deleteTitle_ShouldReturn409_WhenTitleIsAssignedToMembers() throws Exception {
        // Arrange:

        // Act: Attempt to delete the title
        var result = mockMvc.perform(delete("/titles/delete/{titleId}", title.getId()));

        // Assert: Expect 409 Conflict response
        result.andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Title is still assigned to members."));
    }


    @Test
    void getById_ShouldReturnTitleResponseSuccessfully() throws Exception {
        // Act
        var result = mockMvc.perform(get("/titles/get/{titleId}", title.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(title.getId()))
                .andExpect(jsonPath("$.name").value(title.getName()))
                .andExpect(jsonPath("$.description").value(title.getDescription()));
    }
    @Test
    void getById_ShouldReturn404_WhenTitleNotFound() throws Exception {
        // Arrange
        Long nonExistingTitleId = 99999L; // ID that does not exist

        // Act
        var result = mockMvc.perform(get("/titles/get/{titleId}", nonExistingTitleId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format("There is no title with id = %d", nonExistingTitleId)));
    }
    @Test
    void getAllTitlesByProjectId_ShouldReturnTitleListSuccessfully() throws Exception {
        // Arrange
        Title anotherTitle = titleService.add(
                project.getId()
                ,Title.builder()
                .name("Second Title")
                .description("Second Title Description")
                .project(project)
                .build());

        project.getTitles().add(anotherTitle);

        // Act
        var result = mockMvc.perform(get("/titles/get-all-of-project/{projectId}", project.getId())
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))     // Expecting 3 titles
                .andExpect(jsonPath("$[0].name").value("Owner"))  // Note that when user create project, it is automatically added new title (Owner)
                .andExpect(jsonPath("$[1].name").value(title.getName()))
                .andExpect(jsonPath("$[2].name").value(anotherTitle.getName()));
    }

    @Test
    void getAllTitlesByProjectId_ShouldReturn404_WhenProjectNotFound() throws Exception {
        // Arrange
        Long nonExistingProjectId = 9999999L; // ID that does not exist

        // Act
        var result = mockMvc.perform(get("/titles/get-all-of-project/{projectId}", nonExistingProjectId)
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format("There is no project with id = %d", nonExistingProjectId)));
    }

}