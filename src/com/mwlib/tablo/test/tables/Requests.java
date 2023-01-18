package com.mwlib.tablo.test.tables;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 23.07.14
 * Time: 18:33
 * To change this template use File | Settings | File Templates.
 */
public class Requests
{
//Предупреждения toj.datatype_id=44
        final static String GET_W_EVENT ="select toj1.data_obj_id,toj1.cor_time,da.attr_id,al.attr_type," +
        "da.value_s,da.value_i,da.value_f,da.value_t,da.cor_time as acor_time " +
        " ,toj1.text" +
//        ", " +
//        "toj1.comment "+
        " from "+
        "("+
        "  select toj.data_obj_id,toj.cor_time" +
        "," +
        " toj.text " +
        "," +
        " toj.datatype_id" +
//        ", " +
//        " toj.comment" +
        " from tablo_data_objects toj,tablo_data_attributes da"+
        "    where toj.datatype_id =? and toj.dor_kod=? "+
        "      and toj.cor_tip IN ('I','U')\n"+
        "      and toj.data_obj_id=da.data_obj_id " +
        "      and da.attr_id='TIM_OTM'"+
        "      and (da.value_t like '31.12.99%' or da.value_t>= ? )"+
        "      and toj.cor_time> ? "+
        ") toj1, tablo_data_attributes da ,tablo_data_attr_list al "+
        " where toj1.data_obj_id=da.data_obj_id " +
        " and toj1.datatype_id=da.datatype_id " +
        " and al.attr_id=da.attr_id " +
        " and al.DATATYPE_ID=da.datatype_id "+
        " order by  toj1.cor_time";

//Окна
    public static final String GET_W_WINDOWS="select toj1.data_obj_id,\n" +
            "toj2.cor_time,\n" +
            "da.attr_id,al.attr_type,da.value_s,da.value_i,da.value_f,da.value_t,da.cor_time \n" +
            "as acor_time\n" +
            ",toj2.text," +
            " toj2.datatype_id  \n" +
            "from (  \n" +
            "select distinct(toj.data_obj_id) as data_obj_id from tablo_data_objects toj,tablo_data_attributes da " +
            "where toj.DATATYPE_ID IN (46,47,54,57,58,56,60,61,59,94)\n" +
            "and toj.cor_tip IN ('I','U') and toj.dor_kod= ? \n" +
            "and toj.data_obj_id=da.data_obj_id  and da.attr_id='KD' \n" +
            "and (da.value_t>= ? ) \n" + //TO_TIMESTAMP ('08-07-14 15:50:00', 'DD-MM-RR HH24:MI:SS')
            "and toj.cor_time> ? \n"+
            ") " +
            "toj1, \n" +
            "tablo_data_objects toj2,\n" +
            "tablo_data_attributes da,\n" +
            "tablo_data_attr_list al  " +
            "where  \n" +
            "toj1.data_obj_id=da.data_obj_id \n" +
            "and toj2.data_obj_id=da.data_obj_id \n" +
            "and toj2.datatype_id=da.datatype_id\n" +
            "and al.attr_id=da.attr_id \n" +
            "and al.DATATYPE_ID=da.datatype_id \n"+
            "order by \n" +
            "da.cor_time,\n" +
            "da.data_obj_id,da.datatype_id";

//Нарушения
    public static final String GET_W_VIOLATION ="select \n" +
        "toj1.data_obj_id,\n" +
        "toj2.cor_time," +
        "da.attr_id,al.attr_type,da.value_s,da.value_i,da.value_f,da.value_t,da.cor_time as acor_time,\n" +
        "toj2.text, da.datatype_id \n"+
        "from (  \n" +
        "select distinct(toj.data_obj_id) as data_obj_id from tablo_data_objects toj,tablo_data_attributes da where toj.DATATYPE_ID IN (68,69,70,71,72)\n" +
        "and toj.cor_tip IN ('I','U') and toj.dor_kod=? \n" +
        "and toj.data_obj_id=da.data_obj_id  and " +
        "(" +
         "da.attr_id='ND' and ( da.value_t>=? ) \n "+//"TO_TIMESTAMP ('14-07-14 15:50:00', 'DD-MM-RR HH24:MI:SS')) \n" +
        " or\n" +
        " da.attr_id='KD' and (da.value_t<TO_TIMESTAMP ('01-01-00 00:00:00', 'DD-MM-RR HH24:MI:SS'))" +
        ") "+
        "and toj.cor_time> ? \n"+
        ") toj1, \n" +
        "tablo_data_objects toj2,\n" +
        "tablo_data_attributes da,\n" +
        "tablo_data_attr_list al  where  \n" +
        "toj1.data_obj_id=da.data_obj_id \n" +
        "and toj2.data_obj_id=da.data_obj_id \n" +
        "and toj2.datatype_id=da.datatype_id\n" +
        "and al.attr_id=da.attr_id \n" +
        "and al.DATATYPE_ID=da.datatype_id\n"+
        " order by \n" +
        "toj2.cor_time,\n" +
        "da.data_obj_id,da.datatype_id ";

//Задержки по ГИД
    public static final String GET_W_GID_DELAY ="select \n" +
        "toj1.data_obj_id,\n" +
        "toj2.cor_time," +
        "da.attr_id,al.attr_type,da.value_s,da.value_i,da.value_f,da.value_t,da.cor_time as acor_time,\n" +
        "toj2.text, da.datatype_id \n"+
        "from (  \n" +
        "select distinct(toj.data_obj_id) as " +
        "data_obj_id from tablo_data_objects toj,tablo_data_attributes da " +
        "where toj.DATATYPE_ID IN (74,75,76)\n" +
        "and toj.cor_tip IN ('I','U') and toj.dor_kod=? \n" +
        "and toj.data_obj_id=da.data_obj_id  and " +
        "(" +
         "da.attr_id='ND' and ( da.value_t>=? ) \n "+//"TO_TIMESTAMP ('14-07-14 15:50:00', 'DD-MM-RR HH24:MI:SS')) \n" +
        " or\n" +
        " da.attr_id='KD' and (da.value_t<TO_TIMESTAMP ('01-01-00 00:00:00', 'DD-MM-RR HH24:MI:SS'))" +
        ") "+
        "and toj.cor_time> ? \n"+
        ") toj1, \n" +
        "tablo_data_objects toj2,\n" +
        "tablo_data_attributes da,\n" +
        "tablo_data_attr_list al  where  \n" +
        "toj1.data_obj_id=da.data_obj_id \n" +
        "and toj2.data_obj_id=da.data_obj_id \n" +
        "and toj2.datatype_id=da.datatype_id\n" +
        "and al.attr_id=da.attr_id \n" +
        "and al.DATATYPE_ID=da.datatype_id \n"+
         "order by \n" +
        "toj2.cor_time,\n" +
        "da.data_obj_id,da.datatype_id ";

//Отказы
    public static final String GET_W_REFUSE="select \n" +
        "toj1.data_obj_id,\n" +
        "toj2.cor_time," +
        "da.attr_id,al.attr_type,da.value_s,da.value_i,da.value_f,da.value_t,da.cor_time as acor_time,\n" +
        "toj2.text, da.datatype_id \n"+
        "from (  \n" +
        "select distinct(toj.data_obj_id) as data_obj_id from tablo_data_objects toj,tablo_data_attributes da where toj.DATATYPE_ID IN (48,49,50,51,73)\n" +
        "and toj.cor_tip IN ('I','U') and toj.dor_kod=? \n" +
        "and toj.data_obj_id=da.data_obj_id  and " +
        "(" +
         "da.attr_id='ND' and ( da.value_t>=? ) \n "+//"TO_TIMESTAMP ('14-07-14 15:50:00', 'DD-MM-RR HH24:MI:SS')) \n" +
        " or\n" +
        " da.attr_id='KD' and (da.value_t<TO_TIMESTAMP ('01-01-00 00:00:00', 'DD-MM-RR HH24:MI:SS'))" +
        ") "+
        "and toj.cor_time> ? \n"+
        ") toj1, \n" +
        "tablo_data_objects toj2,\n" +
        "tablo_data_attributes da,\n" +
        "tablo_data_attr_list al  where  \n" +
        "toj1.data_obj_id=da.data_obj_id \n" +
        "and toj2.data_obj_id=da.data_obj_id \n" +
        "and toj2.datatype_id=da.datatype_id\n" +
        "and al.attr_id=da.attr_id \n" +
        "and al.DATATYPE_ID=da.datatype_id \n"+
        " order by \n" +
        "toj2.cor_time,\n" +
        "da.data_obj_id,da.datatype_id ";

//Важные отметки ГИД
    public static final String GET_MARKST="select \n" +
        "toj1.data_obj_id,\n" +
        "toj2.cor_time," +
        "da.attr_id," +
//        "al.attr_type," +
//        "da.value_s," +
        "CASE da.attr_id\n" +
        "  WHEN 'MRMCOLOR'\n" +
        "THEN 'STRING'\n" +
        "ELSE\n" +
        "  al.attr_type\n" +
        "END\n" +
        "AS attr_type\n" +
        ",\n" +
        "CASE da.attr_id\n" +
        "  WHEN 'MRMCOLOR'\n" +
        "  THEN ( select NAME from MARK_COLOR where color_id=da.value_f)\n" +
        "  ELSE\n" +
        "    da.value_s\n" +
        "END\n" +
        "AS value_s,"+
        "da.value_i,da.value_f,da.value_t,da.cor_time as acor_time,\n" +
        "toj2.text, da.datatype_id " +
//            ", toj2.DOR_KOD as DOR_KODI \n"+
        "from (  \n" +
        "select distinct(toj.data_obj_id) as data_obj_id from tablo_data_objects toj,tablo_data_attributes da where toj.DATATYPE_ID=90\n" +
        "and toj.cor_tip IN ('I','U') and toj.dor_kod=? \n" +
        "and toj.data_obj_id=da.data_obj_id " +
        "and toj.cor_time> ? \n"+
        ") toj1, \n" +
        "tablo_data_objects toj2,\n" +
        "tablo_data_attributes da,\n" +
        "tablo_data_attr_list al  where  \n" +
        "toj1.data_obj_id=da.data_obj_id \n" +
        "and toj2.data_obj_id=da.data_obj_id \n" +
        "and toj2.datatype_id=da.datatype_id\n" +
        " and al.attr_id=da.attr_id \n" +
        "and al.DATATYPE_ID=da.datatype_id \n"+
        "order by \n" +
        "toj2.cor_time,\n" +
        "da.data_obj_id,da.datatype_id ";
}
