package com.edara.edara.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyAttendancesOfUserResponse {

    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_code")
    private String userCode;

    @JsonProperty("user_attendances")
    private List<DailyAttendanceResponse> dailyAttendances = new ArrayList<>();
}
