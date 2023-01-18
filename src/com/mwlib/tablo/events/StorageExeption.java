package com.mwlib.tablo.events;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 02.09.14
 * Time: 15:47
 * To change this template use File | Settings | File Templates.
 */
public class StorageExeption extends Exception{
    public StorageExeption() {
    }

    public StorageExeption(String message) {
        super(message);
    }

    public StorageExeption(String message, Throwable cause) {
        super(message, cause);
    }

    public StorageExeption(Throwable cause) {
        super(cause);
    }

    public StorageExeption(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
