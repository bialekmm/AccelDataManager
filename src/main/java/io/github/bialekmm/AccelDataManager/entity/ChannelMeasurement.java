package io.github.bialekmm.AccelDataManager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "channelMeasurement")
public class ChannelMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double measurementTime;
    private double ch1;
    private double ch2;
    private double ch3;

    @ManyToOne
    @JoinColumn(name = "measurement_file_id")
    private MeasurementFile measurementFile;
}
