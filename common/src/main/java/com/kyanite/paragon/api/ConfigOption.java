package com.kyanite.paragon.api;

public class ConfigOption<E extends Object>{
    private final String title;
    private final Object defaultValue;
    private Object value = false;

    public  ConfigOption(String title, E defaultValue) {
        this.title = title;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public E value() {
        return (E) value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public String getTitle() {
        return title;
    }
}
