package com.edara.edara.service.impl;

import com.edara.edara.exception.ConflictException;
import com.edara.edara.model.dto.DailyAttendanceResponse;
import com.edara.edara.model.entity.CurrentAttendance;
import com.edara.edara.model.entity.DailyAttendance;
import com.edara.edara.model.entity.MemberShip;
import com.edara.edara.model.entity.Project;
import com.edara.edara.model.mapper.DailyAttendanceMapper;
import com.edara.edara.repository.DailyAttendanceRepo;
import com.edara.edara.service.CurrentAttendanceService;
import com.edara.edara.service.DailyAttendanceService;
import com.edara.edara.utils.Utilts;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DailyAttendanceServiceImpl implements DailyAttendanceService {
    private final DailyAttendanceRepo dailyAttendanceRepo;
    private final DailyAttendanceMapper dailyAttendanceMapper;
    private final CurrentAttendanceService currentAttendanceService;

    @PersistenceContext
    private EntityManager entityManager;

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

    @Transactional
    @Override
    public DailyAttendance add(MemberShip member, Project project, LocalDateTime startTime) {
        /*
         * When this method is called, the `member` and `project` entities are likely
         * in a **detached state** because they were retrieved in a separate transaction
         * (e.g., from a scheduled job or a different service layer method).
         *
         * In JPA, detached entities cannot be directly associated with other managed entities
         * or persisted to the database. Attempting to do so may result in an
         * `org.hibernate.PersistentObjectException: detached entity passed to persist` error.
         *
         * To resolve this, we use `entityManager.merge(entity)`, which:
         *  1. Checks if the entity already exists in the persistence context.
         *  2. If it does, returns the managed instance.
         *  3. If it doesn’t, fetches the entity from the database and returns a new managed instance.
         *
         * By merging `member` and `project`, we ensure they are **reattached** before assigning them
         * to `dailyAttendance`. This allows Hibernate to correctly track changes and persist them
         * without errors.
         */
        MemberShip managedMember = entityManager.merge(member);
        Project managedProject = entityManager.merge(project);

        DailyAttendance dailyAttendance = create(startTime);
        dailyAttendance.setMember(managedMember);
        dailyAttendance.setProject(managedProject);
        return save(dailyAttendance);
    }


    @Transactional
    public CurrentAttendance recordAttendance(MemberShip member, Project project) {

        // Check if there's an ongoing attendance record for the member in the project
        currentAttendanceService.getEntityByMemberIdAndProjectIdAndEndTimeIsNull(member.getId(),project.getId()).ifPresent(
                attendance -> {
                    throw new ConflictException("Member already recorded.");
                }
        );

        // Retrieve daily attendance if exists, otherwise create a new one
        DailyAttendance dailyAttendance = getEntityByMemberIdAndProjectIdAndDateAndIsAggregatedFalse(member.getId(), project.getId(), LocalDate.now())
                .orElseGet(() -> add(member, project, LocalDateTime.now()));

        // Record new attendance
        CurrentAttendance currentAttendance = currentAttendanceService.recordCurrentAttendance(member, project, dailyAttendance);

        dailyAttendance.getCurrentAttendances().add(currentAttendance);
        save(dailyAttendance);

        return currentAttendance;
    }

    @Transactional
    public CurrentAttendance endAttendance(MemberShip member, Project project) {

        // Retrieve the active attendance record for the member in the project
        CurrentAttendance currentAttendance = currentAttendanceService
                .getEntityByMemberIdAndProjectIdAndEndTimeIsNull(member.getId(), project.getId())
                .orElseThrow(() -> new ConflictException(
                        "There is no  attendance recorded for member with code = " + member.getUser().getUserCode())
                );
        DailyAttendance dailyAttendance = currentAttendance.getDailyAttendance();
        currentAttendance = currentAttendanceService.endCurrentAttendance(currentAttendance);


        dailyAttendance.setEndTime(LocalDateTime.now());
        dailyAttendance.getCurrentAttendances().add(currentAttendance);
        save(dailyAttendance);

         return currentAttendance;

    }

    public DailyAttendanceResponse toResponse(DailyAttendance dailyAttendance){
        return dailyAttendanceMapper.toResponse(dailyAttendance);
    }


    public Optional<DailyAttendance> getEntityByMemberIdAndProjectIdAndDateAndIsAggregatedFalse(Long memberId, Long projectId, LocalDate date) {
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
    public List<DailyAttendance> getAllByProjectIdAndMemberId(Long projectId,Long memberId, Integer year,Integer month) {
        // Validate inputs
        if (month != null && year == null) {
            throw new ConflictException("Month cannot be specified without a year.");
        }
        if(month != null && year != null)
            return getAllByProjectIdAndMemberIdInSpecificMonth(projectId,memberId,year,month);
        else if (month != null)
            return getAllByProjectIdAndMemberIdInSpecificYear(projectId,memberId,year);
        else
            return getAllByProjectIdAndMemberIdAndIsAggregatedTure(projectId,memberId);
    }

    private List<DailyAttendance> getAllByProjectIdAndIsAggregatedTrue(Long projectId) {
        return dailyAttendanceRepo.findAllByProjectIdAndIsAggregatedTrue(projectId);
    }
    private List<DailyAttendance> getAllByProjectIdAndDateAndIsAggregatedTrue(Long projectId, LocalDateTime date) {
        return dailyAttendanceRepo.findAllByProjectIdAndDateAndIsAggregatedTrue(projectId, date);
    }
    public List<DailyAttendance> getAllByProjectIdAndIsAggregatedTrue(Long projectId, LocalDateTime date) {
        if (date != null)
            return getAllByProjectIdAndDateAndIsAggregatedTrue(projectId, date);
        return getAllByProjectIdAndIsAggregatedTrue(projectId);
    }

    @Override
    public List<DailyAttendance> getAllCurrentAttendancesByProjectId(Long projectId) {
        return dailyAttendanceRepo.findAllByProjectIdAndEndTimeIsNullAndIsAggregatedFalse(projectId);
    }

    @Override
    public List<DailyAttendance> getAllAbsencesByProjectIdAndMemberId(Long projectId, Long memberId) {
        return dailyAttendanceRepo.findAllByProjectIdAndMemberIdAndStartTimeNullAndEndTimeIsNull(projectId,memberId);

    }

    private List<DailyAttendance> getAllAbsencesByProjectIdAndDate(Long projectId, LocalDate date){
        return dailyAttendanceRepo.findAllByProjectIdAndDateAndStartTimeNullAndEndTimeIsNull(projectId,date);
    }
    private List<DailyAttendance> getAllAbsencesByProjectId(Long projectId){
        return dailyAttendanceRepo.findAllByProjectIdAndStartTimeNullAndEndTimeIsNull(projectId);
    }

    @Override
    public List<DailyAttendance> getAllAbsencesByProjectId(Long projectId, LocalDate date) {
        if (date != null)
            return getAllAbsencesByProjectIdAndDate(projectId,date);
        return getAllAbsencesByProjectId(projectId);
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
     * the method will finalize the attendance by calling {@link #endAttendance(MemberShip, Project)}.
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

        // If the last attendance has no end time, end the attendance first
        if (lastAttendance.getEndTime() == null) {
            lastAttendance = endAttendance(lastAttendance.getMember(), lastAttendance.getProject());
        }

        // Return the last attendance's end time
        return lastAttendance.getEndTime();
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
     * @throws IllegalArgumentException If the {@link DailyAttendance} is null or contains invalid data (e.g., invalid attendance records).
     * @see #getFirstTime(List)
     * @see #getOrFinalizeLastEndTime(List)
     * @see Utilts#calculatePeriod(LocalDateTime, LocalDateTime)
     */
    private DailyAttendance aggregate(DailyAttendance dailyAttendance) {
        List<CurrentAttendance> currentAttendances = dailyAttendance.getCurrentAttendances();

        // Return existing dailyAttendance if no attendance records are found
        if (currentAttendances == null || currentAttendances.isEmpty()) {
            return dailyAttendance;
        }

        LocalDateTime firstStartTime = getFirstTime(currentAttendances);
        LocalDateTime lastEndTime = getOrFinalizeLastEndTime(currentAttendances);

        if (firstStartTime != null && lastEndTime != null) {
            dailyAttendance.setPeriod(Utilts.calculatePeriod(firstStartTime, lastEndTime));

            // Mark all CurrentAttendance records as aggregated
            currentAttendances.forEach(attendance -> attendance.setIsAggregated(true));
        }

        dailyAttendance.setIsAggregated(true);
        return save(dailyAttendance);
    }


//    @Transactional
//    public void aggregateDailyMemberAttendancesOfProject(MemberShip member, Project project) {
//        System.out.println("Aggregating daily attendances for member: " + member.getId() + " in project: " + project.getId());
//        DailyAttendance dailyAttendance = getEntityByMemberIdAndProjectIdAndDateAndIsAggregatedFalse(
//                member.getId(), project.getId(), LocalDate.now()
//        ).orElseGet(() -> add(member, project).setIsAggregated(true));
//        System.out.println("Here1");
//        if (!dailyAttendance.getCurrentAttendances().isEmpty()) {
//            System.out.println("Here2");
//            aggregate(dailyAttendance);
//        }
//    }


    @Transactional
    public void aggregateDailyMemberAttendancesOfProject(MemberShip member, Project project) {
        //System.out.println("Aggregating daily attendances for member: " + member.getId() + " in project: " + project.getId());

        // Try to fetch an existing DailyAttendance
        Optional<DailyAttendance> OptionalDailyAttendance = getEntityByMemberIdAndProjectIdAndDateAndIsAggregatedFalse(
                member.getId(), project.getId(), LocalDate.now()
        );

        DailyAttendance dailyAttendance = OptionalDailyAttendance.orElseGet(() -> {
            // If not found, create a new DailyAttendance and set isAggregated to true
            DailyAttendance newDailyAttendance = add(member, project,null);
            newDailyAttendance.setIsAggregated(true); // Mark it as aggregated
            save(newDailyAttendance); // Persist the new DailyAttendance
            return newDailyAttendance;
        });

        // Only aggregate if there are current attendances
        if (!dailyAttendance.getCurrentAttendances().isEmpty()) {
            aggregate(dailyAttendance);
        }
    }



}
