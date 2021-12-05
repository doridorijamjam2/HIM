package com.example.him;

public class Constants {

    /* SharedPreferences 관련 상수 */
    public static class SharedPreferencesName {
        public static final String USER_DOCUMENT_ID = "user_document_id";   // 사용자 Fire store Document ID
    }

    /* Activity 요청 코드 */
    public static class RequestCode {
        public static final int RESERVATION = 1;            // 예약
    }

    /* Fire store Collection 이름 */
    public static class FirestoreCollectionName {
        public static final String USER = "users";          // 사용자
        public static final String RESERVATION = "reservations";    // 예약
    }

    /* 부위 종류 */
    public static class PartKind {
        public static final int CHEST = 1;                  // 가슴
        public static final int LOWER_BODY = 2;             // 하체
        public static final int BACK = 3;                   // 등
        public static final int SHOULDER = 4;               // 어깨
    }

    /* 로딩 딜레이 */
    public static class LoadingDelay {
        public static final int SHORT = 300;
        public static final int LONG = 1000;
    }
}
