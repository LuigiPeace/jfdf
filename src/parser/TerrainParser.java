package parser;

import core.Terrain;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TerrainParser {

    private String filename;
    private List<String> lines;

    public TerrainParser() {
    }

    public TerrainParser(final String filename) {
        if (filename == null || filename.isEmpty())
            throw new RuntimeException("[Fatal] Missing filename argument!");

        this.filename = filename;
    }


    /**
     * Parse terrain file with given filename or constructor filename.
     * @param filename path to parse.
     * @return true if parsing was successful, false otherwise.
     */
    public boolean parse(final String filename) {
        lines = getLines(filename);

        if (lines.isEmpty()) {
            System.err.println("[Fatal] Empty file!");
            return false;
        }

        return true;
    }

    public boolean parse() {
        return parse(this.filename);
    }

    /**
     * Create a Terrain class with 3D and 2D matrices build.
     * @return new Terrain pre-build
     */
    public Terrain build() {
        Terrain terrain = new Terrain(lines);

        terrain.buildData();
        terrain.buildProjectionData();

        return terrain;
    }

    private final List<String> getLines(final String filename) throws RuntimeException {
        final Path path = Paths.get(filename);
        final File file = path.toFile();

        if (!file.exists())
            throw new RuntimeException("[Fatal] Cannot open file: " + filename);

        if (!file.canRead())
            throw new RuntimeException("[Fatal] Cannot read file: " + filename);

        try {
            return Files.readAllLines(path, Charset.defaultCharset());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}
