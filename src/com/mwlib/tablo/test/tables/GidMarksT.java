package com.mwlib.tablo.test.tables;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 15.07.14
 * Time: 18:04
 * To change this template use File | Settings | File Templates.
 */

import com.mwlib.utils.db.Directory;
import com.mwlib.utils.db.FillFromDb;
import com.mycompany.common.FieldException;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mwlib.tablo.tables.SimpleField;
import com.smartgwt.client.types.ListGridFieldType;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 * To change this template use File | Settings | File Templates.
 */
public class GidMarksT extends BaseTable
{
    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
    {
                    new SimpleField("DOR_NAME","Дорога")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Double dor_kod=(Double)tuple.get("DOR_KOD");

                            if (Directory.isInit())
                            {
                                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod.intValue());
                                if (byDorCode!=null)
                                    return byDorCode.getNAME();
                            }
                            return String.valueOf(dor_kod);
                        }
                    },

                    new SimpleField("SERV_CHAR","Служба")
                    {
                        {
                            autofit=true;
                        }
                    },
                    new SimpleField("TNAME","Тип"),
                    new SimpleField("MRMCOLOR","Цвет"),
                    new SimpleField("STAN1_ID","Место")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Double dstan1code=(Double)tuple.get("MRCODEA");
                            Double dstan2code=(Double)tuple.get("MRCODEB");
                            int stan1code=0;
                            if (dstan1code!=null)
                                stan1code=dstan1code.intValue()/10;
                            int stan2code=0;
                            if (dstan2code!=null)
                                stan2code=dstan2code.intValue()/10;

                            if (Directory.isInit())
                            {
                                Directory.StanDesc stan1=null;
                                if (stan1code!=0)
                                    stan1 = Directory.getByStanCode(stan1code);

                                Directory.StanDesc stan2=null;
                                if (stan2code!=0)
                                    stan2 = Directory.getByStanCode(stan2code);
                                if (stan1!=null)
                                    return stan1.getNAME()+(stan2!=null?"-"+stan2.getNAME():"");
                                else if (stan2!=null)
                                    return stan2.getNAME();
                                return "";
                            }
                            else
                            {
                                return stan1code+((stan2code!=0)?"-"+stan2code:"");
                            }

                        }
                    },

                    new SimpleField("MRSWAY","Путь")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Double way=(Double)tuple.get("MRSWAY");
                            int w = way.intValue();
                            if (w ==0)
                                return "Все";
                            return ""+w;
                        }
                    },

                    new DateField("MRB","Время возникновения"),
                    new DateField("MRE","Время завершения"),

                    new SimpleField("TIME_LEN","Длительность")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Double dtlen=(Double)tuple.get("TIME_LEN");
                            int tlen=dtlen.intValue();

                            int dn=tlen/(24*60);
                            int h=(tlen-dn*24*60)/60;
                            int min=tlen-dn*24*60-h*60;
                            String res="";
                            if (dn>0) res+=dn+" д ";
                            if (h>0) res+=h+" ч ";
                            if (min>0) res+=min+" м";
                            return res;
                        }

                    },
            /*
            ВРЕМЯ ВВОДА MRDATECORR
             */
                    new DateField("MRDATECORR","Время ввода"),

                    new SimpleField("FNAME","Причина")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            String rv=(String)super.getS(column,tuple,_outTuple);
                            Double dflt=(Double)tuple.get("MRCFAULT");
                            if (dflt!=null)
                                return dflt.intValue()+" - "+rv;
                            return rv;
                        }
                    },

                    new SimpleField("MRS","Описание"),

                    new SimpleField("DOR_KOD","Дорога",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false),
    };


    @Override
    public String getTableType() {
        return TablesTypes.MARKS_GID;
    }

    protected GidMarksT(boolean test) {
        super(test);
    }

    protected static BaseTable inst;
    public static BaseTable getInstance(boolean test) throws Exception
    {
        if (inst!=null)
            return inst;
        return inst=new GidMarksT(test);
    }


    @Override
    protected FieldTranslator[] getFieldTranslator() {
        return  fieldTranslator;
    }

    @Override
    protected void addParameters(Map mapParams)
    {
        mapParams.put(FillFromDb.DOR_COD_ATTR,new Integer[] {28});
    }

    @Override
    protected Pair<ColumnHeadBean[], Map[]> getDbTable(Map mapParams, Map<String, Object> outParams) throws Exception
    {
        Map<String,FillFromDb.ParamVal> valMap = new HashMap<String,FillFromDb.ParamVal>();

        Timestamp maxTimeStamp = FillFromDb.getMaxTimeStamp(mapParams);
        valMap.put(TablesTypes.MAX_TIMESTAMP,new FillFromDb.ParamVal(-1, maxTimeStamp, Types.NULL));

        Integer dor_code = ((Integer[])mapParams.get(FillFromDb.DOR_COD_ATTR))[0];

        valMap.put(FillFromDb.DOR_COD_ATTR+"_1",new FillFromDb.ParamVal(1, dor_code, Types.INTEGER));
        valMap.put("CORTIME1",new FillFromDb.ParamVal(2, maxTimeStamp, Types.TIMESTAMP));

        Pair<ColumnHeadBean[], Map[]> resVal = FillFromDb._getDbTable(GET_MARKST, valMap, outParams, new FillFromDb.IRowOperation()
        {
            public void setObjectArc(Map<String, ColumnHeadBean> attrs, Timestamp cor_time, ResultSet rs, Map<String, Pair<Timestamp, Object>> obj) throws Exception
            {
//                int dor_kod = rs.getInt("DOR_KODI");
//                 obj.put("DOR_KODI", new Pair<Timestamp, Object>(cor_time, dor_kod));
//                if (!attrs.containsKey("DOR_KODI"))
//                    attrs.put("DOR_KODI", new ColumnHeadBean("DOR_KODI","DOR_KODI", ListGridFieldType.INTEGER.toString()));

            }
        });
        return resVal;
    }


    public static final String GET_MARKST="select \n" +
        "toj1.data_obj_id,\n" +
        "toj2.cor_time," +
        "da.attr_id," +
//        "al.attr_type," +
//        "da.value_s," +
        "CASE da.attr_id\n" +
        "  WHEN 'MRMCOLOR'\n" +
        "THEN 'STRING'\n" +
        "ELSE\n" +
        "  al.attr_type\n" +
        "END\n" +
        "AS attr_type\n" +
        ",\n" +
        "CASE da.attr_id\n" +
        "  WHEN 'MRMCOLOR'\n" +
        "  THEN ( select NAME from MARK_COLOR where color_id=da.value_f)\n" +
        "  ELSE\n" +
        "    da.value_s\n" +
        "END\n" +
        "AS value_s,"+
        "da.value_i,da.value_f,da.value_t,da.cor_time as acor_time,\n" +
        "toj2.text, da.datatype_id " +
//            ", toj2.DOR_KOD as DOR_KODI \n"+
        "from (  \n" +
        "select distinct(toj.data_obj_id) as data_obj_id from tablo_data_objects toj,tablo_data_attributes da where toj.DATATYPE_ID=90\n" +
        "and toj.cor_tip IN ('I','U') and toj.dor_kod=? \n" +
        "and toj.data_obj_id=da.data_obj_id " +
        "and toj.cor_time> ? \n"+
        ") toj1, \n" +
        "tablo_data_objects toj2,\n" +
        "tablo_data_attributes da,\n" +
        "tablo_data_attr_list al  where  \n" +
        "toj1.data_obj_id=da.data_obj_id \n" +
        "and toj2.data_obj_id=da.data_obj_id \n" +
        "and toj2.datatype_id=da.datatype_id\n" +
        " and al.attr_id=da.attr_id \n" +
        "and al.DATATYPE_ID=da.datatype_id \n"+
        "order by \n" +
        "toj2.cor_time,\n" +
        "da.data_obj_id,da.datatype_id ";

}
