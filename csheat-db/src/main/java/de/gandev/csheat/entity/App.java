package de.gandev.csheat.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class App implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String version;
    @ManyToMany(mappedBy = "apps")
    private List<OS> os;
    private Boolean cmdline;
    private Boolean gui;
    @OneToMany(mappedBy = "app")
    private List<Cheat> cheats;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<OS> getOs() {
        return os;
    }

    public void setOs(List<OS> os) {
        this.os = os;
    }

    public Boolean getCmdline() {
        return cmdline;
    }

    public void setCmdline(Boolean cmdline) {
        this.cmdline = cmdline;
    }

    public Boolean getGui() {
        return gui;
    }

    public void setGui(Boolean gui) {
        this.gui = gui;
    }

    public List<Cheat> getCheats() {
        return cheats;
    }

    public void setCheats(List<Cheat> cheats) {
        this.cheats = cheats;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final App other = (App) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "App{" + "id=" + id + ", name=" + name + ", version=" + version + ", os=" + os + ", cmdline=" + cmdline + ", gui=" + gui + ", cheats=" + cheats + '}';
    }
}
