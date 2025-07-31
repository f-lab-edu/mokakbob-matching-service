## MemberRepository

* 이메일 중복 확인 DDL
  ~~~
  CREATE UNIQUE INDEX idx_member_email ON member (email);
  ~~~
  
* 닉네임 중복 확인 DDL
  ~~~
  CREATE UNIQUE INDEX idx_member_nickname ON member (nickname);
  ~~~
