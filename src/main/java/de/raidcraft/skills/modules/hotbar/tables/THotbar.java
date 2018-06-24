package de.raidcraft.skills.modules.hotbar.tables;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "skills_hotbars")
@Getter
@Setter
public class THotbar {

    @Id
    private int id;

    @NotNull
    private int ownerId;

    @Column(nullable = false, length = 32)
    @NotEmpty
    private String name;

    @NotNull
    private int index = 0;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "hotbar_id")
    private List<THotbarSlot> hotbarSlots = new ArrayList<>();
}
