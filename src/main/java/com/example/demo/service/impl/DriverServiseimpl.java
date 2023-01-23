package com.example.demo.service.impl;

import com.example.demo.service.DriverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DriverServiseimpl implements DriverService {

    @Override
    public String driverName() {
        String message = "In the driverName method";
        log.info(message);
        log.warn(message);
        log.error(message);

        return "Ivan";
    }

}
