package ru.practicum.shareit.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.request.exception.BadParametersException;

@Slf4j
public class ValidatorParameters {
    public static void validatePageParameters(Integer from, Integer size) {
        if (from < 0 || size == 0) {
            log.warn("From = {}; Size = {}", from, size);
            throw new BadParametersException("Error! You giving bad Parameters");
        }
    }
}
