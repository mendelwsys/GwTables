package com.mycompany.client;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mycompany.client.apps.App.App01;
import com.mycompany.client.apps.App.api.CreateInformerOperation;
import com.mycompany.client.apps.App.api.NewWarnInformer;
import com.mycompany.common.DefaultProfiles;
import com.mycompany.common.DescOperation;
import com.mycompany.common.UserProfile;
import com.mycompany.common.security.IUser;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.util.SC;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Anton.Pozdnev on 18.06.2015.
 */
public class DesktopGUIStateDesc extends GUIStateDesc {
    public DesktopGUIStateDesc(String currentUserId, UserProfile currentProfile, IUser user) {
        super(currentUserId, currentProfile, user);
        init("informer");
    }

    public DesktopGUIStateDesc() {
        init("informer");

    }

    @Override
    public void loadDescTop(final App01 app01) {
        this.app01 = app01;
        if (app01.map.containsKey("isdesc") || app01.map.containsKey("informer")) applicationMode = DESKTOP;

        if (!new_profiles) {
            getBuildTreeService().getProfiles(getCurrentUserId()
                    , new AsyncCallback<UserProfile[]>() {
                @Override
                public void onFailure(Throwable caught) {
                    SC.warn(caught.getMessage());
                }

                @Override
                public void onSuccess(UserProfile[] inBaseProfiles) {
                    if (inBaseProfiles == null)
                        inBaseProfiles = new UserProfile[0];


                    if (currentProfile == null) {
                        if (inBaseProfiles.length > 0)
                            currentProfile = new UserProfile(inBaseProfiles[0]);
                        else
                            currentProfile = new UserProfile(DefaultProfiles.defProfile);
                    }

                }
            });
        } else {
           /* getBuildTreeService().getProfiles_new(new BigDecimal("" + user.getIdUser())
                    , new AsyncCallback<UserProfile[]>() {
                @Override
                public void onFailure(Throwable caught) {
                    SC.warn(caught.getMessage());
                }

                @Override
                public void onSuccess(UserProfile[] inBaseProfiles) {
                    if (inBaseProfiles == null)
                        inBaseProfiles = new UserProfile[0];


                    if (currentProfile == null) {
                        if (inBaseProfiles.length > 0)
                            currentProfile = new UserProfile(inBaseProfiles[0]);
                        else
                            currentProfile = new UserProfile(DefaultProfiles.defProfile);
                    }
                    com.google.gwt.user.client.Window.setTitle(currentProfile.getProfileName() + "-" + App01.DEFAULT_TITLE);

                }
            });*/

            getBuildTreeService().getProfileIdToLoadFirst(new BigDecimal("" + user.getIdUser()), UserProfile.DESKTOP_INFORMER_PROFILES, new AsyncCallback<List<UserProfile>>() {
                @Override
                public void onFailure(Throwable caught) {
                    SC.warn(caught.getMessage());
                }

                @Override
                public void onSuccess(List<UserProfile> result) {


                    if (currentProfile == null) {
                        if (result.size() > 0)
                            currentProfile = new UserProfile(result.get(0));
                        else
                            currentProfile = new UserProfile(DefaultProfiles.defProfile);
                    }
                    com.google.gwt.user.client.Window.setTitle(currentProfile.getProfileName() + "-" + App01.DEFAULT_TITLE);
                }
            });

        }

//        if (false)
        {
            final Timer[] t = new Timer[1];
            t[0] = new Timer() {
                @Override
                public void run() {
                    if (getCurrentProfile() != null) {
                        if (!new_profiles) {
                            getBuildTreeService().getNodes(getCurrentProfile(), new AsyncCallback<DescOperation>() //Проверка передачи сложный объектов через канал - работает
                            {
                                @Override
                                public void onFailure(Throwable caught) {
                                    SC.warn(caught.getMessage());
                                }

                                @Override
                                public void onSuccess(DescOperation result) {
                                    applyDescOperation(result);
                                }
                            });
                        } else {

                            getBuildTreeService().getNodes_new(new BigDecimal("" + user.getIdUser()), getCurrentProfile(), new AsyncCallback<DescOperation>() //Проверка передачи сложный объектов через канал - работает
                            {
                                @Override
                                public void onFailure(Throwable caught) {
                                    SC.warn(caught.getMessage());
                                }

                                @Override
                                public void onSuccess(DescOperation result) {

                                    processResult(result);
                                }
                            });

                        }
                    } else
                        t[0].schedule(800);
                }
            };
            t[0].schedule(800);
        }
    }

    public void processResult(DescOperation result) {
        res = result;


        if (app01.map.containsKey("isdesc")) {

            setDesktopConfig(result);
        } else {

            loadInformer(app01.map.get("informer").get(0));
        }

    }


    DescOperation res;

    static String[] informers = {"com.mycompany.client.apps.App.api.NewDelayInformer", "com.mycompany.client.apps.App.api.NewRefInformer", "com.mycompany.client.apps.App.api.NewViolInformer", "com.mycompany.client.apps.App.api.NewWarnInformer", "com.mycompany.client.apps.App.api.NewDelayInformer2", "com.mycompany.client.apps.App.api.NewRefInformer2", "com.mycompany.client.apps.App.api.NewViolInformer2", "com.mycompany.client.apps.App.api.NewWarnInformer2", "com.mycompany.client.apps.App.api.NewDelayInformer3", "com.mycompany.client.apps.App.api.NewRefInformer3", "com.mycompany.client.apps.App.api.NewViolInformer3", "com.mycompany.client.apps.App.api.NewWarnInformer3"};

    static {
        Arrays.sort(informers);

    }

    protected void setDesktopConfig(DescOperation result) {
        boolean first = true;
        //  int num = 1;
        for (int i = 0; i < result.getSubOperation().size(); i++) {
            if (Arrays.binarySearch(informers, ((DescOperation) result.getSubOperation().get(i)).apiName) < 0) {
                continue;

            } else if (first) {

                String firstInformerId = "" + ((Long) ((DescOperation) result.getSubOperation().get(i)).get(CreateInformerOperation.CREATED_INFORMER_ID)).longValue();
                setMainWindowInformerIdNative(firstInformerId);
                loadInformer(firstInformerId);

                first = false;
            } else {

                openNewDesktopWindow("" + ((Long) ((DescOperation) result.getSubOperation().get(i)).get(CreateInformerOperation.CREATED_INFORMER_ID)).longValue(), (Integer) (((DescOperation) result.getSubOperation().get(i)).get(NewWarnInformer.CRD_LEFT)), (Integer) (((DescOperation) result.getSubOperation().get(i)).get(NewWarnInformer.CRD_TOP)));

            }

        }
        if (first) {
            SC.say("Нет информеров для загрузки");

        }

    }


    protected void loadInformer(String informerId) {

        DescOperation desc = new DescOperation();
        desc.apiName = res.apiName;
        desc.setParamsHM(res.getParamsHM());
        //  int num = 0;
        for (int i = 0; i < res.getSubOperation().size(); i++) {
            if (Arrays.binarySearch(informers, ((DescOperation) res.getSubOperation().get(i)).apiName) < 0)

            {

                continue;
            } else if (informerId.equalsIgnoreCase("" + ((Long) ((DescOperation) res.getSubOperation().get(i)).get(CreateInformerOperation.CREATED_INFORMER_ID)).longValue())) {

                desc.getSubOperation().add(res.getSubOperation().get(i));

            }


        }

        applyDescOperation(desc);


        DescOperation op = ((DescOperation) desc.getSubOperation().get(0));
        Integer x = null;
        Integer y = null;
        Integer zoom = (Integer) op.get(CreateInformerOperation.DESKTOP_ZOOM);
        String state = (String) op.get(CreateInformerOperation.DESKTOP_WINDOW_STATE);
        if (op.get(CreateInformerOperation.DESKTOP_X) != null) {

            x = (Integer) op.get(CreateInformerOperation.DESKTOP_X);
        } else {

            x = (Integer) (op.get(NewWarnInformer.CRD_LEFT));
        }

        if (op.get(CreateInformerOperation.DESKTOP_Y) != null) {

            y = (Integer) op.get(CreateInformerOperation.DESKTOP_Y);
        } else {

            y = (Integer) (op.get(NewWarnInformer.CRD_TOP));
        }


        setWindowPositionNative(informerId, state == null ? null : state, zoom == null ? 0 : zoom, x, y);
    }

    void openNewDesktopWindow(String id, int x, int y) {

        openWindowsNative(id, x, y);

    }

    native void setWindowPositionNative(String id, String state, int zoom, int x, int y) throws JavaScriptException/*-{
        //  $wnd.alert($wnd.parentframe);


        if ($wnd.parentframe) {
            //$wnd.alert('inside');
            $wnd.parentframe.parent.setWindowPosition(id, state, zoom, x, y);
            //    $wnd.alert('after');
        }

    }-*/;

    native void openWindowsNative(String id, int x, int y) throws JavaScriptException/*-{
        //  $wnd.alert($wnd.parentframe);


        if ($wnd.parentframe) {
            //$wnd.alert('inside');
            $wnd.parentframe.parent.createNewWindow(id, x, y);
            //    $wnd.alert('after');
        }

    }-*/;

    native void setMainWindowInformerIdNative(String id) throws JavaScriptException/*-{
        //  $wnd.alert($wnd.parentframe);


        if ($wnd.parentframe) {
            //$wnd.alert('inside');
            $wnd.parentframe.parent.setMainWindowInformerId(id);
            //    $wnd.alert('after');
        }

    }-*/;

    private native void initInternal() throws JavaScriptException/*-{

        // $wnd.parentframe;

        $wnd.findParent = $entry(function (id2, iframes) {

            if (!iframes) return;
            var i = 0;
            var par;
            for (; i < iframes.frames.length; i++) {


                if (id2 === iframes.frames[i].frameElement.id) return iframes.frames[i];
                if (iframes.parent) par = $wnd.findParent(id2, iframes.parent);
                if (par) return par;
            }
        });

        var that = this;
        $wnd.saveConfig = $entry(function (jsonconf) {
                that.@com.mycompany.client.DesktopGUIStateDesc::saveConfig(Ljava/lang/String;)(jsonconf)
            }
        );

    }-*/;

    private native void findParent(String parentId) throws JavaScriptException/*-{
        if (!$wnd.parentframe)
            $wnd.parentframe = $wnd.findParent(parentId, $wnd.parent)

    }-*/;

    protected void saveConfig(String conf) {
        // SC.say(conf);
        JavaScriptObject jso = JSON.decode(conf);
        JavaScriptObject[] windows = JSOHelper.getAttributeAsJavaScriptObjectArray(jso, "confwindows");
        if (windows == null) return;
        // SC.say("windows not null "+windows.length);
        for (int i = 0; i < windows.length; i++) {
            String id = JSOHelper.getAttribute(windows[i], "id");
            String state = JSOHelper.getAttribute(windows[i], "state");
            String x = JSOHelper.getAttribute(windows[i], "x");
            String y = JSOHelper.getAttribute(windows[i], "y");
            String zoom = JSOHelper.getAttribute(windows[i], "zoom");
            //  SC.say("id = "+id+" state = "+state+" x= "+x+" y= "+y);
            for (int j = 0; j < res.getSubOperation().size(); j++) {
                DescOperation op = ((DescOperation) res.getSubOperation().get(j));

                if (Arrays.binarySearch(informers, op.apiName) < 0 || !id.equalsIgnoreCase("" + (Long) op.get(CreateInformerOperation.CREATED_INFORMER_ID)))

                {

                    continue;
                } else if (id.equalsIgnoreCase("" + ((Long) op.get(CreateInformerOperation.CREATED_INFORMER_ID)).longValue())) {
//SC.say("found " +(i+1));
                    op.put(CreateInformerOperation.DESKTOP_WINDOW_STATE, state);
                    op.put(CreateInformerOperation.DESKTOP_X, Integer.parseInt(x));
                    op.put(CreateInformerOperation.DESKTOP_Y, Integer.parseInt(y));
                    op.put(CreateInformerOperation.DESKTOP_ZOOM, Integer.parseInt(zoom));


                }


            }
        }
        this.getBuildTreeService().saveNodes_new(new BigDecimal("" + user.getIdUser()), this.getCurrentProfile(), res, new AsyncCallback<UserProfile>() //Проверка передачи сложный объектов через канал - работает
        {
            @Override
            public void onFailure(Throwable caught) {
                SC.warn(caught.getMessage());
                // winModal.destroy();
            }

            @Override
            public void onSuccess(UserProfile result) {
                // currentProfile = new UserProfile(result);
                //  winModal.destroy();//TODO Надо возвращать идентифкатор профиля
            }
        });


    }

    private void init(String frameId) {
        initInternal();
        findParent(frameId);

    }


}
