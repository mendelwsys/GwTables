package com.mwlib.tablo.db.desc;

import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.IMetaProvider;
import com.mwlib.tablo.db.IRowOperation;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mycompany.common.TablesTypes;


/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 * Подход техники
 */
public class TechDesc extends BaseTableDesc
{
    private FieldTranslator[] fieldTranslator=new FieldTranslator[0];

    public TechDesc(boolean test) {
        super(test);
    }

    @Override
    public String getTableType() {
        return TablesTypes.TECH;
    }

    @Override
    public FieldTranslator[] getFieldTranslator() {
        return  fieldTranslator;
    }

    @Override
    public void addMeta2Type(IMetaProvider metaProvider) {
    }

    @Override
    protected IRowOperation _getRowOperation()
    {
        return null;
//                new IRowOperation()
//        {
//
//            @Override
//            public void setObjectAttr(IMetaProvider metaProvider, ColumnHeadBean attr, ResultSet rs, Map<String, Object> tuple) throws Exception
//            {
//                {
//                    //int typeid = rs.getInt(TablesTypes.DATATYPE_ID);
//                }
//            }
//        };
    }

    @Override
    public int[] getDataTypes()
    {
        return new int[]{81};
    }
}
