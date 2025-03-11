package com.edara.edara.project;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
@AllArgsConstructor
public class ProjectController {
    private final ProjectService projectService;


    @PostMapping("/add/{userCode}")
    public ResponseEntity<?> add(@RequestBody @Valid ProjectRequest projectRequest, @PathVariable String userCode) {
        Project newProject = projectService.add(userCode,projectRequest);
        return new ResponseEntity<>(projectService.toResponse(newProject), HttpStatus.CREATED);
    }
    @PutMapping("/update/{projectId}")
    public ResponseEntity<?> update(@RequestBody @Valid ProjectRequest projectRequest, @PathVariable Long projectId) {
        Project updatedProject = projectService.update(projectId,projectRequest);
        return new ResponseEntity<>(projectService.toResponse(updatedProject), HttpStatus.ACCEPTED);
    }
    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<?> delete(@PathVariable Long projectId) {
        projectService.delete(projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/get/{projectId}")
    public ResponseEntity<?> getById(@PathVariable Long projectId){
        return new ResponseEntity<>(this.projectService.getResponseById(projectId),HttpStatus.OK);
    }


}
