package io.github.bialekmm.AccelDataManager.controller;

import io.github.bialekmm.AccelDataManager.entity.ChannelMeasurement;
import io.github.bialekmm.AccelDataManager.entity.MeasurementFile;
import io.github.bialekmm.AccelDataManager.service.MeasurementFileService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;



@Controller
@RequestMapping("/measurement")
public class MeasurementFileController {

    private final MeasurementFileService measurementFileService;

    public MeasurementFileController(MeasurementFileService measurementFileService) {
        this.measurementFileService = measurementFileService;
    }

    @GetMapping("/list")
    public String listMeasurements(Model model) {
        model.addAttribute("measurementFiles", measurementFileService.getAllMeasurementFiles());
        return "measurement/list";
    }

    @GetMapping("/add")
    public String showUploadForm() {
        return "measurement/add";
    }

    @PostMapping("/add")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("fileName") String fileName,
                             RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            return "redirect:/?message=File is empty.";
        }

        try (InputStream is = file.getInputStream();
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr)) {

            MeasurementFile measurementFile = measurementFileService.processFile(br);
            measurementFile.setName(fileName);
            measurementFileService.saveMeasurementFile(measurementFile);

            redirectAttributes.addFlashAttribute("message", "File uploaded successfully.");
            return "redirect:/measurement/list";
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "File upload error.");
            return "redirect:/measurement/list";        }
    }

    @RequestMapping(value = "/delete/{fileId}", method = RequestMethod.GET)
    public String deleteMeasurement(@PathVariable Long fileId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getAuthorities().stream().noneMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/index";
        }
        measurementFileService.deleteMeasurement(fileId);

        return "redirect:/measurement/list";
    }

    @RequestMapping(value = "/{fileId}", method = RequestMethod.GET)
    public String viewRecords(@PathVariable Long fileId, Model model,
                              @RequestParam("page") Optional<Integer> page,
                              @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(100);

        Page<ChannelMeasurement> channelMeasurementPage = measurementFileService.findPaginated(fileId, PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("channelMeasurementPage", channelMeasurementPage);

        int totalPages = channelMeasurementPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .toList();
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("name", measurementFileService.getAllChannelMeasurementsById(fileId).get(0).getMeasurementFile().getName());
        model.addAttribute("fileId", fileId);
        model.addAttribute("pageNumber", channelMeasurementPage.getNumber() + 1);
        System.out.println("Channel" + channelMeasurementPage.getNumber());
        System.out.println("Page" + page);

        return "records";
    }
}
