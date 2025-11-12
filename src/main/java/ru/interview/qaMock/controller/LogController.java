package ru.interview.qaMock.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class LogController {

    @GetMapping(value = "/logs")
    public ResponseEntity<String> getLogs(@RequestParam(defaultValue = "200") Integer size) throws IOException {
        Path logPath = Paths.get("logs/app.log");
        if (!Files.exists(logPath)) {
            return ResponseEntity.ok("Log file is empty or not found.");
        }

        List<String> lines = Files.readAllLines(logPath);
        int fromIndex = Math.max(0, lines.size() - size);
        String recentLogs = String.join("\n", lines.subList(fromIndex, lines.size()));
        return ResponseEntity.ok(recentLogs);
    }
}
