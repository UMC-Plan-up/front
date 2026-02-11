package com.example.planup.network.data

enum class UserStatus {
    // 일반 로그인 성공 또는 기존 카카오 유저의 로그인 성공
    LOGIN_SUCCESS,
    // 일반 회원가입 완료 또는 카카오 임시 유저의 추가 정보 입력 완료
    SIGNUP_SUCCESS,
    // 카카오 인증은 마쳤으나 추가 정보 입력이 필요한 신규 유저
    SIGNUP_REQUIRED,
    // 카카오 로그인 시도 시, 동일한 이메일로 가입된 일반 계정이 이미 존재할 때
    ACCOUNT_CONFLICT
}