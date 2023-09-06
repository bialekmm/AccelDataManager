package io.github.bialekmm.AccelDataManager.repository;

import io.github.bialekmm.AccelDataManager.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByName(String name);
}
