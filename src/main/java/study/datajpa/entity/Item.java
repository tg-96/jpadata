package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {
//   id생성전략이 @GeneratedValue이면 save()가 정상작동하지만, 직접할당이라면
//    이미 식별자 값이 있는 상태로 save()를 호출한다. 따라서 merge가 호출되고
//     merge는 Db를 호출해서 값을 확인하고,값이 없다면 새로운 엔티티로 인지하기 때문에
//    매우 비효율 적이다. 따라서 persistable를 사용해서 새로운 엔티티 확인 여부를 직접
//    구현하는게 효과적이다.
//    @createdDate를 조합해서 사용하면 이필드로 새로운 엔티티 여부를 편리하게 확인할 수 있다.
//    (@createdDate에 값이 null이면 새로운 엔티티로 판단)
    @Id
    private String id;

    //persist를 하기전에 실행됨.
    @CreatedDate
    private LocalDateTime createdDate;
    public Item(String id){
        this.id = id;
    }
    @Override
    public String getId(){
        return id;
    }
    //createDate가 null이면 true
    @Override
    public boolean isNew(){
        return createdDate == null;
    }
}
