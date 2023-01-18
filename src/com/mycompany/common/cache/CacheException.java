package com.mycompany.common.cache;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 22.09.14
 * Time: 18:35
 * To change this template use File | Settings | File Templates.
 */
public class CacheException extends Exception {
    public CacheException() {
    }

//    public CacheException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
//        super(message, cause, enableSuppression, writableStackTrace);
//    }

    public CacheException(Throwable cause) {
        super(cause);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheException(String message) {
        super(message);
    }
}
