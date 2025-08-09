package com.example.WordWise.service;

import com.example.WordWise.entity.Config;
import com.example.WordWise.enums.KeyConfigEnum;
import com.example.WordWise.repository.ConfigRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfigService {
    @Autowired
    private ConfigRepository configRepository;

    public List<Config> getSetupDataConfig() {
        return configRepository.findAll();
    }

    public Config getConfigByKey(KeyConfigEnum keyConfigEnum, List<Config> configs) {
        Config config = configs.stream()
                .filter(e -> e.getType().equals(keyConfigEnum))
                .findFirst()
                .orElse(null);

        return config;
    }

    public List saveAllDataConfig(List<Config> configs) {
        return configRepository.saveAll(configs);
    }

    public Config saveConfig(Config config) {
        return configRepository.save(config);
    }
}
