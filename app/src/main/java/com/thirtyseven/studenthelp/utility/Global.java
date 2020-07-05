package com.thirtyseven.studenthelp.utility;

public interface Global {
    // Used to define codes, specification, constant, etc.

    enum ResultCode {
        Succeeded, Failed
    }

    enum LoginError {
        NetworkError, LoginError, NotExist, WrongPassword
    }

    enum RegisterError {
        NetworkError, RegisterError, UserExist
    }

    enum PublishError {
        NetworkError, CreateFailed, UploadFileFalied, MoneyInsufficient, PublishError
    }

    enum ApplyError {
        NetworkError,ApplyError,HaveApply
    }
    enum ChooseError{
        NetworkError,ChooseError
    }
    enum DeleteError{
        NetworkError,NotCreator,DeleteFailed
    }
    enum SearchComposite{
        NetworkError,SearchFailed
    }
    enum QueryRecordsError{
        NetworkError,QueryFailed
    }
    enum PushError{
        NetworkError,PushError
    }
    enum CheckError{
        NetworkError,CheckError
    }
    enum RefuseError{
        NetworkError,RefuseError
    }
    enum CheckState{
        CheckRefused,CheckSucceed,InputRightState
    }
}
