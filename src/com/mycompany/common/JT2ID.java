package com.mycompany.common;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 21.02.15
 * Time: 21:24
 * To change this template use File | Settings | File Templates.
 */
public class JT2ID
{
    public JT2ID(String ttype, String tblid) {
        this.ttype = ttype;
        this.tblid = tblid;
    }

    public JT2ID() {
    }

    public String getTtype() {
        return ttype;
    }

    public void setTtype(String ttype) {
        this.ttype = ttype;
    }

    public String getTblid() {
        return tblid;
    }

    public void setTblid(String tblid) {
        this.tblid = tblid;
    }

    String ttype;
    String tblid;
}
