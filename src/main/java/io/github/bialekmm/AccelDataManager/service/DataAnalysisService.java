package io.github.bialekmm.AccelDataManager.service;

import io.github.bialekmm.AccelDataManager.entity.ChannelMeasurement;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataAnalysisService {

    public int numberOfSamples(List<ChannelMeasurement> channelMeasurements) {
        return (int) channelMeasurements.size();
    }

    public int sampleRate(List<ChannelMeasurement> channelMeasurements) {
        double samples = channelMeasurements.size();
        double time = channelMeasurements.get(channelMeasurements.size()-1).getMeasurementTime();
        return (int) Math.round(samples/time);
    }

    public Map<String, Double> minAmp(List<ChannelMeasurement> channelMeasurements) {
        Map<String, Double> minAmplitudes = new HashMap<>();
        minAmplitudes.put("Ch1", Double.MAX_VALUE);
        minAmplitudes.put("Ch2", Double.MAX_VALUE);
        minAmplitudes.put("Ch3", Double.MAX_VALUE);

        for (ChannelMeasurement channelMeasurement : channelMeasurements) {
            double ch1Amp = channelMeasurement.getCh1();
            double ch2Amp = channelMeasurement.getCh2();
            double ch3Amp = channelMeasurement.getCh3();

            if (ch1Amp < minAmplitudes.get("Ch1")) {
                minAmplitudes.put("Ch1", ch1Amp);
            }

            if (ch2Amp < minAmplitudes.get("Ch2")) {
                minAmplitudes.put("Ch2", ch2Amp);
            }

            if (ch3Amp < minAmplitudes.get("Ch3")) {
                minAmplitudes.put("Ch3", ch3Amp);
            }
        }
        return minAmplitudes;
    }

    public Map<String, Double> maxAmplitude(List<ChannelMeasurement> channelMeasurements) {
        Map<String, Double> maxAmplitudes = new HashMap<>();
        maxAmplitudes.put("Ch1", Double.MIN_VALUE);
        maxAmplitudes.put("Ch2", Double.MIN_VALUE);
        maxAmplitudes.put("Ch3", Double.MIN_VALUE);

        for (ChannelMeasurement channelMeasurement : channelMeasurements) {
            double ch1Amp = channelMeasurement.getCh1();
            double ch2Amp = channelMeasurement.getCh2();
            double ch3Amp = channelMeasurement.getCh3();

            if (ch1Amp > maxAmplitudes.get("Ch1")) {
                maxAmplitudes.put("Ch1", ch1Amp);
            }

            if (ch2Amp > maxAmplitudes.get("Ch2")) {
                maxAmplitudes.put("Ch2", ch2Amp);
            }

            if (ch3Amp > maxAmplitudes.get("Ch3")) {
                maxAmplitudes.put("Ch3", ch3Amp);
            }
        }

        return maxAmplitudes;
    }

    public Map<String, Double> averageAmplitude(List<ChannelMeasurement> channelMeasurements) {
        Map<String, Double> averageAmplitudes = new HashMap<>();
        averageAmplitudes.put("Ch1", 0.0);
        averageAmplitudes.put("Ch2", 0.0);
        averageAmplitudes.put("Ch3", 0.0);

        for (ChannelMeasurement channelMeasurement : channelMeasurements) {
            double ch1Amp = channelMeasurement.getCh1();
            double ch2Amp = channelMeasurement.getCh2();
            double ch3Amp = channelMeasurement.getCh3();

            averageAmplitudes.put("Ch1", averageAmplitudes.get("Ch1") + ch1Amp);
            averageAmplitudes.put("Ch2", averageAmplitudes.get("Ch2") + ch2Amp);
            averageAmplitudes.put("Ch3", averageAmplitudes.get("Ch3") + ch3Amp);
        }

        int numSamples = channelMeasurements.size();

        if (numSamples > 0) {
            averageAmplitudes.put("Ch1", averageAmplitudes.get("Ch1") / numSamples);
            averageAmplitudes.put("Ch2", averageAmplitudes.get("Ch2") / numSamples);
            averageAmplitudes.put("Ch3", averageAmplitudes.get("Ch3") / numSamples);
        }

        return averageAmplitudes;
    }


    public List<double[]> calculateFrequencyRMS(List<ChannelMeasurement> channelMeasurements, String channelName) {
        Complex[] fftResult = performFrequencyAnalysis(channelMeasurements, channelName);

        double[] amplitudeSpectrum = calculateAmplitudeSpectrum(fftResult);

        return calculateRMSForFrequencyRanges(
                amplitudeSpectrum, (double) sampleRate(channelMeasurements), (double) sampleRate(channelMeasurements)/2);
    }

    public Complex[] performFrequencyAnalysis(List<ChannelMeasurement> channelMeasurements, String channelName) {
        double[] inputSignal = channelMeasurements.stream()
                .mapToDouble(channelMeasurement -> switch (channelName) {
                    case "Ch1" -> channelMeasurement.getCh1();
                    case "Ch2" -> channelMeasurement.getCh2();
                    case "Ch3" -> channelMeasurement.getCh3();
                    default -> 0.0;
                })
                .toArray();

        int paddedLength = Integer.highestOneBit(inputSignal.length - 1) << 1;
        double[] paddedInputSignal = Arrays.copyOf(inputSignal, paddedLength);

        DftNormalization normalization = DftNormalization.STANDARD;

        FastFourierTransformer transformer = new FastFourierTransformer(normalization);

        return transformer.transform(paddedInputSignal, TransformType.FORWARD);
    }
    public List<double[]> serializeComplexArray(Complex[] complexArray) {
        List<double[]> serializedData = new ArrayList<>();
        for (Complex complex : complexArray) {
            double[] entry = new double[] { complex.getReal(), complex.getImaginary() };
            serializedData.add(entry);
        }
        return serializedData;
    }
    private double[] calculateAmplitudeSpectrum(Complex[] fftResult) {
        double[] amplitudeSpectrum = new double[fftResult.length];
        for (int i = 0; i < fftResult.length; i++) {
            amplitudeSpectrum[i] = fftResult[i].abs();
        }
        return amplitudeSpectrum;
    }
    public List<double[]> calculateRMSForFrequencyRanges(double[] amplitudeSpectrum, double sampleRate, double maxFrequency) {
        List<double[]> rmsValues = new ArrayList<>();
        int spectrumLength = amplitudeSpectrum.length;

        double frequencyResolution = sampleRate / spectrumLength;

        for (double frequency = 0; frequency <= maxFrequency; frequency += frequencyResolution) {
            int startIndex = (int) Math.floor(frequency / frequencyResolution);
            int endIndex = (int) Math.floor((frequency + frequencyResolution) / frequencyResolution);

            List<Double> amplitudes = new ArrayList<>();
            for (int i = startIndex; i < endIndex; i++) {
                double amplitude = amplitudeSpectrum[i];
                amplitudes.add(amplitude);
            }

            double[] amplitudesArray = amplitudes.stream().mapToDouble(Double::doubleValue).toArray();
            double rms = calculateRMS(amplitudesArray);

            double[] frequencyRMS = { frequency, rms };
            rmsValues.add(frequencyRMS);
        }

        return rmsValues;
    }

    public double calculateRMS(double[] data) {
        if (data == null || data.length == 0) {
            return 0.0;
        }

        double sumOfSquares = 0.0;
        for (double value : data) {
            sumOfSquares += value * value;
        }

        double meanOfSquares = sumOfSquares / data.length;

        return Math.sqrt(meanOfSquares);
    }


}
