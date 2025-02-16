package com.edara.edara.attendance;

import com.edara.edara.exception.ConflictException;
import com.edara.edara.global.ServiceLocator;
import com.edara.edara.member.Member;
import com.edara.edara.member.MemberService;
import com.edara.edara.project.Project;
import com.edara.edara.project.ProjectService;
import com.edara.edara.user.UserService;
import com.edara.edara.global.utils.Utilts;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DailyAttendanceServiceImpl implements DailyAttendanceService {
    private final DailyAttendanceRepo dailyAttendanceRepo;
    private final DailyAttendanceMapper dailyAttendanceMapper;
    private final CurrentAttendanceService currentAttendanceService;
    private final ServiceLocator serviceLocator;

    public DailyAttendance create(LocalDateTime startTime) {
        DailyAttendance dailyAttendance = new DailyAttendance();
        dailyAttendance.setDate(LocalDate.now());
        dailyAttendance.setStartTime(startTime);
        dailyAttendance.setEndTime(null);
        dailyAttendance.setPeriod(null);
        dailyAttendance.setIsAggregated(false);
        return dailyAttendance;
    }
    public DailyAttendance save(DailyAttendance dailyAttendance){
        return dailyAttendanceRepo.save(dailyAttendance);
    }

    public DailyAttendanceResponse toResponse(DailyAttendance dailyAttendance){
        return dailyAttendanceMapper.toResponse(dailyAttendance);
    }

    @Transactional
    @Override
    public DailyAttendance add(Member member, Project project, LocalDateTime startTime) {
        DailyAttendance dailyAttendance = create(startTime);
        dailyAttendance.setMember(member);
        dailyAttendance.setProject(project);
        return save(dailyAttendance);
    }

    private void throwExceptionIfMemberAlreadyRecorded(Long memberId, Long projectId) {

        currentAttendanceService.getOnGoingAttendanceByMemberAndProject(memberId,projectId).ifPresent(
                attendance -> {
                    throw new ConflictException("Member already recorded.");
                }
        );

    }
    @Override
    @Transactional
    public CurrentAttendanceResponse recordAttendance(String userCode, Long projectId) {

        serviceLocator.getService(UserService.class).getByCode(userCode);
        Project project = serviceLocator.getService(ProjectService.class).getById(projectId);
        Member member = serviceLocator.getService(MemberService.class).getByUserCodeAndProjectId(userCode, projectId);

        // Check if there's an ongoing attendance record for the member in the project
        throwExceptionIfMemberAlreadyRecorded(member.getId(), project.getId());

        // Retrieve daily attendance if exists, otherwise create a new one
        DailyAttendance onGoingDailyAttendance = getOnGoingDailyAttendanceByUserCodeAndProjectId(userCode, projectId)
                .orElseGet(() -> add(member, project, LocalDateTime.now()));


        // Record new attendance
        CurrentAttendance currentAttendance = currentAttendanceService.recordCurrentAttendance(onGoingDailyAttendance);

        onGoingDailyAttendance.getCurrentAttendances().add(currentAttendance);
        onGoingDailyAttendance.setEndTime(null);
        save(onGoingDailyAttendance);

        return currentAttendanceService.toResponse(currentAttendance);
    }

    @Transactional
    public CurrentAttendanceResponse endAttendance(String userCode, Long projectId) {

        serviceLocator.getService(UserService.class).getByCode(userCode);
        Project project = serviceLocator.getService(ProjectService.class).getById(projectId);
        Member member = serviceLocator.getService(MemberService.class).getByUserCodeAndProjectId(userCode, projectId);


        // Retrieve the active attendance record for the member in the project
        CurrentAttendance currentAttendance = currentAttendanceService
                .getOnGoingAttendanceByMemberAndProject(member.getId(), project.getId())
                .orElseThrow(() -> new ConflictException(
                        "There is no  attendance recorded for member with code = " + userCode)
                );
        DailyAttendance dailyAttendance = currentAttendance.getDailyAttendance();
        currentAttendance = currentAttendanceService.endCurrentAttendance(currentAttendance);


        dailyAttendance.setEndTime(LocalDateTime.now());
        dailyAttendance.getCurrentAttendances().add(currentAttendance);
        save(dailyAttendance);

         return currentAttendanceService.toResponse(currentAttendance);

    }

    @Override
    public Optional<DailyAttendance> getOnGoingDailyAttendanceByUserCodeAndProjectId(String userCode, Long projectId) {

        serviceLocator.getService(UserService.class).getByCode(userCode);
        serviceLocator.getService(ProjectService.class).getById(projectId);
        Member member = serviceLocator.getService(MemberService.class).getByUserCodeAndProjectId(userCode, projectId);

        return dailyAttendanceRepo.findByMember_IdAndProject_IdAndDateAndIsAggregatedIsFalse(member.getId(), projectId, LocalDate.now());
    }
    public Optional<DailyAttendance> getUnAggregatedByMemberIdAndProjectIdAndDate(Long memberId, Long projectId, LocalDate date) {
        return dailyAttendanceRepo.findByMemberIdAndProjectIdAndDateIsAggregatedFalse(memberId, projectId, date);
    }




    private List<DailyAttendance> getAllByProjectIdAndMemberIdAndIsAggregatedTure(Long projectId,Long memberId) {
        return dailyAttendanceRepo.findAllByProjectIdAndMemberIdAndIsAggregatedTrue(projectId,memberId);
    }
    private List<DailyAttendance> getAllByProjectIdAndMemberIdInSpecificYear(Long projectId, Long memberId, Integer year) {
        return dailyAttendanceRepo.findAllByProjectIdAndMemberIdInSpecificYear(projectId, memberId, year);
    }
    private List<DailyAttendance> getAllByProjectIdAndMemberIdInSpecificMonth(Long projectId, Long memberId, Integer year,Integer month) {
        return dailyAttendanceRepo.findAllByProjectIdAndMemberIdInSpecificMonth(projectId, memberId, year,month);
    }
    public List<DailyAttendance> getAllByProjectIdAndMemberId(Long projectId,String userCode, Integer year,Integer month) {
        serviceLocator.getService(ProjectService.class).getById(projectId);
        serviceLocator.getService(UserService.class).getByCode(userCode);
        Member member = serviceLocator.getService(MemberService.class).getByUserCodeAndProjectId(userCode, projectId);

        // Validate inputs
        if (month != null && year == null) {
            throw new ConflictException("Month cannot be specified without a year.");
        }
        if(month != null && year != null)
            return getAllByProjectIdAndMemberIdInSpecificMonth(projectId,member.getId(),year,month);
        else if (month != null)
            return getAllByProjectIdAndMemberIdInSpecificYear(projectId,member.getId(),year);
        else
            return getAllByProjectIdAndMemberIdAndIsAggregatedTure(projectId,member.getId());
    }
    public List<DailyAttendanceResponse> getResponseAllByProjectIdAndUserCode(Long projectId, String userCode, Integer year, Integer month){
        return getAllByProjectIdAndMemberId(projectId, userCode, year, month)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList()); // Collect the stream into a List

    }




    private List<DailyAttendance> getAllByProjectIdAndIsAggregatedTrue(Long projectId) {
        return dailyAttendanceRepo.findAllByProjectIdAndIsAggregatedTrue(projectId);
    }
    private List<DailyAttendance> getAllByProjectIdAndDateAndIsAggregatedTrue(Long projectId, LocalDateTime date) {
        return dailyAttendanceRepo.findAllByProjectIdAndDateAndIsAggregatedTrue(projectId, date);
    }
    public List<DailyAttendance> getAllByProjectId(Long projectId, LocalDateTime date) {
        serviceLocator.getService(ProjectService.class).getById(projectId);
        if (date != null)
            return getAllByProjectIdAndDateAndIsAggregatedTrue(projectId, date);
        return getAllByProjectIdAndIsAggregatedTrue(projectId);
    }
    public List<DailyAttendanceResponse> getResponseAllByProjectId(Long projectId, LocalDateTime date){
        return getAllByProjectId(projectId, date)
                .stream()
                .map(this::toResponse) // Method reference for cleaner code
                .collect(Collectors.toList()); // Collect the stream into a List
    }



    @Override
    public List<DailyAttendance> getAllActiveAttendancesByProjectId(Long projectId) {
        serviceLocator.getService(ProjectService.class).getById(projectId);
        return dailyAttendanceRepo.findAllByProjectIdAndEndTimeIsNullAndIsAggregatedFalse(projectId);
    }
    @Override
    public List<DailyAttendanceResponse> getResponseAllActiveAttendancesByProjectId(Long projectId){
        return getAllActiveAttendancesByProjectId(projectId)
                .stream()
                .map(this::toResponse) // Method reference for cleaner code
                .collect(Collectors.toList()); // Collect the stream into a List
    }



    @Override
    public List<DailyAttendance> getAllAbsencesByProjectIdAndUserCode(Long projectId, String userCode) {
        serviceLocator.getService(ProjectService.class).getById(projectId);
        serviceLocator.getService(UserService.class).getByCode(userCode);
        Member member = serviceLocator.getService(MemberService.class).getByUserCodeAndProjectId(userCode, projectId);
        return dailyAttendanceRepo.findAllByProjectIdAndMemberIdAndStartTimeNullAndEndTimeIsNull(projectId,member.getId());

    }
    @Override
    public List<DailyAttendanceResponse> getResponseAllAbsencesByProjectIdAndUserCode(Long projectId, String userCode) {
        return getAllAbsencesByProjectIdAndUserCode(projectId, userCode)
                .stream()
                .map(this::toResponse) // Method reference for cleaner code
                .collect(Collectors.toList()); // Collect the stream into a List
    }
    private List<DailyAttendance> getAllAbsencesByProjectIdAndDate(Long projectId, LocalDate date){
        serviceLocator.getService(ProjectService.class).getById(projectId);
        return dailyAttendanceRepo.findAllByProjectIdAndDateAndStartTimeNullAndEndTimeIsNull(projectId,date);
    }
    private List<DailyAttendance> getAllAbsencesByProjectId(Long projectId){
        serviceLocator.getService(ProjectService.class).getById(projectId);
        return dailyAttendanceRepo.findAllByProjectIdAndStartTimeNullAndEndTimeIsNull(projectId);
    }
    @Override
    public List<DailyAttendance> getAllAbsencesByProjectId(Long projectId, LocalDate date) {
        if (date != null)
            return getAllAbsencesByProjectIdAndDate(projectId,date);
        return getAllAbsencesByProjectId(projectId);
    }
    @Override
    public List<DailyAttendanceResponse> getResponseAllAbsencesByProjectId(Long projectId, LocalDate date){
        return getAllAbsencesByProjectId(projectId, date)
                .stream()
                .map(this::toResponse) // Method reference for cleaner code
                .collect(Collectors.toList()); // Collect the stream into a List
    }




    /**
     * Retrieves the earliest start time from a list of {@link CurrentAttendance} objects.
     * <p>
     * This method will iterate over the list of attendances, filter out any null start times,
     * and return the earliest {@link LocalDateTime} value. If the list is empty or if all start
     * times are null, it will return null.
     * </p>
     *
     * @param currentAttendances The list of {@link CurrentAttendance} objects to check for start times.
     *                           This list may be empty or contain null values.
     * @return The earliest {@link LocalDateTime} representing the start time, or null if there are no valid start times.
     */
    private LocalDateTime getFirstTime(List<CurrentAttendance> currentAttendances) {
        if (currentAttendances == null || currentAttendances.isEmpty()) {
            return null;
        }

        return currentAttendances.stream()
                .map(CurrentAttendance::getStartTime) // Extracts startTime directly
                .filter(Objects::nonNull) // Filters out null values
                .min(LocalDateTime::compareTo) // Finds the earliest startTime
                .orElse(null); // Returns null if none found
    }

    /**
     * Retrieves the end time of the last attendance record, finalizing the attendance if necessary.
     * <p>
     * This method sorts the list of {@link CurrentAttendance} objects by their end time (with null values last).
     * It then checks the last record in the sorted list. If the end time of the last record is null,
     * the method will finalize the attendance by calling {@link #endAttendance(String userCode, Long projectId)}.
     * Finally, it returns the end time of the last attendance, either from the existing record or the finalized one.
     * </p>
     *
     * @param currentAttendances The list of {@link CurrentAttendance} objects to check for end times.
     *                           This list may be empty, and the end times may be null.
     * @return The {@link LocalDateTime} representing the end time of the last attendance record,
     *         or null if no valid attendance is found.
     */
    private LocalDateTime getOrFinalizeLastEndTime(List<CurrentAttendance> currentAttendances) {
        if (currentAttendances == null || currentAttendances.isEmpty()) {
            return null;
        }

        // Sort attendances by endTime (nulls last)
        currentAttendances.sort(
                Comparator.comparing(
                        CurrentAttendance::getEndTime,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );

        // Get the last attendance record
        CurrentAttendance lastAttendance = currentAttendances.get(currentAttendances.size() - 1);
        LocalDateTime lastEndTime = lastAttendance.getEndTime();

        // If the last attendance has no end time, end the attendance first
        if (lastEndTime == null) {
            lastEndTime = endAttendance(lastAttendance.getDailyAttendance().getMember().getUser().getUserCode(), lastAttendance.getDailyAttendance().getProject().getId()).getEndTime();
        }

        // Return the last attendance's end time
        return lastEndTime;
    }

    /**
     * Aggregates the attendance records for a given {@link DailyAttendance} by calculating the total attendance period
     * and marking all attendance records as aggregated.
     * <p>
     * This method first checks if the given {@link DailyAttendance} contains any valid attendance records. If no attendance
     * records are found, the method returns the existing {@link DailyAttendance} object without modification.
     * </p>
     * <p>
     * If attendance records are present, the method calculates the attendance period by determining the earliest start time
     * and the latest end time. It then updates the {@link DailyAttendance} period and marks all related {@link CurrentAttendance}
     * records as aggregated.
     * </p>
     * <p>
     * The method also updates the {@link DailyAttendance} to indicate that it has been aggregated.
     * </p>
     *
     * @param dailyAttendance The {@link DailyAttendance} object that contains the attendance records to be aggregated.
     * @return The updated {@link DailyAttendance} object, with the attendance period calculated and all attendance records marked as aggregated.
     * @see #getFirstTime(List)
     * @see #getOrFinalizeLastEndTime(List)
     * @see Utilts#calculatePeriod(LocalDateTime, LocalDateTime)
     */

    private DailyAttendance aggregate(DailyAttendance dailyAttendance) {
        List<CurrentAttendance> currentAttendances = dailyAttendance.getCurrentAttendances();

        if (CollectionUtils.isEmpty(currentAttendances)) {
            return dailyAttendance;
        }

        LocalDateTime firstStartTime = getFirstTime(currentAttendances);
        LocalDateTime lastEndTime = getOrFinalizeLastEndTime(currentAttendances);

        if (firstStartTime == null || lastEndTime == null) {
            return dailyAttendance;
        }

        dailyAttendance.setPeriod(Utilts.calculatePeriod(firstStartTime, lastEndTime));

        // Use forEach only if necessary; otherwise, consider using a batch update
        currentAttendances.forEach(attendance -> attendance.setIsAggregated(true));

        dailyAttendance.setIsAggregated(true);
        return save(dailyAttendance);
    }


    @Transactional
    public void aggregateDailyMemberAttendancesOfProject(Long memberId, Long projectId) {

        Optional<DailyAttendance> optionalDailyAttendance =
                getOnGoingDailyAttendanceByUserCodeAndProjectId(
                        serviceLocator.getService(MemberService.class).getById(memberId).getUser().getUserCode()
                        , projectId
                );

        DailyAttendance dailyAttendance;

        if (optionalDailyAttendance.isPresent()) {
            dailyAttendance = optionalDailyAttendance.get();
        } else {
            dailyAttendance = add(
                    serviceLocator.getService(MemberService.class).getById(memberId),
                    serviceLocator.getService(ProjectService.class).getById(projectId),
                    null
            );
            dailyAttendance.setIsAggregated(true);
            save(dailyAttendance);
        }

        if (!CollectionUtils.isEmpty(dailyAttendance.getCurrentAttendances())) {
            aggregate(dailyAttendance);
        }
    }



}
