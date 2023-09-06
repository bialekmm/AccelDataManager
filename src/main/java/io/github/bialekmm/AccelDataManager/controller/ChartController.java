package io.github.bialekmm.AccelDataManager.controller;

import io.github.bialekmm.AccelDataManager.entity.ChannelMeasurement;
import io.github.bialekmm.AccelDataManager.service.MeasurementFileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Controller
public class ChartController {

    private final MeasurementFileService measurementFileService;

    public ChartController(MeasurementFileService measurementFileService) {
        this.measurementFileService = measurementFileService;
    }

    @GetMapping("/chart/{fileId}")
    public String getChartData(@PathVariable Long fileId, Model model) {
        List<ChannelMeasurement> channelMeasurements = measurementFileService.getAllChannelMeasurementsById(fileId);
        List<Map<String, Object>> chartData = new ArrayList<>();
        for (ChannelMeasurement channelMeasurement : channelMeasurements) {
            Map<String, Object> dataPoint = new HashMap<>();
            dataPoint.put("time", channelMeasurement.getMeasurementTime());
            dataPoint.put("Ch1", channelMeasurement.getCh1());
            dataPoint.put("Ch2", channelMeasurement.getCh2());
            dataPoint.put("Ch3", channelMeasurement.getCh3());
            chartData.add(dataPoint);
        }
        model.addAttribute("chartData", chartData);
        return "chart";

    }
}
