package com.mycompany.client.apps.App;

import com.mycompany.client.DesktopGUIStateDesc;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.widgets.layout.HLayout;

/**
 * Created by Anton.Pozdnev on 26.05.2015.
 */
public class AppDesktopInformers extends App01 {

    @Override
    protected HLayout initDefaultDescTop() {
        HLayout mainLayout = new HLayout();
        new_load_ix++;

        AppConst.t_MY_ROOT_PANEL = transformCompId(AppConst.t_MY_ROOT_PANEL);

        mainLayout.setID(AppConst.t_MY_ROOT_PANEL);
        mainLayout.setShowEdges(false);
        mainLayout.setHeight100();
        mainLayout.setWidth100();
        mainLayout.setDragAppearance(DragAppearance.TARGET);


        GUI_STATE_DESC.setMainLayout(mainLayout);

        return mainLayout;
    }

    @Override
    public void reRunApp() {
        if (GUI_STATE_DESC == null)
            GUI_STATE_DESC = new DesktopGUIStateDesc();
        if (mainLayout != null)
            mainLayout.markForDestroy();
        mainLayout = initDefaultDescTop();
        mainLayout.draw();
        GUI_STATE_DESC.loadDescTop(this);
        mainLayout.markForRedraw();
    }

    @Override
    public void unLoad() {
        mainLayout.hide();
        mainLayout.markForDestroy();
        GUI_STATE_DESC = new DesktopGUIStateDesc(GUI_STATE_DESC.getCurrentUserId(), GUI_STATE_DESC.getCurrentProfile(), GUI_STATE_DESC.getUser());
    }
}
