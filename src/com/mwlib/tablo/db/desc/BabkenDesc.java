package com.mwlib.tablo.db.desc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mwlib.tablo.tables.SimpleField;
import com.mycompany.common.TablesTypes;
import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.IMetaProvider;
import com.mwlib.tablo.db.IRowOperation;
import com.smartgwt.client.types.ListGridFieldType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: NEMO
 * Date: 04.10.19
 * Time: 17:15
 * Тестовый описатель данных
 */
public class BabkenDesc extends BaseTableDesc
{

    public static final int BABKENTYPEID = 1001;
    private List dataList =  new LinkedList();
    private FieldTranslator[] flsTrans =  new FieldTranslator[0];

    public BabkenDesc(boolean test) {
        super(test);
        try
        {
            BufferedReader fis = new BufferedReader( new FileReader("C:\\PapaWK\\Projects\\JavaProj\\WsReCall\\o.json"));
            String rs = fis.readLine();
            Gson gson = new GsonBuilder().serializeNulls().create();
            Type type = new TypeToken<List<Map<String,String>>>(){}.getType();
            dataList = gson.fromJson(rs, type);
            if (dataList.size()>0)
            {
                Map<String,Object> flds= (Map<String, Object>) dataList.get(0);

                flds.put(TablesTypes.DATATYPE_ID, BabkenDesc.BABKENTYPEID);
//                flds.put(TablesTypes.OBJ_OSN_ID,1);
//                flds.put(TablesTypes.DATA_OBJ_ID,BabkenDesc.BABKENTYPEID);
                flds.put(TablesTypes.KEY_FNAME, 0);
                flds.put(TablesTypes.DOR_CODE, 1);
                List<FieldTranslator>  lflsTrans =  new LinkedList<FieldTranslator>();
                for (String fldName : flds.keySet())
                {
                    if (fldName.equals(TablesTypes.DOR_CODE))
                        lflsTrans.add( new SimpleField(fldName,fldName,ListGridFieldType.INTEGER.toString(),true));
                    else
                        lflsTrans.add( new SimpleField(fldName,fldName));
                }
                this.flsTrans = lflsTrans.toArray(new FieldTranslator[lflsTrans.size()]);
            }
        }
        catch (Exception e)
        {

        }

    }

    @Override
    public String getTableType() {
        return TablesTypes.BABKEN_TYPE;
    }

    @Override
    public FieldTranslator[] getFieldTranslator() {
        return flsTrans;
    }

    @Override
    public void addMeta2Type(IMetaProvider metaProvider)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected IRowOperation _getRowOperation()
    {
//        return new IRowOperation()
//        {
//
//            @Override
//            public void setObjectAttr(IMetaProvider metaProvider, ColumnHeadBean attr, ResultSet rs, Map<String, Object> tuple) throws Exception
//            {
//                {
//                    int typeid = rs.getInt(TablesTypes.DATATYPE_ID); //Наполнение по typeId
//
//                }
//            }
//        };
        return null;
    }

    @Override
    public int[] getDataTypes() {
        return new int[]{BABKENTYPEID};
    }
}
