package com.mycompany.common;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 20.11.14
 * Time: 11:29
 * Определение процедуры
 */
public class ProcDef
{
    //языки которые понимает интерпретатор
    public static String SQL_L="SQL_L";
    public static String JS_L="JS_L";//JavaScript
    public static String J_L="JN_L";//Java язык

    String lang;//Язык для исполннения
    String[] cmds;//команды на языке

}
