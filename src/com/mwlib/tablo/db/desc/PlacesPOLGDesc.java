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
 *
 */
public class PlacesPOLGDesc extends BaseTableDesc
{
    public static final int PLACEPOLG_TYPE = -15;
    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
            {
                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false),
                    new SimpleField(TablesTypes.KEY_FNAME+"2",TablesTypes.KEY_FNAME+"2", ListGridFieldType.TEXT.toString(),false),
                    new SimpleField(TablesTypes.DATA_OBJ_ID,TablesTypes.DATA_OBJ_ID, ListGridFieldType.TEXT.toString(),true),
                    new SimpleField(TablesTypes.DATATYPE_ID,TablesTypes.DATATYPE_ID, ListGridFieldType.INTEGER.toString(),true),
                    new SimpleField(TablesTypes.POLG_ID, TablesTypes.POLG_ID, ListGridFieldType.INTEGER.toString(),true),
                    new SimpleField(TablesTypes.POLG_NAME, TablesTypes.POLG_NAME, ListGridFieldType.TEXT.toString(),true)
            };

    public PlacesPOLGDesc(boolean test) {
        super(test);
    }

    @Override
    public String getTableType() {
        return TablesTypes.PLACEPOLG;
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
        return new int[]{PLACEPOLG_TYPE};
    }
}
