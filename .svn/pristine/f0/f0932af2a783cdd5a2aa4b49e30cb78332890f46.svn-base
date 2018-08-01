/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tetras.verifika.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Boris
 */
public class CheckLockSegments {
    
    
    private List<File> original;
    private List<File> translation;
    private List<XliffException> exceptions = new ArrayList<>();
    
    
    /**
     * Porovnanie vsetkych Xlifov
     * @param o
     * @param t
     * @return 
     */
    public boolean analyzeAllFiles(List<File> o, List<File> t) {

        original = o;
        translation = t;
       
        setExceptions(new ArrayList<>());
        
        //Prechod vsetkymi povodnymi Xlifmi a najdenie a porovnanie s prelozenym
        for (int i = 0; i < original.size(); i++) {
            File originalFile = original.get(i);
            File translatedFile = findTranslatedFileByName(originalFile.getName());
            if (translatedFile != null) {
                analyzeFiles(originalFile, translatedFile);
            } 
        }
        return true;

    }

    
    
    /**
     * Najdeneio prelozeneho Xlifu podla nazvu
     * @param name
     * @return 
     */
    public File findTranslatedFileByName(String name) {
        for (int i = 0; i < translation.size(); i++) {
            File trans = translation.get(i);
            if (trans.getName().equals(name)) {
                return trans;
            }
        }
        return null;
    }

    
    
    
    public void analyzeFiles(File o, File t) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document originalDoc = dBuilder.parse(o);
            Document targetDoc = dBuilder.parse(t);
            originalDoc.getDocumentElement().normalize();
            targetDoc.getDocumentElement().normalize();
            
            //nacitanie vsetkych uzlov trans-unit (tieto uzly obsahuju zdrojovy a cielovy text)
            NodeList originalNodeList = originalDoc.getElementsByTagName("trans-unit");
            NodeList targetNodeList = targetDoc.getElementsByTagName("trans-unit");
            
            //prechod vsetkymi Trans-unit
            for (int i = 0; i < originalNodeList.getLength(); i++) {
                Node transUnitNode = originalNodeList.item(i);
                if (transUnitNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) transUnitNode;
                    NodeList sdlList = element.getElementsByTagName("sdl:seg");
                    for (int count = 0; count < sdlList.getLength(); count++) {
                        Node sdlNode = sdlList.item(count);
                        if (sdlNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element sdlElement = (Element) sdlNode;
                            
                            //Povodny segment je zamknuty
                            if (sdlElement.hasAttribute("locked")) {
                                if (sdlElement.getAttribute("locked").equals("true")) {
                                    this.compareWithTranslation(element, targetNodeList, sdlElement.getAttribute("id"), o.getName());
                                }
                            }
                        }

                    }

                }

            }

        } catch (Exception ex) {
            
        }
    }

    public String getSourceTag(Element e, String tag) {
        NodeList translateSourceNodeList = e.getElementsByTagName(tag);
        for (int j = 0; j < translateSourceNodeList.getLength(); j++) {
            Node sourceNode = translateSourceNodeList.item(j);
            return sourceNode.getTextContent();
        }
        return "";
    }

    //porovnanie obsahu elementov ktoré obsahujú zdrojový a cielový text
    public void compareWithTranslation(Element translationUnitOriginal, NodeList targetNodeList, String segmentId, String fileName) {
        for (int i = 0; i < targetNodeList.getLength(); i++) {

            boolean locked = true;
            Node transUnitNode = targetNodeList.item(i);
            if (transUnitNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) transUnitNode;
                if (element.hasAttribute("id")) {
                    if (element.getAttribute("id").equals(translationUnitOriginal.getAttribute("id"))) {
                        String originalSource = "";
                        String originalTarget = "";
                        String translatedSource = "";
                        String translatedTarget = "";
                        

                        //zistenie ci je aj v subore od prekladatela zamknuty segment
                        NodeList sdlSegTargetNodeList = element.getElementsByTagName("sdl:seg");
                        for (int j = 0; j < sdlSegTargetNodeList.getLength(); j++) {
                            Node targetNode = sdlSegTargetNodeList.item(j);
                            if (targetNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element sdlElement = ((Element) targetNode);
                                if (sdlElement.getAttribute("id").equals(segmentId)) {
                                    if (sdlElement.hasAttribute("locked")) {
                                        if (sdlElement.getAttribute("locked").equals("false")) {
                                            locked = false;
                                        }
                                    } else {
                                        locked = false;
                                    }
                                }
                            }
                        }

                        NodeList translateTargetNodeList = element.getElementsByTagName("target");
                        for (int j = 0; j < translateTargetNodeList.getLength(); j++) {
                            Node targetNode = translateTargetNodeList.item(j);
                            if (targetNode.getNodeType() == Node.ELEMENT_NODE) {
                                NodeList targetParts = ((Element) targetNode).getElementsByTagName("mrk");
                                for (int k = 0; k < targetParts.getLength(); k++) {
                                    Node mrkNode = targetParts.item(k);
                                    if (mrkNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element mrkElement = ((Element) mrkNode);
                                        if (mrkElement.hasAttribute("mid")) {
                                            if (mrkElement.getAttribute("mid").equals(segmentId)) {
                                                translatedTarget = translatedTarget + mrkElement.getTextContent() + (k + 1 == targetParts.getLength() ? "" : " ");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        NodeList originalTargetNodeList = translationUnitOriginal.getElementsByTagName("target");
                        for (int j = 0; j < originalTargetNodeList.getLength(); j++) {
                            Node targetNode = originalTargetNodeList.item(j);
                            if (targetNode.getNodeType() == Node.ELEMENT_NODE) {
                                NodeList targetParts = ((Element) targetNode).getElementsByTagName("mrk");
                                for (int k = 0; k < targetParts.getLength(); k++) {
                                    Node mrkNode = targetParts.item(k);
                                    if (mrkNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element mrkElement = ((Element) mrkNode);
                                        if (mrkElement.hasAttribute("mid")) {
                                            if (mrkElement.getAttribute("mid").equals(segmentId)) {
                                                originalTarget = originalTarget + mrkElement.getTextContent() + (k + 1 == targetParts.getLength() ? "" : " ");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        NodeList translateSourceNodeList = element.getElementsByTagName("seg-source");
                        for (int j = 0; j < translateSourceNodeList.getLength(); j++) {
                            Node targetNode = translateSourceNodeList.item(j);
                            if (targetNode.getNodeType() == Node.ELEMENT_NODE) {
                                NodeList targetParts = ((Element) targetNode).getElementsByTagName("mrk");
                                for (int k = 0; k < targetParts.getLength(); k++) {
                                    Node mrkNode = targetParts.item(k);
                                    if (mrkNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element mrkElement = ((Element) mrkNode);
                                        if (mrkElement.hasAttribute("mid")) {
                                            if (mrkElement.getAttribute("mid").equals(segmentId)) {
                                                translatedSource = translatedSource + mrkElement.getTextContent() + (k + 1 == targetParts.getLength() ? "" : " ");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        NodeList originalSourceNodeList = translationUnitOriginal.getElementsByTagName("seg-source");
                        for (int j = 0; j < originalSourceNodeList.getLength(); j++) {
                            Node targetNode = originalSourceNodeList.item(j);
                            if (targetNode.getNodeType() == Node.ELEMENT_NODE) {
                                NodeList targetParts = ((Element) targetNode).getElementsByTagName("mrk");
                                for (int k = 0; k < targetParts.getLength(); k++) {
                                    Node mrkNode = targetParts.item(k);
                                    if (mrkNode.getNodeType() == Node.ELEMENT_NODE) {
                                        Element mrkElement = ((Element) mrkNode);
                                        if (mrkElement.hasAttribute("mid")) {
                                            if (mrkElement.getAttribute("mid").equals(segmentId)) {
                                                originalSource = originalSource + mrkElement.getTextContent() + (k + 1 == targetParts.getLength() ? "" : " ");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        if (!locked || !originalSource.equals(translatedSource) || !originalTarget.equals(translatedTarget)) {
                            getExceptions().add(new XliffException(fileName, segmentId, originalSource, translatedSource, originalTarget, translatedTarget, "locked", (locked ? "locked" : "unlocked")));
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * @return the exceptions
     */
    public List<XliffException> getExceptions() {
        return exceptions;
    }

    /**
     * @param exceptions the exceptions to set
     */
    public void setExceptions(List<XliffException> exceptions) {
        this.exceptions = exceptions;
    }

    
    
    public void exportToExcel(String exportFile){
        
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Export");

            Font font = workbook.createFont();
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            CellStyle style = workbook.createCellStyle();
            style.setFont(font);
            int columnIndex = 0;
            int rowIndex = 0;
            String[] headers = {"Názov súboru", "ID segmentu", "Zdrojový text - pôvodný súbor", "Zdrojový text - preložený súbor", "Preklad - pôvodný súbor", "Preklad - preložený súbor", "Status pôvodný súbor", "Status preložený súbor"};
            Row row = sheet.createRow(rowIndex);
            Cell cell = null;
            
            //Nasla sa zmena v zamknutych segmentoch
            if (exceptions.size() > 0) {
                for (int i = 0; i < headers.length; i++) {
                    String header = headers[i];
                    cell = row.createCell(columnIndex);
                    cell.setCellType(CELL_TYPE_STRING);
                    cell.setCellValue(header);
                    columnIndex++;
                }

                for (int i = 0; i < exceptions.size(); i++) {
                    rowIndex++;
                    columnIndex = 0;
                    row = sheet.createRow(rowIndex);
                    XliffException exception = exceptions.get(i);
                    for (int j = 0; j < headers.length; j++) {
                        switch (j) {
                            case 0:
                            writeCell(sheet, rowIndex, columnIndex, exception.getName());
                            columnIndex++;
                            break;
                            case 1:
                            writeCell(sheet, rowIndex, columnIndex, exception.getId());
                            columnIndex++;
                            break;
                            case 2:
                            writeCell(sheet, rowIndex, columnIndex, exception.getOldSourceText());
                            columnIndex++;
                            break;
                            case 3:
                            writeCell(sheet, rowIndex, columnIndex, exception.getNewSourceText());
                            columnIndex++;
                            break;
                            case 4:
                            writeCell(sheet, rowIndex, columnIndex, exception.getOldTargetText());
                            columnIndex++;
                            break;
                            case 5:
                            writeCell(sheet, rowIndex, columnIndex, exception.getNewTargetText());
                            columnIndex++;
                            break;
                            case 6:
                            writeCell(sheet, rowIndex, columnIndex, exception.getOldStatus());
                            columnIndex++;
                            break;
                            case 7:
                            writeCell(sheet, rowIndex, columnIndex, exception.getNewStatus());
                            columnIndex++;
                            break;

                        }
                    }

                }
                FileOutputStream fileOut = new FileOutputStream(exportFile);
                        workbook.write(fileOut);
                fileOut.close();
            }
            
            //Zmena v zamknutych segmentoch nenastala
            else{
                
                FileUtils.writeStringToFile(new File(exportFile.replace("LockedSegments", "LockedSegments_NoErrors").replaceAll(".xlsx", ".txt")), "NO ERRORS", "UTF-8");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }                                        

    
    
    private void writeCell(XSSFSheet sheet, int rowIndex, int columnIndex, String val) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        cell.setCellType(CELL_TYPE_STRING);
        cell.setCellValue(val);
    }

    
    
}
