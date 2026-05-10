package com.inuni.ideathon.global.mock;

import com.inuni.ideathon.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockController {

    @GetMapping("/")
    public ResponseEntity<ApiResponse<String>> root() {
        return ResponseEntity.ok(ApiResponse.success("Ideathon server is running"));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("OK"));
    }
}
