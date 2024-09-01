package com.edara.edara.controller;

import com.edara.edara.model.dto.ProjectRequest;
import com.edara.edara.service.ProjectService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/projects")
@AllArgsConstructor
public class ProjectController {
    private final ProjectService projectService;


    @PostMapping("/add")
    public ResponseEntity<?> addProject(@RequestBody @Valid ProjectRequest projectRequest) {
        return new ResponseEntity<>(projectService.add(projectRequest), HttpStatus.CREATED);
    }

    @PutMapping("/update/{projectId}")
    public ResponseEntity<?> updateProject(@RequestBody @Valid ProjectRequest projectRequest, @PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.update(projectId,projectRequest), HttpStatus.ACCEPTED);
    }

    @GetMapping("/get-by-id/{projectId}")
    public ResponseEntity<?> getManagerById(@PathVariable Long projectId){
        return new ResponseEntity<>(this.projectService.getResponseById(projectId),HttpStatus.OK);
    }

    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<?> deleteById(@PathVariable Long projectId) {
        projectService.delete(projectId);
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.ACCEPTED);
    }
}
