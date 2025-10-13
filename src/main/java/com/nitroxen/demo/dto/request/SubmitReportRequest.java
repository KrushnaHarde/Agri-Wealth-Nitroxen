package com.nitroxen.demo.dto.request;

import lombok.Data;

@Data
public class SubmitReportRequest {
    private Long taskId;
    private String reportText;
}
