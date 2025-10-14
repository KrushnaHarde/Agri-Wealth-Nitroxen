package com.nitroxen.demo.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneDto {
	private Long id;
	private String zoneName;
	private String systemType;
	private Double zoneArea;
	private Double cultivationArea;
	private String cropInfo;
	private Integer rowCount;
	private Double rowSpacing;
	private Double plantSpacing;
	private Integer plantsPerRow;
	private String nutrientDosing;
	private String weatherType;
	private String customWeatherType;
}


