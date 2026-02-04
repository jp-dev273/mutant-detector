package com.jp.mutant.detector.controller;

import com.jp.mutant.detector.controller.dto.DnaRequest;
import com.jp.mutant.detector.model.DnaRaze;
import com.jp.mutant.detector.service.DnaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DnaController.class)
class DnaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DnaService dnaService;

    @Test
    @DisplayName("Should create mutant and Return 200")
    void saveMutant() throws Exception {
        DnaRequest dnaRequest = new DnaRequest();
        dnaRequest.dna = new String[] { "ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG" };

        when(dnaService.saveDna(argThat(
                req -> Arrays.equals(req.dna, dnaRequest.dna)
        ))).thenReturn(DnaRaze.MUTANT);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/mutants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    { "dna": ["ATGCGA","CAGTGC","TTATGT","AGAAGG","CCCCTA","TCACTG"] }
                                """
                        )
        )
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist())
        ;
    }

    @Test
    @DisplayName("Should create human and Return 403")
    void saveHuman() throws Exception {
        DnaRequest dnaRequest = new DnaRequest();
        dnaRequest.dna = new String[] { "ATGCGA", "CAGTGC","TTATTT", "AGACGG", "GCGTCA", "TCACTG" };

        when(dnaService.saveDna(argThat(
                req -> Arrays.equals(req.dna, dnaRequest.dna)
        ))).thenReturn(DnaRaze.HUMAN);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/mutants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                """
                                    { "dna": ["ATGCGA", "CAGTGC","TTATTT", "AGACGG", "GCGTCA", "TCACTG"] }
                                """
                        )
        ).andExpect(status().isForbidden());
    }
}