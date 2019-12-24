package org.wso2.carbon.device.mgt.mobile.android.common.exception;

public class UnexpectedServerErrorException extends AndroidDeviceMgtPluginException{

    public UnexpectedServerErrorException(String message, Throwable ex) {
        super(message, ex);
    }

    public UnexpectedServerErrorException(String message) {
        super(message);
    }
}
