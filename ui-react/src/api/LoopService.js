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

export default class LoopService {
	static getLoopNames() {
		return fetch('/restservices/clds/v2/loop/getAllNames', { method: 'GET', credentials: 'include', })
			.then(function (response) {
				console.debug("GetLoopNames response received: ", response.status);
				if (response.ok) {
					return response.json();
				} else {
					console.error("GetLoopNames query failed");
					return {};
				}
			})
			.catch(function (error) {
				console.error("GetLoopNames error received", error);
				return {};
			});
	}

	static getLoop(loopName) {
		return fetch('/restservices/clds/v2/loop/' + loopName, {
			method: 'GET',
			headers: {
				"Content-Type": "application/json"
			},
			credentials: 'include',
		})
			.then(function (response) {
				console.debug("GetLoop response received: ", response.status);
				if (response.ok) {
					return response.json();
				} else {
					console.error("GetLoop query failed");
					return {};
				}
			})
			.catch(function (error) {
				console.error("GetLoop error received", error);
				return {};
			});
	}

	static getSvg(loopName) {
		return fetch('/restservices/clds/v2/loop/svgRepresentation/' + loopName, {
			method: 'GET',
			credentials: 'include',
		})
			.then(function (response) {
				console.debug("svgRepresentation response received: ", response.status);
				if (response.ok) {
					return response.text();
				} else {
					console.error("svgRepresentation query failed");
					return "";
				}
			})
			.catch(function (error) {
				console.error("svgRepresentation error received", error);
				return "";
			});
	}
}