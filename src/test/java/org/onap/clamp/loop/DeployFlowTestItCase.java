/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property. All rights
 *                             reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END============================================
 * ===================================================================
 *
 */

package org.onap.clamp.loop;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.clamp.clds.Application;
import org.onap.clamp.loop.template.LoopTemplate;
import org.onap.clamp.policy.microservice.MicroServicePolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class DeployFlowTestItCase {
    private Gson gson = new Gson();

    @Autowired
    CamelContext camelContext;

    @Autowired
    LoopService loopService;

    @Autowired
    LoopsRepository loopsRepository;

    @Test
    @Transactional
    public void deployWithSingleBlueprintTest() throws JsonSyntaxException, IOException {
        Loop loopTest = createLoop("ControlLoopTest", "<xml></xml>", "yamlcontent", "{\"testname\":\"testvalue\"}",
                "UUID-blueprint");
        LoopTemplate template = new LoopTemplate();
        template.setName("templateName");
        template.setBlueprint("yamlcontent");
        loopTest.setLoopTemplate(template);
        MicroServicePolicy microServicePolicy = getMicroServicePolicy("configPolicyTest", "",
                "{\"configtype\":\"json\"}", "tosca_definitions_version: tosca_simple_yaml_1_0_0",
                "{\"param1\":\"value1\"}", true);
        loopTest.addMicroServicePolicy(microServicePolicy);
        loopService.saveOrUpdateLoop(loopTest);
        Exchange myCamelExchange = ExchangeBuilder.anExchange(camelContext).withProperty("loopObject", loopTest)
                .build();

        camelContext.createProducerTemplate().send("direct:deploy-loop", myCamelExchange);

        Loop loopAfterTest = loopService.getLoop("ControlLoopTest");
        assertThat(loopAfterTest.getDcaeDeploymentStatusUrl()).isNotNull();
        assertThat(loopAfterTest.getDcaeDeploymentId()).isNotNull();
    }

    @Test
    @Transactional
    public void deployWithMultipleBlueprintTest() throws JsonSyntaxException, IOException {
        Loop loopTest2 = createLoop("ControlLoopTest2", "<xml></xml>", "yamlcontent", "{\"dcaeDeployParameters\": {"
                + "\"microService1\": {\"location_id\": \"\", \"policy_id\": \"TCA_ResourceInstanceName1_tca\"},"
                + "\"microService2\": {\"location_id\": \"\", \"policy_id\": \"TCA_ResourceInstanceName2_tca\"}"
                + "}}", "UUID-blueprint");
        LoopTemplate template = new LoopTemplate();
        template.setName("templateName");
        loopTest2.setLoopTemplate(template);
        MicroServicePolicy microServicePolicy1 = getMicroServicePolicy("microService1", "", "{\"configtype\":\"json\"}",
                "tosca_definitions_version: tosca_simple_yaml_1_0_0", "{\"param1\":\"value1\"}", true);
        MicroServicePolicy microServicePolicy2 = getMicroServicePolicy("microService2", "", "{\"configtype\":\"json\"}",
                "tosca_definitions_version: tosca_simple_yaml_1_0_0", "{\"param1\":\"value1\"}", true);
        loopTest2.addMicroServicePolicy(microServicePolicy1);
        loopTest2.addMicroServicePolicy(microServicePolicy2);
        loopsRepository.saveAndFlush(loopTest2);
        Exchange myCamelExchange = ExchangeBuilder.anExchange(camelContext).withProperty("loopObject", loopTest2)
                .build();

        camelContext.createProducerTemplate().send("direct:deploy-loop", myCamelExchange);

        Loop loopAfterTest = loopService.getLoop("ControlLoopTest2");
        Set<MicroServicePolicy> policyList = loopAfterTest.getMicroServicePolicies();
        for (MicroServicePolicy policy : policyList) {
            assertThat(policy.getDcaeDeploymentStatusUrl()).isNotNull();
            assertThat(policy.getDcaeDeploymentId()).isNotNull();
        }
        assertThat(loopAfterTest.getDcaeDeploymentStatusUrl()).isNull();
        assertThat(loopAfterTest.getDcaeDeploymentId()).isNull();
    }

    @Test
    @Transactional
    public void undeployWithSingleBlueprintTest() throws JsonSyntaxException, IOException {
        Loop loopTest = createLoop("ControlLoopTest", "<xml></xml>", "yamlcontent", "{\"testname\":\"testvalue\"}",
                "UUID-blueprint");
        LoopTemplate template = new LoopTemplate();
        template.setName("templateName");
        template.setBlueprint("yamlcontent");
        loopTest.setLoopTemplate(template);
        loopTest.setDcaeDeploymentId("testDeploymentId");
        loopTest.setDcaeDeploymentStatusUrl("testUrl");
        MicroServicePolicy microServicePolicy = getMicroServicePolicy("configPolicyTest", "",
                "{\"configtype\":\"json\"}", "tosca_definitions_version: tosca_simple_yaml_1_0_0",
                "{\"param1\":\"value1\"}", true);
        loopTest.addMicroServicePolicy(microServicePolicy);
        loopService.saveOrUpdateLoop(loopTest);
        Exchange myCamelExchange = ExchangeBuilder.anExchange(camelContext).withProperty("loopObject", loopTest)
                .build();

        camelContext.createProducerTemplate().send("direct:undeploy-loop", myCamelExchange);

        Loop loopAfterTest = loopService.getLoop("ControlLoopTest");
        assertThat(loopAfterTest.getDcaeDeploymentStatusUrl().contains("/uninstall")).isTrue();
    }

    @Test
    @Transactional
    public void undeployWithMultipleBlueprintTest() throws JsonSyntaxException, IOException {
        Loop loopTest2 = createLoop("ControlLoopTest2", "<xml></xml>", "yamlcontent", "{\"dcaeDeployParameters\": {"
                + "\"microService1\": {\"location_id\": \"\", \"policy_id\": \"TCA_ResourceInstanceName1_tca\"},"
                + "\"microService2\": {\"location_id\": \"\", \"policy_id\": \"TCA_ResourceInstanceName2_tca\"}"
                + "}}", "UUID-blueprint");
        LoopTemplate template = new LoopTemplate();
        template.setName("templateName");
        loopTest2.setLoopTemplate(template);
        MicroServicePolicy microServicePolicy1 = getMicroServicePolicy("microService1", "", "{\"configtype\":\"json\"}",
                "tosca_definitions_version: tosca_simple_yaml_1_0_0", "{\"param1\":\"value1\"}", true,
                "testDeploymentId1", "testDeploymentStatusUrl1");
        MicroServicePolicy microServicePolicy2 = getMicroServicePolicy("microService2", "", "{\"configtype\":\"json\"}",
                "tosca_definitions_version: tosca_simple_yaml_1_0_0", "{\"param1\":\"value1\"}", true,
                "testDeploymentId2", "testDeploymentStatusUrl2");
        loopTest2.addMicroServicePolicy(microServicePolicy1);
        loopTest2.addMicroServicePolicy(microServicePolicy2);
        loopsRepository.saveAndFlush(loopTest2);
        Exchange myCamelExchange = ExchangeBuilder.anExchange(camelContext).withProperty("loopObject", loopTest2)
                .build();

        camelContext.createProducerTemplate().send("direct:undeploy-loop", myCamelExchange);

        Loop loopAfterTest = loopService.getLoop("ControlLoopTest2");
        Set<MicroServicePolicy> policyList = loopAfterTest.getMicroServicePolicies();
        for (MicroServicePolicy policy : policyList) {
            assertThat(policy.getDcaeDeploymentStatusUrl().contains("/uninstall")).isTrue();
        }
        assertThat(loopAfterTest.getDcaeDeploymentStatusUrl()).isNull();
        assertThat(loopAfterTest.getDcaeDeploymentId()).isNull();
    }


    @Test
    @Transactional
    public void getStatusWithSingleBlueprintTest() throws JsonSyntaxException, IOException {
        Loop loopTest = createLoop("ControlLoopTest", "<xml></xml>", "yamlcontent", "{\"testname\":\"testvalue\"}",
                "UUID-blueprint");
        LoopTemplate template = new LoopTemplate();
        template.setName("templateName");
        template.setBlueprint("yamlcontent");
        loopTest.setLoopTemplate(template);
        MicroServicePolicy microServicePolicy = getMicroServicePolicy("configPolicyTest", "",
                "{\"configtype\":\"json\"}", "tosca_definitions_version: tosca_simple_yaml_1_0_0",
                "{\"param1\":\"value1\"}", true);
        loopTest.addMicroServicePolicy(microServicePolicy);
        loopService.saveOrUpdateLoop(loopTest);
        assertThat(loopTest.getComponents().size()).isEqualTo(2);
        assertThat(loopTest.getComponent("DCAE")).isNotNull();
        assertThat(loopTest.getComponent("POLICY")).isNotNull();
        Exchange myCamelExchange = ExchangeBuilder.anExchange(camelContext).withProperty("loopObject", loopTest)
                .build();

        camelContext.createProducerTemplate().send("direct:update-dcae-status-for-loop", myCamelExchange);

        assertThat(loopTest.getComponent("DCAE").getState().getStateName()).isEqualTo("BLUEPRINT_DEPLOYED");

        Loop loopAfterTest = loopService.getLoop("ControlLoopTest");
        assertThat(loopAfterTest.getComponents().size()).isEqualTo(2);
        assertThat(loopAfterTest.getComponent("DCAE")).isNotNull();
        assertThat(loopAfterTest.getComponent("POLICY")).isNotNull();
    }

    @Test
    @Transactional
    public void getStatusWithMultipleBlueprintTest() throws JsonSyntaxException, IOException {
        Loop loopTest = createLoop("ControlLoopTest", "<xml></xml>", "yamlcontent", "{\"testname\":\"testvalue\"}",
                "UUID-blueprint");
        LoopTemplate template = new LoopTemplate();
        template.setName("templateName");
        loopTest.setLoopTemplate(template);
        MicroServicePolicy microServicePolicy = getMicroServicePolicy("configPolicyTest", "",
                "{\"configtype\":\"json\"}", "tosca_definitions_version: tosca_simple_yaml_1_0_0",
                "{\"param1\":\"value1\"}", true);
        MicroServicePolicy microServicePolicy2 = getMicroServicePolicy("configPolicyTest2", "",
                "{\"configtype\":\"json\"}", "tosca_definitions_version: tosca_simple_yaml_1_0_0",
                "{\"param1\":\"value1\"}", true);
        loopTest.addMicroServicePolicy(microServicePolicy);
        loopTest.addMicroServicePolicy(microServicePolicy2);
        loopService.saveOrUpdateLoop(loopTest);
        assertThat(loopTest.getComponents().size()).isEqualTo(3);
        assertThat(loopTest.getComponent("DCAE")).isNull();
        assertThat(loopTest.getComponent("DCAE_configPolicyTest")).isNotNull();
        assertThat(loopTest.getComponent("DCAE_configPolicyTest2")).isNotNull();
        assertThat(loopTest.getComponent("POLICY")).isNotNull();
        Exchange myCamelExchange = ExchangeBuilder.anExchange(camelContext).withProperty("loopObject", loopTest)
                .build();

        camelContext.createProducerTemplate().send("direct:update-dcae-status-for-loop", myCamelExchange);

        assertThat(loopTest.getComponent("DCAE_configPolicyTest").getState().getStateName())
            .isEqualTo("BLUEPRINT_DEPLOYED");
        assertThat(loopTest.getComponent("DCAE_configPolicyTest2").getState().getStateName())
            .isEqualTo("BLUEPRINT_DEPLOYED");

        Loop loopAfterTest = loopService.getLoop("ControlLoopTest");
        assertThat(loopAfterTest.getComponents().size()).isEqualTo(3);
        assertThat(loopAfterTest.getComponent("DCAE")).isNull();
        assertThat(loopAfterTest.getComponent("POLICY")).isNotNull();
        assertThat(loopTest.getComponent("DCAE_configPolicyTest")).isNotNull();
        assertThat(loopTest.getComponent("DCAE_configPolicyTest2")).isNotNull();
    }

    private Loop createLoop(String name, String svgRepresentation, String blueprint, String globalPropertiesJson,
            String dcaeBlueprintId) throws JsonSyntaxException, IOException {
        Loop loop = new Loop(name, svgRepresentation);
        loop.setGlobalPropertiesJson(new Gson().fromJson(globalPropertiesJson, JsonObject.class));
        loop.setLastComputedState(LoopState.DESIGN);
        return loop;
    }

    private MicroServicePolicy getMicroServicePolicy(String name, String modelType, String jsonRepresentation,
            String policyTosca, String jsonProperties, boolean shared) {
        MicroServicePolicy microService = new MicroServicePolicy(name, modelType, policyTosca, shared,
                gson.fromJson(jsonRepresentation, JsonObject.class), new HashSet<>());
        microService.setConfigurationsJson(new Gson().fromJson(jsonProperties, JsonObject.class));
        return microService;
    }

    private MicroServicePolicy getMicroServicePolicy(String name, String modelType, String jsonRepresentation,
            String policyTosca, String jsonProperties, boolean shared, String deploymengId,
            String deploymentStatusUrl) {
        MicroServicePolicy microService = getMicroServicePolicy(name, modelType, jsonRepresentation, policyTosca,
                jsonProperties, shared);

        microService.setDcaeDeploymentId(deploymengId);
        microService.setDcaeDeploymentStatusUrl(deploymentStatusUrl);
        return microService;
    }
}
