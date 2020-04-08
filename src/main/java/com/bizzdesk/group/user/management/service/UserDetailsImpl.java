package com.bizzdesk.group.user.management.service;

import com.bizzdesk.group.user.management.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode

@Accessors(chain = true)
public class UserDetailsImpl implements UserDetails {

    private String userId;
    private String username;
    private String phoneNumber;
    @JsonIgnore
    private String password;
    private boolean activeStatus;
    private String role;

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getUserId(),
                user.getEmailAddress(),
                user.getMobileNumber(),
                user.getPassword(),
                user.isActiveStatus(),
                user.getRoleId().getRoleName());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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
