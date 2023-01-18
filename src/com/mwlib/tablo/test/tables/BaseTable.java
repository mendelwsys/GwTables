package com.mwlib.tablo.test.tables;

import com.google.gson.Gson;
import com.mwlib.utils.db.Directory;
import com.mwlib.utils.db.FillFromDb;
import com.mycompany.common.DiagramDesc;
import com.mycompany.common.FieldException;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.tables.FieldTranslator;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 17:06
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseTable {


    public static String toJson(Object obj)
    {
        String rv = new Gson().toJson(obj);
        return rv;
    }
    protected Map<String,ColumnHeadBean> meta= new HashMap<String,ColumnHeadBean>();

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

    protected Map[] map;
    protected boolean test;


    abstract public String getTableType();

    public String getFileTestName()
    {
      //return "C:\\PapaWK\\Projects\\JavaProj\\GWTProject\\"+getTableType()+".db";
       return "/dbt/"+getTableType()+".db";
    }

    public void fillTestFile(String fname)  throws Exception
    {
        Pair<ColumnHeadBean[], Map[]> pr = getTestData();
        if (fname==null)
            fname=getFileTestName();
        FileOutputStream fos = new FileOutputStream(fname);
        ObjectOutputStream obs = new ObjectOutputStream(fos);
        obs.writeObject(pr.first);
        obs.writeObject(pr.second);
        obs.close();
    }

    protected Pair<ColumnHeadBean[], Map[]> getTestData() throws Exception
    {
        return getTestData(new HashMap<String, Object>());
    }

    protected Pair<ColumnHeadBean[], Map[]> getTestData(Map<String, Object> outParams) throws Exception
    {
        Map mapParams = setFillParameters();
        return getDbTable(mapParams, outParams);
    }

    protected Map setFillParameters()
    {
        Map mapParams = new HashMap();
        mapParams.put("idT",new String[]{"0"});
        mapParams.put("DOR_COD",new Integer[]{28});
        mapParams.put("TTYPE",new String[]{getTableType()});
        mapParams.put("idN",new String[]{"0"});
        mapParams.put(FillFromDb.EV_TYPE_ATTR,new Integer[] {44});
        return mapParams;
    }


    protected BaseTable(boolean test)
    {
        this.test=test;


        try
        {
            if (!Directory.isInit())
                Directory.initDictionary(test);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            String fname=getFileTestName();
            if (test && fname!=null)
            {
                //ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fname));
                InputStream inputStream = BaseTable.class.getResourceAsStream(fname);
                if (inputStream==null)
                    throw new Exception("Can't get input stream from resource name:"+fname);

                ObjectInputStream ois = new ObjectInputStream(inputStream);

                final com.mwlib.tablo.tables.ColumnHeadBean[] _columnHeadBeans = (com.mwlib.tablo.tables.ColumnHeadBean[]) ois.readObject();
                ColumnHeadBean[] columnHeadBeans=new ColumnHeadBean[_columnHeadBeans.length];
                for (int i = 0, columnHeadBeansLength = _columnHeadBeans.length; i < columnHeadBeansLength; i++)
                {
                    com.mwlib.tablo.tables.ColumnHeadBean _columnHeadBean = _columnHeadBeans[i];
                    columnHeadBeans[i] = new ColumnHeadBean(_columnHeadBean.getTitle(),_columnHeadBean.getName(),_columnHeadBean.getType(),_columnHeadBean.isVisible(),_columnHeadBean.getLinkText(),_columnHeadBean.isAutofit());
                }


                this.meta=setMetaByArray(columnHeadBeans);
                this.map=(Map[])ois.readObject();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected abstract FieldTranslator[] getFieldTranslator();

    public ColumnHeadBean[] getMeta() throws FieldException
    {
        List<ColumnHeadBean> ll = new LinkedList<ColumnHeadBean>();
        FieldTranslator[] fieldTranslator = getFieldTranslator();


        for (int i = 0, fieldTranslatorLength = fieldTranslator.length; i < fieldTranslatorLength; i++) {
            FieldTranslator translator = fieldTranslator[i];
            ll.add(translator.getColumnHeadBean());
        }
        return ll.toArray(new ColumnHeadBean[ll.size()]);
    }

    public Map[] getRawMap()
    {
        return this.map;
    }
    public Map[] getData(Map mapParams,Map<String,Object> outParams) throws Exception
    {

        Map<String,ColumnHeadBean> meta;
        Map[] map;

        if (test)
        {
            meta=this.meta;

            if (this instanceof StripT)
            {
                int k= 1+this.map.length/20;
                map=new Map[k];
                for (int i = 0; i < map.length; i++)
                    map[i]=this.map[i];
            }
            else
                map = this.map;

            try {
                String[] params=(String[])mapParams.get(TablesTypes.ID_TM);
                if (params!=null && params.length>0 && Long.parseLong(params[0])>0)
                    map=new Map[0];
            } catch (NumberFormatException e) {
                //
            }

            if (outParams!=null)
            {
                outParams.put(TablesTypes.ID_TM,System.currentTimeMillis());
                outParams.put(TablesTypes.ID_TN,0);
            }
        }
        else
        {
            addParameters(mapParams);
            Pair<ColumnHeadBean[], Map[]> resrequest = getDbTable(mapParams, outParams);
            this.map=map=resrequest.second;
            this.meta=meta=setMetaByArray(resrequest.first);
        }

        FieldTranslator[] fieldTranslator = getFieldTranslator();

        List<Map<String,Object>> resMap= new LinkedList<Map<String,Object>>();
        for (int i = 0, mapLength = map.length; i < mapLength; i++)
        {
            Map map1 = map[i];

            Map<String,Object> _map = new HashMap<String,Object>();
            for (int i1 = 0, fieldTranslatorLength = fieldTranslator.length; i1 < fieldTranslatorLength; i1++)
            {
                FieldTranslator translator = fieldTranslator[i1];
                Collection<ColumnHeadBean> values = meta.values();
                Object val = translator.getS(values.toArray(new ColumnHeadBean[values.size()]), map1, _map);
                _map.put(translator.getColumnHeadBean().getName(), val);
            }
            _map.put(TablesTypes.ACTUAL,1);



            resMap.add(_map);
        }

        DiagramDesc desc = getDiagramDesc(resMap);
        if (desc!=null)
            outParams.put(TablesTypes.DIAGRAM_DESC,desc);
        return resMap.toArray(new Map[resMap.size()]);
    }

    protected abstract Pair<ColumnHeadBean[], Map[]> getDbTable(Map mapParams, Map<String, Object> outParams) throws Exception;


    protected abstract void addParameters(Map mapParams);

    protected DiagramDesc getDiagramDesc(List<Map<String, Object>> resMap)
    {
        return null;
    }



    protected Map<String,ColumnHeadBean> setMetaByArray(ColumnHeadBean[] _meta)
    {
        Map<String,ColumnHeadBean> meta=new HashMap<String,ColumnHeadBean>();
        for (int i = 0, metaLength = _meta.length; i < metaLength; i++) {
            ColumnHeadBean columnHeadBean = _meta[i];
            meta.put(columnHeadBean.getName(), columnHeadBean);
        }
        return meta;
    }

}
