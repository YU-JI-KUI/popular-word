package com.kris.service.impl;

import com.kris.service.ValidatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ValidatingServiceImpl implements ValidatingService {

    // 正则表达式用于验证URL格式
    private static final String URL_REGEX = "^(https?|ftp)://" + "(([a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,4})" // 域名
            + "|" + "((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." // IPv4
            + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." + "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)))" + "(:[0-9]{1,5})?" // 端口
            + "(/.*)?$"; // 资源路径

    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    @Override
    public boolean validateUrl(String urlStr) {
        if (urlStr == null || urlStr.isEmpty()) {
            return false;
        }

        // 使用正则表达式进行格式校验
        Matcher matcher = URL_PATTERN.matcher(urlStr);
        if (!matcher.matches()) {
            return false;
        }

        try {
            // 尝试创建一个 URL 对象
            new URL(urlStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
