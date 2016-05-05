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

    public Scenario validatePublish(PublishInfo publishInfo){
        this.publishInfo = publishInfo;
        fileMap = publishInfo.getFileSet();
        return checkPublishScenarios();
    }

    private Scenario checkPublishScenarios(){
        if (checkScenarioOne())
            return Scenario.SCENARIO_1;
        else if (checkScenarioTwo())
            return Scenario.SCENARIO_2;
        else if (checkScenarioThree())
            return Scenario.SCENARIO_3;
        else if (checkScenarioFour())
            return Scenario.SCENARIO_4;
        return null;
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

    public PublishContext generatePublishContext(Scenario scenario){
        PublishContext publishContext = new PublishContext();
        String message = " It satisfies the minimal requirements for ";
        switch (scenario){
            case SCENARIO_1:publishContext.setMessage(message + "scenario 1.");
                break;
            case SCENARIO_2:publishContext.setMessage(message + "scenario 2.");
                break;
            case SCENARIO_3:publishContext.setMessage(message + "scenario 3.");
                break;
            case SCENARIO_4:publishContext.setMessage(message + "scenario 4.");
                break;
            default: publishContext = null;
                break;
        }
        return publishContext;
    }

    


}
