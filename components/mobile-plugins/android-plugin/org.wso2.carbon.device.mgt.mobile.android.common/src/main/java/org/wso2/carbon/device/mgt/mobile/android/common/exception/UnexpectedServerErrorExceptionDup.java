package org.wso2.carbon.device.mgt.mobile.android.common.exception;

public class UnexpectedServerErrorExceptionDup extends AndroidDeviceMgtPluginException{

    public UnexpectedServerErrorExceptionDup(String message, Throwable ex) {
        super(message, ex);
    }

    public UnexpectedServerErrorExceptionDup(String message) {
        super(message);
    }
}
