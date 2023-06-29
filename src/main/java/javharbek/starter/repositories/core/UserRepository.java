package javharbek.starter.repositories.core;

import javharbek.starter.entities.core.User;
import javharbek.starter.enums.SSOTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    Optional<User> findBySsoIdAndSsoTypeAndIsDeletedFalse(String ssoId, SSOTypeEnum type);
    Optional<User> findByUserNameAndIsDeletedFalse(String username);
}
