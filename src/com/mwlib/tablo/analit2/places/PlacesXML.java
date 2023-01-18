package com.mwlib.tablo.analit2.places;

import com.mycompany.common.TablesTypes;
import com.mycompany.common.analit2.IAnalisysDesc;
import com.mwlib.tablo.analit2.NNodeBuilder;
import com.mwlib.tablo.db.desc.DelayGIDTDesc;
import com.smartgwt.client.types.ListGridFieldType;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 06.07.15
 * Time: 14:37
 * Описатель таблицы состояния
 */
public class PlacesXML
{
    public static final String xml="<?xml version='1.0' encoding='UTF-8' ?>\n" +
            "<project>\n" +
            "<tuple>\n" +
            "\t<col tid='"+TablesTypes.PLACE_ID+"' title='ИД' hide='true' ftype='"+ ListGridFieldType.TEXT.getValue()+"'/>\n" +

            "\t<col tid='"+TablesTypes.DOR_CODE+"' title='ИД Дороги' hide='true' ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" +
            "\t<col tid='"+TablesTypes.DOR_NAME+"' title='Дорога'  hide='true'/>\n" +
            "\t<col tid='NUM' title='Номер в отображении' hide='true' ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" + //Тип ожидаемый в этом поле

            "\t<col tid='"+TablesTypes.POLG_ID+"' title='ИД Участка'  ftype='"+ ListGridFieldType.INTEGER.getValue()+"'/>\n" +
            "\t<col tid='"+TablesTypes.POLG_NAME+"' title='Участок'/>\n" +


            "\t<col tid='ICONS' title='Явления' ftype='"+ ListGridFieldType.IMAGE.getValue()+"'/>\n" + //Тип ожидаемый в этом поле

            "\t<col tid='CONS' title='' ftype='"+ ListGridFieldType.TEXT.getValue()+"' zval='' nval='' />\n" + //Тип ожидаемый в этом поле
            "\t<col tid='CNT' title='Кол. шт.' ftype='"+ ListGridFieldType.INTEGER.getValue()+"' zval=''/>\n" + //Тип ожидаемый в этом поле
            "\t<col tid='TLN' title='Длинна км.' ftype='"+ListGridFieldType.FLOAT.getValue()+"' format='###0.0' zval=''/>\n" + //Тип ожидаемый в этом поле и формат данных

            "</tuple>\n" +


            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.WARNINGS+"' title='Предупреждения'>\n" +

            "\t\t<NNode colid='A' val='GRP' title='Заложено&lt;br&gt;графиком' tblName='"+TablesTypes.WARNINGSINTIME+"'  noDrill='true'>\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.' colN='2'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.' />\n" +
            "\t\t</NNode >\n" +

            "\t\t<NNode colid='A' val='REAL' title='Действуют'>\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t</NNode>\n" +

            "\t\t<NNode colid='A' val='LONG' title='Длительные'" +
            " filter='" +
            "{\n" +
            "   \"operator\":\"equals\", \n" +
            "   \"fieldName\":\"FIXED_END_DATE\", \n" +
            "   \"value\":0 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT' title='шт.'/>\n" +
            "\t\t\t<NVAL colid='TLN' title='км.'/>\n" +
            "\t\t</NNode>\n" +

            "\t\t<NNode colid='A' val='NP' title='C нарушением&lt;br&gt;приказа' tblName='"+TablesTypes.WARNINGS_NP+"'  rotate='true'>\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode>\n" +

            "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.WINDOWS+"' title='Технологические окна'>\n" +

            "\t<NNode colid='A' val='TECH' title='Подход&lt;br&gt;техники' rotate='true' tblName='"+TablesTypes.TECH+"' noDrill='true'>\n" +
            "\t\t<NVAL colid='CNT'/>\n" +
            "\t</NNode >\n" +

            "\t<NNode title='П'>\n" +
            "\t\t<NNode colid='A' val='PPLAN' title='Запланированные' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":54 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='PCURR' title='Предоставленные' rotate='true' tblName='"+TablesTypes.WINDOWS_CURR+"'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":46 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t</NNode >\n" +

            "\t<NNode title='Ш'>\n" +
            "\t\t<NNode colid='A' val='HPLAN' title='Запланированные' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":56 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='HCURR' title='Предоставленные' rotate='true' tblName='"+TablesTypes.WINDOWS_CURR+"'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":57 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT' />\n" +
            "\t\t</NNode >\n" +
            "\t</NNode >\n" +

            "\t<NNode title='Э'>\n" +
            "\t\t<NNode colid='A' val='EPLAN' title='Запланированные' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":59 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='ECURR' title='Предоставленные' rotate='true' tblName='"+TablesTypes.WINDOWS_CURR+"'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":60 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t</NNode >\n" +

            "\t\t<NNode colid='A' val='ALL' title='Итого' rotate='true'>\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +

            "\t\t<NNode colid='A' val='OVERTIME' title='Передержанные' rotate='true' tblName='"+TablesTypes.WINDOWS_OVERTIME+"'>\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +


            "</NNode >\n" +


            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.REFUSES+"' title='Отказы&lt;br&gt;технических&lt;br&gt;средств'>\n" +
            "\t\t<NNode colid='A' val='P' title='П' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":48 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='H' title='Ш'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":49 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='E' title='Э'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":50 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='V' title='В'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":51 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='N' title='Непринятыe&lt;br&gt;к учету' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":73 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "</NNode >\n" +


            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.VIOLATIONS+"' title='Технологические&lt;br&gt;нарушения'>\n" +
            "\t\t<NNode colid='A' val='P' title='П' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":68 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='H' title='Ш'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":69 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='E' title='Э'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":70 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='V' title='В'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":71 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='N' title='Непринятыe&lt;br&gt;к учету' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":72 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "</NNode >\n" +



        "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.DELAYS_GID+"' title='Задержки&lt;br&gt;поездов'>\n" +

//            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.DELAYS_GID+"' title='ГИД'>\n" +

            "<NNode title='ГИД'>\n" +

                "\t\t<NNode colid='A' val='G"+ DelayGIDTDesc.PASS+"' title='Пассажирские' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":74 \n" +
            "}'" +
            ">\n" +
                "\t\t\t<NVAL colid='CNT'/>\n" +
                "\t\t</NNode>\n" +

                "\t\t<NNode colid='A' val='G"+ DelayGIDTDesc.REG+"' title='Пригородные' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":75 \n" +
            "}'" +
            ">\n" +
                 "\t\t\t<NVAL colid='CNT'/>\n" +
                "\t\t</NNode>\n" +
                "\t\t<NNode colid='A' val='G"+ DelayGIDTDesc.CRG+"' title='Грузовые' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":76 \n" +
            "}'" +
            ">\n" +
                "\t\t\t<NVAL colid='CNT'/>\n" +
                "\t\t</NNode>\n" +

            "</NNode>\n" +


//            "<NNode colid='EVENT_TYPE' val='"+ TablesTypes.DELAYS_ABVGD+"' title='ИХ АВГД'>\n" +

            "<NNode title='ИХ АВГД' tblName='"+TablesTypes.DELAYS_ABVGD+"' >\n" +

                "\t\t<NNode colid='A' val='I"+ DelayGIDTDesc.PASS+"' title='Пассажирские' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":62 \n" +
            "}'" +
            ">\n" +
                "\t\t\t<NVAL colid='CNT' />\n" +
                "\t\t</NNode >\n" +

                "\t\t<NNode colid='A' val='I"+ DelayGIDTDesc.REG+"' title='Пригородные' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":63 \n" +
            "}'" +
            ">\n" +
                 "\t\t\t<NVAL colid='CNT' />\n" +
                "\t\t</NNode>\n" +
                "\t\t<NNode colid='A' val='I"+ DelayGIDTDesc.CRG+"' title='Грузовые' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":64 \n" +
            "}'" +
            ">\n" +
                "\t\t\t<NVAL colid='CNT' />\n" +
                "\t\t</NNode>\n" +

            "</NNode>\n" +

        "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.KMOTABLE+"' title='Замечания КМО'>\n" +
            "\t\t<NNode colid='A' val='P' title='П' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":65 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='H' title='Ш' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":66 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='E' title='Э' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":67 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "</NNode >\n" +



            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.ZMTABLE+"' title='ЕКАСУИ'>\n" +
            "\t\t<NNode colid='A' val='Y' title='ЗМ&lt;br&gt;Незакрытые' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":84 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='N' title='ЗМ&lt;br&gt;просроченные' rotate='true'" +
            " filter='" +
            "{" +
            "                    \"operator\":\"equals\", \n" +
            "                    \"fieldName\":\""+TablesTypes.DATATYPE_ID+"\", \n" +
            "                    \"value\":85 \n" +
            "}'" +
            ">\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "</NNode >\n" +


            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.TABLO_WEATHER+"' title='Прогноз погоды' noDrill= 'true' >\n" +
            "\t\t<NNode colid='A' val='TEMP' title='Т день/&lt;br&gt;ночь' >\n" +
            "\t\t\t<NVAL colid='CONS'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='WIND' title='V ветра,&lt;br&gt;м/c' >\n" +
            "\t\t\t<NVAL colid='CONS'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='ICON1' title='Явления&lt;br&gt;погоды' >\n" +
            "\t\t\t<NVAL colid='ICONS'/>\n" +
            "\t\t</NNode >\n" +
            "</NNode >\n" +


            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.TEMPREL+"' title='Температура&lt;br&gt;рельса' noDrill= 'true' >\n" +
            "\t\t<NNode colid='A' val='MIN' title='мин'>\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "\t\t<NNode colid='A' val='MAX' title='макс'>\n" +
            "\t\t\t<NVAL colid='CNT'/>\n" +
            "\t\t</NNode >\n" +
            "</NNode >\n" +



            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.LOST_TRAIN+"' title='Брошенные поезда' rotate='true'>\n" +
            "\t<NVAL colid='CNT'/>\n" +
            "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.VAGTOR+"' title='Вагоны в ТОР' rotate='true'>\n" +
            "\t<NVAL colid='CNT'/>\n" +
            "</NNode >\n" +

            "<NNode colid='EVENT_TYPE' val='"+TablesTypes.VIP_GID+"' title='Пометки ГИД' rotate='true'>\n" +
            "\t<NVAL colid='CNT'/>\n" +
            "</NNode >\n" +



            "<grpX colN='1'>\n" +
            "\t<fld tid='"+TablesTypes.DOR_CODE+"' tColId='"+TablesTypes.DOR_NAME+"'>\n" +
                "\t\t<fld tid='"+TablesTypes.PLACE_ID+"' tColId='"+TablesTypes.POLG_NAME+"'/>\n" +
            "\t</fld>\n" +
            "</grpX>\n" +

            "</project>";

    /**
     * TODO Необходимо сделать единую точку входа для всех заголовков.
     * @return
     * @throws Exception
     */
    public static IAnalisysDesc testHeaders() throws Exception
    {
        return new NNodeBuilder().xml2Desc(xml);
    }
}
