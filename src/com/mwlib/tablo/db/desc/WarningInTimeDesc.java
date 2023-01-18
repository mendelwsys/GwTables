package com.mwlib.tablo.db.desc;

import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.IMetaProvider;
import com.mwlib.tablo.db.IRowOperation;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.smartgwt.client.types.ListGridFieldType;

import java.sql.ResultSet;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 * Предупреждения заложенные графиком
 */
public class WarningInTimeDesc extends BaseTableDesc
{
    private FieldTranslator[] fieldTranslator=new FieldTranslator[0];

    public WarningInTimeDesc(boolean test) {
        super(test);
    }

    @Override
    public String getTableType() {
        return TablesTypes.WARNINGSINTIME;
    }

    @Override
    public FieldTranslator[] getFieldTranslator() {
        return  fieldTranslator;
    }

    public  void addMeta2Type(IMetaProvider metaProvider)
    {
        int typeid=getDataTypes()[0];
        metaProvider.addColumnByEventType(typeid, new ColumnHeadBean(TablesTypes.DOR_CODE,TablesTypes.DOR_CODE, ListGridFieldType.INTEGER.toString()));
    }

    @Override
    protected IRowOperation _getRowOperation()
    {
        return new IRowOperation()
        {

            @Override
            public void setObjectAttr(IMetaProvider metaProvider, ColumnHeadBean attr, ResultSet rs, Map<String, Object> tuple) throws Exception
            {
                {
                    //int typeid = rs.getInt(TablesTypes.DATATYPE_ID);
                }
            }
        };
    }

    @Override
    public int[] getDataTypes()
    {
        return new int[]{96};
    }
}
