package com.kris.service;

import java.io.IOException;
import java.util.Map;

public interface AnalysisService {

    Map<String, Integer> analysis(String url) throws IOException;

}
