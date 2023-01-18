package com.mycompany.client.dojoChart;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 05.03.15
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
public interface IFormatValue
{
    boolean isPresentableValue(Value value);

    Object presentationValue(Value value);

    String formatValue(Value value);
}
