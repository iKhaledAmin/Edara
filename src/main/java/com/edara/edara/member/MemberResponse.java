package com.edara.edara.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {

    @JsonProperty("member_id")
    private Long id;

    @JsonProperty("member_code")
    private String memberCode;

    @JsonProperty("member_name")
    private String memberName;

    @JsonProperty("member_image")
    private byte[] memberImage;

    @JsonProperty("member_role")
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @JsonProperty("member_title")
    private String title;

    @JsonProperty("member_type")
    @Enumerated(EnumType.STRING)
    private MemberType memberType;


    @JsonFormat(pattern="yyyy-MM-dd")
    @JsonProperty("join_date")
    private LocalDate joinDate;


    @JsonProperty("employee_details")
    private EmployeeResponse employeeDetails;


}
