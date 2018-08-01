/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tetras.verifika.logic;

import java.util.ArrayList;

public class TermBase {

    public String sourceLanguage;
    public String destLanguage;
    public ArrayList<String> sourceValues;
    public ArrayList<String> destValues;

    public TermBase(String sourceLanguage, String destLanguage, ArrayList<String> sourceValues, ArrayList<String> destValues) {
        this.sourceLanguage = sourceLanguage;
        this.destLanguage = destLanguage;
        this.sourceValues = sourceValues;
        this.destValues = destValues;
    }

    public TermBase() {
        sourceValues = new ArrayList();
        destValues = new ArrayList();
    }

    public TermBase(String sourceLanguage, String destLanguage) {
        this.sourceLanguage = sourceLanguage;
        this.destLanguage = destLanguage;
        sourceValues = new ArrayList();
        destValues = new ArrayList();
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public String getDestLanguage() {
        return destLanguage;
    }

    public ArrayList<String> getSourceValues() {
        return sourceValues;
    }

    public ArrayList<String> getDestValues() {
        return destValues;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public void setDestLanguage(String destLanguage) {
        this.destLanguage = destLanguage;
    }

    public void setSourceValues(ArrayList<String> sourceValues) {
        this.sourceValues = sourceValues;
    }

    public void setDestValues(ArrayList<String> destValues) {
        this.destValues = destValues;
    }

    public void addSourceTerm(String term) {
        sourceValues.add(term);
    }

    public void addDestTerm(String term) {
        destValues.add(term);
    }

}
