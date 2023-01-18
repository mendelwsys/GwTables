package com.mycompany.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.UmbrellaException;
import com.mycompany.client.apps.App.App01;
import com.mycompany.client.apps.App.AppDesktopInformers;
import com.mycompany.client.security.Authorizer;
import com.mycompany.client.security.IAuthorizerListener;
import com.mycompany.client.test.informer.ReVokeLenta;
import com.mycompany.client.test.tplace.TPlaceTable;
import com.mycompany.common.security.User;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class HelloWorld implements EntryPoint, IAuthorizerListener {

    boolean test=true; //flag to switch mode now in test mode

    Logger logger = Logger.getLogger("");

    public void onModuleLoad()
    {


        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            @Override
            public void onUncaughtException(Throwable e) {
                Throwable unwrapped = unwrap(e);
                logger.log(Level.SEVERE, "", unwrapped);
            }

            public Throwable unwrap(Throwable e) {
                if (e instanceof UmbrellaException) {
                    UmbrellaException ue = (UmbrellaException) e;
                    if (ue.getCauses().size() == 1) {
                        return unwrap(ue.getCauses().iterator().next());
                    }
                }
                return e;
            }
        });

        App01.map = com.google.gwt.user.client.Window.Location.getParameterMap();

        if (App01.map != null && (App01.map.containsKey("isdesc") || App01.map.containsKey("informer"))) {
            App01.GUI_STATE_DESC = new DesktopGUIStateDesc();
            onModuleLoad_desk(App01.map);

        }
        else if (App01.map != null && ((App01.map.containsKey("lenta") || App01.map.containsKey("lnt") || App01.map.containsKey("demo"))))
            onModuleLoad_lenta();
        else {
            App01.GUI_STATE_DESC = new GUIStateDesc();
            if (test)
                onModuleLoad_deb();
            else
                onModuleLoad_wk();
        }
    }

    public void onModuleLoad_lenta()
    {
        new ReVokeLenta().run();
    }


    public void onModuleLoad_deb()
    {
//        com.google.gwt.user.client.Window.setTitle("(T02_137)");

        App01 app01 = new App01();
        if (App01.GUI_STATE_DESC.user == null)
        {
            App01.GUI_STATE_DESC.user = new User();
            App01.GUI_STATE_DESC.user.setIdUser(2821);
        }
        app01.run();

//            new BuilderDlg().run();

//        new RepTest().run();
//        new   TestCanvasInformer().run();


//        new ChartDemo4().run();
        //new GridCellWidgetsSample().run();
        //new DragSnapToGridSample().run();

//        new FiltersEditor().run();

//        new WarnInformer().run();
//        new T1().run();
//        new T2().run();
//        new TPlaceTable().run(); //Тест гриа событий с указанием места где события произошли


    }


    public void onModuleLoad_desk(final Map<String, List<String>> m) {
        // new App01().run();

//            new BuilderDlg().run();

//        new RepTest().run();
//        new   TestCanvasInformer().run();
//        new ReVokeLenta().run();

//        new ChartDemo4().run();
        //new GridCellWidgetsSample().run();
        //new DragSnapToGridSample().run();
//        com.google.gwt.user.client.Window.setTitle("(TD72)");
        //new FiltersEditor().run();
        new Authorizer().authorize(new IAuthorizerListener() {
            @Override
            public void onAuthorized() {
                AppDesktopInformers.map = m;
                // new WarnInformer().run();
                new AppDesktopInformers().run();
            }
        }, false);


    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad_wk() {
        new Authorizer().authorize(this,false);
    }

    @Override
    public void onAuthorized() {
        new App01().run();
    }
}