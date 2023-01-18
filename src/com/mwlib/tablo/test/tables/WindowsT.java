package com.mwlib.tablo.test.tables;

import com.mwlib.utils.db.Directory;
import com.mwlib.utils.db.FillFromDb;
import com.mycompany.common.FieldException;
import com.mycompany.common.LinkObject;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.tables.AField;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mwlib.tablo.tables.SimpleField;
import com.smartgwt.client.types.ListGridFieldType;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 * To change this template use File | Settings | File Templates.
 */
public class WindowsT extends BaseTable
{
    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
    {
                    new SimpleField("DOR_NAME","Дорога",ListGridFieldType.TEXT.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Integer dor_kod=(Integer)tuple.get("DOR_KOD");
                            if (Directory.isInit())
                            {
                                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                                if (byDorCode!=null)
                                    return byDorCode.getNAME();
                            }
                            return String.valueOf(dor_kod);
                        }
                    },
                    new SimpleField("ID_Z","№")
                    {
                        {
                            autofit=true;
                        }
                    },
                    new SimpleField("o_serv","Служба")
                    {
                        {
                            autofit=true;
                        }
                    },
                    new AField(" ","Перегон")
                    {

                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Object stan1code = tuple.get("STAN1_ID");
                            Object stan2code = tuple.get("STAN2_ID");

                            if (Directory.isInit())
                            {
                                Directory.StanDesc stan1=null;
                                if (stan1code!=null && (Integer)stan1code>0)
                                    stan1 = Directory.getByStanId((Integer)stan1code);

                                Directory.StanDesc stan2=null;
                                if (stan2code!=null && (Integer)stan2code>0)
                                    stan2 = Directory.getByStanId((Integer)stan2code);
                                if (stan1!=null && stan1code.equals(stan2code))
                                    return stan1.getNAME();
                                else if (stan1!=null)
                                    return stan1.getNAME()+(stan2!=null?"-"+stan2.getNAME():"");
                                else if (stan2!=null)
                                    return stan2.getNAME();
                                return "";
                            }
                            if (stan2code!=null)
                                return  stan1code.toString()+"-"+stan1code.toString();
                            else
                                return  stan1code.toString();
                        }
                    },
                    new SimpleField("PRED_ID","Исполнитель")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Integer pred_id=(Integer)tuple.get("PRED_ID");
                            if (pred_id!=null && pred_id!=0)
                            {
                                Directory.Pred pred = Directory.getByPredId(pred_id);
                                if (pred!=null)
                                    return pred.getSNAME();
                            }
                            return "";
                        }
                    },
                    new SimpleField("COMMENT","Работы")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            String wkName= (String) super.getS(column,tuple,outTuple);
                            String vname=(String)tuple.get("OBJECT_VNAME");
                            return "<b>"+vname+":</b><br>"+wkName;

                        }

                    },//
                    new DateField("ND","Начало") ,
                    new DateField("KD","Конец") ,


                    new DateField("DT_ND","Начало(ГИД)")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException {

                            Timestamp gnd=(Timestamp)tuple.get("DT_ND");
                            if (gnd.getTime()<0)
                                return "";
                            return super.getS(column,tuple,outTuple);
                        }
                    }
            ,
                    new DateField("DT_KD","Конец(ГИД)")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException {

                            Timestamp gnd=(Timestamp)tuple.get("DT_KD");
                            if (gnd.getTime()<0)
                                return "";
                            return super.getS(column,tuple,outTuple);
                        }
                    },

                    new AField("o_state_desc","Состояние")
                    {
                        {
                            type=ListGridFieldType.LINK.toString();
                        }

                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Object rValue = getRowStyle(column,tuple, outTuple);

                            LinkObject linkObject = new LinkObject(rValue.toString(), "");
                            String id=(String)tuple.get(TablesTypes.KEY_FNAME);
                            if (id!=null && id.length()>0)
                            {
                                String[] dor2id=id.split(";");
                                linkObject.setLink("http://host_card:port_card/wnd_pr/operative/card.jsp?wid=" + dor2id[1] + "&dorKod=" + dor2id[0] + "&opener=0");
                            }
                            return linkObject;
                        }
                    },

                    //new SimpleField(STATUS_FACT,STATUS_FACT,ListGridFieldType.INTEGER.toString(),false),

//                    new DateField("DT_ND","Начало<br>действия(ГИД)<br>") ,
//                    new DateField("DT_KD","Конец<br>действия(ГИД)<br>") ,

                    //new SimpleField("o_state","Состояния"),
                    new SimpleField("DOR_KOD","№ Дороги",ListGridFieldType.TEXT.toString(),false),
                    new SimpleField("o_state","Состояния", ListGridFieldType.TEXT.toString(),false),
                    new SimpleField(STATUS_FACT,"Исполнение",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(STATUS_PL,"Планирование",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false),



    };

    public static Object getRowStyle(ColumnHeadBean[] column,Map tuple, Map<String, Object> _outTuple) throws FieldException
    {
        int overTime=(Integer)tuple.get("OVERTIME");

        Timestamp nd=(Timestamp)tuple.get("ND");
        Timestamp kd=(Timestamp)tuple.get("KD");

        Timestamp gnd=(Timestamp)tuple.get("DT_KD");
        Timestamp gkd=(Timestamp)tuple.get("DT_KD");

        if (gnd!=null && gnd.getTime()>0)
            nd=gnd;
        if (gkd!=null && gkd.getTime()>0)
            kd=gkd;

        int fact=(Integer)tuple.get(STATUS_FACT);
        //int plan=(Integer)tuple.get(STATUS_PL);

        long current = System.currentTimeMillis();

        String rValue="";
        switch (fact)
        {
            case 0:
            { //не активно, проверяем предоставлено оно или просто не активно
                if (nd.getTime()>current)
                {
                    rValue="До начала:"+ getTimeInterval(nd, current,1);
                    if ((nd.getTime()-current) / 60000 < 30)
                        _outTuple.put(TablesTypes.ROW_STYLE,"background-color:#55FFFF;");
                }
                else
                    rValue="Не предоставлено";
                break;
            }
            case 1:
            { //активно проверяем не передержано оно
                if (kd.getTime()<current)
                {
                    if (kd.getTime()<current+60000)
                        rValue="Передержка:"+getTimeInterval(kd, current,-1);
                    else
                        rValue="Ожидание закрытия:"+getTimeInterval(kd, current,-1);
                    _outTuple.put(TablesTypes.ROW_STYLE, "background-color:#FF3300;");
                }
                else
                {
                    rValue="До окончания:"+ getTimeInterval(kd, current,1);
                    _outTuple.put(TablesTypes.ROW_STYLE, "background-color:#DDDD00;");
                }
                break;
            }
            case 2:
            { //закрыто, по овертайму проверим было ли оно передержано
                rValue="Закрыто";
                if (overTime>0)
                {
                    rValue=rValue+" "+overTime+" мин";
                    _outTuple.put(TablesTypes.ROW_STYLE, "background-color:#DD3300;");
                }
                else
                    _outTuple.put(TablesTypes.ROW_STYLE,"background-color:#55DD00;");
                break;
            }
            case 3:
            {
                _outTuple.put(TablesTypes.ROW_STYLE, "background-color:#FF0000;");
                rValue="Сорвано";
                break;
            }
            default:
                rValue="UNKNOWN";
        }
        return rValue;
    }

    public static String getTimeInterval(Timestamp nd, long current,int m) {
        long min = m*(nd.getTime()-current) / 60000;
        if (min>60)
            return " "+(min/60)+"ч :"+min%60+" мин";
        else
            return " "+min+" мин";
    }

    protected static BaseTable inst;

    @Override
    public String getTableType() {
        return TablesTypes.WINDOWS;
    }

    protected WindowsT(boolean test) {
        super(test);
    }

    public static BaseTable getInstance(boolean test) throws Exception
    {

        if (inst!=null)
            return inst;
        return inst=new WindowsT(test);
    }

    @Override
    protected FieldTranslator[] getFieldTranslator() {
        return  fieldTranslator;
    }

    public static final String GET_W_WINDOWS="select toj1.data_obj_id,\n" +
            "toj2.cor_time,\n" +
            "da.attr_id,al.attr_type,da.value_s,da.value_i,da.value_f,da.value_t,da.cor_time \n" +
            "as acor_time\n" +
            ",toj2.text," +
            " toj2.datatype_id  \n" +
            "from (  \n" +
            "select distinct(toj.data_obj_id) as data_obj_id from tablo_data_objects toj,tablo_data_attributes da " +
            "where toj.DATATYPE_ID IN (46,47,54,57,58,56,60,61,59,94)\n" +
            "and toj.cor_tip IN ('I','U') and toj.dor_kod= ? \n" +
            "and toj.data_obj_id=da.data_obj_id  and da.attr_id='KD' \n" +
            "and (da.value_t>= ? ) \n" + //TO_TIMESTAMP ('08-07-14 15:50:00', 'DD-MM-RR HH24:MI:SS')
            "and toj.cor_time> ? \n"+
//            "union\n" +
//            "select data_obj_id from tablo_data_objects toj " +
//            "where toj.DATATYPE_ID IN (46,47,54,57,58,56,60,61,59)\n" +
//            "and toj.cor_tip IN ('I','U') " +
//            "and toj.dor_kod= ? " +
//            "and toj.cor_time > ? "+
//            "and NOT exists (\n" +
//            "SELECT * from tablo_data_objects toj1 where toj1.data_obj_id = toj.data_obj_id and DATATYPE_ID = 94)\n" +
            ") " +
            "toj1, \n" +
            "tablo_data_objects toj2,\n" +
            "tablo_data_attributes da,\n" +
            "tablo_data_attr_list al  " +
            "where  \n" +
            "toj1.data_obj_id=da.data_obj_id \n" +
            "and toj2.data_obj_id=da.data_obj_id \n" +
            "and toj2.datatype_id=da.datatype_id\n" +
            "and al.attr_id=da.attr_id \n" +
            "and al.DATATYPE_ID=da.datatype_id \n"+
            "order by \n" +
            "da.cor_time,\n" +
            "da.data_obj_id,da.datatype_id";

    @Override
    protected Pair<ColumnHeadBean[], Map[]> getDbTable(Map mapParams, Map<String, Object> outParams) throws Exception
    {
        Map<String,FillFromDb.ParamVal> valMap = new HashMap<String,FillFromDb.ParamVal>();

        Timestamp maxTimeStamp = FillFromDb.getMaxTimeStamp(mapParams);
        valMap.put(TablesTypes.MAX_TIMESTAMP,new FillFromDb.ParamVal(-1, maxTimeStamp, Types.NULL));

        Integer dor_code = ((Integer[])mapParams.get(FillFromDb.DOR_COD_ATTR))[0];

        valMap.put(FillFromDb.DOR_COD_ATTR+"_1",new FillFromDb.ParamVal(1, dor_code, Types.INTEGER));
        valMap.put("KD",new FillFromDb.ParamVal(2, new Timestamp(Calendar.getInstance().getTimeInMillis()-24*60*60*1000), Types.TIMESTAMP));
        valMap.put("CORTIME1",new FillFromDb.ParamVal(3, maxTimeStamp, Types.TIMESTAMP));
//        valMap.put(FillFromDb.DOR_COD_ATTR+"_2",new FillFromDb.ParamVal(4, dor_code, Types.INTEGER));
//        valMap.put("CORTIME2",new FillFromDb.ParamVal(5, maxTimeStamp, Types.TIMESTAMP));

        Pair<ColumnHeadBean[], Map[]> resVal = FillFromDb._getDbTable(GET_W_WINDOWS, valMap, outParams, new FillFromDb.IRowOperation()
        {
            public void setObjectArc(Map<String, ColumnHeadBean> attrs, Timestamp cor_time, ResultSet rs, Map<String, Pair<Timestamp, Object>> obj) throws Exception
            {


                int typeid = rs.getInt(TablesTypes.DATATYPE_ID);
                String servName = ix2Service.get(typeid);
                if (servName != null)
                    obj.put(SERV, new Pair<Timestamp, Object>(cor_time, servName));
                else if (obj.get(SERV) == null)
                    obj.put(SERV, new Pair<Timestamp, Object>(cor_time, "X"));

                if (!attrs.containsKey(SERV))
                    attrs.put(SERV, new ColumnHeadBean(SERV, SERV, ListGridFieldType.TEXT.toString()));

                Pair<Timestamp, Object> o_state = obj.get(STATE);
                if (o_state != null)
                    o_state.second = (String) o_state.second + ',' + typeid;
                else
                    obj.put(STATE, new Pair<Timestamp, Object>(cor_time, "" + typeid));

                if (!attrs.containsKey(STATE))
                    attrs.put(STATE, new ColumnHeadBean(STATE, STATE, ListGridFieldType.TEXT.toString()));
            }
        });
        return resVal;
    }

    @Override
    protected void addParameters(Map mapParams)
    {
        mapParams.put(FillFromDb.DOR_COD_ATTR, new Integer[]{28});
//        mapParams.put(FillFromDb.EV_TYPE_ATTR,new Integer[] {46,47,54,57,58,56,60,61,59});
    }

    static Map<String, int[]> service2Ix = new HashMap<String, int[]>();
    static Map<Integer,String> ix2Service= new HashMap<Integer, String>();


    public static final String SERV = "o_serv";
    public static final String STATE = "o_state";
    public static final String STATUS_FACT="STATUS_FACT";
    public static final String STATUS_PL="STATUS_PL";


    static
    {
        int[] p=new int[]{54,46,47};
        service2Ix.put("П",p);
        int[] e=new int[]{59,60,61};
        service2Ix.put("Э",e);
        int[] h=new int[]{56,57,58};
        service2Ix.put("Ш",h);
        for (String s : service2Ix.keySet())
            for (int anIx : service2Ix.get(s))
                ix2Service.put(anIx, s);
        //ix2Service.put(94,"ЗАКР");
    }


//    public static void main(String[] args) throws Exception
//    {
//        HashMap mapParams = new HashMap();
//        mapParams.put(FillFromDb.DOR_COD_ATTR,new Integer[]{28});
//
//        Pair<ColumnHeadBean[], Map[]> res = new WindowsT(false).getUpdateTable(mapParams, new HashMap<String, Object>());
//
//        //Map<String,Map> id2Tuple = new HashMap<String, Map>();
////        ColumnHeadBean[] clHead = res.first;
////        for (ColumnHeadBean columnHeadBean : clHead) {
////            System.out.println(columnHeadBean.getName()+","+columnHeadBean.getColumnDesc()+","+columnHeadBean.getType());
////        }
//
////        for (Map map : res.second)
////        {
//////            map.get("")
//////            String state=(String)map.get(STATE);
////            if (!state.contains("94"))
////            {
////                System.out.println("state = " + state);
////            }
////        }
//
//
//
////        Map[] tuples = res.second;
////        for (Map tuple : tuples)
////        {
////            String id=(String)tuple.get(TablesTypes.KEY_FNAME);
////            if (id2Tuple.containsKey(id))
////            {
////
////            }
////            else
////            {
////                id2Tuple.put(id,tuple);
////                tuple.put("SRV",ix2Service.get());
////            }
////        }
//
//        System.out.println("res = " + res.second.length);
//    }

}
