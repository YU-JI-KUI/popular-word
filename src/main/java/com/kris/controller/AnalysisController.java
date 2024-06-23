package com.kris.controller;

import com.kris.service.AnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    @Autowired private AnalysisService analysisService;

    @GetMapping("/most-common-words")
    public Map<String, Integer> mostCommonWords(@RequestParam String url) throws IOException {
        return analysisService.analysis(url);
    }

}
