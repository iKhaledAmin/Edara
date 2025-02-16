package com.edara.edara.task;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody @Valid TaskRequest taskRequest) {
        return new ResponseEntity<>(taskService.addTaskToProject(taskRequest), HttpStatus.CREATED);
    }
    @PutMapping("/update/{taskId}")
    public ResponseEntity<?> update(@RequestBody @Valid TaskRequest taskRequest, @PathVariable Long taskId) {
        return new ResponseEntity<>(taskService.update(taskId,taskRequest), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<?> delete(@PathVariable Long taskId) {
        taskService.delete(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/get/{taskId}")
    public ResponseEntity<?> getById(@PathVariable Long taskId) {
        return new ResponseEntity<>(this.taskService.getResponseById(taskId),HttpStatus.OK);
    }

    @PutMapping("/finish/{taskId}")
    public ResponseEntity<?> finish( @PathVariable Long taskId) {
        return new ResponseEntity<>(taskService.finishTask(taskId), HttpStatus.ACCEPTED);
    }
    @PutMapping("/assign-to-member/{taskId}/{userCode}")
    public ResponseEntity<?> assignToMember(@PathVariable Long taskId, @PathVariable String userCode) {
        return new ResponseEntity<>( taskService.assignTaskToMember(taskId,userCode), HttpStatus.ACCEPTED);
    }

    @GetMapping("/get-all-of-member/{userCode}")
    ResponseEntity<?> getAllOfMember( @PathVariable String userCode){
        return new ResponseEntity<>(taskService.getResponseAllByUserCode(userCode), HttpStatus.OK);
    }
    @GetMapping("/get-all-of-project/{projectId}")
    ResponseEntity<?> getAllOfProject( @PathVariable Long projectId){
        return new ResponseEntity<>(taskService.getResponseAllByProjectId(projectId), HttpStatus.OK);
    }

    @GetMapping("/get-all-of-member-in-project/{userCode}/{projectId}")
    ResponseEntity<?> getAllOfMemberInProject( @PathVariable String userCode,Long projectId){
        return new ResponseEntity<>(taskService.getResponseAllByUserCodeAndProjectId(userCode,projectId), HttpStatus.OK);
    }

}
