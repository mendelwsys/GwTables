package com.mycompany.client.test.fbuilder;


import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.util.JSOHelper;

/**
 * Created by IntelliJ IDEA.
 * User: LEV
 * Date: 07.03.15
 * Time: 16:52
 * Определитель фильтра - определяет мно-во строк над которым производится дальнейшие агрегационные дествия
 */
public class FilterDet
{

    public String getTblType() {
        return tblType;
    }

    public void setTblType(String tblType) {
        this.tblType = tblType;
    }

    private String tblType;
    private String viewTableName;

    public DataSource getFilterDS() {
        return filterDS;
    }

    private DataSource filterDS;

    public void setFilterDS(DataSource filterDS) {
        this.filterDS = filterDS;
    }

    public DataSource getFieldsMetaDS() {
        return fieldMetaDS;
    }

    public void setFieldMetaDS(DataSource fieldMetaDS) {
        this.fieldMetaDS=fieldMetaDS;
    }

    private DataSource fieldMetaDS = new DataSource();


    public String getViewTableName() {
        return viewTableName;
    }

    public FilterDet(String tblType,String viewTableName,String criteria,String name)
    {
        this.tblType=tblType;
        this.viewTableName = viewTableName;
        if (criteria!=null && criteria.length()>0)
            this.criteria = new AdvancedCriteria(JSOHelper.eval(criteria));
        this.name = name;
    }

    //TODO как для задания фильтра так и для собственно фильтрации
    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Criteria criteria;//Критерий фильтра
    private String name=""; //Имя фильтра

    public Record[] applyFilter(Record[] records, Criteria criteria)
    {
           return filterDS.applyFilter(records,criteria);
    }


}
