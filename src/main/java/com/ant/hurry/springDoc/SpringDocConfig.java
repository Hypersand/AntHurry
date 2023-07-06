package com.ant.hurry.springDoc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "나 급해요 API", version = "v1"))
public class SpringDocConfig {
}
