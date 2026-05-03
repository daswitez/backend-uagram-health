package bo.edu.uagrm.ugram.identity.service;

import bo.edu.uagrm.ugram.common.security.UserPrincipal;
import bo.edu.uagrm.ugram.identity.entity.User;
import bo.edu.uagrm.ugram.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Loads user from DB for Spring Security authentication.
 * Supports login by email OR by RU (Registro Universitario).
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrCi(identifier, identifier)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + identifier));

        return UserPrincipal.fromUser(user);
    }
}
