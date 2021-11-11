package com.example.noticeservice.controller.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;

class DateTest {

    @Test
    void dateTest() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String f1 = LocalDateTime.of(2099, 12, 31, 0, 0, 0).format(formatter);
        String f2 = LocalDateTime.now().format(formatter);

        System.out.println("f1 = " + f1);
        System.out.println("f2 = " + f2);
    }
}
