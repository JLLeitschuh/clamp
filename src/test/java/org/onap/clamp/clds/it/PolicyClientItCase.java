/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights
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
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */

package org.onap.clamp.clds.it;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.clamp.clds.client.req.policy.OperationalPolicyReq;
import org.onap.clamp.clds.client.req.policy.PolicyClient;
import org.onap.clamp.clds.client.req.tca.TcaRequestFormatter;
import org.onap.clamp.clds.config.ClampProperties;
import org.onap.clamp.clds.model.CldsEvent;
import org.onap.clamp.clds.model.properties.ModelProperties;
import org.onap.clamp.clds.model.properties.Policy;
import org.onap.clamp.clds.model.properties.PolicyChain;
import org.onap.clamp.clds.model.properties.Tca;
import org.onap.clamp.clds.util.ResourceFileUtil;
import org.onap.policy.api.AttributeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Test Policy API in org.onap.clamp.ClampDesigner.client package - replicate
 * Policy Delegates in tests.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PolicyClientItCase {

    @Autowired
    private ClampProperties refProp;
    @Autowired
    protected PolicyClient policyClient;
    String modelProp;
    String modelBpmnProp;
    String modelName;
    String controlName;

    /**
     * Initialize Test.
     */
    @Before
    public void setUp() throws IOException {
        modelProp = ResourceFileUtil.getResourceAsString("example/model-properties/policy/modelBpmnProperties.json");
        modelBpmnProp = ResourceFileUtil.getResourceAsString("example/model-properties/policy/modelBpmn.json");
        modelName = "example-model06";
        controlName = "ClosedLoop_FRWL_SIG_fad4dcae_e498_11e6_852e_0050568c4ccf";
    }

    private void createUpdateOperationalPolicy(String actionCd) throws Exception {
        ModelProperties prop = new ModelProperties(modelName, controlName, actionCd, false, modelBpmnProp, modelProp);
        Policy policy = prop.getType(Policy.class);
        if (policy.isFound()) {
            for (PolicyChain policyChain : policy.getPolicyChains()) {
                String operationalPolicyRequestUuid = UUID.randomUUID().toString();
                Map<AttributeType, Map<String, String>> attributes = OperationalPolicyReq.formatAttributes(refProp,
                        prop, policy.getId(), policyChain);
                policyClient.sendBrmsPolicy(attributes, prop, operationalPolicyRequestUuid);
            }
        }
    }

    private void createUpdateTcaPolicy(String actionCd) throws Exception {
        ModelProperties prop = new ModelProperties(modelName, controlName, actionCd, false, modelBpmnProp, modelProp);
        Tca tca = prop.getType(Tca.class);
        if (tca.isFound()) {
            String tcaPolicyRequestUuid = UUID.randomUUID().toString();
            String policyJson = TcaRequestFormatter.createPolicyJson(refProp, prop);
            try {
                policyClient.sendMicroServiceInJson(policyJson, prop, tcaPolicyRequestUuid);
            } catch (Exception e) {
                assertTrue(e.getMessage().contains("Policy send failed: PE500 "));
            }
        }
    }

    private void deleteOperationalPolicy(String actionCd) throws Exception {
        ModelProperties prop = new ModelProperties(modelName, controlName, actionCd, false, modelBpmnProp, modelProp);
        Policy policy = prop.getType(Policy.class);
        if (policy.isFound()) {
            prop.setCurrentModelElementId(policy.getId());
            for (PolicyChain policyChain : policy.getPolicyChains()) {
                prop.setPolicyUniqueId(policyChain.getPolicyId());
                policyClient.deleteBrms(prop);
            }
        }
    }

    private void deleteTcaPolicy(String actionCd) throws Exception {
        ModelProperties prop = new ModelProperties(modelName, controlName, actionCd, false, modelBpmnProp, modelProp);
        Tca tca = prop.getType(Tca.class);
        if (tca.isFound()) {
            prop.setCurrentModelElementId(tca.getId());
            try {
                policyClient.deleteMicrosService(prop);
            } catch (Exception e) {
                assertTrue(e.getMessage().contains("Policy delete failed: PE500 "));
            }
        }
    }

    // @Test
    /**
     * Temporarily disabled Test.
     */
    public void testCreateUpdateDeleteOperationalPolicy() throws Exception {
        createUpdateOperationalPolicy(CldsEvent.ACTION_SUBMIT);
        TimeUnit.SECONDS.sleep(20);
        deleteOperationalPolicy(CldsEvent.ACTION_DELETE);
    }

    @Test
    public void testCreateUpdateDeleteTcaPolicy() throws Exception {
        createUpdateTcaPolicy(CldsEvent.ACTION_SUBMIT);
        TimeUnit.SECONDS.sleep(20);
        deleteTcaPolicy(CldsEvent.ACTION_DELETE);
    }
}
