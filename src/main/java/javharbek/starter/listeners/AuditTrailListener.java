package javharbek.starter.listeners;

import javharbek.starter.entities.core.BaseEntity;
import javharbek.starter.helpers.SystemHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

public class AuditTrailListener {
    private static Log log = LogFactory.getLog(AuditTrailListener.class);

    @PrePersist
    private void PrePersist(BaseEntity baseEntity) {
        baseEntity.setCreatedDatetime(LocalDateTime.now());
        baseEntity.setCreatedBy(SystemHelper.getCurrentUserNameIgnore());
    }

    @PreUpdate
    private void PreUpdate(BaseEntity baseEntity) {
        baseEntity.setUpdatedDatetime(LocalDateTime.now());
        baseEntity.setUpdatedBy(SystemHelper.getCurrentUserNameIgnore());
    }

}