package com.mwlib.tablo.db.desc;

import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.IMetaProvider;
import com.mwlib.tablo.db.IRowOperation;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;

import java.sql.ResultSet;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 * Контроль выдачи локомотива
 */
public class LocReqDesc extends BaseTableDesc
{
    private FieldTranslator[] fieldTranslator=new FieldTranslator[0];

    public LocReqDesc(boolean test) {
        super(test);
    }

    @Override
    public String getTableType() {
        return TablesTypes.LOCREQ;
    }

    @Override
    public FieldTranslator[] getFieldTranslator() {
        return  fieldTranslator;
    }

    public  void addMeta2Type(IMetaProvider metaProvider)
    {
//        int typeid=getDataTypes()[0];
//        metaProvider.addColumnByEventType(typeid, new ColumnHeadBean(TablesTypes.DATA_OBJ_ID+"2",TablesTypes.DATA_OBJ_ID+"2", ListGridFieldType.TEXT.toString()));
    }

    @Override
    protected IRowOperation _getRowOperation()
    {
        return new IRowOperation()
        {

            @Override
            public void setObjectAttr(IMetaProvider metaProvider, ColumnHeadBean attr, ResultSet rs, Map<String, Object> tuple) throws Exception
            {
//                {
//                    int typeid = rs.getInt(TablesTypes.DATATYPE_ID);
//                    if (typeid==92 && !tuple.containsKey(TablesTypes.DATA_OBJ_ID+"2"))
//                    {
//                        final String s = rs.getString(TablesTypes.DATA_OBJ_ID);
//
//                        int ix = s.lastIndexOf(";");
//                        tuple.put(TablesTypes.DATA_OBJ_ID+"2", s.substring(0,ix));
//                    }
//                }
            }
        };
    }

    @Override
    public int[] getDataTypes()
    {
        return new int[]{92};
    }
}
