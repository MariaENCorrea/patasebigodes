
package com.example.patasebigodes.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class UploadConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        Path pastaFotos = Paths.get("uploads/pets")
                .toAbsolutePath()
                .normalize();

        String caminho = pastaFotos.toUri().toString();

        if (!caminho.endsWith("/")) {
            caminho += "/";
        }

        registry.addResourceHandler("/uploads/pets/**")
                .addResourceLocations(caminho);
    }
}
