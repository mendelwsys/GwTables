package com.mycompany.client.integration.commands;


import com.google.gwt.json.client.JSONObject;
import com.mycompany.client.apps.App.App01;
import com.mycompany.common.security.IUser;
import com.mycompany.common.security.User;


/**
 * Created by Anton.Pozdnev on 12.02.2015.
 */
public class DefaultAuthUserCommand implements ICommand {
    @Override
    public void execute(JSONObject obj) {

       App01.GUI_STATE_DESC.setUser(convertFromJSONToUserPrincipal(obj));
    }

    @Override
    public String getDescription() {
        return "Аутентификация пользователя";
    }

    public static IUser convertFromJSONToUserPrincipal(JSONObject obj) {
        IUser user = new User();
        user.setDolId(obj.get("dolId") != null ? (int) obj.get("dolId").isNumber().doubleValue() : 0);
        user.setDorKod(obj.get("dorKod") != null ? (int) obj.get("dorKod").isNumber().doubleValue() : 0);
        user.setFirstName(obj.get("firstName") != null ? obj.get("firstName").isString().stringValue() : "");
        user.setIdLevel(obj.get("idLevel") != null ? (int) obj.get("idLevel").isNumber().doubleValue() : 0);
        user.setIdPers(obj.get("idPers") != null ? (int) obj.get("idPers").isNumber().doubleValue() : 0);
        user.setIdPredType(obj.get("idPredType") != null ? (int) obj.get("idPredType").isNumber().doubleValue() : 0);
        user.setIdUser(obj.get("idUser") != null ? (int) obj.get("idUser").isNumber().doubleValue() : 0);
        user.setIdXoz(obj.get("idXoz") != null ? (int) obj.get("idXoz").isNumber().doubleValue() : 0);
        user.setLastName(obj.get("lastName") != null ? obj.get("lastName").isString().stringValue() : "");
        user.setMiddleName(obj.get("middleName") != null ? obj.get("middleName").isString().stringValue() : "");
        user.setOtdelId(obj.get("otdelId") != null ? (int) obj.get("otdelId").isNumber().doubleValue() : 0);
        user.setPodrId(obj.get("podrId") != null ? (int) obj.get("podrId").isNumber().doubleValue() : 0);
        user.setPredId(obj.get("predId") != null ? (int) obj.get("predId").isNumber().doubleValue() : 0);
        user.setRoles(obj.get("roles") != null ? obj.get("roles").isString().stringValue() : "");
        user.setStanId(obj.get("stanId") != null ? (int) obj.get("stanId").isNumber().doubleValue() : 0);


        return user;
    }




}
