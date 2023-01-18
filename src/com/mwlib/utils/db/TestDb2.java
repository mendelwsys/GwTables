package com.mwlib.utils.db;

import java.sql.*;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 18.07.14
 * Time: 11:55
 * To change this template use File | Settings | File Templates.
 */
public class TestDb2
{
    public static void main(String[] args) throws Exception
    {
        Collection<Directory.StanDesc> reqlist = new LinkedList<Directory.StanDesc>();

        Connection conn = DbUtil.getConnection2(DbUtil.DS_DB2_NAME);
        DaoUtils.fillObjectCollection(conn, reqlist, Directory.StanDesc.class, "select * from IC00.STAN where COR_TIP in ('I','U')");
        DbUtil.closeConnection(conn);

    }

}
