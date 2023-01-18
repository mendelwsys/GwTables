package com.mycompany.client.test.informer;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.event.shared.HandlerRegistration;
import com.mycompany.client.CommonServerFilter;
import com.mycompany.client.GUIStateDesc;
import com.mycompany.client.IDataFlowCtrl;
import com.mycompany.client.IServerFilter;
import com.mycompany.client.apps.App.api.CreateInformerOperation;
import com.mycompany.common.DescOperation;
import com.mycompany.common.Pair;
import com.mycompany.common.TablesTypes;
import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criterion;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.*;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 11.03.15
 * Time: 11:59
 * Информер на основе DetailViewer
 */
abstract public class DetailViewerInfo implements Informer
{

    DescOperation descinformer = null;

    public DetailViewerInfo() {

    }


    public boolean isHighLevelWarning() {
        return highLevelWarning;
    }

    public void setHighLevelWarning(boolean highLevelWarning) {
        this.highLevelWarning = highLevelWarning;
        this.descinformer.put(CreateInformerOperation.HIGH_WARN_LEVEL, highLevelWarning);
    }

    private boolean highLevelWarning = false;

    abstract public Integer getDorKod();

    abstract public void setDorKod(Integer dorKod);


    public void startUpdateData()
    {
        setCtrlCriteria();
        ctrl.startUpdateData(true);  //Запустить после установки
    }

    protected void setCtrlCriteria()
    {
        IServerFilter serverFilter = new CommonServerFilter(TablesTypes.FILTERDATAEXPR);
        final AdvancedCriteria serverCriteria = new AdvancedCriteria();
        serverCriteria.setOperator(OperatorId.AND);
        serverCriteria.addCriteria(new Criterion(TablesTypes.DOR_CODE, OperatorId.EQUALS, getDorKod()));//TODO Дорога
        serverFilter.setCriteria(serverCriteria);
        serverFilter.set2Criteria(ctrl.getCriteria());
    }


    public IDataFlowCtrl getCtrl() {
        return ctrl;
    }

    public void setCtrl(IDataFlowCtrl ctrl) {
        this.ctrl = ctrl;
    }

    protected IDataFlowCtrl ctrl;


    public static String STYLE="STYLE";//При стиле ожидается (имя колонки, и собственно стиль)

    public Map<String, Pair<String, ListGridFieldType>> getMeta() {
        return meta;
    }

    private Map<String, Pair<String, ListGridFieldType>> meta;//Название -> заголовокm, тип данных

    Record rec=new Record();


    @Override
    public void viewValues(Object _values)
    {
        Map values =(Map)_values;
        Record old_rec = null;

        if (rec != null) {
            old_rec = new Record();
            Map m = rec.toMap();
            Set keys = m.keySet();
            Object[] skeys = keys.toArray(new Object[keys.size()]);

            for (int i = 0; i < skeys.length; i++) {
                old_rec.setAttribute((String) skeys[i], rec.getAttributeAsObject((String) skeys[i]));

            }
        }
        for (Object key : values.keySet())
        {
            if (STYLE.equals(key))
            {
                Map<String,String> col2style= (Map<String, String>) values.get(key);
                for (String fName : col2style.keySet())
                {
                    final Integer integer = name2ix.get(fName);
                    DetailViewerField fld = detailViewer.getFields()[integer];
                    fld.setCellStyle(col2style.get(fName));
                }
            }
            else
            {
                Pair<String, ListGridFieldType> pr = meta.get(key);
                if (pr!=null)
                {
                    if (pr.second.equals(ListGridFieldType.IMAGE))
                        rec.setAttribute((String)key,Canvas.imgHTML((String)values.get(key)));
                    else {
                        rec.setAttribute((String) key, values.get(key));
                        if (values.get(key + "_ORIG") != null)
                            rec.setAttribute((String) key + "_ORIG", values.get(key + "_ORIG"));
                    }
                }
            }
        }

        Record new_rec = new Record();
        new_rec.setJavaScriptObject(rec.getJsObj());

        if (!canvasDelete)
        {
            performAnalysisOnUpdate(new Pair(old_rec, new_rec));
            detailViewer.setData(new Record[]{rec});
            detailViewer.markForRedraw();
        }
    }



    public Canvas getInformer() {
        return detailViewer;
    }

    boolean canvasDelete=false;
    protected DetailViewerWithDesc detailViewer = new DetailViewerWithDesc()
    {
        public void destroy()
        {
            final IDataFlowCtrl ctrl = getCtrl();
            if (ctrl!=null)
                ctrl.stopUpdateData(); //TODO Послать команду очистки серверных ресурсов
            canvasDelete=true;
            super.destroy(); //TODO проверить что у нас прекарщается опрос сервера, и в дальнейшем необходимо явно очистить серверные ресурсы
        }
    };

    private Map<String,Integer> name2ix=new HashMap<String,Integer>();


    HandlerRegistration onResizeHandler = null;

    /**
     *
     * @param name2Title2Type - имя - заголовок вместе с типом
     * @param values-начальные значения
     */

    public DetailViewerInfo(Map<String, Pair<String, ListGridFieldType>> name2Title2Type, Map values, final DescOperation descInformer)
    {
        this.meta = name2Title2Type;
        this.descinformer = descInformer;
        if (descinformer.get(CreateInformerOperation.HIGH_WARN_LEVEL) != null)
            this.highLevelWarning = (Boolean) descinformer.get(CreateInformerOperation.HIGH_WARN_LEVEL);
        int ix=0;
        DetailViewerField[] fields= new DetailViewerField[name2Title2Type.size()];
        for (String key : name2Title2Type.keySet())
        {
            fields[ix] = new DetailViewerField(key, name2Title2Type.get(key).first);
            name2ix.put(key,ix);
            ix++;
        }
        detailViewer.setFields(fields);

        if (!(descInformer.get(CreateInformerOperation.APP_MODE) != null && ((String) descInformer.get(CreateInformerOperation.APP_MODE)).equalsIgnoreCase(GUIStateDesc.DESKTOP))) {
            final HLayout hLayout = createCtrlButtons();

            detailViewer.setCanDragReposition(true);
            detailViewer.setDragAppearance(DragAppearance.TARGET);


            detailViewer.setKeepInParentRect(true);
            detailViewer.addMouseOverHandler(new MouseOverHandler() {
                @Override
                public void onMouseOver(MouseOverEvent event) {
                    hLayout.setVisible(true);
                }
            });

            detailViewer.addMouseOutHandler(
                    new MouseOutHandler() {
                        @Override
                        public void onMouseOut(MouseOutEvent event) {
                            int x = event.getX();
                            int y = event.getY();

                            Canvas[] cvs = hLayout.getMembers();

                            for (Canvas cv : cvs) {
                                final int w = cv.getWidth();
                                final int h = cv.getHeight();
                                final int absoluteLeft = cv.getAbsoluteLeft();
                                final int absoluteTop = cv.getAbsoluteTop();
                                if (
                                        absoluteLeft <= x && x <= absoluteLeft + w &&
                                                absoluteTop <= y && y <= absoluteTop + h
                                        )
                                    return;
                            }
                            hLayout.setVisible(false);
                        }
                    });

            hLayout.setVisible(false);
            detailViewer.addChild(hLayout);
            //Установка значений начальных значений
        }
        detailViewer.setDescInformer(descInformer);
        if (descInformer.get(CreateInformerOperation.APP_MODE) != null && ((String) descInformer.get(CreateInformerOperation.APP_MODE)).equalsIgnoreCase(GUIStateDesc.DESKTOP))
            onResizeHandler = detailViewer.addResizedHandler(new ResizedHandler() {
                @Override
                public void onResized(ResizedEvent event) {
                    detailViewer.setIntwidth(detailViewer.getScrollWidth());
                    detailViewer.setIntheight(detailViewer.getScrollHeight());
                    resizeParentWindow("" + ((Long) descInformer.get(CreateInformerOperation.CREATED_INFORMER_ID)).longValue(), detailViewer.getScrollHeight(), detailViewer.getScrollWidth());

                }

                native void resizeParentWindow(String id, int h, int w) throws JavaScriptException/*-{
                    //$wnd.alert($wnd.parent);


                    if ($wnd.parentframe) {
                        //  $wnd.alert('inside');
                        $wnd.parentframe.parent.rWW(id, h, w);
                        //  $wnd.alert('after');
                    }

                }-*/;
            });





        viewValues(values);
    }

    private HLayout createCtrlButtons()
    {

        final HLayout hLayout = new HLayout();
        hLayout.setAlign(Alignment.CENTER);
        hLayout.setMargin(0);
        hLayout.setShowEdges(false);
        hLayout.setAutoWidth();
        hLayout.setAutoHeight();

        {
            final Img img = new Img("info/settings.png");
            img.setWidth(16);
            img.setHeight(16);
            img.setPrompt("Настроить");
            img.setLayoutAlign(Alignment.CENTER);

            img.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event)
                {
                    Window dlg = SetRoadDlg.createViewOptionsDlg(DetailViewerInfo.this);
                    dlg.setLeft(getInformer().getLeft());
                    dlg.setTop(getInformer().getTop());
                    dlg.show();

                    event.cancel();
                }
            });
            hLayout.addMember(img);
        }

        {
            final Img img = new Img("info/zoom.png");
            img.setWidth(16);
            img.setHeight(16);
            img.setPrompt("Просмотр");
            img.setLayoutAlign(Alignment.CENTER);

            img.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event)
                {
                    event.cancel();
                }
            });
            hLayout.addMember(img);
        }

        {
            final Img img = new Img("info/minimize.png");
            img.setWidth(16);
            img.setHeight(16);
            img.setLayoutAlign(Alignment.CENTER);
            img.setPrompt("Свернуть");

            img.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event)
                {
                    //String say=GUIStateDesc.getAppName()+ " АГ:"+GUIStateDesc.getUserAgent()+" ??:"+SC.isIE();
//                    JavaScriptObject js = GUIStateDesc.getUserAgent2();
//                    String say="8:"+ JSOHelper.getAttribute(js, "isIE8")+" 9:"+JSOHelper.getAttribute(js,"isIE9")+" 10:"+JSOHelper.getAttribute(js,"isIE10")+" 11:"+JSOHelper.getAttribute(js,"isIE11");
//                    String[] strs=JSOHelper.getProperties(js);
//                    SC.say(say);
                    event.cancel();
                }
            });
            hLayout.addMember(img);
        }

        {
            final Img img = new Img("info/close.png");
            img.setWidth(16);
            img.setHeight(16);
            img.setLayoutAlign(Alignment.CENTER);
            img.setPrompt("Закрыть");

            img.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event)
                {
                    closeInformer(hLayout);
                    event.cancel();
                }
            });
            hLayout.addMember(img);
        }


        hLayout.setCanDrag(true);
        hLayout.setCanDragReposition(true);
        hLayout.setKeepInParentRect(true);

        hLayout.setLeft(48);
        hLayout.setTop(43);
        return hLayout;
    }

    private void closeInformer(HLayout hLayout)
    {
//        detailViewer.removeChild(hLayout);
        if (onResizeHandler != null)
            onResizeHandler.removeHandler();
        detailViewer.getParentCanvas().removeChild(detailViewer);
        detailViewer.markForDestroy();
    }


}
