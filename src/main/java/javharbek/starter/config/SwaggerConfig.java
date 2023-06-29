package javharbek.starter.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("javharbek.starter.controllers"))
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(List.of(apiKey(), oauthKey()))
                .securityContexts(Collections.singletonList(securityContext())).pathMapping("/")
                .useDefaultResponseMessages(false)
                .apiInfo(metaData());
    }

    private ApiKey apiKey() {
        return new ApiKey("jwtToken", "Authorization", "header");
    }


    private OAuth oauthKey() {

        return new OAuth(
                "oauth2schema",
                new ArrayList<>(),
                Arrays.asList(new ResourceOwnerPasswordCredentialsGrant("/v1/oauth/users/login"))
        );
    }

    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("App - Starter")
                .description(" Starter ")
                .version("1.0.0")
                .license("Digital Team")
                .licenseUrl("https://info@xb.uz\"")
                .contact(new Contact("Javharbek Abdulatipov", "https://xb.uz", "jakharbek@gmail.com"))
                .build();
    }


    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).build();
    }


    private List<SecurityReference> defaultAuth() {
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[2];
        authorizationScopes[0] = new AuthorizationScope("write", "write all");
        authorizationScopes[1] = new AuthorizationScope("read", "read all");
        return Arrays.asList(new SecurityReference("oauth2schema", authorizationScopes));
    }

    @Bean
    public SecurityConfiguration securityInfo() {
        return new SecurityConfiguration("none", "none", "", "", "", ApiKeyVehicle.HEADER, "", " ");
    }


    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")//**address of swagger ui
                .addResourceLocations("classpath:/META-INF/resources/");


        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
