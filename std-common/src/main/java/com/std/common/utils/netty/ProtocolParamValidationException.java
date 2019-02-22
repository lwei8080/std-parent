package com.std.common.utils.netty;


/**
 * 异常：协议参数校验异常
 *
 * @author yang.hao
 * @since 2011-10-31 上午11:03:33
 */
public class ProtocolParamValidationException
        extends AccessException {
    private static final long serialVersionUID = 1L;

    public ProtocolParamValidationException() {
        super();
    }

    public ProtocolParamValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProtocolParamValidationException(String message) {
        super(message);
    }

    public ProtocolParamValidationException(Throwable cause) {
        super(cause);
    }
}
