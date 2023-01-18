package com.mycompany.client.test.informer;

import com.mycompany.common.Pair;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.widgets.Canvas;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 09.03.15
 * Time: 18:52
 *  Интерфейс информера.
 */
public interface Informer
{
    void viewValues(Object values);
    Canvas getInformer();

    void performAnalysisOnUpdate(Pair<Record, Record> data);

}
