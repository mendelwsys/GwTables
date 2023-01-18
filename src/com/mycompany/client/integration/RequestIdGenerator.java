package com.mycompany.client.integration;




/**
 * Created by Anton.Pozdnev on 30.01.2015.
 */
public class RequestIdGenerator implements IRequestIdGenerator {
volatile static int integer = Integer.MIN_VALUE;


    @Override
    public synchronized String generateRequestId(String senderId) {

        if (integer+1==Integer.MAX_VALUE) integer=Integer.MIN_VALUE;
        return ""+senderId+"#"+ ++integer;
    }




}
