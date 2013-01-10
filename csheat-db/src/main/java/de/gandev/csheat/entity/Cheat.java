package de.gandev.csheat.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Cheat implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private App app;
    @ManyToOne
    private OS os;
    private Boolean gui;
    @OneToOne
    private Cheat duplicateOf;
    @ManyToMany
    private List<CheatVersion> versions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public OS getOs() {
        return os;
    }

    public void setOs(OS os) {
        this.os = os;
    }

    public Boolean getGui() {
        return gui;
    }

    public void setGui(Boolean gui) {
        this.gui = gui;
    }

    public Cheat getDuplicateOf() {
        return duplicateOf;
    }

    public void setDuplicateOf(Cheat duplicateOf) {
        this.duplicateOf = duplicateOf;
    }

    public List<CheatVersion> getVersions() {
        return versions;
    }

    public void setVersions(List<CheatVersion> versions) {
        this.versions = versions;
    }

    public CheatVersion getVersion() {
        return versions.get(0);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final Cheat other = (Cheat) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }
//    @Override
//    public String toString() {
//        return "Cheat{" + "id=" + id + ", app=" + app + ", os=" + os + ", gui=" + gui + ", duplicateOf=" + duplicateOf + ", versions=" + versions + '}';
//    }
}
