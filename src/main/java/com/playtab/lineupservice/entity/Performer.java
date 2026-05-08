package com.playtab.lineupservice.entity;

import com.playtab.lineupservice.converter.LocalizedTextConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Entity
@Table(name = "performers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Performer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = LocalizedTextConverter.class)
    @Column(name = "name", nullable = false, columnDefinition = "jsonb")
    private Map<String, String> name;

    @Convert(converter = LocalizedTextConverter.class)
    @Column(name = "description", columnDefinition = "jsonb")
    private Map<String, String> description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Builder
    public Performer(Map<String, String> name, Map<String, String> description, String imageUrl, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isActive = isActive != null ? isActive : true;
    }

    public void update(Map<String, String> name, Map<String, String> description,
                       String imageUrl, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isActive = isActive != null ? isActive : true;
    }
}