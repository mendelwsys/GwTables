package com.mwlib.tablo.db.desc;

import com.google.gwt.visualization.client.AbstractDataTable;
import com.mwlib.tablo.analit2.BusinessUtils;
import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.EventProvider;
import com.mwlib.tablo.db.IMetaProvider;
import com.mwlib.tablo.db.IRowOperation;
import com.mwlib.tablo.tables.*;
import com.mwlib.tablo.test.tables.DateField;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.DiagramDesc;
import com.mycompany.common.FieldException;
import com.mycompany.common.LinkObject;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;

import com.smartgwt.client.types.ListGridFieldType;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 15.10.14
 * Time: 12:27
 * To change this template use File | Settings | File Templates.
 */
public class WarningDesc extends BaseTableDesc {

    public static final String[] SUBSCR = {"TITLE", "ОГР", "", AbstractDataTable.ColumnType.STRING.toString()};
    public static final String[] RED = {"RED", "<25", "#FF000B", AbstractDataTable.ColumnType.NUMBER.toString()};
    public static final String[] YELLOW = {"YELLOW", "[25 - 40)", "#FFFF04", AbstractDataTable.ColumnType.NUMBER.toString()};
    public static final String[] GREEN = {"GREEN", "[40 - 50)", "#27ED1C", AbstractDataTable.ColumnType.NUMBER.toString()};
    public static final String[] DARK_GREEN = {"DARK_GREEN", ">=50", "#008808", AbstractDataTable.ColumnType.NUMBER.toString()};
    public static final String[] GRAY = {"GRAY", "~", "#999999", AbstractDataTable.ColumnType.NUMBER.toString()};

    long dateCancelIndicator;

    {
        final Calendar _dateCancelIndicator = Calendar.getInstance();
        _dateCancelIndicator.set(Calendar.YEAR, 9999);
        this.dateCancelIndicator = _dateCancelIndicator.getTimeInMillis();
    }

    /*
    Теперь мы из мета-данных сможем сделать такие заголовки которые нам нужны.
    */
    private FieldTranslator[] fieldTranslator = new FieldTranslator[]
            {

                    new SimpleField("DOR_NAME", "Дорога", ListGridFieldType.TEXT.toString(), false) {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {
                            Integer dor_kod = (Integer) tuple.get(TablesTypes.DOR_CODE);
                            if (Directory.isInit()) {
                                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                                if (byDorCode != null)
                                    return byDorCode.getNAME();
                            }
                            return String.valueOf(dor_kod);
                        }
                    },

                    new AField("TLG", "Телеграмма", false) {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) {
                            String TLG_NUM = "-";
                            String TLG_DATE = "-";

                            Object tlg_num = tuple.get("TLG_NUM");

                            if (tlg_num instanceof String || tlg_num instanceof Integer)
                                TLG_NUM = String.valueOf(tlg_num);

                            Timestamp tlgTm = (Timestamp) tuple.get("TLG_DATE");
                            if (tlgTm != null)
                                TLG_DATE = DateField.toDateTimeFormatter.format(new Date(tlgTm.getTime()));
                            return TLG_NUM + "<br>" + TLG_DATE; //Телеграмма
                        }
                    },
                    new SimpleField(TablesTypes.PRED_NAME, "Пред.  "),
                    new AField("PLACE", "Место<br>действия<br>", false) {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) {
                            String stan1_name = getStringParameter(tuple, "STAN1_NAME");
                            String stan2_name = getStringParameter(tuple, "STAN2_NAME");

                            String val = stan1_name;
                            if (stan2_name != null && !stan1_name.equals(stan2_name))
                                val += " - " + stan2_name;

                            String put = getStringParameter(tuple, "PUT_TXT");
                            if (put != null) {
                                Object put_gl = tuple.get("FLG_PUTGL");
                                if (put_gl == null || "null".equalsIgnoreCase(put_gl.toString()) || "0".equalsIgnoreCase(put_gl.toString()))
                                    put = "путь " + put;
                                else
                                    put = "гл. путь " + put;

                                val += "<br>" + put;
                            }

                            Object kmn = tuple.get("KMN");
                            if (kmn != null) {
                                if (put == null)
                                    val += "<br>";

                                val += " , " + kmn + "км";

                                Object pkn = tuple.get("PKN");
                                if (pkn != null)
                                    val += " " + pkn + "пк";


                                Object kmk = tuple.get("KMK");
                                if (kmk != null) {
                                    val += " - " + kmk + "км";
                                    Object pkk = tuple.get("PKК");
                                    if (pkk != null)
                                        val += " " + pkk + "пк";
                                }
                            }
                            return val;
                        }
                    },
//                    new AField("LEN","Длина")
//                    {
//                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
//                        {
//                            Integer len=(Integer)tuple.get(name);
//                            if (len!=null)
//                                return len/1000+"."+(len%1000)/100;
//                            return "-";
//                        }
//                    },

                    new AField("LEN", "Длина (км) ", ListGridFieldType.FLOAT.toString(), true) {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {
                            Integer len = (Integer) tuple.get(name);
                            if (len != null)
                                return ((double) len) / 1000;
                            return null;
                        }
                    },

                    new DateField2("TIM_BEG", "Начало<br>действия<br>"),
                    new DateField2("TIM_OTM", "Окончание<br>действия<br>") {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {

//                            Timestamp st=(Timestamp)tuple.get(name);
//                            if (st==null || st.getTime()<dateCancelIndicator)
                            return
                                    super.getS(column, tuple, _outTuple);
//                            return "до отмены";
                        }
                    },
                    new SimpleField("W_KD_ND", "Длительность", ListGridFieldType.INTEGER.toString(), true) {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException {
                            Timestamp kd = (Timestamp) tuple.get("TIM_OTM");
                            Timestamp nd = (Timestamp) tuple.get("TIM_BEG");

                            if (nd != null && kd != null && (nd.before(kd) || nd.equals(kd))) {
                                final long l = (kd.getTime() - nd.getTime()) / (60000);
                                tuple.put("W_KD_ND", new Integer((int) l));
                                return super.getS(column, tuple, outTuple);
                            }


                            return null;
                        }
                    },
                    new AField("VPAS_VGR_VEL_VGRPOR_VSTR_VPSKOR_VPVSKOR", "Скорость<br>П/Г/Э/Гп/Стр/Скор/Вскор") {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                                throws FieldException {
                            return getRowStyle(column, tuple, _outTuple); //Скорость
                        }
                    },
                    new SimpleField("PRICH_NAME", "Причина<br>выдачи"),
                    new AField(TablesTypes.CRDURL, "Карточка") {
                        {
                            linkText = "Карточка";
                            type = ListGridFieldType.LINK.toString();
                        }

                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) {

                            LinkObject linkObject = new LinkObject(linkText, "");

                            String id = (String) tuple.get(TablesTypes.KEY_FNAME);
                            id = removeKeySeparator(id);
                            if (id != null && id.length() > 0) {
                                String[] dor2id = id.split(";");
                                linkObject.setLink("http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=" + dor2id[0] + "&pid=" + dor2id[1] + "&pids=" + dor2id[1]);
                            }
                            return linkObject;

                        }
                    },
//                    new SimpleField("PRICH_V_ID","PRICH_V_ID"), TODO Можно сгруппировать причины и получить более менее полный список их идентификаторов (возможно лучше через БД)
                    new SimpleField(TablesTypes.DOR_CODE, "№ Дороги", ListGridFieldType.INTEGER.toString(), false),

                    new SimpleField(TablesTypes.PRED_ID, "№ Предприятия", ListGridFieldType.INTEGER.toString(), false),
                    new SimpleField(TablesTypes.VID_ID, "№ Службы", ListGridFieldType.INTEGER.toString(), false),
                    new SimpleField(TablesTypes.VID_NAME, "Служба(Гр)", ListGridFieldType.TEXT.toString(), false),


                    new SimpleField(TablesTypes.KEY_FNAME, TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(), false),

                    new FieldV("V", "Огр.Скорости"),
                    new FieldV("VPAS", "Огр.Скорости П"),
                    new FieldV("VGR", "Огр.Скорости Г"),
                    new FieldV("VEL", "Огр.Скорости Э"),
                    new FieldV("VGRPOR", "Огр.Скорости Гп"),
                    new FieldV("VSTR", "Огр.Скорости Стр"),
                    new FieldV("VPSKOR", "Огр.Скорости Скор"),
                    new FieldV("VPVSKOR", "Огр.Скорости Вскор"),

                    new SimpleField(TablesTypes.ROW_STATUS, "Важность", ListGridFieldType.INTEGER.toString(), false) {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> outTuple) throws FieldException {
                            return outTuple.get(TablesTypes.ROW_STATUS);
                        }
                    },

                    new SimpleField(TablesTypes.EVTYPE, "Событие", ListGridFieldType.TEXT.toString(), false) {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) {
                            return TablesTypes.WARNINGS;
                        }
                    },
                    new SimpleField(TablesTypes.EVTYPE_NAME, "Тип События", ListGridFieldType.TEXT.toString(), false) {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) {
                            return "ПРЕДУПРЕЖДЕНИЯ";
                        }
                    },

                    new SimpleField(TablesTypes.DATATYPE_ID,"Ид.типа события",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.ACTUAL,"Актуальность",ListGridFieldType.INTEGER.toString(),false),
                    new DateField2(EventProvider.DATE_MIN_ND,"Время получения записи",false),
                    new DateField2(EventProvider.COR_MAX_TIME,"Время изменения записи",false),

                    new SimpleField("FIXED_END_DATE","Фикс. окончание",ListGridFieldType.INTEGER.toString(),false),

            };


    private static class FieldV extends SimpleFieldB {

        public FieldV(String name, String title) {
            super(name, title, ListGridFieldType.INTEGER.toString(), false);
        }

        public Object getS(Map<String, ColumnHeadBean> columns, Map tuple, Map<String, Object> _outTuple) throws FieldException {
            Object rv = super.getS(columns, tuple, _outTuple);
            if (rv instanceof Integer) {
                int rrv = (Integer) rv;
                if (rrv < 0)
                    return null;
            }
            return rv;
        }

    }

    public static Object getRowStyle(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {
        return getV(tuple, "VPAS", _outTuple) + "/" + getV(tuple, "VGR", _outTuple) + "/" + getV(tuple, "VEL", _outTuple) + "/" + getV(tuple, "VGRPOR", _outTuple) + "/" + getV(tuple, "VSTR", _outTuple) + "/" + getV(tuple, "VPSKOR", _outTuple) + "/" + getV(tuple, "VPVSKOR", _outTuple);
    }


    private String getStringParameter(Map tuple, String parName) {

        String rv = (String) tuple.get(parName);
        if (rv == null || "NULL".equalsIgnoreCase(rv))
            rv = null;
        return rv;
    }


    public static final String ROW_COLOR = "rowcolor";

    public static String getV(Map tuple, String vname, Map<String, Object> _outTuple) {
        String VPAS = "-";
        Integer vVal = (Integer) tuple.get(vname);
        if (vVal == null || vVal < 0)
            VPAS = "-";
        else {
            Object o = _outTuple.get(ROW_COLOR);
            if (o == null) {
                int[] ogrs = new int[7];

                ogrs[0] = tuple.get("VPAS") == null || ((Integer) tuple.get("VPAS")) == -1 ? Integer.MAX_VALUE : (Integer) tuple.get("VPAS");
                ogrs[1] = tuple.get("VGR") == null || ((Integer) tuple.get("VGR")) == -1 ? Integer.MAX_VALUE : (Integer) tuple.get("VGR");
                ogrs[2] = tuple.get("VEL") == null || ((Integer) tuple.get("VEL")) == -1 ? Integer.MAX_VALUE : (Integer) tuple.get("VEL");
                ogrs[3] = tuple.get("VGRPOR") == null || ((Integer) tuple.get("VGRPOR")) == -1 ? Integer.MAX_VALUE : (Integer) tuple.get("VGRPOR");
                ogrs[4] = tuple.get("VSTR") == null || ((Integer) tuple.get("VSTR")) == -1 ? Integer.MAX_VALUE : (Integer) tuple.get("VSTR");
                ogrs[5] = tuple.get("VPSKOR") == null || ((Integer) tuple.get("VPSKOR")) == -1 ? Integer.MAX_VALUE : (Integer) tuple.get("VPSKOR");
                ogrs[6] = tuple.get("VPVSKOR") == null || ((Integer) tuple.get("VPVSKOR")) == -1 ? Integer.MAX_VALUE : (Integer) tuple.get("VPVSKOR");

                Arrays.sort(ogrs);
                if (ogrs[0] < 25) {
                    _outTuple.put(TablesTypes.ROW_STYLE, "background-color:" + RED[2] + ";");
                    _outTuple.put(ROW_COLOR, RED[0]);
                    _outTuple.put(TablesTypes.ROW_STATUS, 0);


                } else if (ogrs[0] >= 25 && ogrs[0] < 40) {
                    _outTuple.put(TablesTypes.ROW_STYLE, "background-color:" + YELLOW[2] + ";");
                    _outTuple.put(ROW_COLOR, YELLOW[0]);
                    _outTuple.put(TablesTypes.ROW_STATUS, 1);


                } else if (ogrs[0] >= 40 && ogrs[0] < 50) {
                    _outTuple.put(TablesTypes.ROW_STYLE, "background-color:" + DARK_GREEN[2] + ";");
                    _outTuple.put(ROW_COLOR, DARK_GREEN[0]);
                    _outTuple.put(TablesTypes.ROW_STATUS, 2);


                } else if (ogrs[0] >= 50 && ogrs[0] != Integer.MAX_VALUE) {
                    _outTuple.put(TablesTypes.ROW_STYLE, "background-color:" + GREEN[2] + ";");
                    _outTuple.put(ROW_COLOR, GREEN[0]);
                    _outTuple.put(TablesTypes.ROW_STATUS, 3);


                } else {
                    _outTuple.put(ROW_COLOR, GRAY[0]);
                    _outTuple.put(TablesTypes.ROW_STATUS, 4);

                }
            }


            /*

            if (vVal<=15)
            {
                _outTuple.put(TablesTypes.ROW_STYLE,"background-color:red;");
                _outTuple.put(ROW_COLOR, RED[0]);
                _outTuple.put(TablesTypes.ROW_STATUS, 0);
            }
            else if (vVal<=25)
            {


                if (o ==null || !o.equals(RED[0]))
                {
                    _outTuple.put(TablesTypes.ROW_STYLE,"background-color:#DDDD00;");
                    _outTuple.put(ROW_COLOR, YELLOW[0]);
                    _outTuple.put(TablesTypes.ROW_STATUS, 1);
                }
            }
            else if (vVal<=40)
            {
                if (o ==null || (!o.equals(RED[0]) && !o.equals(YELLOW[0])))
                {
                    _outTuple.put(TablesTypes.ROW_STYLE,"background-color:#99DD00;");
                    _outTuple.put(ROW_COLOR, GREEN[0]);
                    _outTuple.put(TablesTypes.ROW_STATUS, 2);
                }
            }
            else if (o ==null || (!o.equals(RED[0]) && !o.equals(YELLOW[0]) && !o.equals(GREEN[0])))
            {
                   _outTuple.put(ROW_COLOR, GRAY[0]);
                   _outTuple.put(TablesTypes.ROW_STATUS, 3);
            }*/


            VPAS = "" + vVal;
        }
        return VPAS;
    }
     /*"ОГР.СК."*/


    protected DiagramDesc getDiagramDesc(List<Map<String, Object>> resMap) {
        DiagramDesc desc = new DiagramDesc();
        desc.setTitle("Ограничения скорости");
        desc.setType("ColumnChart");
        desc.setColumnDesc(new String[][]{SUBSCR, RED, YELLOW, DARK_GREEN, GREEN, GRAY});
        desc.setwType(TablesTypes.WARNINGS);


        Map<String, Object> cnt = new HashMap<String, Object>();
        cnt.put(SUBSCR[0], "ОГР.СК.");


        for (Map<String, Object> objectMap : resMap) {
            String attr = (String) objectMap.get(ROW_COLOR);
            Integer act = (Integer) objectMap.get(TablesTypes.ACTUAL);
            if (attr != null) {
                Integer i = (Integer) cnt.get(attr);
                if (i == null)
                    cnt.put(attr, i = 0);
                if (act > 0)
                    cnt.put(attr, i + 1);
                else
                    cnt.put(attr, i - 1);
            }
        }
        desc.setTuples(new Map[]{cnt});
        return desc;
    }

    @Override
    public String getTableType() {
        return tableTypeName;
    }

    @Override
    public FieldTranslator[] getFieldTranslator() {
        return fieldTranslator;
    }


    public void addMeta2Type(IMetaProvider metaProvider) {
        BusinessUtils.fillMetaByVid(metaProvider, getDataTypes()[0]);
    }

    @Override
    protected IRowOperation _getRowOperation() {
        return new IRowOperation() {

            public void setObjectAttr(IMetaProvider metaProvider, ColumnHeadBean attr, ResultSet rs, Map<String, Object> tuple) throws Exception {
                int typeid = rs.getInt(TablesTypes.DATATYPE_ID);
                for (int tableType : tableTypes) {
                    if (tableType == typeid) {
                        BusinessUtils.fillVidTupleByPredId(tuple);
                        break;
                    }
                }

            }
        };

    }

    @Override
    public int[] getDataTypes() {
        return tableTypes;
    }

    public WarningDesc(boolean test) {
        super(test);
    }

    public WarningDesc(boolean test, int[] tableTypes) {
        super(test);
        this.tableTypes = tableTypes;
    }

    public WarningDesc(boolean test, String tableTypeName, int[] tableTypes) {
        this(test);
        this.tableTypeName = tableTypeName;
        this.tableTypes = tableTypes;
    }

    private String tableTypeName = TablesTypes.WARNINGS;
    private int[] tableTypes = new int[]{44};
}
