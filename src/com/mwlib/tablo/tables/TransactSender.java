package com.mwlib.tablo.tables;

import com.mycompany.common.DiagramDesc;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 21.05.14
 * Time: 20:25
 * Контейнер для передачи данных на клиент
 */
public class TransactSender
{

    public long getLastUpdateStamp() {
        return lastUpdateStamp;
    }

    public void setLastUpdateStamp(long lastUpdateDate) {
        this.lastUpdateStamp = lastUpdateDate;
    }

    private long lastUpdateStamp;


//    public TransactSender(DataSender2 sender2) {
//        this.TESTDATE = sender2.getTESTDATE();
//        this.updateStamp = sender2.getUpdateStamp();
//        this.cliCnt = sender2.getCliCnt();
//        this.tblId = sender2.getTblId();
//        this.updateStampN = sender2.getUpdateStampN();
//    }

    public TransactSender(int cliCnt, String tblId,long lastUpdateStamp) {
        this.cliCnt = cliCnt;
        this.tblId = tblId;
        this.lastUpdateStamp=lastUpdateStamp;
    }

    public String getTESTDATE() {
        return TESTDATE;
    }

    public void setTESTDATE(String TESTDATE) {
        this.TESTDATE = TESTDATE;
    }

    //private String TESTDATE="2012-04-23T18:25:43.511Z";
    private String TESTDATE="2014-12-30T00:00:00Z";

    private long updateStamp;
    private int cliCnt;

    public void setTblId(String tblId) {
        this.tblId = tblId;
    }

    public String getTblId() {
        return tblId;
    }

    private String tblId;

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

    public void setCliCnt(int cliCnt) {
        this.cliCnt = cliCnt;
    }
}
