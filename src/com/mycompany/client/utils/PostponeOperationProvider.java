package com.mycompany.client.utils;

import com.google.gwt.user.client.Timer;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 11.02.15
 * Time: 12:10
 * Асинхронное исполнение операции
 */
public class PostponeOperationProvider
{

    public static final int DEF_DELAY = 500;

    public static interface IPostponeOperation
    {
        boolean operate();
    }

    public PostponeOperationProvider(final IPostponeOperation operation)
    {
        this(operation, DEF_DELAY);
    }

    public PostponeOperationProvider(final IPostponeOperation operation,final int delay)
    {
            if (!operation.operate())
            {
                final Timer[] tt=new Timer [1];
                tt[0]=new Timer()
                {
                    //int ix=0;
                    @Override
                    public void run()
                    {
                        if (!operation.operate())
                            tt[0].schedule(delay);//Здесь ожидаем пока операция не будет готова для исполнения
                    }
                };
                tt[0].schedule(delay);
            }


    }



}
