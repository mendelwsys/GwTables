package com.mwlib.tablo.db.desc;

import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.IMetaProvider;
import com.mwlib.tablo.db.IRowOperation;
import com.mwlib.tablo.tables.DateField2;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mwlib.tablo.tables.SimpleField;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.FieldException;
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
 * Тестовый описатель данных
 */
public class UMsgDesc extends BaseTableDesc
{

    public static final String ID_USER = "ID_USER";
    public static final String MESSAGE_TYPE = "MESSAGE_TYPE";
    public static final String MESSAGE_TEXT = "MESSAGE_TEXT";

    public static final String DOR_CODE_FROM = TablesTypes.DOR_CODE+"_FROM";


    private FieldTranslator[] fieldTranslator=new FieldTranslator[]{

            new SimpleField("DOR_NAME","По Дороге",ListGridFieldType.TEXT.toString(),false)
            {
                public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                {
                    Integer dor_kod=(Integer)tuple.get(TablesTypes.DOR_CODE);
                    if (Directory.isInit())
                    {
                        Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                        if (byDorCode!=null)
                            return byDorCode.getNAME();
                    }
                    return String.valueOf(dor_kod);
                }
            },

            new SimpleField("DOR_NAME_FROM","Дорога",ListGridFieldType.TEXT.toString(),false)
            {
                public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                {
                    Integer dor_kod=(Integer)tuple.get(DOR_CODE_FROM);
                    if (Directory.isInit())
                    {
                        Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                        if (byDorCode!=null)
                            return byDorCode.getNAME();
                    }
                    return String.valueOf(dor_kod);
                }
            },

            new SimpleField(TablesTypes.DOR_CODE,"№ Дороги назначения",ListGridFieldType.INTEGER.toString(),false),
            new SimpleField(ID_USER,"Ид. пользователя", ListGridFieldType.INTEGER.toString(),false)

            ,new SimpleField(DOR_CODE_FROM,"№ Дороги", ListGridFieldType.INTEGER.toString(),false)

            ,new SimpleField(MESSAGE_TYPE,"Тип сообщения", ListGridFieldType.INTEGER.toString(),false)
            ,new DateField2(TablesTypes.CORTIME,"Время выдачи")
            ,new SimpleField(MESSAGE_TEXT,"Текст", ListGridFieldType.TEXT.toString(),true)



            ,new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false)
            ,new SimpleField(TablesTypes.DATATYPE_ID,"Ид.типа события",ListGridFieldType.INTEGER.toString(),false)
            ,new SimpleField(TablesTypes.ACTUAL,"Актуальность",ListGridFieldType.INTEGER.toString(),false)


    };

    public UMsgDesc(boolean test) {
        super(test);
    }

    @Override
    public String getTableType() {
        return TablesTypes.UMESSAGE;
    }

    public void setFieldTranslator(FieldTranslator[] fieldTranslator) {
        this.fieldTranslator = fieldTranslator;
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
        return new int[]{TablesTypes.UMSG_DATATYPE_ID};
    }
}
