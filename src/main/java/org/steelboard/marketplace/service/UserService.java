package org.steelboard.marketplace.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.steelboard.marketplace.dto.user.UserRegisterDto;
import org.steelboard.marketplace.dto.user.UserUpdateDto;
import org.steelboard.marketplace.entity.Cart;
import org.steelboard.marketplace.entity.Role;
import org.steelboard.marketplace.entity.User;
import org.steelboard.marketplace.exception.EmailAlreadyExistsException;
import org.steelboard.marketplace.exception.UsernameAlreadyExistsException;
import org.steelboard.marketplace.mapper.UserMapper;
import org.steelboard.marketplace.repository.RoleRepository;
import org.steelboard.marketplace.repository.UserRepository;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {
    private final CartService cartService;
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

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = false)
    public void updateActiveStatus(Long id, boolean active) {
        User user = findById(id);
        user.setActive(active);
        userRepository.save(user);
    }

    @Transactional(readOnly = false)
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

        userRepository.save(user);
        cartService.addCartByUser(user);
    }

    @Transactional(readOnly = false)
    public void updateUser(User user, UserUpdateDto dto) {
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());

        if (dto.isPasswordBeingUpdated()) {
            // Хэширование пароля через PasswordEncoder
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        userRepository.save(user);
    }

    @Transactional(readOnly = false)
    public void addUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(roleRepository.findByName("ROLE_USER").orElse(new Role())));

        userRepository.save(user);
        cartService.addCartByUser(user);
    }

    public void addAdmin(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
        cartService.addCartByUser(user);
    }
}
