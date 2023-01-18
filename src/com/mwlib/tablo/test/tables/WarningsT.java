package com.mwlib.tablo.test.tables;

import com.google.gwt.visualization.client.AbstractDataTable;
import com.mwlib.utils.db.FillFromDb;
import com.mycompany.common.DiagramDesc;
import com.mycompany.common.FieldException;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.tables.AField;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mwlib.tablo.tables.SimpleField;
import com.smartgwt.client.types.ListGridFieldType;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 23.05.14
 * Time: 16:30
 * Тестовый класс для проверки возможностей и проектирования интерфейса
 */
public class WarningsT extends BaseTable {



    public static final String[] SUBSCR = {"TITLE","ОГР","",AbstractDataTable.ColumnType.STRING.toString()};
    public static final String[] RED = {"RED","<=15","red",AbstractDataTable.ColumnType.NUMBER.toString()};
    public static final String[] YELLOW = {"YELLOW","(15 - 25]","#DDDD00",AbstractDataTable.ColumnType.NUMBER.toString()};
    public static final String[] GREEN = {"GREEN","(25 - 40]","#99DD00",AbstractDataTable.ColumnType.NUMBER.toString()};
    public static final String[] GRAY = {"GRAY",">40","#999999",AbstractDataTable.ColumnType.NUMBER.toString()};


    protected static BaseTable inst;
    public static BaseTable getInstance(boolean test) throws Exception
    {

        if (inst!=null)
            return inst;
        return inst=new WarningsT(test);
    }


    /*
    Теперь мы из мета-данных сможем сделать такие заголовки которые нам нужны.
    */
    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
            {
                    new AField("TLG","Телеграмма",true)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            String TLG_NUM ="-";
                            String TLG_DATE="-";

                            Object tlg_num = tuple.get("TLG_NUM");

                            if (tlg_num instanceof String || tlg_num instanceof Integer)
                                TLG_NUM=String.valueOf(tlg_num);

                            Timestamp tlgTm=(Timestamp)tuple.get("TLG_DATE");
                            if (tlgTm!=null)
                                TLG_DATE= DateField.toDateTimeFormatter.format(new Date(tlgTm.getTime()));
                            return TLG_NUM+"<br>"+TLG_DATE; //Телеграмма
                        }
                    },
                    new SimpleField("PRED_NAME","Пред.  "),
                    new AField("PLACE","Место<br>действия<br>",true) {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            String stan1_name = getStringParameter(tuple, "STAN1_NAME");
                            String stan2_name = getStringParameter(tuple, "STAN2_NAME");

                            String val=stan1_name;
                            if (stan2_name!=null && !stan1_name.equals(stan2_name))
                                val+=" - "+stan2_name;

                            String put = getStringParameter(tuple, "PUT_TXT");
                            if (put!=null)
                            {
                                Object put_gl = tuple.get("FLG_PUTGL");
                                if (put_gl==null || "null".equalsIgnoreCase(put_gl.toString()) || "0".equalsIgnoreCase(put_gl.toString()))
                                    put = "путь "+put;
                                else
                                    put = "гл. путь "+put;

                                val+="<br>"+put;
                            }

                            Object kmn = tuple.get("KMN");
                            if (kmn!=null)
                            {
                                if (put==null)
                                    val+="<br>";

                                val+=" , "+kmn +"км";

                                Object pkn = tuple.get("PKN");
                                if (pkn!=null)
                                    val+=" "+pkn +"пк";


                                Object kmk = tuple.get("KMK");
                                if (kmk!=null)
                                {
                                    val+=" - "+kmk +"км";
                                    Object pkk = tuple.get("PKК");
                                    if (pkk!=null)
                                      val+=" "+pkk +"пк";
                                }
                            }
                            return val;
                        }
                    },
                    new AField("LEN","Длина")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Integer len=(Integer)tuple.get(name);
                            if (len!=null)
                                return len/1000+"."+(len%1000)/100;
                            return "-";
                        }
                    },
                    new DateField("TIM_BEG","Начало<br>действия<br>") ,
                    new DateField("TIM_OTM","Окончание<br>действия<br>")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            String s = (String)super.getS(column, tuple, _outTuple);
                            if (s!=null && s.contains("31.12.99"))
                                s = "до отмены";
                            return s;
                        }
                    },
                    new AField("VPAS_VGR_VEL_VGRPOR_VSTR","Скорость<br>П/Г/Э/Гп/Стр")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                                throws FieldException
                        {
                            return getRowStyle(column,tuple, _outTuple); //Скорость
                        }
                    },
                    new SimpleField("PRICH_NAME","Причина<br>выдачи"),
                    new AField("CRDURL","Карточка")
                    {
                        {
                            linkText="Карточка";
                            type=ListGridFieldType.LINK.toString();
                        }
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {

                            String id=(String)tuple.get(TablesTypes.KEY_FNAME);
                            if (id!=null && id.length()>0)
                            {
                                String[] dor2id=id.split(";");
                                return "http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod="+dor2id[0]+"&pid="+dor2id[1]+"&pids="+dor2id[1];
                            }
                            return "";

                        }
                    },
//                    new SimpleField("PRICH_V_ID","PRICH_V_ID"), TODO Можно сгруппировать причины и получить более менее полный список их идентификаторов (возможно лучше через БД)
                    new SimpleField("DOR_KOD","Дорога",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false)

//                    ,
//                    new AField("actual","actual", ListGridFieldType.INTEGER.toString(),false)
//                    {
//                        public Object getS(ColumnHeadBean[] column, Map tuple) throws FieldException {
//                            return 1;
//                        }
//                    }
            };

    public static Object getRowStyle(ColumnHeadBean[] column,Map tuple, Map<String, Object> _outTuple) throws FieldException
    {
        return getV(tuple,"VPAS", _outTuple)+"/"+getV(tuple,"VGR", _outTuple)+"/"+getV(tuple,"VEL", _outTuple)+"/"+getV(tuple,"VGRPOR", _outTuple)+"/"+getV(tuple,"VSTR", _outTuple);
    }

    private String getStringParameter(Map tuple, String parName)
    {

        String rv = (String) tuple.get(parName);
        if (rv==null || "NULL".equalsIgnoreCase(rv))
            rv=null;
        return rv;
    }
    public static final String ROW_COLOR="rowcolor";

    public static String getV(Map tuple, String vname, Map<String, Object> _outTuple)
    {
        String VPAS="-";
        Integer vVal=(Integer)tuple.get(vname);
        if (vVal==null || vVal<0)
            VPAS="-";
        else
        {
            Object o = _outTuple.get(ROW_COLOR);

            if (vVal<=15)
            {
                _outTuple.put(TablesTypes.ROW_STYLE,"background-color:red;");
                _outTuple.put(ROW_COLOR, RED[0]);
            }
            else if (vVal<=25)
            {


                if (o ==null || !o.equals(RED[0]))
                {
                    _outTuple.put(TablesTypes.ROW_STYLE,"background-color:#DDDD00;");
                    _outTuple.put(ROW_COLOR, YELLOW[0]);
                }
            }
            else if (vVal<=40)
            {
                if (o ==null || (!o.equals(RED[0]) && !o.equals(YELLOW[0])))
                {
                    _outTuple.put(TablesTypes.ROW_STYLE,"background-color:#99DD00;");
                    _outTuple.put(ROW_COLOR, GREEN[0]);
                }
            }
            else if (o ==null || (!o.equals(RED[0]) && !o.equals(YELLOW[0]) && !o.equals(GREEN[0])))
            {
                   _outTuple.put(ROW_COLOR, GRAY[0]);
            }


            VPAS=""+vVal;
        }
        return VPAS;
    }
     /*"ОГР.СК."*/


    protected DiagramDesc getDiagramDesc(List<Map<String, Object>> resMap)
    {
        DiagramDesc desc = new DiagramDesc();
        desc.setTitle("Ограничения скорости");
        desc.setType("ColumnChart");
        desc.setColumnDesc(new String[][]{SUBSCR,RED, YELLOW, GREEN, GRAY});
        desc.setwType(TablesTypes.WARNINGS);


        Map<String,Object> cnt=new HashMap<String, Object>();
        cnt.put(SUBSCR[0],"ОГР.СК.");


        for (Map<String, Object> objectMap : resMap)
        {
            String attr=(String)objectMap.get(ROW_COLOR);
            Integer act=(Integer)objectMap.get(TablesTypes.ACTUAL);
            if (attr!=null)
            {
                Integer i=(Integer)cnt.get(attr);
                if (i ==null)
                    cnt.put(attr,i = 0);
               if (act>0)
                   cnt.put(attr,i+1);
                else
                   cnt.put(attr,i-1);
            }
        }
        desc.setTuples(new Map[]{cnt});
        return desc;
    }

    @Override
    public String getTableType() {
        return TablesTypes.WARNINGS;
    }

    public WarningsT(boolean  test) throws Exception
    {
        super(test);
    }


    @Override
    protected FieldTranslator[] getFieldTranslator() {
        return fieldTranslator;
    }

    @Override
    protected Pair<ColumnHeadBean[], Map[]> getDbTable(Map mapParams, Map<String, Object> outParams) throws Exception
    {
        return FillFromDb._getDbTable(mapParams, outParams);
    }

    @Override
    protected void addParameters(Map mapParams) {
        mapParams.put(FillFromDb.DOR_COD_ATTR,new Integer[] {28});
        mapParams.put(FillFromDb.EV_TYPE_ATTR,new Integer[] {44});
    }


    protected Map setFillParameters()
    {
        Map mapParams = super.setFillParameters();
        mapParams.put(FillFromDb.EV_TYPE_ATTR,new Integer[] {44});
        return mapParams;
    }

}
