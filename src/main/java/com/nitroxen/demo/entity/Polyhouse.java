package com.nitroxen.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "polyhouses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Polyhouse {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "farm_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private Farm farm;

	@OneToMany(mappedBy = "polyhouse", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Zone> zones;
}


