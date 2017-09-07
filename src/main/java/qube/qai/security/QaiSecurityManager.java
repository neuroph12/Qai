/*
 * Copyright 2017 Qoan Software Association. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package qube.qai.security;

import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.mgt.SecurityManager;
import qube.qai.user.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class QaiSecurityManager implements QaiSecurity {

    @Inject
    private SecurityManager securityManager;

    @Inject
    private EntityManager entityManager;

    public User authenticateUser(String username, String password) throws QaiAuthenticationException {

        AuthenticationToken token = null;
        QaiSaltedAuthentificationInfo info = (QaiSaltedAuthentificationInfo) securityManager.authenticate(token);

        return info.getUser();
    }

    public String createUser(String username, String password) {
        User user = new User(username, password);

        entityManager.persist(user);

        return user.getUuid();
    }
}
