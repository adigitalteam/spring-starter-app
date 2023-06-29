package javharbek.starter.entities.core;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import javharbek.starter.enums.SSOTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class),
        @TypeDef(name = "json", typeClass = JsonStringType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
public class User extends BaseEntity{

    @Column(name = "user_name")
    private String userName;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "status")
    private String status;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private List<String> authorities;

    @Column(name = "passport")
    private String passport;

    @Column(name = "personal_identification_number")
    public String personalIdentificationNumber;

    @Column(name = "sso_id")
    private String ssoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sso_type")
    private SSOTypeEnum ssoType = SSOTypeEnum.SSO_XALQ_BANK;
}