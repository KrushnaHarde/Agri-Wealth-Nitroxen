package com.nitroxen.demo.dto.request;

import lombok.Data;

@Data
public class AssignTaskRequest {
    private String title;
    private String description;
    private Long workerId;
    private String deadline;
}
