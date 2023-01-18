package com.mwlib.tablo.tables;

import com.mycompany.common.FieldException;
import com.mycompany.common.tables.ColumnHeadBean;
import com.smartgwt.client.types.ListGridFieldType;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 26.05.14
 * Time: 20:45
 * Поле используется для передачи дат
 */
public class DateField2 extends AField
{

//    private SimpleDateFormat format;
    ConcurrentLinkedQueue<SimpleDateFormat> dateFormats= new ConcurrentLinkedQueue<SimpleDateFormat>();

    protected String dateTimePattern;// = "yyyy-MM-dd'T'HH:mm:ss";//TODO Без временной зоны, по умолчанию совпадает с UTC

    public DateField2(String name, String title,boolean visible)
    {
        this(name, title, ListGridFieldType.DATETIME.toString(), visible);
//        String pattern = "yyyy-MM-dd'T'HH:mm:ssXXX";
//        this.format = new SimpleDateFormat(dateTimePattern);
    }
    public DateField2(String name, String title,String dateFormat)
    {
        this(name,title,dateFormat,true);
    }


    public DateField2(String name, String title,String dateFormat,boolean visible)
    {
        super(name, title, dateFormat, visible);
        switch (ListGridFieldType.valueOf(dateFormat))
        {
            case DATE:
                dateTimePattern="yyyy-MM-dd";
                break;
            case TIME:
                dateTimePattern="HH:mm:ss";
                break;
            case DATETIME:
                dateTimePattern = "yyyy-MM-dd'T'HH:mm:ss";
                break;
            default:
                throw new RuntimeException("DateField2 can't work with type:"+dateFormat);
        }
        dateFormats.add(new SimpleDateFormat(dateTimePattern));
        dateFormats.add(new SimpleDateFormat(dateTimePattern));
        dateFormats.add(new SimpleDateFormat(dateTimePattern));
        dateFormats.add(new SimpleDateFormat(dateTimePattern));

//        this.format = new SimpleDateFormat(dateTimePattern);
    }



    public DateField2(String name, String title)
    {
        this(name, title, true);
    }


    public Object getS(Map<String,ColumnHeadBean> column, Map tuple, Map<String, Object> outTuple) throws FieldException
    {
        SimpleDateFormat format = null;
        try {
            Timestamp st=(Timestamp)tuple.get(name);
            if (st!=null)
            {
                int i=0;
                while (format==null)
                {
                    if (i>3000)
                        return new SimpleDateFormat(dateTimePattern).format(new Date(st.getTime()));
                    format = dateFormats.poll();
                    i++;
                }
//                if (i>1000)
//                    System.out.println("Date format was create queue" + i);
                return format.format(new Date(st.getTime()));
            }
            return null;
        }
        finally
        {
            if (format!=null)// && dateFormats.size()<10
                dateFormats.add(format);
        }
    }

//    public static void main(String[] args)
//    {
//        TimeZone tz = TimeZone.getDefault();
//        tz.getDSTSavings();
//
//        String pattern = "yyyy-MM-dd'T'HH:mm:ssXXX";
//        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
//        String psv=sdf.format(new Date());
//        System.out.println("psv = " + psv);
//    }
}
