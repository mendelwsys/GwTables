package com.mycompany.client.apps.App;

import com.mycompany.client.CliFilterByCriteria;
import com.mycompany.client.GUIStateDesc;
import com.mycompany.client.ListGridWithDesc;
import com.mycompany.common.DescOperation;
import com.mycompany.client.apps.App.api.NewFilterOperation;
import com.mycompany.client.apps.OperationNode;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.operations.IOperation;
import com.mycompany.client.utils.PortalLayoutUtils;
import com.smartgwt.client.data.*;
import com.smartgwt.client.types.*;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.menu.MenuButton;
import com.smartgwt.client.widgets.toolbar.ToolStripButton;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 02.12.14
 * Time: 12:45
 * TODO Диалоговые компоненты настройки интерфейса пользователя
 * Доделать:Выбор пиктограммы из файла(возможно после подсоединения к пользовательской БД),Загрузку из конфига,
 * в частности списка пиктограмм.
 *
 */
public class OptionsViewers
{


    public static Map<String, Object> getParamsByForm(DynamicForm form)
    {
        Map<String,Object> params = new HashMap<String,Object>();
        FormItem[] flds = form.getFields();
        for (FormItem fld : flds)
            params.put(fld.getName(),fld.getValue());
        return params;
    }


    public static int getNextNodeID(TreeNode[] data) {
        int maxoperationid=-1;
        for (TreeNode treeNode : data)
        {
            int operationId = treeNode.getAttributeAsInt(OperationNode.OPERATION_ID);
            if (maxoperationid< operationId) maxoperationid= operationId;
        }
        return maxoperationid+1;
    }


    public static Window createViewPortalColumnOptions(final MyPortalLayout portalLayout, final int column)
    {
        final Window winModal = createEmptyWindow(AppConst.CUSTOM_PAGE_OPTIONS_HEADER);
        winModal.setAutoSize(true);

        DynamicForm form = createEmptyForm();
        form.setNumCols(4);
        form.setPadding(10);



        final MenuButton mb = PortalLayoutUtils.getMenuButton(portalLayout, column);

        final TextItem textItem = new TextItem();
        textItem.setTitle(AppConst.HEADER_PORTAL_LAYOUT_TITLE_OPTIONS);

        textItem.setValue(mb.getTitle());


        final  SelectItem pictItem = new SelectItem();
        pictItem.setTitle(AppConst.HEADER_PORTAL_LAYOUT_PICT_OPTIONS);


        LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();

        valueMap.put("headerIcon","Исходная");
        valueMap.put("application_cascade", "Каскад");
        valueMap.put("application_double", "Двойной");
        valueMap.put("application_edit", "Редактирование");
        valueMap.put("application_delete", "Удаление");


        pictItem.setValueMap(valueMap);
        pictItem.setImageURLPrefix(AppConst.TOOLSTRIPICONS_PREFIX);
        pictItem.setImageURLSuffix(AppConst.IMAGE_URL_SUFFIX);

        LinkedHashMap<String, String> valueIcons = new LinkedHashMap<String, String>();

        valueIcons.put("headerIcon","headerIcon");
        valueIcons.put("application_cascade","application_cascade");
        valueIcons.put("application_double", "application_double");
        valueIcons.put("application_edit", "application_edit");
        valueIcons.put("application_delete","application_delete");

        pictItem.setValueIcons(valueIcons);

        String icon=portalLayout.getSwitchButton().getIcon();
        for (String s : valueIcons.keySet())
        {
            if (icon.contains(s))
            {
                pictItem.setDefaultValue(s);
                break;
            }
        }

        form.setFields(textItem,pictItem);

//        final FileItem pictFile = new FileItem ();
//        pictFile.setTitle(AppConst.HEADER_PORTAL_LAYOUT_PICT_FILE_OPTIONS);

        final IButton buttonItem = new IButton(AppConst.APPLY_BUTTON_PORTAL_LAYOUT_OPTIONS);
        buttonItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                String value=(String)pictItem.getValue();
                final ToolStripButton switchButton = portalLayout.getSwitchButton();

                if (value!=null && value.length()>0)
                    switchButton.setIcon(AppConst.TOOLSTRIPICONS_PREFIX + value + AppConst.IMAGE_URL_SUFFIX);

                final String title = (String) textItem.getValue();
                if (title!=null)
                {
                    final MenuButton menuButton = PortalLayoutUtils.getMenuButton(portalLayout, column);
                    menuButton.setTitle(title);
                    menuButton.setPrompt(title);
                    switchButton.setPrompt(title);
                }
                winModal.destroy();
            }
        });

        final IButton cancelItem = new IButton(AppConst.CANCEL_BUTTON_PORTAL_LAYOUT_OPTIONS);
        cancelItem.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                winModal.destroy();
            }
        });


        HLayout hLayout= new HLayout();

        final GUIStateDesc guiStateDesc = App01.GUI_STATE_DESC;
        if (guiStateDesc.getNumPages()>1 || portalLayout.getNumColumns()>1)
        {
            IButton deleteItem = new IButton(AppConst.DELETE_BUTTON_PORTAL_LAYOUT_OPTIONS);
            deleteItem.addClickHandler(new ClickHandler()
            {
                @Override
                public void onClick(ClickEvent event)
                {
                   SC.ask(AppConst.SAY_DELETE_PAGE +mb.getTitle(),new BooleanCallback()
                   {
                       @Override
                       public void execute(Boolean value)
                       {
                           if (value)
                           {
                               portalLayout.removeColumn(column);//TODO !!!проверить удаление всех внутренних портлетов!!!!
                               if (portalLayout.getNumColumns()==0)
                                   guiStateDesc.destroyCurrentPage();
                               else
                                   portalLayout.setColumnMenu();
                               winModal.destroy();
                           }
                       }
                   });

                }
            });
            hLayout.addMembers(buttonItem, cancelItem,deleteItem);
        }
        else
            hLayout.addMembers(buttonItem, cancelItem);


        hLayout.setAlign(Alignment.RIGHT);
        hLayout.setMargin(5);


        winModal.addItem(form);
        winModal.addItem(hLayout);

        return winModal;
    }

    public static DynamicForm createEmptyForm() {
        DynamicForm form = new DynamicForm();
        form.setHeight100();
        form.setWidth100();
        form.setPadding(5);
        form.setLayoutAlign(VerticalAlignment.BOTTOM);
        return form;
    }

    public  static Window createEmptyWindow(String header) {
        final Window winModal = new Window();
        winModal.setWidth(360);
        winModal.setHeight(165);
        winModal.setTitle(header);
        winModal.setShowMinimizeButton(false);
        winModal.setIsModal(true);
        winModal.setShowModalMask(true);
        winModal.centerInPage();
        winModal.addCloseClickHandler(new CloseClickHandler()
        {
            public void onCloseClick(CloseClickEvent event)
            {
                winModal.destroy();
            }
        });
        return winModal;
    }

}
