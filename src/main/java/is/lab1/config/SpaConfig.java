package is.lab1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpaConfig implements WebMvcConfigurer {
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Forward all non-API routes to index.html for React Router
        registry.addViewController("/{spring:^(?!api|ws).*$}")
                .setViewName("forward:/index.html");
        registry.addViewController("/**/{spring:^(?!api|ws).*$}")
                .setViewName("forward:/index.html");
    }
}
