package fisei.uta.edu.ec.flagquiz.domain.entities;

public class Country {
    private final String fileName;
    private final String name;
    private final String region;

    public Country(String fileName, String name, String region) {
        this.fileName = fileName;
        this.name = name;
        this.region = region;
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }
}
