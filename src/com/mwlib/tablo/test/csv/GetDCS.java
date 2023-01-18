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
public class GetDCS
{
    public static Map<String,String> dorKode2dorName= new HashMap();

    static
    {
        dorKode2dorName.put("Октябрьская","1");
        dorKode2dorName.put("Калининградская","10");
        dorKode2dorName.put("Московская","17");
        dorKode2dorName.put("Горьковская","24");
        dorKode2dorName.put("Северная","28");
        dorKode2dorName.put("Северо-Кавказская","51");
        dorKode2dorName.put("Юго-Восточная","58");
        dorKode2dorName.put("Приволжская","61");
        dorKode2dorName.put("Куйбышевская","63");
        dorKode2dorName.put("Свердловская","76");
        dorKode2dorName.put("Южно-Уральская","80");
        dorKode2dorName.put("Западно-Сибирская","83");
        dorKode2dorName.put("Красноярская","88");
        dorKode2dorName.put("Восточно-Сибирская","92");
        dorKode2dorName.put("Забайкальская","94");
        dorKode2dorName.put("Дальневосточная","96");

    }
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
        Pair<String[], Map[]> rv1;
        Pair<String[], Map[]> rv2;
        final String[] keyCols1 = {"Наименование дирекции","Наименование Центра организации работы станций","Характер работы станции","Наименование станции"};
        final String[] keyCols2 = {"id","VName","Name","kod","Dor_Kod","StanType","X","Y"};
        {
            String tableName="DCS";
            final String csv = "C:\\PapaWK\\RUSLAN_\\"+tableName+".CSV";


            final Class[] types = {String.class,String.class,String.class,String.class};
            rv1 = readFromCSV(csv, ';', types);
            System.out.println("rv = " + rv1.second.length);
        }



        {
            String tableName="nodes";
            final String csv = "C:\\PapaWK\\RUSLAN_\\"+tableName+".CSV";


            final Class[] types = {String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class,String.class};
            rv2 = readFromCSV(csv, '|', types);
            System.out.println("rv = " + rv2.second.length);
        }

        int tot=0;
        for (String dorName : dorKode2dorName.keySet())
        {

            String DOR_KOD=dorKode2dorName.get(dorName);
            List<Map> notFounds=new LinkedList<Map>();
            List<Map> found=new LinkedList<Map>();
            Map[] rvs11=rv1.second;
            for (Map rv11 : rvs11)
            {
                if (dorName.equalsIgnoreCase((String) rv11.get(keyCols1[0])))
                {
                    String stName= (String) rv11.get(keyCols1[3]);
                    stName=stName.replace("ё","е");
//                    stName=stName.replace("-1"," I");
                    br:
                    {
                        for (Map rv22 : rv2.second)
                        {
                            final String vName = (String) rv22.get("VName");
                            if (
                                    DOR_KOD.equals(rv22.get("Dor_Kod")) &&
                                    ( vName.equalsIgnoreCase(stName)
//                                    ||
//                                    vName.contains(stName.toUpperCase()))
//                                    ||
//                                    (stName.toUpperCase().contains(vName))
                                    ||
                                    (stName.toUpperCase().replace("-1", " I").equals(vName))
                                    ||
                                    (stName.toUpperCase().replace("-2", " II").equals(vName))
                                    ||
                                    (stName.toUpperCase().replace("-3", " III").equals(vName))
                                    ||
                                    (stName.toUpperCase().replace(" ", "-").equals(vName))
                                    ||
                                    (stName.toUpperCase().replace("-"," ").equals(vName))
                                    ))
                            {
                                rv11.put("kod",rv22.get("kod"));
                                rv11.put("id",rv22.get("id"));
                                found.add(rv11);
                                break br;
                            }
                        }
//                        System.out.println("stName = " + stName+" not found for dorName:"+dorName);
                        rv11.put("kod","X");
                        rv11.put("id", "X");
                        notFounds.add(rv11);
                        found.add(rv11);
                    }

                }
            }

            FileOutputStream osf = new FileOutputStream("C:\\PapaWK\\RUSLAN_\\OUT"+DOR_KOD+"_O.CSV");
            writeOUT(keyCols1, found, osf);
            osf.flush();
            osf.close();

           FileOutputStream osf2 = new FileOutputStream("C:\\PapaWK\\RUSLAN_\\NOFOUND_"+DOR_KOD+"_O.CSV");
           writeOUT(keyCols1, notFounds, osf2);
           osf2.flush();
           osf2.close();

            final int size = notFounds.size();
            System.out.println("notFounds = " + size +" for dorName:"+dorName+" dorKode:"+DOR_KOD);
            tot+=size;
        }
        System.out.println("tot = " + tot);



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
