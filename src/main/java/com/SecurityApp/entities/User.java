package com.SecurityApp.entities;

import com.SecurityApp.entities.enums.Permission;
import com.SecurityApp.entities.enums.Role;
import com.SecurityApp.utils.PermissionMapping;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;
    private String name;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private  List<Role>roles;

//    @ElementCollection(fetch = FetchType.EAGER)
//    @Enumerated(EnumType.STRING)
//    private List<Permission> permission;

    //every time returring role along with return authority for that user
    //authority have two thing: role and permission
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        Set<SimpleGrantedAuthority> authorities= roles.stream()
//                .map(role->new SimpleGrantedAuthority("ROLE_"+role.name()))
//                .collect(Collectors.toSet());
//
//        permission.forEach(
//                permission -> authorities.add(
//                        new SimpleGrantedAuthority(permission.name()))
//        );
//
//        return  authorities;
//    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
//        Set<SimpleGrantedAuthority> authorities= roles.stream()
//                .map(role->new SimpleGrantedAuthority("ROLE_"+role.name()))
//                .collect(Collectors.toSet());

             Set<SimpleGrantedAuthority>authorities=new HashSet<>();

             roles.forEach(role ->{
                 Set<SimpleGrantedAuthority>permission= PermissionMapping.getAuthorityForRole(role);
                 authorities.addAll(permission);
                 authorities.add(new SimpleGrantedAuthority("ROLE_"+role.name()));

                     }

             );

        return  authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}

