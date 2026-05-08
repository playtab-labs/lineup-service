package com.playtab.lineupservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Entity
@Table(name = "stages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "name", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "location_desc", columnDefinition = "jsonb")
    private Map<String, String> locationDesc;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Builder
    public Stage(Map<String, String> name, Map<String, String> locationDesc, Integer displayOrder) {
        this.name = name;
        this.locationDesc = locationDesc;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }

    public void update(Map<String, String> name, Map<String, String> locationDesc, Integer displayOrder) {
        this.name = name;
        this.locationDesc = locationDesc;
        this.displayOrder = displayOrder != null ? displayOrder : 0;
    }
}