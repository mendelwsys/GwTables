package com.mycompany.client.security;


import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Dialog;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.*;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

import java.util.LinkedHashMap;


/**
 * Created by Anton.Pozdnev on 19.02.2015.
 */
public class AuthWindow extends Dialog {


    public static AuthWindow createAuthWindow(final IAuthenticateCallback callback) {
        final AuthWindow auth = constructWindowTemplate();
        final DynamicForm form = new DynamicForm();
        form.setLayoutAlign(Alignment.CENTER);
        form.setAutoWidth();
        final TextItem textItem = new TextItem();
        textItem.setTitle("Пользователь");
        final PasswordItem passwordItem = new PasswordItem();
        passwordItem.setTitle("Пароль");
        auth.error = new StaticTextItem();
        auth.error.setTextBoxStyle("authError");
        auth.error.hide();
        auth.error.setShowTitle(false);
        auth.error.setColSpan(2);
        final ButtonItem authenticate = new ButtonItem("Войти");
        form.setItems(textItem, passwordItem, auth.error, authenticate);
        authenticate.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
            @Override
            public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
                if (auth.error.isVisible())
                    auth.error.hide();
                callback.authenticate(textItem.getValueAsString(), passwordItem.getValueAsString());
            }
        });
        auth.addItem(form);
        return auth;
    }

    StaticTextItem error = null;

    public void setError(AuthException e) {
        try {
            error.show();
            error.setValue(e.getMessage());
        } catch (Exception e1) {
            //   GWT.log(""+e1);
        }
    }

    private void test(AuthWindow w) {
        AuthException e = new AuthException("Неверно введен логин или пароль.\n Попытайтесь еще раз.");
        StackTraceElement e1 = new StackTraceElement("", "Элемент 1", "", 1);
        StackTraceElement e2 = new StackTraceElement("", "Элемент 2", "", 1);
        StackTraceElement[] array = new StackTraceElement[2];
        array[0] = e1;
        array[1] = e2;
        e.setStackTrace(array);
        w.setError(e);
    }

    public static AuthWindow createAuthWindowWithDropdown(final IAuthenticateCallback callback) {
final AuthWindow auth = constructWindowTemplate();
        final DynamicForm form = new DynamicForm();
        form.setLayoutAlign(Alignment.CENTER);
        form.setAutoWidth();
        final ComboBoxItem textItem = new ComboBoxItem();
        textItem.setCanEdit(true);
        textItem.setValueMap(users);
        textItem.setTitle("Пользователь");
        final PasswordItem passwordItem = new PasswordItem();
        passwordItem.setTitle("Пароль");
        passwordItem.hide();
        auth.error = new StaticTextItem();
        auth.error.setTextBoxStyle("authError");
        auth.error.hide();
        auth.error.setShowTitle(false);
        auth.error.setColSpan(2);
        textItem.setAddUnknownValues(true);

        textItem.addChangedHandler(new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
              if (event.getItem().getValue() instanceof String) {

                  passwordItem.show();
                  passwordItem.setValue("");
              }
                else
                  passwordItem.setValue(((UserWithPassword) event.getItem().getValue()).getPassword());
            }
        });
        final ButtonItem authenticate = new ButtonItem("Войти");

        form.setItems(textItem, passwordItem, auth.error, authenticate);
        authenticate.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
            @Override
            public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
                if (auth.error.isVisible())
                    auth.error.hide();
                callback.authenticate(textItem.getValueAsString(), passwordItem.getValueAsString());
            }
        });
        authenticate.setColSpan(2);
        authenticate.setAlign(Alignment.CENTER);
        auth.addItem(form);
        return auth;
    }


    static AuthWindow constructWindowTemplate()
    {
        final AuthWindow auth = new AuthWindow();
        auth.setIsModal(false);
        auth.setWidth(300);
        auth.setHeight(150);
        auth.setShowResizer(false);
        auth.setAutoCenter(true);
        auth.setShowMinimizeButton(false);
        auth.setIsModal(true);
        auth.setTitle("Вход в систему");
        auth.setShowCloseButton(false);
        auth.setShowModalMask(true);
return auth;
    }

static class UserWithPassword
{
    String login;

    public UserWithPassword(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    String password;

    @Override
    public String toString() {
        return login;
    }
}

    static LinkedHashMap<UserWithPassword,String> users= null;
    static{
        users= new LinkedHashMap<UserWithPassword,String>();
        populateMapWithData();
    }
    static void  populateMapWithData()
    {
        users.put(new AuthWindow.UserWithPassword("user", "user"),"00: Центральное ЦУСИ");
        users.put(new AuthWindow.UserWithPassword("user01", "user01"),"01: Октябрьская дорога");
        users.put(new AuthWindow.UserWithPassword("user10", "user10"),"10: Калининградская дорога");
        users.put(new AuthWindow.UserWithPassword("user17", "user17"),"17: Московская дорога");
        users.put(new AuthWindow.UserWithPassword("user24", "user24"),"24: Горьковкая дорога");
        users.put(new AuthWindow.UserWithPassword("user28", "user28"),"28: Северная дорога");
        users.put(new AuthWindow.UserWithPassword("user51", "user51"),"51: Северо-Кавказская дорога");
        users.put(new AuthWindow.UserWithPassword("user58", "user58"),"58: Юго-Восточная дорога");
        users.put(new AuthWindow.UserWithPassword("user61", "user61"),"61: Приволжская дорога");
        users.put(new AuthWindow.UserWithPassword("user63", "user63"),"63: Куйбышевская дорога");
        users.put(new AuthWindow.UserWithPassword("user76", "user76"),"76: Свердловская дорога");
        users.put(new AuthWindow.UserWithPassword("user80", "user80"),"80: Южно-Уральская дорога");
        users.put(new AuthWindow.UserWithPassword("user83", "user83"),"83: Западно-Сибирская дорога");
        users.put(new AuthWindow.UserWithPassword("user88", "user88"),"88: Красноярская дорога");
        users.put(new AuthWindow.UserWithPassword("user92", "user92"),"92: Восточно-Сибирская дорога");
        users.put(new AuthWindow.UserWithPassword("user94", "user94"),"94: Забайкальская дорога");
        users.put(new AuthWindow.UserWithPassword("user96", "user96"),"96: Дальневосточная дорога");
    }









}
