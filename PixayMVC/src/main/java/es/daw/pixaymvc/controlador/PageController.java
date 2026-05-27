package es.daw.pixaymvc.controlador;

import es.daw.pixaymvc.dto.SettingsViewModel;
import es.daw.pixaymvc.dto.response.UserProfileResponse;
import es.daw.pixaymvc.service.SettingsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PageController {

    private final SettingsService settingsService;

    public PageController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    // Para mostrar la pagina de ajustes
    @GetMapping("/configuracion")
    public String ajustes(HttpSession session, Model model) {
        System.out.println("DEBUG: ENTRANDO EN AJUSTES");
        Object userIdObj = session.getAttribute("usuarioIdLogueado");
        Long usuarioIdLogueado = userIdObj == null ? null : ((Number) userIdObj).longValue();
        String token = (String) session.getAttribute("token");

        if (token == null || usuarioIdLogueado == null) {
            return "redirect:/login";
        }
        try {
            SettingsViewModel ajustes = settingsService.getSettings(usuarioIdLogueado, token);
            UserProfileResponse usuario = settingsService.getUserSummary(usuarioIdLogueado, token);

            model.addAttribute("usuario", usuario);
            model.addAttribute("ajustes", ajustes);

        } catch (Exception e) {
            model.addAttribute("error", "No se pudieron cargar los ajustes: " + e.getMessage());
            model.addAttribute("error", "Error al cargar los ajustes: " + e.getMessage());
            return "pantallas/ajustes/ajustes";
        }
        return "pantallas/ajustes/ajustes";
    }

    @PostMapping("/ajustes/visibilidad")
    public String actualizarVisibilidad(@RequestParam boolean perfilPrivado,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            settingsService.actualizarVisibilidad(perfilPrivado, token);
            redirectAttributes.addFlashAttribute("message", "Visibilidad de perfil actualizada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudo actualizar la privacidad.");
        }
        return "redirect:/configuracion";
    }

    @PostMapping("/ajustes/permisos")
    public String actualizarPermisos(@RequestParam String campo,
                                     @RequestParam boolean valor,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {
            settingsService.actualizarPermisos(campo, valor, token);
            redirectAttributes.addFlashAttribute("message", "Permisos actualizados correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se pudieron actualizar los permisos.");
        }
        return "redirect:/configuracion";
    }

    @RestController
    @RequestMapping("/test")
    public class TestController {
        @GetMapping("/ajustes")
        public String test() {
            return "FUNCIONA";
        }
    }

}
