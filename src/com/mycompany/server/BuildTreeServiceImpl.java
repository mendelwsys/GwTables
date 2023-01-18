package com.mycompany.server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mycompany.client.BuildTreeService;
import com.mycompany.common.*;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mycompany.common.tables.HeaderSpanMimic;
import com.mycompany.server.export.excel.ExcelExporter;
import com.mwlib.tablo.derby.TestUserProfiles;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * The server side implementation of the RPC service.
 * Отладка сохранение профиля в дерево
 */
@SuppressWarnings("serial")
public class BuildTreeServiceImpl extends RemoteServiceServlet implements
        BuildTreeService {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

    private static final ThreadLocal<SimpleDateFormat> thsdf =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return sdf;
                }
            };

    TestUserProfiles testUserProfiles;

    {

        try {
            testUserProfiles = new TestUserProfiles();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public String greetServer(String input) {
        String serverInfo = getServletContext().getServerInfo();
        String userAgent = getThreadLocalRequest().getHeader("User-Agent");
        return "Hello, " + input + "!<br><br>I am running " + serverInfo
                + ".<br><br>It looks like you are using:<br>" + userAgent;
    }

    @Override
    public DescOperation getNodes(UserProfile profile) {
        try {

            String jString = testUserProfiles.getDescriptorByProfile2(profile.getProfileId());
            Gson gson = new GsonBuilder().serializeNulls().create();
            Type mapType = new TypeToken<DescOperation<Object>>() {
            }.getType();
            final DescOperation descOperation = gson.fromJson(jString, mapType);
            return descOperation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public DescOperation getNodes_new(BigDecimal userId, UserProfile profile) {
        try {

            String jString = testUserProfiles.getDescriptorByProfile_new(new BigDecimal("" + profile.getProfileId()));
            if (jString == null || jString.length() == 0)
                return null;
            Gson gson = new GsonBuilder().serializeNulls().create();
            Type mapType = new TypeToken<DescOperation<Object>>() {
            }.getType();
            final DescOperation descOperation = gson.fromJson(jString, mapType);
            descOperation.put("lastLoaded", thsdf.get().format(new Date()));
            saveNodes_new(userId, profile, descOperation);
            return descOperation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UserProfile saveNodes(String userId, UserProfile profile, DescOperation operations) {
        Type mapType = new TypeToken<DescOperation<Object>>() {
        }.getType();
        Gson gson = new GsonBuilder().serializeNulls().create();
        String jString = gson.toJson(operations, mapType);

        try {
            testUserProfiles.insertOrUpdateProfiles2(userId, profile, jString);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        List<UserProfile> ll= new LinkedList<UserProfile>(Arrays.asList(getProfiles(userId)));
//        ll.add(0,profile);
//        return ll.toArray(new UserProfile[ll.size()]);
        return profile;
    }

    @Override
    public UserProfile saveNodes_new(BigDecimal userId, UserProfile profile, DescOperation operations) {
        Type mapType = new TypeToken<DescOperation<Object>>() {
        }.getType();
        operations.put("lastLoaded", thsdf.get().format(new Date()));
        Gson gson = new GsonBuilder().serializeNulls().create();
        String jString = gson.toJson(operations, mapType);

        try {
            testUserProfiles.insertOrUpdateUser(userId);
            testUserProfiles.insertOrUpdateProfiles_new(profile, jString);
            testUserProfiles.addProfileToUser(userId, new BigDecimal("" + profile.getProfileId()));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        List<UserProfile> ll= new LinkedList<UserProfile>(Arrays.asList(getProfiles(userId)));
//        ll.add(0,profile);
//        return ll.toArray(new UserProfile[ll.size()]);
        return profile;
    }


    @Override
    public DescOperation testSerializer(UserProfile profile, DescOperation operations) {

        Type mapType = new TypeToken<DescOperation<Object>>() {
        }.getType();

        Gson gson = new GsonBuilder().serializeNulls().create();

        String jString = gson.toJson(operations, mapType);
        System.out.println("Serial jString = " + jString);

        return gson.fromJson(jString, mapType);
    }

    @Override
    public DmmyType dummy(DmmyType dummyVal) {
        return dummyVal;
    }

    /**
     * Метод БЛ получения профилей ПО ИДЕНТИФИКАТОРУ ПОЛЬЗОВАТЕЛЯ
     *
     * @param userId
     * @return
     */
    @Override
    public UserProfile[] getProfiles(String userId) {
        try {
            List<UserProfile> profilesByUser = testUserProfiles.getProfilesByUser(userId);
            return profilesByUser.toArray(new UserProfile[profilesByUser.size()]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new UserProfile[0];
    }

    /**
     * Метод БЛ получения профилей ПО ИДЕНТИФИКАТОРУ ПОЛЬЗОВАТЕЛЯ
     *
     * @param userId
     * @return
     */
    @Override
    public UserProfile[] getProfiles_new(BigDecimal userId) {
        try {
            List<UserProfile> profilesByUser = testUserProfiles.getProfilesByUser_new(userId, true);
            Gson gson = new GsonBuilder().serializeNulls().create();
            Type mapType = new TypeToken<DescOperation<Object>>() {
            }.getType();
            for (int i = 0; profilesByUser != null && i < profilesByUser.size(); i++) {
                try {
                    final DescOperation descOperation = gson.fromJson(profilesByUser.get(i).getProfileContents(), mapType);
                    setAdditionalParamsFromDescriptor(profilesByUser.get(i), descOperation);
                    profilesByUser.get(i).setProfileContents(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return profilesByUser.toArray(new UserProfile[profilesByUser.size()]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new UserProfile[0];
    }


    /**
     * Метод БЛ удаления профиля по идентификатору (Видимо подразумевается удаления профиля как единицы без привязки к пользователю)
     *
     * @param profile
     */
    @Override
    public void deleteProfile(UserProfile profile) {
        try {
            testUserProfiles.deleteByProfileId(profile.getProfileId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Метод БЛ удаления профиля по идентификатору (Видимо подразумевается удаления профиля как единицы без привязки к пользователю)
     *
     * @param profile
     */
    @Override
    public void deleteProfile_new(UserProfile profile) {
        try {
            testUserProfiles.deleteByProfileId_new(new BigDecimal(profile.getProfileId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String buildExcelReport(String records, String[] headers) {

        Type mapType = new TypeToken<Map<String, String>[]>() {
        }.getType();
        Gson gson = new GsonBuilder().serializeNulls().create();
        Map<String, String>[] res = gson.fromJson(records, mapType);
        ExcelExporter ee = new ExcelExporter();
        // ee.export(res,headers);
        return "tmpFName";
    }

    @Override
    public String[] buildExcelReport_new(String records, String styles, ListGridDescriptor desc, HeaderSpanMimic hsmroot) {
        try {
            Type mapType = new TypeToken<ExcelExporter.MyMap<String, String>[]>() {
            }.getType();
            Type mapType2 = new TypeToken<Map<String, String>>() {
            }.getType();
            Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(mapType, new MyMapToStringDeserializer()).create();
            ExcelExporter.MyMap<String, String>[] res = gson.fromJson(records, mapType);
            Map<String, String> res2 = gson.fromJson(styles, mapType2);


            if (hsmroot != null) {

                // removeShadowedSpans(hsmroot);
                removeHiddenFieldsAndSpans(hsmroot, desc.getChs());
                calculateRowSpans(hsmroot);
                desc.setGroupedHeader(hsmroot);

            }

            ExcelExporter ee = new ExcelExporter();
            String sessionId = this.getThreadLocalRequest().getSession().getId();
            String fileName = ee.export_new(sessionId, desc.getTableTitle() + " " + new Date().getTime(), res, res2, desc);
            return new String[]{sessionId, fileName};
        } catch (Exception e) {


        }
        return null;
    }

    private void removeHiddenFieldsAndSpans(HeaderSpanMimic hsmroot, ColumnHeadBean[] chs) {
        if (hsmroot.getFieldNames() != null) {

            List<String> currentFieldNames = new ArrayList<String>();

            currentFieldNames = Arrays.asList(hsmroot.getFieldNames());

            List<String> currentFieldNames2 = new ArrayList<String>();

            currentFieldNames2.addAll(currentFieldNames);

            for (int i = 0; i < currentFieldNames2.size(); i++) {
                if (findField(currentFieldNames2.get(i), chs) == null) {
                    currentFieldNames2.remove(i);
                    i--;

                }


            }
            if (currentFieldNames2.size() == 0)
                hsmroot.setFieldNames(null);
            else
                hsmroot.setFieldNames(currentFieldNames2.toArray(new String[currentFieldNames2.size()]));


        } else if (hsmroot.getSubs() != null) {

            List<HeaderSpanMimic> currentSubs = new ArrayList<HeaderSpanMimic>();
            List<HeaderSpanMimic> currentSubs2 = new ArrayList<HeaderSpanMimic>();
            currentSubs = Arrays.asList(hsmroot.getSubs());
            currentSubs2.addAll(currentSubs);
            for (int i = 0; i < hsmroot.getSubs().length; i++)
                removeHiddenFieldsAndSpans(hsmroot.getSubs()[i], chs);
            for (int i = 0; i < currentSubs2.size(); i++) {
                if ((currentSubs2.get(i).getFieldNames() == null || currentSubs2.get(i).getFieldNames().length == 0) && (currentSubs2.get(i).getSubs() == null || currentSubs2.get(i).getSubs().length == 0)) {
                    currentSubs2.remove(i);
                    i--;
                }


            }
            if (currentSubs2.size() == 0)
                hsmroot.setSubs(null);
            else
                hsmroot.setSubs(currentSubs2.toArray(new HeaderSpanMimic[currentSubs2.size()]));


        }


    }

    private ColumnHeadBean findField(String s, ColumnHeadBean[] chs) {
        for (int i = 0; i < chs.length; i++) {
            if (chs[i].getName().equals(s)) return chs[i];


        }
        return null;
    }


    private List<String> removeShadowedSpans(HeaderSpanMimic node) {
        List<String> removeFields = new ArrayList<String>();


        if (node.getSubs() != null) {
            List<String> removedAll = new ArrayList<String>();
            List<String> currentFieldNames = new ArrayList<String>();
            List<HeaderSpanMimic> currentSubs = new ArrayList<HeaderSpanMimic>();
            if (node.getFieldNames() != null) currentFieldNames = Arrays.asList(node.getFieldNames());
            currentSubs = Arrays.asList(node.getSubs());
            for (int i = 0; i < currentSubs.size(); i++) {
                List<String> removed = removeShadowedSpans(node.getSubs()[i]);
                if (removed.size() > 0) {
                    removedAll.addAll(removed);
                    currentSubs.remove(node.getSubs()[i]);
                    i--;

                }
            }
            currentFieldNames.addAll(removedAll);
            node.setSubs(currentSubs.toArray(new HeaderSpanMimic[currentSubs.size()]));
            node.setFieldNames(currentFieldNames.toArray(new String[currentFieldNames.size()]));

        }
        if (node.getFieldNames() != null && node.getFieldNames().length == 1 && node.getSubs() == null) {
            //removeFields.add(node.getFieldNames());
            List<String> nodeFieldName = Arrays.asList(node.getFieldNames());
            removeFields.addAll(nodeFieldName);

        }
        return removeFields;

    }

    private void calculateRowSpans(HeaderSpanMimic hsmroot) {

        int[] height = findMaxHeight(hsmroot, 0);
        int[] height2 = setRowColSpans2(hsmroot.getSubs(), height[1], 0);
        hsmroot.setRowspan(height[1]);

    }


    int[] findMaxHeight(HeaderSpanMimic node, int currentHeight) {
        currentHeight++;
        int maxHeight = 0;
        if (node.getSubs() == null || node.getSubs().length == 0) return new int[]{currentHeight, currentHeight};
        for (int i = 0; i < node.getSubs().length; i++) {
            int[] height = findMaxHeight(node.getSubs()[i], currentHeight);
            if (height[1] > maxHeight) maxHeight = height[1];
        }

        return new int[]{currentHeight, maxHeight};
    }


    int[] setRowColSpans(HeaderSpanMimic[] node, int maxHeight, int currentHeight) {
        // if (node==null) return new int [] {currentHeight,1};
        currentHeight++;
        int colSpans = 0;
        int rowSpan = currentHeight;
        for (int i = 0; i < node.length; i++) {
            if (node[i].getSubs() == null && node[i].getFieldNames() != null) {
                node[i].setColspan(node[i].getFieldNames().length);
                colSpans += node[i].getFieldNames().length;
                node[i].setRowspan(maxHeight - currentHeight);
                //  if (maxHeight-currentHeight>rowSpan)
                // rowSpan = maxHeight-currentHeight;

            } else if (node[i].getSubs() != null) {
                int[] height = setRowColSpans(node[i].getSubs(), maxHeight, currentHeight);
                node[i].setRowspan(maxHeight - height[0]);
                // if (height[0]>rowSpan)
                rowSpan = height[0];
                node[i].setColspan(height[1]);
                colSpans += height[1];
            } else {
                node[i].setRowspan(maxHeight - currentHeight);
                node[i].setColspan(colSpans + 1);
                // if (maxHeight-currentHeight>rowSpan)
                //rowSpan = maxHeight-currentHeight;
            }

        }

        return new int[]{rowSpan, colSpans};
    }

    int[] setRowColSpans3(HeaderSpanMimic[] node, int maxHeight, int currentLevel) {
        // if (node==null) return new int [] {currentHeight,1};
        currentLevel++;
        int colSpans = 0;
        int downrowSpan = currentLevel;
        int maxRowSpan = 1;
        int toUpperHeight = 0;
        for (int i = 0; i < node.length; i++) {
            if (node[i].getSubs() == null && node[i].getFieldNames() != null) {
                node[i].setColspan(node[i].getFieldNames().length);
                colSpans += node[i].getFieldNames().length;
                if (maxRowSpan > maxHeight - downrowSpan) {
                    node[i].setRowspan(maxRowSpan);
                    toUpperHeight = maxRowSpan + 1;

                } else {
                    node[i].setRowspan(maxHeight - downrowSpan);
                    toUpperHeight = node[i].getRowspan() + 1;
                }
                //  if (maxHeight-currentHeight>rowSpan)
                // rowSpan += node[i].getRowspan()-1;

            } else if (node[i].getSubs() != null) {
                int[] height = setRowColSpans3(node[i].getSubs(), maxHeight, currentLevel);
                node[i].setRowspan((maxHeight - height[0]) / currentLevel);
                if (downrowSpan < height[0])
                    downrowSpan = height[0];
                if ((maxHeight - height[0]) / currentLevel > maxRowSpan)
                    maxRowSpan = ((maxHeight - height[0]) / currentLevel);
                node[i].setColspan(height[1]);
                colSpans += height[1];
                toUpperHeight = downrowSpan + ((maxHeight - height[0])) / currentLevel;
            } /*else {
                node[i].setRowspan(maxHeight - currentLevel);
                node[i].setColspan(colSpans + 1);
                // if (maxHeight-currentHeight>rowSpan)
                //rowSpan = maxHeight-currentHeight;
            }*/


        }

        return new int[]{toUpperHeight, colSpans};
    }

    int[] setRowColSpans2(HeaderSpanMimic[] node, int maxHeight, int currentLevel) {
        // if (node==null) return new int [] {currentHeight,1};
        currentLevel++;
        int colSpans = 0;
        int downrowSpan = currentLevel;
        int maxRowSpan = 1;
        int toUpperHeight = 0;
        for (int i = 0; i < node.length; i++) {
            if (node[i].getSubs() == null && node[i].getFieldNames() != null) {
                node[i].setColspan(node[i].getFieldNames().length);
                colSpans += node[i].getFieldNames().length;
                if (maxRowSpan > maxHeight - downrowSpan) {
                    node[i].setRowspan(maxRowSpan);
                    toUpperHeight = maxRowSpan + 1;

                } else {
                    node[i].setRowspan(maxHeight - downrowSpan);
                    toUpperHeight = node[i].getRowspan() + 1;
                    if (maxRowSpan < maxHeight - downrowSpan)
                        maxRowSpan = maxHeight - downrowSpan;
                }
                //  if (maxHeight-currentHeight>rowSpan)
                // rowSpan += node[i].getRowspan()-1;

            } else if (node[i].getSubs() != null) {
                int[] height = setRowColSpans2(node[i].getSubs(), maxHeight, currentLevel);
                int rs = (maxHeight - height[0]) / currentLevel;
                if (rs == 0) rs = 1;
                node[i].setRowspan(rs);
                if (downrowSpan < height[0])
                    downrowSpan = height[0];
                if (rs > maxRowSpan)
                    maxRowSpan = rs;
                node[i].setColspan(height[1]);
                colSpans += height[1];
                toUpperHeight = downrowSpan + rs;
            } /*else {
                node[i].setRowspan(maxHeight - currentLevel);
                node[i].setColspan(colSpans + 1);
                // if (maxHeight-currentHeight>rowSpan)
                //rowSpan = maxHeight-currentHeight;
            }*/


        }

        return new int[]{toUpperHeight, colSpans};
    }
    static class MyMapToStringDeserializer implements JsonDeserializer<ExcelExporter.MyMap<String, String>[]> {
        @Override
        public ExcelExporter.MyMap<String, String>[] deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {


            // System.out.println("je = " + je.toString());
            //   if (je.isJsonObject())
            //   {
            // System.out.println("Found object");
            //     Set<Map.Entry<String,JsonElement>> elements = ((com.google.gson.JsonObject) je).entrySet();
            //      Iterator<Map.Entry<String, JsonElement>> it =  elements.iterator();
            //      while(it.hasNext()) {
            //          Map.Entry<String, JsonElement> entry = it.next();
            //   System.out.println("key = "+entry.getKey()+" value = "+entry.getValue());
                /*if (entry.getKey().equalsIgnoreCase("groupMembers"))
                {
                    System.out.println("Found groupMembers");
                    System.out.println("array = "+entry.getValue().isJsonArray());
                    System.out.println("object = "+entry.getValue().isJsonObject());
                    System.out.println("null = "+entry.getValue().isJsonNull());




                }
                    else
                {


                    System.out.println("array = "+entry.getValue().isJsonArray());
                    System.out.println("object = "+entry.getValue().isJsonObject());
                    System.out.println("null = "+entry.getValue().isJsonNull());
                    System.out.println("primitive = "+entry.getValue().isJsonPrimitive());

                }*/

            //     }


            //    }
            /*if(je.isJsonPrimitive())
            {
                ExcelExporter.MyMap<String,String> map = new ExcelExporter.MyMap<String,String>();
                System.out.println("Found primitive");
                //map.put()




            }*/
            ExcelExporter.MyMap<String, String>[] mm = null;
            if (je.isJsonArray()) {
                //   System.out.println("je is array");
//Массив MyMap
                ExcelExporter.MyMap<String, String>[] m = new ExcelExporter.MyMap[((JsonArray) je).size()];

                for (int i = 0; i < ((JsonArray) je).size(); i++) {


                  /*  System.out.println("array = " + ((JsonArray) je).get(i).isJsonArray());
                    System.out.println("object = " + ((JsonArray) je).get(i).isJsonObject());
                    System.out.println("null = " + ((JsonArray) je).get(i).isJsonNull());
                    System.out.println("primitive = " + ((JsonArray) je).get(i).isJsonPrimitive());*/
                    m[i] = deserializeInt(((JsonArray) je).get(i), type, jdc);

                }
                mm = m;
            }

            return mm;
        }

        public ExcelExporter.MyMap<String, String> deserializeInt(JsonElement je, Type type, JsonDeserializationContext jdc)
                throws JsonParseException {
            //  System.out.println("je = " + je.toString());
            ExcelExporter.MyMap<String, String> mm = new ExcelExporter.MyMap();
            if (je.isJsonObject()) {
                // System.out.println("Found object");
                Set<Map.Entry<String, JsonElement>> elements = ((com.google.gson.JsonObject) je).entrySet();
                Iterator<Map.Entry<String, JsonElement>> it = elements.iterator();
                while (it.hasNext()) {
                    Map.Entry<String, JsonElement> entry = it.next();
                    //  System.out.println("key = "+entry.getKey()+" value = "+entry.getValue());
                    if (entry.getKey().equalsIgnoreCase("groupMembers")) {
                       /* System.out.println("Found groupMembers");
                        System.out.println("array = "+entry.getValue().isJsonArray());
                        System.out.println("object = "+entry.getValue().isJsonObject());
                        System.out.println("null = "+entry.getValue().isJsonNull());*/
                        if (entry.getValue().isJsonArray()) {

                            mm.setGroupMembers(deserialize(entry.getValue(), type, jdc));
                        }

                    } else if (!entry.getValue().isJsonArray()) {


                      /*  System.out.println("array = "+entry.getValue().isJsonArray());
                        System.out.println("object = "+entry.getValue().isJsonObject());
                        System.out.println("null = "+entry.getValue().isJsonNull());
                        System.out.println("primitive = "+entry.getValue().isJsonPrimitive());*/
                        if (entry.getValue().isJsonNull())
                            mm.put(entry.getKey(), null);
                        else
                            mm.put(entry.getKey(), entry.getValue().getAsString());

                    }

                }


            }
           /* if(je.isJsonPrimitive())
            {
                ExcelExporter.MyMap<String,String> map = new ExcelExporter.MyMap<String,String>();
                System.out.println("Found primitive");
                //map.put()




            }*/

            if (je.isJsonArray()) {
                //  System.out.println("je is array");
//Массив MyMap
                ExcelExporter.MyMap<String, String>[] m = new ExcelExporter.MyMap[((JsonArray) je).size()];

                for (int i = 0; i < ((JsonArray) je).size(); i++) {


                  /*  System.out.println("array = " + ((JsonArray) je).get(i).isJsonArray());
                    System.out.println("object = " + ((JsonArray) je).get(i).isJsonObject());
                    System.out.println("null = " + ((JsonArray) je).get(i).isJsonNull());
                    System.out.println("primitive = " + ((JsonArray) je).get(i).isJsonPrimitive());*/
                    m[i] = deserializeInt(((JsonArray) je).get(i), type, jdc);

                }
                mm.setGroupMembers(m);
            }

            return mm;

        }


    }


//    public static void main(String[] args) throws IOException
//    {
//        DescOperation des1 = new DescOperation();
//        des1.apiName="dddddd";
//        des1.put("A1",1);
//        des1.put("A1Z",1.1);
//        des1.put("A2","AASASAS");
//        des1.put("A3",null);
//
//        DescOperation des2 = new DescOperation();
//        des2.apiName="dddddd1";
//        des2.put("A11",11);
//        des2.put("A21","1AASASAS");
//        des2.put("A31",null);
//        des1.getSubOperation().add(des2);
//
//
//        Type mapType = new TypeToken<DescOperation<Object>>() {}.getType();
//
//        final GsonBuilder gsonBuilder = new GsonBuilder();
//
//        Gson gson = gsonBuilder.serializeNulls().create();
//        String jString=gson.toJson(des1,mapType);
//
////        gson.getAdapter(Number.class);
//
//        JsonParser parser = new JsonParser();
//        JsonObject obj = parser.parse("{val: 1.1}").getAsJsonObject();
//
//        JsonPrimitive res = (JsonPrimitive) obj.get("val");
//        res.isNumber();
////        res=((JsonObject) res).get("A1Z");
////        res=((JsonObject) res).getAsJsonPrimitive("val");
//
//
//        DescOperation<Object> operations1 = gson.fromJson(jString,mapType);
//
//
//
//
//        //new DefaultTypeAdapters();
//        System.out.println("operations1 = " + operations1);
//
//
//
//        //JsonDeserializer<Object>()
//
//    }

    static Integer firstRestartHourInteger = null;
    static Boolean restartEnabled = false;
    static Integer restartHoursPeriodInteger = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        String firstRestartHour = config.getInitParameter("firstRestartHour");
        String restartHoursPeriod = config.getInitParameter("restartHoursPeriod");
        String sRestartEnabled = config.getInitParameter("restartEnabled");
        firstRestartHourInteger = Integer.parseInt(firstRestartHour);
        restartHoursPeriodInteger = Integer.parseInt(restartHoursPeriod);
        restartEnabled = Boolean.parseBoolean(sRestartEnabled);
        System.out.println("firstRestartHourInteger = " + firstRestartHourInteger + " restartHoursPeriodInteger=" + restartHoursPeriodInteger + " restartEnabled = " + restartEnabled);

    }

    public static void main(String[] args) {


//        String records="[   { EVTYPE_NAME={val=ОКНА},VID_ID={val=28} }," +
//                "    {EVTYPE_NAME={val=ОКНА1},VID_ID={val=28} }] ";


     /*   String records="{\n" +
                "    \"EVTYPE_NAME\":\"ОКНА\", \n" +
                "    \"o_serv\":\"П\", \n" +
                "    \"PRED_NAME\":\"ПЧ-2\", \n" +
                "    \"VID_ID\":28, \n" +
                "    \"ID_Z\":\"6822\", \n" +
                "    \"o_state\":\"54\", \n" +
                "    \"id\":\"10;6822##54\", \n" +
                "    \"OVERTIME\":0, \n" +
                "    \"DT_KD\":\"2015-02-20T17:10:00.000\", \n" +
                "    \"PRED_ID\":1595, \n" +
                "    \"EVTYPE\":\"WINDOWS\", \n" +
                "    \"STATUS_FACT\":2, \n" +
                "    \"DT_ND\":\"2015-02-20T15:35:00.000\", \n" +
                "    \"STATUS_PL\":1, \n" +
                "    \"DOR_KOD\":10, \n" +
                "    \"COMMENT\":\"<b>Текущее содержание пути:</b><br>Одиночная смена рельсов (1 шт)\", \n" +
                "    \"actual\":1, \n" +
                "    \"PEREG\":\"ЧЕРНЯХОВСК-ПАСТУХОВО-НОВ\", \n" +
                "    \"DOR_NAME\":\"Калининградская\", \n" +
                "    \"VID_NAME\":\"П\", \n" +
                "    \"ND\":\"2015-02-20T15:00:00.000\", \n" +
                "    \"KD\":\"2015-02-20T17:00:00.000\", \n" +
                "    \"o_state_desc\":\"http://host_card:port_card/wnd_pr/operative/card.jsp?wid=6822&dorKod=10&opener=0\", \n" +
                "    \"linkText\":\"Закрыто\", \n" +
                "    \"rowstyle\":\"background-color:#55DD00;\", \n" +
                "    \"ORDIX\":0\n" +
                "}";*/

        //  Type mapType = new TypeToken<Map<String,String>>() {}.getType();
        // Gson gson = new GsonBuilder().serializeNulls().create();

//        Map<String,GWTSuccs<Object>> map = new HashMap<String,GWTSuccs<Object>>();
//        map.put("XXXX",new GWTSuccs(28));
//        map.put("XXXX1",new GWTSuccs("Sss"));

//        Type mapType2 = new TypeToken<Map<String,GWTSuccs<Object>>>() {}.getType();
//        String res1=gson.toJson(map,mapType2);

        //Map<String,GWTSuccs<Object>>[] res = gson.fromJson(records, mapType);
        // Map<String,String> res = gson.fromJson(records, mapType);


        BuildTreeServiceImpl btsi = new BuildTreeServiceImpl();

        String s = "[\n" +
                "    {\n" +
                "        \"groupName\":\"DOR_NAME\", \n" +
                "        \"groupValue\":\"Горьковская\", \n" +
                "        \"$52e\":true, \n" +
                "        \"canDrag\":false, \n" +
                "        \"DOR_NAME\":\"Горьковская\", \n" +
                "        \"_baseStyle\":null, \n" +
                "        \"customStyle\":\"groupNode\", \n" +
                "        \"_canEdit\":false, \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_0\", \n" +
                "        \"isFolder\":true, \n" +
                "        \"groupMembers\":[\n" +
                "            {\n" +
                "                \"PRED_NAME\":\"ПЧ-10\", \n" +
                "                \"VID_ID\":28, \n" +
                "                \"PRICH_NAME\":\"ПОДГОТОВКА К ОКНУ\", \n" +
                "                \"VEL\":60, \n" +
                "                \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "                \"id\":\"24;2007262##44\", \n" +
                "                \"PLACE\":\"БУМКОМБИНАТ - ПОЛОЙ<br>гл. путь 1 , 980км 3пк - 981км\", \n" +
                "                \"TIM_BEG\":\"2015-04-01T18:30:00.000\", \n" +
                "                \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"60/60/60/-/-\", \n" +
                "                \"VPAS\":60, \n" +
                "                \"DOR_NAME\":\"Горьковская\", \n" +
                "                \"TLG\":\"124<br>01.04.2015 10:24\", \n" +
                "                \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "                \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=24&pid=2007262&pids=2007262\", \n" +
                "                \"rowcolor\":\"GRAY\", \n" +
                "                \"LEN\":\"1.7\", \n" +
                "                \"colorStatus\":3, \n" +
                "                \"PRED_ID\":1899, \n" +
                "                \"EVTYPE\":\"WARNINGS\", \n" +
                "                \"DOR_KOD\":24, \n" +
                "                \"actual\":1, \n" +
                "                \"VID_NAME\":\"П\", \n" +
                "                \"VGR\":60, \n" +
                "                \"linkText\":\"Карточка\", \n" +
                "                \"groupParentId\":null, \n" +
                "                \"name\":\"0_1\", \n" +
                "                \"isFolder\":null\n" +
                "            }, \n" +
                "            {\n" +
                "                \"PRED_NAME\":\"ПЧ-23\", \n" +
                "                \"VID_ID\":28, \n" +
                "                \"PRICH_NAME\":\"ДЕФЕКТНОСТЬ РЕЛЬСОВ\", \n" +
                "                \"VEL\":70, \n" +
                "                \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "                \"id\":\"24;2008119##44\", \n" +
                "                \"PLACE\":\"КИЗНЕР<br>гл. путь 1 , 973км 1пк - 973км\", \n" +
                "                \"TIM_BEG\":\"2015-04-01T18:35:00.000\", \n" +
                "                \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"70/70/70/-/-\", \n" +
                "                \"VPAS\":70, \n" +
                "                \"DOR_NAME\":\"Горьковская\", \n" +
                "                \"TLG\":\"35А<br>01.04.2015 18:30\", \n" +
                "                \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "                \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=24&pid=2008119&pids=2008119\", \n" +
                "                \"rowcolor\":\"GRAY\", \n" +
                "                \"LEN\":\"0.3\", \n" +
                "                \"colorStatus\":3, \n" +
                "                \"PRED_ID\":5065, \n" +
                "                \"EVTYPE\":\"WARNINGS\", \n" +
                "                \"DOR_KOD\":24, \n" +
                "                \"actual\":1, \n" +
                "                \"VID_NAME\":\"П\", \n" +
                "                \"VGR\":70, \n" +
                "                \"linkText\":\"Карточка\", \n" +
                "                \"groupParentId\":null, \n" +
                "                \"name\":\"0_2\", \n" +
                "                \"isFolder\":null\n" +
                "            }\n" +
                "        ], \n" +
                "        \"singleCellValue\":\"Горьковская\"\n" +
                "    }, \n" +
                "    {\n" +
                "        \"PRED_NAME\":\"ПЧ-10\", \n" +
                "        \"VID_ID\":28, \n" +
                "        \"PRICH_NAME\":\"ПОДГОТОВКА К ОКНУ\", \n" +
                "        \"VEL\":60, \n" +
                "        \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "        \"id\":\"24;2007262##44\", \n" +
                "        \"PLACE\":\"БУМКОМБИНАТ - ПОЛОЙ<br>гл. путь 1 , 980км 3пк - 981км\", \n" +
                "        \"TIM_BEG\":\"2015-04-01T18:30:00.000\", \n" +
                "        \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"60/60/60/-/-\", \n" +
                "        \"VPAS\":60, \n" +
                "        \"DOR_NAME\":\"Горьковская\", \n" +
                "        \"TLG\":\"124<br>01.04.2015 10:24\", \n" +
                "        \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "        \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=24&pid=2007262&pids=2007262\", \n" +
                "        \"rowcolor\":\"GRAY\", \n" +
                "        \"LEN\":\"1.7\", \n" +
                "        \"colorStatus\":3, \n" +
                "        \"PRED_ID\":1899, \n" +
                "        \"EVTYPE\":\"WARNINGS\", \n" +
                "        \"DOR_KOD\":24, \n" +
                "        \"actual\":1, \n" +
                "        \"VID_NAME\":\"П\", \n" +
                "        \"VGR\":60, \n" +
                "        \"linkText\":\"Карточка\", \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_1\", \n" +
                "        \"isFolder\":null\n" +
                "    }, \n" +
                "    {\n" +
                "        \"PRED_NAME\":\"ПЧ-23\", \n" +
                "        \"VID_ID\":28, \n" +
                "        \"PRICH_NAME\":\"ДЕФЕКТНОСТЬ РЕЛЬСОВ\", \n" +
                "        \"VEL\":70, \n" +
                "        \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "        \"id\":\"24;2008119##44\", \n" +
                "        \"PLACE\":\"КИЗНЕР<br>гл. путь 1 , 973км 1пк - 973км\", \n" +
                "        \"TIM_BEG\":\"2015-04-01T18:35:00.000\", \n" +
                "        \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"70/70/70/-/-\", \n" +
                "        \"VPAS\":70, \n" +
                "        \"DOR_NAME\":\"Горьковская\", \n" +
                "        \"TLG\":\"35А<br>01.04.2015 18:30\", \n" +
                "        \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "        \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=24&pid=2008119&pids=2008119\", \n" +
                "        \"rowcolor\":\"GRAY\", \n" +
                "        \"LEN\":\"0.3\", \n" +
                "        \"colorStatus\":3, \n" +
                "        \"PRED_ID\":5065, \n" +
                "        \"EVTYPE\":\"WARNINGS\", \n" +
                "        \"DOR_KOD\":24, \n" +
                "        \"actual\":1, \n" +
                "        \"VID_NAME\":\"П\", \n" +
                "        \"VGR\":70, \n" +
                "        \"linkText\":\"Карточка\", \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_2\", \n" +
                "        \"isFolder\":null\n" +
                "    }, \n" +
                "    {\n" +
                "        \"groupName\":\"DOR_NAME\", \n" +
                "        \"groupValue\":\"Калининградская\", \n" +
                "        \"$52e\":true, \n" +
                "        \"canDrag\":false, \n" +
                "        \"DOR_NAME\":\"Калининградская\", \n" +
                "        \"_baseStyle\":null, \n" +
                "        \"customStyle\":\"groupNode\", \n" +
                "        \"_canEdit\":false, \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_3\", \n" +
                "        \"isFolder\":true, \n" +
                "        \"groupMembers\":[\n" +
                "            {\n" +
                "                \"PRED_NAME\":\"ПЧ-2\", \n" +
                "                \"VID_ID\":28, \n" +
                "                \"PRICH_NAME\":\"РАЗРЯД ТЕМПЕРАТУРНЫХ НАПРЯЖЕНИЙ\", \n" +
                "                \"TIM_OTM\":\"2015-04-02T00:30:00.000\", \n" +
                "                \"id\":\"10;119732##44\", \n" +
                "                \"PLACE\":\"ГУСЕВ - НЕСТЕРОВ<br>гл. путь 1 , 1148км 7пк - 1149км\", \n" +
                "                \"TIM_BEG\":\"2015-04-01T18:30:00.000\", \n" +
                "                \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"60/60/-/-/-\", \n" +
                "                \"VPAS\":60, \n" +
                "                \"DOR_NAME\":\"Калининградская\", \n" +
                "                \"TLG\":\"41<br>01.04.2015 10:00\", \n" +
                "                \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "                \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=10&pid=119732&pids=119732\", \n" +
                "                \"rowcolor\":\"GRAY\", \n" +
                "                \"LEN\":\"0.7\", \n" +
                "                \"colorStatus\":3, \n" +
                "                \"PRED_ID\":1595, \n" +
                "                \"EVTYPE\":\"WARNINGS\", \n" +
                "                \"DOR_KOD\":10, \n" +
                "                \"actual\":1, \n" +
                "                \"VID_NAME\":\"П\", \n" +
                "                \"VGR\":60, \n" +
                "                \"linkText\":\"Карточка\", \n" +
                "                \"groupParentId\":null, \n" +
                "                \"name\":\"0_4\", \n" +
                "                \"isFolder\":null\n" +
                "            }\n" +
                "        ], \n" +
                "        \"singleCellValue\":\"Калининградская\"\n" +
                "    }, \n" +
                "    {\n" +
                "        \"PRED_NAME\":\"ПЧ-2\", \n" +
                "        \"VID_ID\":28, \n" +
                "        \"PRICH_NAME\":\"РАЗРЯД ТЕМПЕРАТУРНЫХ НАПРЯЖЕНИЙ\", \n" +
                "        \"TIM_OTM\":\"2015-04-02T00:30:00.000\", \n" +
                "        \"id\":\"10;119732##44\", \n" +
                "        \"PLACE\":\"ГУСЕВ - НЕСТЕРОВ<br>гл. путь 1 , 1148км 7пк - 1149км\", \n" +
                "        \"TIM_BEG\":\"2015-04-01T18:30:00.000\", \n" +
                "        \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"60/60/-/-/-\", \n" +
                "        \"VPAS\":60, \n" +
                "        \"DOR_NAME\":\"Калининградская\", \n" +
                "        \"TLG\":\"41<br>01.04.2015 10:00\", \n" +
                "        \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "        \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=10&pid=119732&pids=119732\", \n" +
                "        \"rowcolor\":\"GRAY\", \n" +
                "        \"LEN\":\"0.7\", \n" +
                "        \"colorStatus\":3, \n" +
                "        \"PRED_ID\":1595, \n" +
                "        \"EVTYPE\":\"WARNINGS\", \n" +
                "        \"DOR_KOD\":10, \n" +
                "        \"actual\":1, \n" +
                "        \"VID_NAME\":\"П\", \n" +
                "        \"VGR\":60, \n" +
                "        \"linkText\":\"Карточка\", \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_4\", \n" +
                "        \"isFolder\":null\n" +
                "    }, \n" +
                "    {\n" +
                "        \"groupName\":\"DOR_NAME\", \n" +
                "        \"groupValue\":\"Куйбышевская\", \n" +
                "        \"$52e\":true, \n" +
                "        \"canDrag\":false, \n" +
                "        \"DOR_NAME\":\"Куйбышевская\", \n" +
                "        \"_baseStyle\":null, \n" +
                "        \"customStyle\":\"groupNode\", \n" +
                "        \"_canEdit\":false, \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_5\", \n" +
                "        \"isFolder\":true, \n" +
                "        \"groupMembers\":[\n" +
                "            {\n" +
                "                \"PRED_NAME\":\"ПЧ-9\", \n" +
                "                \"VID_ID\":28, \n" +
                "                \"PRICH_NAME\":\"РИХТОВКА\", \n" +
                "                \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "                \"id\":\"63;3491910##44\", \n" +
                "                \"PLACE\":\"ПРАВАЯ ВОЛГА - ОБШАРОВКА<br>гл. путь 1 , 992км 1пк - 992км\", \n" +
                "                \"TIM_BEG\":\"2015-04-01T18:35:00.000\", \n" +
                "                \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"120/80/-/-/-\", \n" +
                "                \"VPAS\":120, \n" +
                "                \"DOR_NAME\":\"Куйбышевская\", \n" +
                "                \"TLG\":\"122<br>01.04.2015 18:21\", \n" +
                "                \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "                \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=63&pid=3491910&pids=3491910\", \n" +
                "                \"rowcolor\":\"GRAY\", \n" +
                "                \"LEN\":\"0.1\", \n" +
                "                \"colorStatus\":3, \n" +
                "                \"PRED_ID\":1933, \n" +
                "                \"EVTYPE\":\"WARNINGS\", \n" +
                "                \"DOR_KOD\":63, \n" +
                "                \"actual\":1, \n" +
                "                \"VID_NAME\":\"П\", \n" +
                "                \"VGR\":80, \n" +
                "                \"linkText\":\"Карточка\", \n" +
                "                \"groupParentId\":null, \n" +
                "                \"name\":\"0_6\", \n" +
                "                \"isFolder\":null\n" +
                "            }\n" +
                "        ], \n" +
                "        \"singleCellValue\":\"Куйбышевская\"\n" +
                "    }, \n" +
                "    {\n" +
                "        \"PRED_NAME\":\"ПЧ-9\", \n" +
                "        \"VID_ID\":28, \n" +
                "        \"PRICH_NAME\":\"РИХТОВКА\", \n" +
                "        \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "        \"id\":\"63;3491910##44\", \n" +
                "        \"PLACE\":\"ПРАВАЯ ВОЛГА - ОБШАРОВКА<br>гл. путь 1 , 992км 1пк - 992км\", \n" +
                "        \"TIM_BEG\":\"2015-04-01T18:35:00.000\", \n" +
                "        \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"120/80/-/-/-\", \n" +
                "        \"VPAS\":120, \n" +
                "        \"DOR_NAME\":\"Куйбышевская\", \n" +
                "        \"TLG\":\"122<br>01.04.2015 18:21\", \n" +
                "        \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "        \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=63&pid=3491910&pids=3491910\", \n" +
                "        \"rowcolor\":\"GRAY\", \n" +
                "        \"LEN\":\"0.1\", \n" +
                "        \"colorStatus\":3, \n" +
                "        \"PRED_ID\":1933, \n" +
                "        \"EVTYPE\":\"WARNINGS\", \n" +
                "        \"DOR_KOD\":63, \n" +
                "        \"actual\":1, \n" +
                "        \"VID_NAME\":\"П\", \n" +
                "        \"VGR\":80, \n" +
                "        \"linkText\":\"Карточка\", \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_6\", \n" +
                "        \"isFolder\":null\n" +
                "    }, \n" +
                "    {\n" +
                "        \"groupName\":\"DOR_NAME\", \n" +
                "        \"groupValue\":\"Приволжская\", \n" +
                "        \"$52e\":true, \n" +
                "        \"canDrag\":false, \n" +
                "        \"DOR_NAME\":\"Приволжская\", \n" +
                "        \"_baseStyle\":null, \n" +
                "        \"customStyle\":\"groupNode\", \n" +
                "        \"_canEdit\":false, \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_7\", \n" +
                "        \"isFolder\":true, \n" +
                "        \"groupMembers\":[\n" +
                "            {\n" +
                "                \"PRED_NAME\":\"ПЧ-11\", \n" +
                "                \"VID_ID\":28, \n" +
                "                \"PRICH_NAME\":\"СМЕНА РЕЛЬСА\", \n" +
                "                \"VEL\":50, \n" +
                "                \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "                \"id\":\"61;3924632##44\", \n" +
                "                \"PLACE\":\"СУХОЙ КАРАБУЛАК - ЕЛХОВКА<br>гл. путь 1 , 230км 9пк - 230км\", \n" +
                "                \"TIM_BEG\":\"2015-04-01T18:30:00.000\", \n" +
                "                \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"50/50/50/-/-\", \n" +
                "                \"VPAS\":50, \n" +
                "                \"DOR_NAME\":\"Приволжская\", \n" +
                "                \"TLG\":\"105<br>01.04.2015 15:30\", \n" +
                "                \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "                \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=61&pid=3924632&pids=3924632\", \n" +
                "                \"rowcolor\":\"GRAY\", \n" +
                "                \"LEN\":\"0.1\", \n" +
                "                \"colorStatus\":3, \n" +
                "                \"PRED_ID\":3732, \n" +
                "                \"EVTYPE\":\"WARNINGS\", \n" +
                "                \"DOR_KOD\":61, \n" +
                "                \"actual\":1, \n" +
                "                \"VID_NAME\":\"П\", \n" +
                "                \"VGR\":50, \n" +
                "                \"linkText\":\"Карточка\", \n" +
                "                \"groupParentId\":null, \n" +
                "                \"name\":\"0_8\", \n" +
                "                \"isFolder\":null\n" +
                "            }, \n" +
                "            {\n" +
                "                \"rowstyle\":\"background-color:#DDDD00;\", \n" +
                "                \"PRED_NAME\":\"ПЧ-2\", \n" +
                "                \"VID_ID\":28, \n" +
                "                \"PRICH_NAME\":\"СМЕНА РЕЛЬСА\", \n" +
                "                \"TIM_OTM\":\"2015-04-01T19:20:00.000\", \n" +
                "                \"id\":\"61;3924846##44\", \n" +
                "                \"PLACE\":\"АХТУБА<br> , -1км -1пк - -1км\", \n" +
                "                \"TIM_BEG\":\"2015-04-01T18:30:00.000\", \n" +
                "                \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"25/25/-/-/-\", \n" +
                "                \"VPAS\":25, \n" +
                "                \"DOR_NAME\":\"Приволжская\", \n" +
                "                \"TLG\":\"58<br>01.04.2015 18:31\", \n" +
                "                \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "                \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=61&pid=3924846&pids=3924846\", \n" +
                "                \"rowcolor\":\"YELLOW\", \n" +
                "                \"LEN\":\"1.0\", \n" +
                "                \"colorStatus\":1, \n" +
                "                \"PRED_ID\":834, \n" +
                "                \"EVTYPE\":\"WARNINGS\", \n" +
                "                \"DOR_KOD\":61, \n" +
                "                \"actual\":1, \n" +
                "                \"VID_NAME\":\"П\", \n" +
                "                \"VGR\":25, \n" +
                "                \"linkText\":\"Карточка\", \n" +
                "                \"groupParentId\":null, \n" +
                "                \"name\":\"0_9\", \n" +
                "                \"isFolder\":null\n" +
                "            }\n" +
                "        ], \n" +
                "        \"singleCellValue\":\"Приволжская\"\n" +
                "    }, \n" +
                "    {\n" +
                "        \"PRED_NAME\":\"ПЧ-11\", \n" +
                "        \"VID_ID\":28, \n" +
                "        \"PRICH_NAME\":\"СМЕНА РЕЛЬСА\", \n" +
                "        \"VEL\":50, \n" +
                "        \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "        \"id\":\"61;3924632##44\", \n" +
                "        \"PLACE\":\"СУХОЙ КАРАБУЛАК - ЕЛХОВКА<br>гл. путь 1 , 230км 9пк - 230км\", \n" +
                "        \"TIM_BEG\":\"2015-04-01T18:30:00.000\", \n" +
                "        \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"50/50/50/-/-\", \n" +
                "        \"VPAS\":50, \n" +
                "        \"DOR_NAME\":\"Приволжская\", \n" +
                "        \"TLG\":\"105<br>01.04.2015 15:30\", \n" +
                "        \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "        \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=61&pid=3924632&pids=3924632\", \n" +
                "        \"rowcolor\":\"GRAY\", \n" +
                "        \"LEN\":\"0.1\", \n" +
                "        \"colorStatus\":3, \n" +
                "        \"PRED_ID\":3732, \n" +
                "        \"EVTYPE\":\"WARNINGS\", \n" +
                "        \"DOR_KOD\":61, \n" +
                "        \"actual\":1, \n" +
                "        \"VID_NAME\":\"П\", \n" +
                "        \"VGR\":50, \n" +
                "        \"linkText\":\"Карточка\", \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_8\", \n" +
                "        \"isFolder\":null\n" +
                "    }, \n" +
                "    {\n" +
                "        \"rowstyle\":\"background-color:#DDDD00;\", \n" +
                "        \"PRED_NAME\":\"ПЧ-2\", \n" +
                "        \"VID_ID\":28, \n" +
                "        \"PRICH_NAME\":\"СМЕНА РЕЛЬСА\", \n" +
                "        \"TIM_OTM\":\"2015-04-01T19:20:00.000\", \n" +
                "        \"id\":\"61;3924846##44\", \n" +
                "        \"PLACE\":\"АХТУБА<br> , -1км -1пк - -1км\", \n" +
                "        \"TIM_BEG\":\"2015-04-01T18:30:00.000\", \n" +
                "        \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"25/25/-/-/-\", \n" +
                "        \"VPAS\":25, \n" +
                "        \"DOR_NAME\":\"Приволжская\", \n" +
                "        \"TLG\":\"58<br>01.04.2015 18:31\", \n" +
                "        \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "        \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=61&pid=3924846&pids=3924846\", \n" +
                "        \"rowcolor\":\"YELLOW\", \n" +
                "        \"LEN\":\"1.0\", \n" +
                "        \"colorStatus\":1, \n" +
                "        \"PRED_ID\":834, \n" +
                "        \"EVTYPE\":\"WARNINGS\", \n" +
                "        \"DOR_KOD\":61, \n" +
                "        \"actual\":1, \n" +
                "        \"VID_NAME\":\"П\", \n" +
                "        \"VGR\":25, \n" +
                "        \"linkText\":\"Карточка\", \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_9\", \n" +
                "        \"isFolder\":null\n" +
                "    }, \n" +
                "    {\n" +
                "        \"groupName\":\"DOR_NAME\", \n" +
                "        \"groupValue\":\"Северная\", \n" +
                "        \"$52e\":true, \n" +
                "        \"canDrag\":false, \n" +
                "        \"DOR_NAME\":\"Северная\", \n" +
                "        \"_baseStyle\":null, \n" +
                "        \"customStyle\":\"groupNode\", \n" +
                "        \"_canEdit\":false, \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_10\", \n" +
                "        \"isFolder\":true, \n" +
                "        \"groupMembers\":[\n" +
                "            {\n" +
                "                \"rowstyle\":\"background-color:#DDDD00;\", \n" +
                "                \"PRED_NAME\":\"ПЧ-10\", \n" +
                "                \"VID_ID\":28, \n" +
                "                \"PRICH_NAME\":\"БОКОВОЙ ИЗНОС\", \n" +
                "                \"VEL\":25, \n" +
                "                \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "                \"id\":\"28;4380066##44\", \n" +
                "                \"PLACE\":\"БУЙ<br>путь 10-12 , 450км 6пк - 450км\", \n" +
                "                \"TIM_BEG\":\"2015-04-01T17:25:00.000\", \n" +
                "                \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"25/25/25/-/-\", \n" +
                "                \"VPAS\":25, \n" +
                "                \"DOR_NAME\":\"Северная\", \n" +
                "                \"TLG\":\"1505<br>01.04.2015 17:24\", \n" +
                "                \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "                \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=28&pid=4380066&pids=4380066\", \n" +
                "                \"rowcolor\":\"YELLOW\", \n" +
                "                \"LEN\":\"0.3\", \n" +
                "                \"colorStatus\":1, \n" +
                "                \"PRED_ID\":1916, \n" +
                "                \"EVTYPE\":\"WARNINGS\", \n" +
                "                \"DOR_KOD\":28, \n" +
                "                \"actual\":1, \n" +
                "                \"VID_NAME\":\"П\", \n" +
                "                \"VGR\":25, \n" +
                "                \"linkText\":\"Карточка\", \n" +
                "                \"groupParentId\":null, \n" +
                "                \"name\":\"0_11\", \n" +
                "                \"isFolder\":null\n" +
                "            }, \n" +
                "            {\n" +
                "                \"rowstyle\":\"background-color:#99DD00;\", \n" +
                "                \"PRED_NAME\":\"ПЧ-7\", \n" +
                "                \"VID_ID\":28, \n" +
                "                \"PRICH_NAME\":\"ДЕФЕКТНОСТЬ ШПАЛ\", \n" +
                "                \"VEL\":40, \n" +
                "                \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "                \"id\":\"28;4380080##44\", \n" +
                "                \"PLACE\":\"ФУРМАНОВ - ВОЛГОРЕЧЕНСК<br>путь 1 , 20км 2пк - 20км\", \n" +
                "                \"TIM_BEG\":\"2015-04-01T18:43:28.000\", \n" +
                "                \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"40/40/40/-/-\", \n" +
                "                \"VPAS\":40, \n" +
                "                \"DOR_NAME\":\"Северная\", \n" +
                "                \"TLG\":\"301<br>01.04.2015 18:40\", \n" +
                "                \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "                \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=28&pid=4380080&pids=4380080\", \n" +
                "                \"rowcolor\":\"GREEN\", \n" +
                "                \"LEN\":\"0.1\", \n" +
                "                \"colorStatus\":2, \n" +
                "                \"PRED_ID\":1416, \n" +
                "                \"EVTYPE\":\"WARNINGS\", \n" +
                "                \"DOR_KOD\":28, \n" +
                "                \"actual\":1, \n" +
                "                \"VID_NAME\":\"П\", \n" +
                "                \"VGR\":40, \n" +
                "                \"linkText\":\"Карточка\", \n" +
                "                \"groupParentId\":null, \n" +
                "                \"name\":\"0_12\", \n" +
                "                \"isFolder\":null\n" +
                "            }, \n" +
                "            {\n" +
                "                \"rowstyle\":\"background-color:#99DD00;\", \n" +
                "                \"PRED_NAME\":\"ПЧ-7\", \n" +
                "                \"VID_ID\":28, \n" +
                "                \"PRICH_NAME\":\"ДЕФЕКТНОСТЬ ШПАЛ\", \n" +
                "                \"VEL\":40, \n" +
                "                \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "                \"id\":\"28;4380082##44\", \n" +
                "                \"PLACE\":\"ФУРМАНОВ - ВОЛГОРЕЧЕНСК<br>путь 1 , 22км 7пк - 22км\", \n" +
                "                \"TIM_BEG\":\"2015-04-01T18:43:56.000\", \n" +
                "                \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"40/40/40/-/-\", \n" +
                "                \"VPAS\":40, \n" +
                "                \"DOR_NAME\":\"Северная\", \n" +
                "                \"TLG\":\"311<br>01.04.2015 18:43\", \n" +
                "                \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "                \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=28&pid=4380082&pids=4380082\", \n" +
                "                \"rowcolor\":\"GREEN\", \n" +
                "                \"LEN\":\"0.1\", \n" +
                "                \"colorStatus\":2, \n" +
                "                \"PRED_ID\":1416, \n" +
                "                \"EVTYPE\":\"WARNINGS\", \n" +
                "                \"DOR_KOD\":28, \n" +
                "                \"actual\":1, \n" +
                "                \"VID_NAME\":\"П\", \n" +
                "                \"VGR\":40, \n" +
                "                \"linkText\":\"Карточка\", \n" +
                "                \"groupParentId\":null, \n" +
                "                \"name\":\"0_13\", \n" +
                "                \"isFolder\":null\n" +
                "            }, \n" +
                "            {\n" +
                "                \"rowstyle\":\"background-color:#99DD00;\", \n" +
                "                \"PRED_NAME\":\"ПЧ-7\", \n" +
                "                \"VID_ID\":28, \n" +
                "                \"PRICH_NAME\":\"ДЕФЕКТНОСТЬ ШПАЛ\", \n" +
                "                \"VEL\":40, \n" +
                "                \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "                \"id\":\"28;4380083##44\", \n" +
                "                \"PLACE\":\"ФУРМАНОВ - ВОЛГОРЕЧЕНСК<br>путь 1 , 22км 10пк - 22км\", \n" +
                "                \"TIM_BEG\":\"2015-04-01T18:44:27.000\", \n" +
                "                \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"40/40/40/-/-\", \n" +
                "                \"VPAS\":40, \n" +
                "                \"DOR_NAME\":\"Северная\", \n" +
                "                \"TLG\":\"312<br>01.04.2015 18:43\", \n" +
                "                \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "                \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=28&pid=4380083&pids=4380083\", \n" +
                "                \"rowcolor\":\"GREEN\", \n" +
                "                \"LEN\":\"0.1\", \n" +
                "                \"colorStatus\":2, \n" +
                "                \"PRED_ID\":1416, \n" +
                "                \"EVTYPE\":\"WARNINGS\", \n" +
                "                \"DOR_KOD\":28, \n" +
                "                \"actual\":1, \n" +
                "                \"VID_NAME\":\"П\", \n" +
                "                \"VGR\":40, \n" +
                "                \"linkText\":\"Карточка\", \n" +
                "                \"groupParentId\":null, \n" +
                "                \"name\":\"0_14\", \n" +
                "                \"isFolder\":null\n" +
                "            }, \n" +
                "            {\n" +
                "                \"rowstyle\":\"background-color:#99DD00;\", \n" +
                "                \"PRED_NAME\":\"ПЧ-7\", \n" +
                "                \"VID_ID\":28, \n" +
                "                \"PRICH_NAME\":\"ДЕФЕКТНОСТЬ ШПАЛ\", \n" +
                "                \"VEL\":40, \n" +
                "                \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "                \"id\":\"28;4380085##44\", \n" +
                "                \"PLACE\":\"ФУРМАНОВ - ВОЛГОРЕЧЕНСК<br>путь 1 , 23км 4пк - 23км\", \n" +
                "                \"TIM_BEG\":\"2015-04-01T18:46:23.000\", \n" +
                "                \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"40/40/40/-/-\", \n" +
                "                \"VPAS\":40, \n" +
                "                \"DOR_NAME\":\"Северная\", \n" +
                "                \"TLG\":\"321<br>01.04.2015 18:45\", \n" +
                "                \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "                \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=28&pid=4380085&pids=4380085\", \n" +
                "                \"rowcolor\":\"GREEN\", \n" +
                "                \"LEN\":\"0.1\", \n" +
                "                \"colorStatus\":2, \n" +
                "                \"PRED_ID\":1416, \n" +
                "                \"EVTYPE\":\"WARNINGS\", \n" +
                "                \"DOR_KOD\":28, \n" +
                "                \"actual\":1, \n" +
                "                \"VID_NAME\":\"П\", \n" +
                "                \"VGR\":40, \n" +
                "                \"linkText\":\"Карточка\", \n" +
                "                \"groupParentId\":null, \n" +
                "                \"name\":\"0_15\", \n" +
                "                \"isFolder\":null\n" +
                "            }\n" +
                "        ], \n" +
                "        \"singleCellValue\":\"Северная\"\n" +
                "    }, \n" +
                "    {\n" +
                "        \"rowstyle\":\"background-color:#DDDD00;\", \n" +
                "        \"PRED_NAME\":\"ПЧ-10\", \n" +
                "        \"VID_ID\":28, \n" +
                "        \"PRICH_NAME\":\"БОКОВОЙ ИЗНОС\", \n" +
                "        \"VEL\":25, \n" +
                "        \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "        \"id\":\"28;4380066##44\", \n" +
                "        \"PLACE\":\"БУЙ<br>путь 10-12 , 450км 6пк - 450км\", \n" +
                "        \"TIM_BEG\":\"2015-04-01T17:25:00.000\", \n" +
                "        \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"25/25/25/-/-\", \n" +
                "        \"VPAS\":25, \n" +
                "        \"DOR_NAME\":\"Северная\", \n" +
                "        \"TLG\":\"1505<br>01.04.2015 17:24\", \n" +
                "        \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "        \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=28&pid=4380066&pids=4380066\", \n" +
                "        \"rowcolor\":\"YELLOW\", \n" +
                "        \"LEN\":\"0.3\", \n" +
                "        \"colorStatus\":1, \n" +
                "        \"PRED_ID\":1916, \n" +
                "        \"EVTYPE\":\"WARNINGS\", \n" +
                "        \"DOR_KOD\":28, \n" +
                "        \"actual\":1, \n" +
                "        \"VID_NAME\":\"П\", \n" +
                "        \"VGR\":25, \n" +
                "        \"linkText\":\"Карточка\", \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_11\", \n" +
                "        \"isFolder\":null\n" +
                "    }, \n" +
                "    {\n" +
                "        \"rowstyle\":\"background-color:#99DD00;\", \n" +
                "        \"PRED_NAME\":\"ПЧ-7\", \n" +
                "        \"VID_ID\":28, \n" +
                "        \"PRICH_NAME\":\"ДЕФЕКТНОСТЬ ШПАЛ\", \n" +
                "        \"VEL\":40, \n" +
                "        \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "        \"id\":\"28;4380080##44\", \n" +
                "        \"PLACE\":\"ФУРМАНОВ - ВОЛГОРЕЧЕНСК<br>путь 1 , 20км 2пк - 20км\", \n" +
                "        \"TIM_BEG\":\"2015-04-01T18:43:28.000\", \n" +
                "        \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"40/40/40/-/-\", \n" +
                "        \"VPAS\":40, \n" +
                "        \"DOR_NAME\":\"Северная\", \n" +
                "        \"TLG\":\"301<br>01.04.2015 18:40\", \n" +
                "        \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "        \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=28&pid=4380080&pids=4380080\", \n" +
                "        \"rowcolor\":\"GREEN\", \n" +
                "        \"LEN\":\"0.1\", \n" +
                "        \"colorStatus\":2, \n" +
                "        \"PRED_ID\":1416, \n" +
                "        \"EVTYPE\":\"WARNINGS\", \n" +
                "        \"DOR_KOD\":28, \n" +
                "        \"actual\":1, \n" +
                "        \"VID_NAME\":\"П\", \n" +
                "        \"VGR\":40, \n" +
                "        \"linkText\":\"Карточка\", \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_12\", \n" +
                "        \"isFolder\":null\n" +
                "    }, \n" +
                "    {\n" +
                "        \"rowstyle\":\"background-color:#99DD00;\", \n" +
                "        \"PRED_NAME\":\"ПЧ-7\", \n" +
                "        \"VID_ID\":28, \n" +
                "        \"PRICH_NAME\":\"ДЕФЕКТНОСТЬ ШПАЛ\", \n" +
                "        \"VEL\":40, \n" +
                "        \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "        \"id\":\"28;4380082##44\", \n" +
                "        \"PLACE\":\"ФУРМАНОВ - ВОЛГОРЕЧЕНСК<br>путь 1 , 22км 7пк - 22км\", \n" +
                "        \"TIM_BEG\":\"2015-04-01T18:43:56.000\", \n" +
                "        \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"40/40/40/-/-\", \n" +
                "        \"VPAS\":40, \n" +
                "        \"DOR_NAME\":\"Северная\", \n" +
                "        \"TLG\":\"311<br>01.04.2015 18:43\", \n" +
                "        \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "        \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=28&pid=4380082&pids=4380082\", \n" +
                "        \"rowcolor\":\"GREEN\", \n" +
                "        \"LEN\":\"0.1\", \n" +
                "        \"colorStatus\":2, \n" +
                "        \"PRED_ID\":1416, \n" +
                "        \"EVTYPE\":\"WARNINGS\", \n" +
                "        \"DOR_KOD\":28, \n" +
                "        \"actual\":1, \n" +
                "        \"VID_NAME\":\"П\", \n" +
                "        \"VGR\":40, \n" +
                "        \"linkText\":\"Карточка\", \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_13\", \n" +
                "        \"isFolder\":null\n" +
                "    }, \n" +
                "    {\n" +
                "        \"rowstyle\":\"background-color:#99DD00;\", \n" +
                "        \"PRED_NAME\":\"ПЧ-7\", \n" +
                "        \"VID_ID\":28, \n" +
                "        \"PRICH_NAME\":\"ДЕФЕКТНОСТЬ ШПАЛ\", \n" +
                "        \"VEL\":40, \n" +
                "        \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "        \"id\":\"28;4380083##44\", \n" +
                "        \"PLACE\":\"ФУРМАНОВ - ВОЛГОРЕЧЕНСК<br>путь 1 , 22км 10пк - 22км\", \n" +
                "        \"TIM_BEG\":\"2015-04-01T18:44:27.000\", \n" +
                "        \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"40/40/40/-/-\", \n" +
                "        \"VPAS\":40, \n" +
                "        \"DOR_NAME\":\"Северная\", \n" +
                "        \"TLG\":\"312<br>01.04.2015 18:43\", \n" +
                "        \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "        \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=28&pid=4380083&pids=4380083\", \n" +
                "        \"rowcolor\":\"GREEN\", \n" +
                "        \"LEN\":\"0.1\", \n" +
                "        \"colorStatus\":2, \n" +
                "        \"PRED_ID\":1416, \n" +
                "        \"EVTYPE\":\"WARNINGS\", \n" +
                "        \"DOR_KOD\":28, \n" +
                "        \"actual\":1, \n" +
                "        \"VID_NAME\":\"П\", \n" +
                "        \"VGR\":40, \n" +
                "        \"linkText\":\"Карточка\", \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_14\", \n" +
                "        \"isFolder\":null\n" +
                "    }, \n" +
                "    {\n" +
                "        \"rowstyle\":\"background-color:#99DD00;\", \n" +
                "        \"PRED_NAME\":\"ПЧ-7\", \n" +
                "        \"VID_ID\":28, \n" +
                "        \"PRICH_NAME\":\"ДЕФЕКТНОСТЬ ШПАЛ\", \n" +
                "        \"VEL\":40, \n" +
                "        \"TIM_OTM\":\"9999-12-31T00:00:00.000\", \n" +
                "        \"id\":\"28;4380085##44\", \n" +
                "        \"PLACE\":\"ФУРМАНОВ - ВОЛГОРЕЧЕНСК<br>путь 1 , 23км 4пк - 23км\", \n" +
                "        \"TIM_BEG\":\"2015-04-01T18:46:23.000\", \n" +
                "        \"VPAS_VGR_VEL_VGRPOR_VSTR\":\"40/40/40/-/-\", \n" +
                "        \"VPAS\":40, \n" +
                "        \"DOR_NAME\":\"Северная\", \n" +
                "        \"TLG\":\"321<br>01.04.2015 18:45\", \n" +
                "        \"EVTYPE_NAME\":\"ПРЕДУПРЕЖДЕНИЯ\", \n" +
                "        \"CRDURL\":\"http://warn_host:warn_port/wXXXXXX/jsp_predupr.jsp?dor_kod=28&pid=4380085&pids=4380085\", \n" +
                "        \"rowcolor\":\"GREEN\", \n" +
                "        \"LEN\":\"0.1\", \n" +
                "        \"colorStatus\":2, \n" +
                "        \"PRED_ID\":1416, \n" +
                "        \"EVTYPE\":\"WARNINGS\", \n" +
                "        \"DOR_KOD\":28, \n" +
                "        \"actual\":1, \n" +
                "        \"VID_NAME\":\"П\", \n" +
                "        \"VGR\":40, \n" +
                "        \"linkText\":\"Карточка\", \n" +
                "        \"groupParentId\":null, \n" +
                "        \"name\":\"0_15\", \n" +
                "        \"isFolder\":null\n" +
                "    }\n" +
                "]";


        btsi.parserTest(s);

    }


    public void parserTest(String source) {
        Type mapType = new TypeToken<ExcelExporter.MyMap<String, String>[]>() {
        }.getType();
        Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(new TypeToken<ExcelExporter.MyMap<String, String>[]>() {
        }.getType(), new MyMapToStringDeserializer()).create();
        ExcelExporter.MyMap<String, String>[] res = gson.fromJson(source, mapType);
        res = postProcessMap(res);
        printRes(0, res);


    }

    private ExcelExporter.MyMap<String, String>[] postProcessMap(ExcelExporter.MyMap<String, String>[] res) {
        if (res == null) return null;

        List<ExcelExporter.MyMap> al = new ArrayList<ExcelExporter.MyMap>();
        for (int i = 0; i < res.length; i++) {
            if (res[i].isGroup()) al.add(res[i]);


        }

        return al.toArray(new ExcelExporter.MyMap[al.size()]);


    }


    public void printRes(int level, ExcelExporter.MyMap<String, String>[] map) {

        int j = level + 1;
        String s = "";
        while (j > 0) {
            s += "    ";
            j--;
        }

        for (int i = 0; i < map.length; i++) {

            System.out.print(s + (map[i]).toString());
            System.out.println("");
            if (map[i].getGroupMembers() != null) {

                printRes(level + 1, map[i].getGroupMembers());

            }

        }


    }


    public List<UserProfile> getProfileIdToLoadFirst(BigDecimal userId, int type) {

        List<UserProfile> foundProfileIds = new ArrayList<UserProfile>();
        try {
            List<UserProfile> userProfiles = testUserProfiles.getProfilesByUser_new(userId, true);

            UserProfile firstProfileId = null;
            UserProfile defaultProfile = null;
            UserProfile lastLoaded = null;
            Gson gson = new GsonBuilder().serializeNulls().create();
            Type mapType = new TypeToken<DescOperation<Object>>() {
            }.getType();
            Date d = new Date(0);
            for (int i = 0; userProfiles != null && i < userProfiles.size(); i++) {

                final DescOperation descOperation = gson.fromJson(userProfiles.get(i).getProfileContents(), mapType);
                if (i == 0) firstProfileId = userProfiles.get(i);
                if (userProfiles.get(i).getProfileName().equalsIgnoreCase("default"))
                    defaultProfile = userProfiles.get(i);
                switch (type) {
                    case UserProfile.LAST_USED_PROFILE: {
                        if (descOperation.get("lastLoaded") != null) {
                            try {
                                Date dd = thsdf.get().parse((String) descOperation.get("lastLoaded"));
                                if (dd.getTime() > d.getTime()) {
                                    d = dd;
                                    setAdditionalParamsFromDescriptor(userProfiles.get(i), descOperation);
                                    lastLoaded = userProfiles.get(i);

                                }
                            } catch (ParseException e) {
                                //  e.printStackTrace();
                            }


                        }
                        break;
                    }
                    case UserProfile.DESKTOP_INFORMER_PROFILES: {
                        if (descOperation.get("desktopInformer") != null && (Boolean) descOperation.get("desktopInformer")) {
                            setAdditionalParamsFromDescriptor(userProfiles.get(i), descOperation);

                            foundProfileIds.add(userProfiles.get(i));

                        }

                        break;
                    }


                }

            }
            if (lastLoaded != null) {
                foundProfileIds.add(lastLoaded);

            }

            if (foundProfileIds.size() == 0) {
                if (defaultProfile != null) {
                    foundProfileIds.add(defaultProfile);
                } else {
                    foundProfileIds.add(firstProfileId);

                }


            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        return foundProfileIds;
    }

    @Override
    public String checkVersion(String inVersion) {
        return TablesTypes.VERSION;
    }

    @Override
    public Boolean shouldRestart() {
        if (restartEnabled == null || !restartEnabled) return false;
        Date d = new Date();
        int currentHour = d.getHours();
        int hoursCounter = firstRestartHourInteger;
        for (int i = 0; i < 24; i++) {
            if (currentHour == hoursCounter) return true;
            else {
                hoursCounter += restartHoursPeriodInteger;
                if (hoursCounter >= 24) hoursCounter -= 24;
            }
        }
        return false;
    }

    void setAdditionalParamsFromDescriptor(UserProfile up, DescOperation desc) {

        if (desc.get("desktopInformer") != null && (Boolean) desc.get("desktopInformer")) {
            up.setDesktopInformers(true);
        }

    }


}
