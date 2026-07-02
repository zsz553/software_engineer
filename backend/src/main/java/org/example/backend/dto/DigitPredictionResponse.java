package org.example.backend.dto;

public class DigitPredictionResponse {
    private int digit;
    private double confidence;

    public DigitPredictionResponse(int digit, double confidence) {
        this.digit = digit;
        this.confidence = confidence;
    }

    public int getDigit() {
        return digit;
    }

    public double getConfidence() {
        return confidence;
    }
}