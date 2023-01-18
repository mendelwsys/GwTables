package com.mycompany.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mycompany.client.apps.App.App01;
import com.mycompany.client.apps.App.AppConst;
import com.mycompany.client.apps.App.OptionsViewers;
import com.mycompany.client.apps.App.api.MainProcessor;
import com.mycompany.client.apps.App.api.OperationHolder;
import com.mycompany.client.apps.toolstrip.MyToolStrip;
import com.mycompany.client.operations.OperationCtx;
import com.mycompany.client.security.Authorizer;
import com.mycompany.client.security.IAuthorizerListener;
import com.mycompany.client.utils.ListenerCtrl;
import com.mycompany.client.utils.PostponeOperationProvider;
import com.mycompany.common.DefaultProfiles;
import com.mycompany.common.DescOperation;
import com.mycompany.common.TablesTypes;
import com.mycompany.common.UserProfile;
import com.mycompany.common.security.IUser;
import com.smartgwt.client.types.*;
import com.smartgwt.client.util.EventHandler;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.*;
import com.smartgwt.client.widgets.events.*;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.TreeGrid;

import java.math.BigDecimal;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 23.07.14
 * Time: 11:14
 */
public class GUIStateDesc implements IAuthorizerListener {

    public static final String OPEN_DESCRIPTOR_ATTR_NAME = "OpenDescriptor";
    protected boolean new_profiles = true;

    protected ListenerCtrl<GUIStateDesc> unloadListenerCtrl = new ListenerCtrl<GUIStateDesc>();

    public ListenerCtrl<GUIStateDesc> getUnloadListenerCtrl() {
        return unloadListenerCtrl;
    }

    public static native JavaScriptObject getUserAgent2() /*-{
        return $wnd.isc.Browser;
    }-*/;


    public static native String getUserAgent() /*-{
        return $wnd.navigator.userAgent;
    }-*/;

    /**
     * Gets the navigator.appName.
     *
     * @return the window's navigator.appName.
     */
    public static native String getAppName() /*-{
        return $wnd.navigator.appName;
    }-*/;

    public GUIStateDesc() {
    }

    public String getApplicationMode() {
        return applicationMode;
    }

    public void setApplicationMode(String applicationMode) {
        this.applicationMode = applicationMode;
    }

    String applicationMode = BROWSER;
    public static final String DESKTOP = "isdesc";
    public static final String BROWSER = "";

    public DescOperation getChangeUserProfile() {
        return changeUserProfile;
    }

    public void setChangeUserProfile(DescOperation changeUserProfile) {
        this.changeUserProfile = changeUserProfile;
    }

    protected DescOperation changeUserProfile = null;

    public GUIStateDesc(String currentUserId, UserProfile currentProfile, IUser user) {
        this.currentUserId = currentUserId;
        this.currentProfile = currentProfile;
        this.user = user;
    }

    public BuildTreeServiceAsync getBuildTreeService() {
        return buildTreeService;
    }

    final BuildTreeServiceAsync buildTreeService = GWT.create(BuildTreeService.class);

    public MainProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(MainProcessor processor) {
        this.processor = processor;
    }

    protected MainProcessor processor = new MainProcessor();//Обработчик описателей

    public final static int systemPage = -1;
    public final static int tablePage = 0;
    public final static int queuePage = 1;

    private int nextPgNum = 10;

    //Addon
    public IUser getUser() {
        return user;
    }

    public void setUser(IUser user) {
        this.user = user;
    }

    //Addon-end
    protected IUser user = null;


    public MyToolStrip getToolStrip() {
        return toolStrip;
    }

    public void setToolStrip(MyToolStrip toolStrip) {
        this.toolStrip = toolStrip;
    }

    private MyToolStrip toolStrip;

    public Layout getMainLayout() {
        return mainLayout;
    }

    public void setMainLayout(Layout mainLayout) {
        this.mainLayout = mainLayout;
    }

    protected Layout mainLayout;


    public Canvas[] getNotSwitchPageCanvas() {
        final Collection<Canvas[]> values = notSwitchPageCanvas.values();
        return collectCanvas(values);
    }

    private Canvas[] collectCanvas(Collection<Canvas[]> values) {
        List<Canvas> ll = new LinkedList<Canvas>();
        for (Canvas[] canvases : values)
            ll.addAll(Arrays.asList(canvases));
        return ll.toArray(new Canvas[ll.size()]);
    }

    public void add2NotSwitchToolsCanvas(int pageNumber, Canvas[] notSwitchToolsCanvas) {
        add2Canvas(pageNumber, notSwitchToolsCanvas, this.notSwitchToolsCanvas);
    }

    private void add2Canvas(int pageNumber, Canvas[] addNotSwitchCanvas, Map<Integer, Canvas[]> notSwitchCanvas) {
        final Canvas[] canvases = notSwitchCanvas.get(pageNumber);
        if (canvases == null)
            notSwitchCanvas.put(pageNumber, addNotSwitchCanvas);
        else {
            List<Canvas> ll = new LinkedList<Canvas>();
            ll.addAll(Arrays.asList(canvases));
            ll.addAll(Arrays.asList(addNotSwitchCanvas));
            notSwitchCanvas.put(pageNumber, ll.toArray(new Canvas[ll.size()]));
        }
    }

    public void add2NotSwitchPageCanvas(int pageNumber, Canvas[] notSwitchPageCanvas) {
        add2Canvas(pageNumber, notSwitchPageCanvas, this.notSwitchPageCanvas);
    }

    public Canvas[] getNotSwitchToolsCanvas() {
        final Collection<Canvas[]> values = notSwitchToolsCanvas.values();
        return collectCanvas(values);
    }


    protected Map<Integer, Canvas[]> notSwitchPageCanvas = new HashMap<Integer, Canvas[]>();
    protected Map<Integer, Canvas[]> notSwitchToolsCanvas = new HashMap<Integer, Canvas[]>();


    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getNumPages() {
        return pages.size();//Не учитываем системную страницу
    }

    private Map<Integer, List<Canvas>> getPages() {
        return pages;
    }

    public Set<Integer> getOrderedNumPages() {
        return new TreeSet<Integer>(pages.keySet());
    }


    public List<Canvas> getPageCanvas(int pageIx) {
        return pages.get(pageIx);
    }


    public int addPage(List<Canvas> comps) {
        int pgNum = nextPgNum++;
        this.pages.put(pgNum, comps);
        return pgNum;
    }


    public void destroyCurrentPage() {
        destroyPage(getCurrentPage());
    }

    public void destroyPage(int pgNumber) {

        final List<Integer> ixPages = new LinkedList<Integer>(pages.keySet());
        int newPageIx = ixPages.indexOf(pgNumber) - 1;
        if (newPageIx < 0)
            newPageIx = 1;
        int toPage = -1;
        if (newPageIx < getNumPages())
            toPage = ixPages.get(newPageIx);

        List<Canvas> tools = this.tools.remove(pgNumber);
        if (tools != null)
            for (Canvas tool : tools)
                toolStrip.removeMember(tool);

        Canvas[] notSwitchTools = this.notSwitchToolsCanvas.remove(pgNumber);
        if (notSwitchTools != null) {
            for (Canvas notSwitchTool : notSwitchTools)
                toolStrip.removeMember(notSwitchTool);
            for (Canvas notSwitchTool : notSwitchTools)
                notSwitchTool.destroy();
        }


        Canvas[] notSwitchCanvases = this.notSwitchPageCanvas.remove(pgNumber);
        if (notSwitchCanvases != null) {
            for (Canvas notSwitchCanvas : notSwitchCanvases)
                mainLayout.removeMember(notSwitchCanvas);
            for (Canvas notSwitchCanvas : notSwitchCanvases)
                notSwitchCanvas.destroy();

        }


        List<Canvas> onPages = this.pages.remove(pgNumber);


        if (pgNumber == getCurrentPage())
            mainLayout.removeMembers(onPages.toArray(new Canvas[onPages.size()]));

        for (Canvas onPage : onPages) {
            String treeView = onPage.getID();
            if (treeView != null && treeView.contains(AppConst.t_TREE_VIEW_INDICATOR))
                continue;
            onPage.destroy();
        }

        if (tools != null)
            for (Canvas tool : tools)
                tool.destroy();

        if (toPage >= 0)
            toPage(toPage);
    }


//    public void addNotSwitchTools(Canvas comp)
//    {
//        LinkedList<Canvas> ll=new LinkedList<Canvas>(Arrays.asList(notSwitchToolsCanvas));
//        ll.add(comp);
//        notSwitchToolsCanvas=ll.toArray(new Canvas[ll.size()]);
//    }


    private int currentPage = systemPage;

    private Map<Integer, List<Canvas>> pages = new HashMap<Integer, List<Canvas>>();

    public Map<Integer, List<Canvas>> getTools() {
        return tools;
    }

    public void setTools(Map<Integer, List<Canvas>> tools) {
        this.tools = tools;
    }

    private Map<Integer, List<Canvas>> tools = new HashMap<Integer, List<Canvas>>();

//    public void initCurrentPageStates()
//    {
//        List<Canvas> onPages = AppUtils.collectCurrentLayout(mainLayout, Arrays.asList(getNotSwitchPageCanvas()));
//        List<Canvas> onTool = AppUtils.collectCurrentVisibleLayout(toolStrip, Arrays.asList(getNotSwitchToolsCanvas()));
//
//        getPages().put(getCurrentPage(), onPages);
//        getTools().put(getCurrentPage(), onTool);
//
//    }

    public boolean switchPages(int toPage) {


        if (getCurrentPage() != toPage) {
            List<Canvas> onPages = AppUtils.collectCurrentLayout(mainLayout, Arrays.asList(getNotSwitchPageCanvas()));
            List<Canvas> onTool = AppUtils.collectCurrentVisibleLayout(toolStrip, Arrays.asList(getNotSwitchToolsCanvas()));

            if (getCurrentPage() != systemPage) {
                if (onPages.size() > 0)
                    getPages().put(getCurrentPage(), onPages);
                if (onTool.size() > 0)
                    getTools().put(getCurrentPage(), onTool);
            }

            mainLayout.removeMembers(onPages.toArray(new Canvas[onPages.size()]));
            for (Canvas canvas : onTool)
                canvas.setVisible(false);

            List<Canvas> currentPageMembers = toPage(toPage);

            return (currentPageMembers != null && currentPageMembers.size() > 0);
        }
        return true;
    }

    private List<Canvas> toPage(int toPage) {
        setCurrentPage(toPage);
        List<Canvas> currentPageMembers = getPages().get(toPage);
        if (currentPageMembers != null)
            mainLayout.addMembers(currentPageMembers.toArray(new Canvas[currentPageMembers.size()]));

        List<Canvas> currentToolsMembers = getTools().get(toPage);
        if (currentToolsMembers != null)
            for (Canvas currentToolsMember : currentToolsMembers) {
                currentToolsMember.setVisible(true);
            }
        return currentPageMembers;
    }


    public void addToolStripButton(ToolStripButton button) {
        if (toolStrip != null) {
            int cnt = toolStrip.getMembers().length;
            if (cnt > 0)
                toolStrip.addButton(button, cnt - 1);
        }

    }

    public Img getThinStripCtrl() {
        return thinStripCtrl;
    }

    Img thinStripCtrl;

    native void closeCurrentWindowNative() throws JavaScriptException/*-{
        var browserName = $wnd.navigator.appName;
        var browserVer = $wnd.parseInt($wnd.navigator.appVersion);
        //alert(browserName + " : "+browserVer);

        //document.getElementById("flashContent").innerHTML = "<br>&nbsp;<font face='Arial' color='blue' size='2'><b> You have been logged out of the Game. Please Close Your Browser Window.</b></font>";

        if(browserName == "Microsoft Internet Explorer"){
            var ie7 = ($wnd.document.all && !$wnd.window.opera && $wnd.window.XMLHttpRequest) ? true : false;
            if (ie7)
            {
                //This method is required to close a window without any prompt for IE7 & greater versions.
                $wnd.window.open('','_parent','');
                $wnd.window.close();
            }
            else
            {
                //This method is required to close a window without any prompt for IE6
               // this.focus();
               // $wnd.self.opener = this;
              //  $wnd.self.close();

                $wnd.window.open('javascript:$wnd.window.open("", "_self", "");$wnd.window.close();', '_self');
            }
        }else {
            //For NON-IE Browsers except Firefox which doesnt support Auto Close
            try {
                this.focus();
                $wnd.self.opener = this;
                $wnd.self.close();
            }
            catch (e) {

            }

            try {
                $wnd.window.open('', '_self', '');
                $wnd.window.close();
            }
            catch (e) {

            }
        }
    }-*/;
    public Img makeThinStripCtrl() {
        thinStripCtrl = new Img();
        thinStripCtrl.setSrc("[SKIN]Splitbar/vsplit_bg.png");
        thinStripCtrl.setImageType(ImageStyle.TILE);
        thinStripCtrl.setWidth(6);
        thinStripCtrl.setHeight100();
        thinStripCtrl.setPrompt(AppConst.DEF_TOOL_BAR_PROMT);
        thinStripCtrl.addMouseOverHandler(new MouseOverHandler() {
            public void onMouseOver(MouseOverEvent event) {
                thinStripCtrl.setCursor(Cursor.HAND);
            }
        });
        thinStripCtrl.addMouseOutHandler(new MouseOutHandler() {

            public void onMouseOut(MouseOutEvent event) {
                thinStripCtrl.setCursor(Cursor.DEFAULT);
            }
        });
        thinStripCtrl.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {

//                toolStrip.animateShow(toolStrip, AnimationEffect.SLIDE, "T", "B");
                toolStrip.setVisible(true);
                thinStripCtrl.setVisible(false);
            }
        });

        if (SC.isIE()) {
            JavaScriptObject js = GUIStateDesc.getUserAgent2();
            if (js != null) {
                String isIE10 = JSOHelper.getAttribute(js, "isIE10");
                String isIE11 = JSOHelper.getAttribute(js, "isIE11");

                if ("true".equalsIgnoreCase(isIE10) && !("true".equalsIgnoreCase(isIE11))) {

//                    com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler()
//                    {
//                        @Override
//                        public void onResize(ResizeEvent event)
//                        {
//                            int y = com.google.gwt.user.client.Window.getClientHeight();
//
//                            int y1 = toolStrip.getHeight();
//                            int y2 = mainLayout.getHeight();
//                            if (y2 != y)
//                                mainLayout.setHeight(y);
//                            if (y1 != y)
//                                toolStrip.setHeight(y);
//                        }
//                    });

//                    SC.say("!!!DEBUG MESSAGE!!! IE10 detected");
                    //TODO FIXING FUCKING IE10 RESIZE BUG!!!!!
                    toolStrip.addResizedHandler(new ResizedHandler() {
                        @Override
                        public void onResized(ResizedEvent event) {
                            int y = com.google.gwt.user.client.Window.getClientHeight();

                            int y1 = toolStrip.getHeight();
//                            int y2 = mainLayout.getHeight();
//                            if (y2 != y)
//                                mainLayout.setHeight(y);
                            if (y1 != y)
                                toolStrip.setHeight(y);
                        }
                    });
                    final Timer[] t = new Timer[1];
                    final GUIStateDesc gsd = this;
                    t[0] = new Timer() {
                        @Override
                        public void run() {
                            gsd.getBuildTreeService().shouldRestart(new AsyncCallback<Boolean>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    // better luck next time
                                }

                                @Override
                                public void onSuccess(Boolean result) {
                                    if (result) {
                                        int currentWindowWidth = com.google.gwt.user.client.Window.getClientWidth();
                                        int currentWindowHeight = com.google.gwt.user.client.Window.getClientHeight();
                                        String currentURL = com.google.gwt.user.client.Window.Location.getHref();

                                        com.google.gwt.user.client.Window.open(currentURL, "", "width=" + currentWindowWidth + ",height=" + currentWindowHeight + "");
                                        closeCurrentWindowNative();

                                    }
                                    else
                                        t[0].schedule(1000*60*60);

                                }

                            });



                        }


                    };
                    t[0].schedule(1000*60*60);

                }
            }
            //TODO FIXING FUCKING IE10 RESIZE BUG!!!!!
        }


        //full restart timer


        toolStrip.addFill();
        toolStrip.addSeparator();


        {
            ToolStripButton basketButton = new ToolStripButton();
            basketButton.setIcon("stripp/basket_delete.png");
            basketButton.setPrompt(AppConst.BASKET_TOOL_BAR_HEADER);
            toolStrip.addButton(basketButton);

            basketButton.setCanAcceptDrop(true);

            basketButton.addDropHandler(new DropHandler() {
                @Override
                public void onDrop(DropEvent event) {

                    Canvas dragTarget = EventHandler.getDragTarget();
                    if (dragTarget instanceof ToolStripButton) {
                        if (getNumPages() > 1) {
                            ToolStripButton bt = ((ToolStripButton) dragTarget);
                            Set<Integer> pageNums = notSwitchToolsCanvas.keySet();

                            br:
                            {
                                for (Integer pageNum : pageNums) {
                                    Canvas[] canvases = notSwitchToolsCanvas.get(pageNum);
                                    for (Canvas canvas : canvases) {
                                        if (canvas == bt) {
                                            destroyPage(pageNum);
                                            break br;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    event.cancel();
                }
            });
        }
        {
            ToolStripButton profileButton = new ToolStripButton();
            profileButton.setIcon("stripp/vcard_edit.png");
            profileButton.setPrompt(AppConst.PROFILE_TOOL_BAR_HEADER);
            toolStrip.addButton(profileButton);
            profileButton.addClickHandler(new ClickHandler() {
                boolean isProfileAllow = true;

                @Override
                public void onClick(final ClickEvent event) {


//                    DescOperation testDesc = new DescOperation();
//                    testDesc.apiName="DDDDDD" ;
//                    testDesc.put("XXXX", "HHHHH");
//                    testDesc.put("XXXX2",null);
//                    testDesc.put("XXXX3", 1);
//                    testDesc.put("X4", 8.8);
//                    DescOperation e = new DescOperation();
//                    testDesc.getSubOperation().add(e);
//                    e.put("XXXX", "HHHHH223");
//                    e.put("XXXX2",null);
//                    e.put("XXXX3", 1);
//                    e.put("XXXX4", 3.14);
//
//                    guiStateDesc.getBuildTreeService().testSerializer(DefaultProfiles.defProfile, testDesc, new AsyncCallback<DescOperation>() //Проверка передачи сложный объектов через канал - работает

                    final GUIStateDesc guiStateDesc = App01.GUI_STATE_DESC;

                    //ЗАПРЕТИТЬ РЕЕНТЕРАБЕЛЬНОСТЬ ДИАЛОГА ПРОФИЛЕЙ!!!!
                    if (isProfileAllow) {

                        isProfileAllow = false;
                        if (!new_profiles) {
                            guiStateDesc.getBuildTreeService().getProfiles(currentUserId, new AsyncCallback<UserProfile[]>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    SC.warn(caught.getMessage());
                                    isProfileAllow = true;
                                }

                                @Override
                                public void onSuccess(UserProfile[] inBaseProfiles) {
                                    try {
                                        if (inBaseProfiles == null)
                                            inBaseProfiles = new UserProfile[0];

                                        if (currentProfile == null) {
                                            if (inBaseProfiles.length > 0)
                                                currentProfile = new UserProfile(inBaseProfiles[0]);
                                            else
                                                currentProfile = new UserProfile(DefaultProfiles.defProfile);
                                        }
                                        Window wnd = createProfileForm(inBaseProfiles, guiStateDesc, event.isAltKeyDown() && event.isCtrlKeyDown());
                                        wnd.show();

                                    } finally {

                                        isProfileAllow = true;
                                    }
                                }
                            });
                        } else {
                            guiStateDesc.getBuildTreeService().getProfiles_new(new BigDecimal("" + user.getIdUser()), new AsyncCallback<UserProfile[]>() {
                                @Override
                                public void onFailure(Throwable caught) {
                                    SC.warn(caught.getMessage());
                                    isProfileAllow = true;
                                }

                                @Override
                                public void onSuccess(UserProfile[] inBaseProfiles) {
                                    try {
                                        if (inBaseProfiles == null)
                                            inBaseProfiles = new UserProfile[0];

                                        if (currentProfile == null) {
                                            if (inBaseProfiles.length > 0)
                                                currentProfile = new UserProfile(inBaseProfiles[0]);
                                            else
                                                currentProfile = new UserProfile(DefaultProfiles.defProfile);
                                        }
                                        Window wnd = createProfileForm(inBaseProfiles, guiStateDesc, event.isAltKeyDown() && event.isCtrlKeyDown());
                                        wnd.show();
                                    } finally {
                                        isProfileAllow = true;
                                    }
                                }
                            });


                        }

                    }

//                        @Override
//                        public void onSuccess(DescOperation result) {
//
//                            String res = result.apiName;
//
//                            DescOperation des1 = (DescOperation) (result.getSubOperation().get(0));
//                            Object xxxxx4 = des1.get("XXXX4");
//                            SC.say((xxxxx4 != null) ? xxxxx4.toString() : "NULL");
//                        }
//                    SC.say("Профиль пользователя пока не реализован");
                }
            });

        }

        {
            ToolStripButton hideButton = new ToolStripButton();
            hideButton.setIcon("stripp/arrow_left.png");
            hideButton.setPrompt(AppConst.THIN_TOOL_BAR_HEADER);
            toolStrip.addButton(hideButton);

            hideButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {

//                    toolStrip.animateHide(toolStrip, AnimationEffect.SLIDE, "R", "L",
//                            new AnimationCallback() {
//                                @Override
//                                public void execute(boolean earlyFinish) {
//                                    thinStripCtrl.setVisible(true);
//                                }
//                            }
//                    );

                    toolStrip.setVisible(false);
                    thinStripCtrl.setVisible(true);

                }
            });
        }


        thinStripCtrl.setVisible(false);
        return thinStripCtrl;
    }


    public MyToolStrip makeToolStrip() {
        final MyToolStrip toolStrip = new MyToolStrip();
        toolStrip.setVertical(true);
        toolStrip.setHeight100();
        toolStrip.setWidth(30);
        toolStrip.setCanAcceptDrop(true);

        this.setToolStrip(toolStrip);

        return toolStrip;
    }


    public TreeGrid getMainTreeGrid() {
        return mainTreeGrid;
    }

    public void setMainTreeGrid(TreeGrid mainTreeGrid) {
        this.mainTreeGrid = mainTreeGrid;
    }

    private TreeGrid mainTreeGrid;

    protected UserProfile currentProfile;//= new UserProfile(DefaultProfiles.defProfile);

    public UserProfile getCurrentProfile() {
        return currentProfile;
    }

    public void setCurrentProfile(UserProfile currentProfile) {
        this.currentProfile = currentProfile;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    private String currentUserId = DefaultProfiles.DEF_USER_ID;

    private native static void openWindow(String url, String winName, String options) /*-{
        var w = window.open(url, winName, options);
        w.focus();
    }-*/;


    private Window createProfileForm(final UserProfile[] inBaseProfiles, final GUIStateDesc guiStateDesc, final boolean isOpenDescShow) {

        final LinkedHashMap<Integer, String> profiles = new LinkedHashMap<Integer, String>();
        for (UserProfile inBaseProfile : inBaseProfiles)
            profiles.put(inBaseProfile.getProfileId(), inBaseProfile.getProfileName());


        final Window winModal = OptionsViewers.createEmptyWindow(AppConst.CUSTOM_FILTER_OPTIONS_HEADER);
        winModal.setAutoSize(true);


        if (user != null)
            winModal.setTitle("Пользователь: " + user.getFirstName() + " " + user.getLastName() + " Загруженный профиль:" + currentProfile.getProfileName());
        else
            winModal.setTitle(currentProfile.getProfileName());

        final DynamicForm form = OptionsViewers.createEmptyForm();
        form.setLayoutAlign(VerticalAlignment.TOP);
        form.setNumCols(2);
        form.setPadding(10);
        ///form.setAutoWidth();

//        final TextItem textName = new TextItem();
//        textName.setTitle("Имя пользователя");
//        textName.setValue(DefaultProfiles.DEF_USER_ID);

        final ComboBoxItem selectProfileName = new ComboBoxItem();
        selectProfileName.setTitle("Имя профиля");
        selectProfileName.setType("comboBox");
//        selectProfileName.setColSpan(4);
        selectProfileName.setAlign(Alignment.CENTER);
        selectProfileName.setValueMap(profiles);
        if (profiles.get(currentProfile.getProfileId()) != null)
            selectProfileName.setDefaultValue(currentProfile.getProfileId());
        else
            selectProfileName.setDefaultValue(currentProfile.getProfileName());

        final CheckboxItem desktopInformersProfile = new CheckboxItem();
        desktopInformersProfile.setTitle("Профиль с информерами для рабочего стола");
        desktopInformersProfile.setValue(currentProfile.isDesktopInformers());

        final TextAreaItem textAreaItem = new TextAreaItem();
//        textAreaItem.setColSpan(4);
        final String wasValue = encodeOpenDescriptor();
        textAreaItem.setValue(wasValue);
        textAreaItem.setTitle("Интеграция");

        final IButton saveButton = new IButton(AppConst.SAVE_BUTTON_OPTIONS);
        ClickHandler saveHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent _event) {
                final DescOperation descOperation = guiStateDesc.getProcessor().buildDescriptorFromApp(guiStateDesc);
                descOperation.put(OPEN_DESCRIPTOR_ATTR_NAME, encodeOpenDescriptor());
                if (isOpenDescShow) {
                    String _openDescriptors = textAreaItem.getValueAsString();
                    if (_openDescriptors == null)
                        _openDescriptors = "";
                    if (!_openDescriptors.equals(wasValue))
                        decodeOpenDescriptor(_openDescriptors);
                    descOperation.put(OPEN_DESCRIPTOR_ATTR_NAME, encodeOpenDescriptor());
                }

                Object profileId = selectProfileName.getValue();
                UserProfile saveProfile = new UserProfile();
                if (profileId instanceof String) {
                    saveProfile.setProfileName((String) profileId);
                    saveProfile.setProfileId(-1);
                } else {
                    saveProfile.setProfileName(profiles.get(profileId));
                    saveProfile.setProfileId((Integer) profileId);
                }
                saveProfile.setDesktopInformers(desktopInformersProfile.getValueAsBoolean());
                if (!new_profiles) {
                    guiStateDesc.getBuildTreeService().saveNodes(currentUserId, saveProfile, descOperation, new AsyncCallback<UserProfile>() //Проверка передачи сложный объектов через канал - работает
                    {
                        @Override
                        public void onFailure(Throwable caught) {
                            SC.warn(caught.getMessage());
                            winModal.destroy();
                        }

                        @Override
                        public void onSuccess(UserProfile result) {
                            currentProfile = new UserProfile(result);
                            winModal.destroy();//TODO Надо возвращать идентифкатор профиля
                        }
                    });
                } else {
                    guiStateDesc.getBuildTreeService().saveNodes_new(new BigDecimal("" + user.getIdUser()), saveProfile, descOperation, new AsyncCallback<UserProfile>() //Проверка передачи сложный объектов через канал - работает
                    {
                        @Override
                        public void onFailure(Throwable caught) {
                            SC.warn(caught.getMessage());
                            winModal.destroy();
                        }

                        @Override
                        public void onSuccess(UserProfile result) {
                            currentProfile = new UserProfile(result);
                            com.google.gwt.user.client.Window.setTitle(currentProfile.getProfileName() + "-" + App01.DEFAULT_TITLE);
                            winModal.destroy();//TODO Надо возвращать идентифкатор профиля
                        }
                    });


                }

            }
        };

        saveButton.addClickHandler
                (
                        saveHandler
                );


        final IButton loadButton = new IButton(AppConst.LOAD_BUTTON_OPTIONS);
        loadButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Object profileId = selectProfileName.getValue();
                Boolean isDesk = desktopInformersProfile.getValueAsBoolean();
                if (profileId instanceof Integer) {
                    //Загружаем новый профиль
                    winModal.destroy();

                    currentProfile = new UserProfile((Integer) profileId, profiles.get(profileId));
                    currentProfile.setDesktopInformers(isDesk);
                    unloadListenerCtrl.clickIndex(GUIStateDesc.this);

                    app01.unLoad();
                    app01.reRunApp();
                    // Сохранить настройки последнего загруженного профиля


                } else {
                    SC.say("Профиль еще не создан, загрузка невозможна");
                }
            }
        });


        final IButton delButton = new IButton(AppConst.DEL_BUTTON_OPTIONS);
        delButton.setDisabled(true);
        delButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {


                Object profileId = selectProfileName.getValue();
                if (profileId != null && profileId instanceof Integer) {
                    if (!new_profiles) {
                        guiStateDesc.getBuildTreeService().deleteProfile(new UserProfile((Integer) profileId, profiles.get(profileId)), new AsyncCallback<Void>() //Проверка передачи сложный объектов через канал - работает
                        {
                            @Override
                            public void onFailure(Throwable caught) {
                                SC.warn(caught.getMessage());
                                winModal.destroy();
                            }

                            @Override
                            public void onSuccess(Void result) {
                                winModal.destroy();
                            }
                        });
                    } else {
                        guiStateDesc.getBuildTreeService().deleteProfile_new(new UserProfile((Integer) profileId, profiles.get(profileId)), new AsyncCallback<Void>() //Проверка передачи сложный объектов через канал - работает
                        {
                            @Override
                            public void onFailure(Throwable caught) {
                                SC.warn(caught.getMessage());
                                winModal.destroy();
                            }

                            @Override
                            public void onSuccess(Void result) {
                                winModal.destroy();
                            }
                        });

                    }
                }
            }
        });

        selectProfileName.addChangedHandler(new ChangedHandler() {
            @Override
            public void onChanged(com.smartgwt.client.widgets.form.fields.events.ChangedEvent event) {

                Object val = selectProfileName.getValue();
                boolean disabled = (val == null || (val instanceof String)
                        ||
                        (currentProfile.getProfileId() == ((Integer) val) || ((Integer) val) == DefaultProfiles.defProfile.getProfileId()));

                delButton.setDisabled(disabled);
                loadButton.setDisabled((val == null) || (val instanceof String));
                saveButton.setDisabled((val == null));
                boolean dip = false;
                if (!(val instanceof String)) {
                    UserProfile up = findUserProfileById((Integer) val);
                    if (up != null)
                        dip = up.isDesktopInformers();
                } else {
                    dip = currentProfile.isDesktopInformers();

                }
                desktopInformersProfile.setValue(dip);
            }

            public UserProfile findUserProfileById(int userProfile) {
                for (int i = 0; inBaseProfiles != null && i < inBaseProfiles.length; i++) {
                    if (inBaseProfiles[i].getProfileId() == userProfile) return inBaseProfiles[i];


                }
                return null;


            }

        });


        desktopInformersProfile.addChangedHandler(new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                //  Object val=selectProfileName.getValue();
                Boolean isDesk = desktopInformersProfile.getValueAsBoolean();
                //  if (!(val instanceof String)) {
                //     UserProfile up = findUserProfileById((Integer)val);
                //     if (up!=null)
                //          up.setDesktopInformers(isDesk);
                //      else
                currentProfile.setDesktopInformers(isDesk);
                // }

            }


        });

        final IButton cancelButton = new IButton(AppConst.CANCEL_BUTTON_OPTIONS);
        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                winModal.destroy();
            }
        });

        if (isOpenDescShow)
            form.setFields(selectProfileName,/* desktopInformersProfile,*/ textAreaItem);//,textName);
        else
            form.setFields(selectProfileName/*, desktopInformersProfile*/);


        HLayout hLayout = new HLayout();
        hLayout.addMembers(loadButton, saveButton, delButton, cancelButton);
        hLayout.setAlign(Alignment.RIGHT);
        hLayout.setAutoHeight();

        VLayout vl = new VLayout();
        vl.addMember(form);
        vl.setIsGroup(true);
        vl.setGroupTitle("Профили");
        vl.setPadding(5);
        vl.setMargin(5);
        vl.addMember(hLayout);

        vl.addDrawHandler(new DrawHandler() {
            @Override
            public void onDraw(DrawEvent event) {
                if (isOpenDescShow) {
                    selectProfileName.setWidth(winModal.getWidth());
                    textAreaItem.setWidth(winModal.getWidth());
                    desktopInformersProfile.setWidth(winModal.getWidth());
                }
            }
        });

//        final TabSet topTabSet = new TabSet();
//        topTabSet.setTabBarPosition(Side.TOP);
//        topTabSet.setTabBarAlign(Side.LEFT);
//
//        topTabSet.setWidth100();
//        topTabSet.setHeight100();
//        Tab profileTab = new Tab("Профиль");
//        topTabSet.addTab(profileTab);
//        {
//            VLayout vLayout = new VLayout();
//            vLayout.addMember(vl);
//            vLayout.addMember(changeUserForm);
//            vLayout.setWidth100();
//            vLayout.setHeight100();
//            profileTab.setPane(vLayout);
//        }
//        winModal.addItem(topTabSet);

        winModal.addItem(vl);
        winModal.addItem(createChangeUserForm(winModal));

        //winModal.addItem(createIntegrationForm(winModal,saveHandler));


        return winModal;


    }

    public static final String DEF_OPEN_CMD = "left=100,top=100,width=500,height=500,scrollbars=yes,resizable=yes";
    public static final String DEF_OPEN_DESCRIPTOR = "[" +
            "{type:'" + TablesTypes.WINDOWS + "',winName:'Карточка Окна',mode:'popUp',params:''}," +
//            "{type:'" + TablesTypes.WINDOWS_CURR + "',winName:'Карточка Окна',mode:'popUp',params:''}," +
//            "{type:'" + TablesTypes.WINDOWS_OVERTIME + "',winName:'Карточка Окна',mode:'popUp',params:''}," +
            "{type:'" + TablesTypes.WARNINGS + "',winName:'Карточка Предупреждения',mode:'popUp',params:''}," +
            "{type:'" + TablesTypes.REFUSES + "',winName:'',params:'left=10,top=10,width=500,height=500,scrollbars=yes,resizable=yes'}," +
            "{type:'" + TablesTypes.VIOLATIONS + "',winName:'',params:'left=10,top=10,width=500,height=500,scrollbars=yes,resizable=yes'}," +
//            "{type:'" + TablesTypes.ZMTABLE + "',winName:'',params:'left=10,top=10,width=1000,height=700,scrollbars=yes,resizable=yes'}," +
            "{type:'" + TablesTypes.ZMTABLE + "',winName:'Карточка ЗМ',mode:'popUp',params:''}," +
            "{type:'all',winName:'',params:'" + DEF_OPEN_CMD +
            "'}]";
    public static final String DEF_WIN_NAME = "";

    public void openUrl(String url, String tableType) {
        if (url != null) {
//            url="http://www.google.ru";

            if (tableType == null)
                tableType = "all";

            JavaScriptObject obj = openDescriptors.get(tableType);
            if (obj == null)
                obj = openDescriptors.get("all");
            if (obj == null)
                openWindow(url, DEF_WIN_NAME, DEF_OPEN_CMD);
            else {
                String winName = JSOHelper.getAttribute(obj, "winName");
                String params = JSOHelper.getAttribute(obj, "params");
                String mode = JSOHelper.getAttribute(obj, "mode");
                if ("popUp".equalsIgnoreCase(mode)) {
                    int h = com.google.gwt.user.client.Window.getClientHeight();
                    int w = com.google.gwt.user.client.Window.getClientWidth();

                    Window win = new Window();
                    win.setTitle(winName);
                    win.setCanDragResize(true);
                    final int wh = (int) Math.max(h * 0.9, 100);
                    win.setHeight(wh);
                    final int ww = (int) Math.max(w * 0.9, 100);
                    win.setWidth(ww);
                    win.setTop(Math.max((h - wh) / 2, 0));
                    win.setLeft(Math.max((w - ww) / 2, 0));


                    HTMLFlow htmlFlow = new HTMLFlow();
                    htmlFlow.setContentsType(ContentsType.PAGE);
                    htmlFlow.setContentsURL(url);
                    htmlFlow.setHeight100();
                    htmlFlow.setWidth100();
                    win.addItem(htmlFlow);
                    win.show();
                } else {
                    openWindow(url, winName, params);
                }
            }
        }
    }

    private Map<String, JavaScriptObject> openDescriptors = new HashMap<String, JavaScriptObject>();

    {
        decodeOpenDescriptor(DEF_OPEN_DESCRIPTOR);
    }

//    private DynamicForm createIntegrationForm(final Window winModal,final ClickHandler saveHandler)
//    {
//        final DynamicForm integrationForm = new DynamicForm();
//        integrationForm.setIsGroup(true);
//        integrationForm.setPadding(5);
//        integrationForm.setMargin(5);
//        integrationForm.setGroupTitle("Интеграция");
//        integrationForm.setNumCols(2);
//        integrationForm.setWidth(420);
//        integrationForm.setAlign(Alignment.CENTER);
//
//        final TextAreaItem textAreaItem = new TextAreaItem();
//        textAreaItem.setColSpan(2);
//
//
//        textAreaItem.setValue(encodeOpenDescriptor());
//
//        textAreaItem.setTitle("Открытие окон");
//        final ButtonItem apply = new ButtonItem("Применить");
//        apply.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
//            @Override
//            public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
//
//                {
//                    String _openDescriptors = textAreaItem.getValueAsString();
//                    decodeOpenDescriptor(_openDescriptors);
//                    saveHandler.onClick(null);
//                }
//            }
//        });
//
//        final ButtonItem cancel = new ButtonItem("Отмена");
//        cancel.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler()
//        {
//            @Override
//            public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
//                winModal.destroy();
//            }
//        });
//
//
//        integrationForm.setFields(textAreaItem,apply,cancel);
//
//        return integrationForm;
//    }

    public String encodeOpenDescriptor() {
        Collection<JavaScriptObject> values = openDescriptors.values();
        JavaScriptObject[] _array = values.toArray(new JavaScriptObject[values.size()]);
        JavaScriptObject array = JSOHelper.arrayConvert(_array);
        return JSON.encode(array);
    }

    public void decodeOpenDescriptor(String _openDescriptors) {
        try {
            JavaScriptObject objs = JSON.decode(_openDescriptors);
            boolean isArray = JSOHelper.isArray(objs);
            if (isArray) {
                int len = JSOHelper.getArrayLength(objs);
                for (int i = 0; i < len; i++) {
                    JavaScriptObject object = JSOHelper.getJSOArrayValue(objs, i);
                    String type = JSOHelper.getAttribute(object, "type");
                    openDescriptors.put(type, object);
                }
            }
        } catch (Exception e) {
            SC.say("Ошибка трансляции вызовов смежных систем");
        }
    }

    private DynamicForm createChangeUserForm(final Window winModal) {
        final IAuthorizerListener call = this;
        final ButtonItem changeUserButton = new ButtonItem("chuser");
        changeUserButton.setTitle(AppConst.CHANGE_USER_OPTIONS);
        changeUserButton.setColSpan(2);
        changeUserButton.setAlign(Alignment.CENTER);
        final CheckboxItem keepProfile = new CheckboxItem("keepProfile");
        final GUIStateDesc currentState = this;
        changeUserButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
            @Override
            public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
                if (!keepProfile.getValueAsBoolean()) {
                    currentProfile = null;
                } else {
                    changeUserProfile = getProcessor().buildDescriptorFromApp(currentState);
                    currentProfile.setProfileId(-1);
                }

                winModal.destroy();
                Cookies.removeCookie("user");
                user = null;


                new Authorizer().authorize(call, true);
            }
        });


        final DynamicForm changeUserForm = new DynamicForm();
        changeUserForm.setIsGroup(true);
        changeUserForm.setPadding(5);
        changeUserForm.setMargin(5);
        changeUserForm.setGroupTitle("Пользователь");
        changeUserForm.setNumCols(2);
        changeUserForm.setWidth(420);
        changeUserForm.setAlign(Alignment.CENTER);
        //  changeUserForm.setColWidths(120, "*");
        //  changeUserForm.setLayoutAlign(Alignment.CENTER);
        //changeUserForm.setAutoWidth();
        keepProfile.setTitle("Без изменения настроек профиля");
        keepProfile.setColSpan(2);
        keepProfile.setValue(false);
        keepProfile.setShowTitle(false);
        keepProfile.setAlign(Alignment.LEFT);
        changeUserForm.setFields(keepProfile, changeUserButton);
        return changeUserForm;
    }

    protected App01 app01;

    Integer checkVersionRes;

    public void loadDescTop(final App01 app01) //TODO ВВести интерфейс приложения для управления загрузкой и выгрузкой
    {
        checkVersionRes=null;
        getBuildTreeService().checkVersion(TablesTypes.VERSION,new AsyncCallback<String>()
        {

            @Override
            public void onFailure(Throwable caught) {
                SC.warn("Ошибка при проверке версии, проверьте доступность сервера и нажмите F5 для перезагрузки приложения: "+caught.getMessage());
                checkVersionRes=-1;
            }

            @Override
            public void onSuccess(String result)
            {
                if (TablesTypes.VERSION.equals(result))
                    checkVersionRes=1;
                else
                {
                    SC.warn("Тек. версия приложения: "+TablesTypes.VERSION+", требуемая версия приложения: "+result+",нажмите F5 для обновления версии приложения ");
                    checkVersionRes=0;
                }
            }
        });

        if (Integer.valueOf(1).equals(checkVersionRes))
            _loadDescTop(app01);
        else
            new PostponeOperationProvider(new PostponeOperationProvider.IPostponeOperation(){

                @Override
                public boolean operate()
                {
                    if (Integer.valueOf(1).equals(checkVersionRes))
                         _loadDescTop(app01);
                    return checkVersionRes!=null;
                }
            });

    }


    public void _loadDescTop(App01 app01) //TODO ВВести интерфейс приложения для управления загрузкой и выгрузкой
    {


        this.app01 = app01;
        if (app01.map != null && (app01.map.containsKey("isdesc") || app01.map.containsKey("informer")))
            applicationMode = DESKTOP;

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
            getBuildTreeService().getProfileIdToLoadFirst(new BigDecimal("" + user.getIdUser()), UserProfile.LAST_USED_PROFILE, new AsyncCallback<List<UserProfile>>() {
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

          /*  getBuildTreeService().getProfiles_new(new BigDecimal("" + user.getIdUser())
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

        }
        if (changeUserProfile != null) {
            applyDescOperation(changeUserProfile);
            changeUserProfile = null;
            return;
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

                            getBuildTreeService().getNodes_new(new BigDecimal("" + getUser().getIdUser()), getCurrentProfile(), new AsyncCallback<DescOperation>() //Проверка передачи сложный объектов через канал - работает
                            {
                                @Override
                                public void onFailure(Throwable caught) {
                                    SC.warn(caught.getMessage());
                                }

                                @Override
                                public void onSuccess(DescOperation result) {
                                    applyDescOperation(result);
//                                    if (result != null)
//                                    {
//                                        MainProcessor processor = getProcessor();
//                                        List<OperationHolder> operationHolder = processor.preProcessAll(result.getSubOperation());
//                                        processor.operateIt(null, operationHolder);//TODO Здесь возможно передавать ссылку на абстрагированное приложение
//                                        mainLayout.markForRedraw();
//                                    }
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

    protected void applyDescOperation(DescOperation result) {
        if (result != null) {
            MainProcessor processor = getProcessor();
            String _openDescriptor = (String) result.get(OPEN_DESCRIPTOR_ATTR_NAME);
            if (_openDescriptor != null)
                decodeOpenDescriptor(_openDescriptor);
            else
                decodeOpenDescriptor(DEF_OPEN_DESCRIPTOR);
            List<OperationHolder> operationHolder = processor.preProcessAll(result.getSubOperation());
            processor.operateIt(null, operationHolder, null);//TODO Здесь возможно передавать ссылку на абстрагированное приложение
            mainLayout.markForRedraw();
        }
    }

    @Override
    public void onAuthorized() {
        unloadListenerCtrl.clickIndex(GUIStateDesc.this);

        app01.unLoad();
        app01.reRunApp();
    }
}
