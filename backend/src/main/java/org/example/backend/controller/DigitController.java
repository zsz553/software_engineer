package org.example.backend.controller;

import org.example.backend.dto.DigitPredictionResponse;
import org.example.backend.service.DigitRecognitionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/digit")
@CrossOrigin(origins = "http://localhost:5173")
public class DigitController {

    private final DigitRecognitionService digitRecognitionService;

    public DigitController(DigitRecognitionService digitRecognitionService) {
        this.digitRecognitionService = digitRecognitionService;
    }

    @PostMapping("/predict")
    public DigitPredictionResponse predict(@RequestParam("file") MultipartFile file) {
        return digitRecognitionService.predict(file);
    }
}