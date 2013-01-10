package de.gandev.csheat.web;

import de.gandev.csheat.web.entitycontroller.CheatJpaController;
import de.gandev.csheat.entity.Cheat;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.TransferEvent;

import org.primefaces.model.DualListModel;

@SessionScoped
@Named
public class CheatsPickListBean implements Serializable {

    private DualListModel<Cheat> cheats;
    @Inject
    private CheatJpaController jpa;
    private int selectedVersion;

    @PostConstruct
    public void initCheatsPickListBean() {
        List<Cheat> source = new ArrayList<Cheat>();
        List<Cheat> target = new ArrayList<Cheat>();

        List<Cheat> allCheats = jpa.findCheatEntities();
        for (Cheat cheat : allCheats) {
            source.add(cheat);
        }

        cheats = new DualListModel<Cheat>(source, target);
    }

    public DualListModel<Cheat> getCheats() {
        return cheats;
    }

    public void setCheats(DualListModel<Cheat> cheats) {
        this.cheats = cheats;
    }

    public CheatJpaController getJpa() {
        return jpa;
    }

    public void setJpa(CheatJpaController jpa) {
        this.jpa = jpa;
    }

    public void onTransfer(TransferEvent event) {
//        StringBuilder builder = new StringBuilder();
//        for (Object item : event.getItems()) {
//            builder.append(((Cheat) item).getVersions().get(0).getPurpose()).append("<br />");
//        }
//
//        FacesMessage msg = new FacesMessage();
//        msg.setSeverity(FacesMessage.SEVERITY_INFO);
//        msg.setSummary("Items Transferred");
//        msg.setDetail(builder.toString());
//
//        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
