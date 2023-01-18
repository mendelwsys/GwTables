package com.mycompany.client.test.staf;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 21.05.14
 * Time: 17:38
 * Реализация конструктора
 */
public class GridMetaProviderBU
//        implements IGridMetaProvider
{
//    public MyDSCallback initGrid(final IGridConstructor gridConstructor,String tblType, String headerURL,String dataURL)
//    {
//        //Сначала загружаемм опсиание столбцов
//        String idHeader = headerURL.replace(".", "_");
//        DataSource headerSource;
//        if ((headerSource=DataSource.get(idHeader))==null)
//        {
//            headerSource = new DataSource(headerURL);
//            headerSource.setID(idHeader);
//            headerSource.setDataFormat(DSDataFormat.JSON);
//        }
//
//        headerSource.fetchData
//        (
//                new Criteria(TablesTypes.TTYPE, tblType), //Среди прочих параметров передавать идентифкатор таблицы
//                new DSCallback()
//                {
//                    public void execute(DSResponse response, Object rawData, DSRequest request)
//                    {
//                        try {
//                            gridConstructor.setHeaderGrid(response.getData()[0]);
//                        } catch (SetGridException e)
//                        {
//                            e.printStackTrace();  //TODO Что делать на это исключение
//                        }
//                    }
//                }
//        );
//        gridConstructor.getDataBoundComponent().setAutoFetchData(false);//TODO Посмотреть можно ли скрыть это
//
//        String dataId = dataURL.replace(".", "_");
//        DataSource dataSource;
//        if ((dataSource=DataSource.get(dataId))==null)
//        {
//            dataSource= new DataSource(dataURL);
//            dataSource.setID(dataId);
//            dataSource.setDataFormat(DSDataFormat.JSON);
//        }
//
//        return new MyDSCallback()
//        {
//            public void execute(DSResponse response, Object rawData, DSRequest request)
//            {
//                try {
//                    Record[] data = response.getData();
//
//                    this.timeStamp=data[0].getAttributeAsLong("updateStamp");
//                    this.timeStampN=data[0].getAttributeAsInt("updateStampN");
//
//                    gridConstructor.setDiagramDesc(data[0].getAttributeAsMap("desc"));
//                    data = data[0].getAttributeAsRecordArray("tuples");
//
//                    gridConstructor.updateDataGrid(data, false);
//
//                    Timer timer1 = getTimer();
//                    if (timer1!=null)
//                        timer1.schedule(15000);
//                } catch (SetGridException e)
//                {
//                    e.printStackTrace(); //TODO Что делать на это исключение
//                }
//            }
//        };
//
//    }
//
//    @Override
//    public Pair<DSCallback, MyDSCallback> initGrid2(IGridConstructor gridConstructor, String headerURL, String dataURL, int period) {
//        throw new UnsupportedOperationException();
//    }
}
