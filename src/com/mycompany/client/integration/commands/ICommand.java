package com.mycompany.client.integration.commands;


import com.google.gwt.json.client.JSONObject;

/**
 * Created by Anton.Pozdnev on 12.02.2015.
 */
public interface ICommand {


    void execute(JSONObject obj);
    String getDescription();
}
