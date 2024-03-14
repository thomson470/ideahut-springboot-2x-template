package net.ideahut.springboot.template.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import net.ideahut.springboot.annotation.Audit;
import net.ideahut.springboot.entity.EntityAudit;

@Audit
@Entity
@Table(name = "long_id_join_embedded")
@Setter
@Getter
@SuppressWarnings("serial")
public class LongIdJoinEmbedded extends EntityAudit {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	
	@Column(name = "name", nullable = false, length = 128)
	private String name;
	
	@Column(name = "description")
	private String description;

	@Column(name = "is_active", nullable = false, length = 1)
	private Character isActive;
	
	@ManyToOne(fetch = FetchType.LAZY)
	//@JoinColumn(name="here_type", referencedColumnName="type")
    //@JoinColumn(name="here_code", referencedColumnName="code")
	private EmbeddedHardDel embedded;
	
	public LongIdJoinEmbedded() {}
	
	public LongIdJoinEmbedded(Long id) {
		this.id = id;
	}
	
}
