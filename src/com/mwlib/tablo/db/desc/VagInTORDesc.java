package com.mwlib.tablo.db.desc;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 15.07.14
 * Time: 18:04
 * Вагоны в ТОР
 */

import com.mwlib.tablo.db.BaseTableDesc;
import com.mwlib.tablo.db.EventProvider;
import com.mwlib.tablo.db.IMetaProvider;
import com.mwlib.tablo.db.IRowOperation;
import com.mwlib.tablo.tables.AField;
import com.mwlib.tablo.tables.DateField2;
import com.mwlib.tablo.tables.FieldTranslator;
import com.mwlib.tablo.tables.SimpleField;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.FieldException;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.tables.ColumnHeadBean;
import com.smartgwt.client.types.ListGridFieldType;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

public class VagInTORDesc extends BaseTableDesc
{


    public boolean shouldResetData(Map<String, Object> parameters)
    {
        final String[] lu_timestamp = (String[]) parameters.get(TablesTypes.LU_TIMESTAMP);
        if (lu_timestamp==null)
            return false;

        long lastUpdateStamp=Long.parseLong(lu_timestamp[0]);
        long currentStamp=((long[]) parameters.get(TablesTypes.CURRENT_TIMESTAMP))[0];

        if (lastUpdateStamp == 0L) return false;

        Date last = new Date(lastUpdateStamp);
        Date current = new Date(currentStamp);
        return last.getHours() < 18 && current.getHours() >= 18;
    }


    private FieldTranslator[] fieldTranslator=new FieldTranslator[]
    {

            new SimpleField(TablesTypes.DOR_NAME, "Дорога", ListGridFieldType.TEXT.toString(), true) {
                public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {
                    Integer dor_kod = (Integer) tuple.get(TablesTypes.DOR_CODE);
                    if (Directory.isInit()) {
                        Directory.RailRoad byDorCode = Directory.getByDorCode(dor_kod);
                        if (byDorCode != null)
                            return byDorCode.getNAME();
                    }
                    return String.valueOf(dor_kod);
                }
            },
            new AField("STAN", "Станция") {

                public Object getS(Map<String, ColumnHeadBean> column, Map tuple, Map<String, Object> outTuple) throws FieldException {
                    Object stan1code = tuple.get("STAN_ID");


                    if (Directory.isInit()) {
                        Directory.StanDesc stan1 = null;
                        if (stan1code != null && (Integer) stan1code > 0)
                            stan1 = Directory.getByStanId((Integer) stan1code);


                        if (stan1 != null)
                            return stan1.getNAME();


                    }
                    return "";

                }
            },

            new SimpleField("NOM_VAG", "Номер вагона", ListGridFieldType.INTEGER.toString(), false) {
                {
//                            autofit=true;
                }

            },
            new SimpleField("IS_VSP", "ВСП", ListGridFieldType.INTEGER.toString(), false) {
                {
//                            autofit=true;
                }
            },
            new DateField2("DATE_OP", "Дата", false) {
                {
//                            autofit=true;
                }

            },

            new SimpleField("THIS_DATE", "Сутки", ListGridFieldType.INTEGER.toString(), true) {
                public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {

                    Date dateOp = (Date) tuple.get("DATE_OP");
                    Calendar c = getLastRZDDateEnd();


                    if (dateOp.after(c.getTime()))
                        return 1;


                    return 0;
                }

            },

            new SimpleField("LAST_DATE", "Прошлые сутки", ListGridFieldType.INTEGER.toString(), true) {
                {
//                            autofit=true;
                }

                public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {

                    Date dateOp = (Date) tuple.get("DATE_OP");
                    Calendar c = getLastRZDDateEnd();


                    if (dateOp.before(c.getTime()))
                        return 1;


                    return 0;
                }

            },
            new SimpleField("EIGHT_MORE", "Более 8 суток", ListGridFieldType.INTEGER.toString(), true) {
                {
//                            autofit=true;
                }

                public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {

                    final long eight = 8L * 24L * 60L * 60L * 1000L;

                    Date dateOp = (Date) tuple.get("DATE_OP");

                    Calendar c = GregorianCalendar.getInstance();

                    long currTime = c.getTime().getTime();

                    currTime=(currTime/(24L * 60L * 60L * 1000L))*(24L * 60L * 60L * 1000L);

                    if ((currTime - dateOp.getTime()) > eight)
                        return 1;


                    return 0;
                }

            },
            new SimpleField("EIGHT_LESS", "Менее 8 суток", ListGridFieldType.INTEGER.toString(), true) {
                {
//                            autofit=true;
                }

                public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {

                    final long eight = 8L * 24L * 60L * 60L * 1000L;

                    Date dateOp = (Date) tuple.get("DATE_OP");
                    Calendar c = GregorianCalendar.getInstance();

                    long currTime = c.getTime().getTime();
                    currTime=(currTime/(24L * 60L * 60L * 1000L))*(24L * 60L * 60L * 1000L);

                    if ((currTime - dateOp.getTime()) < eight)
                        return 1;


                    return 0;
                }

            },

            new SimpleField("THIS_DATE_VSP", "Сутки ВСП", ListGridFieldType.INTEGER.toString(), true) {
                {
//                            autofit=true;
                }

                public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {

                    Date dateOp = (Date) tuple.get("DATE_OP");
                    Calendar c = getLastRZDDateEnd();


                    if (dateOp.after(c.getTime()) && Integer.parseInt((String) tuple.get("IS_VSP")) > 0)
                        return 1;


                    return 0;
                }


            },

            new SimpleField("LAST_DATE_VSP", "Прошлые сутки ВСП", ListGridFieldType.INTEGER.toString(), true) {
                {
//                            autofit=true;
                }

                public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) throws FieldException {

                    Date dateOp = (Date) tuple.get("DATE_OP");
                    Calendar c = getLastRZDDateEnd();


                    if (dateOp.before(c.getTime()) && Integer.parseInt((String) tuple.get("IS_VSP")) > 0)
                        return 1;


                    return 0;
                }
            },
            new SimpleField(TablesTypes.EVTYPE, "Событие", ListGridFieldType.TEXT.toString(), false) {
                public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) {
                    return TablesTypes.VAGTOR;
                }
            },
            new SimpleField(TablesTypes.EVTYPE_NAME, "Тип События", ListGridFieldType.TEXT.toString(), false) {
                public Object getS(ColumnHeadBean[] column, Map tuple, Map<String, Object> _outTuple) {
                    return "Вагоны в ТОР";
                }
            },
            new SimpleField(TablesTypes.DOR_CODE,"№ Дороги",ListGridFieldType.TEXT.toString(),false),
            new SimpleField(TablesTypes.KEY_FNAME,TablesTypes.KEY_FNAME,ListGridFieldType.TEXT.toString(),false),
            new SimpleField("STAN_ID","STAN_ID",ListGridFieldType.INTEGER.toString(),false),

            new SimpleField(TablesTypes.DATATYPE_ID,"Ид.типа события",ListGridFieldType.INTEGER.toString(),false),
            new SimpleField(TablesTypes.ACTUAL,"Актуальность",ListGridFieldType.INTEGER.toString(),false),
            new DateField2(EventProvider.DATE_MIN_ND,"Время получения записи",false),
            new DateField2(EventProvider.COR_MAX_TIME,"Время изменения записи",false)

    };


    @Override
    public String getTableType() {
        return TablesTypes.VAGTOR;
    }


    public VagInTORDesc(boolean test) {
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
        metaProvider.addColumnByEventType(typeid, new ColumnHeadBean("ADM_KOD","ADM_KOD",ListGridFieldType.INTEGER.toString()));
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
                if (typeid==77
                        && attr.getName().equalsIgnoreCase("STAN_ID")
                        )
                {

                    int dor_kod = rs.getInt(TablesTypes.DOR_CODE);
                    tuple.put(TablesTypes.DOR_CODE, dor_kod);
                    tuple.put("ADM_KOD", Directory.getByDorCode(dor_kod).getADM_KOD());




                }
            }
        };
    }

    @Override
    public int[] getDataTypes() {
        return new int[]{77};
    }


    private Calendar getLastRZDDateEnd() {
        //Date d = new Date();
        Calendar c2 = GregorianCalendar.getInstance();

        Calendar c = GregorianCalendar.getInstance();

        if (!(c2.get(Calendar.HOUR_OF_DAY) >= 18 && (c2.get(Calendar.HOUR_OF_DAY) <= 23 && c2.get(Calendar.MINUTE) <= 59 && c2.get(Calendar.SECOND) <= 59 && c2.get(Calendar.MILLISECOND) <= 9999)))

            c.add(Calendar.DATE, -1);
        c.set(Calendar.HOUR_OF_DAY, 18);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c;
    }

}
