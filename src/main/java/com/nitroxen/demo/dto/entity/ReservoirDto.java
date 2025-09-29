package com.nitroxen.demo.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservoirDto {
	private Long id;
	private String reservoirName;
	private Double capacity;
	private String reservoirType;
	private String customReservoirType;
}


