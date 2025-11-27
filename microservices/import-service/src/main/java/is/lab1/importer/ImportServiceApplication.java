package is.lab1.importer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ImportServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ImportServiceApplication.class, args);
    }

    @Bean
    @Beanasda
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
