package com.vegeta.global.util;


import com.vegeta.global.exception.vegeta.VegetaException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Parameter validity check util.
 *
 * @author Nacos
 */
public class ParamUtils {
    
    private static char[] validChars = new char[] {'_', '-', '.', ':'};
    
    private static final int TAG_MAX_LEN = 16;
    
    private static final int TANANT_MAX_LEN = 128;
    
    private static final String CONFIG_TAGS = "config_tags";
    
    private static final String DESC = "desc";
    
    private static final String USE = "use";
    
    private static final String EFFECT = "effect";
    
    private static final String TYPE = "type";
    
    private static final String SCHEMA = "schema";
    
    /**
     * Whitelist checks that valid parameters can only contain letters, Numbers, and characters in validChars, and
     * cannot be empty.
     */
    public static boolean isValid(String param) {
        if (param == null) {
            return false;
        }
        int length = param.length();
        for (int i = 0; i < length; i++) {
            char ch = param.charAt(i);
            if (!Character.isLetterOrDigit(ch) && !isValidChar(ch)) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isValidChar(char ch) {
        for (char c : validChars) {
            if (c == ch) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check the parameter.
     */
    public static void checkParam(String dataId, String group, String datumId, String content) throws VegetaException {
        if (StringUtils.isBlank(dataId) || !isValid(dataId.trim())) {
            throw new VegetaException(VegetaException.INVALID_PARAM, "invalid dataId : " + dataId);
        } else if (StringUtils.isBlank(group) || !isValid(group)) {
            throw new VegetaException(VegetaException.INVALID_PARAM, "invalid group : " + group);
        } else if (StringUtils.isBlank(datumId) || !isValid(datumId)) {
            throw new VegetaException(VegetaException.INVALID_PARAM, "invalid datumId : " + datumId);
        } else if (StringUtils.isBlank(content)) {
            throw new VegetaException(VegetaException.INVALID_PARAM, "content is blank : " + content);
        } else if (content.length() > PropertyUtil.getMaxContent()) {
            throw new VegetaException(VegetaException.INVALID_PARAM,
                    "invalid content, over " + PropertyUtil.getMaxContent());
        }
    }
    
    /**
     * Check the tag.
     */
    public static void checkParam(String tag) {
        if (StringUtils.isNotBlank(tag)) {
            if (!isValid(tag.trim())) {
                throw new IllegalArgumentException("invalid tag : " + tag);
            }
            if (tag.length() > TAG_MAX_LEN) {
                throw new IllegalArgumentException("too long tag, over 16");
            }
        }
    }
    
    /**
     * Check the config info.
     */
    public static void checkParam(Map<String, Object> configAdvanceInfo) throws VegetaException {
        for (Map.Entry<String, Object> configAdvanceInfoTmp : configAdvanceInfo.entrySet()) {
            if (CONFIG_TAGS.equals(configAdvanceInfoTmp.getKey())) {
                if (configAdvanceInfoTmp.getValue() != null) {
                    String[] tagArr = ((String) configAdvanceInfoTmp.getValue()).split(",");
                    if (tagArr.length > 5) {
                        throw new VegetaException(VegetaException.INVALID_PARAM, "too much config_tags, over 5");
                    }
                    for (String tag : tagArr) {
                        if (tag.length() > 64) {
                            throw new VegetaException(VegetaException.INVALID_PARAM, "too long tag, over 64");
                        }
                    }
                }
            } else if (DESC.equals(configAdvanceInfoTmp.getKey())) {
                if (configAdvanceInfoTmp.getValue() != null
                        && ((String) configAdvanceInfoTmp.getValue()).length() > 128) {
                    throw new VegetaException(VegetaException.INVALID_PARAM, "too long desc, over 128");
                }
            } else if (USE.equals(configAdvanceInfoTmp.getKey())) {
                if (configAdvanceInfoTmp.getValue() != null
                        && ((String) configAdvanceInfoTmp.getValue()).length() > 32) {
                    throw new VegetaException(VegetaException.INVALID_PARAM, "too long use, over 32");
                }
            } else if (EFFECT.equals(configAdvanceInfoTmp.getKey())) {
                if (configAdvanceInfoTmp.getValue() != null
                        && ((String) configAdvanceInfoTmp.getValue()).length() > 32) {
                    throw new VegetaException(VegetaException.INVALID_PARAM, "too long effect, over 32");
                }
            } else if (TYPE.equals(configAdvanceInfoTmp.getKey())) {
                if (configAdvanceInfoTmp.getValue() != null
                        && ((String) configAdvanceInfoTmp.getValue()).length() > 32) {
                    throw new VegetaException(VegetaException.INVALID_PARAM, "too long type, over 32");
                }
            } else if (SCHEMA.equals(configAdvanceInfoTmp.getKey())) {
                if (configAdvanceInfoTmp.getValue() != null
                        && ((String) configAdvanceInfoTmp.getValue()).length() > 32768) {
                    throw new VegetaException(VegetaException.INVALID_PARAM, "too long schema, over 32768");
                }
            } else {
                throw new VegetaException(VegetaException.INVALID_PARAM, "invalid param");
            }
        }
    }
    
    /**
     * Check the tenant.
     */
    public static void checkTenant(String tenant) {
        if (StringUtils.isNotBlank(tenant)) {
            if (!isValid(tenant.trim())) {
                throw new IllegalArgumentException("invalid tenant");
            }
            if (tenant.length() > TANANT_MAX_LEN) {
                throw new IllegalArgumentException("too long tag, over 128");
            }
        }
    }
    
}
