package com.mwlib.tablo;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mycompany.common.ValDef;

import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 30.09.14
 * Time: 17:51
 * To change this template use File | Settings | File Templates.
 */
public class EventUtils
{
    public static String toJson(Object obj)
    {
        return new Gson().toJson(obj);
    }

    public static String toJson2(Object obj)
    {
        return new GsonBuilder().serializeNulls().create().toJson(obj);
    }


    public static String getParameter(Map parameters, String cliid) {
        String[] arCliId=(String[])parameters.get(cliid);
        String cliId=null;
        if (arCliId!=null && arCliId.length==1)
            cliId=arCliId[0];
        return cliId;
    }


    public static Map<String,ValDef> createFilterByJson(String jString, Map<String, ValDef> rv)
    {
        if (rv==null)
            rv=new HashMap<String,ValDef>();

        Type mapType = new TypeToken<Map<String,ValDef>>() {}.getType();
        Gson gson = new GsonBuilder().serializeNulls().create();
        Map<String,ValDef> res = gson.fromJson(jString, mapType);

        for (String key : res.keySet())
        {
            ValDef value = res.get(key);
            rv.put(key, value);
        }
        return rv;
    }

//    class TCL
//    {
//        private Integer ASD0;
//        private Double ASD1;
//        private String ASD2;
//        private String ASD3;
//    }
    public static void main(String[] args)
    {
        GsonBuilder gsonBuilder = new GsonBuilder();


//        gsonBuilder.registerTypeAdapter(BigDecimal.class, new JsonDeserializer<ValDef>()
//        {
//
//            @Override
//            public ValDef deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
//                return null;  //To change body of implemented methods use File | Settings | File Templates.
//            }
//        });
//


        Gson gson = gsonBuilder.serializeNulls().create();

        Map<String, ValDef> param = new HashMap<String, ValDef>();
        //param.put("ASD0",т);
//        param.put("ASD1","1.0");
//        param.put("ASD2","SDASD");
//        param.put("ASD3",null);
        String sparam=
                "{\n" +
                        "    \"ASD1\":{\n" +
                        "        \"val\":[\n" +
                        "            1.1\n" +
                        "        ], \n" +
                        "        \"valType\":\"FLOAT\", \n" +
                        "        \"oper\":\"=\"\n" +
                        "    }, \n" +
                        "    \"ASD0\":{\n" +
                        "        \"val\":[\n" +
                        "            1\n" +
                        "        ], \n" +
                        "        \"valType\":\"INTEGER\", \n" +
                        "        \"oper\":\"=\"\n" +
                        "    }, \n" +
                        "    \"ASD2\":{\n" +
                        "        \"val\":[\n" +
                        "            \"QWERTY\"\n" +
                        "        ], \n" +
                        "        \"valType\":\"TEXT\", \n" +
                        "        \"oper\":\"=\"\n" +
                        "    }, \n" +
                        "    \"ASD3\":{\n" +
                        "        \"val\":[\n" +
                        "            null\n" +
                        "        ], \n" +
                        "        \"valType\":null, \n" +
                        "        \"oper\":null\n" +
                        "    }\n" +
                        "}";

         Type mapType = new TypeToken<Map<String,ValDef>>() {}.getType();


        Map res = gson.fromJson(sparam, mapType);
        System.out.println("res = " + res);

//        String res = new TTest().checkJson();
//        System.out.println("res = " + res);

//        for (Object key : res.values()) {
//
//            System.out.println("key = " + key);
//        }


    }


}
