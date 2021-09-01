etc
===
### spring mvc
```
// redirect 후 한번만 쓸 일회용 데이터를 전송
attributes.addFlashAttribute("message", "프로필을 수정하였습니다.");
```
-----------------

`@OneToMany` <-> `@ManyToOne`만 건다면 이것은 단방향 매핑이다.
```java
class Team {
    /**
     * 수정하려는 Team이 Member의 member 이름으로 매핑 되어있다.
     * 즉, Team.members를 바꿔도 연관관계에 아무런 영향을 주지 않는다.
     * readOnly 이다.
     */
    @OneToMany(mappedBy = "member")
    private List<Member> members;
}

```


### JPA
### 1:N:1 관계
기초적인 기능은 N쪽의 domain만 만들고 1쪽에서 N쪽까지 persist한다.
N의 service나 repository는 필요가 없다. 

### ObjectMapper
Object를 json으로 매핑하기위한 클래스로, fasterxml의 ObjectMapper는 기본적으로 spring bean으로 등록돼어있다. 
즉, `@RequestBody`를 의존성 추가 없이 사용 할 수 있었던 이유이다.

### Lombok
`@Builder`를 사용시 정의하지 않은 값은 필드 기본값을 무시하고 null을 채워넣는다. 