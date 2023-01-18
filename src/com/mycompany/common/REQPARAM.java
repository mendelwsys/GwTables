package com.mycompany.common;


/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 30.05.15
 * Time: 14:33
 * параметр для получения провайдера
 */
public class REQPARAM
{
    String request;//Запрос к кешам бд
    String[] inTblTypes;//мно-во типов эл. событий на которые надо подписаться для получения обновлений

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String[] getInTblTypes() {
        return inTblTypes;
    }

    public void setInTblTypes(String[] inTblTypes) {
        this.inTblTypes = inTblTypes;
    }
}
