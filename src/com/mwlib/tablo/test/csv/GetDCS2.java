package com.mwlib.tablo.test.csv;

import com.mycompany.common.Pair;
import ru.ts.gisutils.common.CSV.CSVFileReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 17.09.15
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
public class GetDCS2
{

    private static final SimpleDateFormat toDateFormatter     = new SimpleDateFormat("dd.MM.yyyy");
    private static final SimpleDateFormat toDateTimeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final SimpleDateFormat toTimeFormatter     = new SimpleDateFormat("HH:mm:ss");
    public static Pair<String[],Map[]> readFromCSV(String CSV, char delimiter,Class[] types)
            throws FileNotFoundException, IOException, NumberFormatException, ParseException {
        CSVFileReader reader = new CSVFileReader( CSV );
        try
        {

            List<Map> maps=new LinkedList<Map>();
            String[] headers=null;


            reader.setDelimiter( delimiter );
            int ln = 0; // line number


            while ( reader.readRecord() )
                try
                {

                    if (ln==0)
                    {
                        headers=reader.getValues();
                    }
                    else
                    {
                        Map tuple = new HashMap();
                        //String[] vals = reader.getValues();
                        for (int i = 0; i < headers.length; i++)
                        {
                                final String val = reader.get(i);
                                if (!types[i].equals(String.class) && (val==null || val.length()==0))
                                    tuple.put(headers[i],null);
                                else if (types[i].equals(Integer.class))
                                     tuple.put(headers[i],reader.getInteger(i));
                                else if (types[i].equals(Double.class))
                                    tuple.put(headers[i],reader.getDouble(i));
                                else if (types[i].equals(Float.class))
                                    tuple.put(headers[i],reader.getDouble(i).floatValue());
                                else
                                {

                                    if ((types[i].equals(Date.class)))
                                    {
                                        java.util.Date res = toDateFormatter.parse(val);
                                        tuple.put(headers[i],new Date(res.getTime()));
                                    }
                                    else if ((types[i].equals(Time.class)))
                                    {
                                        java.util.Date res = toTimeFormatter.parse(val);
                                        tuple.put(headers[i],new Time(res.getTime()));
                                    }
                                    else if ((types[i].equals(Timestamp.class)))
                                    {
                                        java.util.Date res = toDateTimeFormatter.parse(val);
                                        tuple.put(headers[i],new Timestamp(res.getTime()));
                                    }
                                    else
                                        tuple.put(headers[i], val);
                                }


                        }
                        maps.add(tuple);
                    }
                    ln++;
                }
                catch ( Exception nfe )
                {
                    System.err.println( " CSV file \"" + CSV + "\", line " + ln);
                    throw nfe;
                }


            return new Pair(headers,maps.toArray(new Map[maps.size()]));
        }
        finally
        {
            reader.close();
        }
    }


    public static void main(String[] args) throws IOException, ParseException
    {

        for (String DOR_KOD : GetDCS.dorKode2dorName.values())
        {
            if ("1".equals(DOR_KOD))
                marge(DOR_KOD);

        }
   }

    private static void marge(String DOR_KOD) throws IOException, ParseException {
        Pair<String[], Map[]> rv1;
        Pair<String[], Map[]> rv2;
        final String[] keyCols0 = {"Наименование дирекции","Наименование Центра организации работы станций","Характер работы станции","Наименование станции"};
        final String[] keyCols1 = {"Наименование дирекции","Наименование Центра организации работы станций","Характер работы станции","Наименование станции","id","kod"};
        {
            String tableName="NOFOUND_"+DOR_KOD+"_I";
            final String csv = "C:\\PapaWK\\RUSLAN_\\"+tableName+".CSV";


            final Class[] types = {String.class,String.class,String.class,String.class,String.class,String.class};
            rv1 = readFromCSV(csv, ';', types);
            System.out.println("rv = " + rv1.second.length);
        }


        {
            String tableName="OUT"+DOR_KOD+"_O";
            final String csv = "C:\\PapaWK\\RUSLAN_\\"+tableName+".CSV";


            final Class[] types = {String.class,String.class,String.class,String.class,String.class,String.class};
            rv2 = readFromCSV(csv, ';', types);
            System.out.println("rv = " + rv2.second.length);
        }

        {
            List<Map> found=new LinkedList<Map>();
            Map[] rvs22=rv2.second;
            for (Map rv22 : rvs22)
            {
                    String id= (String) rv22.get("id");
                    if ("X".equalsIgnoreCase(id))
                    {
                        br2:
                        {
                            for (Map rv11 : rv1.second)
                            {
                                br1:
                                {

                                    for (String colName : keyCols0)
                                    {
                                        if (!rv11.get(colName).equals(rv22.get(colName)))
                                          break br1;
                                    }
                                    final String id1 = (String) rv11.get("id");

                                    if (!"X".equalsIgnoreCase(id1))
                                    {
                                        rv22.put("id", id1);
                                        rv22.put("kod",rv11.get("kod"));
                                        found.add(rv22);
                                    }
                                    else
                                    {
                                        System.out.println(" not defined " + rv11);
                                    }
                                    break br2;
                                }
                            }
                            System.out.println(" not found " + rv22.get("Наименование станции"));
                        }
                    }
                    else
                        found.add(rv22);
            }

            FileOutputStream osf = new FileOutputStream("C:\\PapaWK\\RUSLAN_\\OUT"+DOR_KOD+"_F.CSV");
            writeOUT(keyCols0, found, osf);
            osf.flush();
            osf.close();
        }
    }

    final static String charsetName = "WINDOWS-1251";

    private static void writeOUT(String[] keyCols1, List<Map> found, FileOutputStream osf) throws IOException {
        for (int i = 0, keyCols1Length = keyCols1.length; i < keyCols1Length; i++)
        {
            String title = keyCols1[i];
            osf.write(title.getBytes(charsetName));
            osf.write(";".getBytes(charsetName));
        }

        osf.write("id".getBytes(charsetName));
        osf.write(";".getBytes(charsetName));

        osf.write("kod\n".getBytes(charsetName));

        for (Map map : found)
        {
            for (int i = 0, keyCols1Length = keyCols1.length; i < keyCols1Length; i++)
            {
                String title = keyCols1[i];
                String val= (String) map.get(title);
                osf.write(val.getBytes(charsetName));
                osf.write(";".getBytes(charsetName));
            }

            {
                String val= (String) map.get("id");
                osf.write(val.getBytes(charsetName));
                osf.write(";".getBytes(charsetName));
            }
            {
                String val= (String) map.get("kod");
                osf.write((val + "\n").getBytes(charsetName));
            }
        }
    }
}
