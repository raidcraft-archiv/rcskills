package de.raidcraft.skills.tables;

import com.avaje.ebean.validation.Length;
import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import lombok.Getter;
import lombok.Setter;

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
    public int id;

    @NotNull
    private int ownerId;

    @Length(max = 32)
    @NotEmpty
    private String item;

    @Length(max = 32)
    private String skill;

    @Length(max = 32)
    private String args;

}
