package com.vegeta.global.http.result.exception;

/**
 * Error code enum.
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
public enum ErrorCodeEnum {

    UNKNOWN_ERROR {
        @Override
        public String getCode() {
            return "1";
        }

        @Override
        public String getMessage() {
            return "UNKNOWN_ERROR";
        }
    },

    VALIDATION_ERROR {
        @Override
        public String getCode() {
            return "2";
        }

        @Override
        public String getMessage() {
            return "VALIDATION_ERROR";
        }
    },

    SERVICE_ERROR {
        @Override
        public String getCode() {
            return "3";
        }

        @Override
        public String getMessage() {
            return "SERVICE_ERROR";
        }
    },

    NOT_FOUND {
        @Override
        public String getCode() {
            return "404";
        }

        @Override
        public String getMessage() {
            return "NOT_FOUND";
        }
    };

    public abstract String getCode();

    public abstract String getMessage();

}
