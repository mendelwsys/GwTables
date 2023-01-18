package com.mwlib.tablo.test;

import com.mycompany.client.test.evalf.JSFormula;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 16.04.15
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
public class regExpr
{

        public String getViewFormula(JSFormula
            formula,Map<String,String> newKey2Fname)
        {
            String expression=formula.getExpressionValue();

            Map<String,String> newKey2Holder=new HashMap<String,String>();

            Map<String,String> fname2key=new HashMap<String,String>();
            for (String key : newKey2Fname.keySet())
                fname2key.put(newKey2Fname.get(key),key);

            int phIx=0;
            Map<String, String> oldKey2Fname = formula.getVarName2NameInRecord();
            for (String oldKey : oldKey2Fname.keySet())
            {
                final String oldFName = oldKey2Fname.get(oldKey);
                final String newFName = newKey2Fname.get(oldKey);
                if (!oldFName.equals(newFName))
                {

                    String newKey=fname2key.get(newFName);
                    final String holder = "#_" + phIx + "_#";
                    expression=expression.replace("\\b" + oldKey + "\\b", holder);
                    newKey2Holder.put(holder,newKey);
                    phIx++;
                }
            }

            for (String newKey : newKey2Holder.keySet())
                expression=expression.replace(newKey2Holder.get(newKey),newKey);

            formula.setVarName2NameInRecord(newKey2Fname);
            formula.setExpressionValue(expression);
            return formula.getExpressionValue();
        }


    public static void main(String[] args) {
        String expr="A1?*A.D+f(\"?B1@\",C+B1)*A";


        final Map<String, String> map = new HashMap<String, String>();

        map.put("A1","FA1");
        map.put("A","FA");
        map.put("D","FD");
        map.put("D","FD");
        JSFormula jsf = new JSFormula("XXX", expr, map);


//        final String regex = "[\\s\\*\\/\\+\\-\\(\\)\\.\\!\\^\\&\\|\\;\\,\\}\\{]A\\b";

            final String regex = "[\"].+[\"]";

          //final String regex = "\\bB1\\b";

        String res=expr.replaceAll(regex,"#_1_#");
        System.out.println("res = " + res);

//        final Matcher matcher = Pattern.compile(regex).matcher(expr);
//        int end=0;
//        while (matcher.find(end))
//        {
//            int start=matcher.start();
//            end=matcher.end();
////            matcher.regionEnd();
//            System.out.println("res = " + matcher.group());
//        }

//

    }
}
