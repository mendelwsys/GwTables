var key = '';
if (!global.first) {
    global.gui = require('nw.gui');
    global.dataUrl = '';
    global.map = new Map;
    global.minimized = new Map;
    global.maximized = new Map;
    global.originalheights = new Map;
    global.originalwidths = new Map;
    global.closed = new Map;
    global.titles = new Map;
    global.windows = 1;
    global.menu = new global.gui.Menu();
    global.tray = new global.gui.Tray({
        title: 'Tray',
        icon: 'img/target.png'
    });
    global.minimizeMenuIndex = 2;
    global.maximizeMenuIndex = 3;
    global.closeMenuIndex = 1;
    global.openMenuIndex = 0;
}
var window2 = global.gui.Window.get();
if (!window2.title) {
    key = global.title;
} else {
    key = window2.title;
}
function init() {
    global.gui.App.clearCache();
    global.mainWindowId = key;
    function minimize() {
        var win = global.gui.Window.get();
        win.minimize();
    }

    function restore() {
        var win = global.gui.Window.get();
        win.restore();
    }

    function disableWarning() {
        var win = global.gui.Window.get();
        win.setShowInTaskbar(false);
        win.requestAttention(false);
    }

    var win = global.gui.Window.get();
    global.map.set(key, win);
    global.maximized.set(key, win);
    win.on("close", function () {
        win.cookies.remove({
            url: "app://fake",
            name: Math.random()
        }, function () {
        });
    })
    var m12 = new global.gui.MenuItem({
        label: 'Закрыть',
        enabled: false
    });
    global.menu.append(m12);
    var m14 = new global.gui.MenuItem({
        label: 'Открыть',
        enabled: false
    });
    global.menu.append(m14);
    var m1 = new global.gui.MenuItem({
        label: 'Свернуть',
        enabled: false
    })
    global.menu.append(m1);
    var m2 = new global.gui.MenuItem({
        label: 'Развернуть',
        enabled: false
    })
    global.menu.append(m2);
    var m40 = new global.gui.MenuItem({
        label: 'Сменить профиль...'
    })
    m40.click = function () {
        changeProfile();
    };
    global.menu.append(m40);
    var m10 = new global.gui.MenuItem({
        label: 'Выйти'
    })
    m10.click = function () {
        setAllWindowsAlwaysOnTopSign(false);

        var ret = global.gui.Window.get().window.confirm("Сохранить профиль?");
        if (ret === true) {
            saveProfile();
        }
        setAllWindowsAlwaysOnTopSign(true);
        global.gui.App.quit();
    };
    global.menu.append(m10);
    global.tray.menu = global.menu;
    function closeWindow() {
        window.close();
    }

    function updateImageUrl(image_id, new_image_url) {
        var image = document.getElementById(image_id);
        if (image)
            image.src = new_image_url;
    }

    function createImage(image_id, image_url) {
        var image = document.createElement("img");
        image.setAttribute("id", image_id);
        image.src = image_url;
        return image;
    }

    function createButton(button_id, button_name, normal_image_url,
                          hover_image_url, click_func) {
        var button = document.createElement("div");
        button.setAttribute("class", button_name);
        var button_img = createImage(button_id, normal_image_url);
        button.appendChild(button_img);
        button.onmouseover = function () {
            updateImageUrl(button_id, hover_image_url);
        }
        button.onmouseout = function () {
            updateImageUrl(button_id, normal_image_url);
        }
        button.onclick = click_func;
        return button;
    }

    function focusTitlebars(focus) {
        var bg_color = focus ? "#3a3d3d" : "#7a7c7c";

        var titlebar = document.getElementById("top-titlebar");
        if (titlebar)
            titlebar.style.backgroundColor = bg_color;
        titlebar = document.getElementById("bottom-titlebar");
        if (titlebar)
            titlebar.style.backgroundColor = bg_color;
        titlebar = document.getElementById("left-titlebar");
        if (titlebar)
            titlebar.style.backgroundColor = bg_color;
        titlebar = document.getElementById("right-titlebar");
        if (titlebar)
            titlebar.style.backgroundColor = bg_color;
    }

    function addTitlebar(titlebar_name, titlebar_icon_url, titlebar_text) {
        var titlebar = document.createElement("div");
        titlebar.setAttribute("id", titlebar_name);
        titlebar.setAttribute("class", titlebar_name);
        var title = document.createElement("div");
        title.setAttribute("class", titlebar_name + "-text");
        title.innerText = titlebar_text;
        titlebar.appendChild(title);
        var closeButton = createButton(titlebar_name + "-close-button",
            titlebar_name + "-close-button",
            "img/button_close.png",
            "img/button_close_hover.png",
            closeWindow);
        titlebar.appendChild(closeButton);
        var divider = document.createElement("div");
        divider.setAttribute("class", titlebar_name + "-divider");
        titlebar.appendChild(divider);
        document.body.appendChild(titlebar);
    }

    function removeTitlebar(titlebar_name) {
        var titlebar = document.getElementById(titlebar_name);
        if (titlebar)
            document.body.removeChild(titlebar);
    }

    function updateContentStyle() {
        var content = document.getElementById("content");
        if (!content)
            return;
        var left = 0;
        var top = 0;
        var width = window.outerWidth;
        var height = window.outerHeight;
        var titlebar = document.getElementById("top-titlebar");
        if (titlebar) {
            height -= titlebar.offsetHeight;
            top += titlebar.offsetHeight;
        }
        titlebar = document.getElementById("bottom-titlebar");
        if (titlebar) {
            height -= titlebar.offsetHeight;
        }
        titlebar = document.getElementById("left-titlebar");
        if (titlebar) {
            width -= titlebar.offsetWidth;
            left += titlebar.offsetWidth;
        }
        titlebar = document.getElementById("right-titlebar");
        if (titlebar) {
            width -= titlebar.offsetWidth;
        }
        var contentStyle = "position: absolute; ";
        contentStyle += "left: " + left + "px; ";
        contentStyle += "top: " + top + "px; ";
        contentStyle += "width: " + width + "px; ";
        contentStyle += "height: " + height + "px; ";
        content.setAttribute("style", contentStyle);
    }
}
function warning(win, winid, title, text) {
    $.when(notify(winid, title, text)).done(function () {
        win.setShowInTaskbar(true);
        win.requestAttention(true);
        for (var i = 0; i < 10; i++) {
            win.setTransparent(!win.isTransparent);
            sleep(1000);
        }
    });
}
function notify(win, title, text) {
    var ititle = title;
    var itext = text;
    return $.Deferred(function () {
        var self = this;
        var options = {
            icon: "img/warning.png",
            body: text
        };
        var win_title = "";
        if (global.titles.get(win))
            win_title = win_title + global.titles.get(win) + " : ";
        var notification = new Notification(win_title + title, options);
        notification.onshow = function () {
            self.resolve();
        }
    });
}

function changeProfile() {
    global.map.forEach(function (item, key, mapObj) {
        if (("" + global.mainWindowId) != ("" + key)) {
            global.map.delete(key);
            if (global.minimized.get(key)) {
                global.minimized.delete(key);
            }
            if (global.maximized.get(key)) {
                global.maximized.delete(key);
            }
            item.close(true);
        } else {
            if (global.minimized.get(key)) {
                item.restore();
                global.maximized.delete(key);
                global.maximized.set(key, item);
            }
            item.cookies.remove({
                url: global.baseUrl,
                name: 'user'
            });
            item.reloadIgnoringCache();
            setWindowSize(key, 200, 300);
        }
    });
    //Затем закрытые
    global.closed.forEach(function (item, key, mapObj) {
        if (("" + global.mainWindowId) != ("" + key)) {
            global.closed.delete(key);
            if (global.minimized.get(key)) {
                global.minimized.delete(key);
            }
            if (global.maximized.get(key)) {
                global.maximized.delete(key);
            }
            item.close(true);
        } else {
            global.closed.delete(key);
            global.map.set(key, item);
            if (global.minimized.get(key)) {
                global.minimized.delete(key);

}
            global.maximized.set(key, item);

            item.cookies.remove({
                url: global.baseUrl,
                name: 'user'
            });
            item.restore();
            item.reloadIgnoringCache();
            setWindowSize(key, 250, 300);
        }
    });
    updateAllMenus();

}
function alarmSoft(windowId, title, text, mode) {

	if (global.closed.get(windowId))
		return;
	var windowObject = global.map.get(windowId);
	if (windowObject)
{		
notify(windowId, title, text);
}
}
function alarmHard(windowId, title, text, mode) {
    if (global.closed.get(windowId))
        return;
    if (global.minimized.get(windowId)) {
        var w = global.minimized.get(windowId);
        if (w) {
            global.minimized.delete(windowId);
            global.maximized.set(windowId, w);
            w.restore();
        }
    }
    var windowObject = global.map.get(windowId);
warning(windowObject, windowId, title, text);
    updateAllMenus();
}
function warn(windowId, title, text, mode) {
if (mode && mode === true)
        alarmHard(windowId, title, text);
    else
        alarmSoft(windowId, title, text);
}
function sleep(ms) {
    ms += new Date().getTime();
    while (new Date() < ms) {
    }
}
function updateSubmenusOnClose() {
    var submenu = new global.gui.Menu();
    if (global.map.size > 0) {
        var m3 = new global.gui.MenuItem({
            label: "Закрыть все"
        });
        m3.click = function () {
            global.map.forEach(function (item, key, mapObj) {
                onClose(key, item);
            });
        };
        submenu.append(m3);
    }
    global.map.forEach(function (item, key, mapObj) {
        var m = new global.gui.MenuItem({
            label: global.titles.get(key)
        });
        var iitem = item;
        var ikey = key;
        m.click = function () {
            onClose(ikey, iitem);
        };
        submenu.append(m);
    });
    var menit = global.menu.items[global.openMenuIndex];
    menit.submenu = submenu;
    if (submenu.items.length > 0) {
        menit.submenu = submenu;
    } else {
        var newmenu2 = new global.gui.MenuItem({
            label: menit.label
        });
        menit = newmenu2;
    }
    if (global.map.size > 0) {
        menit.enabled = true;
    }
    else {
        menit.enabled = false;
    }
    global.menu.removeAt(global.openMenuIndex);
    global.menu.insert(menit, global.openMenuIndex);
}
function updateSubmenusOnOpen() {
    var submenu2 = new global.gui.Menu();
    if (global.closed.size > 0) {
        var m3 = new global.gui.MenuItem({
            label: "Открыть все"
        });
        m3.click = function () {
            global.closed.forEach(function (item, key, mapObj) {
                onOpen(key, item);
            });
        };
        submenu2.append(m3);
    }
    global.closed.forEach(function (item, key, mapObj) {
        var m = new global.gui.MenuItem({
            label: global.titles.get(key)
        });
        var iitem = item;
        var ikey = key;
        m.click = function () {
            onOpen(ikey, iitem);
        };
        submenu2.append(m);
    });
    var menit2 = global.menu.items[global.closeMenuIndex];
    if (submenu2.items.length > 0) {
        menit2.submenu = submenu2;
    } else {
        var newmenu2 = new global.gui.MenuItem({
            label: menit2.label
        });
        menit2 = newmenu2;
    }
    if (global.closed.size > 0) {
        menit2.enabled = true;
    }
    else {
        menit2.enabled = false;
    }
    global.menu.removeAt(global.closeMenuIndex);
    global.menu.insert(menit2, global.closeMenuIndex);
}
function updateAllMenus() {
    updateSubmenusOnClose();
    updateSubmenusOnOpen();
    updateSubmenusOnMinimizeMaximize();
}
function updateSubmenusOnMinimizeMaximize() {
    var submenuminimize = new global.gui.Menu();
    var submenumaximize = new global.gui.Menu();
    if (global.minimized.size > 0) {
        var m3 = new global.gui.MenuItem({
            label: "Развернуть все"
        });
        m3.click = function () {
            global.minimized.forEach(function (item, key, mapObj) {
                onMaximize(key, item);
            });
        };
        submenumaximize.append(m3);
    }
    global.minimized.forEach(function (item, key, mapObj) {
        var m = new global.gui.MenuItem({
            label: global.titles.get(key)
        });
        var iitem = item;
        var ikey = key;
        var imaximized = global.maximized;
        var iminimized = global.minimized;
        m.click = function () {
            onMaximize(ikey, iitem);
        };
        submenumaximize.append(m);
    });

    var menit = global.menu.items[global.maximizeMenuIndex];
    if (submenumaximize.items.length > 0)
        menit.submenu = submenumaximize;
    else {
        var newmenu = new global.gui.MenuItem({
            label: menit.label
        });
        menit = newmenu;
    }
    global.menu.removeAt(global.maximizeMenuIndex);
    global.menu.insert(menit, global.maximizeMenuIndex);
    if (global.minimized.size > 0) {
        menit.enabled = true;
    }
    else {
        menit.enabled = false;
    }
    if (global.maximized.size > 0) {
        var m4 = new global.gui.MenuItem({
            label: "Свернуть все"
        });
        m4.click = function () {
            global.maximized.forEach(function (item, key, mapObj) {
                onMinimize(key, item);
            });
        };
        submenuminimize.append(m4);
    }
    global.maximized.forEach(function (item, key, mapObj) {
        var m = new global.gui.MenuItem({
            label: global.titles.get(key)
        });
        var iitem = item;
        var ikey = key;
        m.click = function () {
            onMinimize(ikey, iitem);
        };
        submenuminimize.append(m);
    });
    var menit = global.menu.items[global.minimizeMenuIndex];
    if (submenuminimize.items.length > 0)
        menit.submenu = submenuminimize;
    else {
        var newmenu = new global.gui.MenuItem({
            label: menit.label
        });
        menit = newmenu;
    }
    global.menu.removeAt(global.minimizeMenuIndex);
    global.menu.insert(menit, global.minimizeMenuIndex);
    if (global.maximized.size > 0) {
        menit.enabled = true;
}
    else {
        menit.enabled = false;
    }
}
function onMinimize(ikey, iitem) {
    var dd = global.maximized.get(ikey);
    dd.minimize();
    global.maximized.delete(ikey);
    global.minimized.set(ikey, dd);
    updateSubmenusOnMinimizeMaximize();
}
function onMaximize(ikey, iitem) {
    var dd = global.minimized.get(ikey);
    dd.restore();
    global.minimized.delete(ikey);
    global.maximized.set(ikey, dd);
    setWindowSize(ikey, global.originalwidths.get(ikey), global.originalheights.get(ikey));
    updateSubmenusOnMinimizeMaximize();
}
function createNewWindow(id, x, y) {
    var options = {
        "show": true,
        "always-on-top": true,
        "frame": false,
        "height": 200,
        "visible": false,
        "as_desktop": false,
        // "min_width": 300,
        "fullscreen": false,
        "title": id,
        "mac_icon": "",
        "exe_icon": "img\\target.png",
        "width": 300,
        "resizable": false,
        "kiosk": false,
        "show_in_taskbar": false,
        "transparent": true,
        "toolbar": false,
        "icon": "img\\target.png",
        "kiosk_emulation": false,
        "position": ""
    };
    var new_win = global.gui.Window.open('index.html?id=' + id, options);
    global.map.set(id, new_win);
    global.maximized.set(id, new_win);
    new_win.on("loaded", function () {
        addWheelEvent(id, new_win);
        new_win.eval(null, '$(\'.top-titlebar-close-button\').on(\"click\",function(){onClose(\'' + id + '\',undefined)});');
        new_win.eval(null, '$(\'.top-titlebar-minimize-button\').on(\"click\",function(){onMinimize(\'' + id + '\',undefined)});');
        new_win.moveTo(x, y);
    });
}
function onClose(ikey, iitem) {
    var dd = global.map.get(ikey);
    if (global.map.get(ikey))
        global.map.delete(ikey);
    if (!dd) {
        dd = global.maximized.get(ikey);
    }
    if (!dd) {
        dd = global.minimized.get(ikey);
    }
    global.maximized.delete(ikey);
    global.minimized.delete(ikey);
    dd.window.closed = true;
    global.closed.set(ikey, dd);
    dd.minimize();
    updateAllMenus();
}
function onOpen(ikey, iitem) {
    var dd = global.closed.get(ikey);
    if (global.closed.get(ikey))
        global.closed.delete(ikey);
    dd.window.closed = false;
    global.map.set(ikey, dd);
    global.maximized.set(ikey, dd);
    dd.restore();
    setWindowSize(ikey, global.originalwidths.get(ikey), global.originalheights.get(ikey));
    updateAllMenus();
}
function saveProfile() {
    //Собираем объекты окон (id, состояние, положение)
    var windows = [];
    var config = new Object();
    //Сперва открытые
    var i = 0;
    global.map.forEach(function (item, key, mapObj) {
        windows.push(new Object());
        windows[i].id = key;
        var state = 'maximized';
        if (global.minimized.get(key)) {
            state = 'minimized';
            item.restore();
        }
        windows[i].state = state;
        windows[i].x = item.x;
        windows[i].y = item.y;
        windows[i].zoom = item.zoomLevel;
        i++;
    });
    //Затем закрытые
    global.closed.forEach(function (item, key, mapObj) {
        windows.push(new Object());
        windows[i].id = key;
        var state = 'closed';
        item.restore();
        windows[i].state = state;
        windows[i].x = item.x;
        windows[i].y = item.y;
        windows[i].zoom = item.zoomLevel;
        i++;
    });
    config.confwindows = windows;
    var el = document.getElementById("informer");
    var doc = el.contentDocument;
    var frm = doc.getElementById('helloworld');
    if (frm.contentWindow) {
        frm.contentWindow.$wnd.saveConfig(JSON.stringify(config));
    } else if (frm.contentDocument) {
        rfm.contentDocument.$wnd.saveConfig(JSON.stringify(config));
    }
}
function setWindowSize(id, h, w) {
    var window3 = global.maximized.get(id);
    global.originalheights.set(id, w);
    global.originalwidths.set(id, h);
    if (!window3) {
        window3 = global.minimized.get(id);
    }
    if (!window3) {
        window3 = global.closed.get(id);
    }
    if (!window3) {
        window3 = global.map.get(id);
    }
    if (window3) {
        window3.setResizable(true);
        var currentZoom = window3.zoomLevel;
        var actualZoom = Math.pow(1.2, currentZoom);
        window3.resizeTo(Math.floor(w * actualZoom), Math.floor((h * actualZoom + 36.0 * actualZoom)));
        window3.setResizable(false);
    }
}
function setWindowPosition2(id, state, zoom, x, y) {
    var window3 = global.map.get(id);
    if (window3) {
        window3.moveTo(x, y);
        if (zoom)
            window3.zoomLevel = zoom;
        if (state) {
            if (state === 'maximized') {
            }
            if (state === 'closed') {
                onClose(id, window3);
            }
            if (state === 'minimized') {
                onMinimize(id, window3);
            }
        }
    }
}
function setWindowTitle(id, title) {
    global.titles.set(id, title);
    var mainWindow = global.map.get(id);
    var setTitleEvent = 'setTitle(\'' + title + '\')';
    if (mainWindow) {
        mainWindow.eval(null, setTitleEvent);
    }
    mainWindow = global.minimized.get(id);
    if (mainWindow) {
        mainWindow.eval(null, setTitleEvent);
    }
    mainWindow = global.maximized.get(id);
    if (mainWindow) {
        mainWindow.eval(null, setTitleEvent);
    }
    mainWindow = global.closed.get(id);
    if (mainWindow) {
        mainWindow.eval(null, setTitleEvent);
    }
    updateAllMenus();
}
function setMainWindowInformerId(id) {
    var defaultId = global.mainWindowId;
    var closeButtonEvent = '$(\'.top-titlebar-close-button\').on(\"click\",function(){onClose(\'' + id + '\',undefined)});';
    var minimizeButtonEvent = '$(\'.top-titlebar-minimize-button\').on(\"click\",function(){onMinimize(\'' + id + '\',undefined)});';
    var closeButtonEventUnbind = '$(\'.top-titlebar-close-button\').unbind(\'click\');';
    var minimizeButtonEventUnbind = '$(\'.top-titlebar-minimize-button\').unbind(\'click\');';
    var mainWindow = global.map.get(defaultId);
    var wheelEventSet = false;
    if (mainWindow) {
        global.mainWindowId = id;
        global.map.delete(defaultId);
        global.map.set(id, mainWindow);
        mainWindow.eval(null, closeButtonEventUnbind);
        mainWindow.eval(null, minimizeButtonEventUnbind);
        mainWindow.eval(null, closeButtonEvent);
        mainWindow.eval(null, minimizeButtonEvent);
        addWheelEvent(id, mainWindow);
        wheelEventSet = true;
    }
    mainWindow = global.minimized.get(defaultId);
    if (mainWindow) {
        global.mainWindowId = id;
        global.minimized.delete(defaultId);
        global.minimized.set(id, mainWindow);
        if (!wheelEventSet) {
            mainWindow.eval(null, closeButtonEventUnbind);
            mainWindow.eval(null, minimizeButtonEventUnbind);
            mainWindow.eval(null, closeButtonEvent);
            mainWindow.eval(null, minimizeButtonEvent);
            addWheelEvent(id, mainWindow);
            wheelEventSet = true;
        }
    }
    mainWindow = global.maximized.get(defaultId);
    if (mainWindow) {
        global.mainWindowId = id;
        global.maximized.delete(defaultId);
        global.maximized.set(id, mainWindow);
        if (!wheelEventSet) {
            mainWindow.eval(null, closeButtonEventUnbind);
            mainWindow.eval(null, minimizeButtonEventUnbind);
            mainWindow.eval(null, closeButtonEvent);
            mainWindow.eval(null, minimizeButtonEvent);
            addWheelEvent(id, mainWindow);
            wheelEventSet = true;
        }
    }
    mainWindow = global.closed.get(defaultId);
    if (mainWindow) {
        global.mainWindowId = id;
        global.closed.delete(defaultId);
        global.closed.set(id, mainWindow);
        if (!wheelEventSet) {
            mainWindow.eval(null, closeButtonEventUnbind);
            mainWindow.eval(null, minimizeButtonEventUnbind);
            mainWindow.eval(null, closeButtonEvent);
            mainWindow.eval(null, minimizeButtonEvent);
            addWheelEvent(id, mainWindow);
        }
    }
}
function onWheelEvent(e, windowId) {
    var delta = e.originalEvent.wheelDelta || -e.originalEvent.detail;
    if (e.altKey === true) {
        var wind = global.map.get(windowId);
        var currentZoom = wind.zoomLevel;
        var newZoom;
        if (delta > 0) {
            newZoom = currentZoom + 1;
        } else {
            newZoom = currentZoom - 1;
        }
        if (newZoom > 5 || newZoom < -3)
            return;
        wind.zoomLevel = newZoom;
        setWindowSize(windowId, global.originalwidths.get(windowId), global.originalheights.get(windowId));
    }
}

function onPinchEvent(e, windowId) {
var delta = e.scale;
        var wind = global.map.get(windowId);
wind.eval(null,"alert(\"pinch captured\");");        
var currentZoom = wind.zoomLevel;
        var newZoom;
        var actualZoom = Math.pow(1.2, currentZoom);
        if (delta > 0) {
            newZoom = currentZoom + 1;
        } else {
            newZoom = currentZoom - 1;
        }
        if (newZoom > 5 || newZoom < -3)
            return;
        wind.zoomLevel = newZoom;
        if (actualZoom == 0)
            actualZoom = 1;
        setWindowSize(windowId, Math.ceil(wind.width / actualZoom), Math.ceil(wind.height / actualZoom - 35.0 * actualZoom));
}
function addWheelEvent(windowId, win) {
    win.eval(null, '$(\'#informer\').unbind(\'mousewheel\');');
    win.eval(null, '$(\'#informer\').unbind(\'DOMMouseScroll\');');
    win.eval(null, '$(\'#informer\').bind(\'mousewheel DOMMouseScroll\',function(e){onWheelEvent(e,\'' + windowId + '\')});');
}
function finishLoading(id) {
    var finishLoadingEvent = 'onFinishLoading();';
    var mainWindow = global.map.get(id);
    var wheelEventSet = false;
    if (mainWindow) {
        mainWindow.eval(null, finishLoadingEvent);
        wheelEventSet = true;
    }
    mainWindow = global.minimized.get(id);
    if (mainWindow) {
        if (!wheelEventSet) {
            mainWindow.eval(null, finishLoadingEvent);
            wheelEventSet = true;
        }
    }
    mainWindow = global.maximized.get(id);
    if (mainWindow) {
        if (!wheelEventSet) {
            mainWindow.eval(null, finishLoadingEvent);
            wheelEventSet = true;
        }
    }
    mainWindow = global.closed.get(id);
    if (mainWindow) {
        if (!wheelEventSet) {
            mainWindow.eval(null, finishLoadingEvent);
        }
    }
}
function onFinishLoading() {
}
function setAllWindowsAlwaysOnTopSign(sign) {
    global.map.forEach(function (item, key, mapObj) {
        item.setAlwaysOnTop(sign);
    });
    //Затем закрытые
    global.closed.forEach(function (item, key, mapObj) {
        item.setAlwaysOnTop(sign);
    });
}