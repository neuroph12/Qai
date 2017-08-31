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

import org.apache.shiro.authc.*;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import qube.qai.user.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class QaiRealm extends JdbcRealm {

    @Inject
    private EntityManager entityManager;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        UsernamePasswordToken userPassToken = (UsernamePasswordToken) token;
        String username = userPassToken.getUsername();
        String password = new String(userPassToken.getPassword());

        String queryString = "SELECT user FROM User AS user WHERE user.username = :username AND user.password = :password";
        Query query = entityManager.createQuery(queryString).setParameter("username", username).setParameter("password", password);
        User user = (User) query.getSingleResult();

        SaltedAuthenticationInfo info = null;
        if (user != null) {
            info = new QaiSaltedAuthentificationInfo(username, user.getPassword());
        }

        return info;

    }
}
