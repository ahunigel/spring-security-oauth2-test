package com.github.ahunigel.test.security.helper;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by Nigel Zheng on 7/31/2018.
 */
public class SerializationHelper {
  private final List<HttpMessageConverter<?>> messageConverters;

  public SerializationHelper(List<HttpMessageConverter<?>> messageConverters) {
    this.messageConverters = messageConverters;
  }

  public <T> ByteArrayHttpOutputMessage outputMessage(final T payload, final MediaType mediaType) {
    if (payload == null) {
      return null;
    }

    List<HttpMessageConverter<?>> relevantConverters = messageConverters.stream()
        .filter(converter -> converter.canWrite(payload.getClass(), mediaType))
        .toList();

    final ByteArrayHttpOutputMessage converted = new ByteArrayHttpOutputMessage();
    boolean isConverted = false;
    for (HttpMessageConverter<?> converter : relevantConverters) {
      try {
        ((HttpMessageConverter<T>) converter).write(payload, mediaType, converted);
        isConverted = true; //won't be reached if a conversion error occurs
        break; //stop iterating over converters after first successful conversion
      } catch (IOException e) {
        //swallow exception so that next converter is tried
      }
    }

    if (!isConverted) {
      throw new RuntimeException("Could not convert " + payload.getClass() + " to " + mediaType.toString());
    }

    return converted;
  }

  /**
   * Provides a String representation of provided payload
   *
   * @param payload
   * @param mediaType
   * @param <T>
   * @return
   * @throws Exception if things go wrong (no registered serializer for payload type and asked MediaType, serialization failure, ...)
   */
  public <T> String asString(T payload, MediaType mediaType) {
    return payload == null ?
        null :
        outputMessage(payload, mediaType).out.toString();
  }

  public <T> String asJsonString(T payload) {
    return asString(payload, MediaType.APPLICATION_JSON);
  }

  public static final class ByteArrayHttpOutputMessage implements HttpOutputMessage {
    public final ByteArrayOutputStream out = new ByteArrayOutputStream();
    public final HttpHeaders headers = new HttpHeaders();

    @Override
    public OutputStream getBody() {
      return out;
    }

    @Override
    public HttpHeaders getHeaders() {
      return headers;
    }
  }
}
