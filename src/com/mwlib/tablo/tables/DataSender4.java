package com.mwlib.tablo.tables;

import com.mycompany.common.DiagramDesc;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 21.05.14
 * Time: 20:25
 * Контейнер для передачи данных на клиент
 */
public class DataSender4
{

    public DataSender4(Object[] tuples, TransactSender trans,DiagramDesc desc) {
        this.tuples = tuples;
        this.trans = trans;
        this.desc = desc;
    }

    public Object[] getTuples() {
        return tuples;
    }

    public void setTuples(Object[] tuples) {
        this.tuples = tuples;
    }


    public DiagramDesc getDesc() {
        return desc;
    }

    public void setDesc(DiagramDesc desc)
    {
        this.desc = desc;
    }

    private DiagramDesc desc;
    private Object[] tuples;

    public TransactSender getTrans() {
        return trans;
    }

    public void setTrans(TransactSender trans) {
        this.trans = trans;
    }

    private TransactSender trans;

}
