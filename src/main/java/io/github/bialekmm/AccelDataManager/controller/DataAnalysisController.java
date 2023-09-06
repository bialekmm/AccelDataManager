package io.github.bialekmm.AccelDataManager.controller;

import io.github.bialekmm.AccelDataManager.entity.ChannelMeasurement;
import io.github.bialekmm.AccelDataManager.service.DataAnalysisService;
import io.github.bialekmm.AccelDataManager.service.MeasurementFileService;
import org.apache.commons.math3.complex.Complex;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Controller
public class DataAnalysisController {

    private final DataAnalysisService dataAnalysisService;
    private final MeasurementFileService measurementFileService;

    public DataAnalysisController(DataAnalysisService dataAnalysisService, MeasurementFileService measurementFileService) {
        this.dataAnalysisService = dataAnalysisService;
        this.measurementFileService = measurementFileService;
    }

    @GetMapping("/fft/{fileId}")
    public String getChartData(@PathVariable Long fileId, Model model) {
        List<ChannelMeasurement> channelMeasurements = measurementFileService.getAllChannelMeasurementsById(fileId);
        Complex[] complexesCh1 = dataAnalysisService.performFrequencyAnalysis(channelMeasurements, "Ch1");
        Complex[] complexesCh2 = dataAnalysisService.performFrequencyAnalysis(channelMeasurements, "Ch2");
        Complex[] complexesCh3 = dataAnalysisService.performFrequencyAnalysis(channelMeasurements, "Ch3");

        List<double[]> serializedDataCh1 = dataAnalysisService.serializeComplexArray(complexesCh1);
        List<double[]> serializedDataCh2 = dataAnalysisService.serializeComplexArray(complexesCh2);
        List<double[]> serializedDataCh3 = dataAnalysisService.serializeComplexArray(complexesCh3);

        List<Double> measurementTime = new ArrayList<>();
        for (ChannelMeasurement channelMeasurement : channelMeasurements) {
            measurementTime.add(channelMeasurement.getMeasurementTime());
        }
        model.addAttribute("time", measurementTime);
        model.addAttribute("fftCh1", serializedDataCh1);
        model.addAttribute("fftCh2", serializedDataCh2);
        model.addAttribute("fftCh3", serializedDataCh3);
        return "fft";
    }

    @GetMapping("/statistic/{fileId}")
    public String getStatisticData(@PathVariable Long fileId, Model model) {
        List<ChannelMeasurement> channelMeasurements = measurementFileService.getAllChannelMeasurementsById(fileId);
        int numSmp = dataAnalysisService.numberOfSamples(channelMeasurements);
        int smpRate = dataAnalysisService.sampleRate(channelMeasurements);
        Map<String, Double> minAmp = dataAnalysisService.minAmp(channelMeasurements);
        Map<String, Double> maxAmp = dataAnalysisService.maxAmplitude(channelMeasurements);
        Map<String, Double> avaAmp = dataAnalysisService.averageAmplitude(channelMeasurements);

        model.addAttribute("numSmp", numSmp);
        model.addAttribute("smpRate", smpRate);
        model.addAttribute("minAmp", minAmp);
        model.addAttribute("maxAmp", maxAmp);
        model.addAttribute("avaAmp", avaAmp);

        return "statistic";
    }

    @GetMapping("/rms/{fileId}")
    public String getRMSData(@PathVariable Long fileId, Model model) {
        List<ChannelMeasurement> channelMeasurements = measurementFileService.getAllChannelMeasurementsById(fileId);
        List<double[]> rmsDataCh1 = dataAnalysisService.calculateFrequencyRMS(channelMeasurements, "Ch1");
        List<double[]> rmsDataCh2 = dataAnalysisService.calculateFrequencyRMS(channelMeasurements, "Ch2");
        List<double[]> rmsDataCh3 = dataAnalysisService.calculateFrequencyRMS(channelMeasurements, "Ch3");

        model.addAttribute("rmsDataCh1", rmsDataCh1);
        model.addAttribute("rmsDataCh2", rmsDataCh2);
        model.addAttribute("rmsDataCh3", rmsDataCh3);


        return "rms";
    }
}
