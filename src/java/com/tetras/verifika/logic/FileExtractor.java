/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tetras.verifika.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Boris
 */
public class FileExtractor {
    
    private String targetLanguage;
    private String translatorPackage;
    private String extractDirPath;

    public FileExtractor(String translatorPackage, String targetLanguage) {
        this.targetLanguage = targetLanguage;
        this.translatorPackage = translatorPackage;
    }
    
    
    /**
     * Vyextrahuje subory na Verifiku z balicka od prekladatela
     * @return 
     */
    public String extractFileFromPackage(){
        
        //Nakopirovanie balicka do pomocneho adresara
        File translatorPackageFile = new File(translatorPackage);
        String extractDir = translatorPackageFile.getName().substring(0, translatorPackageFile.getName().lastIndexOf("."));
        extractDirPath = Constants.UNZIP_DIR + File.separator + extractDir + "_" + (new Date()).getTime();
        File zipFile = new File(Constants.UNZIP_DIR + File.separator + translatorPackageFile.getName()); 
        try {
            FileUtils.copyFile(translatorPackageFile, zipFile);
        } catch (Exception e) {
        }
        
        //Rozzipovanie balicka
        byte[] buffer = new byte[4096];
        try {
            InputStream zipFileStream = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(zipFileStream);

            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(extractDirPath + File.separator + fileName);

                //System.out.println("file unzip : " + newFile.getAbsoluteFile());

                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            //Zmazanie balicka, ktory sa rozzipovaval z docasneho uloziska
            FileUtils.deleteQuietly(zipFile);

        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
        
        //Vyskladanie cesty k suborom na Verifiku podla source language
        File dirWithFiles = new File(extractDirPath + File.separator + targetLanguage);
        if(dirWithFiles.exists() && dirWithFiles.isDirectory()){
            return dirWithFiles.getAbsolutePath() + File.separator;
        }
        
        return "";
    }
    
    
    /**
     * Zmazanie adresara s rozzipovanymi subormi
     */
    public void deleteExtractDir(){
        try {
            FileUtils.deleteDirectory(new File(extractDirPath));
        } catch (Exception e) {
        }
    }
}
