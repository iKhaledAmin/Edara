package com.edara.edara.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TitleResponse {

    private Long id;
    private String name;
    private String description;

}
