package es.daw.pixaymvc.security;

import es.daw.pixaymvc.entity.User;
import es.daw.pixaymvc.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//son servicios, pero especializados en seguridad. Para tenerlos bien diferenciados
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

//    @Autowired
//    public CustomUserDetailsService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //cargar el usuario desde la BDD
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return user;
    }
}

