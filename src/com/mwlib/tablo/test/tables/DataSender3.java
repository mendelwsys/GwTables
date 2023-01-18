package com.mwlib.tablo.test.tables;

import com.mycompany.common.DiagramDesc;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 21.05.14
 * Time: 20:25
 * Контейнер для передачи данных на клиент
 */
public class DataSender3
{
    private long updateStamp;

    public int getUpdateStampN() {
        return updateStampN;
    }

    public void setUpdateStampN(int updateStampN) {
        this.updateStampN = updateStampN;
    }

    private int updateStampN;

    public long getUpdateStamp() {
        return updateStamp;
    }

    public void setUpdateStamp(long updateStamp) {
        this.updateStamp = updateStamp;
    }

    public Object[] getTuples() {
        return tuples;
    }

    public void setTuples(Object[][] tuples) {
        this.tuples = tuples;
    }


    public DiagramDesc getDesc() {
        return desc;
    }

    public void setDesc(DiagramDesc desc) {
        this.desc = desc;
    }

    private DiagramDesc desc;

    private Object[][] tuples;

}
