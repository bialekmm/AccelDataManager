package io.github.bialekmm.AccelDataManager.repository;

import io.github.bialekmm.AccelDataManager.entity.ChannelMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelMeasurementRepository extends JpaRepository<ChannelMeasurement, Long> {
    List<ChannelMeasurement> findAllByMeasurementFile_Id(Long id);
}

