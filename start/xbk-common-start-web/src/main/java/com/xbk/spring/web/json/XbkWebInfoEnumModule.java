package com.xbk.spring.web.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.util.Objects;

public class XbkWebInfoEnumModule extends SimpleModule {

    /**
     * 初始化
     */
    public XbkWebInfoEnumModule() {
        super(PackageVersion.VERSION);
        this.addSerializer(XbkWebInfoBaseEnum.class, new CrmWebInfoBaseEnumSerializer());
        this.addDeserializer(Enum.class, new CrmWebInfoBaseEnumDeSerializer());
    }

    /**
     * 序列化方法
     */
    public static class CrmWebInfoBaseEnumSerializer extends JsonSerializer<XbkWebInfoBaseEnum> {

        /**
         * 序列化方案
         */
        @Override
        public void serialize(XbkWebInfoBaseEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (Objects.nonNull(value)) {
                gen.writeString(String.valueOf(value.getWebInfoCode()));
            }
        }
    }

    /**
     * 反序列化方法
     */
    public static class CrmWebInfoBaseEnumDeSerializer extends StdDeserializer<Enum> {

        public CrmWebInfoBaseEnumDeSerializer() {
            super(Enum.class);
        }

        @Override
        public boolean isCachable() {
            return true;
        }

        /**
         * 反序列化方法
         */
        @Override
        public Enum deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            //当前需要解析的对象
            Object currentValue = jsonParser.getCurrentValue();
            if (Objects.isNull(currentValue)) {
                return null;
            }
            //当前字段名称、类信息
            final String currentFieldName = jsonParser.getCurrentName();
            Class<Enum> currentValueClass = (Class<Enum>) BeanUtils.findPropertyType(currentFieldName, currentValue.getClass());
            //获取当前字段对应的值
            JsonNode treeNode = jsonParser.getCodec().readTree(jsonParser);
            JsonNode currentNode = treeNode.get(currentFieldName);
            final String value = Objects.isNull(currentNode) ? treeNode.asText() : currentNode.asText();
            //获取当前枚举对应的值
            Enum[] enumConstants = currentValueClass.getEnumConstants();
            if (Objects.isNull(enumConstants) || enumConstants.length == 0) {
                return null;
            }
            //判断当前是否是子类
            boolean assignableFromCrmWebBaseEnum = XbkWebInfoBaseEnum.class.isAssignableFrom(currentValueClass);
            for (Enum e : enumConstants) {
                String compareValue = assignableFromCrmWebBaseEnum
                        ? ((XbkWebInfoBaseEnum) e).getWebInfoCode()
                        : e.name();
                if (value.equals(compareValue)) {
                    return e;
                }
            }
            return assignableFromCrmWebBaseEnum
                    ? (Enum) ((XbkWebInfoBaseEnum) enumConstants[0]).deserializeDefaultValue()
                    : null;
        }
    }

}
