package com.kyanite.paragon.api.interfaces.serializers;

import java.io.IOException;

public interface ConfigSerializer {
    public String getSuffix();
    public void load() throws IOException;
    public void save() throws IOException;
}
