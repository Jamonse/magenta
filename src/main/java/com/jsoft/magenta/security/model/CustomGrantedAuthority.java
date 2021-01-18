package com.jsoft.magenta.security.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public class CustomGrantedAuthority implements GrantedAuthority
{
    private final Privilege privilege;

    @Override
    public String getAuthority()
    {
        return privilege.getName();
    }
}
