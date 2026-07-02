package org.example.backend.service;

import org.example.backend.dto.DigitPredictionResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class DigitRecognitionService {

    public DigitPredictionResponse predict(MultipartFile file) {
        try {
            Path currentDir = Path.of(System.getProperty("user.dir"));
            Path pythonModelDir;
            if (Files.exists(currentDir.resolve("python_model"))) {
                pythonModelDir = currentDir.resolve("python_model");
            } else {
                pythonModelDir = currentDir.getParent().resolve("python_model");

            }
            Path uploadDir = pythonModelDir.resolve("uploads");

            Files.createDirectories(uploadDir);

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path imagePath = uploadDir.resolve(fileName);
            file.transferTo(imagePath.toFile());

            String pythonExecutable = System.getenv().getOrDefault("PYTHON_PATH", "python3");

            ProcessBuilder processBuilder = new ProcessBuilder(
                    pythonExecutable,
                    "predict.py",
                    imagePath.toString()
            );

            processBuilder.directory(pythonModelDir.toFile());
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            StringBuilder outputBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            String output = outputBuilder.toString().trim();

            if (exitCode != 0 || output.isEmpty()) {
                throw new RuntimeException("Python prediction failed.\n" + output);
            }

            String[] lines = output.split("\n");
            String result = lines[lines.length - 1];

            String[] parts = result.split(",");

            if (parts.length != 2) {
                throw new RuntimeException("Invalid Python output: " + result);
            }

            int digit = Integer.parseInt(parts[0]);
            double confidence = Double.parseDouble(parts[1]);

            return new DigitPredictionResponse(digit, confidence);

        } catch (Exception e) {
            throw new RuntimeException("Digit recognition failed: " + e.getMessage(), e);
        }
    }
}