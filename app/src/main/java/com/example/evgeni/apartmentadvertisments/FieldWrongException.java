package com.example.evgeni.apartmentadvertisments;

/**
 * Created by RomaMarg on 29.05.2015.
 */

/**
 * Exception That Handle Wrong Filed In Form
 */
public class FieldWrongException extends Exception {

    private String m_wrongField;
   public FieldWrongException(String wrongField){
       super(wrongField);
       this.m_wrongField = wrongField;
   }

    @Override
    public String toString() {
        return m_wrongField;
    }
}
