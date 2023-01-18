package com.mycompany.client;

import com.mycompany.client.operations.ICliFilter;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;


/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 05.12.14
 * Time: 18:09
 *
 */
public class CliFilterByCriteria implements ICliFilter
{

    public DataSource getFilterDS() {
        return filterDS;
    }

    public void setFilterDS(DataSource filterDS) {
        this.filterDS = filterDS;
    }

    private DataSource filterDS;
    private Criteria criteria;

    public CliFilterByCriteria(DataSource filterDS,Criteria criteria)
    {
        this.criteria=criteria;
        this.filterDS = filterDS;
    }

//    public CliFilterByCriteria(DataSource filterDS)
//    {
//        this.filterDS = filterDS;
//    }


    @Override
    public void setCriteria(Criteria criteria)
    {
        this.criteria=criteria;
    }

    @Override
    public Record[] filter(Record[] records)
    {
        return filterDS.applyFilter(records,criteria);
    }

    @Override
    public Criteria getCriteria() {
        return criteria;
    }
}
