package com.mycompany.client.test.fbuilder;

import com.smartgwt.client.types.ValueEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 07.03.15
 * Time: 16:57
 * Именна функций агрегации
 */
public enum Aggregates implements ValueEnum
{


    COUNT("Кол-во"),
    SUM("Сумма"),
    MIN("Мин"),
    MAX("Макс"),
    ARG("Среднее"),
    MULTIPLIER("Произведение");
    private static Map<String, Aggregates> nameToFunctionMapping;
    private String value;
    Aggregates(String value) {
        this.value = value;
    }

    public static Aggregates getAggregate(String i) {
        if (nameToFunctionMapping == null) {
            initMapping();
        }
        return nameToFunctionMapping.get(i);
    }

    private static void initMapping() {
        nameToFunctionMapping = new HashMap<String, Aggregates>();
        for (Aggregates s : values()) {
            nameToFunctionMapping.put(s.value, s);
        }
    }

    public String getValue() {
        return this.value;
    }


}
