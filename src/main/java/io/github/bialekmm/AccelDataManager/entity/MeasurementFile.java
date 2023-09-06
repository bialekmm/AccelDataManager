package io.github.bialekmm.AccelDataManager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "measurementFile")
public class MeasurementFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private String name;
    private int records;

    @OneToMany(mappedBy = "measurementFile", cascade = CascadeType.ALL)
    private List<ChannelMeasurement> channelMeasurements = new ArrayList<>();

}
