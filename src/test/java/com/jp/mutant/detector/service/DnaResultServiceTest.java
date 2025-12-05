package com.jp.mutant.detector.service;

import com.jp.mutant.detector.dto.DnaRequest;
import com.jp.mutant.detector.model.DnaRaze;
import com.jp.mutant.detector.repository.DnaResultRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DnaResultServiceTest {
    @Autowired
    private DnaService dnaService;

    @MockitoBean
    private DnaResultRepository dnaResultRepository;

    @Test
    @DisplayName("Given mutant dna Should return true")
    void saveMutant(){
        String[] mutantDna = {"ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"};

        DnaRequest dnaRequest = new DnaRequest();
        dnaRequest.dna = mutantDna;

        DnaRaze raze = dnaService.saveDna(dnaRequest);

        assertThat(raze).isSameAs(DnaRaze.MUTANT);

    }

    @Test
    @DisplayName("Given human dna Should return false")
    void saveHuman() {
        String[] humanDna = {"ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG" };

        DnaRequest dnaRequest = new DnaRequest();
        dnaRequest.dna = humanDna;

        DnaRaze raze = dnaService.saveDna(dnaRequest);

        assertThat(raze).isSameAs(DnaRaze.HUMAN);
    }
}