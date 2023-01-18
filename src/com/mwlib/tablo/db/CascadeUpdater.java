package com.mwlib.tablo.db;

import com.mycompany.common.TablesTypes;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 01.06.15
 * Time: 17:08
 * Класс для обновления данных, нескольких классов следжующих интерфейсу ICascadeUpdater
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
    private int period = TablesTypes.DEFPERIOD;
    private final List<ICascadeUpdater> updaters;

    public CascadeUpdater(List<ICascadeUpdater> updaters)
    {
        this.updaters = updaters;
        terminate = false;
    }

    public int getPeriod()
    {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }



    @Override
    public void run()
    {

        for (ICascadeUpdater updater : updaters)
          updater.initStartParams();

        while (!terminate)
        {
            try {
                long ln = System.currentTimeMillis();
                for (ICascadeUpdater updater : updaters)
                {
                    System.out.println("updater " + updater.getUpdaterName()+" Start ");
                    long ln1 = System.currentTimeMillis();
                    updater.performUpdate();
                    System.out.println("updater " + updater.getUpdaterName()+" complete  time: "+(System.currentTimeMillis()-ln1));
                }
                ln = System.currentTimeMillis() - ln;
                try
                {
                    Thread.sleep(Math.max(period - ln, 100));
                }
                catch (InterruptedException e)
                {//
                }
            }

            catch (RuntimeException e)
            {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
