package com.nitroxen.demo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "farms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Farm {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String farmName;
	private Double farmArea;
	private String location;
	private String farmType;
	private String ioDevices;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private User owner;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "farm_managers",
		joinColumns = @JoinColumn(name = "farm_id"),
		inverseJoinColumns = @JoinColumn(name = "manager_id")
	)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<User> managers;

	@OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Polyhouse> polyhouses;

	@OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private List<Reservoir> reservoirs;

	private Double totalUsedArea;
}


