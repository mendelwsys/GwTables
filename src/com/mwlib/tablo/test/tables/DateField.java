package com.mwlib.tablo.test.tables;

import com.mycompany.common.FieldException;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mwlib.tablo.tables.AField;
import com.smartgwt.client.types.ListGridFieldType;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 26.05.14
 * Time: 20:45
 * To change this template use File | Settings | File Templates.
 */
public class DateField extends AField
{

    public static final SimpleDateFormat toDateTimeFormatterSS = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public  static final SimpleDateFormat toDateTimeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    public  static final SimpleDateFormat toDateFormatter     = new SimpleDateFormat("dd.MM.yyyy");
    public  static final SimpleDateFormat mY_Formatter        = new SimpleDateFormat("MM.yyyy");
    private SimpleDateFormat format;


    public DateField(String name, String title)
    {
        this(name, title, ListGridFieldType.TEXT.toString(), true, toDateTimeFormatter);
    }

    public DateField(String name, String title, String type, boolean visible) {
        this(name, title, type, visible, toDateFormatter);
    }

    public DateField(String name, String title, String type, boolean visible, SimpleDateFormat format) {
        super(name, title, type, visible);
        this.format=format;
    }



    public Object getS(Map<String,ColumnHeadBean> column, Map tuple, Map<String, Object> outTuple) throws FieldException
    {
            Timestamp st=(Timestamp)tuple.get(name);
            if (st!=null)
                return format.format(new Date(st.getTime()));
            return "-"; //Начало действия //
    }
}
