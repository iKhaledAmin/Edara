package com.edara.edara.controller;

import com.edara.edara.model.dto.TaskRequest;
import com.edara.edara.service.TaskService;
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

    @GetMapping("/get-by-id/{taskId}")
    public ResponseEntity<?> getManagerById(@PathVariable Long taskId) {
        return new ResponseEntity<>(this.taskService.getResponseById(taskId),HttpStatus.OK);
    }
    @PutMapping("/update/{taskId}")
    public ResponseEntity<?> updateTask(@RequestBody @Valid TaskRequest taskRequest, @PathVariable Long taskId) {
        return new ResponseEntity<>(taskService.update(taskId,taskRequest), HttpStatus.ACCEPTED);
    }

    @PutMapping("/finish-task/{taskId}")
    public ResponseEntity<?> finishTask( @PathVariable Long taskId) {
        return new ResponseEntity<>(taskService.finishTask(taskId), HttpStatus.ACCEPTED);
    }

}
