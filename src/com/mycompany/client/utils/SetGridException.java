package com.mycompany.client.utils;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 04.09.14
 * Time: 12:22
 * Исключение при установке грида
 */
public class SetGridException extends Exception
{

    public SetGridException() {
    }

//    public SetGridException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
//        super(message, cause, enableSuppression, writableStackTrace);
//    }

    public SetGridException(Throwable cause) {
        super(cause);
    }

    public SetGridException(String message, Throwable cause) {
        super(message, cause);
    }

    public SetGridException(String message) {
        super(message);
    }
}
