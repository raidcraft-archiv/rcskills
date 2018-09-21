package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.NotEmpty;
import io.ebean.annotation.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Container for a binding.
 */
@Entity
@Table(name = "skills_bindings")
@Getter
@Setter
public class TBinding {

    @Id
    private int id;

    @NotNull
    private int ownerId;

    @Column(nullable = false, length = 32)
    @NotEmpty
    private String item;

    @Column(length = 32)
    private String skill;

    @Column(length = 32)
    private String args;

}
