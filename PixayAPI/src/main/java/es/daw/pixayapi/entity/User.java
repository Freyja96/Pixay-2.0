package es.daw.pixayapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails {
    //id BIGINT AUTO_INCREMENT PRIMARY KEY,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //username VARCHAR(50) NOT NULL UNIQUE,
    @Column(unique = true, nullable = false, length = 30)
    private String username;

    //password VARCHAR(255) NOT NULL,
    @Column(nullable = false, length = 255)
    private String password;

    //email VARCHAR(100) NOT NULL,
    @Column(nullable = false, length = 100)
    private String email;

    //profile_picture LONGBLOB,
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] profilePicture;

    //description VARCHAR(255),
    @Column(length = 255, name = "description")
    private String description;

    @Column(nullable = false)
    private Boolean allowDownloads = true;

    @Column(nullable = false)
    private String commentsPrivacy = "PUBLIC";

    @Column(name = "image_visibility", length = 20)
    private String imageVisibility = "PUBLIC";

    //role_id BIGINT NOT NULL,
    // Relación bidireccional.
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    // CAMPOS PARA AJUSTES Y PERMISOS

    @Column(nullable = false)
    private boolean publicProfile = true;


    // RELACIONES PARA SEGUIDORES Y SEGUIDOS
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> following;

    // Relación: otros usuarios siguen a este usuario
    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Follow> followers;


    // --------------------- 5 MÉTODOS DE LA INTERFACE UserDetails -----------------
    // Devuelve los roles convertidos en objetos GrantedAuthority
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) return List.of(); //evita null pointer exception

        String roleName = role.getName().toUpperCase();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    /**
     * Indica si la cuenta del usuario ha expirado.
     * Devuelve true si la cuenta no ha expirado.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
        // // Devuelve true si la fecha actual es anterior o igual a la de expiración
        //    return !LocalDate.now().isAfter(accountExpirationDate);
    }

    /**
     * Indica si la cuenta del usuario está bloqueada.
     * Devuelve true si la cuenta no está bloqueada.
     */
    @Override
    public boolean isAccountNonLocked() { return true; }

    /**
     * Indica si las credenciales del usuario (contraseña) han expirado.
     * Devuelve true si las credenciales no han expirado.
     */
    @Override
    public boolean isCredentialsNonExpired() { return true; }

    /**
     * Indica si la cuenta del usuario está habilitada.
     * Devuelve true si la cuenta está activa.
     */
    @Override
    public boolean isEnabled() { return true; }

}