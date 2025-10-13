package com.nitroxen.demo.dto.request;

import lombok.Data;

@Data
public class WhatsAppTaskAssignRequest {
    private Long taskId;
    private Long workerId;
    private String taskName;
    private String description;
    private String deadline;
}
