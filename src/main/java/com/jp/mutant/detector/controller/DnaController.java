package com.jp.mutant.detector.controller;

import com.jp.mutant.detector.controller.dto.DnaRequest;
import com.jp.mutant.detector.model.DnaRaze;
import com.jp.mutant.detector.service.DnaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mutants")
public class DnaController {

    private final DnaService dnaService;
    public DnaController(DnaService dnaService){
        this.dnaService = dnaService;
    }

    @PostMapping
    public ResponseEntity<?> saveDna(@Valid @RequestBody DnaRequest dnaRequest) {
        DnaRaze result = dnaService.saveDna(dnaRequest);
        if (result == DnaRaze.HUMAN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.noContent().build();
    }
}
