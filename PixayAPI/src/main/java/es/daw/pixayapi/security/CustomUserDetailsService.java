package es.daw.pixayapi.security;

import es.daw.pixayapi.entity.User;
import es.daw.pixayapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//son servicios, pero especializados en seguridad. Para tenerlos bien diferenciados
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    //    @Autowired
//    public CustomUserDetailsService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Buscando en la BD de la API al usuario: " + username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        System.out.println("Cargando usuario: " + user.getUsername());
        System.out.println("Password en BD: " + user.getPassword());
//        boolean matches = passwordEncoder.matches("admin123", user.getPassword());
//        System.out.println("¿Coincide admin123 con el hash? " + matches);
//        return org.springframework.security.core.userdetails.User.builder()
//                .username(user.getUsername())
//                .password(user.getPassword())
//                .authorities(user.getRole().getName())
//                .build();
        return user;
    }
}