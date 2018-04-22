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

package qube.qai.parsers;


/**
 * Created by rainbird on 3/29/17.
 */
public class AppInfo {

    public static final String DEFAULT_CHARSET = "UTF-8";
    public static final String APPINFO_INSTANCE = "AppInfoInstance";
    private String serverBaseUri;
    private String serverBaseUrl;

    public String getId() {
        return null;
    }

    public String getServerBaseUri() {
        return this.serverBaseUri;
    }

    public void setServerBaseUri(String baseUriServer) {
        this.serverBaseUri = serverBaseUri;
    }

    public String getServerBaseUrl() {
        return this.serverBaseUrl;
    }

    public void setServerBaseUrl(String serverUrl) {
        this.serverBaseUrl = serverUrl;
    }


}
