package com.kris.service.impl;

import com.kris.service.AnalysisService;
import com.kris.service.ValidatingService;
import com.kris.util.WordFrequencyAnalyzer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired private ValidatingService validatingService;

    @Override
    public Map<String, Integer> analysis(String url) throws IOException {
        if (!validatingService.validateUrl(url)) {
            throw new IllegalArgumentException();
        }

        return WordFrequencyAnalyzer.analysis(url);
    }
}
