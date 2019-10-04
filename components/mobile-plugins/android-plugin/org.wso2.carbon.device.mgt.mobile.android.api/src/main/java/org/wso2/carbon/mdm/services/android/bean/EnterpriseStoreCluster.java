/*
 * Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.wso2.carbon.mdm.services.android.bean;

import java.util.List;

public class EnterpriseStoreCluster {

    String pageId;
    String clusterId;
    String name;
    List<EnterpriseStorePackages> products;
    String orderInPage;

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<EnterpriseStorePackages> getProducts() {
        return products;
    }

    public void setProducts(List<EnterpriseStorePackages> products) {
        this.products = products;
    }

    public String getOrderInPage() {
        return orderInPage;
    }

    public void setOrderInPage(String orderInPage) {
        this.orderInPage = orderInPage;
    }


}
