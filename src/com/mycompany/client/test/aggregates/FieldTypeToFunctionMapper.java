package com.mycompany.client.test.aggregates;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SummaryFunctionType;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.SummaryFunction;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Created by Anton.Pozdnev on 06.05.2015.
 */
public class FieldTypeToFunctionMapper {
    static Map<ListGridFieldType, LinkedHashSet<SummaryFunctionType>> type2function = new HashMap<ListGridFieldType, LinkedHashSet<SummaryFunctionType>>();
    static Map<ListGridFieldType, Map<SummaryFunctionType, SummaryFunction>> type2customFunction = new HashMap<ListGridFieldType, Map<SummaryFunctionType, SummaryFunction>>();

    static {
        // Поле число целое
        LinkedHashSet<SummaryFunctionType> t1 = new LinkedHashSet<SummaryFunctionType>();
        t1.add(SummaryFunctionType.COUNT);
        t1.add(SummaryFunctionType.SUM);
        t1.add(SummaryFunctionType.MIN);
        t1.add(SummaryFunctionType.MAX);
        t1.add(SummaryFunctionType.AVG);
        t1.add(SummaryFunctionType.MULTIPLIER);
        type2function.put(ListGridFieldType.INTEGER, t1);
        // Поле число вещественное
        LinkedHashSet<SummaryFunctionType> t10 = new LinkedHashSet<SummaryFunctionType>();
        t10.add(SummaryFunctionType.COUNT);
        t10.add(SummaryFunctionType.SUM);
        t10.add(SummaryFunctionType.MIN);
        t10.add(SummaryFunctionType.MAX);
        t10.add(SummaryFunctionType.AVG);
        t10.add(SummaryFunctionType.MULTIPLIER);
        type2function.put(ListGridFieldType.FLOAT, t10);
        // Поле строка
        LinkedHashSet<SummaryFunctionType> t2 = new LinkedHashSet<SummaryFunctionType>();
        t2.add(SummaryFunctionType.COUNT);
        type2function.put(ListGridFieldType.TEXT, t2);
        // Поле дата
        LinkedHashSet<SummaryFunctionType> t3 = new LinkedHashSet<SummaryFunctionType>();
        t3.add(SummaryFunctionType.COUNT);
        t3.add(SummaryFunctionType.MIN);
        t3.add(SummaryFunctionType.MAX);
        type2function.put(ListGridFieldType.DATE, t3);
        // Поле дата-время
        LinkedHashSet<SummaryFunctionType> t4 = new LinkedHashSet<SummaryFunctionType>();
        t4.add(SummaryFunctionType.COUNT);
        t4.add(SummaryFunctionType.MIN);
        t4.add(SummaryFunctionType.MAX);
        type2function.put(ListGridFieldType.DATETIME, t4);
        // Поле время
        LinkedHashSet<SummaryFunctionType> t5 = new LinkedHashSet<SummaryFunctionType>();
        t5.add(SummaryFunctionType.COUNT);
        t5.add(SummaryFunctionType.MIN);
        t5.add(SummaryFunctionType.MAX);
        type2function.put(ListGridFieldType.TIME, t5);
//Поле ссылка
        LinkedHashSet<SummaryFunctionType> t6 = new LinkedHashSet<SummaryFunctionType>();
        t6.add(SummaryFunctionType.COUNT);
        type2function.put(ListGridFieldType.LINK, t6);
        //Поле булин
        LinkedHashSet<SummaryFunctionType> t7 = new LinkedHashSet<SummaryFunctionType>();
        t7.add(SummaryFunctionType.COUNT);
        type2function.put(ListGridFieldType.BOOLEAN, t7);
        //Поле иконка
        LinkedHashSet<SummaryFunctionType> t8 = new LinkedHashSet<SummaryFunctionType>();
        t8.add(SummaryFunctionType.COUNT);
        type2function.put(ListGridFieldType.ICON, t8);
        //Двоичное поле
        LinkedHashSet<SummaryFunctionType> t9 = new LinkedHashSet<SummaryFunctionType>();
        t9.add(SummaryFunctionType.COUNT);
        type2function.put(ListGridFieldType.BINARY, t9);

        /// Кастомные функции

        //Для поля дата время
        Map<SummaryFunctionType, SummaryFunction> f1 = new HashMap<SummaryFunctionType, SummaryFunction>();

        f1.put(SummaryFunctionType.MIN, new SummaryFunction() {
            @Override
            public Object getSummaryValue(Record[] records, ListGridField field) {
                long min = Long.MAX_VALUE;
                for (int i = 0, recordsLength = records.length; i < recordsLength; i++) {
                    Date d = ((Date) records[i].toMap().get(field.getName()));
                    if (d == null) continue;
                    long ld = d.getTime();
                    if (ld < min) min = ld;
                }
                return new Date(min);
            }
        });
        f1.put(SummaryFunctionType.MAX, new SummaryFunction() {
            @Override
            public Object getSummaryValue(Record[] records, ListGridField field) {
                long max = Long.MIN_VALUE;
                for (int i = 0, recordsLength = records.length; i < recordsLength; i++) {
                    Date d = (Date) records[i].toMap().get(field.getName());
                    if (d == null) continue;
                    long ld = d.getTime();
                    if (ld > max) max = ld;
                }
                return new Date(max);
            }
        });

        type2customFunction.put(ListGridFieldType.DATETIME, f1);
        // Для поля link
        Map<SummaryFunctionType, SummaryFunction> f2 = new HashMap<SummaryFunctionType, SummaryFunction>();
        f2.put(SummaryFunctionType.COUNT, new SummaryFunction() {
            @Override
            public Object getSummaryValue(Record[] records, ListGridField field) {

                return "" + records.length;
            }
        });
        type2customFunction.put(ListGridFieldType.LINK, f2);
    }

    public static LinkedHashSet<SummaryFunctionType> getFunctionForType(ListGridFieldType lgft) {
        return type2function.get(lgft);
    }

    public static SummaryFunction getCustomSummaryFunctionForType(ListGridFieldType lgft, SummaryFunctionType sft) {
        if (sft == null) return null;
        Map<SummaryFunctionType, SummaryFunction> m = type2customFunction.get(lgft);
        if (m == null) return null;
        SummaryFunction sf = m.get(sft);
        return sf;
    }

}
