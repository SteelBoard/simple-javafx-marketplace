package org.steelboard.marketplace.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.steelboard.marketplace.dto.user.UserRegisterDto;
import org.steelboard.marketplace.entity.Role;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.exception.EmailAlreadyExistsException;
import org.steelboard.marketplace.exception.UsernameAlreadyExistsException;
import org.steelboard.marketplace.mapper.UserMapper;
import org.steelboard.marketplace.repository.RoleRepository;
import org.steelboard.marketplace.repository.UserRepository;

import java.util.Set;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {
    @PersistenceContext
    private EntityManager em;
    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    // пересмотреть возвращаемый тип
    public void register(UserRegisterDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(dto.getUsername());
        }
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Set.of(roleRepository.findByName("ROLE_USER").orElse(new Role())));

        User saved = userRepository.save(user);
    }
}
