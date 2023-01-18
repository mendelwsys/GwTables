package com.mwlib.tablo.cache;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 25.09.14
 * Time: 19:07
 * To change this template use File | Settings | File Templates.
 */
public class WrongParam extends Exception{
    public WrongParam() {
    }

    public WrongParam(String message) {
        super(message);
    }

    public WrongParam(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongParam(Throwable cause) {
        super(cause);
    }

    public WrongParam(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
