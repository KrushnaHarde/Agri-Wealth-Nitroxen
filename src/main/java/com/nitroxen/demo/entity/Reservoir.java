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
@Table(name = "reservoirs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservoir {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String reservoirName;
	private Double capacity;
	private String reservoirType;
	private String customReservoirType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "farm_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Farm farm;
}


