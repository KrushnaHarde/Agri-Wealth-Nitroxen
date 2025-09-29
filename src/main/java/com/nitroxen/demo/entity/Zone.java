package com.nitroxen.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "zones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Zone {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "polyhouse_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Polyhouse polyhouse;
}


