package com.mycompany.client.test.t8;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;

/**
 * Created by IntelliJ IDEA.
 * User: Vladislav.Mendelevic
 * Date: 24.11.14
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
 public class CountryDS extends DataSource {

        private static CountryDS instance = null;

        public static CountryDS getInstance() {
            if (instance == null) {
                instance = new CountryDS("localCountryDS");
            }
            return instance;
        }

        public CountryDS(String id) {
            setID(id);

            DataSourceTextField countryCodeField = new DataSourceTextField("countryCode", "Code");
            DataSourceTextField countryNameField = new DataSourceTextField("countryName", "Country");
            DataSourceTextField capitalField = new DataSourceTextField("capital", "Capital");
            setFields(countryCodeField, countryNameField, capitalField);

//            setCacheData(CountrySampleData.getNewRecords());

            setClientOnly(true);
        }
    }