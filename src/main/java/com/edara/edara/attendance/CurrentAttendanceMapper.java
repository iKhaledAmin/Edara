package com.edara.edara.attendance;

import com.edara.edara.member.Member;
import com.edara.edara.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Duration;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CurrentAttendanceMapper {

    @Mapping(target = "userName", expression = "java(concatenateUserName(entity.getDailyAttendance().getMember().getUser().getFirstName(), entity.getDailyAttendance().getMember().getUser().getLastName()))")//java(concatenateUserName(entity.getMember().getUser().getFirstName(), entity.getMember().getUser().getLastName()))")
    @Mapping(target = "userCode", source = "entity.dailyAttendance.member.user.userCode")
    @Mapping(target = "period", expression = "java(formatDuration(entity.getPeriod()))")
    CurrentAttendanceResponse toResponse(CurrentAttendance entity);


    DailyAttendanceResponse toResponse(DailyAttendance entity);


    default String concatenateUserName(String firstName, String lastName) {
        if (firstName == null && lastName == null) {
            return null;
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }
    default String formatDuration(Duration duration) {
        if (duration == null) {
            return null;
        }
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

    default DailyAttendancesOfUserResponse toResponse(List<DailyAttendance> dailyAttendances) {
        if (dailyAttendances == null || dailyAttendances.isEmpty()) {
            // Return an empty response if no daily attendances are provided
            return new DailyAttendancesOfUserResponse();
        }

        // Extract user-related data from the first DailyAttendance
        DailyAttendance firstAttendance = dailyAttendances.get(0);
        Member member = firstAttendance.getMember();
        User user = member.getUser();

        Long userId = user.getId();
        String userName = String.format("%s %s",
                user.getFirstName() == null ? "" : user.getFirstName(),
                user.getLastName() == null ? "" : user.getLastName()).trim();
        String userCode = user.getUserCode();

        // Map DailyAttendance to DailyAttendanceResponse
        List<DailyAttendanceResponse> dailyAttendanceResponses = dailyAttendances.stream()
                .map(this::toResponse)
                .toList();

        // Build and return the response
        return new DailyAttendancesOfUserResponse(
                userId,
                userName,
                userCode,
                dailyAttendanceResponses
        );
    }


}





