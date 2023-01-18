package com.mycompany.common.analit;

import com.mycompany.common.Pair;
import com.smartgwt.client.types.ListGridFieldType;

import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 21.10.14
 * Time: 12:31
 * Аналитический описатель таблицы
 */
public interface IAnalisysDesc
{
    /**
     * @return получить описание измерений таблицы (название столбца -> пара(тип, возможные значения))
     */
    Map<String,Pair<ListGridFieldType,DomainDim[]>> getDimDef();

    /**
     * @return отдать показатели которые надо демонстрировать под определнными заголовками
     */
    Pair<Set<String>,FigDef[]>[] getFigMapping();

    /**
     * @return Отдать иерерахию по группам по оси Y
     */
    GrpDef[] getGrpYHierarchy();

    /**
     * @return Отдать иерерахию по группам по оси X
     */
    GrpDef[] getGrpXHierarchy();

    Map<String, ColDef> getTupleDef();
}
