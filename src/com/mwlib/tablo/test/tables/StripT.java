package com.mwlib.tablo.test.tables;

import com.mwlib.utils.db.Directory;
import com.mwlib.utils.db.FillFromDb;
import com.mycompany.common.FieldException;
import com.mycompany.common.Pair;
import com.mycompany.common.StripCNST;
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
 * Лента событий (Таблица, все события (предупреждения, окна, нарушения, отказы ))
 */
public class StripT extends BaseTable
{

    public static final String STATE_DESC="o_state_desc";

    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
    {
                    new SimpleField(StripCNST.DOR_NAME,"Дорога",ListGridFieldType.TEXT.toString(),false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Integer dor_kod=(Integer)tuple.get(StripCNST.DOR_KOD);
                            if (Directory.isInit())
                            {
                                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                                if (byDorCode!=null)
                                    return byDorCode.getNAME();
                            }
                            return String.valueOf(dor_kod);
                        }
                    },

                    new SimpleField(StripCNST.EVENTID,"№")
                    {

                        private AField winf=new SimpleField("ID_Z","№");
                        private AField warnf=new SimpleField("PREDUPR_ID","№");
                        private AField vf=new SimpleField("ID_VIOL","№");
                        //private AField rf=new SimpleField("ID_VIOL","№");

                        {
                            autofit=true;
                        }

                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            String evName= (String) tuple.get(StripCNST.EVENT);
                            if (evName.equals(StripCNST.WIN_NAME))
                                return winf.getS(column, tuple, outTuple);
                            else if (evName.equals(StripCNST.WARN_NAME))
                                return warnf.getS(column,tuple,outTuple);
                            else
                            /*
                            if (evName.equals(VIOL_NAME) || evName.equals(REFUSE_NAME))
                             */
                            {
                                return vf.getS(column,tuple,outTuple);
                            }
                        }
                    },
                    new SimpleField(StripCNST.SERV,"Служба")
                    {
                        {
                            autofit=true;
                        }
                    },

                    new SimpleField(StripCNST.EVENT,"Событие")
                    {
                        {
                            autofit=true;
                        }
                    },

                    new AField(StripCNST.PLACE,"Место")
                    {
                        {
                            autofit=true;
                        }

                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {

                            //TODO Здесь необходимо в зависимости от идентикатора события
                            //TODO определять место (в частности для тех. нарушений определяем отказов место по PLACE)
                            /*
                            1. Т.е. по сути лента эта всего лишь одна из таблиц, в поля которой пишуться данные
                            из разных событий (мы события определем по  toj2.datatype_id)

                            что касается теперь функции приоритета события - это всего лишь сортировка и фильтр

                            что касается функции назначения событий для определнных идентификаторов пользователя.

                            фильтр пользователя содержит список идентификаторов событий, и отдает только эти события.


                            Как начать строить?  Где у нас данные по профилям, в местной БД, например в МуSQL
                           (на время строительства). Там разворачиваем и экспериментируем.

                            На серверную часть накладываем фильтр с идентифкаторами или типами,
                            сохранем функцию эту в БД. Применяем во время исполнения запроса, т.е. необходимо
                            расширить класс БД для того что бы можно применять к нему фильтры, во время анализа
                            получившихся объектов вызываем последовательно фильтр как для слоя карты и смотрим
                            подходит ли объект или нет.

                             Зам: В некоторых случаях притменение фильтров может быть ускорено применением их
                             до агрегации событий (в простейшем случае кстати ,фильтрация по идентификаторам событий)

                             */

                            String evName= (String) tuple.get(StripCNST.EVENT);
                            if (evName.equals(StripCNST.WARN_NAME) || evName.equals(StripCNST.WIN_NAME))
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
                                else if (stan1code!=null)
                                    return  stan1code.toString();
                            }
                            else
                            /*
                            if (evName.equals(VIOL_NAME) || evName.equals(REFUSE_NAME))
                             */
                            {
                                Object place = tuple.get("PLACE");
                                if (place!=null)
                                    return place.toString();
                            }
                            return  "-";
                        }
                    },



                    new SimpleField(StripCNST.PRED_ID,"Исполнитель")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            Integer pred_id=(Integer)tuple.get(StripCNST.PRED_ID);
                            if (pred_id!=null && pred_id!=0)
                            {
                                Directory.Pred pred = Directory.getByPredId(pred_id);
                                if (pred!=null)
                                    return pred.getSNAME();
                            }
                            return "";
                        }
                    },

                    new SimpleField(StripCNST.COMMENT,"Работы")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            String wkName= (String) super.getS(column,tuple,outTuple);
                            String vname=(String)tuple.get("OBJECT_VNAME");
                            if (vname!=null)
                                return "<b>"+vname+":</b><br>"+wkName;
                            else
                                return wkName; //TODO Возможно сюда надо еще выдавать дополнительную информацию в зависимости
                               // от событий (Как для окон в "OBJECT_VNAME")
                        }
                    },//


                    new SimpleField(STATE_DESC,"Описание")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException
                        {
                            String evName= (String) tuple.get(StripCNST.EVENT);
                            if (evName.equals(StripCNST.WIN_NAME))
                                return WindowsT.getRowStyle(column,tuple,outTuple);
                            else if (evName.equals(StripCNST.WARN_NAME))
                                return WarningsT.getRowStyle(column,tuple,outTuple);
                            else if (evName.equals(StripCNST.VIOL_NAME))
                                  return ViolationT.getRowStyle(column,tuple,outTuple);
                            else if (evName.equals(StripCNST.REFUSE_NAME))
                                return RefuseT.getRowStyle(column,tuple,outTuple);
                            return "";
                        }
                    },


                    new DateField(StripCNST.ND,"Начало") ,
                    new DateField(StripCNST.KD,"Конец") ,

//                    new DateField("DT_ND","Начало<br>действия(ГИД)<br>") ,
//                    new DateField("DT_KD","Конец<br>действия(ГИД)<br>") ,

                    new SimpleField(StripCNST.DOR_KOD,"№ Дороги",ListGridFieldType.TEXT.toString(),false),
                    new SimpleField(StripCNST.STATE,"Состояния", ListGridFieldType.TEXT.toString(),false),
                    new SimpleField(StripCNST.STATUS_FACT,"Исполнение",ListGridFieldType.INTEGER.toString(),false), //TODO Исключить
                    new SimpleField(StripCNST.STATUS_PL,"Планирование",ListGridFieldType.INTEGER.toString(),false), //TODO Исключить
                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false),
    };

    private String getTimeInterval(Timestamp nd, long current,int m) {
        long min = m*(nd.getTime()-current) / 60000;
        if (min>60)
            return " "+(min/60)+"ч :"+min%60+" мин";
        else
            return " "+min+" мин";
    }

    protected static BaseTable inst;

    protected StripT(boolean test) {
        super(test);
    }

    public static BaseTable getInstance(boolean test) throws Exception
    {

        if (inst!=null)
            return inst;
        return inst=new StripT(test);
    }

    @Override
    protected FieldTranslator[] getFieldTranslator() {
        return  fieldTranslator;
    }

    public static final String GET_EVENTS ="select toj1.data_obj_id,\n" +
            "toj2.cor_time,\n" +
            "da.attr_id,al.attr_type,da.value_s,da.value_i,da.value_f,da.value_t,da.cor_time \n" +
            "as acor_time\n" +
            ",toj2.text," +
            " toj2.datatype_id  \n" +
            "from (  \n" +
            "select distinct(toj.data_obj_id) as data_obj_id from tablo_data_objects toj,tablo_data_attributes da " +
            "where toj.DATATYPE_ID IN (" +
            "46,47,54,57,58,56,60,61,59,94," + // -- Окна
            "68,69,70,71,72," +// --  нарушения
            "48,49,50,51,73," +//-- отказы
            "44" + // --предупреждения
            ")\n" +
            "and toj.cor_tip IN ('I','U') and toj.dor_kod<> ? \n" +
            "and toj.data_obj_id=da.data_obj_id  and da.attr_id='KD' \n" +
            "and (da.value_t>= ? ) \n" + //TO_TIMESTAMP ('08-07-14 15:50:00', 'DD-MM-RR HH24:MI:SS')
            "and toj.cor_time> ? \n"+
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

        Pair<ColumnHeadBean[], Map[]> resVal = FillFromDb._getDbTable(GET_EVENTS, valMap, outParams, new FillFromDb.IRowOperation()
        {
            public void setObjectArc(Map<String, ColumnHeadBean> attrs, Timestamp cor_time, ResultSet rs, Map<String, Pair<Timestamp, Object>> obj) throws Exception
            {

            int typeid = rs.getInt(TablesTypes.DATATYPE_ID);

            {
                String servName = ix2Service.get(typeid);
                String serv = StripCNST.SERV;

                if (servName != null)
                    obj.put(serv, new Pair<Timestamp, Object>(cor_time, servName));
                else if (obj.get(serv) == null)
                    obj.put(serv, new Pair<Timestamp, Object>(cor_time, "-"));
                if (!attrs.containsKey(serv))
                    attrs.put(serv, new ColumnHeadBean(serv, serv, ListGridFieldType.TEXT.toString()));
            }

            {
                String servName = ix2event.get(typeid);
                String serv = StripCNST.EVENT;
                if (servName != null)
                    obj.put(serv, new Pair<Timestamp, Object>(cor_time, servName));
                else if (obj.get(serv) == null)
                    obj.put(serv, new Pair<Timestamp, Object>(cor_time, "-"));
                if (!attrs.containsKey(serv))
                    attrs.put(serv, new ColumnHeadBean(serv, serv, ListGridFieldType.TEXT.toString()));
            }

                Pair<Timestamp, Object> o_state = obj.get(StripCNST.STATE);
                if (o_state != null)
                    o_state.second = (String) o_state.second + ',' + typeid;
                else
                    obj.put(StripCNST.STATE, new Pair<Timestamp, Object>(cor_time, "" + typeid));

                if (!attrs.containsKey(StripCNST.STATE))
                    attrs.put(StripCNST.STATE, new ColumnHeadBean(StripCNST.STATE, StripCNST.STATE, ListGridFieldType.TEXT.toString()));
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

    static Map<String, int[]> event2Ix = new HashMap<String, int[]>();
    static Map<Integer,String> ix2event= new HashMap<Integer, String>();

    static Map<String, int[]> service2Ix = new HashMap<String, int[]>();
    static Map<Integer,String> ix2Service= new HashMap<Integer, String>();





    private static int[] fA(int[] a1, int[] a2)
    {
        int[] rv=new int[a1.length+a2.length];
        System.arraycopy(a1,0,rv,0,a1.length);
        System.arraycopy(a2,0,rv,a1.length,a2.length);
        return rv;
    }


    static
    {

        int[] wEv=new int[]{46,47,54,57,58,56,60,61,59,94};
        int[] vEv=new int[]{68,69,70,71,72};
        int[] rEv=new int[]{48,49,50,51,73};
        int[] wrEv=new int[]{44};

        event2Ix.put(StripCNST.WIN_NAME,wEv);
        event2Ix.put(StripCNST.VIOL_NAME,vEv);
        event2Ix.put(StripCNST.REFUSE_NAME,rEv);
        event2Ix.put(StripCNST.WARN_NAME,wrEv);

        for (String s : event2Ix.keySet())
            for (int anIx : event2Ix.get(s))
                ix2event.put(anIx, s);


        int[] wP=new int[]{54,46,47};
        int[] wE=new int[]{59,60,61};
        int[] wH=new int[]{56,57,58};

        int[] vP=new int[]{68};
        int[] vH=new int[]{69};
        int[] vE=new int[]{70};
        int[] vV=new int[]{71};

        int[] rP=new int[]{48};
        int[] rH=new int[]{49};
        int[] rE=new int[]{50};
        int[] rV=new int[]{51};



        service2Ix.put(StripCNST.WAY_NM, fA(fA(wP, vP),rP));

        service2Ix.put(StripCNST.EL_NAME,fA(fA(wE, vE),rE));

        service2Ix.put(StripCNST.CTRL_NAME,fA(fA(wH, vH),rH));

        service2Ix.put(StripCNST.VAG_NAME,fA(vV, rV));

        for (String s : service2Ix.keySet())
            for (int anIx : service2Ix.get(s))
                ix2Service.put(anIx, s);
        //ix2Service.put(94,"ЗАКР");
   }

    public String getTableType()
    {
        return TablesTypes.LENTA;
    }



}
