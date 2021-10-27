package com.xbk.spring.web.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.cache.support.NullValue;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @date: 2021/2/25 下午5:55
 * @see org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
 */
public class NullValueSerializer extends StdSerializer<NullValue> {

    private static final long serialVersionUID = 1999052150548658808L;
    private final String classIdentifier;

    /**
     * @param classIdentifier can be {@literal null} and will be defaulted to {@code @class}.
     */
    NullValueSerializer(@Nullable String classIdentifier) {

        super(NullValue.class);
        this.classIdentifier = StringUtils.hasText(classIdentifier) ? classIdentifier : "@class";
    }

    @Override
    public void serialize(NullValue value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException {

        jgen.writeStartObject();
        jgen.writeStringField(classIdentifier, NullValue.class.getName());
        jgen.writeEndObject();
    }

}