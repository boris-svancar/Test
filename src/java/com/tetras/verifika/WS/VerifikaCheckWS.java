/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tetras.verifika.WS;

import com.tetras.verifika.logic.CheckLockSegments;
import com.tetras.verifika.logic.FileExtractor;
import com.tetras.verifika.logic.ProfileCreator;
import com.tetras.verifika.logic.SendMail;
import com.tetras.verifika.logic.TermBase;
import com.tetras.verifika.logic.Verifika;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Boris
 */
@WebService(serviceName = "VerifikaCheckWS")
public class VerifikaCheckWS {

    @PersistenceContext(unitName = "VerifikaCheckWSPU")
    private EntityManager em;

    @Resource
    private UserTransaction userTransaction;

    private boolean running = false;
    private boolean verifikaIsRunning = false;

    /**
     * Vytvorenie profilu na zaklade terminologie
     *
     * @param pathToFile
     * @param sourceLanguage
     * @param destLanguage
     */
    @WebMethod(operationName = "createProfileFromTermbase")
    public String createProfileFromTermbase(
            @WebParam(name = "pathToTermbase") String pathToTermbase,
            @WebParam(name = "sourceLanguage") String sourceLanguage,
            @WebParam(name = "destLanguage") String destLanguage) {

        TermBase termbase = new TermBase();

        String sourceLanguageTermbase = (String) em.createNativeQuery("SELECT jazyk_termbase FROM cis_jazyk_verifika_konverzia WHERE jazyk_sdl = '" + sourceLanguage + "'").getSingleResult();
        String destLanguageTermbase = (String) em.createNativeQuery("SELECT jazyk_termbase FROM cis_jazyk_verifika_konverzia WHERE jazyk_sdl = '" + destLanguage + "'").getSingleResult();
        String sourceLanguageVerifika = (String) em.createNativeQuery("SELECT jazyk_verifika_id FROM cis_jazyk_verifika_konverzia WHERE jazyk_sdl = '" + sourceLanguage + "'").getSingleResult();
        String destLanguageVerifika = (String) em.createNativeQuery("SELECT jazyk_verifika_id FROM cis_jazyk_verifika_konverzia WHERE jazyk_sdl = '" + destLanguage + "'").getSingleResult();

        if (!pathToTermbase.isEmpty()) {

            termbase.setSourceLanguage(sourceLanguageVerifika);
            termbase.setDestLanguage(destLanguageVerifika);
            try {
                FileInputStream fis = new FileInputStream(new File(pathToTermbase));

                try {
                    XSSFWorkbook workbook = new XSSFWorkbook(fis);
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    XSSFRow row = null;

                    //zistenie poctuz stlpcov
                    row = sheet.getRow(0);
                    int colCount = row.getLastCellNum();

                    for (int i = 0; i < colCount; i++) {
                        Cell cell = row.getCell(i);
                        if (cell.toString().equals(sourceLanguageTermbase)) {

                            //zistenie poctu riadkov
                            int lastRow = sheet.getLastRowNum();
                            /*while (lastRow >= 0 && sheet.getRow(lastRow).getCell(i) == null) {
                             lastRow--;
                             }*/
                            int rowSizeSource = lastRow + 1;

                            for (int s = 1; s < rowSizeSource; s++) {
                                if (sheet.getRow(s) == null) {
                                    termbase.addSourceTerm("NULL");
                                } else {
                                    Cell cellOfSource = sheet.getRow(s).getCell(i);

                                    //ak je prazdna da do arraylistu hodnotu NULL
                                    if (cellOfSource != null) {
                                        termbase.addSourceTerm(cellOfSource.toString());
                                    } else {
                                        termbase.addSourceTerm("NULL");
                                    }
                                }
                            }
                        }

                        if (cell.toString().equals(destLanguageTermbase)) {

                            int lastRow = sheet.getLastRowNum();
                            /*while (lastRow >= 0 && sheet.getRow(lastRow).getCell(i) == null) {
                             lastRow--;
                             }*/
                            int rowSizeSource = lastRow + 1;

                            for (int s = 1; s < rowSizeSource; s++) {
                                if (sheet.getRow(s) == null) {
                                    termbase.addDestTerm("NULL");
                                } else {
                                    Cell cellOfDest = sheet.getRow(s).getCell(i);
                                    if (cellOfDest != null) {
                                        termbase.addDestTerm(cellOfDest.toString());
                                    } else {
                                        termbase.addDestTerm("NULL");
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }

        }

        //Vytvorenie profilu z terminologie
        try {
            File temp = null;
            if (pathToTermbase.isEmpty()) {
                temp = File.createTempFile("DefaultProfile", ".vprofile");
            } else {
                temp = File.createTempFile(pathToTermbase.substring(pathToTermbase.lastIndexOf(File.separator) + 1, pathToTermbase.length() - 5), ".vprofile");
            }
            ProfileCreator profileCreator = new ProfileCreator(temp.getName(), termbase);
            String pathToProfile = profileCreator.createProfile(pathToTermbase);
            FileUtils.deleteQuietly(temp);
            return pathToProfile;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "";
    }

    /**
     * Verifika pre zadany balicek od prekladatela
     */
    @WebMethod(operationName = "verifikaForDir")
    public String verifikaForDir(
            @WebParam(name = "translatorPackage") String translatorPackage,
            @WebParam(name = "targetLanguage") String targetLanguage,
            @WebParam(name = "profile") String profile,
            @WebParam(name = "reportOutput") String reportOutput) {

        //Vytiahnutie suborov na Verifiku z SDL balika
        FileExtractor extractor = new FileExtractor(translatorPackage, targetLanguage);
        String files = extractor.extractFileFromPackage();

        //System.out.println("Subory na verifiku " + files);
        Runnable k = new Runnable() {
            @Override
            public void run() {
                try {
                    //Cakanie 20 minut
                    Thread.sleep((long) 20 * 15 * 1000);

                    if (verifikaIsRunning) {
                        SendMail mail = new SendMail();
                        try {
                            mail.send("verifikaWS@tetras.sk", "developers@tetras.de", "Dlhý čas generovania Verifiky", "Čas generovania Verifiky prekročil 20 minút");
                        } catch (Exception e) {
                        }

                        System.out.println("Email ohladom dlheho behu Verifiky");
                    } else {
                        SendMail mail = new SendMail();
                        try {
                            mail.send("verifikaWS@tetras.sk", "developers@tetras.de", "Dlhé čakanie na Verifiku", "Čakanie na zamknutie Verifiky prekročilo 20 minút");
                        } catch (Exception e) {
                        }

                    }

                    running = false;
                } catch (InterruptedException e) {
                    running = false;
                }
            }
        };

        Thread longOperationWatchdog = new Thread(k);
        longOperationWatchdog.start();

        running = true;

        //Cakanie na uspesne zamknutie Verifiky
        while (running && ((int) (em.createNativeQuery("SELECT vendorportal.p_verifika_lock(1, '" + translatorPackage + "')").getSingleResult())) == 0) {
        }

        //Cakanie na zamknutie bolo ukoncene pre prilis dlhe cakanie
        if (!running) {
            System.out.println("Ukoncene pre dlhe trvanie");

            //Pripadne odomknutie zamku Verifiky (ak by v jednom okomihu doslo k zamknutiu a aj ukonceniu pre dlhe trvanie)
            em.createNativeQuery("SELECT vendorportal.p_verifika_lock(0, '" + translatorPackage + "')").getSingleResult();
            return "";
        }

        System.out.println("Uspesne zamknute");

        //Ukoncenie sledovania dlzky cakania na zamknutie verifiky
        longOperationWatchdog.interrupt();
        running = true;
        verifikaIsRunning = true;

        longOperationWatchdog = new Thread(k);
        longOperationWatchdog.start();

        //Spustenie Verifiky
        System.out.println("Vykonavam verifiku");
        Verifika verifika = new Verifika(files, profile, reportOutput);
        boolean noError = verifika.checkFiles();

        //Odomknutie zamku Verifiky po uspesnom spracovani
        em.createNativeQuery("SELECT vendorportal.p_verifika_lock(0, '" + translatorPackage + "')").getSingleResult();
        System.out.println("Odomknutie zamku");

        longOperationWatchdog.interrupt();

        //Zmazanie docasneho adresara a profilu
        extractor.deleteExtractDir();
        FileUtils.deleteQuietly(new File(profile));
        
        //Verifika nenasla ziadnu chybu
        if(noError){
            reportOutput = reportOutput.replace("Verifika", "Verifika_NoErrors").replace(".xls", ".txt");
            try {
                FileUtils.writeStringToFile(new File(reportOutput), "NO ERRORS");
            } catch (Exception e) {
            }
            
        }

        //Kontrola ci sa uspesne vytvoril report Verifiky
        if ((new File(reportOutput)).exists()) {
            return reportOutput;
        }

        return "";
    }

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "checkLockSegments")
    public String checkLockSegments(
            @WebParam(name = "toTranslatorPackage") String toTranslatorPackage,
            @WebParam(name = "fromTranslatorPackage") String fromTranslatorPackage,
            @WebParam(name = "targetLanguage") String targetLanguage,
            @WebParam(name = "reportOutput") String reportOutput) {

        toTranslatorPackage = toTranslatorPackage.replace("/media/Projekty", "\\\\SYNOBACKUP\\Projekty").replace("/", File.separator).replace("\\", File.separator);
        fromTranslatorPackage = fromTranslatorPackage.replace("/media/Projekty", "\\\\SYNOBACKUP\\Projekty").replace("/", File.separator).replace("\\", File.separator);
        reportOutput = reportOutput.replace("/media/Projekty", "\\\\SYNOBACKUP\\Projekty").replace("/", File.separator).replace("\\", File.separator);

        
        FileExtractor extractor2 = new FileExtractor(toTranslatorPackage, targetLanguage);
        String xlifsToTranslator = extractor2.extractFileFromPackage();

        FileExtractor extractor3 = new FileExtractor(fromTranslatorPackage, targetLanguage);
        String xlifsFromTranslator = extractor3.extractFileFromPackage();

        CheckLockSegments lockSegmentChecker = new CheckLockSegments();
        lockSegmentChecker.analyzeAllFiles(Arrays.asList((new File(xlifsToTranslator)).listFiles()), Arrays.asList((new File(xlifsFromTranslator)).listFiles()));
        lockSegmentChecker.exportToExcel(reportOutput);

        extractor2.deleteExtractDir();
        extractor3.deleteExtractDir();

        //Kontrola ci sa uspesne vytvoril report zmenenych segmentov
        if ((new File(reportOutput)).exists()) {
            return reportOutput;
        }

        return "";
    }
}
