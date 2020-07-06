package pl.szpp.backend.version;

import io.smallrye.config.PropertiesConfigSourceProvider;

public final class GitPropertiesConfigSourceProvider extends PropertiesConfigSourceProvider {

    public GitPropertiesConfigSourceProvider() {
        super("git.properties", true, GitPropertiesConfigSourceProvider.class.getClassLoader());
    }

}
