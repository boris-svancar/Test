/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tetras.verifika.logic;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Boris
 */
public class ProfileCreator {

    private String profile;
    private TermBase termbase;

    public ProfileCreator(String profile, TermBase termbase) {
        this.profile = profile;
        this.termbase = termbase;
    }

    /**
     *
     * @return
     */
    public String createProfile(String termbaseFile) {
        File newProfile = null;
        try {
            String termbaseString = "";

            String profileString = FileUtils.readFileToString(new File(Constants.DEFAULT_VERIFIKA_PROFILE), "UTF-8");

            //Terminologia nie je prazdna
            if (!termbase.sourceValues.isEmpty()) {
                for (int i = 0; i < termbase.sourceValues.size(); i++) {
                    String source = termbase.sourceValues.get(i).replace("\"", "\\\"").replace("/", "\\/");
                    String correct = termbase.destValues.get(i).replace("\"", "\\\"").replace("/", "\\/");
                    if (!source.matches("NULL") && !correct.matches("NULL")) {
                        termbaseString += "{\"CorrectTranslations\":[\"" + correct + "\"],\"ForbiddenTranslations\":[],\"ReferenceGlossary\":\"" + termbaseFile.replace("\\", "\\\\") + 
                               "\",\"SourceLanguageId\":" + termbase.sourceLanguage + ",\"SourceTerm\":\"" + source + "\",\"TargetLanguageId\":" + termbase.destLanguage + "},";
                    }
                }
                termbaseString = termbaseString.substring(0, termbaseString.length() - 1);
                profileString = profileString.replace("\"Terms\":{\"Value\":[]}", "\"Terms\":{\"Value\":[" + termbaseString + "]}");
            }

            
            newProfile = new File(Constants.VERIFIKA_PROFILES_DIR + File.separator + profile);
            FileUtils.writeStringToFile(newProfile, profileString, "UTF-8");

        } catch (IOException ex) {
            Logger.getLogger(ProfileCreator.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
        if(newProfile.exists()){
            return newProfile.getAbsolutePath();
        }
        return "";
    }

}
