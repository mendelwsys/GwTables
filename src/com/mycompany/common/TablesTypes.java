package com.mycompany.common;

import com.smartgwt.client.types.ValueEnum;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 21.05.14
 * Time: 14:23
 * Типы таблиц com.mycompany.common.TablesTypes
 */
public class TablesTypes
{

    public static final long DAY_MILS = 24 * 60 * 60 * 1000l;
    public static final long YEAR_MILS =365 * 24 * 60 * 60 * 1000l;
    public static final long V_YEAR_MILS =366 * 24 * 60 * 60 * 1000l;
    public static final int DEFPERIOD = 5 * 1000;


    public static final String VERSION = "TD287G";

    public static final String TESTVAL = "test";
    public static final String TESTMODEVAL = "testMode";
    public static final String DS_NAME = "dsName";
    public static final String DS_ORA_NAME = "dsOraName";
    public static final String DS_CACHE_NAME = "dsCacheName";
    public static final String PLACE_ID = "PLACE_ID";
    public static final String TEMPREL = "TEMPREL";
    public static final String TABLO_WEATHER="TABLO_WEATHER";


    public static enum TESTMODES implements ValueEnum
    {
        SILENCE("SILENCE"),
        UPDATEREMOVE("UPDATEREMOVE"),
        FULLUPADATE("FULLUPADATE"),
        CONSISTENCY("CONSISTENCY");

        public String getValue() {
            return this.value;
        }

        TESTMODES(String value) {
            this.value = value;
        }
        private String value;

    }


    public static final String STATUS_FACT="STATUS_FACT";

    public static final String ROW_STATUS="colorStatus";

    public static final String ID_KIND = "ID_KIND";


    public static final int tMSK = 180 * 60000;

    public static final String TIMESTAMP_FIELD = "TIMESTAMP_FIELD";
    public static final String KEY_SEPARATOR = "##";



    public static final String DIAGRAM_DESC="diagramDesc";//Описатель диаграммы с данными

    public static final String ROW_STYLE="rowstyle";



    public static final String ID_TM = "idT"; //Идентификатор параметра времени с которого брать все события (миллисекунды)
    public static final String ID_TN = "idN"; //Идентификатор параметра времени с которого брать все события (наносекунды)

    public static final String ID_REQN = "REQN";//Счетчик запросов (Для обновления)
    public static final int START_POS = 0;

    public static final String MAX_TIMESTAMP = "MAX_TIMESTAMP";

    public static final String LU_TIMESTAMP = "LU_TIMESTAMP";
    public static final String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";

    public static final String KEY_FNAME = "ID";//"id";
    public static final String DOR_CODE = "DOR_KOD";
    public static final String DOR_NAME = "DOR_NAME";
    public static final String CORTIME = "COR_TIME";
    public static final String DATATYPE_ID = "DATATYPE_ID";//"datatype_id";
    public static final String DATA_OBJ_ID = "DATA_OBJ_ID";

    public static final String COMMENT = "COMMENT";


    public static final String ACTUAL = "actual";
//    public static final String HMASK = "hmask";

    public static String TTYPE="TTYPE";

    public static String JT2ID="JT2ID";

    public static String REQPARAM="REQPARAM";



    public static String WARNINGSINTIME="WARNINGSINTIME";
    public static String LOCREQ="LOCREQ";
    public static final String LOC_DATA = "LOC_DATA";
    public static final int LOC_TR_MM = 15*60;//После минут с начала дня показываем на сл. день.

    public static String WARNINGS="WARNINGS";
    public static final String WARNINGS_NP = "WARNINGS_NP";

    public static String REFUSES="REFUSES";
    public static String WINDOWS="WINDOWS";
    public static String WINDOWS_CURR="WINDOWS_CURR";
    public static String WINDOWS_OVERTIME="WINDOWS_OVERTIME";

    public static final String UMESSAGE = "UMESSAGE";
    public static final int UMSG_DATATYPE_ID = -1000;


    public static String VIOLATIONS="VIOLATIONS";

    public static String TECH="TECH";

    public static String DELAYS_GID="DELAYS_GID";
    public static String DELAYS_ABVGD="DELAYS_ABVGD";


    public static String VIP_GID="VIP_GID";

    public static final String KMOTABLE = "KMOTABLE";
    public static final String ZMTABLE = "ZMTABLE";

    public static String MARKS_GID="MARKS_GID";

    public static String VAGTOR="VAGTOR";
    public static String LOST_TRAIN="LOST_TRAIN";

    public static String LENTA="LENTA";

    public static String PLACES="PLACES";

    public static String ORDIX="ORDIX";

    public static final String TBLID ="tblId";//Номер таблицы возвращается при первом запросе

    public static final String STATEDESC="STATEDESC";

    public static final String STATEDELAY="STATEDELAY";

    public static final String STATEPLACES="STATEPLACES";

    public static final String STATEREF12="STATEREF12";
    public static final String STATEWARNV="STATEWARNV";
    public static final String STATEWARNACT="STATEWARNACT";
    public static final String STATEWARNAGR = "STATEWARNAGR";

    public static final String WINREP="WINREP";
    public static final String WINPLAN="WINPLAN";

    public static final String OINDX="OINDX";

    public static final String RSM_DATA = "RSM_DATA";

    public static final String CRDURL = "CRDURL";//Ссылка на карточку, обрабатыватся как объект

    public static final String TESTTABLE = "TESTTABLE";//Тестовая таблица для проверки не отображается в продуктивном варианте


    public static final String Z_PREDVAL = "Прочие предприятия";
    public static final String Z_SERVAL = "Прочие службы";
    public static final int Z_ID_SERVAL = 0;
    public static final int Z_ID_SUM_SERVAL = -1;

    public static final String HIDE_ATTR="#-1_";

    public static final String FILTERDATAEXPR ="FILTERDATAEXPR";
//    public static final String SERVERFILTER ="SERVERFILTER";
    public static final String DOR_CODE_4_DELAY_TRAINS_SUM_TOTAL = "-1000";

    public static final String PROCEDURE ="PROCEDURE";
    public static final String VID_ID = "VID_ID";//идентификатор группировки, для того что бы можно было отфильтровать данные по группам
    public static final String VID_NAME="VID_NAME";//идентификатор группировки, для того что бы можно было отфильтровать данные по группам
    public static final String PRED_ID = "PRED_ID";
    public static final String PRED_NAME = "PRED_NAME";
    public static final String HOZ_ID = "HOZ_ID";

    public static final String EVTYPE="EVTYPE";

    public static final String EVTYPE_NAME = "EVTYPE_NAME";
    public static final String LINKTEXT = "linkText";
    public static final String OBJ_OSN_ID = "OBJ_OSN_ID";
    public static final String PUTGL_ID = "PUTGL_ID";
    public static final String TEXT = "TEXT";





    public static final String AGGREGATE_FUNCTIONS_TYPES_KEY = "originalFunction";
    public static final String AGGREGATE_CUSTOM_FUNCTIONS_KEY = "summaryFunction";
    public static final String AGGREGATE_FIELD_FORMAT_KEY = "customFormat";


    public static final String DB_TRANSACTION_N = "DB_TRANSACTION_N";
    public static String POLG_ID="POLG_ID";
    public static String POLG_NAME="PNAME";

    public static final String PLACEPOLG = "PLACEPOLG";

    public static final String BABKEN_TYPE = "BABKEN";

//    public static final String NO_TRANS_DELETED = "NO_TRANS_DELETED";
}
