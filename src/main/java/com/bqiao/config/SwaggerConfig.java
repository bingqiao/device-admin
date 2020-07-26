package com.bqiao.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.function.Predicate;

import static springfox.documentation.builders.PathSelectors.*;

@EnableSwagger2 //Enable swagger 2.0 spec
@EnableOpenApi //Enable open api 3.0.3 spec
@Configuration
public class SwaggerConfig implements WebMvcConfigurer {
    private final String baseUrl;

    public SwaggerConfig(
            @Value("${springfox.documentation.swagger-ui.base-url:}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String baseUrl = StringUtils.trimTrailingCharacter(this.baseUrl, '/');
        registry.
                addResourceHandler(baseUrl + "/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .resourceChain(false);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController(baseUrl + "/swagger-ui/")
                .setViewName("forward:" + baseUrl + "/swagger-ui/index.html");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/api/v1")
                .allowedOrigins("http://editor.swagger.io");
        registry
                .addMapping("/v2/api-docs.*")
                .allowedOrigins("http://editor.swagger.io");
    }

    @Bean
    public Docket petApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("full-device-admin-api")
                .apiInfo(apiInfo())
                .select()
                .paths(paths())
                .build();
    }

    @Bean
    public Docket openApiPetStore() {
        return new Docket(DocumentationType.OAS_30)
                .groupName("open-api-device-admin")
                .select()
                .paths(paths())
                .build();
    }

    private Predicate<String> paths() {
        return regex(".*/api/v1/device.*")
                .or(regex(".*/api/v1/search.*")
                        .or(regex(".*/api/v1/importexport.*")));
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Device-Admin API")
                .description("Device-Admin API Swagger Documentation.")
                .termsOfServiceUrl("http://springfox.io")
                .contact(new Contact("springfox", "", "bqiaodev@gmail.com"))
                .license("Apache License Version 2.0")
                .licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
                .version("2.0")
                .build();
    }
}
