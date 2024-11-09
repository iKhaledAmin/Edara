package com.edara.edara.controller;

import com.edara.edara.model.dto.TitleRequest;
import com.edara.edara.service.TitleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/titles")
@AllArgsConstructor
public class TitleController {
    private final TitleService titleService;
    @GetMapping("/get-by-id/{titleId}")
    public ResponseEntity<?> getById(@PathVariable Long titleId) {
        return new ResponseEntity<>(titleService.getResponseById(titleId), HttpStatus.OK);
    }

    @PutMapping("/update/{titleId}")
    public ResponseEntity<?> updateTitle(@RequestBody @Valid TitleRequest titleRequest , @PathVariable Long titleId) {
        return new ResponseEntity<>(titleService.update(titleId,titleRequest), HttpStatus.ACCEPTED);
    }
}
