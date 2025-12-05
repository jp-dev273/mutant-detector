package com.jp.mutant.detector.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class DnaRequest {
    public String[] dna;
}
