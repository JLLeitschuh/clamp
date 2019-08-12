/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights
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
import React from 'react';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import NavDropdown from 'react-bootstrap/NavDropdown';
import 'bootstrap-css-only/css/bootstrap.min.css';
import styled from 'styled-components';
import { Link } from 'react-router-dom'

const StyledLink = styled(Link)`
	color: ${props => props.theme.menuColor};
	background-color: ${props => props.theme.menuBackgroundColor};
	font-weight: normal;
	display: block;
	width: 100%;
	padding: .25rem 1.5rem;
	clear: both;
	text-align: inherit;
	white-space: nowrap;
	border: 0;
	:hover {
			text-decoration: none;
			background-color: ${props => props.theme.loopViewerHeaderBackgroundColor};
			color:  ${props => props.theme.loopViewerHeaderFontColor};
	}
`;
const StyledNavLink = styled(Nav.Link)`
	color: ${props => props.theme.menuColor};
	background-color: ${props => props.theme.menuBackgroundColor};
  font-weight: normal;
	padding: .25rem 1.5rem;
	:hover {
			background-color: ${props => props.theme.loopViewerHeaderBackgroundColor};
			color:  ${props => props.theme.loopViewerHeaderFontColor}
	}
`;
export default class MenuBar extends React.Component {

	render () {
		return (
				<Navbar.Collapse>
					<NavDropdown title="Closed Loop">
							<NavDropdown.Item as={StyledLink} to="/openLoop">Open CL</NavDropdown.Item>
							<NavDropdown.Item as={StyledLink} to="/loopProperties">Properties CL</NavDropdown.Item>
							<NavDropdown.Item as={StyledLink} to="/closeLoop">Close Model</NavDropdown.Item>
					</NavDropdown>
					<NavDropdown title="Manage">
							<NavDropdown.Item as={StyledLink} to="/submit">Submit</NavDropdown.Item>
							<NavDropdown.Item as={StyledLink} to="/stop">Stop</NavDropdown.Item>
							<NavDropdown.Item as={StyledLink} to="/restart">Restart</NavDropdown.Item>
							<NavDropdown.Item as={StyledLink} to="/delete">Delete</NavDropdown.Item>
							<NavDropdown.Item as={StyledLink} to="/deploy">Deploy</NavDropdown.Item>
							<NavDropdown.Item as={StyledLink} to="/undeploy">UnDeploy</NavDropdown.Item>
					</NavDropdown>
					<NavDropdown title="View">
							<NavDropdown.Item as={StyledLink} to="/refreshStatus">Refresh Status</NavDropdown.Item>
					</NavDropdown>
					<NavDropdown title="Help">
							<StyledNavLink href="https://wiki.onap.org/" target="_blank">Wiki</StyledNavLink>
							<StyledNavLink href="mailto:onap-discuss@lists.onap.org?subject=CLAMP&body=Please send us suggestions or feature enhancements or defect. If possible, please send us the steps to replicate any defect.">Contact Us</StyledNavLink>
							<NavDropdown.Item as={StyledLink} to="/userInfo">User Info</NavDropdown.Item>
					</NavDropdown>
				</Navbar.Collapse>
		);
	}
}
