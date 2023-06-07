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
package io.entgra.device.mgt.plugins.input.adapter.http.exception;

/**
 * This exception will thrown when content validator is failed to intialiaze.
 */
public class HTTPContentInitializationException extends RuntimeException {
	private String errMessage;

	public HTTPContentInitializationException(String msg, Exception nestedEx) {
		super(msg, nestedEx);
		setErrorMessage(msg);
	}

	public HTTPContentInitializationException(String message, Throwable cause) {
		super(message, cause);
		setErrorMessage(message);
	}

	public HTTPContentInitializationException(String msg) {
		super(msg);
		setErrorMessage(msg);
	}

	public HTTPContentInitializationException() {
		super();
	}

	public HTTPContentInitializationException(Throwable cause) {
		super(cause);
	}

	public String getErrorMessage() {
		return errMessage;
	}

	public void setErrorMessage(String errMessage) {
		this.errMessage = errMessage;
	}
}