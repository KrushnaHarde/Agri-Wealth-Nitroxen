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
public class PolyhouseDto {
	private Long id;
	private String polyhouseName;
	private Double polyhouseArea;
	private Integer structureLife;
	private Integer plasticLife;
	private String polyhouseType;
	private Double gutterHeight;
	private Double topHeight;
	private Integer acfNumber;
	private Integer exhaustFanNumber;
	private String growingType;
	private List<ZoneDto> zones;
}


