package ru.bengobro.electronic_shop.eval;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;

/** Загрузчик золотого датасета из classpath (src/test/resources/golden-dataset.json). */
public final class GoldenDatasetLoader {

    private static final String RESOURCE = "/golden-dataset.json";

    private GoldenDatasetLoader() {
    }

    public static List<GoldenCase> load() {
        return load(RESOURCE);
    }

    public static List<GoldenCase> load(String resource) {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream in = GoldenDatasetLoader.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Resource not found: " + resource);
            }
            return mapper.readValue(in, new TypeReference<List<GoldenCase>>() {
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + resource, e);
        }
    }
}
