package com.example.WordWise.service;

import com.example.WordWise.dto.request.CreateUserRequest;
import com.example.WordWise.dto.request.InsertWordRequest;
import com.example.WordWise.entity.Config;
import com.example.WordWise.entity.User;
import com.example.WordWise.enums.KeyConfigEnum;
import com.example.WordWise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SetupDataService {
    @Autowired
    private ConfigService configService;

    @Autowired
    private UserService userService;

    @Autowired
    private WordService wordService;

    private List<String> userIds = new ArrayList<>();

    private List<Config> configs;

    public void setupData() {
        setUpConfigData();
        Config configSetUpData = configService.getConfigByKey(KeyConfigEnum.SETUP_DATA, configs);

        if (configs == null || configs.isEmpty()) {
            return;
        }

        setupUserData(configSetUpData);
        setUpWords(configSetUpData);

        // update config
        configSetUpData.setValue("TRUE");
        configs = configService.saveAllDataConfig(configs);
    }

    public void setupUserData(Config configSetUpData) {
        if (configSetUpData != null && "TRUE".equals(configSetUpData.getValue())) {
            return; // User data already set up
        }

        List<CreateUserRequest> createUserRequests = Arrays.asList(
                new CreateUserRequest("user1", "abc1@gmail.com", "123"),
                new CreateUserRequest("user2", "abc2@gmail.com", "123")
        );

        for (int i = 0; i < createUserRequests.size(); i++) {
            User user = null;
            try {
                user = userService.createUser(createUserRequests.get(i));
            } catch (Exception e) {
                user = userService.getUserByEmail(createUserRequests.get(i).getEmail());
            }
            userIds.add(user.getUserId());
        }
    }

    public void setUpConfigData() {
        configs = configService.getSetupDataConfig();

        if (configs != null && !configs.isEmpty()) {
            return; // Configs already set up
        }

        configs.add(new Config(null, "FALSE", KeyConfigEnum.SETUP_DATA.getKey()));

        configs = this.configService.saveAllDataConfig(configs);
    }

    public void setUpWords(Config configSetUpData) {
        Config config = configService.getConfigByKey(KeyConfigEnum.SETUP_DATA, configs);

        if (config != null && "TRUE".equals(config.getValue())) {
            return; // User data already set up
        }

        if (userIds == null || userIds.isEmpty()) {
            return; // No users to set up words for
        }

        userIds.stream().forEach(userId -> {
            List<InsertWordRequest> list = Arrays.asList(
                    InsertWordRequest.builder()
                            .userId(userId)
                            .context("When you meet someone, you can say 'Hello' to greet them.")
                            .isExtension(false)
                            .englishWord("Hello")
                            .vietnameseWord("Xin chào")
                            .note("This is a very common greeting used in many situations. You can use it when meeting someone for the first time or when entering a room. It is polite and friendly.")
                            .build(),
                    InsertWordRequest.builder()
                            .userId(userId)
                            .context("She ate an Apple for breakfast every morning.")
                            .isExtension(true)
                            .englishWord("Apple")
                            .vietnameseWord("Táo")
                            .note("Apples are nutritious and come in many varieties. They are a staple in many diets and cultures.")
                            .build(),
                    InsertWordRequest.builder()
                            .userId(userId)
                            .context("The Dog barked loudly at the stranger.")
                            .isExtension(false)
                            .englishWord("Dog")
                            .vietnameseWord("Chó")
                            .note("Dogs are loyal companions and have been domesticated for thousands of years. They are known for their intelligence and friendliness.")
                            .build(),
                    InsertWordRequest.builder()
                            .userId(userId)
                            .context("He read a Book about history last night.")
                            .isExtension(true)
                            .englishWord("Book")
                            .vietnameseWord("Sách")
                            .note("Books are sources of knowledge and entertainment. They come in many genres and formats, and are essential for education.")
                            .build()
            );

           list.stream().forEach(insertWordRequest -> {
                wordService.insertWord(insertWordRequest);
           });
        });
    }



}
