package dev.odane.memberservice.config;


import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    static final String RESOURCE = "capstone-api";
    static final String ROLES = "roles";

    static final String RESOURCE_ACCESS = "resource_access";
    static final String PRINCIPLE_ATTRIBUTE = "preferred_username";
    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt source) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(source).stream(),
                extractResourceRoles(source).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(
                source,
                authorities,
                getPrincipleClaimName(source)
        );
    }

    private String getPrincipleClaimName(Jwt source) {
        String claimName = JwtClaimNames.SUB;

        if (PRINCIPLE_ATTRIBUTE != null) {
            claimName = PRINCIPLE_ATTRIBUTE;
        }
        return source.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess;
        Map<String, Object> resource;
        Collection<String> resourceRoles;

        if (jwt.getClaim(RESOURCE_ACCESS) == null ){
             return Set.of();
        }
        resourceAccess = jwt.getClaim(RESOURCE_ACCESS);

        if (resourceAccess.get(RESOURCE) == null)    {
            return Set.of();
        }
        resource = (Map<String, Object>) resourceAccess.get(RESOURCE);

        resourceRoles = (Collection<String>) resource.get(ROLES);

        return resourceRoles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role))
                .collect(Collectors.toSet());
    }
}
