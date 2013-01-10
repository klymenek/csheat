package de.gandev.csheat.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class CheatSheet implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @ManyToMany
    private List<CheatVersion> cheats;
    @ManyToOne
    private CheatSheetFormat format;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CheatVersion> getCheats() {
        return cheats;
    }

    public void setCheats(List<CheatVersion> cheats) {
        this.cheats = cheats;
    }

    public CheatSheetFormat getFormat() {
        return format;
    }

    public void setFormat(CheatSheetFormat format) {
        this.format = format;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CheatSheet other = (CheatSheet) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CheatSheet{" + "id=" + id + ", name=" + name + ", cheats=" + cheats + ", format=" + format + '}';
    }
}
