/*
 * Copyright {yyyy} EMBL-EBI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.ddmore.publish.service;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 18/02/2016
 *         Time: 10:16
 */
public class PublishInfo {
    private HashSet<FileType> fileSet;
    private boolean modelComplianceWithOriginalPubliation;

    public PublishInfo(boolean modelComplianceWithOriginalPubliation) {
        this.modelComplianceWithOriginalPubliation = modelComplianceWithOriginalPubliation;
        fileSet = new HashSet<FileType>();
    }

    public void addToFileSet(String filePath, String description) {

        if (filePath.isEmpty() || description == null || description.isEmpty()) {
            return;
        }

        String name = filePath;
        if(name.lastIndexOf('\\') != -1)  {
            name = name.substring(name.lastIndexOf('\\') + 1);
        }

        if (name.startsWith("Executable_") && name.endsWith(".xml")) {
            fileSet.add(FileType.EXE_PHARMML);
        } else if (!name.startsWith("Executable_") && name.endsWith(".xml")) {
            fileSet.add(FileType.NONEXE_PHARMML);
        } else if (name.startsWith("Executable_") && name.endsWith(".mdl")) {
            fileSet.add(FileType.EXE_MDL);
        } else if (!name.startsWith("Executable_") && name.endsWith(".mdl")) {
            fileSet.add(FileType.NONEXE_MDL);
        } else if (name.startsWith("Executable_")) {
            fileSet.add(FileType.EXE_FILE);
        } else if (name.startsWith("Real_")) {
            fileSet.add(FileType.REAL_DATA);
        } else if (name.startsWith("Output_real_")) {
            fileSet.add(FileType.REAL_OUTPUT);
        } else if (name.startsWith("Simulated")) {
            fileSet.add(FileType.SIMULATED_DATA);
        } else if (name.startsWith("Output_simulated_")) {
            fileSet.add(FileType.SIMULATED_OUTPUT);
        } else if (name.equals("Command.txt")) {
            fileSet.add(FileType.COMMAND);
        } else if (name.equals("Model_Accommodations.txt")) {
            fileSet.add(FileType.MODEL_ADDOMODATAION);
        }
    }

    public boolean validDataFile(){
        if(fileSet.contains(FileType.REAL_DATA) && fileSet.contains(FileType.REAL_OUTPUT)){
            return true;
        }
        else if(fileSet.contains(FileType.SIMULATED_DATA) && fileSet.contains(FileType.SIMULATED_OUTPUT)) {
            return true;
        }
        return false;
    }

    public boolean validModelAccomodation(){
        if(!modelComplianceWithOriginalPubliation){
            if(fileSet.contains(FileType.MODEL_ADDOMODATAION))
                return true;
            else
                return false;
        }
        return true;
    }

    public HashSet<FileType> getFileSet() {
        return fileSet;
    }
}
