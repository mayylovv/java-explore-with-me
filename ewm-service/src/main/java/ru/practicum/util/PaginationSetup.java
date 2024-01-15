package ru.practicum.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PaginationSetup extends PageRequest {

    public PaginationSetup(int page, int size, Sort sort) {
        super(page / size, size, sort);
    }
}