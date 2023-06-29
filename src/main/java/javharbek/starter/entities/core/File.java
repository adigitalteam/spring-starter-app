package javharbek.starter.entities.core;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "files")
public class File extends BaseEntity {

    @Column(name = "title")
    String title;

    @Column(name = "description")
    String description;

    @Column(name = "size")
    Long size;

    @Column(name = "file")
    String file;

    @Column(name = "extension")
    String extension;

    @Column(name = "status")
    int status;

    Boolean isDeleted = false;

    @Column(name = "host")
    String host;

    @Column(name = "type")
    String type;

    @Column(name = "type_id")
    String typeId;

    @Column(name = "\"typeFor\"")
    String typeFor;


}
