package com.example.demo.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Rating {

    RARS_0("0+"),
    RARS_6("6+"),
    RARS_12("12+"),
    RARS_16("16+"),
    RARS_18("18+");

    private final String description;

}
