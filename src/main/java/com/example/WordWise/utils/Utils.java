package com.example.WordWise.utils;

import com.example.WordWise.entity.User;
import com.example.WordWise.service.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    // Extracts specified fields into a list of maps (key = field name, value = field value)
    private static List<Map<String, Object>> getFieldsFromList(List<?> list, List<String> fieldNameList) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object obj : list) {
            Map<String, Object> map = new HashMap<>();
            for (String fieldName : fieldNameList) {
                try {
                    Field field = obj.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    map.put(fieldName, field.get(obj));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    map.put(fieldName, null); // optional: log warning
                }
            }
            result.add(map);
        }

        return result;
    }

    // Map each map of field values to a target class using ModelMapper
    private static <T> List<T> mapListToTargetClass(List<Map<String, Object>> sourceList, Class<T> targetClass) {
        ModelMapper modelMapper = new ModelMapper();
        return sourceList.stream()
                .map(map -> modelMapper.map(map, targetClass))
                .collect(Collectors.toList());
    }

    // Combine both steps: extract + convert
    public static <T> List<T> getFieldsFromList(List<?> list, List<String> fieldNameList, Class<T> targetClass) {
        List<Map<String, Object>> mapList = getFieldsFromList(list, fieldNameList);
        return mapListToTargetClass(mapList, targetClass);
    }

    public static String useCharacterToSeperateList(List<String> list, Character character) {
        if (list == null || list.isEmpty()) return "";

        StringBuilder strB = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            strB.append("\"").append(list.get(i)).append("\"");
            if (i < list.size() - 1) {
                strB.append(character);
            }
        }

        // Convert to string and remove {id= and } if present
        String result = strB.toString()
                .replace("{id=", "")
                .replace("}", "");

        return result;
    }

    public static String getUserIdFromSecurityConfig(Authentication authentication) {
        Map<String, Object> userDetails = (Map<String, Object>) authentication.getPrincipal();
        String userId = (String) userDetails.get("userId");

        return userId;
    }

    public static User getUserIdFromSecurityConfig(Authentication authentication, UserService userService) {
        Map<String, Object> userDetails = (Map<String, Object>) authentication.getPrincipal();
        String userId = (String) userDetails.get("userId");

        return userService.getUserFromId(userId);
    }
}
