package com.edara.edara.attendance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentAttendanceResponse {

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_code")
    private String userCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd-hh-mm")
    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd-HH-mm")
    @JsonProperty("end_time")
    private LocalDateTime endTime;

    @JsonProperty("period")
    private String period;
}
