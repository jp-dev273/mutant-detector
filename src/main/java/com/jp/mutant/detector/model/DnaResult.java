package com.jp.mutant.detector.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
public class DnaResult {
    @Id
    @UuidGenerator
    private UUID id;
    private String[] dnaChain;
    @Enumerated(EnumType.STRING)
    private DnaRaze raze;
}
