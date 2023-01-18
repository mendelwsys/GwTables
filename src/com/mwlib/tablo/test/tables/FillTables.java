package com.mwlib.tablo.test.tables;

import com.mwlib.utils.db.Directory;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 05.09.14
 * Time: 19:55
 * To change this template use File | Settings | File Templates.
 */
public class FillTables
{
    public static void main(String[] args) throws Exception
    {

        BaseTable[] tables=new BaseTable[]
                {
                        new GidMarksT(false),
                        new LostTrT(false),
                        new VagInTORT(false),
                        new WarningsT(false),
                        new StripT(false),
                        new WindowsT(false),
                        new ViolationT(false),
                        new RefuseT(false)
                };

        String workdir=System.getProperty("user.dir")+"/src";
        Directory.main(new String[]{workdir});
        for (BaseTable table : tables)
            table.fillTestFile(workdir+table.getFileTestName());


    }
}
