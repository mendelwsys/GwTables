package com.mycompany.client.updaters;

import com.mycompany.client.ListGridWithDesc;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 25.02.15
 * Time: 18:05
 * Фабрика гридов самых разных
 */
public interface IGridFactory
{
    ListGridWithDesc createGrid();
}
