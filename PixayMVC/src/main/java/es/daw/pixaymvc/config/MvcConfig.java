package es.daw.pixaymvc.config;

import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry){
        //registry.addViewController("/").setViewName("pantallas/inicio"); //MAAAAL, solo si no hay lógica
        //registry.addViewController("/inicio").setViewName("pantallas/inicio");
        //registry.addViewController("/login").setViewName("login"); //MAAAAL, solo si no hay lógica
        registry.addViewController("/registro").setViewName("/pantallas/registro");
        //registry.addViewController("/error").setViewName("pantallas/error");
        //registry.addViewController("/perfil").setViewName("pantallas/mi-perfil/mis-imagenes"); //MAAAAL, solo si no hay lógica

        //TODO no sé si sería así para mirar el perfil de otros usuarios y si no podríamos aprovechar así para el propio
        // usuario también
//        registry.addViewController("{id}/imagen/{id-imagen}").setViewName("imagen/{id-imagen}");
//        registry.addViewController("{id}/mis-imagenes").setViewName("mi-perfil/mis-imagenes");
//        registry.addViewController("{id}/mis-imagenes/nueva-imagen").setViewName("mi-perfil/mis-imagenes/nueva-imagen");
//        registry.addViewController("{id}/editar-perfil").setViewName("mi-perfil/editar-perfil");
//        registry.addViewController("{id}/imagenes-guardadas").setViewName("mi-perfil/imagenes-guardadas");
        //registry.addViewController("/busqueda").setViewName("busqueda");
//        registry.addViewController("/ajustes").setViewName("pantallas/ajustes/ajustes");
        //registry.addViewController("asistencia").setViewName("asistencia");
    }
}