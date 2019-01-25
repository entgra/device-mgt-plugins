/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 *
 * Copyright (c) 2019, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
 *
 * Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.mobile.windows.api.operations;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.wso2.carbon.device.mgt.mobile.windows.api.operations.util.Constants;

/**
 * Represents an items that should be retrieved from the device or a command.
 */
public class ItemTag {

    TargetTag target;
    SourceTag source;
    String data;
    MetaTag meta;
    Element elementData;

    public MetaTag getMeta() {
        return meta;
    }

    public void setMeta(MetaTag meta) {
        this.meta = meta;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public SourceTag getSource() {
        return source;
    }

    public void setSource(SourceTag source) {
        this.source = source;
    }

    public TargetTag getTarget() {
        return target;
    }

    public void setTarget(TargetTag target) {
        this.target = target;
    }

    public Element getElementData() {
        return elementData;
    }

    public void setElementData(Element elementData) {
        this.elementData = elementData;
    }

    public void buildItemElement(Document doc, Element rootElement) {
        Element item = doc.createElement(Constants.ITEM);
        rootElement.appendChild(item);

        if (getTarget() != null || getSource() != null) {

            if (getTarget() != null) {
                getTarget().buildTargetElement(doc, item);
            }
            if (getSource() != null) {
                getSource().buildSourceElement(doc, item);
            }
        }
        if (getMeta() != null) {
            getMeta().buildMetaElement(doc, item);
        }
        if (getData() != null || getElementData()!= null) {
            Element data = doc.createElement(Constants.DATA);
            if (getData() != null) {
                data.appendChild(doc.createTextNode(getData()));
            }
            if (getElementData() != null) {
                Node node = doc.importNode(getElementData(), true);
                data.appendChild(node);
            }
            item.appendChild(data);
        }
    }
}
