package com.mycompany.client;

import com.mycompany.client.apps.App.reps.IReportCreator;
import com.mycompany.common.analit2.IAnalisysDesc;

/**
 * Created by Anton.Pozdnev on 20.08.2015.
 */
public class ReportListGridWithDesc extends ListGridWithDesc {
    public IAnalisysDesc getiAnalisysDesc() {
        return iAnalisysDesc;
    }

    public void setiAnalisysDesc(IAnalisysDesc iAnalisysDesc) {
        this.iAnalisysDesc = iAnalisysDesc;
    }

    IAnalisysDesc iAnalisysDesc;

    public IReportCreator getCreator() {
        return creator;
    }

    public void setCreator(IReportCreator creator) {
        this.creator = creator;
    }

    IReportCreator creator;
}
