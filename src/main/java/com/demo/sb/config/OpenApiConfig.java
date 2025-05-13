package com.demo.sb.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Course Management API")
                        .version("1.0")
                        .description("API for managing courses with file upload capabilities")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .components(new Components()
                        .addRequestBodies("courseMultipartRequest", createMultipartRequestBody()));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-api")
                .pathsToMatch("/api/**")
                .build();
    }

    private RequestBody createMultipartRequestBody() {
        Map<String, Schema> properties = new HashMap<>();
        
        Schema titleSchema = new Schema<String>()
                .type("string")
                .description("Course title");
                
        Schema descriptionSchema = new Schema<String>()
                .type("string")
                .description("Course description");
                
        Schema priceSchema = new Schema<String>()
                .type("string")
                .description("Course price");
                
        Schema pictureFileSchema = new Schema<String>()
                .type("string")
                .format("binary")
                .description("Course picture file");
                
        Schema categoryIdsSchema = new Schema<String>()
                .type("array")
                .items(new Schema<Integer>().type("integer"))
                .description("List of category IDs");
                
        Schema tagsSchema = new Schema<String>()
                .type("array")
                .items(new Schema<String>().type("string"))
                .description("List of tags");
                
        properties.put("title", titleSchema);
        properties.put("description", descriptionSchema);
        properties.put("price", priceSchema);
        properties.put("pictureFile", pictureFileSchema);
        properties.put("categoryIds", categoryIdsSchema);
        properties.put("tags", tagsSchema);
        
        Schema schema = new Schema<Object>()
                .type("object")
                .properties(properties);
                
        MediaType mediaType = new MediaType()
                .schema(schema);
                
        Content content = new Content()
                .addMediaType("multipart/form-data", mediaType);
                
        return new RequestBody()
                .content(content)
                .description("Course multipart form data");
    }
} 