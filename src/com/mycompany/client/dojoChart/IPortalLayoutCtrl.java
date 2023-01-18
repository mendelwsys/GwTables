package com.mycompany.client.dojoChart;

import com.smartgwt.client.widgets.layout.PortalLayout;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 09.02.15
 * Time: 17:48
 * Необходимо для того что бы управлять добавлением и удаление портлетов в портал
 */
public interface IPortalLayoutCtrl
{
    void setPortletLayOut(PortalLayout layout,Integer colNum,Integer rowNum);
}
