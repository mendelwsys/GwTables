package com.mycompany.client.test.t5GridApp;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 21.10.14
 * Time: 19:06
 */
public class TXMLDesc
{
    public static String xml="<?xml version='1.0' encoding='utf-8' ?>\n" +
            "<project>\n" +
            "<tuple>\n" +
            "\n" +
            "\t<col tid='DOR_KOD' title='ИД Дороги' hide='true'/>\n" +
            "\t<col tid='VID_ID' title='ИД службы' hide='true'/>\n" +
            "\t<col tid='PRED_ID' title='ИД' hide='true'/>\n" +

            "\n" +
            "\t<col tid='DOR_NAME' title='Дорога'  hide='true'/>\n" +
            "\t<col tid='PRED_NAME' title='Служба/предприятие' />\n" +
            "\t<col tid='VID_NAME' title='Служба'  hide='true'/>\n" +
            "\n" +
            "\t<col tid='CNT' title='Кол. шт.'/>\n" +
            "\t<col tid='TLN' title='Длинна км.'/>\n" +
            "</tuple>\n" +
            "\n" +
            "<dims>\n" +
            "\n" +
            " <dim name='EVENT_TYPE'>\n" +
                "\t<dom tid='WIND' ord = '1' title='Окна'/>\n" +
                "\t<dom tid='WR' ord = '2' title='Предупреждения'/>\n" +
                "\t<dom tid='WRD' ord = '3' title='Длительные'/>\n" +
            " </dim>\n" +
            "\n" +
            "</dims>\n" +
            "\n" +
            "\n" +
            "<maps>\n" +
            "\t<map>\n" +
            "\t<keys>\n" +
            "\t  <key tid='WIND'/>\n" +
            "\t  <key tid='*'/>\n" +
            "\t</keys>\n" +
            "\t<fnames>\n" +
            " \t\t<fname tid='CNT' ord = '1'/>\n" +
            "\t</fnames>\n" +
            "\t</map>\n" +
            "\n" +
            "\t<map>\n" +
            "\t<keys>\n" +
            "\t  <key tid='WR'/>\n" +
            "\t  <key tid='*'/>\n" +
            "\t</keys>\n" +
            "\t<fnames>\n" +
            " \t\t<fname tid='CNT' ord='1'/>\n" +
            " \t\t<fname tid='TLN' ord = '2'/>\n" +
            "\t</fnames>\n" +
            "\t</map>\n" +
            "\n" +
            "\t<map>\n" +
            "\t<keys>\n" +
            "\t  <key tid='WRD'/>\n" +
            "\t  <key tid='*'/>\n" +
            "\t</keys>\n" +
            "\t<fnames>\n" +
            " \t\t<fname tid='CNT' ord='1'/>\n" +
            " \t\t<fname tid='TLN' ord = '2'/>\n" +
            "\t</fnames>\n" +
            "\t</map>\n" +

            "</maps>\n" +
            "<grpY>\n" +
            "   \t<fld tid='EVENT_TYPE'/>\n" +
            "</grpY>\n" +
            "\n" +
            "<grpX>\n" +
            "\t<fld tid='DOR_KOD' tColId='DOR_NAME'>\n" +
                "\t\t<fld tid='VID_ID' tColId='VID_NAME'>\n" +
                    "\t\t<fld tid='PRED_ID' tColId='PRED_NAME'/>\n" +
                "\t</fld>\n" +
            "\t</fld>\n" +
            "</grpX>\n" +
            "\n" +
            "</project>";
}
