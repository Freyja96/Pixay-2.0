package es.daw.pixaymvc.controlador;

import es.daw.pixaymvc.dto.RegisterRequest;
import es.daw.pixaymvc.dto.UserProfileUpdateRequest;
import es.daw.pixaymvc.dto.response.UserProfileResponse; // Asegúrate de importar tu Record
import es.daw.pixaymvc.service.ApiAuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

@Controller
public class UserController {
    private final ApiAuthService apiAuthService;
    private final WebClient webClientAPI; // Añadimos WebClient

    public UserController(ApiAuthService apiAuthService, WebClient webClientAPI) {
        this.apiAuthService = apiAuthService;
        this.webClientAPI = webClientAPI;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "pantallas/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session,
                                Model model) {
        try {
            String token = apiAuthService.login(username, password);

            if (token != null && !token.isEmpty()) {
                session.setAttribute("token", token);
                session.setAttribute("username", username);

                UserProfileResponse profile = webClientAPI.get()
                        .uri("usuarios/me")
                        .headers(h -> h.setBearerAuth(token))
                        .retrieve()
                        .bodyToMono(UserProfileResponse.class)
                        .block();

                if (profile != null) {
                    session.setAttribute("usuarioIdLogueado", profile.id());
                    System.out.println("Login MVC OK. ID guardado en sesión: " + profile.id());
                }

                return "redirect:/";
            } else {
                model.addAttribute("error", "Credenciales incorrectas");
                return "pantallas/login";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
            return "pantallas/login";
        }
    }

    // EDITAR PERFIL ----------------------------------------------------------------------------------------
    @GetMapping("/mi-perfil")
    public String redirigirMiPerfil() {
        return "redirect:/mi-perfil/mis-imagenes";
    }

    @GetMapping("/mi-perfil/editar-perfil")
    public String editarPerfil(HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        try {

            UserProfileResponse profile = webClientAPI.get()
                    .uri("usuarios/me")
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(UserProfileResponse.class)
                    .block();


            model.addAttribute("usuario", profile);
            session.setAttribute("avatar", profile.getFotoDataUrl());
            System.out.println("DEBUG: Profile loaded, returning template");


        } catch (WebClientResponseException e) {
            System.out.println("DEBUG: Exception caught: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/login";
        }

        return "pantallas/mi-perfil/editar-perfil";
    }
    @PostMapping("/mi-perfil/editar-perfil")
    public String guardarCambios(HttpSession session,
                                 Model model,
                                 @RequestParam String email,
                                 @RequestParam(required = false) String description,
                                 @RequestParam(required = false) String password,
                                 @RequestParam(required = false) String password2,
                                 @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
                                 RedirectAttributes redirectAttributes) {

        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        UserProfileUpdateRequest updateRequest = new UserProfileUpdateRequest();
        updateRequest.setEmail(email);
        updateRequest.setDescription(description);

        if (profileImage != null && !profileImage.isEmpty()) {
            final long maxSizeBytes = 50L * 1024 * 1024;
            Set<String> allowedTypes = Set.of("image/png", "image/jpeg", "image/jpg", "image/webp");

            String contentType = profileImage.getContentType() == null ? "" : profileImage.getContentType().toLowerCase();
            if (!allowedTypes.contains(contentType)) {
                redirectAttributes.addFlashAttribute("error", "Formato no permitido. Usa PNG, JPG, JPEG o WEBP.");
                return "redirect:/mi-perfil/editar-perfil";
            }
            if (profileImage.getSize() > maxSizeBytes) {
                redirectAttributes.addFlashAttribute("error", "La imagen excede el tamaño máximo permitido (50MB).");
                return "redirect:/mi-perfil/editar-perfil";
            }
            try {
                updateRequest.setProfilePicture(profileImage.getBytes());
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "No se pudo leer la imagen seleccionada.");
                return "redirect:/mi-perfil/editar-perfil";
            }
        }

        boolean wantsToChangePassword = password != null && !password.isBlank();

        if (wantsToChangePassword) {
            if (password2 == null || password2.isEmpty() || password2.isBlank()) {
                redirectAttributes.addFlashAttribute("error", "Debes repetir la contraseña");
            }

            if (!password.equals(password2)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/mi-perfil/editar-perfil";
            }
            // Si todo esta bien actualizar la contraseña

            updateRequest.setPassword(password);
            redirectAttributes.addFlashAttribute("message", "Contraseña actualizada con éxito");
        }

        try {

            webClientAPI.patch()
                    .uri("usuarios/me")
                    .headers(h -> h.setBearerAuth(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(updateRequest)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            UserProfileResponse profile = webClientAPI.get()
                    .uri("usuarios/me")
                    .headers(h -> h.setBearerAuth(token))
                    .retrieve()
                    .bodyToMono(UserProfileResponse.class)
                    .block();
            if (profile != null) {
                session.setAttribute("avatar", profile.getFotoDataUrl());
            }

            System.out.println("DEBUG: email=" + updateRequest.getEmail() + ", description=" + updateRequest.getDescription() + ", password=" + updateRequest.getPassword());

        } catch (WebClientResponseException e) {
            System.out.println("DEBUG: API error: " + e.getStatusCode() +e.getMessage()
                    + e.getResponseBodyAsString() );
            redirectAttributes.addFlashAttribute("error", "Error al guardar los cambios");
            return "redirect:/mi-perfil/editar-perfil";
        }

        redirectAttributes.addFlashAttribute("message", "Perfil actualizado con éxito");
        return "redirect:/mi-perfil/mis-imagenes";
    }

    // REGISTRO ---------------------------------------------------------------------------------------------
    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("registro", new RegisterRequest("", "", ""));
        return "pantallas/registro";
    }
    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("registro") RegisterRequest regRequest,
                                   BindingResult result,
                                   @RequestParam String repassword,
                                   Model model) {

        // Creamos una lista para acumular todos los errores
        List<String> listaErrores = new java.util.ArrayList<>();


        if (result.hasErrors()) {
            result.getFieldErrors().forEach(f -> listaErrores.add(f.getDefaultMessage()));
        }


        if (!regRequest.password().equals(repassword)) {
            listaErrores.add("Las contraseñas no coinciden");
        }


        if (!listaErrores.isEmpty()) {
            model.addAttribute("listaErrores", listaErrores);
            return "pantallas/registro";
        }

        try {
            apiAuthService.registro(regRequest.username(), regRequest.email(), regRequest.password());
            return "redirect:/login?registrado=true";
        } catch (Exception e) {
            model.addAttribute("listaErrores", List.of("El usuario o email ya existen"));
            return "pantallas/registro";
        }
    }

    @PostMapping("/usuario/{id}/seguir")
    public String seguirUsuario(@PathVariable Long id, HttpSession session) {
        String token = (String) session.getAttribute("token");
        if (token == null) return "redirect:/login";

        webClientAPI.post()
                .uri("usuarios/" + id + "/seguir")
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .toBodilessEntity()
                .block();

        return "redirect:/usuario/" + id;
    }
}