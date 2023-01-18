package com.mwlib.tablo.test.csv;

import com.mwlib.utils.db.DbUtil;
import com.mwlib.utils.db.Directory;
import com.mycompany.common.Pair;
import com.mwlib.tablo.derby.DerbyDefaultCacheFactory;
import ru.ts.gisutils.common.CSV.CSVFileReader;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 14.05.15
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class ImportICG0
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

    static Connection connection;
    static {
            DbUtil.name2Connector.put(DbUtil.DS_JAVA_CACHE_NAME,new DbUtil.IJdbCOnnection()
            {
                @Override
                public Connection getConnection() throws ClassNotFoundException, SQLException
                {
                    connection = DriverManager.getConnection("jdbc:derby:C:/PapaWK/Projects/JavaProj/SGWTVisual2/db/udb4;create=true");
                    return connection;
                }
            });
    }


    public static void main(String[] args) throws Exception
    {
        Directory.initDictionary(false);
    }


    public static void _mainImport(String[] args) throws Exception
    {

        String fileTestName="/dbt/dictionary2.db";
        Directory.initDictionary(false);
        if (args!=null && args.length>0)
            fileTestName=args[0]+fileTestName;

        FileOutputStream fos = new FileOutputStream(fileTestName);
        ObjectOutputStream obs = new ObjectOutputStream(fos);
        obs.writeObject(Directory.stid2stan);
        obs.writeObject(Directory.code2stanid);
        obs.writeObject(Directory.dorCode2Rail);
        obs.writeObject(Directory.predId2Pred);
        obs.writeObject(Directory.vidId2Vid);
        obs.writeObject(Directory.grId2UkGR);
        obs.writeObject(Directory.markColor2colorName);

        {
            /*
            "POLG_TYPE";"POLG_ID";"NAME";"KOMM";"BOOL_STAN";"BOOL_LINE";"BOOL_PUTGL";"DATE_ND";"DATE_KD";"POLG_GR_ID";"NUM";"COR_TIP";"COR_TIME"
             */
            String tableName="POLG";
            final String csv = "C:\\PapaWK\\DOCS\\ICG0\\ICG0."+tableName+".CSV";

            final String[] keyCols = {"POLG_ID"};
            final Class[] types = {Integer.class, Integer.class, String.class, String.class, Integer.class, Integer.class, Integer.class, Date.class, Date.class, Integer.class, Integer.class, String.class, Timestamp.class};
            Pair<String[], Map[]> rv = readFromCSV(csv, ';', types);
            obs.writeObject(new Directory.DirectoryTableDesc(tableName,keyCols,types,rv));


//            add2Table(tableName, types, rv, keyCols);
        }

        {
            /*
            "POLG_TYPE";"POLG_ID";"OBJ_OSN_ID";"DATE_ND";"DATE_KD";"NUM";"COR_TIP";"COR_TIME"
             */
            String tableName="POLG_OBJ";
            final String csv = "C:\\PapaWK\\DOCS\\ICG0\\ICG0."+tableName+".CSV";
            final String[] keyCols = {"POLG_ID","OBJ_OSN_ID"};
            final Class[] types = {Integer.class, Integer.class, Integer.class,  Date.class, Date.class, Integer.class, String.class, Timestamp.class};
            Pair<String[], Map[]> rv = readFromCSV(csv, ';', types);
//            add2Table(tableName, types, rv, keyCols);
            obs.writeObject(new Directory.DirectoryTableDesc(tableName,keyCols,types,rv));

        }

        {
            /*
                "POLG_TYPE";"NUM";"NAME";"COMMENT";"MAX_ID";"DATE_ND";"DATE_KD";"COR_TIP";"COR_TIME"
             */
            String tableName="POLG_TYPES";
            final String csv = "C:\\PapaWK\\DOCS\\ICG0\\ICG0."+tableName+".CSV";
            final String[] keyCols = {"POLG_TYPE"};
            final Class[] types = {Integer.class, Integer.class, String.class,String.class,Integer.class,  Date.class, Date.class, String.class, Timestamp.class};
            Pair<String[], Map[]> rv = readFromCSV(csv, ';', types);
//            add2Table(tableName, types, rv, keyCols);

            obs.writeObject(new Directory.DirectoryTableDesc(tableName,keyCols,types,rv));

        }

        obs.close();

    }


    public static void _main(String[] args) throws Exception
    {
        {
            /*
            "POLG_TYPE";"POLG_ID";"NAME";"KOMM";"BOOL_STAN";"BOOL_LINE";"BOOL_PUTGL";"DATE_ND";"DATE_KD";"POLG_GR_ID";"NUM";"COR_TIP";"COR_TIME"
             */
            String tableName="POLG";
            final String csv = "C:\\PapaWK\\DOCS\\ICG0\\ICG0."+tableName+".CSV";
            final String[] keyCols = {"POLG_ID"};
            final Class[] types = {Integer.class, Integer.class, String.class, String.class, Integer.class, Integer.class, Integer.class, Date.class, Date.class, Integer.class, Integer.class, String.class, Timestamp.class};
            Pair<String[], Map[]> rv = readFromCSV(csv, ';', types);

            Directory.add2Table(new DerbyDefaultCacheFactory(),new Directory.DirectoryTableDesc(tableName,keyCols,types,rv));
        }

        {
            /*
            "POLG_TYPE";"POLG_ID";"OBJ_OSN_ID";"DATE_ND";"DATE_KD";"NUM";"COR_TIP";"COR_TIME"
             */
            String tableName="POLG_OBJ";
            final String csv = "C:\\PapaWK\\DOCS\\ICG0\\ICG0."+tableName+".CSV";
            final String[] keyCols = {"POLG_ID","OBJ_OSN_ID"};
            final Class[] types = {Integer.class, Integer.class, Integer.class,  Date.class, Date.class, Integer.class, String.class, Timestamp.class};
            Pair<String[], Map[]> rv = readFromCSV(csv, ';', types);
            Directory.add2Table( new DerbyDefaultCacheFactory(),new Directory.DirectoryTableDesc(tableName,keyCols,types,rv));
        }

        {
            /*
                "POLG_TYPE";"NUM";"NAME";"COMMENT";"MAX_ID";"DATE_ND";"DATE_KD";"COR_TIP";"COR_TIME"
             */
            String tableName="POLG_TYPES";
            final String csv = "C:\\PapaWK\\DOCS\\ICG0\\ICG0."+tableName+".CSV";
            final String[] keyCols = {"POLG_TYPE"};
            final Class[] types = {Integer.class, Integer.class, String.class,String.class,Integer.class,  Date.class, Date.class, String.class, Timestamp.class};
            Pair<String[], Map[]> rv = readFromCSV(csv, ';', types);
            Directory.add2Table(new DerbyDefaultCacheFactory(),new Directory.DirectoryTableDesc(tableName,keyCols,types,rv));
        }




        if (connection!=null && !connection.isClosed())
        {
            connection.commit();
            connection.close();
        }


    }


}
