package com.xbk.spring.web.validate;

import com.xbk.core.global.exception.BizException;
import com.xbk.core.global.result.ResponseCommon;
import lombok.experimental.UtilityClass;
import org.hibernate.validator.HibernateValidator;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.Set;

/**
 * 验证工具
 */
@UtilityClass
public class ValidateUtil {

    /**
     * 声明全局验证器
     */
    private static Validator validator;

    static {
        validator = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(true)
                .buildValidatorFactory()
                .getValidator();
    }

    /**
     * 把当前验证器托管给 spring
     */
    public static Validator getDefaultValidator() {
        return validator;
    }

    /**
     * 验证当前对象所有字段
     *
     * @param object 待验证对象
     */
    public static <T> void validateEntity(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, Default.class);
        handleValidateResult(violations);
    }

    /**
     * 验证对象字段
     *
     * @param object    待验证对象
     * @param fieldName 字段名称
     */
    public static <T> void validateField(T object, String fieldName) {
        Set<ConstraintViolation<T>> violations = validator.validateProperty(object, fieldName, Default.class);
        handleValidateResult(violations);
    }

    /**
     * 验证后置处理
     *
     * @param constraintViolationSet 当前验证结果
     */
    private static <T> void handleValidateResult(Set<ConstraintViolation<T>> constraintViolationSet) {
        if (!CollectionUtils.isEmpty(constraintViolationSet)) {
            ConstraintViolation<?> violation = constraintViolationSet.stream().findFirst().get();
            String message = violation.getMessage();
            throw BizException.builder().code(ResponseCommon.INVALID_ARGUMENTS.getCode()).message(message).build();
        }
    }
}
