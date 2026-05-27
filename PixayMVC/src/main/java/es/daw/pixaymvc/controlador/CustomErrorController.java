package es.daw.pixaymvc.controlador;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // Aquí le decimos que use tu archivo en la nueva carpeta
        return "pantallas/error";
    }
}