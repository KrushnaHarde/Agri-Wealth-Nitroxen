package com.nitroxen.demo.controller;

import com.nitroxen.demo.entity.Report;
import com.nitroxen.demo.entity.Task;
import com.nitroxen.demo.service.TaskManagerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/manager/task-manager")
public class TaskManagerController {

    private final TaskManagerService taskManagerService;

    public TaskManagerController(TaskManagerService taskManagerService) {
        this.taskManagerService = taskManagerService;
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignTask(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam Long workerId,
            @RequestParam(required = false) String deadline,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        taskManagerService.assignTask(title, description, workerId, deadline, file);
        return ResponseEntity.ok("Task assigned successfully");
    }

    @GetMapping("/worker/{workerId}")
    public ResponseEntity<List<Task>> getTasks(@PathVariable Long workerId) {
        List<Task> tasks = taskManagerService.getTasksByWorkerId(workerId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/report")
    public ResponseEntity<String> submitReport(
            @RequestParam Long taskId,
            @RequestParam String reportText,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        taskManagerService.submitReport(taskId, reportText, file);
        return ResponseEntity.ok("Report submitted successfully");
    }

    @GetMapping("/report/{taskId}")
    public ResponseEntity<List<Report>> getReports(@PathVariable Long taskId) {
        List<Report> reports = taskManagerService.getReportsByTaskId(taskId);
        return ResponseEntity.ok(reports);
    }
}
