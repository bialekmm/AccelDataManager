package io.github.bialekmm.AccelDataManager.repository;

import io.github.bialekmm.AccelDataManager.entity.MeasurementFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementFileRepository extends JpaRepository<MeasurementFile, Long> {
}
