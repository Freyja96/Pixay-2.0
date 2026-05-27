package es.daw.pixaymvc.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConnectApiRestException.class)
    public String handleConnectApiRestException(ConnectApiRestException e, Model model){
        model.addAttribute("errorMessage", e.getMessage());
        return "pantallas/error";
    }
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception e, Model model) {
        model.addAttribute("errorMessage", "Ha ocurrido un error inesperado: " + e.getMessage());
        return "pantallas/error";
    }
}

