package com.mwlib.tablo.test.tables;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 16.05.14
 * Time: 12:41
 * прототип тапла
 */
public class TupleBean
{
    static public int ix=0;

    int id;


    public int getActual() {
        return actual;
    }

    public void setActual(int actual) {
        this.actual = actual;
    }

    int actual;

    public String getCol1() {
        return col1;
    }

    public void setCol1(String col1) {
        this.col1 = col1;
    }

    String col1;
    String col2;

    public TupleBean()
    {
    }

    public TupleBean(int id, int actual, String col1, String col2) {
        this.id = id;
        this.actual = actual;
        this.col1 = col1;
        this.col2 = col2;
    }

    public TupleBean(int id,String col1, String col2)
    {
        this.id = id;
        this.col1 = col1;
        this.col2 = col2;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCol2() {
        return col2;
    }

    public void setCol2(String col2) {
        this.col2 = col2;
    }
}
