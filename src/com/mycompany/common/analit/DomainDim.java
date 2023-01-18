package com.mycompany.common.analit;


import com.smartgwt.client.types.ListGridFieldType;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 21.10.14
 * Time: 12:34
 * Область определения измерения ()
 */
public class DomainDim
{
    public DomainDim(Object val, String title, int ord) {
        this.val = val;
        this.title = title;
        this.ord = ord;
    }

    Object val;
    String title;
    int ord;
}
