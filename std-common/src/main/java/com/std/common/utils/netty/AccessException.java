package com.std.common.utils.netty;


/**
 * top exception
 *
 * @author yang.hao
 * @since 2011-10-26 下午4:52:38
 */
public class AccessException
        extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AccessException() {
        super();
    }

    public AccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccessException(String message) {
        super(message);
    }

    public AccessException(Throwable cause) {
        super(cause);
    }
}
