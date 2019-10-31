/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

export default class TemplateMenu {
  static getToscaModals() {
    return fetch('restservices/clds/v2/loop/tosca/models', { method: 'GET', credentials: 'same-origin', })
      .then(function (response) {
        console.debug("getToscaModels response received: ", response.status);
        if (response.ok) {
          return response.json();
        } else {
          console.error("getToscaModels query failed");
          return {};
        }
      })
      .catch(function (error) {
        console.error("getToscaModels error received", error);
        return {};
      });
  }
}
