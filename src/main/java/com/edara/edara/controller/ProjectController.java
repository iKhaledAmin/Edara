package com.edara.edara.controller;

import com.edara.edara.model.dto.MemberShipRequest;
import com.edara.edara.model.dto.ProjectRequest;
import com.edara.edara.model.dto.TaskRequest;
import com.edara.edara.service.ProjectService;
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

    @PostMapping("/add-user")
    public ResponseEntity<?> addUserToProject(@RequestBody @Valid MemberShipRequest memberShipRequest) {
        return new ResponseEntity<>(projectService.addUserToProject(memberShipRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete-user/{userId}/{projectId}")
    public ResponseEntity<?> deleteUserFromProject(@PathVariable Long userId, @PathVariable Long projectId) {
        projectService.deleteUserFromProject(userId, projectId);
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.ACCEPTED);
    }

    @GetMapping("/get-all-users/{projectId}")
    ResponseEntity<?> getAllUsersByProjectId( @PathVariable Long projectId){
        return new ResponseEntity<>(this.projectService.getResponseAllByProjectId(projectId), HttpStatus.OK);
    }

    @PostMapping("/add-task/{projectId}")
    public ResponseEntity<?> addTaskToProject(@RequestBody @Valid TaskRequest taskRequest, @PathVariable Long projectId) {
        return new ResponseEntity<>(projectService.addTaskToProject(taskRequest,projectId), HttpStatus.CREATED);
    }

    @GetMapping("/get-all-project-tasks/{projectId}")
    ResponseEntity<?> getAllTasksByProjectId( @PathVariable Long projectId){
        return new ResponseEntity<>(this.projectService.getResponseAllTasksByProjectId(projectId), HttpStatus.OK);
    }

}
