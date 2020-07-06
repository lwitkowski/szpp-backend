package pl.szpp.backend.version;

class VersionResponse {

    public final String application;
    public final String version;
    public final String buildDate;
    public final String revision;

    VersionResponse(String application, String version, String buildDate, String revision) {
        this.application = application;
        this.version = version;
        this.buildDate = buildDate;
        this.revision = revision;
    }

}
