package com.bizzdesk.group.user.management.service;

import com.bizzdesk.group.user.management.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode

@Accessors(chain = true)
public class UserDetailsImpl implements UserDetails {

    private String userId;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private boolean activeStatus;
    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRoleId().getRoleName()));
        return new UserDetailsImpl(
                user.getUserId(),
                user.getMobileNumber(),
                user.getEmailAddress(),
                user.getPassword(),
                user.isActiveStatus(),
                authorities);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
