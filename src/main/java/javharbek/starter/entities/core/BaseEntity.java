package javharbek.starter.entities.core;

import javharbek.starter.listeners.AuditTrailListener;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners({AuditTrailListener.class})
abstract public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    public String id;

    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_datetime")
    private LocalDateTime updatedDatetime;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_datetime")
    private LocalDateTime deletedDatetime;

    @Column(name = "deleted_by")
    private String deletedBy;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;


}
