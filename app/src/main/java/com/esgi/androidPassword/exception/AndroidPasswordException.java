package com.esgi.androidPassword.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * @author christopher
 *
 * AndroidPasswordException for managing Exception
 *
 */
public class AndroidPasswordException extends Exception {

    private static final long serialVersionUID = 1L;
    private int errorCode;
    private List<String> errorMessages = new ArrayList<>();
    private String errorMessage;


    public AndroidPasswordException() { }

    public AndroidPasswordException(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public AndroidPasswordException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public AndroidPasswordException(Throwable cause) {
        super(cause);
    }

    public AndroidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    public AndroidPasswordException(int errorCode, String errorMessage, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public AndroidPasswordException(int errorCode, List<String> errorMessages) {
        this.errorCode = errorCode;
        this.errorMessages = errorMessages;
    }

    /**
     * Get errorCode.
     *
     * @return code
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Set errorCode.
     *
     * @param errorCode
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * Get the list of messages
     *
     * @return the list of messages
     */
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    /**
     * Set the list of messages
     *
     * @param errorMessages
     */
    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    /**
     * Get errorMessage
     *
     * @return errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set errorMessage
     *
     * @param errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
