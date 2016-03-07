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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 15/02/2016
 *         Time: 12:29
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:metadatalib-spring-config.xml")
public class PublishValidatorTests {

    @Autowired
    private PublishValidator publishValidator;

    @Test
    public void testScenarioOne(){
        PublishInfo publishInfo = new PublishInfo(true);
        publishInfo.addToFileSet("Executable_pharmml.xml","Executable pharmml");
        publishInfo.addToFileSet("Executable_mdl.mdl","Executable mdl");
        publishInfo.addToFileSet("Real_data","Real data");
        publishInfo.addToFileSet("Output_real_","Output real");
        publishInfo.addToFileSet("Command.txt","Command file");
        assertTrue(publishValidator.validatePublish(publishInfo));
    }

    @Test
    public void testScenarioTwo(){
        PublishInfo publishInfo = new PublishInfo(true);
        publishInfo.addToFileSet("pharmml.xml","nonexecutable pharmml");
        publishInfo.addToFileSet("mdl.mdl","nonexecutable mdl");
        publishInfo.addToFileSet("Executable_file","Executable file");
        publishInfo.addToFileSet("Real_data","Real data");
        publishInfo.addToFileSet("Output_real_","Output real");
        publishInfo.addToFileSet("Command.txt","Command file");
        assertTrue(publishValidator.validatePublish(publishInfo));
    }

    @Test
    public void testScenarioThree(){
        PublishInfo publishInfo = new PublishInfo(true);
        publishInfo.addToFileSet("pharmml.xml","nonexecutable pharmml");
        publishInfo.addToFileSet("mdl.mdl","nonexecutable mdl");
        assertTrue(publishValidator.validatePublish(publishInfo));
    }

    @Test
    public void testScenarioFour(){
        PublishInfo publishInfo = new PublishInfo(true);
        publishInfo.addToFileSet("Executable_pharmml.xml","Executable pharmml");
        publishInfo.addToFileSet("mdl.mdl","nonexecutable mdl");
        publishInfo.addToFileSet("Real_data","Real data");
        publishInfo.addToFileSet("Output_real_","Output real");
        publishInfo.addToFileSet("Command.txt","Command file");
        assertTrue(publishValidator.validatePublish(publishInfo));
    }

    @Test
    public void testNoModelComplianceFile(){
        PublishInfo publishInfo = new PublishInfo(false);
        publishInfo.addToFileSet("Executable_pharmml.xml","Executable pharmml");
        publishInfo.addToFileSet("Executable_mdl.mdl","Executable mdl");
        publishInfo.addToFileSet("Real_data","Real data");
        publishInfo.addToFileSet("Output_real_","Output real");
        publishInfo.addToFileSet("Command.txt","Command file");
        assertFalse(publishValidator.validatePublish(publishInfo));
    }

    @Test
    public void testInvalidPharmmlFile(){
        PublishInfo publishInfo = new PublishInfo(true);
        publishInfo.addToFileSet("pharmml","nonexecutable pharmml");
        publishInfo.addToFileSet("mdl.mdl","nonexecutable mdl");
        publishInfo.addToFileSet("Executable_file","Executable file");
        publishInfo.addToFileSet("Real_data","Real data");
        publishInfo.addToFileSet("Output_real_","Output real");
        publishInfo.addToFileSet("Command.txt","Command file");
        assertFalse(publishValidator.validatePublish(publishInfo));
    }

    @Test
    public void testMissingDataFile(){
        PublishInfo publishInfo = new PublishInfo(true);
        publishInfo.addToFileSet("Executable_pharmml.xml","Executable pharmml");
        publishInfo.addToFileSet("mdl.mdl","nonexecutable mdl");
        publishInfo.addToFileSet("Output_real_","Output real");
        publishInfo.addToFileSet("Command.txt","Command file");
        assertFalse(publishValidator.validatePublish(publishInfo));
    }

    @Test
    public void testMissingCommandFile(){
        PublishInfo publishInfo = new PublishInfo(true);
        publishInfo.addToFileSet("Executable_pharmml.xml","Executable pharmml");
        publishInfo.addToFileSet("mdl.mdl","nonexecutable mdl");
        publishInfo.addToFileSet("Real_data","Real data");
        publishInfo.addToFileSet("Output_real_","Output real");
        assertFalse(publishValidator.validatePublish(publishInfo));
    }

}
