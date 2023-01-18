package com.mwlib.tablo.test.tables;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 15.07.14
 * Time: 18:04
 * To change this template use File | Settings | File Templates.
 */

import com.mwlib.utils.db.FillFromDb;
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
public class VagInTORT extends BaseTable
{
    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
    {
                    new SimpleField("DOR_COD","Дорога",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false),
    };


    @Override
    public String getTableType() {
        return TablesTypes.VAGTOR;
    }


    protected VagInTORT(boolean test) {
        super(test);
    }

    protected static BaseTable inst;
    public static BaseTable getInstance(boolean test) throws Exception
    {

        if (inst!=null)
            return inst;
        return inst=new VagInTORT(test);
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

        Pair<ColumnHeadBean[], Map[]> resVal = FillFromDb._getDbTable(GET_W_LOSTT, valMap, outParams, new FillFromDb.IRowOperation()
        {
            public void setObjectArc(Map<String, ColumnHeadBean> attrs, Timestamp cor_time, ResultSet rs, Map<String, Pair<Timestamp, Object>> obj) throws Exception
            {
                int typeid = rs.getInt(TablesTypes.DATATYPE_ID);


//                 obj.put(SERV_IX, new Pair<Timestamp, Object>(cor_time, typeid));
//                if (!attrs.containsKey(SERV_IX))
//                    attrs.put(SERV_IX, new ColumnHeadBean(SERV_IX, SERV_IX, ListGridFieldType.INTEGER.toString()));
//
//                String servName = ix2Service.get(typeid);
//                if (servName != null)
//                    obj.put(SERV_NAME, new Pair<Timestamp, Object>(cor_time, servName));
//                else if (obj.get(SERV_NAME) == null)
//                    obj.put(SERV_NAME, new Pair<Timestamp, Object>(cor_time, "X"));
//
//                if (!attrs.containsKey(SERV_NAME))
//                    attrs.put(SERV_NAME, new ColumnHeadBean(SERV_NAME, SERV_NAME, ListGridFieldType.TEXT.toString()));

            }
        });
        return resVal;
    }


    public static final String GET_W_LOSTT="select \n" +
        "toj1.data_obj_id,\n" +
        "toj2.cor_time," +
        "da.attr_id,al.attr_type,da.value_s,da.value_i,da.value_f,da.value_t,da.cor_time as acor_time,\n" +
        "toj2.text, da.datatype_id \n"+
        "from (  \n" +
        "select distinct(toj.data_obj_id) as data_obj_id from tablo_data_objects toj,tablo_data_attributes da where toj.DATATYPE_ID=78\n" +
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
        "and al.attr_id=da.attr_id \n" +
        "and al.DATATYPE_ID=da.datatype_id \n"+
         "order by \n" +
        "toj2.cor_time,\n" +
        "da.data_obj_id,da.datatype_id ";

}
