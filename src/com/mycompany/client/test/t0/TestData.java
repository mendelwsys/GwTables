package com.mycompany.client.test.t0;


import com.mycompany.common.TablesTypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: User
 * Date: 12.09.14
 * Time: 18:32
 * Тестовые данные для локальных тестов страниц без обращений на серврер.
 */
public class TestData
{
    public static final String warnHeaderJson="[{\"chs\":[{\"title\":\"Телеграмма\",\"name\":\"TLG\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":true},{\"title\":\"Пред.  \",\"name\":\"PRED_NAME\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":false},{\"title\":\"Место\\u003cbr\\u003eдействия\\u003cbr\\u003e\",\"name\":\"PLACE\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":true},{\"title\":\"Длина\",\"name\":\"LEN\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":false},{\"title\":\"Начало\\u003cbr\\u003eдействия\\u003cbr\\u003e\",\"name\":\"TIM_BEG\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":false},{\"title\":\"Окончание\\u003cbr\\u003eдействия\\u003cbr\\u003e\",\"name\":\"TIM_OTM\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":false},{\"title\":\"Скорость\\u003cbr\\u003eП/Г/Э/Гп/Стр\",\"name\":\"VPAS_VGR_VEL_VGRPOR_VSTR\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":false},{\"title\":\"Причина\\u003cbr\\u003eвыдачи\",\"name\":\"PRICH_NAME\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":false},{\"title\":\"Карточка\",\"name\":\"CRDURL\",\"type\":\"LINK\",\"linkText\":\"Карточка\",\"visible\":true,\"autofit\":false},{\"title\":\"Дорога\",\"name\":\"DOR_KOD\",\"type\":\"INTEGER\",\"visible\":false,\"autofit\":false},{\"title\":\"id\",\"name\":\"id\",\"type\":\"TEXT\",\"visible\":false,\"autofit\":false}],\"headerHeight\":0,\"cellHeight\":0,\"fixedRecordHeights\":false}]";
    public static final String warnDataJson="[{\"updateStamp\":1410532121289,\"updateStampN\":0,\"tuples\":[{\"PRED_NAME\":\"ПЧ-22\",\"PRICH_NAME\":\"ОБКАТКА КАП.РЕМОНТА\",\"TIM_OTM\":\"до отмены\",\"CRDURL\":\"http://warn_port:warn_host/wXXXXXX/jsp_predupr.jsp?dor_kod\\u003d28\\u0026pid\\u003d4126517\\u0026pids\\u003d4126517\",\"id\":\"28;4126517\",\"PLACE\":\"ГЛАЗАНИХА - МУДЬЮГА\\u003cbr\\u003eгл. путь 1 , 271км 6пк - 272км\",\"LEN\":\"1.2\",\"TIM_BEG\":\"07.08.2014 05:00\",\"rowcolor\":\"GRAY\",\"DOR_KOD\":\"28\",\"actual\":1,\"VPAS_VGR_VEL_VGRPOR_VSTR\":\"60/60/60/-/-\",\"TLG\":\"180\\u003cbr\\u003e07.08.2014 02:59\"}]}]";

    public static final String lentaHeaderJson="[{\"chs\":[{\"title\":\"Дорога\",\"name\":\"DOR_NAME\",\"type\":\"TEXT\",\"visible\":false,\"autofit\":false},{\"title\":\"№\",\"name\":\"EVENTID\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":true},{\"title\":\"Служба\",\"name\":\"o_serv\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":true},{\"title\":\"Событие\",\"name\":\"o_event\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":true},{\"title\":\"Место\",\"name\":\"PLACE\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":true},{\"title\":\"Исполнитель\",\"name\":\"PRED_ID\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":false},{\"title\":\"Работы\",\"name\":\"COMMENT\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":false},{\"title\":\"Начало\",\"name\":\"ND\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":false},{\"title\":\"Конец\",\"name\":\"KD\",\"type\":\"TEXT\",\"visible\":true,\"autofit\":false},{\"title\":\"№ Дороги\",\"name\":\"DOR_KOD\",\"type\":\"TEXT\",\"visible\":false,\"autofit\":false},{\"title\":\"Состояния\",\"name\":\"o_state\",\"type\":\"TEXT\",\"visible\":false,\"autofit\":false},{\"title\":\"Исполнение\",\"name\":\"STATUS_FACT\",\"type\":\"INTEGER\",\"visible\":false,\"autofit\":false},{\"title\":\"Планирование\",\"name\":\"STATUS_PL\",\"type\":\"INTEGER\",\"visible\":false,\"autofit\":false},{\"title\":\"id\",\"name\":\"id\",\"type\":\"TEXT\",\"visible\":false,\"autofit\":false}],\"headerHeight\":0,\"cellHeight\":0,\"fixedRecordHeights\":false}]";
    public static final String lentaDataJson="[{\"updateStamp\":1410534636258,\"updateStampN\":0,\"tuples\":[{\"EVENTID\":\"1136786\",\"o_serv\":\"Ш\",\"o_state\":\"56,94,57\",\"id\":\"28;1136786\",\"PLACE\":\"ТУФАНОВО-БУШУИХА\",\"PRED_ID\":\"ШЧ-4\",\"STATUS_FACT\":\"1\",\"STATUS_PL\":\"2\",\"COMMENT\":\"\\u003cb\\u003eТехническое обслуживание устройств СЦБ:\\u003c/b\\u003e\\u003cbr\\u003eЗамена питающего, релейного трансформатора (5 шт)\",\"DOR_KOD\":\"28\",\"actual\":1,\"DOR_NAME\":\"Северная\",\"o_event\":\"Окно\",\"ND\":\"11.09.2014 12:00\",\"KD\":\"11.09.2014 14:00\"}]}]";



    public static Map<String,String[]> data4Tests=new HashMap<String,String[]>();
    static
    {
        data4Tests.put(TablesTypes.WARNINGS,new String[]{warnHeaderJson,warnDataJson});
        data4Tests.put(TablesTypes.LENTA,new String[]{lentaHeaderJson,lentaDataJson});
    }

}
