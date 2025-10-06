package com.nitroxen.demo.service;

import com.nitroxen.demo.entity.Report;
import com.nitroxen.demo.entity.Task;
import com.nitroxen.demo.repository.ReportRepository;
import com.nitroxen.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class TaskManagerService {

    private final TaskRepository taskRepository;
    private final ReportRepository reportRepository;

    public TaskManagerService(TaskRepository taskRepository, ReportRepository reportRepository) {
        this.taskRepository = taskRepository;
        this.reportRepository = reportRepository;
    }

    public void assignTask(String title, String description, Long workerId, String deadline, MultipartFile file) {
        // Logic to save task in the database
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setWorkerId(workerId);
        task.setDeadline(deadline);
        if (file != null) {
            // Logic to save file and set file URL
            task.setFileUrl("/path/to/saved/file");
        }
        taskRepository.save(task);
    }

    public List<Task> getTasksByWorkerId(Long workerId) {
        return taskRepository.findByWorkerId(workerId);
    }

    public void submitReport(Long taskId, String reportText, MultipartFile file) {
        // Logic to save report in the database
        Report report = new Report();
        report.setTaskId(taskId);
        report.setReportText(reportText);
        if (file != null) {
            // Logic to save file and set file URL
            report.setFileUrl("/path/to/saved/file");
        }
        reportRepository.save(report);
    }

    public List<Report> getReportsByTaskId(Long taskId) {
        return reportRepository.findByTaskId(taskId);
    }
}
