package io.github.bialekmm.AccelDataManager.service;

import io.github.bialekmm.AccelDataManager.entity.RoleEntity;
import io.github.bialekmm.AccelDataManager.entity.UserEntity;
import io.github.bialekmm.AccelDataManager.repository.RoleRepository;
import io.github.bialekmm.AccelDataManager.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserEntity> findAllUsers() {
        return userRepository.findAll();
    }

    public void saveUser(UserEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        checkRole(user);
        userRepository.save(user);
    }

    public void deleteUser(UserEntity user) {
        userRepository.delete(user);
    }

    private void checkRole(UserEntity user) {
        if(roleRepository.findByName("ROLE_ADMIN") == null || roleRepository.findByName("ROLE_USER") == null){
            RoleEntity role = new RoleEntity();
            if(role.getName()==null){
                role.setName("ROLE_ADMIN");
                user.setRoles(List.of(role));
            }
            if(roleRepository.findByName("ROLE_ADMIN") != null && roleRepository.findByName("ROLE_USER") == null){
                role.setName("ROLE_USER");
                user.setRoles(List.of(role));
            }
        }
        if(roleRepository.findByName("ROLE_ADMIN") != null && roleRepository.findByName("ROLE_USER") != null){
            RoleEntity role = roleRepository.findByName("ROLE_USER");
            user.setRoles(Collections.singletonList(role));
        }
    }

    public UserEntity findByEmail(String email) {
       return userRepository.findByEmail(email);
    }
}
