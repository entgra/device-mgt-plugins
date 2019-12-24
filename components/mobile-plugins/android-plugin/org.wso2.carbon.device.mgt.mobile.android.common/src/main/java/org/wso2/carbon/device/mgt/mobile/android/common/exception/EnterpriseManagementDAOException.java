/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 */
/*
 *   Copyright (c) 2019, Entgra (pvt) Ltd. (http://entgra.io) All Rights Reserved.
 *
 *   Entgra (Pvt) Ltd. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied. See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 */

package org.wso2.carbon.device.mgt.mobile.android.common.exception;

/**
 * Custom exception class for enterprise specific data access related exceptions.
 */
public class EnterpriseManagementDAOException extends Exception {

	private String message;
	private static final long serialVersionUID = 2021891702072938865L;

	/**
	 * Constructs a new EnterpriseManagementDAOException with the specified detail message and
	 * nested exception.
	 *
	 * @param message         error message
	 * @param nestedException exception
	 */
	public EnterpriseManagementDAOException(String message, Exception nestedException) {
		super(message, nestedException);
		setErrorMessage(message);
	}

	/**
	 * Constructs a new EnterpriseManagementDAOException with the specified detail message
	 * and cause.
	 *
	 * @param message the detail message.
	 * @param cause   the cause of this exception.
	 */
	public EnterpriseManagementDAOException(String message, Throwable cause) {
		super(message, cause);
		setErrorMessage(message);
	}

	/**
	 * Constructs a new EnterpriseManagementDAOException with the specified detail message.
	 *
	 * @param message the detail message.
	 */
	public EnterpriseManagementDAOException(String message) {
		super(message);
		setErrorMessage(message);
	}

	/**
	 * Constructs a new EnterpriseManagementDAOException with the specified and cause.
	 *
	 * @param cause the cause of this exception.
	 */
	public EnterpriseManagementDAOException(Throwable cause) {
		super(cause);
	}

	public String getMessage() {
		return message;
	}

	public void setErrorMessage(String errorMessage) {
		this.message = errorMessage;
	}

}
