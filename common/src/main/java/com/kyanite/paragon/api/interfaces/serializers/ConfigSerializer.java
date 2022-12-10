package com.kyanite.paragon.api.interfaces.serializers;

import blue.endless.jankson.api.SyntaxError;

import java.io.IOException;

public interface ConfigSerializer {
    public String getSuffix();
    public void load() throws IOException, SyntaxError;
    public void save() throws IOException;
}
