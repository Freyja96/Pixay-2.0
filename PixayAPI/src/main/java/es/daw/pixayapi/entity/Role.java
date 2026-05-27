package es.daw.pixayapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    // Relación bidireccional. Es el lado inverso (mapped)
    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private Set<User> users;

    public Role() {
        users = new HashSet<User>();
    }

    // BIDIRECCIONAL ----------
    public void addUser(User user) {
        users.add(user);
    }
    public void removeUser(User user) {
        users.remove(user);
    }


    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}