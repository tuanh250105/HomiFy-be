package com.homifybackend.mapper;

import com.homifybackend.model.*;

public enum PropertyFilterType {

    SINGLE_HOUSE(SingleHouse.class),
    VILLA(Villa.class),
    TOWN_HOME(TownHouse.class);

    private final Class<? extends Property> propertyClass;

    PropertyFilterType(Class<? extends Property> propertyClass) {
        this.propertyClass = propertyClass;
    }

    public Class<? extends Property> getPropertyClass() {
        return propertyClass;
    }

    public static Class<? extends Property> from(String type) {
        if (type == null) return null;
        return PropertyFilterType.valueOf(type.toUpperCase()).getPropertyClass();
    }
}
