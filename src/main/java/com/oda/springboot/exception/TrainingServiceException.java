package com.oda.springboot.exception;

/**
 * 训练任务业务异常
 */
public class TrainingServiceException extends ServiceException {

    public TrainingServiceException(String msg) {
        super("TRAINING_ERROR", msg);
    }

    public TrainingServiceException(String code, String msg) {
        super(code, msg);
    }
}
