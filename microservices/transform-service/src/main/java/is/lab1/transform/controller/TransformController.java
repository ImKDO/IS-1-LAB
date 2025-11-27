package is.lab1.transform.controller;

import is.lab1.transform.dto.CityValidationRequest;
import is.lab1.transform.dto.TransformResponse;
import is.lab1.transform.service.TransformService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transform")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TransformController {

    private final TransformService transformService;

    @PostMapping("/validate")
    public ResponseEntity<TransformResponse> transformCities(
            @RequestBody CityValidationRequest request) {
        
        TransformResponse response = transformService.transformCities(request.getCities());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Transform Service is running");
    }
}
