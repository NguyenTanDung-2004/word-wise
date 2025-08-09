package com.example.WordWise.config;

import com.example.WordWise.service.SetupDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SetupData implements CommandLineRunner {
    @Autowired
    private SetupDataService setupDataService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("setting up data");
        setupDataService.setupData();
    }
}
