package com.mwlib.tablo.db.desc;

import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.IMetaProvider;
import com.mwlib.tablo.db.IRowOperation;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mwlib.tablo.tables.SimpleField;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.types.ListGridFieldType;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 07.07.14
 * Time: 18:20
 * Пустой дескриптор для мест событий
 */
public class PlacesDesc extends BaseTableDesc
{
    public static final int PLACE_TYPE = -11;
    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
            {
                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false),
                    new SimpleField(TablesTypes.DATA_OBJ_ID,TablesTypes.DATA_OBJ_ID, ListGridFieldType.TEXT.toString(),false),
                    new SimpleField(TablesTypes.DATATYPE_ID,TablesTypes.DATATYPE_ID, ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.OBJ_OSN_ID, TablesTypes.OBJ_OSN_ID, ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.PUTGL_ID, TablesTypes.PUTGL_ID, ListGridFieldType.INTEGER.toString(),false)
            };

    public PlacesDesc(boolean test) {
        super(test);
    }

    @Override
    public String getTableType() {
        return TablesTypes.PLACES;
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
    }

    @Override
    public int[] getDataTypes()
    {
        return new int[]{PLACE_TYPE};
    }
}
