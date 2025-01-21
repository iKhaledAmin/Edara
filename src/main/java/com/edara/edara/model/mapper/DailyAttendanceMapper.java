package com.edara.edara.model.mapper;

import com.edara.edara.model.dto.DailyAttendanceResponse;
import com.edara.edara.model.entity.DailyAttendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Duration;

@Mapper(componentModel = "spring")
public interface DailyAttendanceMapper {

    @Mapping(target = "userCode", source = "entity.member.user.userCode")
    @Mapping(target = "userName", expression = "java(concatenateUserName(entity.getMember().getUser().getFirstName(), entity.getMember().getUser().getLastName()))")
    @Mapping(target = "isAbsent", expression = "java(isAbsent(entity))")
    @Mapping(target = "period", expression = "java(formatDuration(entity.getPeriod()))")
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
    default Boolean isAbsent(DailyAttendance entity) {
        return entity != null && entity.getStartTime() == null;
    }
    default String formatDuration(Duration duration) {
        if (duration == null) {
            return null;
        }
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        return String.format("%02d:%02d", hours, minutes);
    }

}
