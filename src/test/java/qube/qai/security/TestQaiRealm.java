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

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import qube.qai.main.QaiTestBase;
import qube.qai.services.implementation.UUIDService;
import qube.qai.user.Permission;
import qube.qai.user.Role;
import qube.qai.user.User;

public class TestQaiRealm extends QaiTestBase {

    public void testQaiRealm() throws Exception {

        QaiRealm realm = new QaiRealm();
        injector.injectMembers(realm);

        String username = createUsername();
        String password = "password";
        User user = realm.createUser(username, password, "admin", "do all", "see all", "delete all");
        assertNotNull(user);
        logger.info("Created user with username: " + user.getUsername());

        User foundUser = realm.findUser(username);
        assertNotNull("there has to be a found user", foundUser);
        assertTrue("users must be same", user.equals(foundUser));
        logger.info("found user " + username + " to have " + foundUser.getUuid());

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        AuthenticationInfo authInfo = realm.doGetAuthenticationInfo(token);
        logger.info("Authentication-Info: " + authInfo.toString());
        assertNotNull("there has to be an auth-info", authInfo);
        User infoUser = authInfo.getPrincipals().oneByType(User.class);
        assertNotNull("there has to be a user", infoUser);
        assertTrue("there has to be a role", !infoUser.getRoles().isEmpty());
        assertTrue(infoUser.getRoles().contains(new Role("admin")));

        PrincipalCollection principals = new SimplePrincipalCollection();
        ((SimplePrincipalCollection) principals).add(user, "QaiRealm");
        AuthorizationInfo atrInfo = realm.doGetAuthorizationInfo(principals);
        logger.info("Authorization-Info: " + atrInfo.toString());
        assertNotNull("there has to be autorization info", atrInfo);
        assertTrue("the roles may not miss", atrInfo.getRoles().contains("admin"));
        assertTrue("do all permission missing", atrInfo.getObjectPermissions().contains(new Permission("do all")));
        assertTrue("see all permission missing", atrInfo.getObjectPermissions().contains(new Permission("see all")));
        assertTrue("delete all permission missing", atrInfo.getObjectPermissions().contains(new Permission("delete all")));
    }

    public void testSecutrity() throws Exception {

        org.apache.shiro.mgt.SecurityManager securityManager = injector.getInstance(org.apache.shiro.mgt.SecurityManager.class);
        SecurityUtils.setSecurityManager(securityManager);

        QaiRealm realm = new QaiRealm();
        injector.injectMembers(realm);

        Subject subject = SecurityUtils.getSubject();
        assertNotNull("there has to be a subject", subject);


        String username = createUsername();
        String password = "password";
        User user = realm.createUser(username, password, "admin", "do all", "see all", "delete all");
        assertNotNull(user);
        logger.info("Created user with username: " + user.getUsername());

        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        subject.login(token);

        User authUser = (User) subject.getPrincipal();
        assertNotNull("there has to be be a user too", authUser);
        logger.info("Authenticated user with username: " + authUser.getUsername());
    }

    private String createUsername() {
        return "user_" + UUIDService.uuidString();
    }
}
