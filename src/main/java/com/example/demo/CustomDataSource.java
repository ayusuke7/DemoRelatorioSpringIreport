package com.example.demo;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

import java.util.ArrayList;

public class CustomDataSource implements JRDataSource {

    private final ArrayList<String> urlImagens = new ArrayList<>();

    private int counter = -1;

    private int lastFieldAdd = 0;

    @Override
    public boolean next() throws JRException {
        if(urlImagens != null && counter < urlImagens.size() -1 ){
            counter++;
            return true;
        }
        return false;
    }

    @Override
    public Object getFieldValue(JRField jrField) throws JRException {



        return null;
    }

    public static JRDataSource getDataSource(){
        return new CustomDataSource();
    }
}
