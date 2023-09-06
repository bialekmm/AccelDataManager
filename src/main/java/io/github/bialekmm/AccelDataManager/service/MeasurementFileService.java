package io.github.bialekmm.AccelDataManager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bialekmm.AccelDataManager.entity.ChannelMeasurement;
import io.github.bialekmm.AccelDataManager.entity.MeasurementFile;
import io.github.bialekmm.AccelDataManager.repository.ChannelMeasurementRepository;
import io.github.bialekmm.AccelDataManager.repository.MeasurementFileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeasurementFileService {

    private final MeasurementFileRepository measurementFileRepository;
    private final ChannelMeasurementRepository channelMeasurementRepository;

    public MeasurementFileService(MeasurementFileRepository measurementFileRepository, ChannelMeasurementRepository channelMeasurementRepository) {
        this.measurementFileRepository = measurementFileRepository;
        this.channelMeasurementRepository = channelMeasurementRepository;
    }

    public MeasurementFile processFile(BufferedReader br) throws IOException {
        MeasurementFile measurementFile = new MeasurementFile();
        measurementFile.setDate(LocalDate.now());
        measurementFile.setTime(LocalTime.now());

        List<ChannelMeasurement> channelMeasurements = br.lines()
                .map(this::parseLine)
                .peek(channelMeasurement -> channelMeasurement.setMeasurementFile(measurementFile))
                .collect(Collectors.toList());

        measurementFile.setChannelMeasurements(channelMeasurements);
        measurementFile.setRecords(channelMeasurements.size());

        return measurementFile;
    }

    private ChannelMeasurement parseLine(String line) {
        String[] parts = line.split("\t");
        ChannelMeasurement channelMeasurement = new ChannelMeasurement();
        channelMeasurement.setMeasurementTime(Double.parseDouble(parts[0].replace(",", ".")));
        channelMeasurement.setCh1(Double.parseDouble(parts[1].replace(",", ".")));
        channelMeasurement.setCh2(Double.parseDouble(parts[2].replace(",", ".")));
        channelMeasurement.setCh3(Double.parseDouble(parts[3].replace(",", ".")));
        return channelMeasurement;
    }

    public void saveMeasurementFile(MeasurementFile measurementFile) {
        measurementFileRepository.save(measurementFile);
    }

    public List<MeasurementFile> getAllMeasurementFiles() {
        return measurementFileRepository.findAll();
    }

    public List<ChannelMeasurement> getAllChannelMeasurementsById(Long id) {
        return channelMeasurementRepository.findAllByMeasurementFile_Id(id);
    }


    public Page<ChannelMeasurement> findPaginated(Long id, Pageable pageable) {
        final List<ChannelMeasurement> measurements = channelMeasurementRepository.findAllByMeasurementFile_Id(id);
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<ChannelMeasurement> list;

        if(measurements.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, measurements.size());
            list = measurements.subList(startItem, toIndex);
        }

        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), measurements.size());
    }
    public Optional<Double> findMaxValue(List<ChannelMeasurement> channelMeasurements) {
        if (channelMeasurements == null || channelMeasurements.isEmpty()) {
            return Optional.empty();
        }

        Comparator<ChannelMeasurement> maxValueComparator = Comparator
                .comparingDouble(channelMeasurement -> Math.max(channelMeasurement.getCh1(),
                        Math.max(channelMeasurement.getCh2(), channelMeasurement.getCh3())));

        ChannelMeasurement maxValueMeasurement = channelMeasurements.stream()
                .max(maxValueComparator)
                .orElse(null);

        if (maxValueMeasurement != null) {
            return Optional.of(Math.max(maxValueMeasurement.getCh1(),
                    Math.max(maxValueMeasurement.getCh2(), maxValueMeasurement.getCh3())));
        } else {
            return Optional.empty();
        }
    }

    public void deleteMeasurement(Long id) {
        measurementFileRepository.deleteById(id);
    }

}

