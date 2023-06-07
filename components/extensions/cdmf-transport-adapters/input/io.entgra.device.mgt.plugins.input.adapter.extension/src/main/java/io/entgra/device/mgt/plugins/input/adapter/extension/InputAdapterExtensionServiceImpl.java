/*
 * Copyright (c) 2018 - 2023, Entgra (Pvt) Ltd. (http://www.entgra.io) All Rights Reserved.
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
 */

package io.entgra.device.mgt.plugins.input.adapter.extension;

import io.entgra.device.mgt.plugins.input.adapter.extension.internal.InputAdapterServiceDataHolder;

/**
 * This hold the input adapter extension service implementation.
 */
public class InputAdapterExtensionServiceImpl implements InputAdapterExtensionService {
    private static final String DEFAULT = "default";


    @Override
    public ContentValidator getContentValidator(String type) {
        return InputAdapterServiceDataHolder.getInstance().getContentValidatorMap().get(type);
    }

    @Override
    public ContentValidator getDefaultContentValidator() {
        return InputAdapterServiceDataHolder.getInstance().getContentValidatorMap().get(DEFAULT);
    }

    @Override
    public ContentTransformer getContentTransformer(String type) {
        return InputAdapterServiceDataHolder.getInstance().getContentTransformerMap().get(type);
    }

    @Override
    public ContentTransformer getDefaultContentTransformer() {
        return InputAdapterServiceDataHolder.getInstance().getContentTransformerMap().get(DEFAULT);
    }
}
