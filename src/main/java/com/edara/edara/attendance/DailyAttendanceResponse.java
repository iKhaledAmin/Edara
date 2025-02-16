package com.edara.edara.attendance;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyAttendanceResponse {

    @JsonProperty("user_code")
    private String userCode;

    @JsonProperty("user_name")
    private String userName;

    @JsonFormat(pattern="yyyy-MM-dd")
    @JsonProperty("date")
    private Date date;

    @JsonProperty("is_absent")
    private Boolean isAbsent;

    @JsonFormat(pattern="yyyy-MM-dd-hh-mm")
    @JsonProperty("start_time")
    private Date startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd-HH-mm")
    @JsonProperty("end_time")
    private Date endTime;

    @JsonProperty("period")
    private String period;
}
