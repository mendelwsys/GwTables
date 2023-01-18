package com.mwlib.tablo.db;

import com.mwlib.tablo.tables.FieldTranslator;
import com.mycompany.common.FieldException;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.UtilsData;
import com.mycompany.common.tables.ColumnHeadBean;

import java.sql.Timestamp;
import java.util.*;

abstract public class BaseTableDesc {


    public boolean shouldResetData(Map<String, Object> parameters)
    {
        return false;
    }


    public static final long TZ_MILS=3*60*60*1000;

    protected String removeKeySeparator(String id) {
        if (id!=null)
        {
            int ix=id.indexOf(TablesTypes.KEY_SEPARATOR);
            if (ix>=0)
                id=id.substring(0,ix);
        }
        return id;
    }


    public boolean isTest() {
        return test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    private boolean test;
    public BaseTableDesc(boolean test)
    {
        this.test=test;
    }

    public int getHeaderHeight() {
        return headerHeight;
    }

    public void setHeaderHeight(int headerHeight) {
        this.headerHeight = headerHeight;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    protected int headerHeight;
    protected int cellHeight;

    public boolean isFixedRecordHeights() {
        return fixedRecordHeights;
    }

    public void setFixedRecordHeights(boolean fixedRecordHeights) {
        this.fixedRecordHeights = fixedRecordHeights;
    }

    protected boolean fixedRecordHeights;

    public static String getTimeInterval(Timestamp nd, long current, int m)
    {
        long min = m * (nd.getTime() - current) / 60000;
        if (min > 60)
            return " " + (min / 60) + "ч :" + min % 60 + " мин";
        else
            return " " + min + " мин";
    }

    public static Map<String,ColumnHeadBean> setMetaByArray(ColumnHeadBean[] _meta)
    {
        Map<String,ColumnHeadBean> meta=new HashMap<String,ColumnHeadBean>();
        for (int i = 0, metaLength = _meta.length; i < metaLength; i++) {
            ColumnHeadBean columnHeadBean = _meta[i];
            meta.put(columnHeadBean.getName(), columnHeadBean);
        }
        return meta;
    }

    abstract public String getTableType();
    abstract public FieldTranslator[] getFieldTranslator();
    abstract public void addMeta2Type(IMetaProvider metaProvider);

    IRowOperation rowOperation;
    public IRowOperation getRowOperation()
    {
        if (rowOperation!=null)
            return rowOperation;
        else
            return rowOperation=_getRowOperation();
    }

    abstract protected IRowOperation _getRowOperation();

    abstract public int[] getDataTypes();

    public Map<Integer, String> getNumber2Key() throws FieldException
    {
           return UtilsData.number2Key(getKey2Number());
    }
    public Map<String,Integer> getKey2Number() throws FieldException
    {
        Map<String,Integer> rv=new HashMap<String,Integer>();
        FieldTranslator[] fieldTranslator = getFieldTranslator();
        for (int i = 0, fieldTranslatorLength = fieldTranslator.length; i < fieldTranslatorLength; i++)
        {
            String name = fieldTranslator[i].getColumnHeadBean().getName();
            if (rv.containsKey(name))
                throw new FieldException("Double column name:"+name);
            rv.put(name,i);
        }
        return rv;
    }

    public ColumnHeadBean[] getMeta() throws FieldException {
        List<ColumnHeadBean> ll = new LinkedList<ColumnHeadBean>();
        FieldTranslator[] fieldTranslator = getFieldTranslator();
        for (int i = 0, fieldTranslatorLength = fieldTranslator.length; i < fieldTranslatorLength; i++) {
            FieldTranslator translator = fieldTranslator[i];
            ll.add(translator.getColumnHeadBean());
        }
        return ll.toArray(new ColumnHeadBean[ll.size()]);
    }


    public Map translateTuple(Map rawTuple, ColumnHeadBean[] rawHead, Set<String> sendMask) throws FieldException
    {

        FieldTranslator[] fieldTranslator = getFieldTranslator();
            Map<String,Object> outTuple = new HashMap<String,Object>();
            for (int i1 = 0, fieldTranslatorLength = fieldTranslator.length; i1 < fieldTranslatorLength; i1++)
            {
                FieldTranslator translator = fieldTranslator[i1];
                Object val = translator.getS(rawHead, rawTuple, outTuple);
                outTuple.put(translator.getColumnHeadBean().getName(), val);
            }
            if (!outTuple.containsKey(TablesTypes.ACTUAL))
                outTuple.put(TablesTypes.ACTUAL, 1);
//            outTuple.put("TESTDATE","2014-12-30T00:00:00Z");

            return outTuple;

//TODO        DiagramDesc desc = getDiagramDesc(resMap);
//        if (desc!=null)
//            outParams.put(TablesTypes.DIAGRAM_DESC,desc);


    }


}