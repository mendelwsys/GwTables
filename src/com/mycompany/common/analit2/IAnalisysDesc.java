package com.mycompany.common.analit2;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 23.10.14
 * Time: 15:02
 * Интерфейс описателя сводных таблиц
 */
public interface IAnalisysDesc extends Serializable
{
    /**
     * @return Отдать иерерахию по группам по оси X
     */
    GrpDef[] getGrpXHierarchy();

    Map<String, ColDef> getTupleDef();

    NNode2[] getNodes();

    void setGrpXHierarchy(GrpDef[] a);

    void  setTupleDef(Map<String, ColDef> b);

    void  setNodes(NNode2[] c);
}
