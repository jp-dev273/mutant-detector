package com.jp.mutant.detector.repository;

import com.jp.mutant.detector.model.DnaResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DnaResultRepository extends JpaRepository<DnaResult, UUID> {

}
