package com.mwlib.tablo.derby.places;

import com.mwlib.tablo.cache.WrongParam;
import com.mwlib.tablo.derby.IDataLoader;
import com.mwlib.tablo.test.tpolg.CliProviderFactoryImpl3;
import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.AppContext;
import com.mwlib.tablo.ICliProvider;
import com.mwlib.tablo.ICliProviderFactory;
import com.mwlib.tablo.db.desc.DelayGIDTDesc;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 27.10.14
 * Time: 12:30
 * Загрузчик консолидационных таблиц дерби, отличается от ConsolidatorLoader тем что
 * не создает не загружет данные в таблицу, исключительно "следит" за обновлением таблиц
 * и производит консолидацию
 */
public class Consolidator4PlaceDataLoader implements IDataLoader
{
    final HashMap dummyHM = new HashMap();

    public static final String DEF_SESSIONID = "CONSOLIDATOR_PLACES_LOADER";

    private Set<String> allTables = new HashSet<String>();

    private int ixReq=0;
    private Map[] mapParms;

    private boolean test;
    private String[] loaderTypes;
    private String dsName;// = DbUtil.DS_JAVA_CACHE_NAME;
    private String oraName;//

    public Consolidator4PlaceDataLoader(String[] loaderTypes, String dsName, String oraName,boolean test)
    {
        this.test=test;
        this.loaderTypes=loaderTypes;
        this.dsName=dsName;
        this.oraName=oraName;
        mapParms=new Map[loaderTypes.length];
    }

    private int initDOR_KOD(ResultSet rs, Map<String, Object> tuple) throws SQLException {
        int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
        tuple.put(TablesTypes.DOR_CODE, dor_kod);
        Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
        if (byDorCode!=null)
            tuple.put(TablesTypes.DOR_NAME,byDorCode.getNAME());
        else
            tuple.put(TablesTypes.DOR_NAME,"UNKNOWN");
        return dor_kod;
    }


    protected void fillDorTuplesByQuery(Connection conn, List<Map> rv, String query, String tblName,Map<String, String> addRSParams,boolean total) throws SQLException
    {
        Statement stmt=null;
        ResultSet rs=null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            int i=0;
            int _total=0;
            while (rs.next())
            {
                Map<String,Object> tuple=new HashMap<String,Object>();
                int dor_kod=0;
                if (!total)
                    dor_kod = initDOR_KOD(rs, tuple);

                if (total || commonFilter(tuple,dor_kod,tblName))
                {
                    tuple.put("EVENT_TYPE", tblName);
                    tuple.put(TablesTypes.PLACE_ID,rs.getObject(TablesTypes.PLACE_ID));
                    tuple.put("CNT",rs.getInt("CNT"));
                    if (addRSParams!=null)
                        for (String fld : addRSParams.keySet())
                            tuple.put(fld,rs.getObject(addRSParams.get(fld)));
                    _total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println(tblName+" consolidation rows:" + i+" total:"+_total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }


    protected void fillTuplesByQuery(Connection conn, List<Map> rv, String query, String tblName,Map<String, String> addRSParams) throws SQLException
    {
        Statement stmt=null;
        ResultSet rs=null;

        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            int i=0;
            int total=0;
            while (rs.next())
            {
                Map<String,Object> tuple=new HashMap<String,Object>();
                int dor_kod = initDOR_KOD(rs, tuple);

                if (commonFilter(tuple,dor_kod,tblName))
                {
                    tuple.put("EVENT_TYPE", tblName);
                    tuple.put(TablesTypes.POLG_ID,rs.getInt(TablesTypes.POLG_ID));
                    tuple.put(TablesTypes.POLG_NAME,rs.getObject(TablesTypes.POLG_NAME));
                    tuple.put(TablesTypes.PLACE_ID,rs.getObject(TablesTypes.PLACE_ID));
                    tuple.put("NUM",rs.getInt("NUM"));
                    tuple.put("CNT",rs.getInt("CNT"));

                    if (addRSParams!=null)
                        for (String fld : addRSParams.keySet())
                            tuple.put(fld,rs.getObject(addRSParams.get(fld)));

                    total+=rs.getInt("CNT");
                    rv.add(tuple);
                }
                i++;
            }
            System.out.println(tblName+" consolidation rows:" + i+" total:"+total);

        }
        finally
        {
            DbUtil.closeAll(rs, stmt, null, false);
        }
    }




    String railSQLRoadFilter =null;
    Set<Integer> railSet =new HashSet<Integer>();

    protected Map[] consolidateData() throws Exception
    {

        Connection conn= null;
        try {

            if (!fillTables())
                return new Map[0];

            Directory.initDictionary(test);


            if (railSQLRoadFilter==null)
            {
                for (Directory.RailRoad railRoad : Directory.dorCode2Rail.values())
                {
                    if (Integer.valueOf(20).equals(railRoad.getADM_KOD()))
                    {
                        final Integer dor_kod = railRoad.getDOR_KOD();
                        if (railSQLRoadFilter ==null)
                            railSQLRoadFilter = dor_kod.toString();
                        else
                            railSQLRoadFilter +=","+ dor_kod.toString();
                        railSet.add(dor_kod);

                    }
                }
                if (railSQLRoadFilter ==null)
                    railSQLRoadFilter ="";
                else
                    railSQLRoadFilter =" IN ("+ railSQLRoadFilter +")";
            }

            conn= getDerbyConnection();
//TODO !!!!проверить политику заполнения кеша данных после получения консолидационных таблиц!!!!
            List<Map> outTuples = new LinkedList<Map>();

//            Date dt = new Date();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String yyyy=sdf.format(dt);
//            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
//            String time=sdf2.format(dt);

            {//Предупрждения
                final String tblName = TablesTypes.WARNINGSINTIME;

                {//Предупреждения действующие
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("A","GRP");
                    addInParams.put("EVENT_TYPE",TablesTypes.WARNINGS);
                    Map<String,String> addRSParams= new HashMap<String,String>();
                    addRSParams.put("TLN", "TLN");

                    sqlReamerConsolidator(conn, outTuples, tblName,addInParams);

                    String queryDorTotal ="select "+TablesTypes.DOR_CODE+",trim(char("+TablesTypes.DOR_CODE+")) || '##00' as PLACE_ID,SUM(F401_GVN) CNT,SUM(F401_GVL) TLN from "+tblName+" " +
                            " WHERE "+TablesTypes.DOR_CODE+ railSQLRoadFilter+
                            " group by "+TablesTypes.DOR_CODE;
                    String queryTotal = "select '##' as PLACE_ID,SUM(F401_GVN) CNT,SUM(F401_GVL) TLN from "+tblName+" WHERE "+TablesTypes.DOR_CODE+ railSQLRoadFilter;

                    sqlDorConsolidator2(conn, outTuples, tblName,queryDorTotal,addInParams,addRSParams,false);
                    sqlDorConsolidator2(conn, outTuples, tblName,queryTotal,addInParams,addRSParams,true);
                }
            }


            { //Задержки по ГИД
                final String tblName=TablesTypes.DELAYS_GID;
                final String eventType=tblName;
                final String trainIdField = "tbl.TRAIN_ID";
                final String prefixAttribute = "G";
                {//Пасс поезда
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("A", prefixAttribute +DelayGIDTDesc.PASS);
                    addInParams.put("EVENT_TYPE",eventType);
                    final String addFilter = "tbl.DATATYPE_ID=74";
                    String queryPlaces = getDelaySQL(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, new String[]{tblName,tblName +"_"+ TablesTypes.PLACES},queryPlaces,addInParams,dummyHM,false);

                    String queryDorPlaces = getDelaySQL4DOR(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, tblName,queryDorPlaces,addInParams,dummyHM,false);
                    String queryTotalPlaces = getDelaySQL4Total(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, tblName,queryTotalPlaces,addInParams,dummyHM,true);

                }


                {//Приг поезда
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("A", prefixAttribute +DelayGIDTDesc.REG);
                    addInParams.put("EVENT_TYPE",eventType);
                    final String addFilter = "tbl.DATATYPE_ID=75";
                    String queryPlaces = getDelaySQL(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, new String[]{tblName,tblName +"_"+ TablesTypes.PLACES},queryPlaces,addInParams,dummyHM,false);

                    String queryDorPlaces = getDelaySQL4DOR(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, tblName,queryDorPlaces,addInParams,dummyHM,false);
                    String queryTotalPlaces = getDelaySQL4Total(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, tblName,queryTotalPlaces,addInParams,dummyHM,true);

                }

                {//Грузовые поезда
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("A", prefixAttribute +DelayGIDTDesc.CRG);
                    addInParams.put("EVENT_TYPE",eventType);
                    final String addFilter = "tbl.DATATYPE_ID=76";
                    String queryPlaces = getDelaySQL(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, new String[]{tblName,tblName +"_"+ TablesTypes.PLACES},queryPlaces,addInParams,dummyHM,false);

                    String queryDorPlaces = getDelaySQL4DOR(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, tblName,queryDorPlaces,addInParams,dummyHM,false);
                    String queryTotalPlaces = getDelaySQL4Total(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, tblName,queryTotalPlaces,addInParams,dummyHM,true);

                }
            }


            { //Задержки по ИХ АВГД
                final String tblName=TablesTypes.DELAYS_ABVGD;
                final String eventType=TablesTypes.DELAYS_GID; //Из-за того что объеденены под одним заголовком
                final String trainIdField = "trim(char(tbl.TRAINID))";
                final String prefixAttribute = "I";
                {//Пасс поезда
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("A", prefixAttribute +DelayGIDTDesc.PASS);
                    addInParams.put("EVENT_TYPE",eventType);
                    final String addFilter = "tbl.DATATYPE_ID=62";
                    String queryPlaces = getDelaySQL(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, new String[]{tblName,tblName +"_"+ TablesTypes.PLACES},queryPlaces,addInParams,dummyHM,false);

                    String queryDorPlaces = getDelaySQL4DOR(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, tblName,queryDorPlaces,addInParams,dummyHM,false);
                    String queryTotalPlaces = getDelaySQL4Total(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, tblName,queryTotalPlaces,addInParams,dummyHM,true);

                }


                {//Приг поезда
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("A", prefixAttribute +DelayGIDTDesc.REG);
                    addInParams.put("EVENT_TYPE",eventType);
                    final String addFilter = "tbl.DATATYPE_ID=63";
                    String queryPlaces = getDelaySQL(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, new String[]{tblName,tblName +"_"+ TablesTypes.PLACES},queryPlaces,addInParams,dummyHM,false);

                    String queryDorPlaces = getDelaySQL4DOR(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, tblName,queryDorPlaces,addInParams,dummyHM,false);
                    String queryTotalPlaces = getDelaySQL4Total(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, tblName,queryTotalPlaces,addInParams,dummyHM,true);

                }

                {//Грузовые поезда
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("A", prefixAttribute +DelayGIDTDesc.CRG);
                    addInParams.put("EVENT_TYPE",eventType);
                    final String addFilter = "tbl.DATATYPE_ID=64";
                    String queryPlaces = getDelaySQL(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, new String[]{tblName,tblName +"_"+ TablesTypes.PLACES},queryPlaces,addInParams,dummyHM,false);

                    String queryDorPlaces = getDelaySQL4DOR(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, tblName,queryDorPlaces,addInParams,dummyHM,false);
                    String queryTotalPlaces = getDelaySQL4Total(tblName, addFilter, trainIdField);
                    sqlDorConsolidator2(conn, outTuples, tblName,queryTotalPlaces,addInParams,dummyHM,true);

                }
            }

            if (!test)
            {
                sqlWEATHERDorConsolidator(getOraConnection(),outTuples);
            }

            {
                final String tblName = TablesTypes.TEMPREL;
                Map<String,Object> addInParams= new HashMap<String,Object>();
                addInParams.put("A", "MAX");
                addInParams.put("EVENT_TYPE",tblName);

                final String rels_temp = "RELS_TEMP";

                final String query = "select int(" + TablesTypes.DOR_CODE + ") as "+TablesTypes.DOR_CODE+" ,POLG_ID, int(" + rels_temp + ") as CNT," +
                        "trim ( char( int("+TablesTypes.DOR_CODE+"))) || '##' || trim ( char(int(POLG_ID))) as PLACE_ID "+
                        " from TEMPREL where DOR_KOD<>0 and " + rels_temp + " is not null and POLG_ID<>0 order BY POLG_ID";

                sqlDorConsolidator2(conn, outTuples, new String[]{tblName}, query, addInParams, dummyHM, false);
                sqlTempDorConsolidator(conn, outTuples, TablesTypes.TEMPREL, rels_temp, null, addInParams, dummyHM, false);
                sqlTempDorConsolidator(conn,outTuples,TablesTypes.TEMPREL, rels_temp,null,addInParams,dummyHM,true);

                addInParams.put("A", "MIN");

                final String query1 = "select int(" + TablesTypes.DOR_CODE + ")  as "+TablesTypes.DOR_CODE+"  ,POLG_ID , int(" + rels_temp + "_MIN) as CNT," +
                        "trim ( char(int("+TablesTypes.DOR_CODE+"))) || '##' || trim ( char(int(POLG_ID))) as PLACE_ID "+
                        " from TEMPREL where DOR_KOD<>0 and " + rels_temp + "_MIN is not null and POLG_ID<>0 order BY POLG_ID";

                sqlDorConsolidator2(conn, outTuples, new String[]{tblName}, query1, addInParams, dummyHM, false);
                sqlTempDorConsolidator(conn,outTuples,TablesTypes.TEMPREL, rels_temp + "_MIN",null,addInParams,dummyHM,false);
                sqlTempDorConsolidator(conn,outTuples,TablesTypes.TEMPREL, rels_temp + "_MIN",null,addInParams,dummyHM,true);





            }


            {//Пометки ГИД
                final String tblName = TablesTypes.VIP_GID;
                {
                    sqlConsolidator(conn, outTuples, tblName,null, dummyHM);
                    sqlDorConsolidator(conn, outTuples, tblName,null, null, dummyHM,dummyHM,false);
                    sqlDorConsolidator(conn, outTuples, tblName, null, null, dummyHM,dummyHM,true);
                }
            }


            {//Вагоны в ТОР
                final String tblName = TablesTypes.VAGTOR;
                {
                    sqlConsolidator(conn, outTuples, tblName,null, dummyHM);
                    sqlDorConsolidator(conn, outTuples, tblName,null, null, dummyHM,dummyHM,false);
                    sqlDorConsolidator(conn, outTuples, tblName, null, null, dummyHM,dummyHM,true);
                }
            }



            {//Брошенные поезда
                final String tblName = TablesTypes.LOST_TRAIN;
                {
                    sqlConsolidator(conn, outTuples, tblName,null, dummyHM);

                    sqlDorConsolidator(conn, outTuples, tblName,null, null, dummyHM,dummyHM,false);
                    sqlDorConsolidator(conn, outTuples, tblName, null, null, dummyHM,dummyHM,true);
                }
            }

            {//ЗМ
                final String tblName = TablesTypes.ZMTABLE;
                { //Незакрыте
                    String addStringFilter="tbl.DATATYPE_ID=84";
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("A","Y");
                    sqlConsolidator(conn, outTuples, tblName,addStringFilter, addInParams);

                    sqlDorConsolidator(conn, outTuples, tblName,addStringFilter, null, addInParams,dummyHM,false);
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);
                }

                { //Просроченные
                    String addStringFilter="tbl.DATATYPE_ID=85";
                    Map<String,Object> addInParams= new HashMap<String,Object>();
                    addInParams.put("A","N");
                    sqlConsolidator(conn, outTuples, tblName,addStringFilter, addInParams);

                    sqlDorConsolidator(conn, outTuples, tblName,addStringFilter, null, addInParams,dummyHM,false);
                    sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);
                }

            }


//            if (false)
            {


                {//КМО
                    final String tblName = TablesTypes.KMOTABLE;
                    { //по службе П
                        String addStringFilter="tbl.DATATYPE_ID=65";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A","P");
                        sqlConsolidator(conn, outTuples, tblName,addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName,addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);
                    }

                    { //по службе П
                        String addStringFilter="tbl.DATATYPE_ID=66";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A","H");
                        sqlConsolidator(conn, outTuples, tblName,addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName,addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);
                    }

                    { //по службе П
                        String addStringFilter="tbl.DATATYPE_ID=67";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A","E");
                        sqlConsolidator(conn, outTuples, tblName,addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName,addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);
                    }
                }



                {//Предупреждения
                    final String tblName = TablesTypes.WARNINGS;

                    {//Предупреждения действующие
//                        final String addStringFilter=" ( tbl.TIM_OTM > TIMESTAMP('"+yyyy+"', '"+time+"') )";
                        final String addStringFilter=null;//TODO спрость у Марины считать ли закончившиеся предпрждение действющим

                        final String addAggregate=" SUM(LEN)/1000.0 TLN ";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A","REAL");

                        Map<String,String> addRSParams= new HashMap<String,String>();
                        addRSParams.put("TLN", "TLN");
                        sqlConsolidator2(conn, outTuples, tblName, addStringFilter, addAggregate, addInParams,addRSParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, addAggregate, addInParams,addRSParams,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, addAggregate, addInParams,addRSParams,true);
                    }

                    {//Предупреждения длительные
//                        final String addStringFilter=" tbl.TIM_OTM = TIMESTAMP('9999-12-31', '00:00:00') ";
                        final String addStringFilter=" tbl.FIXED_END_DATE = 0 ";
                        final String addAggregate=" SUM(LEN)/1000.0 TLN ";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A", "LONG");

                        Map<String,String> addRSParams= new HashMap<String,String>();
                        addRSParams.put("TLN", "TLN");
                        sqlConsolidator2(conn, outTuples, tblName, addStringFilter, addAggregate, addInParams,addRSParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, addAggregate, addInParams,addRSParams,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, addAggregate, addInParams,addRSParams,true);
                    }



                }

                {//Предупреждения с нарушением приказа
                    final String tblName = TablesTypes.WARNINGS_NP;
                    {//Отказы тех. средств по службе П
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A","NP");
                        addInParams.put("EVENT_TYPE",TablesTypes.WARNINGS);

                        sqlConsolidator(conn, outTuples, tblName, null, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, null, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, null, null, addInParams,dummyHM,true);
                    }
                }


                {//Подход техники
                    final String tblName = TablesTypes.TECH;
                    {
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A","TECH");
                        addInParams.put("EVENT_TYPE", TablesTypes.WINDOWS);
                        sqlConsolidator(conn, outTuples, tblName, null, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName,null, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, null, null, addInParams,dummyHM,true);

                    }
                }


                {//Окна
                    final String tblName = TablesTypes.WINDOWS;
                    {//Окна по службе П
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        String addStringFilter="tbl.DATATYPE_ID=54";
                        addInParams.put("A", "PPLAN");
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }

                    {//Окна по службе Ш
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        String addStringFilter="tbl.DATATYPE_ID=56";
                        addInParams.put("A", "HPLAN");
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }

                    {//Окна по службе Э
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        String addStringFilter="tbl.DATATYPE_ID=59";
                        addInParams.put("A", "EPLAN");
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }


                    {//Окна всего
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A","ALL");
                        sqlConsolidator(conn, outTuples, tblName, null, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, null, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, null, null, addInParams,dummyHM,true);

                    }

                }


                {//Окна предоставленные
                    final String tblName = TablesTypes.WINDOWS_CURR;
                    {//Окна по службе П
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        String addStringFilter="tbl.DATATYPE_ID=46";
                        addInParams.put("A", "PCURR");
                        addInParams.put("EVENT_TYPE", TablesTypes.WINDOWS);
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }

                    {//Окна по службе Ш
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        String addStringFilter="tbl.DATATYPE_ID=57";
                        addInParams.put("A", "HCURR");
                        addInParams.put("EVENT_TYPE", TablesTypes.WINDOWS);
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }

                    {//Окна по службе Э
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        String addStringFilter="tbl.DATATYPE_ID=60";
                        addInParams.put("A", "ECURR");
                        addInParams.put("EVENT_TYPE", TablesTypes.WINDOWS);
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }
                }


                {//Окна передержанные
                    final String tblName = TablesTypes.WINDOWS_OVERTIME;
                    {
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A", "OVERTIME");
                        addInParams.put("EVENT_TYPE", TablesTypes.WINDOWS);
                        sqlConsolidator(conn, outTuples, tblName, null, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, null, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, null, null, addInParams,dummyHM,true);

                    }
                }


                {//Отказы тех. средств
                    final String tblName = TablesTypes.REFUSES;
                    {//Отказы тех. средств по службе П
                        String addStringFilter="tbl.DATATYPE_ID=48";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A", "P");
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }

                    {//Отказы тех. средств по службе Ш
                        String addStringFilter="tbl.DATATYPE_ID=49";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A", "H");
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }

                    {//Отказы тех. средств по службе Э
                        String addStringFilter="tbl.DATATYPE_ID=50";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A", "E");
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }


                    {//Отказы тех. средств по службе В
                        String addStringFilter="tbl.DATATYPE_ID=51";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A", "V");
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }


                    {//Отказы тех. средств не приняты к учету
                        String addStringFilter="tbl.DATATYPE_ID=73";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A", "N");
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }
                }


                {//Нарушения
                    final String tblName = TablesTypes.VIOLATIONS;
                    {//Нарушения по службе П
                        String addStringFilter="tbl.DATATYPE_ID=68";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A", "P");
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }

                    {//Нарушения по службе Ш
                        String addStringFilter="tbl.DATATYPE_ID=69";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A", "H");
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }

                    {//Нарушения по службе Э
                        String addStringFilter="tbl.DATATYPE_ID=70";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A", "E");
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }


                    {//Нарушения по службе В
                        String addStringFilter="tbl.DATATYPE_ID=71";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A", "V");
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }


                    {//Нарушения не приняты к учету
                        String addStringFilter="tbl.DATATYPE_ID=72";
                        Map<String,Object> addInParams= new HashMap<String,Object>();
                        addInParams.put("A", "N");
                        sqlConsolidator(conn, outTuples, tblName, addStringFilter, addInParams);

                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,false);
                        sqlDorConsolidator(conn, outTuples, tblName, addStringFilter, null, addInParams,dummyHM,true);

                    }
                }
            }


            return outTuples.toArray(new Map[outTuples.size()]);
        }
        finally
        {
            DbUtil.closeAll(null,null,conn,true);
        }
    }

    private String getDelaySQL4Total(String tblName, String addFilter, String trainIdField)
    {
        if (addFilter==null)
            addFilter="";
        else if (addFilter.length()>0 && !addFilter.trim().startsWith("and"))
            addFilter=" and "+addFilter;

        return "select '##' as PLACE_ID,count(distinct " + trainIdField + ") CNT  from " +tblName+" tbl "+" WHERE "+ TablesTypes.DOR_CODE+railSQLRoadFilter+addFilter;
    }

    private String getDelaySQL4DOR(String tblName, String addFilter, String trainIdField) {

        if (addFilter==null)
            addFilter="";
        else if (addFilter.length()>0 && !addFilter.trim().startsWith("and"))
            addFilter=" and "+addFilter;

        return "select "+ TablesTypes.DOR_CODE+",trim(char(int("+TablesTypes.DOR_CODE+ "))) || '##00' as PLACE_ID,count(distinct " + trainIdField + ") CNT " +
                        " from "+tblName+" tbl WHERE "+TablesTypes.DOR_CODE+railSQLRoadFilter+addFilter+" group by DOR_KOD";
    }

    private String getDelaySQL(String tblName, String addFilter, String trainIdField)
    {

        if (addFilter==null)
            addFilter="";
        else if (addFilter.length()>0 && !addFilter.trim().startsWith("and"))
            addFilter=" and "+addFilter;

        return "select pl.DOR_KOD,pl.POLG_ID,pl.NAME as PNAME,pl.NUM,trim ( char(pl.DOR_KOD)) || '##' || trim ( char(pl.POLG_ID)) as PLACE_ID \n" +
                ",count( distinct  trim (char(tdp.DATATYPE_ID)) || '##' ||trim(char(po.POLG_ID))  || '##'  || " + trainIdField + ") as CNT\n" +
                            "from "+tblName+"_PLACES tdp,"+tblName+" tbl,POLG_OBJ po, TABLO_POLG_LIST pl\n" +
                            "where po.POLG_TYPE=100185 and po.OBJ_OSN_ID=tdp.OBJ_OSN_ID and pl.POLG_ID=po.POLG_ID and pl.DOR_KOD<>0 and pl.DOR_KOD=tbl.DOR_KOD and pl.POLG_TYPE=100185 \n" +
                            "and tbl.DATA_OBJ_ID=tdp.DATA_OBJ_ID "+addFilter+"\n" +
                            "group by pl.POLG_ID,pl.NAME,pl.DOR_KOD,pl.NUM order by DOR_KOD,pl.NUM";
    }

    private void sqlConsolidator(Connection conn, List<Map> outTuples, String tblName, String addFilter, Map<String, Object> addParams) {
        if (hasTable(tblName) && hasTable(tblName +"_"+ TablesTypes.PLACES))
        try
        {
            List<Map> rv= new LinkedList<Map>();

            if (addFilter==null)
                addFilter="";
            else if (addFilter.length()>0 && !addFilter.trim().startsWith("and"))
                addFilter=" and "+addFilter;

            String query = "select pl.DOR_KOD,pl.POLG_ID,pl.NAME as PNAME,pl.NUM,trim ( char(pl.DOR_KOD)) || '##' || trim ( char(pl.POLG_ID)) as " + TablesTypes.PLACE_ID + " ,count( distinct trim ( char(tdp.DATATYPE_ID)) ||'##' || tdp.DATA_OBJ_ID || '##' || trim(char(po.POLG_ID)) ) as CNT \n" +
                    "from "+tblName+"_PLACES tdp,"+tblName+" tbl,POLG_OBJ po, TABLO_POLG_LIST pl\n" +
                    "where po.POLG_TYPE=100185 and po.OBJ_OSN_ID=tdp.OBJ_OSN_ID and pl.POLG_ID=po.POLG_ID and pl.DOR_KOD<>0 and pl.DOR_KOD=tbl.DOR_KOD and pl.POLG_TYPE=100185 \n" +
                    "and tbl.DATA_OBJ_ID=tdp.DATA_OBJ_ID "+addFilter+"\n" +
                    "group by pl.POLG_ID,pl.NAME,pl.DOR_KOD,pl.NUM order by DOR_KOD,pl.NUM";

            fillTuplesByQuery(conn, rv, query, tblName,null);
            for (Map map : rv)
                for (String key : addParams.keySet())
                    map.put(key,addParams.get(key));
            outTuples.addAll(rv);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


    //
    private void sqlConsolidator2(final Connection conn, final List<Map> outTuples, String tblName, String addFilter,String addAggregate,
                                  final Map<String, Object> addInParams,
                                  final Map<String, String> addRSParams
    ) {
        if (hasTable(tblName) && hasTable(tblName +"_"+ TablesTypes.PLACES))
        try
        {
            List<Map> rv= new LinkedList<Map>();

            if (addAggregate==null)
                addAggregate="";
            else if (addAggregate.length()>0 && !addAggregate.trim().startsWith(","))
                addAggregate=","+addAggregate;

            if (addFilter==null)
                addFilter="";
            else if (addFilter.length()>0 && !addFilter.trim().startsWith("and"))
                addFilter=" and "+addFilter;

            String query = "select pl.DOR_KOD,pl.POLG_ID,pl.NAME as PNAME,pl.NUM,trim ( char(pl.DOR_KOD)) || '##' || trim ( char(pl.POLG_ID)) as "+TablesTypes.PLACE_ID+", \n" +
                            "count(*) as CNT "+addAggregate+" \n" +
                            "from (select  distinct tdp.DATATYPE_ID,tdp.DATA_OBJ_ID,po.POLG_ID from "+tblName+"_PLACES tdp,POLG_OBJ po\n" +
                            "\t\twhere po.POLG_TYPE=100185 and po.OBJ_OSN_ID=tdp.OBJ_OSN_ID) tdp,"+tblName+" tbl,TABLO_POLG_LIST pl\n" +
                            "where  pl.DOR_KOD<>0 \n" +
                            "\tand pl.DOR_KOD=tbl.DOR_KOD \n" +
                            "\tand pl.POLG_TYPE=100185 \n" +
                            "\tand pl.POLG_ID=tdp.POLG_ID \n" +
                            "\tand tbl.DATA_OBJ_ID=tdp.DATA_OBJ_ID \n" +
                            "\tand tbl.DATATYPE_ID=tdp.DATATYPE_ID \n" +
                             addFilter +
                            " group by pl.POLG_ID,pl.NAME,pl.DOR_KOD,pl.NUM order by DOR_KOD,pl.NUM";


            fillTuplesByQuery(conn, rv, query, tblName,addRSParams);
            for (Map map : rv)
                for (String key : addInParams.keySet())
                    map.put(key,addInParams.get(key));
            outTuples.addAll(rv);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


    //Делает развертку таблиц, что бы были видны все полигоны, даже если в них не было событий
    private void sqlReamerConsolidator(final Connection conn, final List<Map> outTuples, String tblName,final Map<String, Object> addInParams) {
        if (hasTable(tblName))
        try
        {

            List<Map> rv= new LinkedList<Map>();

            String query ="select distinct pl."+TablesTypes.DOR_CODE+",pl."+TablesTypes.POLG_ID+",pl.NAME || '(' || trim ( char(pl."+TablesTypes.POLG_ID+")) ||  ')' as "+TablesTypes.POLG_NAME+",pl.NUM,trim ( char(pl."+TablesTypes.DOR_CODE+")) || '##' || trim ( char(pl."+TablesTypes.POLG_ID+")) as "+TablesTypes.PLACE_ID+"\n" +
                    " from TABLO_POLG_LIST pl where  pl.DOR_KOD "+railSQLRoadFilter+" and pl.POLG_TYPE=100185 order by DOR_KOD,pl.NUM";

            Statement stmt=null;
            ResultSet rs=null;

            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                int i=0;
                int total=0;
                while (rs.next())
                {
                    Map<String,Object> tuple=new HashMap<String,Object>();
                    int dor_kod = initDOR_KOD(rs, tuple);

                    if (commonFilter(tuple,dor_kod,tblName))
                    {
                        tuple.put("EVENT_TYPE", tblName);
                        tuple.put(TablesTypes.POLG_ID,rs.getInt(TablesTypes.POLG_ID));
                        tuple.put(TablesTypes.POLG_NAME,rs.getObject(TablesTypes.POLG_NAME));
                        tuple.put(TablesTypes.PLACE_ID,rs.getObject(TablesTypes.PLACE_ID));
                        tuple.put("NUM",rs.getInt("NUM"));
                        total+=1;
                        rv.add(tuple);
                    }
                    i++;
                }
                System.out.println(tblName+" consolidation rows:" + i+" total:"+total);

            }
            finally
            {
                DbUtil.closeAll(rs, stmt, null, false);
            }

            for (Map map : rv)
                for (String key : addInParams.keySet())
                    map.put(key,addInParams.get(key));
            outTuples.addAll(rv);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }



    private void sqlDorConsolidator2(final Connection conn, final List<Map> outTuples, String tblNames,String query,
                                  final Map<String, Object> addInParams,
                                  final Map<String, String> addRSParams,boolean total

    )
    {
        sqlDorConsolidator2(conn, outTuples, new String[]{tblNames},query,addInParams,addRSParams,total);
    }



    private void sqlDorConsolidator2(final Connection conn, final List<Map> outTuples, String[] tblNames,String query,
                                  final Map<String, Object> addInParams,
                                  final Map<String, String> addRSParams,boolean total

    ) {

        for (String tblName : tblNames)
            if (!hasTable(tblName))
                return;
        try
        {
            List<Map> rv= new LinkedList<Map>();
            fillDorTuplesByQuery(conn, rv, query, tblNames[0],addRSParams,total);
            for (Map map : rv)
                for (String key : addInParams.keySet())
                    map.put(key,addInParams.get(key));
            outTuples.addAll(rv);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


    //
    private void sqlDorConsolidator(final Connection conn, final List<Map> outTuples, String tblName, String addFilter,String addAggregate,
                                  final Map<String, Object> addInParams,
                                  final Map<String, String> addRSParams,boolean total
    ) {
        if (hasTable(tblName))
        try
        {
            List<Map> rv= new LinkedList<Map>();

            if (addAggregate==null)
                addAggregate="";
            else if (addAggregate.length()>0 && !addAggregate.trim().startsWith(","))
                addAggregate=","+addAggregate;

            if (addFilter==null)
                addFilter="";
            if (addFilter.length()>0 && !addFilter.trim().startsWith("and"))
                addFilter=" and "+addFilter;


            String query;
            if (total)
                query = "select '##' as PLACE_ID,count(*) CNT " +addAggregate + " from "+tblName+" tbl "+" WHERE "+TablesTypes.DOR_CODE+railSQLRoadFilter+addFilter;
            else
                query = "select "+TablesTypes.DOR_CODE+",trim(char(int("+TablesTypes.DOR_CODE+"))) || '##00' as PLACE_ID,count(*) CNT " +
                    addAggregate +
                    " from "+tblName+" tbl WHERE "+TablesTypes.DOR_CODE+railSQLRoadFilter+addFilter+" group by DOR_KOD";


            fillDorTuplesByQuery(conn, rv, query, tblName, addRSParams,total);
            for (Map map : rv)
                for (String key : addInParams.keySet())
                    map.put(key,addInParams.get(key));
            outTuples.addAll(rv);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


    private void sqlTempDorConsolidator(final Connection conn, final List<Map> outTuples, String tblName,String colName,String addFilter,
                                  final Map<String, Object> addInParams,
                                  final Map<String, String> addRSParams,boolean total
    ) {
        if (hasTable(tblName))
        try
        {
            List<Map> rv= new LinkedList<Map>();

            if (addFilter==null)
                addFilter="";
            if (addFilter.length()>0 && !addFilter.trim().startsWith("and"))
                addFilter=" and "+addFilter;


            String query;
            if (total)
                query = "select '##' as PLACE_ID," + colName+"  as CNT from "+tblName+" tbl "+" WHERE DOR_KOD=0 and POLG_ID=0 and "+colName+" is not null "+addFilter;
            else
                query = "select POLG_ID as "+TablesTypes.DOR_CODE+"," +
                        "trim(char(int(POLG_ID))) || '##00' as PLACE_ID," + colName+" as CNT from "+tblName+" tbl "+" WHERE DOR_KOD=0 and POLG_ID<>0 and "+colName+" is not null "+addFilter;

            fillDorTuplesByQuery(conn, rv, query, tblName, addRSParams,total);
            for (Map map : rv)
                for (String key : addInParams.keySet())
                    map.put(key,addInParams.get(key));
            outTuples.addAll(rv);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }



    private void sqlWEATHERDorConsolidator(final Connection conn, final List<Map> outTuples)
    {
        try
        {

            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String strDate=sdf.format(dt);

            List<Map> rv= new LinkedList<Map>();

            String query;
//            query = "select \n" +
//                    "CASE  \n" +
//                    "WHEN DOR_KOD=0 and POLG_TYPE=0 THEN '##'\n" +
//                    "WHEN DOR_KOD>0 and POLG_TYPE=0 THEN to_char(DOR_KOD) || '##00'\n" +
//                    "ELSE to_char(DOR_KOD) || '##' || to_char(POLG_ID)\n" +
//                    "END as PLACE_ID,t.* from ICG0.TABLO_WEATHER t where REP_DATE = TO_DATE('"+strDate+"','yyyy-MM-dd')";


            query = "select \n" +
                    "CASE  \n" +
                    "WHEN t.DOR_KOD=0 and t.POLG_TYPE=0 THEN '##'\n" +
                    "WHEN t.DOR_KOD>0 and t.POLG_TYPE=0 THEN to_char(t.DOR_KOD) || '##00'\n" +
                    "ELSE to_char(t.DOR_KOD) || '##' || to_char(t.POLG_ID)\n" +
                    "END as PLACE_ID,t.* from ICG0.TABLO_WEATHER t,ICG0.TABLO_POLG_LIST po where t.REP_DATE = TO_DATE('"+strDate+"','yyyy-MM-dd') and t.POLG_TYPE=po.POLG_TYPE\n" +
                    "and po.DOR_KOD=t.DOR_KOD and t.POLG_ID=po.POLG_ID";


            {
                Statement stmt=null;
                ResultSet rs=null;

                try {
                    stmt = conn.createStatement();
                    rs = stmt.executeQuery(


                            query);
                    int i=0;
                    int _total=0;
                    while (rs.next())
                    {

                        fillTupleWEATHERS(rv, rs, "TEMP");
                        fillTupleWEATHERS(rv, rs, "WIND");
                        fillTupleWEATHERS(rv, rs, "ICON1");
                        i++;
                    }
                    System.out.println("ICG0.TABLO_WEATHER consolidation rows:" + i+" total:"+_total);

                }
                finally
                {
                    DbUtil.closeAll(rs, stmt, null, false);
                }
            }

            outTuples.addAll(rv);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            DbUtil.closeAll(null, null, conn, true);
        }
    }

    private void fillTupleWEATHERS(List<Map> rv, ResultSet rs, String attr) throws SQLException {
        Map<String,Object> tuple=new HashMap<String,Object>();
        int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
        if (dor_kod>0)
        {
            dor_kod=initDOR_KOD(rs, tuple);
            tuple.put(TablesTypes.POLG_ID, rs.getString(TablesTypes.POLG_ID));
        }
        tuple.put("EVENT_TYPE", TablesTypes.TABLO_WEATHER);
        tuple.put(TablesTypes.PLACE_ID, rs.getString(TablesTypes.PLACE_ID));
        tuple.put("A", attr);
        String sValue = rs.getString(attr);
        if (sValue!=null)
        {
            if ("ICON1".equals(attr))
            {
                sValue="w/"+sValue;
                tuple.put("ICONS", sValue);
            }
            else
            tuple.put("CONS", sValue.replace("\r\n","<br>"));
        }

        if (dor_kod==0 || commonFilter(tuple,dor_kod,TablesTypes.TABLO_WEATHER))
            rv.add(tuple);
    }


    private boolean hasTable(String tblName) {
        return allTables.contains(tblName);
    }

    private Connection getDerbyConnection() throws Exception {

        return DbUtil.getConnection2(dsName);
    }

    private Connection getOraConnection() throws Exception {

        return DbUtil.getConnection2(oraName);
    }


    protected boolean checkLoading() throws Exception
    {
        boolean rv =false;         //TODO Здесь обработать так же суточный переход в тор, т.е. провести вернуть true и провести заново консолидацию
        try
        {
            //ICliProviderFactory providerFactoryInstance = IDerbyCliProviderFactoryImpl.getProviderFactoryInstance();
            ICliProviderFactory providerFactoryInstance = CliProviderFactoryImpl3.getProviderFactoryInstance();
            providerFactoryInstance.addNotSessionCliIds(DEF_SESSIONID);

            for (int i = 0, mapParmsLength = mapParms.length; i < mapParmsLength; i++)
            {
                if (mapParms[i]==null)
                {
                    mapParms[i] = new HashMap();
                    mapParms[i].put(ICliProviderFactory.CLIID, new String[]{DEF_SESSIONID});
                    mapParms[i].put(AppContext.APPCONTEXT, new AppContext[]{new AppContext()});
                    mapParms[i].put(TablesTypes.TTYPE, new String[]{loaderTypes[i]});
                    mapParms[i].put("CLICNT",1000);
                }
                mapParms[i].put(TablesTypes.ID_REQN, new String[]{String.valueOf(ixReq)});

                ICliProvider[] providers = null; //здесь провайдер уже относится только к запрашиваемой таблице
                try {
                    providers = providerFactoryInstance.getProvider(mapParms[i]);
                }
                catch (WrongParam wrongParam)
                {
                    System.out.println("Can't find provider for "+loaderTypes[i]+" possible not ready");
                    //wrongParam.printStackTrace();
                }
                if (providers==null || providers.length==0)
                    continue;

                ICliProvider provider=providers[0];

                int cliCnt=(Integer)mapParms[i].get("CLICNT");
                int currCnt = provider.getCliCnt();
                mapParms[i].put("CLICNT",currCnt);
                rv= rv || (currCnt -cliCnt)<0;

                ixReq++;

                mapParms[i].put(TablesTypes.TBLID,new String[]{provider.getTblId()});

                Map<Object, long[]> newKeys = provider.getNewDataKeys(mapParms[i]).dataRef;
                rv = rv ||(newKeys.size()>0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw e;
        }
        return rv;
    }



    private boolean fillTables()   throws Exception
    {

        if (allTables.size()==loaderTypes.length)
            return true;

        Connection conn=null;
        Statement stmt=null;
        ResultSet rs = null;
        Set<String> needTables = new HashSet<String>();
        needTables.addAll(Arrays.asList(loaderTypes));

        allTables.clear();

        try
        {
            conn= getDerbyConnection();
            DatabaseMetaData dbmd = conn.getMetaData();


            rs = dbmd.getTables(null,"APP", null, new String[]{"TABLE","VIEW"});

            while (rs.next())
            {

                String tableName = rs.getString("TABLE_NAME");
                if (needTables.contains(tableName))
                    allTables.add(tableName);
            }
        }
        finally
        {
            DbUtil.closeAll(rs,stmt,conn, true);
        }
        return allTables.size()>0;
    }

    @Override
    public Map [] getData() throws Exception
    {
        boolean isConsolidate=test;
        Map [] rv =new Map[0];
        if (!test)
        {
            long lg= System.currentTimeMillis();
            isConsolidate= checkLoading();
            long mills = System.currentTimeMillis() - lg;
            printDelayTime(mills,"fill DerbyTable by Events :");
        }

        {
            long lg= System.currentTimeMillis();
            if (isConsolidate)
                rv=consolidateData();
            long mills = System.currentTimeMillis() - lg;
            printDelayTime(mills,"Consolidate Events :");
        }
        return rv;
    }


    protected boolean commonFilter(Map<String,Object> tuple,int dorKod,String type)
    {
        if (!railSet.contains(dorKod))
        {
            System.out.print("skip tuple for type = " + type+" dorKod:"+dorKod+" tule:");
            System.out.println("tuple = " + tuple);
            return false;
        }
        return true;
    }


    public static void printDelayTime(long mills,String message) {
        double sec = (1.0 * mills) / 10;
        System.out.println(message + 1.0*((int)(sec))/100+" sec.");
    }

}
