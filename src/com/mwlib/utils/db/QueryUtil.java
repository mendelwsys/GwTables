/*
 * QueryUtil.java
 * 
 */
package com.mwlib.utils.db;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author  SCimbalov
 */
public final class QueryUtil
{
    
    //private static final SimpleDateFormat toDateTimeFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private static final SimpleDateFormat toDateFormatter     = new SimpleDateFormat("dd.MM.yyyy");
    
    private static final SimpleDateFormat mY_Formatter        = new SimpleDateFormat("MM.yyyy");
    
    //
    private static final String TO_DATE     = "TO_DATE('";
    //
    private static final String DD_MM_YYYY  = "', 'DD.MM.YYYY')";     
    private static final String MM_YYYY     = "', 'MM.YYYY')";
    private static final String YYYY        = "', 'YYYY')";
    //
    private static final String AND         = " and ";
    
    /** Creates a new instance of QueryUtil */
    private QueryUtil() {}
    
    public static String enumerateStrings(final String fieldName, Collection<String> list) {
        if(list==null) return "";
        String buffer = "";
        for(Iterator<String> iter = list.iterator(); iter.hasNext();) {
            String str = iter.next();
            if(str==null) continue;
            buffer += ",'" + str + "'";
        }
        if(buffer.length()<4) return "";
        return fieldName + " IN(" + buffer.substring(1) + ") ";
    }   
    //
    public static String inClause(final String fieldName, final Collection<String> items){       
        //
        if(items==null || items.isEmpty()) return " " + fieldName + " IN('0') ";
        StringBuilder buffer = new StringBuilder();
        for(Iterator<String> iter = items.iterator(); iter.hasNext();) {
            String value = iter.next();            
            buffer.append("'").append(value).append("'");
            if(iter.hasNext()) buffer.append(",");            
        }        
        return " " + fieldName + " IN(" + buffer.toString() + ") ";          
    }
    //
    public static String inClauseForNumerical(final String fieldName, final Collection<String> items){       
        //
        if(items==null || items.isEmpty()) return " " + fieldName + " IN(0) ";
        StringBuilder buffer = new StringBuilder();
        for(Iterator<String> iter = items.iterator(); iter.hasNext();) {
            String value = iter.next();            
            buffer.append(value);
            if(iter.hasNext()) buffer.append(",");            
        }        
        return " " + fieldName + " IN(" + buffer.toString() + ") ";          
    }
    
    //
    public static String toDateString(final java.util.Date date) {
        return TO_DATE + toDateFormatter.format(date) + DD_MM_YYYY;
    }
    //
    public static String toStartMonthDateString(final java.util.Date date) {
        return TO_DATE + "01." + mY_Formatter.format(date) + DD_MM_YYYY;        
    }    
    /*public static String toEndMonthDateString(java.util.Date date) {
        return TO_DATE + toDateFormatter.format(DateTimeUtil.getEndMonthDate(date)) + DD_MM_YYYY;        
    } */       
    //
    public static String toMonthYearString(final java.util.Date date) {
        return TO_DATE + mY_Formatter.format(date) + MM_YYYY;
    }
    //
    public static String toMonthYearString(final int month, final int year) {
        return TO_DATE + month + "." + year + MM_YYYY;
    }       
    //
    public static String toYearString(final int year) {
        return TO_DATE + year + YYYY;
    }       
}


    //TO_DATE('03.2002', 'MM.YYYY')
    /*public static void main(String[] strs){
        try{
            //Date dt = Calendar.getInstance().getTime();
            //ystem.out.println("month-year = " + toMonthYearString(dt));
            //System.out.println("month-year = " + toMonthYearString(11, 2003));
            
            //System.out.println("month-year = " + toStartMonthDateString(dt));
            
            //String regClause   = inClause("m_region_id",      UserRefItemsCache.getCache().getRegions(userId));
            //String distrClause = inClause("m_distributor_id", UserRefItemsCache.getCache().getDistributors(userId));
            //
            String userId = "ksuip301";
            
            String regClause      = distrInClause("m_distributor_id", userId);
            String distrClause    = regionInClause("m_region_id"    , userId);
            String distrRegClause = distrRegionInClause("m_distributor_id", "m_region_id", userId);
            
            System.out.println("regClause="   + regClause);
            System.out.println("distrClause=" + distrClause);            
            System.out.println("distrRegClause=" + distrRegClause);
            //
        }catch(Exception exc){
            exc.printStackTrace();
        }
    }*/
