package com.example.noticeservice.service;

import com.tinify.Source;
import com.tinify.Tinify;
import java.io.IOException;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageCompressor {

    @Value("${tinify.apiKey}")
    private String apiKey;

    @PostConstruct
    private void init() {
        Tinify.setKey(apiKey);
    }

    public void compressAndSave(byte[] buffer, String path) {
        Source source = Tinify.fromBuffer(buffer);
        try {
            source.toFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
