package com.jp.mutant.detector.service;

import com.jp.mutant.detector.dto.DnaRequest;
import com.jp.mutant.detector.model.DnaRaze;
import com.jp.mutant.detector.model.DnaResult;
import com.jp.mutant.detector.repository.DnaResultRepository;
import org.springframework.stereotype.Service;

@Service
public class DnaService {

    private final DnaResultRepository dnaResultRepository;

    public DnaService(DnaResultRepository dnaResultRepository) {
        this.dnaResultRepository = dnaResultRepository;
    }

    private boolean isMutant(String[] dna){
        int n = dna.length;
        char[][] matrix = new char[n][n];

        // Convertir a matriz NxN
        for (int i = 0; i < n; i++) {
            matrix[i] = dna[i].toCharArray();
        }

        int sequences = 0;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                char letter = matrix[i][j];

                // Horizontal →
                if (j + 3 < n &&
                        letter == matrix[i][j+1] &&
                        letter == matrix[i][j+2] &&
                        letter == matrix[i][j+3]) {
                    sequences++;
                }

                // Vertical ↓
                if (i + 3 < n &&
                        letter == matrix[i+1][j] &&
                        letter == matrix[i+2][j] &&
                        letter == matrix[i+3][j]) {
                    sequences++;
                }

                // Diagonal ↘
                if (i + 3 < n && j + 3 < n &&
                        letter == matrix[i+1][j+1] &&
                        letter == matrix[i+2][j+2] &&
                        letter == matrix[i+3][j+3]) {
                    sequences++;
                }

                // Diagonal ↙
                if (i + 3 < n && j - 3 >= 0 &&
                        letter == matrix[i+1][j-1] &&
                        letter == matrix[i+2][j-2] &&
                        letter == matrix[i+3][j-3]) {
                    sequences++;
                }

                // Si ya hay 2 o más, es mutante
                if (sequences >= 2) {
                    return true;
                }
            }
        }

        return false;
    }
    public DnaRaze saveDna(DnaRequest dnaRequest) {
        boolean dnaIsMutant = this.isMutant(dnaRequest.dna);

        DnaResult dnaResult = new DnaResult();
        dnaResult.setDnaChain(dnaRequest.dna);

        if (!dnaIsMutant) {
            dnaResult.setRaze(DnaRaze.HUMAN);
            dnaResultRepository.save(dnaResult);
            return DnaRaze.HUMAN;
        }

        dnaResult.setRaze(DnaRaze.MUTANT);
        dnaResultRepository.save(dnaResult);
        return DnaRaze.MUTANT;
    }
}
