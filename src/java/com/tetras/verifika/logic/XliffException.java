/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tetras.verifika.logic;

/**
 *
 * @author programator
 */
public class XliffException {
    private String name;
    private String id;
    private String oldSourceText;
    private String newSourceText;
    private String oldTargetText;
    private String newTargetText;
    private String oldStatus;
    private String newStatus;

    public XliffException(String name, String id, String oldSourceText, String newSourceText, String oldTargetText, String newTargetText, String oldStatus, String newStatus) {
        this.name = name;
        this.id = id;
        this.oldSourceText = oldSourceText;
        this.newSourceText = newSourceText;
        this.oldTargetText = oldTargetText;
        this.newTargetText = newTargetText;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the oldSourceText
     */
    public String getOldSourceText() {
        return oldSourceText;
    }

    /**
     * @param oldSourceText the oldSourceText to set
     */
    public void setOldSourceText(String oldSourceText) {
        this.oldSourceText = oldSourceText;
    }

    /**
     * @return the newSourceText
     */
    public String getNewSourceText() {
        return newSourceText;
    }

    /**
     * @param newSourceText the newSourceText to set
     */
    public void setNewSourceText(String newSourceText) {
        this.newSourceText = newSourceText;
    }

    /**
     * @return the oldTargetText
     */
    public String getOldTargetText() {
        return oldTargetText;
    }

    /**
     * @param oldTargetText the oldTargetText to set
     */
    public void setOldTargetText(String oldTargetText) {
        this.oldTargetText = oldTargetText;
    }

    /**
     * @return the newTargetText
     */
    public String getNewTargetText() {
        return newTargetText;
    }

    /**
     * @param newTargetText the newTargetText to set
     */
    public void setNewTargetText(String newTargetText) {
        this.newTargetText = newTargetText;
    }

    /**
     * @return the oldStatus
     */
    public String getOldStatus() {
        return oldStatus;
    }

    /**
     * @param oldStatus the oldStatus to set
     */
    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    /**
     * @return the newStatus
     */
    public String getNewStatus() {
        return newStatus;
    }

    /**
     * @param newStatus the newStatus to set
     */
    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    @Override
    public String toString() {
        return "XliffException{" + "name=" + name + ", id=" + id + ", oldSourceText=" + oldSourceText + ", newSourceText=" + newSourceText + ", oldTargetText=" + oldTargetText + ", newTargetText=" + newTargetText + ", oldStatus=" + oldStatus + ", newStatus=" + newStatus + '}';
    }
    
          
    
}
