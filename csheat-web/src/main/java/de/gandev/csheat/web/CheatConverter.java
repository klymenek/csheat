/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gandev.csheat.web;

import de.gandev.csheat.entity.Cheat;
import de.gandev.csheat.web.entitycontroller.CheatJpaController;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author ares
 */
@ApplicationScoped
@Named
public class CheatConverter implements Converter {

    @Inject
    private CheatJpaController jpa;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Cheat cheat = jpa.findCheat(Long.valueOf(value));

        return cheat;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Cheat c = ((Cheat) value);

        return String.valueOf(c.getId());
    }
}
