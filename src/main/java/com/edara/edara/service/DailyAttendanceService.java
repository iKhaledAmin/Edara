package com.edara.edara.service;

import com.edara.edara.exception.ConflictException;
import com.edara.edara.model.dto.DailyAttendanceResponse;
import com.edara.edara.model.entity.CurrentAttendance;
import com.edara.edara.model.entity.DailyAttendance;
import com.edara.edara.model.entity.MemberShip;
import com.edara.edara.model.entity.Project;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface DailyAttendanceService {
     DailyAttendance add( MemberShip member, Project project, LocalDateTime startTime);
     DailyAttendanceResponse toResponse(DailyAttendance dailyAttendance);

     /**
      * Records a new attendance session for a member in a specific project.
      *
      * This method performs the following steps:
      * <ol>
      *   <li>Checks if the member already has an ongoing attendance record (i.e., an attendance with a null end time).</li>
      *   <li>If an ongoing attendance is found, throws a {@link ConflictException}.</li>
      *   <li>Retrieves the existing {@link DailyAttendance} for the member and project on the current day, or creates a new one if none exists.</li>
      *   <li>Creates and records a new {@link CurrentAttendance} for the member in the project.</li>
      *   <li>Associates the new attendance with the corresponding {@link DailyAttendance} and updates it in the database.</li>
      *   <li>Returns the newly created {@link CurrentAttendance} object.</li>
      * </ol>
      *
      * This method is transactional, ensuring that all database operations are executed as part of a single transaction.
      *
      * @param member the {@link MemberShip} of the member whose attendance is being recorded
      * @param project the {@link Project} in which the attendance is being recorded
      * @return the newly created {@link CurrentAttendance} object
      * @throws ConflictException if the member already has an active attendance session in the project
      */
     CurrentAttendance recordAttendance(MemberShip member, Project project);

     /**
      * Ends the attendance session for a member in a specific project by retrieving the active
      * attendance record, marking the attendance as ended, and updating the associated daily attendance.
      *
      * This method performs the following steps:
      * <ol>
      *   <li>Retrieves the active {@link CurrentAttendance} for the given member and project where the end time is null.</li>
      *   <li>If no active attendance is found, throws a {@link ConflictException}.</li>
      *   <li>Ends the {@link CurrentAttendance} session by updating its end time to the current time.</li>
      *   <li>Updates the associated {@link DailyAttendance} by setting its end time and adding the current attendance to it.</li>
      *   <li>Saves the updated {@link DailyAttendance} to the database.</li>
      *   <li>Returns the updated {@link CurrentAttendance} object.</li>
      * </ol>
      *
      * This method is transactional, meaning that all the database operations will be committed as a single transaction.
      *
      * @param member the {@link MemberShip} of the member whose attendance is being ended
      * @param project the {@link Project} in which the attendance session is being recorded
      * @return the updated {@link CurrentAttendance} object
      * @throws ConflictException if no active attendance record is found for the member in the project
      */
     CurrentAttendance endAttendance(MemberShip member, Project project);


     /**
      * Retrieves an optional {@link DailyAttendance} record for a given member within a specific project
      * on a specified date, provided that the record has not been aggregated.
      *
      * <p>This method queries the {@code daily_attendance} table to find an entry where:
      * <ul>
      *     <li>The {@code member_id} matches the provided {@code memberId}.</li>
      *     <li>The {@code project_id} matches the provided {@code projectId}.</li>
      *     <li>The {@code date} matches the specified {@code date}.</li>
      *     <li>The {@code isAggregated} flag is false, indicating that the attendance data has not been finalized.</li>
      * </ul>
      *
      * <p><strong>Important:</strong> This method does not verify whether the provided {@code memberId}
      * actually belongs to the given {@code projectId}. It assumes the correctness of the input parameters,
      * so the caller must ensure that the {@code memberId} corresponds to a valid member within the specified
      * {@code projectId} before invoking this method.</p>
      *
      * <p>Use this method to check for an existing attendance record for a member on a particular date
      * before adding or updating attendance data.</p>
      *
      * @param memberId  The unique identifier of the member whose attendance record is being retrieved. Must not be null.
      * @param projectId The unique identifier of the project associated with the attendance record. Must not be null.
      * @param date      The specific date for which the attendance record is being queried. Must not be null.
      * @return An {@link Optional} containing the {@link DailyAttendance} record if found; otherwise, an empty {@link Optional}.
      */
     Optional<DailyAttendance> getEntityByMemberIdAndProjectIdAndDateAndIsAggregatedFalse(Long memberId, Long projectId, LocalDate date);


     /**
      * Retrieves a list of {@link DailyAttendance} records for a specific project and member based on the provided filters.
      *
      * <p>The method behavior is as follows:
      * <ul>
      *   <li>If both {@code year} and {@code month} are provided, it retrieves attendance records for the specific month in this year.</li>
      *   <li>If only {@code year} is provided, it retrieves attendance records for the this year.</li>
      *   <li>If neither {@code year} nor {@code month} is provided, it retrieves all attendance records for the member in this project.</li>
      * </ul>
      *
      * <p><strong>Important:</strong> This method does not validate whether the provided
      * {@code projectId} and {@code memberId} actually belong to the same project-member
      * relationship. It assumes the correctness of the input parameters, so the caller must
      * ensure that the {@code memberId} corresponds to a valid member of the specified
      * {@code projectId} before invoking this method.</p>
      *
      * <p>Throws an exception if {@code month} is provided without {@code year}.
      *
      * @param projectId the ID of the project whose attendance records are being queried; must not be null.
      * @param memberId the ID of the member whose attendance records are being queried; must not be null.
      * @param year the year for which attendance records are being queried (optional).
      * @param month the month for which attendance records are being queried (optional). If provided, {@code year} must also be specified.
      * @return a list of {@link DailyAttendance} records matching the specified filters.
      * @throws ConflictException if {@code month} is provided without {@code year}.
      */
     List<DailyAttendance> getAllByProjectIdAndMemberId(Long projectId,Long memberId, Integer year,Integer month);

     /**
      * Retrieves a list of aggregated daily attendance records for a specific project.
      * If a specific date is provided, it filters by the project ID, date, and the aggregated status being true.
      * If no date is provided, it filters only by the project ID and the aggregated status being true.
      *
      * <p>
      * <strong>Important:</strong> This method does not verify whether the provided {@code projectId}
      * actually exists or is valid. It assumes the correctness of the input parameter, so the caller must
      * ensure that the given {@code projectId} corresponds to a valid project before invoking this method.
      * </p>
      *
      *
      * @param projectId the ID of the project for which daily attendance records are being retrieved
      * @param date (optional) the specific date to filter the records by, can be null
      * @return a list of {@link DailyAttendance} objects that match the specified project ID and aggregated status
      */
     List<DailyAttendance> getAllByProjectIdAndIsAggregatedTrue(Long projectId, LocalDateTime date);

     /**
      * Retrieves a list of {@link DailyAttendance} records representing all ongoing (currently working on a project) attendances
      * for a given project. A current attendance is identified when:
      * <ul>
      *     <li>The {@code project_id} matches the provided {@code projectId}.</li>
      *     <li>The {@code endTime} is null, indicating that the attendance session has not ended.</li>
      *     <li>The {@code isAggregated} flag is false, meaning the record has not been aggregated into final attendance reports.</li>
      * </ul>
      *
      * <p><strong>Important:</strong> This method does not verify whether the provided {@code projectId}
      * actually exists or is valid. It assumes the correctness of the input parameter, so the caller must
      * ensure that the given {@code projectId} corresponds to a valid project before invoking this method.</p>
      *
      * <p>Use this method to retrieve a list of members currently marked as present in a specific project.</p>
      *
      * @param projectId The unique identifier of the project for which the current attendance records are being retrieved.
      *                  Must not be null.
      * @return A list of {@link DailyAttendance} instances representing ongoing attendance records for the specified project.
      *         Returns an empty list if no such records exist.
      */
     List<DailyAttendance> getAllCurrentAttendancesByProjectId(Long projectId);


     /**
      * Retrieves a list of {@link DailyAttendance} records representing all absences for a given member
      * within a specific project. An absence is identified when both {@code startTime} and {@code endTime}
      * are null, indicating that the member did not register their attendance for that day.
      *
      * <p>This method queries the {@code daily_attendance} table to find all entries where:
      * <ul>
      *     <li>The {@code project_id} matches the provided {@code projectId}.</li>
      *     <li>The {@code member_id} matches the provided {@code memberId}.</li>
      *     <li>Both {@code startTime} and {@code endTime} are null, signifying an absence.</li>
      * </ul>
      *
      * <p><strong>Important:</strong> This method does not validate whether the provided
      * {@code projectId} and {@code memberId} actually belong to the same project-member
      * relationship. It assumes the correctness of the input parameters, so the caller must
      * ensure that the {@code memberId} corresponds to a valid member of the specified
      * {@code projectId} before invoking this method.</p>
      *
      * <p>Use this method to track attendance compliance for a specific member within a project.</p>
      *
      * @param projectId The unique identifier of the project for which the absence records are being retrieved.
      *                  Must not be null.
      * @param memberId  The unique identifier of the member whose absences are being queried. Must not be null.
      * @return A list of {@link DailyAttendance} instances where the member was absent (i.e., did not register
      *         their attendance). Returns an empty list if no such records exist.
      */
     List<DailyAttendance> getAllAbsencesByProjectIdAndMemberId(Long projectId, Long memberId);


     /**
      * Retrieves a list of {@link DailyAttendance} records representing all absences for a given project.
      * An absence is identified when both {@code startTime} and {@code endTime} are null, indicating that
      * no attendance was recorded for that day.
      *
      * <p>If a specific date is provided, the method filters absences for that date; otherwise, it retrieves
      * all absences across all recorded dates within the project.</p>
      *

      *
      * <p><strong>Important:</strong> This method does not validate whether the provided {@code projectId}
      * actually corresponds to a valid project. It assumes the correctness of the input parameter, so the
      * caller must ensure that the {@code projectId} is valid before invoking this method.</p>
      *
      * <p>Use this method to track project-wide attendance compliance, either for a specific date or for all recorded dates.</p>
      *
      * @param projectId The unique identifier of the project for which absence records are being retrieved. Must not be null.
      * @param date      The specific date for which absences should be retrieved. If null, absences for all dates are returned.
      * @return A list of {@link DailyAttendance} instances where no attendance was recorded for the specified project.
      *         Returns an empty list if no such records exist.
      */
     List<DailyAttendance> getAllAbsencesByProjectId(Long projectId, LocalDate date);


     /**
      * Aggregates the daily attendance records for a given member and project.
      * <p>
      * This method first attempts to retrieve the existing {@link DailyAttendance} record for the specified
      * {@link MemberShip} and {@link Project} on the current day using the {@link #getEntityByMemberIdAndProjectIdAndDateAndIsAggregatedFalse} method.
      * If no such record exists, a new {@link DailyAttendance} entry is created using the {@link #add(MemberShip, Project)} method.
      * If the {@link DailyAttendance} is newly created, it will automatically be marked as aggregated.
      * </p>
      * <p>
      * Once the attendance record is found or created, the method checks if any attendance records exist for the day.
      * If such records are found, the method proceeds to perform the aggregation of attendance data:
      * <ul>
      *     <li>It calculates the total attendance period by determining the first and last attendance times recorded for the day.</li>
      *     <li>It updates the {@link DailyAttendance} period and marks all related {@link CurrentAttendance} records as aggregated.</li>
      * </ul>
      * </p>
      * <p>
      * This method ensures that the {@link DailyAttendance} record is either retrieved or newly created, with the attendance being
      * aggregated only if current attendance records are present.
      * </p>
      *
      * @param member The {@link MemberShip} of the member whose daily attendance is to be aggregated.
      * @param project The {@link Project} to which the member's attendance is associated.
      */
     void aggregateDailyMemberAttendancesOfProject(MemberShip member, Project project);

}
