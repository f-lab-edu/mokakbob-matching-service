## Member 최초 스키마 DDL
~~~
CREATE TABLE member (
id BIGINT AUTO_INCREMENT PRIMARY KEY,

    email VARCHAR(255) NOT NULL UNIQUE,
    
    password_enc VARCHAR(255),  -- 소셜 로그인 사용자는 NULL 가능
    
    nickname VARCHAR(255) NOT NULL UNIQUE,
    
    profile_image VARCHAR(512), -- 프로필 이미지는 선택 사항
    
    preference VARCHAR(50) NOT NULL, -- EnumType.STRING으로 저장됨
    
    score INT NOT NULL DEFAULT 50,  -- 기본값 50
    
    deposit_point INT NOT NULL DEFAULT 2000, -- 기본값 2000
    
    created_at DATETIME NOT NULL,  -- BaseEntity 상속 필드
    updated_at DATETIME NOT NULL   -- BaseEntity 상속 필드
);
~~~

## MemberRepository

* 이메일 중복 확인 DDL
  ~~~
  CREATE UNIQUE INDEX idx_member_email ON member (email);
  ~~~
  * 이메일 인덱스는 이미 생성되어있음.
  

  
* 닉네임 중복 확인 DDL
  ~~~
  CREATE UNIQUE INDEX idx_member_nickname ON member (nickname);
  ~~~
  * 닉네임 인덱스는 이미 생성되어있음.

---
