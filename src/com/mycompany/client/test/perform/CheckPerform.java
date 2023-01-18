package com.mycompany.client.test.perform;

import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.client.test.TestBuilder;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.PortalLayout;
import com.smartgwt.client.widgets.layout.Portlet;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.06.15
 * Time: 19:00
 * Тестирование получения данных с сервера через стандартный провайдер (тестируется консистентонсть данных)
 */
public class CheckPerform
        implements TestBuilder,Runnable
{



    @Override
    public void run() {

        final HLayout mainLayout = new HLayout();
        mainLayout.setID(AppConst.t_MY_ROOT_PANEL);
        mainLayout.setShowEdges(false);
        mainLayout.setHeight100();
        mainLayout.setWidth100();
        mainLayout.setDragAppearance(DragAppearance.TARGET);

        this.setComponents(mainLayout);
        mainLayout.draw();
    }


    TextAreaItem textAreaItem;

    @Override
    public void setComponents(Layout mainLayout)
    {


        final DynamicForm form = OptionsViewers.createEmptyForm();

        form.setLayoutAlign(VerticalAlignment.TOP);
        textAreaItem= new TextAreaItem();
        textAreaItem.setWidth("100%");
        textAreaItem.setTitle("");
        form.setFields(textAreaItem);

        Portlet portlet0 = new Portlet();
        portlet0.addItem(form);
        portlet0.setHeight("20%");
        portlet0.setTitle("Статус теста");

        final PortalLayout portalLayout = new PortalLayout(0);
        portalLayout.setWidth100();
        portalLayout.setHeight100();
        portalLayout.setShowColumnMenus(false);

        portalLayout.addPortlet(portlet0);


        {


            new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation() {

                @Override
                public boolean operate()
                {


                    String[] ll = new String[100000];
                    String  array="qwertyuiopasdfghjkl;zxcvbnm,.;'[]";
                    Set<String> hs=new HashSet<String>();
                    for (int i=0;i<ll.length;i++)
                    {


                        String e = String.valueOf(i) + "_" + array.charAt(i % array.length());
                        for (int j=0;j<10;j++)
                        {
                            int ix= (int)(Math.random()*(array.length()-1));
                            e+="_"+array.charAt(ix);
                        }


                        ll[i]=e;
                        hs.add(e);
                    }



                    long ln=System.currentTimeMillis();

                    int cnt=0;
                    for (int i=0;i<100000;i++)
                    {
                        int ix= (int) Math.round(Math.random()*ll.length);
                        if (hs.contains(ll[ix]))
                            cnt++;
                    }
                    ln=System.currentTimeMillis()-ln;
                    textAreaItem.setValue("Check  status:  data groups " + ln+" "+cnt);
                    return false;
                }
            },1500);


        }
        mainLayout.addMembers(portalLayout);
    }

}
