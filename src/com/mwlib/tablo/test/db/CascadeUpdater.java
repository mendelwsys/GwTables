package com.mwlib.tablo.test.db;

import com.mwlib.tablo.db.ICascadeUpdater;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.06.15
 * Time: 17:08
 * To change this template use File | Settings | File Templates.
 */
public class CascadeUpdater implements Runnable {

    public boolean isTerminate()
    {
        return terminate;
    }
    public void setTerminate()
    {
        this.terminate = true;
    }

    private boolean terminate = false;

    private final List<ICascadeUpdater> updaters;

    public CascadeUpdater(List<ICascadeUpdater> updaters)
    {
        this.updaters = updaters;
        terminate = false;
    }

    @Override
    public void run()
    {

        for (ICascadeUpdater updater : updaters)
          updater.initStartParams();

        while (!terminate)
        {
            long ln = System.currentTimeMillis();
            for (ICascadeUpdater updater : updaters)
                updater.performUpdate();
            ln = System.currentTimeMillis() - ln;
            try
            {
                    Thread.sleep(Math.max(5 * 1000 - ln, 100));
            }
            catch (InterruptedException e)
            {//
            }
        }
    }
}
