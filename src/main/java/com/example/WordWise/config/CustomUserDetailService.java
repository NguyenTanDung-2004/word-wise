package com.example.WordWise.config;

import com.example.WordWise.entity.User;
import com.example.WordWise.enums.PermissionEnum;
import com.example.WordWise.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@Component
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomUserDetailService implements UserDetails {
    @Autowired
    private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        RoleEnum role = RoleEnum.fromId(user.getRoleId());
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (PermissionEnum permission : role.getPermission()) {
            authorities.add(new SimpleGrantedAuthority(permission.getId()));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
