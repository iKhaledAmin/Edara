package com.edara.edara.attendance;

import com.edara.edara.global.utils.NonNullBeanUtils;
import com.edara.edara.global.utils.Utilts;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CurrentCurrentAttendanceServiceImpl implements CurrentAttendanceService {
    private final CurrentAttendanceRepo currentAttendanceRepo;
    private final CurrentAttendanceMapper currentAttendanceMapper;
    private final NonNullBeanUtils nonNullBeanUtils;


    @Override
    public CurrentAttendanceResponse toResponse(CurrentAttendance entity) {
        return currentAttendanceMapper.toResponse(entity);
    }

    @Override
    public CurrentAttendance create() {

        CurrentAttendance currentAttendance = new CurrentAttendance();
        currentAttendance.setStartTime(LocalDateTime.now());
        currentAttendance.setEndTime(null);
        currentAttendance.setPeriod(null);
        currentAttendance.setIsAggregated(false);

        return currentAttendance;
    }

    @Override
    public CurrentAttendance save(CurrentAttendance currentAttendance) {
        return currentAttendanceRepo.save(currentAttendance);
    }

    @Override
    public CurrentAttendance recordCurrentAttendance( DailyAttendance dailyAttendance) {
        CurrentAttendance currentAttendance = create();

        currentAttendance.setDailyAttendance(dailyAttendance);
        dailyAttendance.getCurrentAttendances().add(currentAttendance);

        return currentAttendance;
    }

    @SneakyThrows
    private CurrentAttendance update(Long attendanceId, CurrentAttendance newCurrentAttendance) {
        Optional<CurrentAttendance> existedAttendance = getEntityById(attendanceId);
        if(existedAttendance.isPresent()) {
            // Copy properties from newCurrentAttendance to existedAttendance, excluding the "id", "startTime"
            nonNullBeanUtils.copyProperties(newCurrentAttendance, existedAttendance, "id", "startTime");
        }
        return currentAttendanceRepo.save(existedAttendance.get());
    }

    @Override
    public Optional<CurrentAttendance> getEntityById(Long id) {
        return currentAttendanceRepo.findById(id);
    }

    @Override
    public CurrentAttendance endCurrentAttendance(CurrentAttendance currentAttendance) {
        currentAttendance.setEndTime(LocalDateTime.now());
        currentAttendance.setPeriod(
                Utilts.calculatePeriod(
                        currentAttendance.getStartTime(),
                        currentAttendance.getEndTime()
                )
        );
        return currentAttendance;
    }

    @Override
    public Optional<CurrentAttendance> getOnGoingAttendanceByMemberAndProject(Long memberId, Long projectId) {
        return currentAttendanceRepo.findByDailyAttendanceMemberIdAndDailyAttendanceProjectIdAndEndTimeIsNull(memberId, projectId);
    }







}
