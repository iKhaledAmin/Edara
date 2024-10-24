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

    @PutMapping("/update/{taskId}")
    public ResponseEntity<?> updateProject(@RequestBody @Valid TaskRequest taskRequest, @PathVariable Long taskId) {
        return new ResponseEntity<>(taskService.update(taskId,taskRequest), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete/{taskId}")
    public ResponseEntity<?> deleteById(@PathVariable Long taskId) {
        taskService.delete(taskId);
        return new ResponseEntity<>("Deleted Successfully", HttpStatus.ACCEPTED);
    }
}
