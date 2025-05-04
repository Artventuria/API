package com.artventuria.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VersionController {

    @Value("${spring.application.version}")
    private String version;

    @GetMapping("/version")
    public ResponseEntity<Map<String, String>> getVersion() {
        Map<String, String> versionInfo = new HashMap<>();
        versionInfo.put("version", version);
        versionInfo.put("timestamp", String.valueOf(System.currentTimeMillis()));

        return ResponseEntity
                .ok()
                .header("X-API-Version", version)
                .body(versionInfo);
    }
}