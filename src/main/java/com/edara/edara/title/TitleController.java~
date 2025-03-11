package com.edara.edara.title;

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

    @PostMapping("/add/{projectId}")
    public ResponseEntity<?> add(@RequestBody @Valid TitleRequest titleRequest, @PathVariable Long projectId) {
        Title title = titleService.add(titleRequest,projectId);
        return new ResponseEntity<>(titleService.toResponse(title), HttpStatus.CREATED);
    }

    @PutMapping("/update/{titleId}")
    public ResponseEntity<?> update(@RequestBody @Valid TitleRequest titleRequest , @PathVariable Long titleId) {
        Title title = titleService.update(titleId,titleRequest);
        return new ResponseEntity<>(titleService.toResponse(title), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete/{titleId}")
    public ResponseEntity<?> delete(@PathVariable Long titleId) {
        titleService.delete(titleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/get/{titleId}")
    public ResponseEntity<?> getById(@PathVariable Long titleId) {
        return new ResponseEntity<>(titleService.getResponseById(titleId), HttpStatus.OK);
    }

    @GetMapping("/get-all-of-project/{projectId}")
    ResponseEntity<?> getAllTitlesByProjectId( @PathVariable Long projectId){
        return new ResponseEntity<>(this.titleService.getResponseAllByProjectId(projectId), HttpStatus.OK);
    }



}
