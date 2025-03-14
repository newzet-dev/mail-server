package com.newzet.api.health;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/functions/v1/health")
public class HealthController {
	@GetMapping("/health")
	public ResponseEntity<Object> healthCheck() {
		return ResponseEntity.ok().build();
	}
}
