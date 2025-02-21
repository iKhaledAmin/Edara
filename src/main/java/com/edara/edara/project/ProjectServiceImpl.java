package com.edara.edara.project;

import com.edara.edara.attendance.DailyAttendanceService;
import com.edara.edara.exception.ConflictException;
import com.edara.edara.global.ServiceLocator;
import com.edara.edara.member.Member;
import com.edara.edara.member.MemberRole;
import com.edara.edara.member.MemberService;
import com.edara.edara.title.Title;
import com.edara.edara.title.TitleRequest;
import com.edara.edara.title.TitleService;
import com.edara.edara.user.User;
import com.edara.edara.user.UserService;
import com.edara.edara.global.utils.NonNullBeanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepo projectRepo;
    private final ProjectMapper projectMapper;
    private final ServiceLocator serviceLocator;
    private final NonNullBeanUtils nonNullBeanUtils;




    @Transactional
    protected  void aggregateDailyAttendance(Long memberId, Long projectId) {
        serviceLocator.getService(DailyAttendanceService.class).aggregateDailyMemberAttendancesOfProject(memberId, projectId);
    }

    //@Scheduled(cron = "0 * * * * ?") // Runs every minute
    @Scheduled(cron = "0 0 * * * ?") // Runs at the start of every hour
    protected void aggregateAllMemberDailyAttendancesInProject() {
        //System.out.println("Aggregating all member daily attendances in projects...");
        Integer currentHour = LocalDateTime.now().getHour();
        List<Project> projects = projectRepo.findAllByAggregationHour(currentHour);
        if (!projects.isEmpty()) {
            projects.forEach(project -> {
                // Fetch all members of the project
                List<Member> members = project.getMembers();
                if (!members.isEmpty()) {
                    // Perform aggregation
                    members.forEach(member -> {
                        aggregateDailyAttendance(member.getId(), project.getId());
                    });
                }

            });
        }

    }



    private Long getNextId(){
        Long lastId = projectRepo.getLastId();
        if(lastId == null)
            return 0L;
        else
            return ++lastId;
    }
    private String hashIdToSixDigit(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes());
            BigInteger number = new BigInteger(1, encodedhash);
            BigInteger maxDigits = new BigInteger("1000000"); // 10^6
            BigInteger reducedNumber = number.mod(maxDigits);
            return String.format("%06d", reducedNumber); // Ensure it is 6 digits with leading zeros if necessary
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateUniqueProjectCode() {
        Long nextId = getNextId();
        String sequenceNumber = hashIdToSixDigit(nextId.toString());
        return sequenceNumber;
    }

    @Override
    public ProjectResponse toResponse(Project project) {
        return projectMapper.toResponse(project);
    }

    @Override
    public Project toEntity(ProjectRequest projectRequest) {
        return projectMapper.toEntity(projectRequest);
    }

    private Project create(){
        Project newProject = new Project();
        newProject.setCode(generateUniqueProjectCode());
        newProject.setStartedDate(LocalDate.now());

        return newProject;
    }

    private Project create(ProjectRequest projectRequest) {

        Project newProject = toEntity(projectRequest);
        newProject.setCode(generateUniqueProjectCode());
        newProject.setStartedDate(LocalDate.now());

        return newProject;
    }

    private Project save(Project project) {
        return projectRepo.save(project);
    }


    @Transactional
    @Override
    public Project add(Project newProject) {

        // Save the project first to get an ID
        newProject = save(newProject);

        // Create new title and add it to the project
        Title newTitle = serviceLocator.getService(TitleService.class)
                .add(new TitleRequest("Owner", "The owner of the project"), newProject.getId());

        // Get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = serviceLocator.getService(UserService.class).getByAccount(authentication.getName());

        // Create the owner member
        Member ownerMember = serviceLocator.getService(MemberService.class).add(user, newProject, MemberRole.OWNER, newTitle);

        // Ensure bi-directional relationship
        newProject.getMembers().add(ownerMember);

        return newProject;
    }

    @Override
    public Project add(ProjectRequest projectRequest) {
        Project newProject = toEntity(projectRequest);
        return add(newProject);
    }
    @Override
    public Project update(Long projectId, Project newProject) {
        Project existingProject = getById(projectId);

        // Copy properties from newProject to existedProject, excluding the "id", "code", "type","startedDate","members", "tasks", "titles","dailyAttendances"
        nonNullBeanUtils.copyProperties(newProject, existingProject, "id", "code", "type","startedDate","members", "tasks", "titles","dailyAttendances");

        // Save the updated project
        return projectRepo.save(existingProject);
    }
    @Override
    public Project update(Long projectId, ProjectRequest projectRequest) {
        Project newProject = projectMapper.toEntity(projectRequest);
        return update(projectId,newProject);
    }

    private void throwExceptionIfProjectStillHasEmployees(Project project) {
        if (project.getMembers().stream().anyMatch(member -> member.getMemberRole() != MemberRole.OWNER)) {
            throw new ConflictException("Cannot delete the project. It still has employees work on.");
        }
    }
    @Override
    public void delete(Long projectId) {
        Project project = getById(projectId);
        throwExceptionIfProjectStillHasEmployees(project);
        projectRepo.deleteById(projectId);
    }

    @Override
    public Optional<Project> getOptionalById(Long projectId) {
        return projectRepo.findById(projectId);
    }

    @Override
    public Project getById(Long projectId) {
        return getOptionalById(projectId).orElseThrow(
                () -> new NoSuchElementException("There is no project with id = " + projectId)
        );
    }

    @Override
    public ProjectResponse getResponseById(Long projectId) {
        return projectMapper.toResponse(getById(projectId));
    }


}
