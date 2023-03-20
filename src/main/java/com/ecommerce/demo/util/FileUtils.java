package com.ecommerce.demo.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public final class FileUtils {
  private static final String TEST_DATA_ROOT_PATH = "src/test/resources/";
  private static ObjectMapper objectMapper;

  private FileUtils() {
    throw new IllegalStateException("Utility class");
  }

  @SneakyThrows
  public static <T> T loadObject(String jsonFile, Class<T> clazz) {
    Path path = Paths.get(TEST_DATA_ROOT_PATH.concat(jsonFile));
    Reader reader = Files.newBufferedReader(path);
    return getObjectMapperInstance().readValue(reader, clazz);
  }

  public static <T> List<T> loadObjectList(String jsonFile, Class<T> clazz) throws IOException {
    Path path = Paths.get(TEST_DATA_ROOT_PATH.concat(jsonFile));
    Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
    JavaType type =
        getObjectMapperInstance().getTypeFactory().constructCollectionType(List.class, clazz);
    return getObjectMapperInstance().readValue(reader, type);
  }

  @SneakyThrows
  public static String toStringFromJson(String jsonFile) {
    StringBuilder contentBuilder = new StringBuilder();
    Files.lines(Paths.get(TEST_DATA_ROOT_PATH.concat(jsonFile)), StandardCharsets.UTF_8)
        .forEach(s -> contentBuilder.append(s).append("\n"));
    return contentBuilder.toString();
  }

  private static ObjectMapper getObjectMapperInstance() {
    if (objectMapper == null) {
      objectMapper =
          new ObjectMapper()
              .setSerializationInclusion(JsonInclude.Include.NON_NULL)
              .setDateFormat(new StdDateFormat())
              .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
              .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
              .enable(JsonGenerator.Feature.IGNORE_UNKNOWN)
              .enable(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS)
              .registerModule(new JavaTimeModule());
    }
    return objectMapper;
  }
}
