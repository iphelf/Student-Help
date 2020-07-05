package com.thirtyseven.studenthelp.utility;

public interface Global {
    // Used to define codes, specification, constant, etc.
    enum ResultCode {
        Succeeded, Failed
    }

    enum LoginError {
        NetworkError, LoginError, NotExist, WrongPassword
    }

    enum RegisterError{
        RegisterError,NetworkError,UserExist
    }
    enum PublishError{
        CreateFailed,UploadFileFalied,MoneyInsufficient,PublishError
    }
}