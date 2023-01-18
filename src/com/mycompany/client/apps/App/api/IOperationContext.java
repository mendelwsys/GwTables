package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.ICopyObject;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 10.02.15
 * Time: 18:55
 *
 */
public interface IOperationContext extends ICopyObject
{
   Object getSrc();
   Object getDst();
   Map getParams();
   List<IOperationContext> getChildList();
   //Object[] getParams();

//    void setSrc(Object src);
//    void setDst(Object dst);
}
