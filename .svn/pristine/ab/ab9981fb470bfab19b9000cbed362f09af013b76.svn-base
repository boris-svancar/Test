/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tetras.verifika.logic;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author Boris
 */
public class Verifika {
    
    private String files;
    private String profile;
    private String reportOutput;

    public Verifika(String files, String profile, String reportOutput) {
        this.files = files;
        this.profile = profile;
        this.reportOutput = reportOutput;
    }
    
    
    /**
     * Spustenie Verifiky nad zadanymi subormi
     */
    public void checkFiles(){
        String command = Constants.VERIFIKA_EXE + " -files " + this.files + " -profile " + this.profile + " -type " + Constants.REPORT_TYPE + " -result " + reportOutput;
        System.out.println("Spustam Verifiku: " + command);
        String ret = "";
        try {
            String line;
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = bri.readLine()) != null) {
              ret+=line;
            }
            System.out.println(ret);
            bri.close();
            p.waitFor(); 
        }
        catch (Exception err) {
          err.printStackTrace();
        }
        return;
        
    }
}
