package com.mycompany.client.apps.App;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 04.09.15
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public class SyncHandlerImpl implements ISyncHandler
{
    private boolean completely=false;

    public void setCompletely(boolean completely)
    {
        this.completely=completely;
    }
    @Override
    public boolean isCompletely() {
        return completely;
    }
}
