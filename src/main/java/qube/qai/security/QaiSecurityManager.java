/*
 * Copyright 2017 Qoan Wissenschaft & Software. All rights reserved.
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

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import qube.qai.user.Permission;
import qube.qai.user.User;

import javax.inject.Inject;

public class QaiSecurityManager implements QaiSecurity {

    @Inject
    private QaiRealm realm;

    @Override
    public boolean hasPermission(User user, Permission permission) {

        PrincipalCollection principalCollection = new SimplePrincipalCollection();
        ((SimplePrincipalCollection) principalCollection).add(user, realm.getName());
        AuthorizationInfo info = realm.doGetAuthorizationInfo(principalCollection);
        if (info != null) {
            return info.getObjectPermissions().contains(permission);
        }
        return false;
    }
}
