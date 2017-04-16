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

package qube.qai.persistence;

import org.encog.util.text.Base64;
import qube.qai.services.implementation.UUIDService;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by rainbird on 4/16/17.
 */
public class ResourceData implements Serializable {

    private String uuid;

    private String name;

    private String mimeType;

    private byte[] binaryData;

    /**
     * this class is meant to be used for serializing the images and other binary resources
     * which are kept in the wikipedia-resource archive, so that the images there can be
     * searched and displayed on screen when so desired. and since they will mainly be
     * used for transferring binary data, like images, or sound-files, it is better to uuencode
     * the data before transferring the data over grid.
     */
    public ResourceData() {
        this.uuid = UUIDService.uuidString();
    }

    public String getBinaryAsString() {
        if (this.binaryData != null) {
            return Base64.encodeBytes(binaryData);
        }
        return null;
    }

    public void setBinaryAsString(String binary) {
        if (binary != null) {
            try {
                this.binaryData = Base64.decode(binary);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        binaryData = null;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }
}
