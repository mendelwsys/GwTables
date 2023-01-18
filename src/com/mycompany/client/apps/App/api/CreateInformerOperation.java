package com.mycompany.client.apps.App.api;

import com.mycompany.client.apps.App.App01;
import com.mycompany.client.apps.SimpleOperation;
import com.mycompany.client.operations.IOperation;
import com.mycompany.common.DescOperation;
import com.smartgwt.client.widgets.Canvas;

import java.util.Date;

/**
 * Created by Anton.Pozdnev on 24.06.2015.
 */
public class CreateInformerOperation extends SimpleOperation {
    public Long id = 0L;
    public static final String DOR_KOD = "DOR_KOD";
    public static final String CRD_LEFT = "CRD_LEFT";
    public static final String CRD_TOP = "CRD_TOP";
    public static final String CREATED_INFORMER_ID = "INFORMER_ID";
    public static final String APP_MODE = "MODE";
    public static final String HIGH_WARN_LEVEL = "HIGH_WARN_LEVEL";
    public static final String DESKTOP_WINDOW_STATE = "DESKTOP_STATE";
    public static final String DESKTOP_X = "DESKTOP_X";
    public static final String DESKTOP_Y = "DESKTOP_Y";
    public static final String DESKTOP_ZOOM = "DESKTOP_ZOOM";

    protected Integer dor_kod;
    protected Integer crd_left;
    protected Integer crd_top;
    protected Boolean highWarnLevel = null;
    protected Integer desktopX;
    protected Integer desktopY;
    String desktopWindowState;
    protected Integer desktopZoom;

    public CreateInformerOperation() {
    }

    public CreateInformerOperation(int operationId, int parentOperationId, String viewName, TypeOperation type) {
        super(operationId, parentOperationId, viewName, type);

    }

    @Override
    public DescOperation getDescOperation(DescOperation descOperation) {
        descOperation = super.getDescOperation(descOperation);
        descOperation.put(APP_MODE, App01.GUI_STATE_DESC.getApplicationMode());
        descOperation.put(DOR_KOD, dor_kod);
        descOperation.put(CRD_LEFT, crd_left);
        descOperation.put(CRD_TOP, crd_top);
        if (id > 0L)
            descOperation.put(CREATED_INFORMER_ID, id);
        if (highWarnLevel != null)
            descOperation.put(HIGH_WARN_LEVEL, highWarnLevel);
        if (desktopWindowState != null)
            descOperation.put(DESKTOP_WINDOW_STATE, desktopWindowState);
        if (desktopX != null)
            descOperation.put(DESKTOP_X, desktopX);
        if (desktopY != null)
            descOperation.put(DESKTOP_Y, desktopY);
        if (desktopZoom != null)
            descOperation.put(DESKTOP_ZOOM, desktopZoom);

        return descOperation;
    }

    @Override
    public Canvas operate(Canvas dragTarget, IOperationContext ctx) {
        Canvas c = super.operate(dragTarget, ctx);
        if (id == 0) id = new Date().getTime();
        return c;
    }


    @Override
    public IOperation createOperation(DescOperation descOperation, IOperation operation) {
        IOperation op = super.createOperation(descOperation, operation);
        ((CreateInformerOperation) op).dor_kod = (Integer) descOperation.get(DOR_KOD);
        ((CreateInformerOperation) op).crd_left = (Integer) descOperation.get(CRD_LEFT);
        ((CreateInformerOperation) op).crd_top = (Integer) descOperation.get(CRD_TOP);
        if (descOperation.get(CREATED_INFORMER_ID) != null && ((Long) descOperation.get(CREATED_INFORMER_ID)) > 0L)
            ((CreateInformerOperation) op).id = (Long) descOperation.get(CREATED_INFORMER_ID);
        ((CreateInformerOperation) op).highWarnLevel = (Boolean) descOperation.get(HIGH_WARN_LEVEL);
        ((CreateInformerOperation) op).desktopWindowState = (String) descOperation.get(DESKTOP_WINDOW_STATE);
        ((CreateInformerOperation) op).desktopX = (Integer) descOperation.get(DESKTOP_X);
        ((CreateInformerOperation) op).desktopY = (Integer) descOperation.get(DESKTOP_Y);
        ((CreateInformerOperation) op).desktopZoom = (Integer) descOperation.get(DESKTOP_ZOOM);

        return op;
    }
}
