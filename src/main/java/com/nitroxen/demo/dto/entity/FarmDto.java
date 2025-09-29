package com.nitroxen.demo.dto.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FarmDto {
	private Long id;
	private String farmName;
	private Double farmArea;
	private String location;
	private String farmType;
	private Double totalUsedArea;
	private List<PolyhouseDto> polyhouses;
	private List<ReservoirDto> reservoirs;
}


