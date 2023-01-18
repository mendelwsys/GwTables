package com.mwlib.tablo.test.derby;

import com.mwlib.tablo.derby.DerbyTableOperations;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 04.09.15
 * Time: 16:43
 * To change this template use File | Settings | File Templates.
 */
public class CheckIX
{
    public static void main(String[] args) throws Exception {
        DerbyTableOperations op = DerbyTableOperations.getDefDerbyTableOperations();
        op.isTableIxExists("VAGTOR","VAGTOR_IX");
    }
}
