package com.example.springboot_test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {

    /*

    Este es el objeto que se va a configurar en spring boot, se va a registrar en el contenedor
    y va a crear la documentación automática usando spring-fox con swagger

    Si salta este eror y otro como de NullPointer : Failed to start bean 'documentationPluginsBootstrapper' in spring data rest
        Lo resolví con esto: https://stackoverflow.com/questions/40241843/failed-to-start-bean-documentationpluginsbootstrapper-in-spring-data-rest

    Este endpoint devuelve un json con toda la descripción de los endpoints : http://localhost:8080/v2/api-docs

    Y Este endpoint es para verlo con la interfaz grafica: http://localhost:8080/swagger-ui/

    Es como hacer pruebas ent to end, prueba muchas cosas, controler, servicios, etc
     */
   @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select() // me permite seleccionar las rutas
                .apis(RequestHandlerSelectors.basePackage("com.example.springboot_test.controllers")) // son los controladores, en este caso ponemos el package del o los controladores
                .paths(PathSelectors.ant("/api/cuentas/*")).build();
    }

}
