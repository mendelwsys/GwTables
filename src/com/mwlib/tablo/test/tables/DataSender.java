package com.mwlib.tablo.test.tables;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 21.05.14
 * Time: 20:25
 * To change this template use File | Settings | File Templates.
 */
public class DataSender
{
    private long updateStamp;

    public long getUpdateStamp() {
        return updateStamp;
    }

    public void setUpdateStamp(long updateStamp) {
        this.updateStamp = updateStamp;
    }

    public TupleBean[] getTuples() {
        return tuples;
    }

    public void setTuples(TupleBean[] tuples) {
        this.tuples = tuples;
    }

    private TupleBean[] tuples;

}
