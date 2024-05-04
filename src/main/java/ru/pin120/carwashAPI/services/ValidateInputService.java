package ru.pin120.carwashAPI.services;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service

public class ValidateInputService {

    public String getErrors(BindingResult bindingResult) {
        DefaultMessageCodesResolver resolver = new DefaultMessageCodesResolver();
        Map<String, List<String>> fieldErrors = bindingResult.getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        fieldError -> fieldError.getField(),
                        Collectors.mapping(
                                fieldError -> {
                                    String defaultMessage = fieldError.getDefaultMessage();
                                    return defaultMessage != null ? defaultMessage : fieldError.getCode();
                                },
                                Collectors.toList()
                        )
                ));

        StringBuilder stringBuilder = new StringBuilder();
        fieldErrors.forEach((field, errors) -> {
            stringBuilder.append(field).append(" -> ");
            stringBuilder.append(errors.toString()).append("; ");
        });

        return stringBuilder.toString();
    }
}
