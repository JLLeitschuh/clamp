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
import ReactDOM from 'react-dom';

import { Route, Switch, BrowserRouter } from 'react-router-dom'
import OnapClamp from './OnapClamp';
import NotFound from './components/app/NotFound';
import LoginPage from './components/app/login/LoginPage';
import LoginFailedPage from './components/app/login/LoginFailedPage';
import BasicAuthLogin from './components/app/login/BasicAuthLogin';
import LoginRoute from './components/route/LoginRoute';


const routing = (
  <BrowserRouter>
    <div>
      <Switch>
        <LoginRoute exact path="/" component={OnapClamp} />
        <Route path="/basicAuthLogin" component={BasicAuthLogin} />
        <Route path="/login" component={LoginPage} />
        <Route path="/loginFailed" component={LoginFailedPage} />
        <Route component={NotFound} />
      </Switch>
		</div>
  </BrowserRouter>
)

ReactDOM.render(routing, document.getElementById('root'))
