package com.planup.planup.main.user.domain

open class UserException(message: String): IllegalStateException(message)

class UserNameAlreadyExistException() : UserException("이미 사용 중인 닉네임 입니다")
