/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.divudi.bean.clinical;

import com.divudi.bean.common.AreaController;
import com.divudi.bean.pharmacy.*;
import com.divudi.bean.common.InstitutionController;
import com.divudi.bean.common.ItemController;
import com.divudi.bean.common.SessionController;
import com.divudi.bean.common.UtilityController;
import com.divudi.bean.inward.InwardBeanController;
import com.divudi.data.AreaType;
import com.divudi.data.BillType;
import com.divudi.data.DepartmentType;
import com.divudi.data.InstitutionType;
import com.divudi.data.PaymentMethod;
import com.divudi.data.Sex;
import com.divudi.data.SymanticType;
import com.divudi.data.dataStructure.PharmacyImportCol;
import com.divudi.data.inward.InwardChargeType;
import com.divudi.ejb.PharmacyBean;
import com.divudi.entity.Area;
import com.divudi.entity.Bill;
import com.divudi.entity.BillFee;
import com.divudi.entity.BillItem;
import com.divudi.entity.BilledBill;
import com.divudi.entity.CancelledBill;
import com.divudi.entity.Category;
import com.divudi.entity.Department;
import com.divudi.entity.Institution;
import com.divudi.entity.IssueRateMargins;
import com.divudi.entity.Item;
import com.divudi.entity.Patient;
import com.divudi.entity.PatientEncounter;
import com.divudi.entity.Person;
import com.divudi.entity.Service;
import com.divudi.entity.hr.StaffShift;
import com.divudi.entity.inward.InwardService;
import com.divudi.entity.inward.TimedItem;
import com.divudi.entity.lab.Investigation;
import com.divudi.entity.pharmacy.Amp;
import com.divudi.entity.pharmacy.Ampp;
import com.divudi.entity.pharmacy.Atm;
import com.divudi.entity.pharmacy.ItemBatch;
import com.divudi.entity.pharmacy.ItemsDistributors;
import com.divudi.entity.pharmacy.MeasurementUnit;
import com.divudi.entity.pharmacy.PharmaceuticalBillItem;
import com.divudi.entity.pharmacy.PharmaceuticalItem;
import com.divudi.entity.pharmacy.PharmaceuticalItemCategory;
import com.divudi.entity.pharmacy.PharmaceuticalItemType;
import com.divudi.entity.pharmacy.StockHistory;
import com.divudi.entity.pharmacy.StoreItemCategory;
import com.divudi.entity.pharmacy.Vmp;
import com.divudi.entity.pharmacy.Vmpp;
import com.divudi.entity.pharmacy.Vtm;
import com.divudi.entity.pharmacy.VtmsVmps;
import com.divudi.facade.AmpFacade;
import com.divudi.facade.AmppFacade;
import com.divudi.facade.AtmFacade;
import com.divudi.facade.BillFacade;
import com.divudi.facade.BillFeeFacade;
import com.divudi.facade.BillItemFacade;
import com.divudi.facade.ItemFacade;
import com.divudi.facade.ItemsDistributorsFacade;
import com.divudi.facade.MeasurementUnitFacade;
import com.divudi.facade.PatientEncounterFacade;
import com.divudi.facade.PharmaceuticalBillItemFacade;
import com.divudi.facade.PharmaceuticalItemCategoryFacade;
import com.divudi.facade.PharmaceuticalItemFacade;
import com.divudi.facade.StaffShiftFacade;
import com.divudi.facade.StockFacade;
import com.divudi.facade.StockHistoryFacade;
import com.divudi.facade.StoreItemCategoryFacade;
import com.divudi.facade.VmpFacade;
import com.divudi.facade.VmppFacade;
import com.divudi.facade.VtmFacade;
import com.divudi.facade.VtmsVmpsFacade;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TemporalType;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Buddhika
 */
@Named
@SessionScoped
public class ClinicalExcelManager implements Serializable {

    /**
     *
     * EJBs
     *
     */
    @EJB
    AtmFacade atmFacade;
    @EJB
    VtmFacade vtmFacade;
    @EJB
    AmpFacade ampFacade;
    @EJB
    VmpFacade vmpFacade;
    @EJB
    AmppFacade amppFacade;
    @EJB
    VmppFacade vmppFacade;
    @EJB
    VtmsVmpsFacade vtmInAmpFacade;
    @EJB
    MeasurementUnitFacade muFacade;
    @EJB
    PharmaceuticalItemCategoryFacade pharmaceuticalItemCategoryFacade;
    @EJB
    private PharmacyBean pharmacyBean;
    @EJB
    StoreItemCategoryFacade storeItemCategoryFacade;
    @EJB
    PatientEncounterFacade patientEncounterFacade;

    List<PharmacyImportCol> itemNotPresent;
    List<String> itemsWithDifferentGenericName;
    List<String> itemsWithDifferentCode;

    @Inject
    AreaController areaController;

    /**
     *
     * Values of Excel Columns
     *
     */
//        Category      0
//        Item Name     1
//        Code          2
//        Trade Name    3
//        Generic Name  4
//        Generic Product  5            
//        Strength      6
//        Strength Unit 7
//        Pack Size     8
//        Issue Unit    9
//        Pack Unit     10
    //    Distributor 11
//        Manufacturer  12
//        Importer      13
//          14. Date of Expiary
//                15. Batch
//                16. Quentity
//                17. Purchase Price
//                18. Sale Price
    /**
     * Values of Excel Columns
     */
    int number = 0;
    int catCol = 1;
    int ampCol = 2;
    int codeCol = 3;
    int barcodeCol = 4;
    int vtmCol = 5;
    int strengthOfIssueUnitCol = 6;
    int strengthUnitCol = 7;
    int issueUnitsPerPackCol = 8;
    int issueUnitCol = 9;
    int packUnitCol = 10;
    int distributorCol = 11;
    int manufacturerCol = 12;
    int importerCol = 13;
    int doeCol = 14;
    int batchCol = 15;
    int stockQtyCol = 16;
    int pruchaseRateCol = 17;
    int saleRateCol = 18;

    int startRow = 1;
    /**
     * DataModals
     *
     */
    List<Vtm> vtms;
    List<Amp> amps;
    List<Ampp> ampps;
    /**
     *
     * Uploading File
     *
     */
    private UploadedFile file;

    /**
     * Creates a new instance of DemographyExcelManager
     */
    public ClinicalExcelManager() {
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getDistributorCol() {
        return distributorCol;
    }

    public void setDistributorCol(int distributorCol) {
        this.distributorCol = distributorCol;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    @Inject
    private InstitutionController institutionController;

    @Inject
    SessionController sessionController;

    @Inject
    PharmacyPurchaseController pharmacyPurchaseController;

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public void removeDuplicateAmpps() {
        List<Ampp> temAmpps = getAmppFacade().findAll(true);
        for (Ampp ampp : temAmpps) {
            for (Ampp dup : temAmpps) {
                if (ampp.getName().equals(dup.getName())) {
                    if (ampp.isRetired() == false && dup.isRetired() == false) {
                        dup.setRetired(true);
                        getAmppFacade().edit(dup);

                    }
                }
            }
        }
    }

    @EJB
    private BillFacade billFacade;

    @EJB
    private ItemFacade itemFacade;

    @Inject
    InwardBeanController inwardBeanController;

    @EJB
    BillFeeFacade billFeeFacade;

    @Inject
    ItemController itemController;

    public ItemController getItemController() {
        return itemController;
    }

    public void setItemController(ItemController itemController) {
        this.itemController = itemController;
    }

    @EJB
    StockFacade stockFacade;

    public StockFacade getStockFacade() {
        return stockFacade;
    }

    public void setStockFacade(StockFacade stockFacade) {
        this.stockFacade = stockFacade;
    }

    @EJB
    private BillItemFacade billItemFacade;

    @EJB
    private PharmaceuticalBillItemFacade pharmaceuticalBillItemFacade;

    @EJB
    private StockHistoryFacade stockHistoryFacade;

    public String importToExcelWithStock() {
        ////System.out.println("importing to excel");
        String strCat;
        String strAmp;
        String strCode;
        String strBarcode;
        String strGenericName;
        String strStrength;
        String strStrengthUnit;
        String strPackSize;
        String strIssueUnit;
        String strPackUnit;
        String strDistributor;
        String strManufacturer;
        String strImporter;

        PharmaceuticalItemCategory cat;
        PharmaceuticalItemType phType;
        Vtm vtm;
        Atm atm;
        Vmp vmp;
        Amp amp;
        Ampp ampp;
        Vmpp vmpp;
        VtmsVmps vtmsvmps;
        MeasurementUnit issueUnit;
        MeasurementUnit strengthUnit;
        MeasurementUnit packUnit;
        double strengthUnitsPerIssueUnit;
        double issueUnitsPerPack;
        Institution distributor;
        Institution manufacturer;
        Institution importer;

        double stockQty;
        double pp;
        double sp;
        String batch;
        Date doe;

        File inputWorkbook;
        Workbook w;
        Cell cell;
        InputStream in;
        UtilityController.addSuccessMessage(file.getFileName());
        try {
            UtilityController.addSuccessMessage(file.getFileName());
            in = file.getInputstream();
            File f;
            f = new File(Calendar.getInstance().getTimeInMillis() + file.getFileName());
            FileOutputStream out = new FileOutputStream(f);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            inputWorkbook = new File(f.getAbsolutePath());

            UtilityController.addSuccessMessage("Excel File Opened");
            w = Workbook.getWorkbook(inputWorkbook);
            Sheet sheet = w.getSheet(0);

            getPharmacyPurchaseController().makeNull();

            for (int i = startRow; i < sheet.getRows(); i++) {

                Map m = new HashMap();

                //Category
                cell = sheet.getCell(catCol, i);
                strCat = cell.getContents();
                ////System.out.println("strCat is " + strCat);
                cat = getPharmacyBean().getPharmaceuticalCategoryByName(strCat);
                if (cat == null) {
                    continue;
                }
                ////System.out.println("cat = " + cat.getName());

                phType = getPharmacyBean().getPharmaceuticalItemTypeByName(strCat);

                //Strength Unit
                cell = sheet.getCell(strengthUnitCol, i);
                strStrengthUnit = cell.getContents();
                ////System.out.println("strStrengthUnit is " + strengthUnitCol);
                strengthUnit = getPharmacyBean().getUnitByName(strStrengthUnit);
                if (strengthUnit == null) {
                    continue;
                }
                ////System.out.println("strengthUnit = " + strengthUnit.getName());
                //Pack Unit
                cell = sheet.getCell(packUnitCol, i);
                strPackUnit = cell.getContents();
                ////System.out.println("strPackUnit = " + strPackUnit);
                packUnit = getPharmacyBean().getUnitByName(strPackUnit);
                if (packUnit == null) {
                    continue;
                }
                ////System.out.println("packUnit = " + packUnit.getName());
                //Issue Unit
                cell = sheet.getCell(issueUnitCol, i);
                strIssueUnit = cell.getContents();
                ////System.out.println("strIssueUnit is " + strIssueUnit);
                issueUnit = getPharmacyBean().getUnitByName(strIssueUnit);
                if (issueUnit == null) {
                    continue;
                }
                //StrengthOfAnMeasurementUnit
                cell = sheet.getCell(strengthOfIssueUnitCol, i);
                strStrength = cell.getContents();
                ////System.out.println("strStrength = " + strStrength);
                if (!strStrength.equals("")) {
                    try {
                        strengthUnitsPerIssueUnit = Double.parseDouble(strStrength);
                    } catch (NumberFormatException e) {
                        strengthUnitsPerIssueUnit = 0.0;
                    }
                } else {
                    strengthUnitsPerIssueUnit = 0.0;
                }

                //Issue Units Per Pack
                cell = sheet.getCell(issueUnitsPerPackCol, i);
                strPackSize = cell.getContents();
                ////System.out.println("strPackSize = " + strPackSize);
                if (!strPackSize.equals("")) {
                    try {
                        issueUnitsPerPack = Double.parseDouble(strPackSize);
                    } catch (NumberFormatException e) {
                        issueUnitsPerPack = 0.0;
                    }
                } else {
                    issueUnitsPerPack = 0.0;
                }

                //Vtm
                cell = sheet.getCell(vtmCol, i);
                strGenericName = cell.getContents();
                ////System.out.println("strGenericName = " + strGenericName);
                if (!strGenericName.equals("")) {
                    vtm = getPharmacyBean().getVtmByName(strGenericName);
                } else {
                    ////System.out.println("vtm is null");
                    vtm = null;
                }

                //Vmp
                vmp = getPharmacyBean().getVmp(vtm, strengthUnitsPerIssueUnit, strengthUnit, cat);
                if (vmp == null) {
                    ////System.out.println("vmp is null");
                    continue;
                } else {
                    vmp.setCategory(phType);
                    getVmpFacade().edit(vmp);
                }
                ////System.out.println("vmp = " + vmp.getName());

                //Code
                cell = sheet.getCell(codeCol, i);
                strCode = cell.getContents();
                ////System.out.println("strCode = " + strCode);

                //Code
                cell = sheet.getCell(barcodeCol, i);
                strBarcode = cell.getContents();
                ////System.out.println("strBarCode = " + strBarcode);

                //Distributor
                cell = sheet.getCell(distributorCol, i);

                //Amp
                cell = sheet.getCell(ampCol, i);
                strAmp = cell.getContents();
                ////System.out.println("strAmp = " + strAmp);
                m = new HashMap();
                m.put("v", vmp);
                m.put("n", strAmp.toUpperCase());
                if (!strCat.equals("")) {
                    amp = ampFacade.findFirstBySQL("SELECT c FROM Amp c Where c.retired=false and upper(c.name)=:n "
                            + " AND c.vmp=:v", m);
                    //System.out.println("m = " + m);
                    if (amp == null) {
                        amp = new Amp();
                        amp.setName(strAmp);
                        amp.setCode(strCode);
                        amp.setBarcode(strBarcode);
                        amp.setMeasurementUnit(strengthUnit);
                        amp.setDblValue((double) strengthUnitsPerIssueUnit);
                        amp.setCategory(cat);
                        amp.setVmp(vmp);
                        getAmpFacade().create(amp);
                    } else {
                        amp.setRetired(false);
                        getAmpFacade().edit(amp);
                    }
                } else {
                    amp = null;
                    ////System.out.println("amp is null");
                }
                if (amp == null) {
                    continue;
                }
                ////System.out.println("amp = " + amp.getName());
                //Ampp
                ampp = getPharmacyBean().getAmpp(amp, issueUnitsPerPack, packUnit);

                //Code
                cell = sheet.getCell(codeCol, i);
                strCode = cell.getContents();
                ////System.out.println("strCode = " + strCode);
                amp.setCode(strCode);
                //System.out.println("Code = " + amp.getCode());
                getAmpFacade().edit(amp);
                //Code
                cell = sheet.getCell(barcodeCol, i);
                strBarcode = cell.getContents();
                ////System.out.println("strBarCode = " + strBarcode);
                amp.setCode(strBarcode);
                getAmpFacade().edit(amp);
                //Distributor
                cell = sheet.getCell(distributorCol, i);
                strDistributor = cell.getContents();
                distributor = getInstitutionController().getInstitutionByName(strDistributor, InstitutionType.Dealer);
                if (distributor != null) {
                    ////System.out.println("distributor = " + distributor.getName());
                    ItemsDistributors id = new ItemsDistributors();
                    id.setInstitution(distributor);
                    id.setItem(amp);
                    id.setOrderNo(0);
                    getItemsDistributorsFacade().create(id);
                } else {
                    ////System.out.println("distributor is null");
                }
                //Manufacture
                cell = sheet.getCell(manufacturerCol, i);
                strManufacturer = cell.getContents();
                manufacturer = getInstitutionController().getInstitutionByName(strManufacturer, InstitutionType.Manufacturer);
                amp.setManufacturer(manufacturer);
                //Importer
                cell = sheet.getCell(importerCol, i);
                strImporter = cell.getContents();
                importer = getInstitutionController().getInstitutionByName(strImporter, InstitutionType.Importer);
                amp.setManufacturer(importer);
                //
                String temStr;

                cell = sheet.getCell(stockQtyCol, i);
                temStr = cell.getContents();
                try {
                    stockQty = Double.valueOf(temStr);
                } catch (Exception e) {
                    stockQty = 0;
                }

                cell = sheet.getCell(pruchaseRateCol, i);
                temStr = cell.getContents();
                try {
                    pp = Double.valueOf(temStr);
                } catch (Exception e) {
                    pp = 0;
                }

                cell = sheet.getCell(saleRateCol, i);
                temStr = cell.getContents();
                try {
                    sp = Double.valueOf(temStr);
                } catch (Exception e) {
                    sp = 0;
                }

                cell = sheet.getCell(batchCol, i);
                batch = cell.getContents();

                cell = sheet.getCell(doeCol, i);
                temStr = cell.getContents();
                try {
                    doe = new SimpleDateFormat("M/d/yyyy", Locale.ENGLISH).parse(temStr);
                } catch (Exception e) {
                    doe = new Date();
                }

                getPharmacyPurchaseController().getCurrentBillItem().setItem(amp);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().setItem(amp) = " + getPharmacyPurchaseController().getCurrentBillItem().getItem());
                getPharmacyPurchaseController().getCurrentBillItem().setTmpQty(stockQty);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().setTmpQty(stockQty) = " + getPharmacyPurchaseController().getCurrentBillItem().getTmpQty());
                getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setPurchaseRate(pp);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setPurchaseRate(pp); = " + getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().getPurchaseRate());
                getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setRetailRate(sp);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setRetailRate(sp); = " + getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().getRetailRate());
                getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setDoe(doe);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setDoe(doe) = " + getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().getDoe());
                if (batch == null || batch.trim().equals("")) {
                    getPharmacyPurchaseController().setBatch();
                } else {
                    getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setStringValue(batch);
                }
                getPharmacyPurchaseController().addItem();
            }
            UtilityController.addSuccessMessage("Succesful. All the data in Excel File Impoted to the database");
            return "/pharmacy/pharmacy_purchase";
        } catch (IOException | BiffException ex) {
            UtilityController.addErrorMessage(ex.getMessage());
            return "";
        }
    }

    List<String> itemNamesFailedToImport;

    public List<String> getItemNamesFailedToImport() {
        return itemNamesFailedToImport;
    }

    public void setItemNamesFailedToImport(List<String> itemNamesFailedToImport) {
        this.itemNamesFailedToImport = itemNamesFailedToImport;
    }

    public String importFromExcelByName() {
        ////System.out.println("importing to excel");
        String strAmp;
        Amp amp;
        itemNamesFailedToImport = new ArrayList<>();
        double stockQty;
        double pp;
        double sp;
        String batch;
        Date doe;
        String temStr;

        File inputWorkbook;
        Workbook w;
        Cell cell;
        InputStream in;
        UtilityController.addSuccessMessage(file.getFileName());
        try {
            UtilityController.addSuccessMessage(file.getFileName());
            in = file.getInputstream();
            File f;
            f = new File(Calendar.getInstance().getTimeInMillis() + file.getFileName());
            FileOutputStream out = new FileOutputStream(f);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            inputWorkbook = new File(f.getAbsolutePath());

            UtilityController.addSuccessMessage("Excel File Opened");
            w = Workbook.getWorkbook(inputWorkbook);
            Sheet sheet = w.getSheet(0);

            getPharmacyPurchaseController().makeNull();

            int doeCol = 1;
            int batchCol = 2;
            int stockQtyCol = 3;
            int pruchaseRateCol = 4;
            int saleRateCol = 5;

            for (int i = startRow; i < sheet.getRows(); i++) {

                Map m = new HashMap();

                cell = sheet.getCell(0, i);
                strAmp = cell.getContents();
                ////System.out.println("strAmp = " + strAmp);
                m = new HashMap();
                m.put("n", strAmp.toUpperCase());
                amp = ampFacade.findFirstBySQL("SELECT c FROM Amp c Where c.retired=false and upper(c.name)=:n ", m);
                //System.out.println("m is " + m);

                if (amp == null) {
                    itemNamesFailedToImport.add(strAmp);
                    continue;
                }

                cell = sheet.getCell(stockQtyCol, i);
                temStr = cell.getContents();
                try {
                    stockQty = Double.valueOf(temStr);
                } catch (Exception e) {
                    stockQty = 0;
                }

                cell = sheet.getCell(pruchaseRateCol, i);
                temStr = cell.getContents();
                try {
                    pp = Double.valueOf(temStr);
                } catch (Exception e) {
                    pp = 0;
                }

                cell = sheet.getCell(saleRateCol, i);
                temStr = cell.getContents();
                try {
                    sp = Double.valueOf(temStr);
                } catch (Exception e) {
                    sp = 0;
                }

                cell = sheet.getCell(batchCol, i);
                batch = cell.getContents();

                cell = sheet.getCell(doeCol, i);
                temStr = cell.getContents();
                try {
                    doe = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(temStr);
                } catch (Exception e) {
                    doe = new Date();
                }

                getPharmacyPurchaseController().getCurrentBillItem().setItem(amp);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().setItem(amp) = " + getPharmacyPurchaseController().getCurrentBillItem().getItem());
                getPharmacyPurchaseController().getCurrentBillItem().setTmpQty(stockQty);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().setTmpQty(stockQty) = " + getPharmacyPurchaseController().getCurrentBillItem().getTmpQty());
                getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setPurchaseRate(pp);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setPurchaseRate(pp); = " + getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().getPurchaseRate());
                getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setRetailRate(sp);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setRetailRate(sp); = " + getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().getRetailRate());
                getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setDoe(doe);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setDoe(doe) = " + getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().getDoe());
                if (batch == null || batch.trim().equals("")) {
                    getPharmacyPurchaseController().setBatch();
                } else {
                    getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setStringValue(batch);
                }
                getPharmacyPurchaseController().addItem();
            }
            UtilityController.addSuccessMessage("Succesful. All the data in Excel File Impoted to the database");
            return "/pharmacy/pharmacy_purchase";
        } catch (IOException | BiffException ex) {
            UtilityController.addErrorMessage(ex.getMessage());
            return "";
        }
    }

    public String importFromExcelByBarcode() {
        ////System.out.println("importing to excel");
        String strAmp;
        Amp amp;

        double stockQty;
        double pp;
        double sp;
        String batch;
        Date doe;
        String temStr;

        File inputWorkbook;
        Workbook w;
        Cell cell;
        InputStream in;
        UtilityController.addSuccessMessage(file.getFileName());
        try {
            UtilityController.addSuccessMessage(file.getFileName());
            in = file.getInputstream();
            File f;
            f = new File(Calendar.getInstance().getTimeInMillis() + file.getFileName());
            FileOutputStream out = new FileOutputStream(f);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            inputWorkbook = new File(f.getAbsolutePath());

            UtilityController.addSuccessMessage("Excel File Opened");
            w = Workbook.getWorkbook(inputWorkbook);
            Sheet sheet = w.getSheet(0);

            getPharmacyPurchaseController().makeNull();

            int doeCol = 1;
            int batchCol = 2;
            int stockQtyCol = 3;
            int pruchaseRateCol = 4;
            int saleRateCol = 5;

            for (int i = startRow; i < sheet.getRows(); i++) {

                Map m = new HashMap();

                cell = sheet.getCell(0, i);
                strAmp = cell.getContents();
                ////System.out.println("strAmp = " + strAmp);
                m = new HashMap();
                m.put("n", strAmp.toUpperCase());
                amp = ampFacade.findFirstBySQL("SELECT c FROM Amp c Where c.retired=false and upper(c.code)=:n ", m);
                //System.out.println("m = " + m);
                //System.out.println("amp");
                if (amp == null) {
                    continue;
                }

                cell = sheet.getCell(stockQtyCol, i);
                temStr = cell.getContents();
                try {
                    stockQty = Double.valueOf(temStr);
                } catch (Exception e) {
                    stockQty = 0;
                }

                cell = sheet.getCell(pruchaseRateCol, i);
                temStr = cell.getContents();
                try {
                    pp = Double.valueOf(temStr);
                } catch (Exception e) {
                    pp = 0;
                }

                cell = sheet.getCell(saleRateCol, i);
                temStr = cell.getContents();
                try {
                    sp = Double.valueOf(temStr);
                } catch (Exception e) {
                    sp = 0;
                }

                cell = sheet.getCell(batchCol, i);
                batch = cell.getContents();

                cell = sheet.getCell(doeCol, i);
                temStr = cell.getContents();
                try {
                    doe = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(temStr);
                } catch (Exception e) {
                    doe = new Date();
                }

                getPharmacyPurchaseController().getCurrentBillItem().setItem(amp);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().setItem(amp) = " + getPharmacyPurchaseController().getCurrentBillItem().getItem());
                getPharmacyPurchaseController().getCurrentBillItem().setTmpQty(stockQty);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().setTmpQty(stockQty) = " + getPharmacyPurchaseController().getCurrentBillItem().getTmpQty());
                getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setPurchaseRate(pp);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setPurchaseRate(pp); = " + getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().getPurchaseRate());
                getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setRetailRate(sp);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setRetailRate(sp); = " + getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().getRetailRate());
                getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setDoe(doe);
                //System.out.println("getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setDoe(doe) = " + getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().getDoe());
                if (batch == null || batch.trim().equals("")) {
                    getPharmacyPurchaseController().setBatch();
                } else {
                    getPharmacyPurchaseController().getCurrentBillItem().getPharmaceuticalBillItem().setStringValue(batch);
                }
                getPharmacyPurchaseController().addItem();
            }
            UtilityController.addSuccessMessage("Succesful. All the data in Excel File Impoted to the database");
            return "/pharmacy/pharmacy_purchase";
        } catch (IOException | BiffException ex) {
            UtilityController.addErrorMessage(ex.getMessage());
            return "";
        }
    }

    public String importToExcel() {
        /**
         * <h:outputLabel value ="0. Index No" ></h:outputLabel>
         * <h:outputLabel value ="1. Clinic No" ></h:outputLabel>
         * <h:outputLabel value ="2. Full Name " ></h:outputLabel>
         * <h:outputLabel value ="3. Sex" ></h:outputLabel>
         * <h:outputLabel value ="4. Address" ></h:outputLabel>
         * <h:outputLabel value ="5. Telephone" ></h:outputLabel>
         * <h:outputLabel value ="6. District" ></h:outputLabel>
         * <h:outputLabel value ="7. Divisional secratory Area"
         * ></h:outputLabel>
         * <h:outputLabel value ="8. Grama Niladhari Division" ></h:outputLabel>
         * <h:outputLabel value ="9. NIC" ></h:outputLabel>
         * <h:outputLabel value ="10. Date of Birth" ></h:outputLabel>
         * <h:outputLabel value ="12. Age" ></h:outputLabel>
         * <h:outputLabel value ="12. Occupation" ></h:outputLabel>
         * <h:outputLabel value ="13. Educational Level" ></h:outputLabel>
         * <h:outputLabel value ="14. Treatment Taken Since" ></h:outputLabel>
         * <h:outputLabel value ="15. Funds Receiving" ></h:outputLabel>
         * <h:outputLabel value ="16. Caregiver" ></h:outputLabel>
         * <h:outputLabel value ="17. Diagnosis" ></h:outputLabel>
         * <h:outputLabel value ="18. Treatment" ></h:outputLabel>
         */
        String strIndexNo;
        String strClinicNo;
        String strFullName;
        String strSex;
        String strAddress;
        String strTelephone;
        String strDistrict;
        String strDsArea;
        String strGnArea;
        String strNic;
        String strDob;
        String strOcc;
        String strEl;
        String strSince;
        String strFunds;
        String strCaregiver;
        String strDx;
        String strRx;

        File inputWorkbook;
        Workbook w;
        Cell cell;
        InputStream in;
        Date dob;
        
        UtilityController.addSuccessMessage(file.getFileName());
        try {
            UtilityController.addSuccessMessage(file.getFileName());
            in = file.getInputstream();
            File f;
            f = new File(Calendar.getInstance().getTimeInMillis() + file.getFileName());
            FileOutputStream out = new FileOutputStream(f);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            inputWorkbook = new File(f.getAbsolutePath());

            UtilityController.addSuccessMessage("Excel File Opened");
            w = Workbook.getWorkbook(inputWorkbook);
            Sheet sheet = w.getSheet(0);

            for (int i = startRow; i < sheet.getRows(); i++) {
                Person p = new Person();
                Patient pt = new Patient();
                pt.setPerson(p);

                Map m = new HashMap();

                cell = sheet.getCell(0, i);
                strIndexNo = cell.getContents();
                Long lngIndexNo = 0L;
                try {
                    lngIndexNo = Long.getLong(strIndexNo);
                    pt.setIndexNo(lngIndexNo);
                } catch (Exception e) {
                    System.out.println("e = " + e);
                }

                cell = sheet.getCell(1, i);
                strClinicNo = cell.getContents();
                pt.setCode(strClinicNo);

                cell = sheet.getCell(2, i);
                strFullName = cell.getContents();
                p.setName(strFullName);
                p.setName(strFullName);
                p.setNameWithInitials(strFullName);

                cell = sheet.getCell(3, i);
                strSex = cell.getContents();
                if (strSex.trim().equalsIgnoreCase("MALE")) {
                    p.setSex(Sex.Male);
                } else {
                    p.setSex(Sex.Female);
                }

                cell = sheet.getCell(4, i);
                strAddress = cell.getContents();
                p.setAddress(strAddress);

                cell = sheet.getCell(5, i);
                strTelephone = cell.getContents();
                p.setPhone(strTelephone);

                cell = sheet.getCell(6, i);
                strDistrict = cell.getContents();
                Area d = areaController.findArea(strDistrict, AreaType.District);
                pt.setDistrict(d);

                cell = sheet.getCell(7, i);
                strDsArea = cell.getContents();
                Area ds = areaController.findArea(strDsArea, AreaType.Divisional_Secretariat);
                pt.setDistrict(ds);

                cell = sheet.getCell(8, i);
                strGnArea = cell.getContents();
                Area gna = areaController.findArea(strGnArea, AreaType.Grama_Niladhari_Divisions);
                pt.setDistrict(gna);

                cell = sheet.getCell(9, i);
                strNic = cell.getContents();
                p.setPhone(strNic);

                cell = sheet.getCell(10, i);
                strDob = cell.getContents();
                try {
                    dob = new SimpleDateFormat("yyyy.M.d", Locale.ENGLISH).parse(strDob);
                } catch (Exception e) {
                    dob = new Date();
                }
                p.setDob(dob);
                
                cell = sheet.getCell(12, i);
                strOcc = cell.getContents();
                Item occ = itemController.findItem(strOcc, SymanticType.Occupation_or_Discipline);
                pt.setOccupation(occ);
                
                cell = sheet.getCell(13, i);
                strEl = cell.getContents();
                Item edu = itemController.findItem(strOcc, SymanticType.Educational_Activity);
                pt.setEducationLevel(edu);
                
                
                
                
                
            }

            UtilityController.addSuccessMessage("Succesful. All the data in Excel File Impoted to the database");
            return "";
        } catch (IOException ex) {
            UtilityController.addErrorMessage(ex.getMessage());
            return "";
        } catch (BiffException e) {
            UtilityController.addErrorMessage(e.getMessage());
            return "";
        }
    }

    @EJB
    ItemsDistributorsFacade itemsDistributorsFacade;

    @EJB
    PharmaceuticalItemFacade pharmaceuticalItemFacade;

    public PharmaceuticalItemFacade getPharmaceuticalItemFacade() {
        return pharmaceuticalItemFacade;
    }

    public void setPharmaceuticalItemFacade(PharmaceuticalItemFacade pharmaceuticalItemFacade) {
        this.pharmaceuticalItemFacade = pharmaceuticalItemFacade;
    }

    public void removeAllPharmaceuticalItems() {
        String sql;
        sql = "select p from PharmaceuticalItem p";
        List<PharmaceuticalItem> pis = getPharmaceuticalItemFacade().findBySQL(sql);
        for (PharmaceuticalItem p : pis) {
            getPharmaceuticalItemFacade().remove(p);
        }
    }

    public ItemsDistributorsFacade getItemsDistributorsFacade() {
        return itemsDistributorsFacade;
    }

    public void setItemsDistributorsFacade(ItemsDistributorsFacade itemsDistributorsFacade) {
        this.itemsDistributorsFacade = itemsDistributorsFacade;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getAmpCol() {
        return ampCol;
    }

    public void setAmpCol(int ampCol) {
        this.ampCol = ampCol;
    }

    public AmpFacade getAmpFacade() {
        return ampFacade;
    }

    public void setAmpFacade(AmpFacade ampFacade) {
        this.ampFacade = ampFacade;
    }

    public AmppFacade getAmppFacade() {
        return amppFacade;
    }

    public void setAmppFacade(AmppFacade amppFacade) {
        this.amppFacade = amppFacade;
    }

    public AtmFacade getAtmFacade() {
        return atmFacade;
    }

    public void setAtmFacade(AtmFacade atmFacade) {
        this.atmFacade = atmFacade;
    }

    public int getMeasurementUnitCol() {
        return issueUnitCol;
    }

    public void setMeasurementUnitCol(int issueUnitCol) {
        this.issueUnitCol = issueUnitCol;
    }

    public int getMeasurementUnitsPerPackCol() {
        return issueUnitsPerPackCol;
    }

    public void setMeasurementUnitsPerPackCol(int issueUnitsPerPackCol) {
        this.issueUnitsPerPackCol = issueUnitsPerPackCol;
    }

    public int getCatCol() {
        return catCol;
    }

    public void setCatCol(int catCol) {
        this.catCol = catCol;
    }

    public VtmsVmpsFacade getVtmInAmpFacade() {
        return vtmInAmpFacade;
    }

    public void setVtmInAmpFacade(VtmsVmpsFacade vtmInAmpFacade) {
        this.vtmInAmpFacade = vtmInAmpFacade;
    }

    public MeasurementUnitFacade getMuFacade() {
        return muFacade;
    }

    public void setMuFacade(MeasurementUnitFacade muFacade) {
        this.muFacade = muFacade;
    }

    public int getIssueUnitCol() {
        return issueUnitCol;
    }

    public void setIssueUnitCol(int issueUnitCol) {
        this.issueUnitCol = issueUnitCol;
    }

    public int getStrengthUnitCol() {
        return strengthUnitCol;
    }

    public void setStrengthUnitCol(int strengthUnitCol) {
        this.strengthUnitCol = strengthUnitCol;
    }

    public int getIssueUnitsPerPackCol() {
        return issueUnitsPerPackCol;
    }

    public void setIssueUnitsPerPackCol(int issueUnitsPerPackCol) {
        this.issueUnitsPerPackCol = issueUnitsPerPackCol;
    }

    public int getPackUnitCol() {
        return packUnitCol;
    }

    public void setPackUnitCol(int packUnitCol) {
        this.packUnitCol = packUnitCol;
    }

    public PharmaceuticalItemCategoryFacade getPharmaceuticalItemCategoryFacade() {
        return pharmaceuticalItemCategoryFacade;
    }

    public void setPharmaceuticalItemCategoryFacade(PharmaceuticalItemCategoryFacade pharmaceuticalItemCategoryFacade) {
        this.pharmaceuticalItemCategoryFacade = pharmaceuticalItemCategoryFacade;
    }

    public int getStrengthOfIssueUnitCol() {
        return strengthOfIssueUnitCol;
    }

    public void setStrengthOfIssueUnitCol(int strengthOfIssueUnitCol) {
        this.strengthOfIssueUnitCol = strengthOfIssueUnitCol;
    }

    public int getMeasurmentUnitCol() {
        return strengthUnitCol;
    }

    public void setMeasurmentUnitCol(int strengthUnitCol) {
        this.strengthUnitCol = strengthUnitCol;
    }

    public VmpFacade getVmpFacade() {
        return vmpFacade;
    }

    public void setVmpFacade(VmpFacade vmpFacade) {
        this.vmpFacade = vmpFacade;
    }

    public VmppFacade getVmppFacade() {
        return vmppFacade;
    }

    public void setVmppFacade(VmppFacade vmppFacade) {
        this.vmppFacade = vmppFacade;
    }

    public int getVtmCol() {
        return vtmCol;
    }

    public void setVtmCol(int vtmCol) {
        this.vtmCol = vtmCol;
    }

    public VtmFacade getVtmFacade() {
        return vtmFacade;
    }

    public void setVtmFacade(VtmFacade vtmFacade) {
        this.vtmFacade = vtmFacade;
    }

    public VtmsVmpsFacade getVtmsVmpsFacade() {
        return vtmInAmpFacade;
    }

    public void setVtmsVmpsFacade(VtmsVmpsFacade vtmInAmpFacade) {
        this.vtmInAmpFacade = vtmInAmpFacade;
    }

    public List<Ampp> getAmpps() {
        return getAmppFacade().findAll();
    }

    public void setAmpps(List<Ampp> ampps) {
        this.ampps = ampps;
    }

    public List<Amp> getAmps() {
        return getAmpFacade().findAll();
    }

    public void setAmps(List<Amp> amps) {
        this.amps = amps;
    }

    public List<Vtm> getVtms() {
        return getVtmFacade().findAll();
    }

    public void setVtms(List<Vtm> vtms) {
        this.vtms = vtms;
    }

    public PharmacyBean getPharmacyBean() {
        return pharmacyBean;
    }

    public void setPharmacyBean(PharmacyBean pharmacyBean) {
        this.pharmacyBean = pharmacyBean;
    }

    public int getCodeCol() {
        return codeCol;
    }

    public void setCodeCol(int codeCol) {
        this.codeCol = codeCol;
    }

    public int getManufacturerCol() {
        return manufacturerCol;
    }

    public void setManufacturerCol(int manufacturerCol) {
        this.manufacturerCol = manufacturerCol;
    }

    public int getImporterCol() {
        return importerCol;
    }

    public void setImporterCol(int importerCol) {
        this.importerCol = importerCol;
    }

    public InstitutionController getInstitutionController() {
        return institutionController;
    }

    public void setInstitutionController(InstitutionController institutionController) {
        this.institutionController = institutionController;
    }

    public int getBarcodeCol() {
        return barcodeCol;
    }

    public void setBarcodeCol(int barcodeCol) {
        this.barcodeCol = barcodeCol;
    }

    public BillFacade getBillFacade() {
        return billFacade;
    }

    public void setBillFacade(BillFacade billFacade) {
        this.billFacade = billFacade;
    }

    public BillItemFacade getBillItemFacade() {
        return billItemFacade;
    }

    public void setBillItemFacade(BillItemFacade billItemFacade) {
        this.billItemFacade = billItemFacade;
    }

    public PharmaceuticalBillItemFacade getPharmaceuticalBillItemFacade() {
        return pharmaceuticalBillItemFacade;
    }

    public void setPharmaceuticalBillItemFacade(PharmaceuticalBillItemFacade pharmaceuticalBillItemFacade) {
        this.pharmaceuticalBillItemFacade = pharmaceuticalBillItemFacade;
    }

    public StockHistoryFacade getStockHistoryFacade() {
        return stockHistoryFacade;
    }

    public void setStockHistoryFacade(StockHistoryFacade stockHistoryFacade) {
        this.stockHistoryFacade = stockHistoryFacade;
    }

    public List<String> getItemsWithDifferentGenericName() {
        return itemsWithDifferentGenericName;
    }

    public void setItemsWithDifferentGenericName(List<String> itemsWithDifferentGenericName) {
        this.itemsWithDifferentGenericName = itemsWithDifferentGenericName;
    }

    public List<String> getItemsWithDifferentCode() {
        return itemsWithDifferentCode;
    }

    public void setItemsWithDifferentCode(List<String> itemsWithDifferentCode) {
        this.itemsWithDifferentCode = itemsWithDifferentCode;
    }

    public ItemFacade getItemFacade() {
        return itemFacade;
    }

    public void setItemFacade(ItemFacade itemFacade) {
        this.itemFacade = itemFacade;
    }

    public List<PharmacyImportCol> getItemNotPresent() {
        return itemNotPresent;
    }

    public void setItemNotPresent(List<PharmacyImportCol> itemNotPresent) {
        this.itemNotPresent = itemNotPresent;
    }

    public StoreItemCategoryFacade getStoreItemCategoryFacade() {
        return storeItemCategoryFacade;
    }

    public void setStoreItemCategoryFacade(StoreItemCategoryFacade storeItemCategoryFacade) {
        this.storeItemCategoryFacade = storeItemCategoryFacade;
    }

    public int getDoeCol() {
        return doeCol;
    }

    public void setDoeCol(int doeCol) {
        this.doeCol = doeCol;
    }

    public int getBatchCol() {
        return batchCol;
    }

    public void setBatchCol(int batchCol) {
        this.batchCol = batchCol;
    }

    public int getStockQtyCol() {
        return stockQtyCol;
    }

    public void setStockQtyCol(int stockQtyCol) {
        this.stockQtyCol = stockQtyCol;
    }

    public int getPruchaseRateCol() {
        return pruchaseRateCol;
    }

    public void setPruchaseRateCol(int pruchaseRateCol) {
        this.pruchaseRateCol = pruchaseRateCol;
    }

    public int getSaleRateCol() {
        return saleRateCol;
    }

    public void setSaleRateCol(int saleRateCol) {
        this.saleRateCol = saleRateCol;
    }

    public PharmacyPurchaseController getPharmacyPurchaseController() {
        return pharmacyPurchaseController;
    }

    public void setPharmacyPurchaseController(PharmacyPurchaseController pharmacyPurchaseController) {
        this.pharmacyPurchaseController = pharmacyPurchaseController;
    }

    public AreaController getAreaController() {
        return areaController;
    }

    public void setAreaController(AreaController areaController) {
        this.areaController = areaController;
    }

}
