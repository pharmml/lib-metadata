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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashSet;


/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 15/02/2016
 *         Time: 11:13
 */
public class PublishValidatorImpl implements PublishValidator {

    private static final Log logger = LogFactory.getLog(PublishValidatorImpl.class);

    private PublishInfo publishInfo;
    private HashSet<FileType> fileMap;

    public boolean validatePublish(PublishInfo publishInfo){
        this.publishInfo = publishInfo;
        fileMap = publishInfo.getFileSet();
        return checkPublishScenarios();
    }

    private boolean checkPublishScenarios(){
        if (checkScenarioOne())
            return true;
        else if (checkScenarioTwo())
            return true;
        else if (checkScenarioThree())
            return true;
        else if (checkScenarioFour())
            return true;
        return false;
    }

    private boolean checkScenarioOne(){
        if (fileMap.contains(FileType.EXE_PHARMML) &&
                fileMap.contains(FileType.EXE_MDL) &&
                publishInfo.validDataFile() &&
                fileMap.contains(FileType.COMMAND) &&
                publishInfo.validModelAccomodation()){
            logger.info("Successful scenario one");
            return true;
        }
        return false;
    }

    private boolean checkScenarioTwo(){
        if (fileMap.contains(FileType.NONEXE_PHARMML) &&
                fileMap.contains(FileType.NONEXE_MDL) &&
                fileMap.contains(FileType.EXE_FILE) &&
                publishInfo.validDataFile() &&
                fileMap.contains(FileType.COMMAND) &&
                publishInfo.validModelAccomodation()){
            logger.info("Successful scenario two");
            return true;
        }
        return false;
    }

    private boolean checkScenarioThree(){
        if (fileMap.contains(FileType.NONEXE_PHARMML) &&
                fileMap.contains(FileType.NONEXE_MDL) &&
                publishInfo.validModelAccomodation()){
            logger.info("Successful scenario three");
            return true;
        }
        return false;
    }

    private boolean checkScenarioFour(){
        if ((fileMap.contains(FileType.EXE_PHARMML) || fileMap.contains(FileType.EXE_MDL)) &&
                publishInfo.validDataFile() &&
                fileMap.contains(FileType.COMMAND) &&
                publishInfo.validModelAccomodation()){
            logger.info("Successful scenario four");
            return true;
        }
        return false;
    }

    


}
