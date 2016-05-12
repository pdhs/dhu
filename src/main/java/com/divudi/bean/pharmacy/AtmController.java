/*
 * MSc(Biomedical Informatics) Project
 *
 * Development and Implementation of a Web-based Combined Data Repository of
 Genealogical, Clinical, Laboratory and Genetic Data
 * and
 * a Set of Related Tools
 */
package com.divudi.bean.pharmacy;
import com.divudi.bean.common.BillBeanController;
import com.divudi.bean.common.SessionController;
import com.divudi.bean.common.UtilityController;
import com.divudi.entity.pharmacy.Atm;
import com.divudi.entity.pharmacy.Atm;
import com.divudi.facade.SpecialityFacade;
import com.divudi.facade.AtmFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext; import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 Informatics)
 */
@Named
@SessionScoped
public  class AtmController implements Serializable {

    private static final long serialVersionUID = 1L;
    @Inject
    SessionController sessionController;
    @EJB
    private AtmFacade ejbFacade;
    @EJB
    private SpecialityFacade specialityFacade;
    @Inject
    private BillBeanController billBean;
    List<Atm> selectedItems;
    private Atm current;
    private List<Atm> items = null;
    String selectText = "";
    String bulkText = "";
    boolean billedAs;
    boolean reportedAs;
    List<Atm> atmList;

    public List<Atm> completeAtm(String query) {
        
        String sql;
        if (query == null) {
            atmList = new ArrayList<Atm>();
        } else {
            sql = "select c from Atm c where c.retired=false and upper(c.name) like '%" + query.toUpperCase() + "%' order by c.name";
            ////System.out.println(sql);
            atmList = getFacade().findBySQL(sql);
        }
        return atmList;
    }

    public boolean isBilledAs() {
        return billedAs;
    }

    public void setBilledAs(boolean billedAs) {
        this.billedAs = billedAs;
    }

    public boolean isReportedAs() {
        return reportedAs;
    }

    public void setReportedAs(boolean reportedAs) {
        this.reportedAs = reportedAs;
    }

    public BillBeanController getBillBean() {
        return billBean;
    }

    public void setBillBean(BillBeanController billBean) {
        this.billBean = billBean;
    }

    public void correctIx() {
        List<Atm> allItems = getEjbFacade().findAll();
        for (Atm i : allItems) {
            i.setPrintName(i.getName());
            i.setFullName(i.getName());
            i.setShortName(i.getName());
            i.setDiscountAllowed(Boolean.TRUE);
            i.setUserChangable(false);
            i.setTotal(getBillBean().totalFeeforItem(i));
            getEjbFacade().edit(i);
        }

    }

    public void correctIx1() {
        List<Atm> allItems = getEjbFacade().findAll();
        for (Atm i : allItems) {
            i.setBilledAs(i);
            i.setReportedAs(i);
            getEjbFacade().edit(i);
        }

    }

    public String getBulkText() {

        return bulkText;
    }

    public void setBulkText(String bulkText) {
        this.bulkText = bulkText;
    }

    public List<Atm> getSelectedItems() {
         
        if (selectText.trim().equals("")) {
            selectedItems = getFacade().findBySQL("select c from Atm c where c.retired=false order by c.name");
        } else {
            String sql = "select c from Atm c where c.retired=false and upper(c.name) like '%" + getSelectText().toUpperCase() + "%' order by c.name";
            selectedItems = getFacade().findBySQL(sql);
            
        }
        return selectedItems;
    }

    public void prepareAdd() {
        current = new Atm();
    }

    public void bulkUpload() {
        List<String> lstLines = Arrays.asList(getBulkText().split("\\r?\\n"));
        for (String s : lstLines) {
            List<String> w = Arrays.asList(s.split(","));
            try {
                String code = w.get(0);
                String ix = w.get(1);
                String ic = w.get(2);
                String f = w.get(4);
                ////System.out.println(code + " " + ix + " " + ic + " " + f);


                Atm tix = new Atm();
                tix.setCode(code);
                tix.setName(ix);
                tix.setDepartment(null);



            } catch (Exception e) {
            }

        }
    }

    public void setSelectedItems(List<Atm> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public String getSelectText() {
        return selectText;
    }

    private void recreateModel() {
        items = null;
    }

    public void saveSelected() {
        if (getCurrent().getId() != null && getCurrent().getId() > 0) {
            if (billedAs == false) {
                ////System.out.println("2");
                getCurrent().setBilledAs(getCurrent());

            }
            if (reportedAs == false) {
                getCurrent().setReportedAs(getCurrent());
            }
            getFacade().edit(getCurrent());
            UtilityController.addSuccessMessage("Updated Successfully.");
        } else {
            getCurrent().setCreatedAt(new Date());
            getCurrent().setCreater(getSessionController().getLoggedUser());
            getFacade().create(getCurrent());
            if (billedAs == false) {
                getCurrent().setBilledAs(getCurrent());
            }
            if (reportedAs == false) {
                getCurrent().setReportedAs(getCurrent());
            }
            getFacade().edit(getCurrent());
            UtilityController.addSuccessMessage("Saved Successfully");
        }
        recreateModel();
        getItems();
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
    }

    public AtmFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(AtmFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public AtmController() {
    }

    public Atm getCurrent() {
        if (current == null) {
            current = new Atm();
        }
        return current;
    }

    public void setCurrent(Atm current) {
        this.current = current;
        if (current != null) {
            if (current.getBilledAs() == current) {
                billedAs = false;
            } else {
                billedAs = true;
            }
            if (current.getReportedAs() == current) {
                reportedAs = false;
            } else {
                reportedAs = true;
            }
        }
    }

    public void delete() {

        if (current != null) {
            current.setRetired(true);
            current.setRetiredAt(new Date());
            current.setRetirer(getSessionController().getLoggedUser());
            getFacade().edit(current);
            UtilityController.addSuccessMessage("Deleted Successfully");
        } else {
            UtilityController.addSuccessMessage("Nothing to Delete");
        }
        recreateModel();
        getItems();
        current = null;
        getCurrent();
    }

    private AtmFacade getFacade() {
        return ejbFacade;
    }

    public List<Atm> getItems() {
        String j;
        j="select v from Atm v "
                + "where v.retired=false "
                + "order by v.name";
        items = getFacade().findBySQL(j);
        return items;
    }

    public SpecialityFacade getSpecialityFacade() {
        return specialityFacade;
    }

    public void setSpecialityFacade(SpecialityFacade specialityFacade) {
        this.specialityFacade = specialityFacade;
    }

    /**
     *
     */
   
    @FacesConverter("atm")
    public static class AtmControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AtmController controller = (AtmController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "atmController");
            return controller.getEjbFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Atm) {
                Atm o = (Atm) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + AtmController.class.getName());
            }
        }
    }

}
