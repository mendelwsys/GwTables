package com.mwlib.utils.db;

import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.cache.CacheException;
import com.mycompany.common.cache.SimpleKeyGenerator;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.cache.ICache;
import com.mwlib.tablo.cache.ICacheFactory;
import com.mwlib.tablo.db.DbUtils;
import com.mwlib.tablo.derby.DerbyCache;
import com.smartgwt.client.types.ListGridFieldType;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 18.07.14
 * Time: 14:04
 * Справочники используемые системой
 * com.mwlib.utils.db.Directory
 */
public class Directory
{


    public static class StanDesc  implements Serializable
    {

        private static final long serialVersionUID = -6332537910472639001L;

        private Integer STAN_ID;
        private Integer DOR_KOD;
        private Integer PRED_ID;
        private Integer ST_KOD;
        private String VNAME;
        private String NAME;
        private Integer STAN_TIP_ID;

        public Integer getSTAN_ID() {
            return STAN_ID;
        }

        public void setSTAN_ID(Integer STAN_ID) {
            this.STAN_ID = STAN_ID;
        }

        public Integer getDOR_KOD() {
            return DOR_KOD;
        }

        public void setDOR_KOD(Integer DOR_KOD) {
            this.DOR_KOD = DOR_KOD;
        }

        public Integer getPRED_ID() {
            return PRED_ID;
        }

        public void setPRED_ID(Integer PRED_ID) {
            this.PRED_ID = PRED_ID;
        }

        public Integer getST_KOD() {
            return ST_KOD;
        }

        public void setST_KOD(Integer ST_KOD) {
            this.ST_KOD = ST_KOD;
        }

        public String getVNAME() {
            return VNAME;
        }

        public void setVNAME(String VNAME) {
            this.VNAME = VNAME;
        }

        public String getNAME() {
            return NAME;
        }

        public void setNAME(String NAME) {
            this.NAME = NAME;
        }

        public Integer getSTAN_TIP_ID() {
            return STAN_TIP_ID;
        }

        public void setSTAN_TIP_ID(Integer STAN_TIP_ID) {
            this.STAN_TIP_ID = STAN_TIP_ID;
        }
    }

    public static class RailRoad   implements Serializable
    {
        private static final long serialVersionUID = -6332537910472639010L;
        public Integer getDOR_KOD() {
            return DOR_KOD;
        }

        public void setDOR_KOD(Integer DOR_KOD) {
            this.DOR_KOD = DOR_KOD;
        }

        public Integer getADM_KOD() {
            return ADM_KOD;
        }

        public void setADM_KOD(Integer ADM_KOD) {
            this.ADM_KOD = ADM_KOD;
        }

        public String getNAME() {
            return NAME;
        }

        public void setNAME(String NAME) {
            this.NAME = NAME;
        }

        public String getSNAME() {
            return SNAME;
        }

        public void setSNAME(String SNAME) {
            this.SNAME = SNAME;
        }

        Integer DOR_KOD;
        Integer ADM_KOD;
        String NAME;
        String SNAME;
    }

    public static class Pred    implements Serializable
    {
        private static final long serialVersionUID = -6332537910472639020L;

        public Integer getPRED_ID() {
            return PRED_ID;
        }

        public void setPRED_ID(Integer PRED_ID) {
            this.PRED_ID = PRED_ID;
        }

        public Integer getVD_ID() {
            return VD_ID;
        }

        public void setVD_ID(Integer VD_ID) {
            this.VD_ID = VD_ID;
        }

        public String getVNAME() {
            return VNAME;
        }

        public void setVNAME(String VNAME) {
            this.VNAME = VNAME;
        }

        public String getNAME() {
            return NAME;
        }

        public void setNAME(String NAME) {
            this.NAME = NAME;
        }

        public String getSNAME() {
            return SNAME;
        }

        public void setSNAME(String SNAME) {
            this.SNAME = SNAME;
        }

        Integer PRED_ID;
        Integer VD_ID;

        public Integer getDOR_KOD() {
            return DOR_KOD;
        }

        public void setDOR_KOD(Integer DOR_KOD) {
            this.DOR_KOD = DOR_KOD;
        }

        Integer DOR_KOD;
        String VNAME;
        String NAME;
        String SNAME;

        public Integer getGR_ID() {
            return GR_ID;
        }

        public void setGR_ID(Integer GR_ID) {
            this.GR_ID = GR_ID;
        }

        Integer GR_ID;
    }

    public static class UkGR    implements Serializable
    {
        private static final long serialVersionUID = -6332537910472639030L;

        Integer GR_ID;

        public UkGR(){}
        public UkGR(int gr_id)
        {
            this.GR_ID=gr_id;
        }
        public Integer getGR_ID() {
            return GR_ID;
        }

        public void setGR_ID(Integer GR_ID) {
            this.GR_ID = GR_ID;
        }

        public Integer getGR_KOD() {
            return GR_KOD;
        }

        public void setGR_KOD(Integer GR_KOD) {
            this.GR_KOD = GR_KOD;
        }

        public String getNAME() {
            return NAME;
        }

        public void setNAME(String NAME) {
            this.NAME = NAME;
        }

        public String getSNAME() {
            return SNAME;
        }

        public void setSNAME(String SNAME) {
            this.SNAME = SNAME;
        }

        public String getOPR() {
            return OPR;
        }

        public void setOPR(String OPR) {
            this.OPR = OPR;
        }

        Integer GR_KOD;
        String NAME;
        String SNAME;
        String OPR;

    }

    public static class MarkColor implements Serializable
    {
        private static final long serialVersionUID = -6332537910472639040L;
        public BigDecimal getCOLOR_ID() {
            return COLOR_ID;
        }

        public void setCOLOR_ID(BigDecimal COLOR_ID) {
            this.COLOR_ID = COLOR_ID;
        }

        public String getNAME() {
            return NAME;
        }

        public void setNAME(String NAME) {
            this.NAME = NAME;
        }

        BigDecimal COLOR_ID;
        String NAME;


    }

    public static class Vid    implements Serializable
    {
        private static final long serialVersionUID = -6332537910472639050L;
        public Integer getVD_ID() {
            return VD_ID;
        }

        public void setVD_ID(Integer VD_ID) {
            this.VD_ID = VD_ID;
        }


        Integer VD_ID;

        public Integer getVD_KOD() {
            return VD_KOD;
        }

        public void setVD_KOD(Integer VD_KOD) {
            this.VD_KOD = VD_KOD;
        }

        public Integer getPVD_KOD() {
            return PVD_KOD;
        }

        public void setPVD_KOD(Integer PVD_KOD) {
            this.PVD_KOD = PVD_KOD;
        }

        public String getNAME() {
            return NAME;
        }

        public void setNAME(String NAME) {
            this.NAME = NAME;
        }

        public String getSNAME() {
            return SNAME;
        }

        public void setSNAME(String SNAME) {
            this.SNAME = SNAME;
        }

        public String getOPR() {
            return OPR;
        }

        public void setOPR(String OPR) {
            this.OPR = OPR;
        }

        Integer VD_KOD;
        Integer PVD_KOD;
        String NAME;
        String SNAME;
        String OPR;
    }

    public static class Polg implements Serializable
    {
        private static final long serialVersionUID = -6332537910472639060L;
        public Integer getOPER_ID() {
            return OPER_ID;
        }

        public void setOPER_ID(Integer OPER_ID) {
            this.OPER_ID = OPER_ID;
        }

        public Integer getREPL_FL() {
            return REPL_FL;
        }

        public void setREPL_FL(Integer REPL_FL) {
            this.REPL_FL = REPL_FL;
        }

        public Integer getPOLG_TYPE() {
            return POLG_TYPE;
        }

        public void setPOLG_TYPE(Integer POLG_TYPE) {
            this.POLG_TYPE = POLG_TYPE;
        }

        public Integer getPOLG_ID() {
            return POLG_ID;
        }

        public void setPOLG_ID(Integer POLG_ID) {
            this.POLG_ID = POLG_ID;
        }

        public String getNAME() {
            return NAME;
        }

        public void setNAME(String NAME) {
            this.NAME = NAME;
        }

        public String getKOMM() {
            return KOMM;
        }

        public void setKOMM(String KOMM) {
            this.KOMM = KOMM;
        }

        public Integer getBOOL_STAN() {
            return BOOL_STAN;
        }

        public void setBOOL_STAN(Integer BOOL_STAN) {
            this.BOOL_STAN = BOOL_STAN;
        }

        public Integer getBOOL_LINE() {
            return BOOL_LINE;
        }

        public void setBOOL_LINE(Integer BOOL_LINE) {
            this.BOOL_LINE = BOOL_LINE;
        }

        public Integer getBOOL_PUTGL() {
            return BOOL_PUTGL;
        }

        public void setBOOL_PUTGL(Integer BOOL_PUTGL) {
            this.BOOL_PUTGL = BOOL_PUTGL;
        }

        public Integer getPOLG_GR_ID() {
            return POLG_GR_ID;
        }

        public void setPOLG_GR_ID(Integer POLG_GR_ID) {
            this.POLG_GR_ID = POLG_GR_ID;
        }

        public Integer getNUM() {
            return NUM;
        }

        public void setNUM(Integer NUM) {
            this.NUM = NUM;
        }

        Integer OPER_ID;
          Integer REPL_FL;
          Integer POLG_TYPE;
          Integer POLG_ID;
          String NAME;
          String KOMM;

          Integer BOOL_STAN;
          Integer BOOL_LINE;
          Integer BOOL_PUTGL;
          Integer POLG_GR_ID;
          Integer NUM;
    }

    public static class Polg_Obj implements Serializable
    {
        private static final long serialVersionUID = -6332537910472639070L;
        Integer POLG_TYPE;

        public Integer getPOLG_TYPE() {
            return POLG_TYPE;
        }

        public void setPOLG_TYPE(Integer POLG_TYPE) {
            this.POLG_TYPE = POLG_TYPE;
        }

        public Integer getPOLG_ID() {
            return POLG_ID;
        }

        public void setPOLG_ID(Integer POLG_ID) {
            this.POLG_ID = POLG_ID;
        }

        public Integer getOBJ_OSN_ID() {
            return OBJ_OSN_ID;
        }

        public void setOBJ_OSN_ID(Integer OBJ_OSN_ID) {
            this.OBJ_OSN_ID = OBJ_OSN_ID;
        }

        public Integer getNUM() {
            return NUM;
        }

        public void setNUM(Integer NUM) {
            this.NUM = NUM;
        }

        public Integer getOPER_ID() {
            return OPER_ID;
        }

        public void setOPER_ID(Integer OPER_ID) {
            this.OPER_ID = OPER_ID;
        }

        public Integer getREPL_FL() {
            return REPL_FL;
        }

        public void setREPL_FL(Integer REPL_FL) {
            this.REPL_FL = REPL_FL;
        }

        Integer POLG_ID;
        Integer OBJ_OSN_ID;
        Integer NUM;
        Integer OPER_ID;
        Integer REPL_FL;

    }

    public static class TabloPolg implements Serializable
    {
        private static final long serialVersionUID = -6332537910472639080L;

          Integer DOR_KOD;
          Integer NUM;
          Integer POLG_TYPE;
          Integer POLG_ID;

        public Integer getDOR_KOD() {
            return DOR_KOD;
        }

        public void setDOR_KOD(BigDecimal DOR_KOD) {
            this.DOR_KOD = DOR_KOD.intValue();
        }

        public String getSTYLE() {
            return STYLE;
        }

        public void setSTYLE(String STYLE) {
            this.STYLE = STYLE;
        }

        public String getNAME() {
            return NAME;
        }

        public void setNAME(String NAME) {
            this.NAME = NAME;
        }

        public String getROLES() {
            return ROLES;
        }

        public void setROLES(String ROLES) {
            this.ROLES = ROLES;
        }

        String NAME;
          String ROLES;
          String STYLE;

        public Integer getNUM() {
            return NUM;
        }

        public void setNUM(BigDecimal NUM) {
            this.NUM = NUM.intValue();
        }

        public Integer getPOLG_TYPE() {
            return POLG_TYPE;
        }

        public void setPOLG_TYPE(BigDecimal POLG_TYPE) {
            this.POLG_TYPE = POLG_TYPE.intValue();
        }

        public Integer getPOLG_ID() {
            return POLG_ID;
        }

        public void setPOLG_ID(BigDecimal POLG_ID) {
            this.POLG_ID = POLG_ID.intValue();
        }
    }

    public static class DirectoryTableDesc implements Serializable
    {
        private static final long serialVersionUID = -6332537910472639090L;

        public DirectoryTableDesc() {
        }

        public DirectoryTableDesc(String tblName, String[] keyCols, Class[] types, Pair<String[], Map[]> content) {
            this.tblName = tblName;
            this.keyCols = keyCols;
            this.types = types;
            this.content = content;
        }

        public String getTblName() {
            return tblName;
        }

        public void setTblName(String tblName) {
            this.tblName = tblName;
        }

        public String[] getKeyCols() {
            return keyCols;
        }

        public void setKeyCols(String[] keyCols) {
            this.keyCols = keyCols;
        }

        public Class[] getTypes() {
            return types;
        }

        public void setTypes(Class[] types) {
            this.types = types;
        }

        public Pair<String[], Map[]> getContent() {
            return content;
        }

        public void setContent(Pair<String[], Map[]> content) {
            this.content = content;
        }

        String tblName;
        String[] keyCols;
        Class[] types;
        Pair<String[], Map[]> content;
    }


    public static void add2Table(ICacheFactory factory,DirectoryTableDesc tableDesc) throws SQLException, CacheException
    {
        final Pair<String[], Map[]> rv = tableDesc.getContent();
        String[] headers = rv.first;
        Class[] types = tableDesc.getTypes();

        ColumnHeadBean[] colHeaders;
        String[] cols = tableDesc.getKeyCols();
        if (cols!=null)
        {
            colHeaders = new ColumnHeadBean[headers.length];
            for (int i = 0, firstLength = headers.length; i < firstLength; i++)
                colHeaders[i]=new ColumnHeadBean(headers[i],headers[i], DbUtils.translate2AttrType(types[i]),i);
        }
        else
        {
            colHeaders = new ColumnHeadBean[headers.length+1];
            for (int i = 0, firstLength = headers.length; i < firstLength; i++)
                colHeaders[i]=new ColumnHeadBean(headers[i],headers[i], DbUtils.translate2AttrType(types[i]),i);
            colHeaders[colHeaders.length-1]=new ColumnHeadBean(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.INTEGER.toString(),colHeaders.length-1);
            for (int i = 0; i < rv.second.length; i++)
                rv.second[i].put(TablesTypes.KEY_FNAME,i);
            cols=new String[]{TablesTypes.KEY_FNAME};
        }

        Map<String, Object> params = new HashMap<String,Object>();
        params.put(ICache.CACHENAME,tableDesc.getTblName());


        ICache cache = factory.createCache(params);
        cache.setMeta(colHeaders);
        cache.setKeyGenerator(new SimpleKeyGenerator(cols, cache));

        cache.update(rv.second,true);
    }


    public static Map<Integer,StanDesc> stid2stan= new HashMap<Integer, StanDesc>();
    public static Map<Integer,Integer> code2stanid = new HashMap<Integer,Integer>();

    public static Map<Integer,RailRoad> dorCode2Rail = new HashMap<Integer,RailRoad>();
    public static Map<Integer,Pred> predId2Pred = new HashMap<Integer,Pred>();
    public static Map<Integer,Vid> vidId2Vid = new HashMap<Integer,Vid>();
    public static Map<Integer,UkGR> grId2UkGR = new HashMap<Integer,UkGR>();
    public static Map<BigDecimal, MarkColor> markColor2colorName = new HashMap<BigDecimal, MarkColor>();

//    public static Map<Integer,Polg> polgId2Polg = new HashMap<Integer,Polg>();
//    public static Map<Integer,Polg_Obj> obj_osn_id2Polg_Obj = new HashMap<Integer,Polg_Obj>();
//    public static Map<Integer,Map<Integer,TabloPolg>> dorkod2num2tabloPolg = new LinkedHashMap<Integer,Map<Integer,TabloPolg>>();

    public static StanDesc getByStanId(int stanId) {
        return stid2stan.get(stanId);
    }

    public static MarkColor getByMarkColorId(BigDecimal stanId)
    {

        return markColor2colorName.get(stanId);
    }

    public static StanDesc getByStanCode(int stancode)
    {
        Integer stanId= code2stanid.get(stancode);
        if (stanId!=null)
            return stid2stan.get(stanId);
        return null;
    }

    public static RailRoad getByDorCode(int dorCode)
    {
        return dorCode2Rail.get(dorCode);
    }

    public static Pred getByPredId(int predId)
    {
        return predId2Pred.get(predId);
    }

    public static Vid getByVidCode(int vid_code)
    {
        return vidId2Vid.get(vid_code);
    }

    public static UkGR getByGrId(int gr_id)
    {
        UkGR ukGR = grId2UkGR.get(gr_id);
        if (test && ukGR==null)
        {
            return new UkGR(gr_id);
        }
        return ukGR;
    }


    public static boolean isInit()
    {
        return isinit;
    }

    private volatile static boolean isinit=false;
    static boolean  test= false;


   private static DirectoryTableDesc[] descs= new DirectoryTableDesc[3];

    public synchronized static void initDictionary(boolean _test) throws Exception
    {
        initDictionary(_test,null);
    }

    public synchronized static void initDictionary(boolean test,final String dsCacheName) throws Exception
    {


//        boolean  test=true;//TODO На сервере Нет доступа к ЦНСИ!!!!

        if (isinit)
            return;

        Directory.test=test;
        loadFromFile();

        if (!test)
            reloadFromDB2();


        if (!test)
        { //Если боевое применение, тогда развернем в рабочей БД справочники, если применение тестовое, тогда у нас в кеше все уже развернуто.

            for (int i = 0, descsLength = descs.length; i < descsLength; i++)
            {
                Directory.add2Table(new ICacheFactory()
                {
                    @Override
                    public ICache createCache(Map<String, Object> params)
                    {
                        if (dsCacheName!=null)
                            params.put(TablesTypes.DS_CACHE_NAME,dsCacheName);
                        String tblName = (String) params.get(ICache.CACHENAME);
                        if ("POLG_OBJ".equalsIgnoreCase(tblName))
                        {
                            return new DerbyCache(params)
                            {
                                protected String[] getIXColNames()
                                {
                                    return new String[]{TablesTypes.POLG_ID,TablesTypes.OBJ_OSN_ID};
                                }
                            };
                        }
                        return new DerbyCache(params)
                        {
                            protected String[] getIXColNames()
                            {
                                return new String[]{};
                            }
                        };
                    }
                },descs[i]);
            }

            Connection conn=null;
            try
            {
                conn = DbUtil.getConnection2(DbUtil.DS_ORA_NAME);
                {
                    Collection<MarkColor> reqlist = new LinkedList<MarkColor>();
                    DaoUtils.fillObjectCollection(conn, reqlist, MarkColor.class, "select * from ICG0.MARK_COLOR");

                    markColor2colorName.clear();//Если БД доступна сбрасываем то что было в тестовых данных

                    for (MarkColor markColor : reqlist)
                        markColor2colorName.put(markColor.COLOR_ID, markColor);
                    System.out.println(" got MARK_COLOR "+markColor2colorName.size());
                }

                {

                    final String tblName = "TABLO_POLG_LIST";
                    final String[] keyCols = {"DOR_KOD", "POLG_TYPE", "POLG_ID"};
                    final String queryToRun = "select * from ICG0.TABLO_POLG_LIST order by DOR_KOD,NUM";
                    copyTable2Cache(conn, dsCacheName, tblName, keyCols, queryToRun);
                }

    //            {
    //                Collection<TabloPolg> reqlist = new LinkedList<TabloPolg>();
    //                DaoUtils.fillObjectCollection(conn, reqlist, TabloPolg.class, "select * from ICG0.TABLO_POLG_LIST order by DOR_KOD,NUM");
    //
    //                dorkod2num2tabloPolg.clear();//Если БД доступна сбрасываем то что было в тестовых данных
    //
    //                for (TabloPolg tabloPolg : reqlist)
    //                {
    //
    //                    Map<Integer, TabloPolg> num2TabloPolg = dorkod2num2tabloPolg.get(tabloPolg.DOR_KOD);
    //                    if (num2TabloPolg==null)
    //                        dorkod2num2tabloPolg.put(tabloPolg.DOR_KOD,num2TabloPolg=new LinkedHashMap<Integer,TabloPolg>());
    //                    num2TabloPolg.put(tabloPolg.NUM,tabloPolg);
    //                }
    //                if (test)
    //                    System.out.println(" got TABLO_POLG " + dorkod2num2tabloPolg.size());
    //            }

            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
            finally
            {
                DbUtil.closeConnection(conn);
            }
        }

        isinit=true;
    }

    private static void loadFromFile() throws Exception {
        InputStream inputStream = Directory.class.getResourceAsStream(fileTestName);
        if (inputStream==null)
            throw new Exception("Can't get input stream from resource name:"+fileTestName);
        ObjectInputStream ois = new ObjectInputStream(inputStream);


        stid2stan= (Map<Integer, StanDesc>) ois.readObject();
        code2stanid= (Map<Integer, Integer>) ois.readObject();
        dorCode2Rail= (Map<Integer, RailRoad>) ois.readObject();
        predId2Pred= (Map<Integer, Pred>) ois.readObject();
        vidId2Vid= (Map<Integer, Vid>) ois.readObject();
        grId2UkGR= (Map<Integer, UkGR>) ois.readObject();

        markColor2colorName=(Map<BigDecimal, MarkColor>) ois.readObject();//TODO Необходимо для автономного запуска без оракловой БД.
//            polgId2Polg=(Map<Integer, Polg>) ois.readObject();
//            obj_osn_id2Polg_Obj=(Map<Integer, Polg_Obj>) ois.readObject();

        for (int i = 0, descsLength = descs.length; i < descsLength; i++)
            descs[i]= (DirectoryTableDesc) ois.readObject();

//            dorkod2num2tabloPolg= (Map<Integer, Map<Integer, TabloPolg>>) ois.readObject();//TODO Необходимо для автономного запуска без оракловой БД.
/*
            grId2UkGR.put(25,new UkGR(25));
            grId2UkGR.put(28,new UkGR(28));
            grId2UkGR.put(70,new UkGR(70));
            grId2UkGR.put(44,new UkGR(44));
            grId2UkGR.put(40,new UkGR(40));
*/
        ois.close();
    }

    private static boolean reloadFromDB2() {
        Connection conn = null;
        try {
            conn = DbUtil.getConnection2(DbUtil.DS_DB2_NAME2);
//                conn = DbUtil.getConnection2(DbUtil.DS_DB2_NAME);

            if (conn!=null)
                System.out.println(" got connection to "+DbUtil.DS_DB2_NAME2);
            else
            {
                System.out.println(" error got connection "+DbUtil.DS_DB2_NAME2);
                return true;
            }

            stid2stan.clear();
            dorCode2Rail.clear();
            predId2Pred.clear();
            vidId2Vid.clear();
            grId2UkGR.clear();

//                {
//                    Collection<Polg> reqlist = new LinkedList<Polg>();
//                    DaoUtils.fillObjectCollection(conn, reqlist, Polg.class, "select * from IC00.POLG where COR_TIP in ('I','U')");
//                    for (Polg polg : reqlist)
//                        polgId2Polg.put(polg.POLG_ID, polg);
//                    System.out.println(" got POLG "+polgId2Polg.size());
//                }
//
//                {
//                    Collection<Polg_Obj> reqlist = new LinkedList<Polg_Obj>();
//                    DaoUtils.fillObjectCollection(conn, reqlist, Polg_Obj.class, "select * from IC00.POLG_OBJ where COR_TIP in ('I','U')");
//                    for (Polg_Obj polg_obj : reqlist)
//                        obj_osn_id2Polg_Obj.put(polg_obj.OBJ_OSN_ID, polg_obj);
//                    System.out.println(" got POLG_OBJ "+obj_osn_id2Polg_Obj.size());
//                }

            {
                Collection<StanDesc> reqlist = new LinkedList<StanDesc>();
                DaoUtils.fillObjectCollection(conn, reqlist, StanDesc.class, "select * from IC00.STAN where COR_TIP in ('I','U')");
                for (StanDesc stanDesc : reqlist) {
                    stid2stan.put(stanDesc.STAN_ID, stanDesc);
                    code2stanid.put(stanDesc.ST_KOD, stanDesc.STAN_ID);
                }
                System.out.println(" got stan "+stid2stan.size());

            }

            {
                Collection<RailRoad> reqlist = new LinkedList<RailRoad>();
                DaoUtils.fillObjectCollection(conn, reqlist, RailRoad.class, "select * from IC00.DOR where COR_TIP in ('I','U')");
                for (RailRoad railRoad : reqlist)
                    dorCode2Rail.put(railRoad.DOR_KOD, railRoad);
                System.out.println(" got dor "+dorCode2Rail.size());
            }


            {
                Collection<Pred> reqlist = new LinkedList<Pred>();
                DaoUtils.fillObjectCollection(conn, reqlist, Pred.class, "select * from IC00.PRED where COR_TIP in ('I','U')");
                for (Pred pred : reqlist)
                    predId2Pred.put(pred.PRED_ID, pred);
                System.out.println(" got pred "+predId2Pred.size());
            }

            {
                Collection<Vid> reqlist = new LinkedList<Vid>();
                DaoUtils.fillObjectCollection(conn, reqlist, Vid.class, "select * from IC00.VIDDEJ where COR_TIP in ('I','U')");
                for (Vid vid : reqlist)
                    vidId2Vid.put(vid.VD_ID, vid);
                System.out.println(" got viddej "+vidId2Vid.size());
            }


            {
                Collection<UkGR> reqlist = new LinkedList<UkGR>();
                DaoUtils.fillObjectCollection(conn, reqlist, UkGR.class, "select * from IC00.UKGR where COR_TIP in ('I','U')");
                for (UkGR ukGR : reqlist)
                    grId2UkGR.put(ukGR.GR_ID, ukGR);
                System.out.println(" got ukgr "+grId2UkGR.size());
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        finally
        {
            DbUtil.closeConnection(conn);
        }
        return false;
    }

    private static void copyTable2Cache(Connection conn, final String dsCacheName, final String tblName, final String[] keyCols, final String queryToRun) throws SQLException
    {
        final DaoUtils.ResultProcessor processor = new DaoUtils.ResultProcessor()
        {
            @Override
            public void processResult(ResultSet result) throws SQLException {
                try
                {
                    Map<Class, Class> map = new HashMap<Class, Class>();
                    map.put(BigDecimal.class,Integer.class);
                    DirectoryTableDesc desc = getDirectoryTableByResultSet(result,map, tblName, keyCols);

                    Directory.add2Table(new ICacheFactory()
                    {
                        @Override
                        public ICache createCache(Map<String, Object> params)
                        {
                            if (dsCacheName!=null)
                                params.put(TablesTypes.DS_CACHE_NAME,dsCacheName);
                            return new DerbyCache(params)
                            {
                                protected String[] getIXColNames()
                                {
                                    return new String[]{TablesTypes.POLG_ID};
                                }
                            };
                        }
                    },desc);
                }
                catch (ClassNotFoundException e)
                {
                    throw new SQLException(e);
                } catch (CacheException cacheExcpetion) {
                    throw new SQLException(cacheExcpetion);
                }
            }
            @Override
            public void processConn(Connection con) throws SQLException {
            }
        };
        DaoUtils.query(conn,queryToRun, processor);
    }

    public static DirectoryTableDesc getDirectoryTableByResultSet(ResultSet result,String tblName, String[] keyCols) throws SQLException, ClassNotFoundException
    {
        return getDirectoryTableByResultSet(result,new HashMap<Class,Class>(), tblName, keyCols);
    }

    public static DirectoryTableDesc getDirectoryTableByResultSet(ResultSet result,Map<Class,Class> replaceTypes, String tblName, String[] keyCols) throws SQLException, ClassNotFoundException
    {
        ResultSetMetaData meta = result.getMetaData();
        int colCount=meta.getColumnCount();
        Class[] types=new Class[colCount];
        String[] headers=new String[colCount];
        for (int i = 1; i <= colCount; i++)
        {
            final Class<?> aClass = Class.forName(meta.getColumnClassName(i));
            if (replaceTypes.containsKey(aClass))
                types[i-1]= replaceTypes.get(aClass);
            else
                types[i-1]= aClass;
            headers[i-1]=meta.getColumnName(i);
        }

        List<Map> tuples=new LinkedList<>();
        while (result.next())
        {
            Map<String, Object> tuple = new HashMap<String, Object>();
            for (int i = 1; i <= colCount; i++)
                tuple.put(headers[i-1],result.getObject(i));
            tuples.add(tuple);
        }
        return new DirectoryTableDesc(tblName, keyCols,types,new Pair<String[],Map[]>(headers,tuples.toArray(new Map[tuples.size()])));
    }




    static String fileTestName="/dbt/dictionary.db";


    public static void main(String[] args) throws Exception
    {
//Загружаем то что есть в файле словарей

        loadFromFile();

        if (reloadFromDB2()) return;

        if (args!=null && args.length>0)
            fileTestName=args[0]+fileTestName;
//Сохраняем в файл обновленные данные
        FileOutputStream fos = new FileOutputStream(fileTestName);
        ObjectOutputStream obs = new ObjectOutputStream(fos);
        obs.writeObject(stid2stan);
        obs.writeObject(code2stanid);
        obs.writeObject(dorCode2Rail);
        obs.writeObject(predId2Pred);
        obs.writeObject(vidId2Vid);
        obs.writeObject(grId2UkGR);
        obs.writeObject(markColor2colorName);
        for (DirectoryTableDesc desc : descs)
            obs.writeObject(desc);
        obs.close();



    }


    public static void _main(String[] args) throws Exception
    {
        initDictionary(true);
        if (args!=null && args.length>0)
            fileTestName=args[0]+fileTestName;


        for (DirectoryTableDesc desc : descs)
        {
            Pair<String[], Map[]> cont = desc.getContent();
            LinkedList<Map> ll = new LinkedList(Arrays.asList(cont.second));
            for (int i = 0; i < ll.size();)
            {
                Map map = ll.get(i);
                if ("D".equals(map.get("COR_TIP")))
                    ll.remove(i);
                else
                     i++;
            }
            cont.second=ll.toArray(new Map[ll.size()]);
        }


        FileOutputStream fos = new FileOutputStream(fileTestName);
        ObjectOutputStream obs = new ObjectOutputStream(fos);
        obs.writeObject(stid2stan);
        obs.writeObject(code2stanid);
        obs.writeObject(dorCode2Rail);
        obs.writeObject(predId2Pred);
        obs.writeObject(vidId2Vid);
        obs.writeObject(grId2UkGR);
//        obs.writeObject(polgId2Polg);
//        obs.writeObject(obj_osn_id2Polg_Obj);
        obs.writeObject(markColor2colorName);
//        obs.writeObject(dorkod2num2tabloPolg);

        for (DirectoryTableDesc desc : descs)
            obs.writeObject(desc);
        obs.close();
    }
}
