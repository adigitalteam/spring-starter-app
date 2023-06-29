package javharbek.starter.repositories.core;

import javharbek.starter.entities.core.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, String> {
    Optional<File> getFileByTypeAndTypeIdAndIsDeleted(String type, String typeId, Boolean isDeleted);
    Optional<File> getFileByTypeAndTypeIdAndIsDeletedAndExtension(String type, String typeId, Boolean isDeleted,String ext);
    Optional<File> getFileByTypeAndTypeIdAndTypeForAndIsDeletedAndExtensionOrderById(String type, String typeId, String typeFor, Boolean isDeleted, String ext);
    List<File> findByTypeAndTypeIdAndTypeForAndExtensionOrderByIdDesc(String type, String typeId, String typeFor, String extension);
    List<File> findByTypeAndTypeIdAndTypeForOrderByIdDesc(String type, String typeId, String typeFor);
}
