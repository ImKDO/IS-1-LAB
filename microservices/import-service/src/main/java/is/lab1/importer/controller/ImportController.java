package is.lab1.importer.controller;

import is.lab1.importer.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImportController {

    private final ImportService importService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startImport(@RequestBody Map<String, String> request) {
        String queueId = request.get("queueId");
        
        // Run import asynchronously
        CompletableFuture.runAsync(() -> importService.importQueue(queueId));
        
        return ResponseEntity.ok(Map.of(
            "message", "Import started",
            "queueId", queueId
        ));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Import Service is running");
    }
}
