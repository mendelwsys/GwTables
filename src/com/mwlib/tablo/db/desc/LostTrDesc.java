package com.mwlib.tablo.db.desc;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 15.07.14
 * Time: 18:04
 * Брошенные поезда
 */

import com.mwlib.tablo.analit2.BusinessUtils;
import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.EventProvider;
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


public class LostTrDesc extends BaseTableDesc
{
    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
    {

                    new SimpleField("DOR_NAME","Дорога")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            Integer dor_kod=(Integer)tuple.get("DOR_KOD");
                            if (Directory.isInit())
                            {
                                Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                                if (byDorCode!=null)
                                    return byDorCode.getNAME();
                            }
                            return String.valueOf(dor_kod);
                        }
                    },

            new SimpleField("NOD_DIS", "Регион") {
                public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {
                    String reg = (String) super.getS(column, tuple, _outTuple);
                    return "РЕГ-" + reg;       //TODO Регион!!!!!
                }
            },
                    new SimpleField("MNEM_STAN","Станция, путь")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            String resstan=(String)super.getS(column,tuple,_outTuple);
                            Integer put=(Integer)tuple.get("NOM_PUT");
                            if (put!=null)
                                return resstan+", путь "+put;
                            else
                                return resstan;
                        }

                    },
                    new SimpleField("INDEX_POEZD","Индекс")
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException
                        {
                            String rv=(String)super.getS(column,tuple,_outTuple);
                            if (rv.length()>9)
                                return rv.substring(0,6)+"-"+rv.substring(6,9)+"-"+rv.substring(9);
                            else
                                return rv;
                        }
                    },
                    new SimpleField("KOL_VAG_GRUZ","Вагонов",ListGridFieldType.INTEGER.toString(),true),
                    new SimpleField("GRUJ","Груженых",ListGridFieldType.INTEGER.toString(),true),
                    new SimpleField("POR","Порожних",ListGridFieldType.INTEGER.toString(),true),
                    new SimpleField("NRP","НРП",ListGridFieldType.INTEGER.toString(),true),

                    new SimpleField(TablesTypes.DOR_CODE,"№ Дороги",ListGridFieldType.INTEGER.toString(),false),

                    new SimpleField(TablesTypes.VID_ID,"№ Службы",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.VID_NAME,"Служба(Гр)",ListGridFieldType.TEXT.toString(),false),

                    new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME, ListGridFieldType.TEXT.toString(),false),
                    new SimpleField(TablesTypes.EVTYPE, "Событие", ListGridFieldType.TEXT.toString(), false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            return TablesTypes.LOST_TRAIN;
                        }
                    },
                    new SimpleField(TablesTypes.EVTYPE_NAME, "Тип События", ListGridFieldType.TEXT.toString(), false)
                    {
                        public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple)
                        {
                            return "Брошенные поезда";
                        }
                    },
                    new SimpleField(TablesTypes.DATATYPE_ID,"Ид.типа события",ListGridFieldType.INTEGER.toString(),false),
                    new SimpleField(TablesTypes.ACTUAL,"Актуальность",ListGridFieldType.INTEGER.toString(),false),
                    new DateField2(EventProvider.DATE_MIN_ND,"Время получения записи",false),
                    new DateField2(EventProvider.COR_MAX_TIME,"Время изменения записи",false)

    };


    @Override
    public String getTableType() {
        return TablesTypes.LOST_TRAIN;
    }

    public LostTrDesc(boolean test) {
        super(test);
    }


    @Override
    public FieldTranslator[] getFieldTranslator() {
        return  fieldTranslator;
    }

    public  void addMeta2Type(IMetaProvider metaProvider)
    {
        int typeid=getDataTypes()[0];
        metaProvider.addColumnByEventType(typeid, new ColumnHeadBean(TablesTypes.DOR_CODE,TablesTypes.DOR_CODE,ListGridFieldType.INTEGER.toString()));
        BusinessUtils.fillMetaByVid(metaProvider, getDataTypes()[0]);
    }

    @Override
    protected IRowOperation _getRowOperation()
    {
        return new IRowOperation()
        {

            @Override
            public void setObjectAttr(IMetaProvider metaProvider, ColumnHeadBean attr, ResultSet rs, Map<String, Object> tuple) throws Exception
            {
                int typeid = rs.getInt(TablesTypes.DATATYPE_ID);
                for (int dataType : getDataTypes()) {
                    if (typeid==dataType)
                    {
                        if (!tuple.containsKey(TablesTypes.DOR_CODE))
                        {
                            int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
                            tuple.put(TablesTypes.DOR_CODE,dor_kod);
                            tuple.put(TablesTypes.VID_NAME, TablesTypes.Z_SERVAL);
                            tuple.put(TablesTypes.VID_ID, TablesTypes.Z_ID_SERVAL);
                        }
                        break;
                    }
                }
            }
        };
    }

    @Override
    public int[] getDataTypes() {
        return new int[]{78};
    }

}
