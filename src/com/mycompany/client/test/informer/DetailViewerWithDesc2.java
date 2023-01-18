package com.mycompany.client.test.informer;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.mycompany.client.apps.App.api.CreateInformerOperation;
import com.mycompany.client.apps.App.api.NewWarnInformer;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 18.03.15
 * Time: 19:52
 */
public class DetailViewerWithDesc2 extends HTMLPane implements IInformerCli {
    private static Map<String, DetailViewerWithDesc2> informers;

    static {
        informers = new HashMap<String, DetailViewerWithDesc2>();


    }

    public int getIntheight() {
        return intheight;
    }

    public void setIntheight(int intheight) {
        this.intheight = intheight;
    }

    public int getIntwidth() {
        return intwidth;
    }

    public void setIntwidth(int intwidth) {
        this.intwidth = intwidth;
    }

    int intheight = 110;
    int intwidth = 132;

    static boolean commonFunctionsBouund = false;


    public String getEventFieldName() {
        return eventFieldName;
    }

    public Record[] getCurrentRecord() {
        return currentRecord;
    }

    public String[] getExcludeFieldNames() {
        return excludeFieldNames;
    }

    public DetailViewerField[] getFields() {
        return fields;
    }

    public String getRoadFieldName() {
        return roadFieldName;
    }

    private String eventFieldName;
    private String roadFieldName;
    private DetailViewerField[] fields;
    private String[] excludeFieldNames;
    private Record[] currentRecord;
    private String informer_id = null;


    public DescOperation getDescInformer() {
        descInformer.put(NewWarnInformer.CRD_LEFT, this.getLeft());
        descInformer.put(NewWarnInformer.CRD_TOP, this.getTop());

        return descInformer;
    }

    public String getInformerId() {

        if (informer_id == null) {
            informer_id = "inf" + ((Long) descInformer.get(CreateInformerOperation.CREATED_INFORMER_ID)).longValue();
            registerInformer(this);
        }
        return informer_id;

    }

    private synchronized void registerInformer(DetailViewerWithDesc2 detailViewerWithDesc2) {

        if (informers.get(detailViewerWithDesc2.getInformerId()) == null) {

            informers.put(detailViewerWithDesc2.getInformerId(), detailViewerWithDesc2);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        informers.remove(this.getInformerId());
        removeLoadedListeners();
    }

    public void setDescInformer(DescOperation descInformer) {
        this.descInformer = descInformer;
    }

    private DescOperation descInformer;

    public DetailViewerWithDesc2() {
        synchronized (this) {
            if (!commonFunctionsBouund) {
                initInternal();
                commonFunctionsBouund = true;
            }
        }

    }

    public DetailViewerWithDesc2(JavaScriptObject jsObj) {
        super(jsObj);
        synchronized (this) {
            if (!commonFunctionsBouund) {
                initInternal();
                commonFunctionsBouund = true;
            }
        }
        final DetailViewerWithDesc2 obj = this;


    }



    public void setData(Record[] rec) {
        Arrays.sort(excludeFieldNames);
        currentRecord = rec;
        Map m = currentRecord[0].toMap();
        List<DetailViewerField> params = new ArrayList<DetailViewerField>();
        try {
            Map<String, String> styles = (Map) m.get(DetailViewerInfo.STYLE);
            setOverAllStatus(getInformerId(), ((styles != null && styles.get("OVERALL") != null) ? Integer.parseInt(styles.get("OVERALL")) : 1));
            int fieldsLength = fields.length;

            for (int i = 0; i < fieldsLength; i++) {
                String fieldName = fields[i].getName();

                if (fieldName.equalsIgnoreCase(getRoadFieldName())) {
                    setRoadTitle(getInformerId(), (String) m.get(fieldName), ((String) m.get(fieldName)).substring(0, 4).toUpperCase());
                    continue;

                } else if (fieldName.equalsIgnoreCase(getEventFieldName()))

                {
                    setEventTitle(getInformerId(), (String) m.get(fieldName), ((String) m.get(fieldName)).substring(0, 4).toUpperCase());
                    continue;

                } else if (isInExcluded(fieldName)) continue;

                params.add(0, fields[i]);

            }
            setParamsNum(getInformerId(), (int) params.size());
            int paramsSize = params != null ? params.size() : 0;
            for (int j = 0; j < paramsSize; j++) {
                DetailViewerField field = params.get(j);
                String fieldName = field.getName();

                //TODO Научиться определять статусы
                setParamValueAndStatus(getInformerId(), j + 1, field.getTitle() + ":" + m.get(fieldName), Integer.parseInt(styles.get(fieldName)));

            }
        } catch (Exception e) {
            e.printStackTrace();

        }


    }


    private boolean isInExcluded(String fieldName) {

        if (Arrays.binarySearch(excludeFieldNames, fieldName) > -1) return true;
        int excludedFieldsLength = excludeFieldNames.length;
        for (int i = 0; i < excludedFieldsLength; i++) {
            if (excludeFieldNames[i].startsWith("*")) {
                if (fieldName.endsWith(excludeFieldNames[i].substring(1))) return true;


            }

        }

        return false;


    }

    @Override
    public void setRoadFieldName(String road) {
        this.roadFieldName = road;
    }

    @Override
    public void setEventFieldName(String event) {
        this.eventFieldName = event;
    }

    @Override
    public void setExcludedFieldsArray(String[] excluded) {
        this.excludeFieldNames = excluded;
    }

    @Override
    public void setFields(DetailViewerField[] fields) {

        this.fields = fields;
    }


    private native void initInternal() throws JavaScriptException/*-{

        // $wnd.parentframe;


        $wnd.onExpand2 = $entry(function (infid, width, height) {
                @com.mycompany.client.test.informer.DetailViewerWithDesc2::onExpand(Ljava/lang/String;II)(infid, width, height)
            }
        );
        $wnd.onCollapse2 = $entry(function (infid, width, height) {
                @com.mycompany.client.test.informer.DetailViewerWithDesc2::onCollapse(Ljava/lang/String;II)(infid, width, height)
            }
        );
        $wnd.onLoad = $entry(function (infid) {
                @com.mycompany.client.test.informer.DetailViewerWithDesc2::onLoaded(Ljava/lang/String;)(infid)
            }
        );
    }-*/;

    public static void onExpand(String id, int width, int height) {
        DetailViewerWithDesc2 d = informers.get(id);
        if (d != null) {
            d.setIntheight(height);
            d.setIntwidth(width);
            d.setHeight(height);
            d.setWidth(width);
        }
    }

    public static void onCollapse(String id, int width, int height) {
        DetailViewerWithDesc2 d = informers.get(id);
        if (d != null) {
            d.setIntheight(height);
            d.setIntwidth(width);
            d.setHeight(height);
            d.setWidth(width);
        }
    }

    @Override
    public native void setParamsNum(String id, int paramsNum) /*-{
        var r = $wnd.document.getElementById(id);
        //  console.log(r);
        if (r.contentWindow)
            r.contentWindow.setParamsNum(paramsNum);
        else if (r.contentDocument)
            r.contentDocument.setParamsNum(paramsNum);
        else
            $wnd.setParamsNum(paramsNum);

    }-*/;

    @Override
    public native void setEventTitle(String id, String longEventName, String shortEventName) /*-{
        //  console.log(id);
        var r = $wnd.document.getElementById(id);
        //   console.log(r);

        if (r.contentWindow) {
            //   console.log("1");
            r.contentWindow.setEventTitle(longEventName, shortEventName);
        }
        else if (r.contentDocument) {
            // console.log("2");
            r.contentDocument.setEventTitle(longEventName, shortEventName);
        }
        else {
            // console.log("3");
            r.$doc.setEventTitle(longEventName, shortEventName);
        }
    }-*/;

    @Override
    public native void setRoadTitle(String id, String longRoadTitle, String shortRoadTitle) /*-{
        var r = $wnd.document.getElementById(id);
        // console.log(r);

        if (r.contentWindow)
            r.contentWindow.setRoadTitle(longRoadTitle, shortRoadTitle);
        else if (r.contentDocument)
            r.contentDocument.setRoadTitle(longRoadTitle, shortRoadTitle);
        else
            $wnd.setRoadTitle(longRoadTitle, shortRoadTitle);
        // $wnd.document.getElementById(id).contentWindow.setRoadTitle(longRoadTitle,shortRoadTitle);

    }-*/;

    @Override
    public native void setOverAllStatus(String id, int status) /*-{
        var r = $wnd.document.getElementById(id);
        //  console.log(r);

        if (r.contentWindow)r.contentWindow.setOverAllStatus(status);
        else if (r.contentDocument)
            r.contentDocument.setOverAllStatus(status);
        else
            $wnd.setOverAllStatus(status);
        // $wnd.document.getElementById(id).contentWindow.setOverAllStatus(status);
    }-*/;

    @Override
    public native void setParamValueAndStatus(String id, int paramNum, String paramValue, int status) /*-{
        var r = $wnd.document.getElementById(id);
        //  console.log(r);

        if (r.contentWindow)
            r.contentWindow.setParamValueAndStatus(paramNum, paramValue, status);
        else if (r.contentDocument)
            r.contentDocument.setParamValueAndStatus(paramNum, paramValue, status);
        else
            $wnd.setParamValueAndStatus(paramNum, paramValue, status);
        //$wnd.document.getElementById(id).contentWindow.setParamVaueAndStatus(paramNum,paramValue,status);
    }-*/;

    @Override
    public native int getInitialHeight(String id) /*-{
        var r = $wnd.document.getElementById(id);
        //  console.log(r);

        if (r.contentWindow)
            return r.contentWindow.getInitialHeight();
        else if (r.contentDocument)
            return r.contentDocument.getInitialHeight();
        else
            return $wnd.getInitialHeight();
        //$wnd.document.getElementById(id).contentWindow.setParamVaueAndStatus(paramNum,paramValue,status);
    }-*/;

    @Override
    public native int getInitialWidth(String id) /*-{
        var r = $wnd.document.getElementById(id);
        //  console.log(r);

        if (r.contentWindow)
            return r.contentWindow.getInitialWidth();
        else if (r.contentDocument)
            return r.contentDocument.getInitialWidth();
        else
            return $wnd.getInitialWidth();
        //$wnd.document.getElementById(id).contentWindow.setParamVaueAndStatus(paramNum,paramValue,status);
    }-*/;

    List<LoadedListener> loadedListeners = new ArrayList<LoadedListener>();

    public void addLoadedListener(LoadedListener l) {

        loadedListeners.add(l);
    }

    public void removeLoadedListeners() {
        loadedListeners.clear();

    }


    public static void onLoaded(String id) {
        DetailViewerWithDesc2 d = informers.get(id);
        if (d != null)
            for (int i = 0; i < d.loadedListeners.size(); i++) {
                d.loadedListeners.get(i).onLoad();

            }
    }
}
