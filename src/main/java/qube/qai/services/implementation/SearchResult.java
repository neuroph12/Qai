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

package qube.qai.services.implementation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * Created by rainbird on 11/27/15.
 */
public class SearchResult implements Serializable {

    private String context;

    private String uuid;

    private String title;

    private String description;

    private double relevance;

    public SearchResult() {
    }

    public SearchResult(String context, String title, String uuid, String description, double relevance) {
        this.context = context;
        this.title = title;
        this.uuid = uuid;
        this.description = description;
        this.relevance = relevance;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(uuid).append(context).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        SearchResult rhs = (SearchResult) obj;
        return new EqualsBuilder()
                .append(context, rhs.context)
                .append(uuid, rhs.uuid)
                .append(title, rhs.title)
                .append(description, rhs.description)
                .append(relevance, rhs.relevance)
                .isEquals();
    }
}
