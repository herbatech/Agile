package com.ads.agile.AgileCrashAnalytic.utils;



/**
 * An Exception indicating that the Crash Reporter has not been correctly initialized.
 */
public class AgileCrashReporterNotInitializedException extends AgileCrashReporterException {
    static final long serialVersionUID = 1;

    /**
     * Constructs a AgileCrashReporterNotInitializedException with no additional information.
     */
    public AgileCrashReporterNotInitializedException() {
        super();
    }

    /**
     * Constructs a AgileCrashReporterNotInitializedException with a message.
     *
     * @param message A String to be returned from getMessage.
     */
    public AgileCrashReporterNotInitializedException(String message) {
        super(message);
    }

    /**
     * Constructs a AgileCrashReporterNotInitializedException with a message and inner error.
     *
     * @param message   A String to be returned from getMessage.
     * @param throwable A Throwable to be returned from getCause.
     */
    public AgileCrashReporterNotInitializedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a AgileCrashReporterNotInitializedException with an inner error.
     *
     * @param throwable A Throwable to be returned from getCause.
     */
    public AgileCrashReporterNotInitializedException(Throwable throwable) {
        super(throwable);
    }
}