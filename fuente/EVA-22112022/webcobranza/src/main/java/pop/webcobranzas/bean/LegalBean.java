/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pop.webcobranzas.bean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import pop.comun.dominio.CobTchn;
import pop.comun.dominio.MaeCorreo;
import pop.comun.dominio.MaeDeposito;
import pop.comun.dominio.MaeEmail;
import pop.comun.dominio.MaeEstadoCuenta;
import pop.comun.dominio.MaeFondo;
import pop.comun.dominio.MaeInmueble;
import pop.comun.dominio.MaeInversion;
import pop.comun.dominio.MaePersona;
import pop.comun.dominio.MaeReporte;
import pop.comun.dominio.MaeUbigeo;
import pop.comun.dominio.reporte.RepSaldoDeudor;
import pop.webcobranzas.filter.SessionUtils;
import pop.webcobranzas.iface.IEmailServ;
import pop.webcobranzas.iface.IFondoServ;
import pop.webcobranzas.iface.IInversionServ;
import pop.webcobranzas.ifacecorreo.ICorreoServ;
import pop.webcobranzas.ifacerep.IRepEstadoCuentaServ;
import pop.webcobranzas.ifacerep.IRepSaldoDeudorServ;
import pop.webcobranzas.servicio.EmailServ;
import pop.webcobranzas.servicio.EstadoCuentaServ;
import pop.webcobranzas.servicio.FondoServ;
import pop.webcobranzas.servicio.InversionServ;
import pop.webcobranzas.serviciocorreo.CorreoServ;
import pop.webcobranzas.serviciorep.RepEstadoCuentaServ;
import pop.webcobranzas.serviciorep.RepSaldoDeudorServ;
import pop.webcobranzas.properties.ManageProperties;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.BorderStyle;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pop.comun.dominio.CobTablas;
import pop.comun.dominio.LegEtapa;
import pop.comun.dominio.LegDemanTchn;
import pop.comun.dominio.LegGastoJudicial;
import pop.comun.dominio.LegOtroProceso;
import pop.comun.dominio.LegSeguiTchn;
import pop.comun.dominio.LegSgtoOtroProceso;
import pop.comun.dominio.LegTabla;
import pop.comun.dominio.LegalAsesor;
import pop.comun.dominio.LegalTchn;
import pop.comun.dominio.MaeAnio;
import pop.comun.dominio.MaeAsesor;
import pop.comun.dominio.MaeEstadoLegal;
import pop.comun.dominio.MaeMes;
import pop.webcobranzas.iface.ITablasServ;
import pop.webcobranzas.servicio.LegalServ;
import pop.webcobranzas.servicio.TablasServ;

/**
 *
 * @author Jyoverar
 */
@ManagedBean(name = "legalBean", eager = true)
@ViewScoped

public class LegalBean implements Serializable {
    private static final long serialVersionUID = 1L;
    // ubigeo Busqueda
    private MaeUbigeo maeUbigeo = new MaeUbigeo();
    // ubigeo Provincia
    private MaeUbigeo maeUbigeoP = new MaeUbigeo();
    // ubigeo Distrito
    private MaeUbigeo maeUbigeoD = new MaeUbigeo();
    // imnueble (busq por provincia/distrito)
    private MaeInmueble maeInmueble = new MaeInmueble();
    //
    private MaePersona maePersona = new MaePersona();
    private MaeInversion maeInversion = new MaeInversion();
    // inversion para imprimir
    private MaeInversion maeInversionP = new MaeInversion();
    private MaeFondo maeFondo = new MaeFondo();
    private MaeEstadoCuenta maeEstadoCuenta = new MaeEstadoCuenta();
    
    private LegalTchn legTchn = new LegalTchn();
    private MaeEstadoLegal maeEstadoLegal =new MaeEstadoLegal();
    private List<MaeEstadoLegal> maeEtapaLegalList;
    private MaeAnio maeAnio = new MaeAnio();
    private MaeMes maeMes = new MaeMes();
    private List<MaeAnio> maeAnioList;
    private List<MaeMes> maeMesList;

    private List<MaeEstadoLegal> maeEstadoLegalList;
    private List<MaeEstadoLegal> maeJuzgadoLegalList;
    private List<MaeEstadoLegal> maeEspecialLegalList;

    private List<CobTablas> cobTablasEstInvList;    
    private CobTablas cobTablasEstInv = new CobTablas(); 
    private ITablasServ iCobTablasServ = new TablasServ();


    private  LegDemanTchn legDemanTchn= new LegDemanTchn();
    private  LegSeguiTchn legSeguiTchn=new LegSeguiTchn();

    
    

    
    private List<MaeEstadoCuenta> maeEstadoCuentaList;
    private List<MaeEstadoCuenta> maeEstadoCuentaBList;
    private List<MaeDeposito> maeEstadoCuentaCList;
    private List<LegalTchn> legalTchnList;
    private List<LegalTchn> legalTchnList2;
    private List<LegalTchn> legalTchnList3;
    private List<LegalTchn> legalTchnListEt3;
    private List<LegalTchn> legalTchnList4;
    private List<LegalTchn> legalTchnList5;
    private List<LegalTchn> legalTchnList6;  
    private List<LegalTchn> legalTchnList7;  
    private List<LegalTchn> legalTchnList8;  
    private List<LegalTchn> legalTchnList9;  
    private List<LegalTchn> legalTchnList10;  
    private List<LegalTchn> legalTchnList11;  
    private List<LegalTchn> legalTchnList12;  
    private List<LegalTchn> legalTchnList13;  
    private List<LegSeguiTchn> legSeguiTchnList;
    private List<LegSeguiTchn> legSeguiTchnList2;
    private List<LegDemanTchn> legDemanTchnList;
    private List<LegGastoJudicial> legGastoJudicialList;
    private List<LegGastoJudicial> legGastoJudicialList2;
    
    
    
    // lista de fondos
    private List<MaeFondo> maeFondoList;
    
    private List<LegalAsesor> legalAsesorList;    

    // servicios de estado de cuenta
    private EstadoCuentaServ estadoCuentaServ = new EstadoCuentaServ();
    
    private LegalServ legalServ = new LegalServ();
    // servicios de tipo de cambio reporete
    private IRepEstadoCuentaServ repEstadoCuentaServ = new RepEstadoCuentaServ();
    // servicios de correo
    private ICorreoServ correoServ = new CorreoServ();
    // servicios de correos
    private IEmailServ emailServ = new EmailServ();
    // servicios de tipo de cambio reporte
    private IRepSaldoDeudorServ repSaldoDeudorServ = new RepSaldoDeudorServ();
    // servicios de inversion
    private IInversionServ inversionServ = new InversionServ();
    // servicios de fondos
    private IFondoServ fondoServ = new FondoServ();

    private int iShowButtonsLiqJudic = 0;
    
    private boolean showMsg = false;
    private String mensaje = "";
    private String tipoMsj = "";
    private String data0;
    private String dataA;
    private String dataA1;
    private String dataB;
    private String dataC;
    private String dataD;

    
    private String data10;
    private String data1A;
    private String data1B;
    private String data1C;
    private String data1D;
    
    private String data20;
    private String data2A;
    private String data2B;
    private String data2C;
    private String data2D;

    private String data30;
    private String data3A;
    private String data3B;
    private String data3C;
    private String data3D;

    
    private String data40;
    private String data4A;
    private String data4B;
    private String data4C;
    private String data4D;
    private String data4E;

    private String data50;
    private String data5A;
    private String data5B;
    private String data5C;
    private String data5D;

    
    private int iJudi;
    private int iCali;
    private int iTot;
    
    private int iPC;
    private int iCC;
    private int iTOTC;

    // exportar
    private int calFCE,calMYP,calPOP,calFPH;
    private int posFCE,posMYP,posPOP,posFPH;    
    private int proFCE,proMYP,proPOP,proFPH;    
    private int desiFCE,desiMYP,desiPOP,desiFPH;    
    private int impFCE,impMYP,impPOP,impFPH;  
    private int ejeFCE,ejeMYP,ejePOP,ejeFPH;  
    private int finFCE,finMYP,finPOP,finFPH;  

    private int calOpt,calNor,calReg,calDef;
    private int posOpt,posNor,posReg,posDef;    
    private int proOpt,proNor,proReg,proDef;    
    private int desiOpt,desiNor,desiReg,desiDef;    
    private int impOpt,impNor,impReg,impDef;  
    private int ejeOpt,ejeNor,ejeReg,ejeDef;  
    private int finOpt,finNor,finReg,finDef; 
    
    private int FCEOpt,FCENor,FCEReg,FCEDef;
    private int POPOpt,POPNor,POPReg,POPDef;
    private int MYPOpt,MYPNor,MYPReg,MYPDef;
    private int PHOpt,PHNor,PHReg,PHDef;

    private int FCEOptx,FCENorx,FCERegx,FCEDefx;
    private int POPOptx,POPNorx,POPRegx,POPDefx;
    private int MYPOptx,MYPNorx,MYPRegx,MYPDefx;
    private int PHOptx,PHNorx,PHRegx,PHDefx;
    private double DFCEOptx,DFCENorx,DFCERegx,DFCEDefx;
    private double DPOPOptx,DPOPNorx,DPOPRegx,DPOPDefx;
    private double DMYPOptx,DMYPNorx,DMYPRegx,DMYPDefx;
    private double DPHOptx,DPHNorx,DPHRegx,DPHDefx;
    
    private String lsAsesor1,lsAse1;
    private String lsAsesor2,lsAse2;
    private String lsAsesor3,lsAse3;
    private String lsAsesor4,lsAse4;
    
    
    private int calAse1,calAse2,calAse3,calAse4;
    private int posAse1,posAse2,posAse3,posAse4;    
    private int proAse1,proAse2,proAse3,proAse4;    
    private int desiAse1,desiAse2,desiAse3,desiAse4;    
    private int impAse1,impAse2,impAse3,impAse4;  
    private int ejeAse1,ejeAse2,ejeAse3,ejeAse4;  
    private int finAse1,finAse2,finAse3,finAse4; 
    
    private int optAse1,norAse1,regAse1,defAse1;    
    private int optAse2,norAse2,regAse2,defAse2;
    private int optAse3,norAse3,regAse3,defAse3;
    private int optAse4,norAse4,regAse4,defAse4;
    
    private String Ase1,Ase2,Ase3,Ase4,Ase5,Ase6,Ase7,Ase8,Ase9,Ase10,Ase11;
    
    private String Ase1Cap,Ase2Cap,Ase3Cap,Ase4Cap,Ase5Cap,Ase6Cap,Ase7Cap,Ase8Cap,Ase9Cap,Ase10Cap,Ase11Cap;
    private String Ase1Pop,Ase2Pop,Ase3Pop,Ase4Pop,Ase5Pop,Ase6Pop,Ase7Pop,Ase8Pop,Ase9Pop,Ase10Pop,Ase11Pop;
    private String Ase1Myp,Ase2Myp,Ase3Myp,Ase4Myp,Ase5Myp,Ase6Myp,Ase7Myp,Ase8Myp,Ase9Myp,Ase10Myp,Ase11Myp;
    private String Ase1Prh,Ase2Prh,Ase3Prh,Ase4Prh,Ase5Prh,Ase6Prh,Ase7Prh,Ase8Prh,Ase9Prh,Ase10Prh,Ase11Prh;
    
    private String data70;
    private String data7A;
    private String data7B;
    private String data7C;
    private String data7D;

    private String mensajeRpta;
    
    private ManageProperties properties; 
    private boolean mostrarListaGastosJudiciales;
    private boolean mostrarAddGastoJudicial;
    private String etapa;
    private List<LegEtapa> legEtapaList;
    private List<LegTabla> legTiposGastosJudiList;
    private String tipoGastoJudicial;
    private String comentario;
    private String comprobanteNumero;
    private String montoGastoJudicial;
    private Date fechaOperacion;

   
    private String codigoTCHN;
    private String fondo;
    private String nroExpediente;
    private String nroDocumento;
    private boolean mostrarModificaGastoJudicial;
    private String idLegGastoJudicial;
    
    private List<LegTabla> legMateriaOtroProcesoList;
    private List<LegTabla> legTipoOtroProcesoList;
    private List<LegOtroProceso> legalOtroProcesoList;
    private boolean mostrarAddOtroProceso;
    private String demandado;
    private String demandante;
    private String materia;
    private String tipoOtroProceso;
    private String organoCompetente;
    private String especialista;
    private String descripcionOtroProceso;
    private String usuario;
    private boolean mostrarModiOtroProceso;
    private int idOtroProceso;
    private boolean mostrarAddSgtoOtroProceso;
    private boolean mostrarVerSgtosOtroProceso;
    private List<LegSgtoOtroProceso> legalSgtoOtroProcesoList;
    private boolean mostrarModiSgtoOtroProceso;
    private int idSegOtroProceso;
    
    public LegalBean() {
        properties = new ManageProperties();
        properties.getProperties();
        maeInversion.setMaeFondo(maeFondo);
        maeInversion.setcPersonaId(maePersona);
        maeInversion.setMaeInmueble(maeInmueble);
        legTchn.setFondo(maeFondo);
        legTchn.setMaeInversion(maeInversion);
        legTchn.setLnCapital(0.00);
        legTchn.setLnInteres(0.00);
        legTchn.setLnMora(0.00);
        legSeguiTchn.setFmostrarEjeOpc1(true);
        legSeguiTchn.setFmostrarEjeOpc2(false);
        legSeguiTchn.setLnCostas(0.00);
        legSeguiTchn.setLnCostos(0.00);
        legSeguiTchn.setLnMontoval(0.00);
        legSeguiTchn.setLnMontodolval(0.00);
        legSeguiTchn.setLnIntMora(0.00);
        
        mensajeRpta="";
        mostrarListaGastosJudiciales=false;
        mostrarAddGastoJudicial=false;
    }

  
   
    public void listarFondos() {
        try {

            if (maeFondoList == null) {
                maeFondoList = getFondoServ().listarFondos(new MaeFondo());
            }
            //System.out.println(" cantidad de Fondos -> " + maeFondoList.size());
        } catch (Exception e) {

        }
    }
    
    public void listarEstadoTchn() {
        try {

            if (maeEtapaLegalList == null) {
                 maeEtapaLegalList = legalServ.listarEstadoTchnLegalMODY();
            }
            //System.out.println(" cantidad de Fondos -> " + maeFondoList.size());
        } catch (Exception e) {

        }
    }
    
    public void buscarTchnLegal() {
        try {
            maeUbigeo.setCUbigeoId(maeUbigeoD.getCUbigeoId());
            maeUbigeo.setCUbigeoPad(maeUbigeoP.getCUbigeoId());
            legSeguiTchn.setFmostrar(false);    
            legSeguiTchn.setFmostrarEje(false);  
            
            legTchn.setFmostrar(false);
            legTchn.setFmostrarEt2(false);
            legTchn.setFmostrarEt3(false);
            
            legTchn.setFmostrarView1(false);
            legTchn.setFmostrarView2(false);
            legTchn.setFmostrarView3(false);
            
            maeInmueble.setMaeUbigeo(maeUbigeo);
            legalTchnList = legalServ.listarTchnLegal(legTchn);
        } catch (Exception e) {

        }
    }
    
    public void buscarTchnLegalForGastos() {
        try {
            mensajeRpta="";
            this.setMostrarAddGastoJudicial(false);
            this.setMostrarListaGastosJudiciales(false);
            this.setMostrarModificaGastoJudicial(false);
            
            int numeroNulos=0;
            if(this.getMaeInversion().getCInversion()==null || this.getMaeInversion().getCInversion().trim().equals(""))
            {
                System.out.println("getCInversion, null");
                ++numeroNulos;
            }
            if(this.getMaeFondo().getCFondoId()==null || this.getMaeFondo().getCFondoId().trim().equals(""))
            {
                System.out.println("getCFondoId, null");
                ++numeroNulos;
            }
            if(this.getMaeInversion().getcPersonaId().getANroDocumento()==null || this.getMaeInversion().getcPersonaId().getANroDocumento().trim().equals(""))
            {
                System.out.println("getANroDocumento, null");
                ++numeroNulos;
            }
            
            //evaluando cuando hacer la consulta
            if(numeroNulos>2)
            {
                mensajeRpta="Debe seleccionar al menos un filtro";
                legalTchnList.clear();
            }
            else if(!(maeFondo.getCFondoId()==null || maeFondo.getCFondoId().trim().equals("")))
            {
                if(numeroNulos>=2)
                {
                    mensajeRpta="Debe seleccionar al menos un filtro mas";
                    legalTchnList.clear();
                }
                else
                {
                    legalTchnList = legalServ.listarTchnLegalForGastos(legTchn);
                }
            }
            else
            {
                legalTchnList = legalServ.listarTchnLegalForGastos(legTchn);
            }
        } catch (Exception e) {

        }
    }    
    
     public void buscarTchnLegalMody() {
        try {
            
            //System.out.println("pop.webcobranzas.bean.EstadoCuentaBean.buscarTchn()");
            maeUbigeo.setCUbigeoId(maeUbigeoD.getCUbigeoId());
            maeUbigeo.setCUbigeoPad(maeUbigeoP.getCUbigeoId());
            legSeguiTchn.setFmostrar(false);    
            legSeguiTchn.setFmostrarEje(false);  
            
            legTchn.setFmostrar(false);
            legTchn.setFmostrarEt2(false);
            legTchn.setFmostrarEt3(false);
            
            legTchn.setFmostrarView1(false);
            legTchn.setFmostrarView2(false);
            legTchn.setFmostrarView3(false);
            
            //System.out.println("Ubigeo -> Distrito " + maeUbigeoD.getCUbigeoId());
            //System.out.println("Ubigeo -> Provincia " + maeUbigeoP.getCUbigeoId());
            maeInmueble.setMaeUbigeo(maeUbigeo);
            legalTchnList = legalServ.listarTchnLegal(legTchn);
            System.out.println("Ubigeo -> Provincia " + legalTchnList.size() );
            
            
        } catch (Exception e) {

        }
    }
    
    public void buscarTchnLegalRepo() {
        try {
            System.out.println("buscarTchnLegalRepo");
            mensajeRpta="";
            maeUbigeo.setCUbigeoId(maeUbigeoD.getCUbigeoId());
            maeUbigeo.setCUbigeoPad(maeUbigeoP.getCUbigeoId());
            legSeguiTchn.setFmostrar(false);    
            legSeguiTchn.setFmostrarEje(false);
            legTchn.setFmostrarView3(false);
            legTchn.setFmostrar(false);
            legTchn.setFmostrarEt2(false);
            legTchn.setFmostrarEt3(false);
            maeInmueble.setMaeUbigeo(maeUbigeo);
            
            int numeroNulos=0;
            if(maeFondo.getCFondoId()==null || maeFondo.getCFondoId().trim().equals(""))
            {
                System.out.println("getCFondoId, null");
                ++numeroNulos;
            }
            if(maeInversion.getCInversion()==null || maeInversion.getCInversion().trim().equals(""))
            {
                System.out.println("getCInversion, null");
                ++numeroNulos;
            }
            if(maeInversion.getcPersonaId().getANroDocumento()==null || maeInversion.getcPersonaId().getANroDocumento().trim().equals(""))
            {
                System.out.println("getANroDocumento, null");
                ++numeroNulos;
            }
            if(maeInversion.getApellidosnombres()==null || maeInversion.getApellidosnombres().trim().equals(""))
            {
                System.out.println("getApellidosnombres, null");
                ++numeroNulos;
            }
            if(maeInversion.getNumeroexpediente()==null || maeInversion.getNumeroexpediente().trim().equals(""))
            {
                System.out.println("getNumeroexpediente, null");
                ++numeroNulos;
            }
            if(maeInversion.getAsesorId()==null || maeInversion.getAsesorId().trim().equals(""))
            {
                System.out.println("getAsesorId, null");
                ++numeroNulos;
            }
            //evaluando cuando hacer la consulta
            if(maeFondo.getCFondoId()==null || maeFondo.getCFondoId().trim().equals(""))
            {
                mensajeRpta="Debe seleccionar al menos un fondo y un filtro adicional";
                legalTchnList.clear();
            }
            else if(numeroNulos>4)
            {
                mensajeRpta="Debe seleccionar al menos un fondo y un filtro adicional";
                legalTchnList.clear();
            }
            else
            {
                legalTchnList = legalServ.listarTchnLegalRepo(legTchn);
            }
            legSeguiTchnList.clear();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    
    
    public void listarActivosJudiciales() {
        try {
            int numeroNulos=0;
                System.out.println(legTchn.getFIniBusq());
                System.out.println(legTchn.getFFinBusq());
                legalTchnList = legalServ.listarActivoJudicial(legTchn);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    
    
    
    public void ListarEtapaTchnLegal(MaeFondo fondo,MaeInversion maeinversion, String idEtapa  ) {
        try {
            
            //System.out.println("pop.webcobranzas.bean.EstadoCuentaBean.buscarTchn()");
            maeUbigeo.setCUbigeoId(maeUbigeoD.getCUbigeoId());
            maeUbigeo.setCUbigeoPad(maeUbigeoP.getCUbigeoId());
            
            //System.out.println("Ubigeo -> Distrito " + maeUbigeoD.getCUbigeoId());
            //System.out.println("Ubigeo -> Provincia " + maeUbigeoP.getCUbigeoId());
                       
             legTchn.setFmostrarView1(false);
             legTchn.setFmostrarView2(false);
             legTchn.setFmostrarView3(false);
             
             legSeguiTchn.setFmostrar(false);
             legSeguiTchn.setFmostrarEje(false);    
             maeInmueble.setMaeUbigeo(maeUbigeo);
            
             LegalTchn legalTchnNew=new LegalTchn();
             legalTchnNew.setFondo(fondo) ;
             legalTchnNew.setMaeInversion(maeinversion) ;
             legalTchnNew.setIdetapa(idEtapa);
                 maeJuzgadoLegalList = legalServ.listarEstadoTchnLegal("0614");
                 maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615","0001");   
                if (idEtapa.equals("0616")){  /// calificacion
                  legTchn.setFmostrar(true); 
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(false);
                  legalTchnList2 = legalServ.listarEtapaTchnLegal(legalTchnNew);  
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList2.get(0).getLsCodJuz());  
               }else if (idEtapa.equals("0617")){  /// Postulatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(true);
                  legTchn.setFmostrarEt3(false);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                   maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
               }else if (idEtapa.equals("0618")){ /// Probatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
               }else if (idEtapa.equals("0619")){  /// Decisoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
               }else if (idEtapa.equals("0620")){  /// Impugnatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
               }else if (idEtapa.equals("0621")){   ///Ejecución
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
               }else if (idEtapa.equals("0622")){   ///Final
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
               }   
     
        } catch (Exception e) {

        }
    }
    

    public void ListarEtapaTchnMODY(MaeFondo fondo,MaeInversion maeinversion, String idEtapa  ) {
        try {
            
          //System.out.println("pop.webcobranzas.bean.EstadoCuentaBean.buscarTchn()");
            maeUbigeo.setCUbigeoId(maeUbigeoD.getCUbigeoId());
            maeUbigeo.setCUbigeoPad(maeUbigeoP.getCUbigeoId());
            
            //System.out.println("Ubigeo -> Distrito " + maeUbigeoD.getCUbigeoId());
            //System.out.println("Ubigeo -> Provincia " + maeUbigeoP.getCUbigeoId());
                       
             legTchn.setFmostrarView1(false);
             legTchn.setFmostrarView2(false);
             legTchn.setFmostrarView3(false);
             
             legSeguiTchn.setFmostrar(false);
             legSeguiTchn.setFmostrarEje(false);    
             maeInmueble.setMaeUbigeo(maeUbigeo);
             
             Date fecha = new Date();
             LegalTchn legalTchnNew=new LegalTchn();
             legalTchnNew.setFondo(fondo) ;
             legalTchnNew.setMaeInversion(maeinversion) ;
             legalTchnNew.setIdetapa(idEtapa);
     
             
                 maeJuzgadoLegalList = legalServ.listarEstadoTchnLegal("0614");
                 maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615","0001");   
                if (idEtapa.equals("0616")){  /// calificacion
                  legTchn.setFmostrar(true); 
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(false);
                  legalTchnNew.setIdetapa("0616");
                  legalTchnList2 = legalServ.listarEtapaTchnMODY(legalTchnNew);  
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList2.get(0).getLsCodJuz());  
                 
               }else if (idEtapa.equals("0617")){  /// Postulatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(true);
                  legTchn.setFmostrarEt3(false);
                  legalTchnNew.setIdetapa("0617");
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  
                  
                   maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
                   legalTchnList3.get(0).setIdetapa("0617");
                   legalTchnList3.get(0).setEtapa("Postulatoria");
                   
               }else if (idEtapa.equals("0618")){ /// Probatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
                  legalTchnList3.get(0).setIdetapa("0618");
                  legalTchnList3.get(0).setEtapa("Probatoria");
               }else if (idEtapa.equals("0619")){  /// Decisoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  legalTchnList3.get(0).setIdetapa("0619");
                  legalTchnList3.get(0).setEtapa("Decisoria");
               }else if (idEtapa.equals("0620")){  /// Impugnatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  legalTchnList3.get(0).setIdetapa("0620");
                  legalTchnList3.get(0).setEtapa("Impugnatoria");
               }else if (idEtapa.equals("0621")){   ///Ejecución
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  legalTchnList3.get(0).setIdetapa("0621");
                  legalTchnList3.get(0).setEtapa("Ejecución");
               }else if (idEtapa.equals("0622")){   ///Final
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  legalTchnList3.get(0).setIdetapa("0622");
                  legalTchnList3.get(0).setEtapa("Final");   
               }
 
        } catch (Exception e) {

        }
    }
    
    
    public void ListarSeguiTchnLegal(LegalTchn olegTchn) {
        try {
            
           
            maeUbigeo.setCUbigeoId(maeUbigeoD.getCUbigeoId());
            maeUbigeo.setCUbigeoPad(maeUbigeoP.getCUbigeoId());

            maeInmueble.setMaeUbigeo(maeUbigeo);
            olegTchn.setFmostrar(false);
            olegTchn.setFmostrarEt2(false);
            
            legSeguiTchn.setFmostrar(true);
            legSeguiTchn.setFondo(olegTchn.getFondo());
            legSeguiTchn.setMaeInversion(olegTchn.getMaeInversion());
            legSeguiTchn.setLs_tipoEtapa(olegTchn.getIdetapa());
            legSeguiTchn.setLsDestipoEtapa(olegTchn.getEtapa());
            
             LegalTchn legalTchnNew=new LegalTchn();
             legalTchnNew.setFondo(olegTchn.getFondo()) ;
             legalTchnNew.setMaeInversion(olegTchn.getMaeInversion()) ;
             legalTchnNew.setIdetapa(olegTchn.getIdetapa());
                 maeJuzgadoLegalList = legalServ.listarEstadoTchnLegal("0614");
                 maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615","0001");   
                if (olegTchn.getIdetapa().equals("0616")){  /// calificacion
                  legTchn.setFmostrarView1(true);
                  legTchn.setFmostrarView2(false);
                  legTchn.setFmostrarView3(false);
                  legalTchnList2 = legalServ.listarEtapaTchnLegal(legalTchnNew);  
                  
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList2.get(0).getLsCodJuz());  
               }else if (olegTchn.getIdetapa().equals("0617")){  /// Postulatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrarView1(false);
                  legTchn.setFmostrarView2(true);
                  legTchn.setFmostrarView3(false);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                   maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
               }else if (olegTchn.getIdetapa().equals("0618")){ /// Probatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrarView1(false);
                  legTchn.setFmostrarView2(false);
                  legTchn.setFmostrarView3(true);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
               }else if (olegTchn.getIdetapa().equals("0619")){  /// Decisoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrarView1(false);
                  legTchn.setFmostrarView2(false);
                  legTchn.setFmostrarView3(true);                  
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
               }else if (olegTchn.getIdetapa().equals("0620")){  /// Impugnatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrarView1(false);
                  legTchn.setFmostrarView2(false);
                  legTchn.setFmostrarView3(true);                  
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
               }else if (olegTchn.getIdetapa().equals("0621")){   ///Ejecución
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrarView1(false);
                  legTchn.setFmostrarView2(false);
                  legTchn.setFmostrarView3(true);                  
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
               }else if (olegTchn.getIdetapa().equals("0622")){   ///Final
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrarView1(false);
                  legTchn.setFmostrarView2(false);
                  legTchn.setFmostrarView3(true);                  
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
               }  
            legSeguiTchnList = legalServ.listarSeguiTchnLegal(legSeguiTchn);
            if (legalTchnList3.size()>0){
            legSeguiTchn.setSimbolo(legalTchnList3.get(0).getSimbolo());
            }
          } catch (Exception e) {

        }
    }
    
    
    
     public void ListarSeguiTchnMODY(LegalTchn olegTchn) {
        try {
            
            
            maeUbigeo.setCUbigeoId(maeUbigeoD.getCUbigeoId());
            maeUbigeo.setCUbigeoPad(maeUbigeoP.getCUbigeoId());

            maeInmueble.setMaeUbigeo(maeUbigeo);
            olegTchn.setFmostrar(false);
            olegTchn.setFmostrarEt2(false);
            
            legSeguiTchn.setFmostrar(true);
            legSeguiTchn.setFondo(olegTchn.getFondo());
            legSeguiTchn.setMaeInversion(olegTchn.getMaeInversion());
            legSeguiTchn.setLs_tipoEtapa(olegTchn.getIdetapa());
            legSeguiTchn.setLsDestipoEtapa(olegTchn.getEtapa());
            
             LegalTchn legalTchnNew=new LegalTchn();
             legalTchnNew.setFondo(olegTchn.getFondo()) ;
             legalTchnNew.setMaeInversion(olegTchn.getMaeInversion()) ;
              
             legalTchnNew.setIdetapa(olegTchn.getIdetapa());
                 maeJuzgadoLegalList = legalServ.listarEstadoTchnLegal("0614");
                 maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615","0001");   
                if (olegTchn.getIdetapa().equals("0616")){  /// calificacion
                  legTchn.setFmostrarView1(true);
                  legTchn.setFmostrarView2(false);
                  legTchn.setFmostrarView3(false);
                  
                  legalTchnList2 = legalServ.listarEtapaTchnMODY(legalTchnNew);  
                
                  
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList2.get(0).getLsCodJuz());  
               }else if (olegTchn.getIdetapa().equals("0617")){  /// Postulatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrarView1(false);
                  legTchn.setFmostrarView2(true);
                  legTchn.setFmostrarView3(false);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  System.out.println(legalTchnList3.get(0).getLsCodJuz()+"-"  + legalTchnList3.get(0).getLsNomJuz()+ " " + legalTchnList3.get(0).getLsCodEsp() +" - "+  legalTchnList3.get(0).getLsNomEsp() );
                   maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
                   legalTchnList3.get(0).setIdetapa("0617");
                   legalTchnList3.get(0).setEtapa("Postulatoria");
               }else if (olegTchn.getIdetapa().equals("0618")){ /// Probatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrarView1(false);
                  legTchn.setFmostrarView2(false);
                  legTchn.setFmostrarView3(true);
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());  
                   legalTchnList3.get(0).setIdetapa("0618");
                   legalTchnList3.get(0).setEtapa("Probatoria");
                   
               }else if (olegTchn.getIdetapa().equals("0619")){  /// Decisoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrarView1(false);
                  legTchn.setFmostrarView2(false);
                  legTchn.setFmostrarView3(true);                  
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz()); 
                  legalTchnList3.get(0).setIdetapa("0619");
                  legalTchnList3.get(0).setEtapa("Decisoria");
               }else if (olegTchn.getIdetapa().equals("0620")){  /// Impugnatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrarView1(false);
                  legTchn.setFmostrarView2(false);
                  legTchn.setFmostrarView3(true);                  
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
                  legalTchnList3.get(0).setIdetapa("0620");
                  legalTchnList3.get(0).setEtapa("Impugnatoria");
               }else if (olegTchn.getIdetapa().equals("0621")){   ///Ejecución
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrarView1(false);
                  legTchn.setFmostrarView2(false);
                  legTchn.setFmostrarView3(true);                  
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
                  legalTchnList3.get(0).setIdetapa("0621");
                  legalTchnList3.get(0).setEtapa("Ejecución");                  
               }else if (olegTchn.getIdetapa().equals("0622")){   ///Final
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrarView1(false);
                  legTchn.setFmostrarView2(false);
                  legTchn.setFmostrarView3(true);                  
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());   
                   legalTchnList3.get(0).setIdetapa("0622");
                  legalTchnList3.get(0).setEtapa("Final");  
               }  
            legSeguiTchnList = legalServ.listarSeguiTchnLegalMODY(legSeguiTchn);
          } catch (Exception e) {

        }
    }
    
    public void ListarSeguiTchnLegalRep(LegalTchn olegTchn) {
        try {
            
             maeUbigeo.setCUbigeoId(maeUbigeoD.getCUbigeoId());
            maeUbigeo.setCUbigeoPad(maeUbigeoP.getCUbigeoId());

            maeInmueble.setMaeUbigeo(maeUbigeo);
            olegTchn.setFmostrar(false);
            olegTchn.setFmostrarEt2(false);
            
            legSeguiTchn.setFmostrar(true);
            legSeguiTchn.setFondo(olegTchn.getFondo());
            legSeguiTchn.setMaeInversion(olegTchn.getMaeInversion());
            legSeguiTchn.setLs_tipoEtapa(olegTchn.getIdetapa());
            legSeguiTchn.setLsDestipoEtapa(olegTchn.getEtapa());
            
            LegalTchn legalTchnNew=new LegalTchn();
            legalTchnNew.setFondo(olegTchn.getFondo()) ;
            legalTchnNew.setMaeInversion(olegTchn.getMaeInversion()) ;
              
            legalTchnNew.setIdetapa(olegTchn.getIdetapa());
            maeJuzgadoLegalList = legalServ.listarEstadoTchnLegal("0614");
            maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615","0001");   

            legTchn.setFnuevo(false);
            legTchn.setFmostrarView1(false);
            legTchn.setFmostrarView2(false);
            legTchn.setFmostrarView3(true);                  
            legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
            if (legalTchnList3.size()>0) {
                maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
            }
            legSeguiTchn.setLs_tipoEtapa(null);
            legSeguiTchn.setLsDestipoEtapa("");
            legSeguiTchnList = legalServ.listarSeguiTchnLegal(legSeguiTchn);
       
        } catch (Exception e) {

        }
    }
    
   
    
    public void insertarDemanda(LegalTchn olegTchn ){
          int rep=0;
        
        String MError="";
        showMsg = true;
           System.out.println(olegTchn.getLsCodEsp() + "  "  + olegTchn.getLsCodJuz()+ " " + olegTchn.getDdescripcion()+ " "+ olegTchn.getLsNroExp());
        try {
            
            olegTchn.setLsusuario(SessionUtils.getUserName()); 
             if (olegTchn.getIdetapa().equals("0617")){
                 olegTchn.setLdFecha(olegTchn.getLdFechaAutomisor());
             }
               
            if (olegTchn.getIdetapa().equals("0616")){
                if (olegTchn.getLnCapital()!=0   && olegTchn.getLnMora()!=0 && olegTchn.getDdescripcion().trim().length()>0 && olegTchn.getLsCodJuz().length()>0    && olegTchn.getLsCodEsp().length()>0 ){
                   rep = legalServ.insertarDemanda(olegTchn);         
                }
            }else{
                    if (  olegTchn.getDdescripcion().length()>0 &&   olegTchn.getLnmontoAdm()>0   ){
                            rep = legalServ.insertarDemanda(olegTchn);   
                    }
            }
            
            if (rep == 0) {
                tipoMsj = "error";
                mensaje = "Error no se grabo Etapa";
            } else {
                if (rep == -1) {
                    tipoMsj = "error";
                    mensaje = "monto Admitido debe ser igual o mayor a capital";
                    rep =0;
                }else{
                     if (rep == -2) {
                        tipoMsj = "error";
                        mensaje = "fecha  debe ser igual o mayor del ultimo seguimiento";
                        rep =0;
                     }else{
                        tipoMsj = "success";
                        mensaje = "Se grabo Etapa";
                        rep =0;
                 }
                 }    
            }
           
        } catch (Exception e) {
             showMsg = true;
            tipoMsj = "error";
            mensaje = "Error al insertar Etapa";
        }
       
     }
    
    
    
     public void insertarDemandaMODY(LegalTchn olegTchn ){
         int rep=0;
        
        String MError="";
        showMsg = true;
           System.out.println(olegTchn.getLsCodEsp() + "  "  + olegTchn.getLsCodJuz()+ " " + olegTchn.getDdescripcion()+ " "+ olegTchn.getLsNroExp());
        try {
            
            olegTchn.setLsusuario(SessionUtils.getUserName()); 
             if (olegTchn.getIdetapa().equals("0617")){
                 olegTchn.setLdFecha(olegTchn.getLdFechaAutomisor());
             }
               
            if (olegTchn.getIdetapa().equals("0616")){
                if (olegTchn.getLnCapital()!=0 &&   olegTchn.getLnMora()!=0 && olegTchn.getDdescripcion().trim().length()>0 && olegTchn.getLsCodJuz().length()>0    && olegTchn.getLsCodEsp().length()>0 ){
                   rep = legalServ.insertarDemandaMODY(olegTchn);         
                }
            }else{
                    if (      olegTchn.getLnmontoAdm()>0   ){
                            rep = legalServ.insertarDemandaMODY(olegTchn);   
                    }
            }
            
            if (rep == 0) {
                tipoMsj = "error";
                mensaje = "Error no se grabo Etapa";
            } else {
                if (rep == -1){
                     tipoMsj = "error";
                     mensaje = "monto admitido debe ser igual o mayor al capital";
                     
                 }else{
                    if (rep == -2){
                       tipoMsj = "error";
                       mensaje = "fecha debe ser igual o mayor del ultimo seguimiento";
                    }else{    
                       tipoMsj = "success";
                       mensaje = "Se grabo Etapa";
                       rep =0;
                      }
                 }
            }
           
        } catch (Exception e) {
             showMsg = true;
            tipoMsj = "error";
            mensaje = "Error al insertar Etapa";
        }
       
     }
     
     public void nuevoetapa(LegalTchn olegTchn){
         
         try {
             Date fecha = new Date();
             ArrayList<LegalTchn> lstTchn = new ArrayList<>();
             LegalTchn legalTchnNew=new LegalTchn();
             legalTchnNew.setFondo(olegTchn.getFondo());
             legalTchnNew.setMaeInversion(olegTchn.getMaeInversion());
             legalTchnNew.setIdetapa(olegTchn.getIdetapa());
             legalTchnNew.setLdFecha(fecha);
             legalTchnNew.setLdFechaTasa(olegTchn.getLdFechaTasa());
             legalTchnNew.setNvaloriza(olegTchn.getNvaloriza());
             legalTchnNew.setLnCapital(0.00);
             legalTchnNew.setLnInteres(0.00);
             legalTchnNew.setLnmontoAdm(0.00);
             legalTchnNew.setLsCodEsp("Todos");
             legalTchnNew.setLsCodJuz("Todos");
             legalTchnNew.setLnMora(0.00);
             legalTchnNew.setSimbolo(olegTchn.getSimbolo());
             System.out.println("este es el simbolo"+olegTchn.getSimbolo());
             maeJuzgadoLegalList = legalServ.listarEstadoTchnLegal("0614");
             maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615","0001");   
             if (olegTchn.getEtapa().contains("Cobranza")){
                 legalTchnNew.setEtapa("Calificación");
                 legTchn.setFmostrar(true);
                 legTchn.setFmostrarEt2(false);
                 legTchn.setFmostrarEt3(false);
              }else{
                 legalTchnNew.setEtapa(legalTchnList.get(0).getEtapa());
                 
                       if (legalTchnNew.getIdetapa().equals("0616")){   /// calificacion
                           legalTchnNew.setLdFechaAutomisor(fecha);
                           legTchn.setFnuevo(true);
                           legTchn.setFmostrar(true);
                           legTchn.setFmostrarEt2(false);
                           legTchn.setFmostrarEt3(false);
                        }else if (legalTchnNew.getIdetapa().equals("0617")){ // postulatoria
                           legalTchnNew.setIdetapa("0616");
                           legalTchnListEt3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                           legalTchnNew.setLnCapital(legalTchnListEt3.get(0).getLnCapital());
                           legalTchnNew.setLnInteres(legalTchnListEt3.get(0).getLnInteres());
                           legalTchnNew.setLnMora(legalTchnListEt3.get(0).getLnMora());
                           legalTchnNew.setLnTotal(legalTchnListEt3.get(0).getLnTotal());
                           legalTchnNew.setLsCodEsp(legalTchnListEt3.get(0).getLsCodEsp());
                           legalTchnNew.setLsCodJuz(legalTchnListEt3.get(0).getLsCodJuz());
                           legalTchnNew.setLsNomEsp(legalTchnListEt3.get(0).getLsNomEsp());
                           legalTchnNew.setLsNomJuz(legalTchnListEt3.get(0).getLsNomJuz());
                           legalTchnNew.setLnmontoAdm(legalTchnListEt3.get(0).getLnmontoAdm());
                           legalTchnNew.setLsNroExp(legalTchnListEt3.get(0).getLsNroExp());
                           legalTchnNew.setLdFechaAutomisor(fecha);
                           maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnListEt3.get(0).getLsCodJuz());   
                           legalTchnNew.setIdetapa("0617");
                           legTchn.setFnuevo(false);
                           legTchn.setFmostrar(false);
                           legTchn.setFmostrarEt2(true);
                           legTchn.setFmostrarEt3(false);
                        }else if (legalTchnNew.getIdetapa().equals("0618")){  /// probatoria
                           legalTchnNew.setIdetapa("0617");
                           legalTchnListEt3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                           legalTchnNew.setLnCapital(legalTchnListEt3.get(0).getLnCapital());
                           legalTchnNew.setLnInteres(legalTchnListEt3.get(0).getLnInteres());
                           legalTchnNew.setLnMora(legalTchnListEt3.get(0).getLnMora());
                           legalTchnNew.setLnTotal(legalTchnListEt3.get(0).getLnTotal());
                           legalTchnNew.setLsCodEsp(legalTchnListEt3.get(0).getLsCodEsp());
                           legalTchnNew.setLsCodJuz(legalTchnListEt3.get(0).getLsCodJuz());
                           legalTchnNew.setLsNomEsp(legalTchnListEt3.get(0).getLsNomEsp());
                           legalTchnNew.setLsNomJuz(legalTchnListEt3.get(0).getLsNomJuz());
                           legalTchnNew.setLnmontoAdm(legalTchnListEt3.get(0).getLnmontoAdm());
                           legalTchnNew.setLsNroExp(legalTchnListEt3.get(0).getLsNroExp());
                           legalTchnNew.setLdFechaAutomisor(legalTchnListEt3.get(0).getLdFechaAutomisor());
                           maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnListEt3.get(0).getLsCodJuz());   
                           legalTchnNew.setIdetapa("0618");
                           legTchn.setFnuevo(false);
                           legTchn.setFmostrar(false);
                           legTchn.setFmostrarEt2(false);
                           legTchn.setFmostrarEt3(true);
                        }else if (legalTchnNew.getIdetapa().equals("0619")){  /// Decisoria
                           legalTchnNew.setIdetapa("0618");
                           legalTchnListEt3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                           legalTchnNew.setLnCapital(legalTchnListEt3.get(0).getLnCapital());
                           legalTchnNew.setLnInteres(legalTchnListEt3.get(0).getLnInteres());
                           legalTchnNew.setLnMora(legalTchnListEt3.get(0).getLnMora());
                           legalTchnNew.setLnTotal(legalTchnListEt3.get(0).getLnTotal());
                           legalTchnNew.setLsCodEsp(legalTchnListEt3.get(0).getLsCodEsp());
                           legalTchnNew.setLsCodJuz(legalTchnListEt3.get(0).getLsCodJuz());
                           legalTchnNew.setLsNomEsp(legalTchnListEt3.get(0).getLsNomEsp());
                           legalTchnNew.setLsNomJuz(legalTchnListEt3.get(0).getLsNomJuz());
                           legalTchnNew.setLnmontoAdm(legalTchnListEt3.get(0).getLnmontoAdm());
                           legalTchnNew.setLsNroExp(legalTchnListEt3.get(0).getLsNroExp());
                           legalTchnNew.setLdFechaAutomisor(legalTchnListEt3.get(0).getLdFechaAutomisor()); 
                           maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnListEt3.get(0).getLsCodJuz());   
                           legalTchnNew.setIdetapa("0619");
                           legTchn.setFnuevo(false);
                           legTchn.setFmostrar(false);
                           legTchn.setFmostrarEt2(false);
                           legTchn.setFmostrarEt3(true);
                        }else if (legalTchnNew.getIdetapa().equals("0620")){   /// Impugnatoria       
                           legalTchnNew.setIdetapa("0619");
                           legalTchnListEt3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                           legalTchnNew.setLnCapital(legalTchnListEt3.get(0).getLnCapital());
                           legalTchnNew.setLnInteres(legalTchnListEt3.get(0).getLnInteres());
                           legalTchnNew.setLnMora(legalTchnListEt3.get(0).getLnMora());
                           legalTchnNew.setLnTotal(legalTchnListEt3.get(0).getLnTotal());
                           legalTchnNew.setLsCodEsp(legalTchnListEt3.get(0).getLsCodEsp());
                           legalTchnNew.setLsCodJuz(legalTchnListEt3.get(0).getLsCodJuz());
                           legalTchnNew.setLsNomEsp(legalTchnListEt3.get(0).getLsNomEsp());
                           legalTchnNew.setLsNomJuz(legalTchnListEt3.get(0).getLsNomJuz());
                           legalTchnNew.setLnmontoAdm(legalTchnListEt3.get(0).getLnmontoAdm());
                           legalTchnNew.setLsNroExp(legalTchnListEt3.get(0).getLsNroExp());
                           legalTchnNew.setLdFechaAutomisor(legalTchnListEt3.get(0).getLdFechaAutomisor());
                           maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnListEt3.get(0).getLsCodJuz());                            
                           legalTchnNew.setIdetapa("0620");
                           legTchn.setFnuevo(false);
                           legTchn.setFmostrar(false);
                           legTchn.setFmostrarEt2(false);
                           legTchn.setFmostrarEt3(true);
                        }else if (legalTchnNew.getIdetapa().equals("0621")){  /// ejecucion
                           legalTchnNew.setIdetapa("0617");
                           legalTchnListEt3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                           legalTchnNew.setLnCapital(legalTchnListEt3.get(0).getLnCapital());
                           legalTchnNew.setLnInteres(legalTchnListEt3.get(0).getLnInteres());
                           legalTchnNew.setLnMora(legalTchnListEt3.get(0).getLnMora());
                           legalTchnNew.setLnTotal(legalTchnListEt3.get(0).getLnTotal());
                           legalTchnNew.setLsCodEsp(legalTchnListEt3.get(0).getLsCodEsp());
                           legalTchnNew.setLsCodJuz(legalTchnListEt3.get(0).getLsCodJuz());
                           legalTchnNew.setLsNomEsp(legalTchnListEt3.get(0).getLsNomEsp());
                           legalTchnNew.setLsNomJuz(legalTchnListEt3.get(0).getLsNomJuz());
                           legalTchnNew.setLnmontoAdm(legalTchnListEt3.get(0).getLnmontoAdm());
                           legalTchnNew.setLsNroExp(legalTchnListEt3.get(0).getLsNroExp()); 
                           legalTchnNew.setLdFechaAutomisor(legalTchnListEt3.get(0).getLdFechaAutomisor());
                           maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnListEt3.get(0).getLsCodJuz());                             
                           legTchn.setFnuevo(false);
                           legTchn.setFmostrar(false);
                           legTchn.setFmostrarEt2(false);
                           legTchn.setFmostrarEt3(true);
                           legalTchnNew.setIdetapa("0621");
                         }else if (legalTchnNew.getIdetapa().equals("0622")){  /// final
                           legalTchnNew.setIdetapa("0621");
                           legalTchnListEt3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                           legalTchnNew.setLnCapital(legalTchnListEt3.get(0).getLnCapital());
                           legalTchnNew.setLnInteres(legalTchnListEt3.get(0).getLnInteres());
                           legalTchnNew.setLnMora(legalTchnListEt3.get(0).getLnMora());
                           legalTchnNew.setLnTotal(legalTchnListEt3.get(0).getLnTotal());
                           legalTchnNew.setLsCodEsp(legalTchnListEt3.get(0).getLsCodEsp());
                           legalTchnNew.setLsCodJuz(legalTchnListEt3.get(0).getLsCodJuz());
                           legalTchnNew.setLsNomEsp(legalTchnListEt3.get(0).getLsNomEsp());
                           legalTchnNew.setLsNomJuz(legalTchnListEt3.get(0).getLsNomJuz());
                           legalTchnNew.setLnmontoAdm(legalTchnListEt3.get(0).getLnmontoAdm());
                           legalTchnNew.setLsNroExp(legalTchnListEt3.get(0).getLsNroExp());
                           legalTchnNew.setLdFechaAutomisor(legalTchnListEt3.get(0).getLdFechaAutomisor());
                           maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnListEt3.get(0).getLsCodJuz());                             
                           legalTchnNew.setIdetapa("0622");
                           legTchn.setFnuevo(false);
                           legTchn.setFmostrar(false);
                           legTchn.setFmostrarEt2(false);
                           legTchn.setFmostrarEt3(true);
                         }
                 }
             
             lstTchn.add(legalTchnNew);
             legalTchnList3=lstTchn; 
             legalTchnList2=lstTchn;  /// calificacion
           
             
        } catch (Exception e) {
             
        }
       
     }
     
     
     
     
     
     public void nuevoetapaMody(LegalTchn olegTchn){
      
        
      
      try {
            
            //System.out.println("pop.webcobranzas.bean.EstadoCuentaBean.buscarTchn()");
            maeUbigeo.setCUbigeoId(maeUbigeoD.getCUbigeoId());
            maeUbigeo.setCUbigeoPad(maeUbigeoP.getCUbigeoId());
            
            //System.out.println("Ubigeo -> Distrito " + maeUbigeoD.getCUbigeoId());
            //System.out.println("Ubigeo -> Provincia " + maeUbigeoP.getCUbigeoId());
                       
             legTchn.setFmostrarView1(false);
             legTchn.setFmostrarView2(false);
             legTchn.setFmostrarView3(false);
             
             legSeguiTchn.setFmostrar(false);
             legSeguiTchn.setFmostrarEje(false);    
             maeInmueble.setMaeUbigeo(maeUbigeo);
             
             Date fecha = new Date();
             LegalTchn legalTchnNew=new LegalTchn();
             legalTchnNew.setFondo(olegTchn.getFondo()) ;
             legalTchnNew.setMaeInversion(olegTchn.getMaeInversion()) ;
             legalTchnNew.setIdetapa(olegTchn.getIdetapa());
             String idEtapa= olegTchn.getIdetapa();
             
                 maeJuzgadoLegalList = legalServ.listarEstadoTchnLegal("0614");
                 maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615","0001");   
                if (idEtapa.equals("0616")){  /// calificacion
                  legTchn.setFmostrar(true); 
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(false);
                  legalTchnNew.setIdetapa("0616");
                  legalTchnList2 = legalServ.listarEtapaTchnMODY(legalTchnNew);  
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList2.get(0).getLsCodJuz());  
                 
               }else if (idEtapa.equals("0617")){  /// Postulatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(true);
                  legTchn.setFmostrarEt3(false);
                  legalTchnNew.setIdetapa("0616");
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  
                  
                   maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
                   legalTchnList3.get(0).setIdetapa("0617");
                   legalTchnList3.get(0).setEtapa("Postulatoria");
                   
               }else if (idEtapa.equals("0618")){ /// Probatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnNew.setIdetapa("0617");
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  
                  maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",legalTchnList3.get(0).getLsCodJuz());    
                  
                  legalTchnList3.get(0).setIdetapa("0618");
                  legalTchnList3.get(0).setEtapa("Probatoria");
               }else if (idEtapa.equals("0619")){  /// Decisoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnNew.setIdetapa("0617");
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  legalTchnList3.get(0).setIdetapa("0619");
                  legalTchnList3.get(0).setEtapa("Decisoria");
               }else if (idEtapa.equals("0620")){  /// Impugnatoria
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnNew.setIdetapa("0617");
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  legalTchnList3.get(0).setIdetapa("0620");
                  legalTchnList3.get(0).setEtapa("Impugnatoria");
               }else if (idEtapa.equals("0621")){   ///Ejecución
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnNew.setIdetapa("0617");
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  legalTchnList3.get(0).setIdetapa("0621");
                  legalTchnList3.get(0).setEtapa("Ejecución");
               }else if (idEtapa.equals("0622")){   ///Final
                  legTchn.setFnuevo(false);
                  legTchn.setFmostrar(false);
                  legTchn.setFmostrarEt2(false);
                  legTchn.setFmostrarEt3(true);
                  legalTchnNew.setIdetapa("0617");
                  legalTchnList3 = legalServ.listarEtapaTchnLegal(legalTchnNew);
                  legalTchnList3.get(0).setIdetapa("0622");
                  legalTchnList3.get(0).setEtapa("Final");   
               }
 
   
             System.out.println("SISTEMAS" + legalTchnList3.size()+" - " + legalTchnNew.getIdetapa() );
        } catch (Exception e) {

        }
       
     }
     
    public void  ConsultarEspecial(String codjuz){
        try {
            
            maeEspecialLegalList = legalServ.listarEspecialTchnLegal("0615",codjuz);     
        } catch (Exception e) {
        }
 
    }
     
    public void nuevosegui(LegalTchn olegTchn2 ){
        try {
             ArrayList<LegSeguiTchn> lstTchn = new ArrayList<>();
             LegSeguiTchn legSeguiTchnNEW=new LegSeguiTchn();
             legSeguiTchnNEW.setFondo(legSeguiTchn.getFondo());
             legSeguiTchnNEW.setMaeInversion(legSeguiTchn.getMaeInversion());
             legSeguiTchnNEW.setLsCodigoTchn(legSeguiTchn.getMaeInversion().getCInversion());
             legSeguiTchnNEW.setSimbolo(legSeguiTchn.getSimbolo());
             legSeguiTchnNEW.setLs_tipoEtapa(legSeguiTchn.getLs_tipoEtapa());
             legSeguiTchnNEW.setLsDestipoEtapa(legSeguiTchn.getLsDestipoEtapa());
             legSeguiTchnNEW.setLnMontoval(0.00);
             legSeguiTchnNEW.setLnMontodolval(0.00);
             legSeguiTchnNEW.setLnInteres(0.00);
             legSeguiTchnNEW.setLnCostas(0.00);
             legSeguiTchnNEW.setLnCostos(0.00);
             legSeguiTchnNEW.setLnIntMora(0.00);
            System.out.println("paso por qui2"+legSeguiTchn.getSimbolo());
             Date fecha = new Date();
             legSeguiTchnNEW.setLd_fecha(fecha);
             legSeguiTchnNEW.setLdFechaval(fecha);
           
             if  (legSeguiTchnList.size()==0){
                legSeguiTchnNEW.setLnCorrelativo(1);
             }else{
                 legSeguiTchnNEW.setLnCorrelativo(legSeguiTchnList.get(0).getLnCorrelativo());
             } 
            System.out.println("paso por qui3");
             lstTchn.add(legSeguiTchnNEW);
             String idEtapa=legSeguiTchn.getLs_tipoEtapa();
           System.out.println("paso por qui4");
             if (idEtapa.equals("0621")){
                legSeguiTchn.setFnuevoEje(true);
                legSeguiTchn.setFnuevo(false);
             }else{
                legSeguiTchn.setFnuevo(true);
                legSeguiTchn.setFnuevoEje(false);
             }
             legSeguiTchnList2=lstTchn;
      System.out.println("paso por qui5");
             maeEstadoLegalList = legalServ.listarEstadoTchnLegal(idEtapa);
         System.out.println("paso por qui6");
             
        } catch (Exception e) {
        }
     }
    
    public void modificarsegui(LegSeguiTchn olegSeguiTchn){
        
      
        try {
              
              legSeguiTchnList2 = legalServ.modificarSegLegal(olegSeguiTchn);
             String idEtapa=legSeguiTchnList2.get(0).getLs_tipoEtapa();
              if (idEtapa.equals("0621")){
                legSeguiTchn.setFnuevoEje(true);
                legSeguiTchn.setFnuevo(false);
             }else{
                legSeguiTchn.setFnuevo(true);
                legSeguiTchn.setFnuevoEje(false);
             }
             maeEstadoLegalList = legalServ.listarEstadoTchnLegal(idEtapa);
         } catch (Exception e) {
             
        }
    }
    public void EliminarMODY(LegSeguiTchn olegSeguiTchn ){
    showMsg = true;
        int rep =0;
       try {
            
            olegSeguiTchn.setLs_UsuarioAdd(SessionUtils.getUserName()); 
            legSeguiTchn.setFnuevo(false);
            legSeguiTchn.setFmostrar(false);
            legTchn.setFmostrarView1(false);
            legTchn.setFmostrarView2(false);
            legTchn.setFmostrarView3(false);      
             
            
            rep = legalServ.eliminarMODY(olegSeguiTchn);
          
            if (rep == 0) {
                tipoMsj = "error";
                mensaje = "Error al Elimino Seguimiento";
            } else {
                tipoMsj = "success";
                mensaje = "Se Elimino Seguimiento";
                rep =0;
            }
            
            

        } catch (Exception e) {
            
            showMsg = true;
            tipoMsj = "error";
            mensaje = "Error  al guardar seguimiento " ;
                 
        }  
    } 
        
    
    public void modificarseguiMODY(LegSeguiTchn olegSeguiTchn,String idEtapa){
        
      
        try {
              
              legSeguiTchnList2 = legalServ.modificarSegLegalMODY(olegSeguiTchn);
              
             legSeguiTchnList2.get(0).setLs_tipoEtapa(idEtapa);
              if (idEtapa.equals("0621")){
                legSeguiTchn.setFnuevoEje(true);
                legSeguiTchn.setFnuevo(false);
             }else{
                legSeguiTchn.setFnuevo(true);
                legSeguiTchn.setFnuevoEje(false);
             }
             maeEtapaLegalList = legalServ.listarEstadoTchnLegalMODY();
             legSeguiTchn.setFmostrar(false);
             
             maeEstadoLegalList = legalServ.listarEstadoTchnLegal(idEtapa);
         } catch (Exception e) {
             
        }
    }
    
    public void insertarSegDemanda(LegSeguiTchn olegSeguiTchn){
      showMsg = true;
        int rep =0;
       try {
           
            olegSeguiTchn.setLs_UsuarioAdd(SessionUtils.getUserName()); 
           
            if (olegSeguiTchn.isCconcluir()){
                  olegSeguiTchn.setFconcluir("S");
            }else{
                olegSeguiTchn.setFconcluir("N");
            }
            
            if (olegSeguiTchn.isCremate()){
                  olegSeguiTchn.setFremate("S");
            }else{
                olegSeguiTchn.setFremate("N");
            }
         
        
            
         if (olegSeguiTchn.getLsCodEst().trim().length()>0 && olegSeguiTchn.getLsDescrip().trim().length()>0 ){
             
             if (olegSeguiTchn.getLs_tipoEtapa().equals("0616") || olegSeguiTchn.getLs_tipoEtapa().equals("0621") || olegSeguiTchn.getLs_tipoEtapa().equals("0622")  )
             {
                     rep = legalServ.insertarSegDemanda(olegSeguiTchn);
             }else{        
                if (olegSeguiTchn.getFremate().equals("S") && olegSeguiTchn.getLsCodEst().trim().equals("0001") ){
                    rep = legalServ.insertarSegDemanda(olegSeguiTchn);
                }    
               if (olegSeguiTchn.getFremate().equals("N") && !olegSeguiTchn.getLsCodEst().trim().equals("0001") ){
                    rep = legalServ.insertarSegDemanda(olegSeguiTchn);
               }    
            }
         }   
          
            if (rep == 0) {
                tipoMsj = "error";
                mensaje = "Error al guardar Seguimiento";
            } else {
                if (rep == -1) {
                    tipoMsj = "error";
                    mensaje = "Error la fecha debe ser mayor igua al ultimo Seguimiento";
                    rep =0;
                }else{
                    tipoMsj = "success";
                    mensaje = "Se guardo Seguimiento";
                    rep =0;
                }
            }
            

        } catch (Exception e) {
            
            showMsg = true;
            tipoMsj = "error";
            mensaje = "Error  al guardar seguimiento " ;
                 
        }  
    }
    
    
    public void insertarSegDemandaMODY(LegSeguiTchn olegSeguiTchn,String idtipoEtapa){
     showMsg = true;
        int rep =0;
       try {
           
            olegSeguiTchn.setLs_UsuarioAdd(SessionUtils.getUserName()); 
           
            if (olegSeguiTchn.isCconcluir()){
                  olegSeguiTchn.setFconcluir("S");
            }else{
                olegSeguiTchn.setFconcluir("N");
            }
            
            if (olegSeguiTchn.isCremate()){
                  olegSeguiTchn.setFremate("S");
            }else{
                olegSeguiTchn.setFremate("N");
            }
         
        
            
         if (olegSeguiTchn.getLsCodEst().trim().length()>0 && olegSeguiTchn.getLsDescrip().trim().length()>0 ){
             
             if (olegSeguiTchn.getLs_tipoEtapa().equals("0616") || olegSeguiTchn.getLs_tipoEtapa().equals("0621") || olegSeguiTchn.getLs_tipoEtapa().equals("0622")  )
             {
                     rep = legalServ.insertarSegDemandaMODY(olegSeguiTchn);
                     if (rep==1){
                            cerrarSeguimiento();
                            cerrarSeguimientoEje();
                    }
             }else{        
                if (olegSeguiTchn.getFremate().equals("S") && olegSeguiTchn.getLsCodEst().trim().equals("0001") ){
                    rep = legalServ.insertarSegDemandaMODY(olegSeguiTchn);
                    if (rep==1){
                            cerrarSeguimiento();
                            cerrarSeguimientoEje();
                    }
                }    
               if (olegSeguiTchn.getFremate().equals("N") && !olegSeguiTchn.getLsCodEst().trim().equals("0001") ){
                    rep = legalServ.insertarSegDemandaMODY(olegSeguiTchn);
                    if (rep==1){
                            cerrarSeguimiento();
                            cerrarSeguimientoEje();
                    }
               }    
            }
         }   
          
            if (rep == 0) {
                tipoMsj = "error";
                mensaje = "Error al guardar Seguimiento";
            } else {
                if (rep == -1) {
                    tipoMsj = "error";
                    mensaje = "Error la fecha debe ser mayor igua al ultimo Seguimiento";
                    rep =0;
                }else{

                    tipoMsj = "success";
                    mensaje = "Se guardo Seguimiento";
                    rep =0;
                    
                }
            }
            

        } catch (Exception e) {
            
            showMsg = true;
            tipoMsj = "error";
            mensaje = "Error  al guardar seguimiento " ;
                 
        }  
    }
    
   
    public void CambiarFlag( ){
          showMsg = false;
        
    }
     
    
    public void CierreEtapaSegDemanda(LegSeguiTchn olegSeguiTchn ){
         showMsg = true;
        int rep =0;
       try {
             olegSeguiTchn.setLs_UsuarioAdd(SessionUtils.getUserName()); 
        
            
           
            if (olegSeguiTchn.isCconcluir()){
                  olegSeguiTchn.setFconcluir("S");
            }else{
                olegSeguiTchn.setFconcluir("N");
            }
            
            if (olegSeguiTchn.isCremate()){
                  olegSeguiTchn.setFremate("S");
            }else{
                olegSeguiTchn.setFremate("N");
            }
            if (olegSeguiTchn.getLsCodEst().trim().length()>0 && olegSeguiTchn.getLsDescrip().trim().length()>0 ){
             
             if (olegSeguiTchn.getLs_tipoEtapa().equals("0616") || olegSeguiTchn.getLs_tipoEtapa().equals("0621") || olegSeguiTchn.getLs_tipoEtapa().equals("0622")  )
             {
                     rep = legalServ.insertarSegDemanda(olegSeguiTchn);
                     if (rep>0){
                        legalServ.CerrarEtapaSegDemanda(olegSeguiTchn);
                        LegalTchn legalTchnNew=new LegalTchn();
                        legalTchnNew.setFondo(legTchn.getFondo());
                        legalTchnNew.setMaeInversion(legTchn.getMaeInversion());
                        legalTchnList = legalServ.listarTchnLegal(legalTchnNew);
                        legSeguiTchn.setFnuevo(false);
                        legSeguiTchn.setFmostrar(false);
                        legSeguiTchn.setFnuevoEje(false);
                        legTchn.setFmostrarView1(false);
                        legTchn.setFmostrarView2(false);
                        legTchn.setFmostrarView3(false); 
                     }   
             }else{        
                if (olegSeguiTchn.getFremate().equals("S") && olegSeguiTchn.getLsCodEst().trim().equals("0001") ){
                    rep = legalServ.insertarSegDemanda(olegSeguiTchn);
                     if (rep>0){
                        legalServ.CerrarEtapaSegDemanda(olegSeguiTchn);
                         LegalTchn legalTchnNew=new LegalTchn();
                         legalTchnNew.setFondo(legTchn.getFondo());
                         legalTchnNew.setMaeInversion(legTchn.getMaeInversion());
                         legalTchnList = legalServ.listarTchnLegal(legalTchnNew);
                         legSeguiTchn.setFnuevo(false);
                         legSeguiTchn.setFmostrar(false);
                         legSeguiTchn.setFnuevoEje(false);
                         legTchn.setFmostrarView1(false);
                         legTchn.setFmostrarView2(false);
                         legTchn.setFmostrarView3(false); 
                     }     
                }    
               if (olegSeguiTchn.getFremate().equals("N") && !olegSeguiTchn.getLsCodEst().trim().equals("0001") ){
                    rep = legalServ.insertarSegDemanda(olegSeguiTchn);
                     if (rep>0){
                        legalServ.CerrarEtapaSegDemanda(olegSeguiTchn);
                         LegalTchn legalTchnNew=new LegalTchn();
                         legalTchnNew.setFondo(legTchn.getFondo());
                         legalTchnNew.setMaeInversion(legTchn.getMaeInversion());
                         legalTchnList = legalServ.listarTchnLegal(legalTchnNew);
                         legSeguiTchn.setFnuevo(false);
                         legSeguiTchn.setFmostrar(false);
                         legSeguiTchn.setFnuevoEje(false);
                         legTchn.setFmostrarView1(false);
                         legTchn.setFmostrarView2(false);
                         legTchn.setFmostrarView3(false); 
                     }  
               }    
            }
         }   
           
            if (rep == 0) {
                tipoMsj = "error";
                mensaje = "Error al guardar Seguimiento";
            } else {
                if (rep == -1) {
                    tipoMsj = "error";
                    mensaje = "Error la fecha debe ser mayor igua al ultimo Seguimiento";
                    rep =0;
                }else{
                    tipoMsj = "success";
                    mensaje = "Se guardo Seguimiento";
                    rep =0;
                }
            }
            
        } catch (Exception e) {
                 
        }  
    }
    
     public void CierreEtapaSegDemandaMODY(LegSeguiTchn olegSeguiTchn,String idEtapa ){
        int rep =0;
         showMsg = true;
       try {
             olegSeguiTchn.setLs_UsuarioAdd(SessionUtils.getUserName()); 
        
            olegSeguiTchn.setLs_tipoEtapa(idEtapa);
           
            if (olegSeguiTchn.isCconcluir()){
                  olegSeguiTchn.setFconcluir("S");
            }else{
                olegSeguiTchn.setFconcluir("N");
            }
            
            if (olegSeguiTchn.isCremate()){
                  olegSeguiTchn.setFremate("S");
            }else{
                olegSeguiTchn.setFremate("N");
            }
            
            
            
            
             if (olegSeguiTchn.getLsCodEst().trim().length()>0 && olegSeguiTchn.getLsDescrip().trim().length()>0 ){
             
             if (olegSeguiTchn.getLs_tipoEtapa().equals("0616") || olegSeguiTchn.getLs_tipoEtapa().equals("0621") || olegSeguiTchn.getLs_tipoEtapa().equals("0622")  )
             {
                     rep = legalServ.insertarSegDemandaMODY(olegSeguiTchn);
                    if (rep>0){
                        legalServ.CerrarEtapaSegDemanda(olegSeguiTchn);
                       LegalTchn legalTchnNew=new LegalTchn();
                       legalTchnNew.setFondo(legTchn.getFondo());
                       legalTchnNew.setMaeInversion(legTchn.getMaeInversion());
                       legalTchnList = legalServ.listarTchnLegal(legalTchnNew);
                       legSeguiTchn.setFnuevo(false);
                       legSeguiTchn.setFmostrar(false);
                       legSeguiTchn.setFnuevoEje(false);
                       legTchn.setFmostrarView1(false);
                       legTchn.setFmostrarView2(false);
                       legTchn.setFmostrarView3(false); 
                    }
             }else{        
                if (olegSeguiTchn.getFremate().equals("S") && olegSeguiTchn.getLsCodEst().trim().equals("0001") ){
                    rep = legalServ.insertarSegDemandaMODY(olegSeguiTchn);
                    if (rep>0){
                        legalServ.CerrarEtapaSegDemanda(olegSeguiTchn);
                      LegalTchn legalTchnNew=new LegalTchn();
                      legalTchnNew.setFondo(legTchn.getFondo());
                      legalTchnNew.setMaeInversion(legTchn.getMaeInversion());
                      legalTchnList = legalServ.listarTchnLegal(legalTchnNew);
                      legSeguiTchn.setFnuevo(false);
                      legSeguiTchn.setFmostrar(false);
                      legSeguiTchn.setFnuevoEje(false);
                      legTchn.setFmostrarView1(false);
                      legTchn.setFmostrarView2(false);
                      legTchn.setFmostrarView3(false); 
                   }
                }    
               if (olegSeguiTchn.getFremate().equals("N") && !olegSeguiTchn.getLsCodEst().trim().equals("0001") ){
                    rep = legalServ.insertarSegDemandaMODY(olegSeguiTchn);
                     if (rep>0){
                        legalServ.CerrarEtapaSegDemanda(olegSeguiTchn);
                        LegalTchn legalTchnNew=new LegalTchn();
                        legalTchnNew.setFondo(legTchn.getFondo());
                        legalTchnNew.setMaeInversion(legTchn.getMaeInversion());
                        legalTchnList = legalServ.listarTchnLegal(legalTchnNew);
                        legSeguiTchn.setFnuevo(false);
                        legSeguiTchn.setFmostrar(false);
                        legSeguiTchn.setFnuevoEje(false);
                        legTchn.setFmostrarView1(false);
                        legTchn.setFmostrarView2(false);
                        legTchn.setFmostrarView3(false); 
                     }   
                        
               }    
            }
         }   
            if (rep == 0) {
                tipoMsj = "error";
                mensaje = "Error al guardar Seguimiento";
            } else {
                if (rep == -1) {
                    tipoMsj = "error";
                    mensaje = "Error la fecha debe ser mayor igua al ultimo Seguimiento";
                    rep =0;
                }else{
                    tipoMsj = "success";
                    mensaje = "Se guardo Seguimiento";
                    rep =0;
                }
            }
            
        } catch (Exception e) {
                 
        }  
    }
     
    
      public void cerrarSeguimiento(){
 
        try {
            
            legSeguiTchn.setFnuevo(false);
            legSeguiTchn.setFmostrar(false);
            legTchn.setFmostrarView1(false);
            legTchn.setFmostrarView2(false);
            legTchn.setFmostrarView3(false);              
            LegalTchn olegTchnnew=new LegalTchn();
            olegTchnnew.setFondo(legTchn.getFondo());
            olegTchnnew.setMaeInversion(legTchn.getMaeInversion());
            legalTchnList = legalServ.listarTchnLegal(olegTchnnew);
        } catch (Exception e) {
                 
        }
       
     }
       public void cerrarSeguimientoEje(){
 
        try {
            
            legSeguiTchn.setFnuevoEje(false);
            legSeguiTchn.setFmostrarEje(false);
            legSeguiTchn.setFnuevo(false);
            legSeguiTchn.setFmostrar(false);
            legTchn.setFmostrarView1(false);
            legTchn.setFmostrarView2(false);
            legTchn.setFmostrarView3(false);              
            LegalTchn olegTchnnew=new LegalTchn();
            olegTchnnew.setFondo(legTchn.getFondo());
            olegTchnnew.setMaeInversion(legTchn.getMaeInversion());
            legalTchnList = legalServ.listarTchnLegal(olegTchnnew);
        } catch (Exception e) {
                 
        }
       
     }
       
      
    public void cerrarPantallaEtapa(){
 
        try {
             
            LegalTchn legalTchnNew=new LegalTchn();
            legalTchnNew.setFondo(legTchn.getFondo());
            legalTchnNew.setMaeInversion(legTchn.getMaeInversion());
            legalTchnList = legalServ.listarTchnLegal(legalTchnNew);
            legTchn.setFmostrarView1(false);
            legTchn.setFmostrarView2(false);
            legTchn.setFmostrarView3(false);  
            legTchn.setFnuevo(false);
            legTchn.setFmostrar(false);
            legTchn.setFmostrarEt2(false);
        } catch (Exception e) {
                 
        }
       
    }
    
    public void cerrarPantallaEtapaEt2(String CFondoId,String lsCInversion,String lsidetapa){
 
        try {
             
            LegalTchn legalTchnNew=new LegalTchn();
            legalTchnNew.setFondo(legTchn.getFondo());
            legalTchnNew.setMaeInversion(legTchn.getMaeInversion());
 
            legalTchnList = legalServ.listarTchnLegal(legalTchnNew);

            legTchn.setFmostrarView1(false);
            legTchn.setFmostrarView2(false);
            legTchn.setFmostrarView3(false);  
            legTchn.setFnuevo(false);
            legTchn.setFmostrar(false);
            legTchn.setFmostrarEt2(false);
        } catch (Exception e) {
                 
        }
       
    }
    
    public void cerrarPantallaEtapaEt3(String CFondoId,String lsCInversion,String lsidetapa){
 
        try {
             
            LegalTchn legalTchnNew=new LegalTchn();
            legalTchnNew.setFondo(legTchn.getFondo());
            legalTchnNew.setMaeInversion(legTchn.getMaeInversion());
 
            legalTchnList = legalServ.listarTchnLegal(legalTchnNew);

            legTchn.setFmostrarView1(false);
            legTchn.setFmostrarView2(false);
            legTchn.setFmostrarView3(false);              
            legTchn.setFnuevo(false);
            legTchn.setFmostrar(false);
            legTchn.setFmostrarEt2(false);
            legTchn.setFmostrarEt3(false);
        } catch (Exception e) {
                 
        }
       
    }
    
    public void buscarTchnLegalAses() {
        try {
            
            //System.out.println("pop.webcobranzas.bean.EstadoCuentaBean.buscarTchn()");
            maeUbigeo.setCUbigeoId(maeUbigeoD.getCUbigeoId());
            maeUbigeo.setCUbigeoPad(maeUbigeoP.getCUbigeoId());
            legSeguiTchn.setFmostrar(false);    
            legSeguiTchn.setFmostrarEje(false);    
            legTchn.setFmostrar(false);
            legTchn.setFmostrarEt2(false);
            legTchn.setFmostrarEt3(false);
            //System.out.println("Ubigeo -> Distrito " + maeUbigeoD.getCUbigeoId());
            //System.out.println("Ubigeo -> Provincia " + maeUbigeoP.getCUbigeoId());
            maeInmueble.setMaeUbigeo(maeUbigeo);
            legalTchnList = legalServ.listarTchnLegalAsesor(legTchn);
     
            
            
        } catch (Exception e) {

        }
    }
    
    
    public void SumarDemanda(LegalTchn olegTchn){
        try {
                
            olegTchn.setLnTotal(olegTchn.getLnCapital()+olegTchn.getLnInteres()+ olegTchn.getLnMora());
        } catch (Exception e) {
        }
 
    
    }
      
      

    public void iniciarFecha() {
        try {
            //System.out.println("pop.webcobranzas.bean.EstadoCuentaBean.iniciarFecha()");
            if (getMaeEstadoCuenta().getfIniBusq() == null) {
                getMaeEstadoCuenta().setfIniBusq(new Date());
            }
        } catch (Exception e) {

        }
    }
 
 public void obtenerPeriodo(){
        try {
            maeMesList  = legalServ.listar_mes();
            maeAnioList = legalServ.listar_anio();
        } catch (Exception e) {

        }
 }
    
 public void generarDashboard(){
 
        try {
    
             System.out.println("["+SessionUtils.getUserName()+"] "+" DashboardBean - cargarGrafico ");
            // consolidados codigos judiciales - activos
            //activos
            legTchn.setFmostrar(false);
            String LsUsuario =SessionUtils.getUserName();
            List<List<Object>> list = legalServ.cargarDashboardActivo(LsUsuario) ;
            //System.out.println("pop.webcobranzas.bean.DashboardBean.cargarGrafico()");
            dataA=list.get(1).toString();
            dataB=list.get(2).get(0).toString();
            //dataC=list.get(3).toString();
            //dataD=list.get(4).toString();
            data0=list.get(0).get(0).toString();
          
            iTot=Integer.parseInt(data0);
            iCali=Integer.parseInt(dataB);
            iJudi=iTot-iCali;
            
            dataC="[['en Judiciales'," + iJudi + "],['en Calificacion'," + iCali + "]]";
            // consolidados codigos judiciales - cancelados
            List<List<Object>> list1 = legalServ.cargarDashboardCancelado(LsUsuario) ;
            dataA1=list1.get(1).toString();
            dataB=list1.get(2).get(0).toString();
            data0=list1.get(0).get(0).toString();
            iTOTC=Integer.parseInt(data0);
            iCC=Integer.parseInt(dataB);
            iPC=iTOTC-iCC;
            
            dataD="[['Por Concluir'," + iPC + "],['Concluidos'," + iCC + "]]";
            
            // CONSOLIDADO POR FONDOS - por etapas
            legalTchnList = legalServ.lista_Dashboard_etapaxfondo();
            
            
            List<List<Object>> list2 = legalServ.cargarDashboardEtapaxFondo() ;
            calFCE=Integer.parseInt(list2.get(0).get(0).toString());
            calMYP=Integer.parseInt(list2.get(1).get(0).toString());
            calPOP=Integer.parseInt(list2.get(2).get(0).toString());
            calFPH=Integer.parseInt(list2.get(3).get(0).toString());
            
            
            posFCE=Integer.parseInt(list2.get(0).get(1).toString());
            posMYP=Integer.parseInt(list2.get(1).get(1).toString());
            posPOP=Integer.parseInt(list2.get(2).get(1).toString());
            posFPH=Integer.parseInt(list2.get(3).get(1).toString());
            
            
            
            proFCE=Integer.parseInt(list2.get(0).get(2).toString());
            proMYP=Integer.parseInt(list2.get(1).get(2).toString());
            proPOP=Integer.parseInt(list2.get(2).get(2).toString());
            proFPH=Integer.parseInt(list2.get(3).get(2).toString());


            
            desiFCE=Integer.parseInt(list2.get(0).get(3).toString());
            desiMYP=Integer.parseInt(list2.get(1).get(3).toString());
            desiPOP=Integer.parseInt(list2.get(2).get(3).toString());
            desiFPH=Integer.parseInt(list2.get(3).get(3).toString());            
            
            
            impFCE=Integer.parseInt(list2.get(0).get(4).toString());
            impMYP=Integer.parseInt(list2.get(1).get(4).toString());
            impPOP=Integer.parseInt(list2.get(2).get(4).toString());
            impFPH=Integer.parseInt(list2.get(3).get(4).toString());            
            
            ejeFCE=Integer.parseInt(list2.get(0).get(5).toString());
            ejeMYP=Integer.parseInt(list2.get(1).get(5).toString());
            ejePOP=Integer.parseInt(list2.get(2).get(5).toString());
            ejeFPH=Integer.parseInt(list2.get(3).get(5).toString());            
            
            finFCE=Integer.parseInt(list2.get(0).get(6).toString());
            finMYP=Integer.parseInt(list2.get(1).get(6).toString());
            finPOP=Integer.parseInt(list2.get(2).get(6).toString());
            finFPH=Integer.parseInt(list2.get(3).get(6).toString());           
            
            
            data1A=list2.get(0).toString();
            data1B=list2.get(1).toString();
            data1C=list2.get(2).toString();
            data1D=list2.get(3).toString();
            data10=list2.get(4).toString();
            
            // DETALLE POR ETAPAS - Por desempeño del Proceso
            legalTchnList2 = legalServ.lista_Dashboard_etapaDesem();
            
            List<List<Object>> list3 = legalServ.cargarDashboardEtapaxDesem() ;
            data2A=list3.get(0).toString();
            data2B=list3.get(1).toString();
            data2C=list3.get(2).toString();
            data2D=list3.get(3).toString();
            data20=list3.get(4).toString();
            
            
            calOpt=Integer.parseInt(list3.get(0).get(0).toString());
            calNor=Integer.parseInt(list3.get(1).get(0).toString());
            calReg=Integer.parseInt(list3.get(2).get(0).toString());
            calDef=Integer.parseInt(list3.get(3).get(0).toString());
            
            
            posOpt=Integer.parseInt(list3.get(0).get(1).toString());
            posNor=Integer.parseInt(list3.get(1).get(1).toString());
            posReg=Integer.parseInt(list3.get(2).get(1).toString());
            posDef=Integer.parseInt(list3.get(3).get(1).toString());
            
            
            
            proOpt=Integer.parseInt(list3.get(0).get(2).toString());
            proNor=Integer.parseInt(list3.get(1).get(2).toString());
            proReg=Integer.parseInt(list3.get(2).get(2).toString());
            proDef=Integer.parseInt(list3.get(3).get(2).toString());


            
            desiOpt=Integer.parseInt(list3.get(0).get(3).toString());
            desiNor=Integer.parseInt(list3.get(1).get(3).toString());
            desiReg=Integer.parseInt(list3.get(2).get(3).toString());
            desiDef=Integer.parseInt(list3.get(3).get(3).toString());            
            
            
            impOpt=Integer.parseInt(list3.get(0).get(4).toString());
            impNor=Integer.parseInt(list3.get(1).get(4).toString());
            impReg=Integer.parseInt(list3.get(2).get(4).toString());
            impDef=Integer.parseInt(list3.get(3).get(4).toString());            
            
            ejeOpt=Integer.parseInt(list3.get(0).get(5).toString());
            ejeNor=Integer.parseInt(list3.get(1).get(5).toString());
            ejeReg=Integer.parseInt(list3.get(2).get(5).toString());
            ejeDef=Integer.parseInt(list3.get(3).get(5).toString());            
            
            finOpt=Integer.parseInt(list3.get(0).get(6).toString());
            finNor=Integer.parseInt(list3.get(1).get(6).toString());
            finReg=Integer.parseInt(list3.get(2).get(6).toString());
            finDef=Integer.parseInt(list3.get(3).get(6).toString()); 
            
            
            //DETALLE POR ETAPAS - II.2 Por Responsable
            legalTchnList3 = legalServ.Lista_Dashboard_AseCal();
            lsAsesor1=legalTchnList3.get(0).getLsAsesor();
            lsAsesor2=legalTchnList3.get(1).getLsAsesor();
            lsAsesor3=legalTchnList3.get(2).getLsAsesor();
            lsAsesor4=legalTchnList3.get(3).getLsAsesor();
            
            
            calAse1=legalTchnList3.get(0).getiCalifi();
            posAse1=legalTchnList3.get(0).getiPostula();
            proAse1=legalTchnList3.get(0).getiProba();
            desiAse1=legalTchnList3.get(0).getiDesiso();
            impAse1=legalTchnList3.get(0).getiImpug();
            ejeAse1=legalTchnList3.get(0).getiEjecu();
            finAse1=legalTchnList3.get(0).getiFinal1();
            
            calAse2=legalTchnList3.get(1).getiCalifi();
            posAse2=legalTchnList3.get(1).getiPostula();
            proAse2=legalTchnList3.get(1).getiProba();
            desiAse2=legalTchnList3.get(1).getiDesiso();
            impAse2=legalTchnList3.get(1).getiImpug();
            ejeAse2=legalTchnList3.get(1).getiEjecu();
            finAse2=legalTchnList3.get(1).getiFinal1();
            
            
            calAse3=legalTchnList3.get(2).getiCalifi();
            posAse3=legalTchnList3.get(2).getiPostula();
            proAse3=legalTchnList3.get(2).getiProba();
            desiAse3=legalTchnList3.get(2).getiDesiso();
            impAse3=legalTchnList3.get(2).getiImpug();
            ejeAse3=legalTchnList3.get(2).getiEjecu();
            finAse3=legalTchnList3.get(2).getiFinal1();
            
            
            
            calAse4=legalTchnList3.get(3).getiCalifi();
            posAse4=legalTchnList3.get(3).getiPostula();
            proAse4=legalTchnList3.get(3).getiProba();
            desiAse4=legalTchnList3.get(3).getiDesiso();
            impAse4=legalTchnList3.get(3).getiImpug();
            ejeAse4=legalTchnList3.get(3).getiEjecu();
            finAse4=legalTchnList3.get(3).getiFinal1();
                    
            //III. DETALLE POR DESEMPEÑO DE PROCESOS - III.1 Por Fondo
            legalTchnList4 = legalServ.lista_Dashboard_fondoDese();
            List<List<Object>> list4 = legalServ.cargarDashboardFondoxIndi() ;
            data3A=list4.get(0).toString();
            data3B=list4.get(1).toString();
            data3C=list4.get(2).toString();
            data3D=list4.get(3).toString();
            data30=list4.get(4).toString();
            
            
            FCEOptx=legalTchnList4.get(0).getiOptimo() ;
            FCENorx=legalTchnList4.get(0).getiNormal();
            FCERegx=legalTchnList4.get(0).getiRegular();
            FCEDefx=legalTchnList4.get(0).getiDeficiente();
            POPOptx=legalTchnList4.get(1).getiOptimo();
            POPNorx=legalTchnList4.get(1).getiNormal();
            POPRegx=legalTchnList4.get(1).getiRegular();
            POPDefx=legalTchnList4.get(1).getiDeficiente();
            MYPOptx=legalTchnList4.get(2).getiOptimo();
            MYPNorx=legalTchnList4.get(2).getiNormal();
            MYPRegx=legalTchnList4.get(2).getiRegular();
            MYPDefx=legalTchnList4.get(2).getiDeficiente();
            PHOptx= legalTchnList4.get(3).getiOptimo();
            PHNorx=legalTchnList4.get(3).getiNormal();
            PHRegx=legalTchnList4.get(3).getiRegular();
            PHDefx=legalTchnList4.get(3).getiDeficiente();
            DFCEOptx= ((double)legalTchnList4.get(0).getiOptimo()) /((double) (FCEOptx+FCENorx+FCERegx+FCEDefx));
            DFCENorx= ((double)legalTchnList4.get(0).getiNormal())/((double) (FCEOptx+FCENorx+FCERegx+FCEDefx));
            DFCERegx= ((double)legalTchnList4.get(0).getiRegular())/((double) (FCEOptx+FCENorx+FCERegx+FCEDefx));
            DFCEDefx= ((double)legalTchnList4.get(0).getiDeficiente())/((double) (FCEOptx+FCENorx+FCERegx+FCEDefx));
            DPOPOptx= ((double)legalTchnList4.get(1).getiOptimo())/((double) (POPOptx+POPNorx+POPRegx+POPDefx));
            DPOPNorx= ((double)legalTchnList4.get(1).getiRegular())/((double) (POPOptx+POPNorx+POPRegx+POPDefx));
            DPOPDefx= ((double)legalTchnList4.get(1).getiDeficiente())/((double) (POPOptx+POPNorx+POPRegx+POPDefx));
            DMYPOptx= ((double)legalTchnList4.get(2).getiOptimo())/((double) (MYPOptx+MYPNorx+MYPRegx+MYPDefx));
            DMYPNorx= ((double)legalTchnList4.get(2).getiNormal())/((double) (MYPOptx+MYPNorx+MYPRegx+MYPDefx));
            DMYPRegx= ((double)legalTchnList4.get(2).getiRegular())/((double) (MYPOptx+MYPNorx+MYPRegx+MYPDefx));
            DMYPDefx= ((double)legalTchnList4.get(2).getiDeficiente())/((double) (MYPOptx+MYPNorx+MYPRegx+MYPDefx));
            DPHOptx= ((double)legalTchnList4.get(3).getiOptimo())/((double) (PHOptx+PHNorx+PHRegx+PHDefx));
            DPHNorx= ((double)legalTchnList4.get(3).getiNormal())/((double) (PHOptx+PHNorx+PHRegx+PHDefx));
            DPHRegx= ((double)legalTchnList4.get(3).getiRegular())/((double) (PHOptx+PHNorx+PHRegx+PHDefx));
            DPHDefx= ((double)legalTchnList4.get(3).getiDeficiente())/((double) (PHOptx+PHNorx+PHRegx+PHDefx));

            FCEOpt=Integer.parseInt(list4.get(0).get(0).toString());
            FCENor=Integer.parseInt(list4.get(1).get(0).toString());
            FCEReg=Integer.parseInt(list4.get(2).get(0).toString());
            FCEDef=Integer.parseInt(list4.get(3).get(0).toString());
            POPOpt=Integer.parseInt(list4.get(0).get(1).toString());
            POPNor=Integer.parseInt(list4.get(1).get(1).toString());
            POPReg=Integer.parseInt(list4.get(2).get(1).toString());
            POPDef=Integer.parseInt(list4.get(3).get(1).toString());
            MYPOpt=Integer.parseInt(list4.get(0).get(2).toString());
            MYPNor=Integer.parseInt(list4.get(1).get(2).toString());
            MYPReg=Integer.parseInt(list4.get(2).get(2).toString());
            MYPDef=Integer.parseInt(list4.get(3).get(2).toString());
            PHOpt= Integer.parseInt(list4.get(0).get(3).toString());
            PHNor= Integer.parseInt(list4.get(1).get(3).toString());
            PHReg= Integer.parseInt(list4.get(2).get(3).toString());
            PHDef= Integer.parseInt(list4.get(3).get(3).toString());
            
            //III.2 Por responsable
            legalTchnList5 = legalServ.lista_Dashboard_x_asesor();
            lsAse1=legalTchnList5.get(0).getLsAsesor();
            optAse1=legalTchnList5.get(0).getiOptimo();
            norAse1=legalTchnList5.get(0).getiNormal();
            regAse1=legalTchnList5.get(0).getiRegular();
            defAse1=legalTchnList5.get(0).getiDeficiente();
            
            lsAse2=legalTchnList5.get(1).getLsAsesor();
            optAse2=legalTchnList5.get(1).getiOptimo();
            norAse2=legalTchnList5.get(1).getiNormal();
            regAse2=legalTchnList5.get(1).getiRegular();
            defAse2=legalTchnList5.get(1).getiDeficiente();
            
            lsAse3=legalTchnList5.get(2).getLsAsesor();
            optAse3=legalTchnList5.get(2).getiOptimo();
            norAse3=legalTchnList5.get(2).getiNormal();
            regAse3=legalTchnList5.get(2).getiRegular();
            defAse3=legalTchnList5.get(2).getiDeficiente();
            
            lsAse4=legalTchnList5.get(3).getLsAsesor();
            optAse4=legalTchnList5.get(3).getiOptimo();
            norAse4=legalTchnList5.get(3).getiNormal();
            regAse4=legalTchnList5.get(3).getiRegular();
            defAse4=legalTchnList5.get(3).getiDeficiente();
  

            
            List<List<Object>> list5 = legalServ.cargarDashboardAsesorxIndi() ;
            data4A=list5.get(0).toString();
            data4B=list5.get(1).toString();
            data4C=list5.get(2).toString();
            data4D=list5.get(3).toString();
            data40=list5.get(4).toString();
            data4E="["+data4A+ "," + data4B + "," + data4C + "," + data4D+"]";
            
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("MM");
            DateFormat dateFormat1 = new SimpleDateFormat("YYYY");
           if (legalTchnList6==null && legalTchnList12==null ){
                String sMes  = dateFormat.format(date).substring(0,2);
                String sAnio = dateFormat1.format(date).substring(0,4);
                
                 legalTchnList6 = legalServ.listar_LegalImpxfondo(sMes,sAnio);
                legalTchnList7 = legalServ.lista_TopVisiCap(sMes,sAnio);
                legalTchnList8 = legalServ.lista_TopVisiMyp(sMes,sAnio);
                legalTchnList9 = legalServ.lista_TopVisiPop(sMes,sAnio);
                legalTchnList10 = legalServ.lista_TopVisiPrh(sMes,sAnio);
                 List<List<Object>> listImpxFondo   = legalServ.cargarDashboard_Impxfondo();
                 legalTchnList11 = legalServ.Lista_ImpTopneg(sMes,sAnio);
                  data5A=listImpxFondo.get(0).toString();
                  data5B=listImpxFondo.get(1).toString();
                  data5C=listImpxFondo.get(2).toString();
                  data5D=listImpxFondo.get(3).toString();
                  data50=listImpxFondo.get(5).toString();
                  Ase1=listImpxFondo.get(4).get(0).toString();
                  Ase2=listImpxFondo.get(4).get(1).toString();
                  Ase3=listImpxFondo.get(4).get(2).toString();
                  Ase4=listImpxFondo.get(4).get(3).toString();
                  Ase5=listImpxFondo.get(4).get(4).toString();
                  Ase6=listImpxFondo.get(4).get(5).toString();
                  

                 if (listImpxFondo.get(0).size()>0 ) {
                    Ase1Cap= listImpxFondo.get(0).get(0).toString() ;
                    Ase2Cap= listImpxFondo.get(0).get(1).toString() ;
                    Ase3Cap= listImpxFondo.get(0).get(2).toString() ;
                    Ase4Cap= listImpxFondo.get(0).get(3).toString() ;
                    Ase5Cap= listImpxFondo.get(0).get(4).toString() ;
                    Ase6Cap= listImpxFondo.get(0).get(5).toString() ;
                 }

                 if (listImpxFondo.get(1).size()>0 ) {
                    Ase1Pop= listImpxFondo.get(1).get(0).toString() ;
                    Ase2Pop= listImpxFondo.get(1).get(1).toString() ;
                    Ase3Pop= listImpxFondo.get(1).get(2).toString() ;
                    Ase4Pop= listImpxFondo.get(1).get(3).toString() ;
                    Ase5Pop= listImpxFondo.get(1).get(4).toString() ;
                    Ase6Pop= listImpxFondo.get(1).get(5).toString() ;
                 }

                 if (listImpxFondo.get(2).size()>0 ) {
                    Ase1Myp= listImpxFondo.get(2).get(0).toString() ;
                    Ase2Myp= listImpxFondo.get(2).get(1).toString() ;
                    Ase3Myp= listImpxFondo.get(2).get(2).toString() ;
                    Ase4Myp= listImpxFondo.get(2).get(3).toString() ;
                    Ase5Myp= listImpxFondo.get(2).get(4).toString() ;
                    Ase6Myp= listImpxFondo.get(2).get(5).toString() ;
                 }

                if (listImpxFondo.get(3).size()>0 ) {
                    Ase1Prh= listImpxFondo.get(3).get(0).toString() ;
                    Ase2Prh= listImpxFondo.get(3).get(1).toString() ;
                    Ase3Prh= listImpxFondo.get(3).get(2).toString() ;
                    Ase4Prh= listImpxFondo.get(3).get(3).toString() ;
                    Ase5Prh= listImpxFondo.get(3).get(4).toString() ;
                    Ase6Prh= listImpxFondo.get(3).get(5).toString() ;
                 }


                legalTchnList12 = legalServ.Lista_xAseEtapa(sMes,sAnio);
                legalTchnList13 = legalServ.Lista_xAseIndicador(sMes,sAnio);

                List<List<Object>> list6 = legalServ.Dashboard_xAseIndicador() ;
                data7A=list6.get(0).toString();
                data7B=list6.get(1).toString();
                data7C=list6.get(2).toString();
                data7D=list6.get(3).toString();
                data70=list6.get(4).toString();
                
           } 
            
        } catch (Exception e) {
                 
        }
       
    }
          
    public void exportaDashboard(){
        
       obtenerParam();
      System.out.println("Total de Registros "  + cobTablasEstInvList.size());
      int  Param1 =Integer.parseInt(cobTablasEstInvList.get(0).getDdescCorta());
      int  Param2 =Integer.parseInt(cobTablasEstInvList.get(1).getDdescCorta());
      int  Param3 =Integer.parseInt(cobTablasEstInvList.get(2).getDdescCorta());
      int  Param4 =Integer.parseInt(cobTablasEstInvList.get(3).getDdescCorta());
     
      
        if (legalTchnList6!=null && legalTchnList12!=null ){
           // String ruta_conf=properties.getRuta_conf();
            String ruta_conf="C:\\TEMP\\";
            
            String nombreArchivo = "Dashboard.xlsx";
            String nombreArchivo1 = "Dashboard_1.xlsx";
            String rutaArchivo = ruta_conf+ nombreArchivo;
            String rutaArchivo1 = ruta_conf+ nombreArchivo1;
            File archivo = new File(ruta_conf+nombreArchivo1);
            int inn1,inn2,inn3,inn4,inn5;
            int inn6,inn7,inn8,inn9,inn10;
            int inn11,inn12,inn13,inn14,inn15;
            int inn16,inn17,inn18,inn19,inn20;
            int inn21,inn22,inn23,inn29;
            int totReg;
            int newreg=0;
            ArrayList<Integer> aRowdel = new ArrayList();
            int xlin=legalTchnList6.size();
            int xlin1=legalTchnList7.size();
            int xlin2=legalTchnList8.size();
            int xlin3=legalTchnList9.size();
            int xlin4=legalTchnList10.size();
            int xlin7=legalTchnList11.size();
            int xlin5=0;
            if (legalTchnList12!=null){
                xlin5=legalTchnList12.size();
            }
            int xlin6=legalTchnList13.size();
            int xlin8=legalTchnList3.size();
            int xlin9=legalTchnList5.size();
            int ll_aux=0;
            int ll_aux1=0;
            int ll_aux2=0;
            int ll_aux3=0;
            int ll_aux4=0;
            int ll_aux5=0;
            int ll_aux6=0;
            int ll_aux7=0;
            int ll_aux8=0;
            int ll_aux9=0;
            OutputStream excelNewOutputStream = null;

            try (FileInputStream file = new FileInputStream(new File(rutaArchivo))) {

                XSSFWorkbook worbook = new XSSFWorkbook(file);
                //obtener la hoja que se va leer
                XSSFSheet sheet = worbook.getSheetAt(0);
                //obtener todas las filas de la hoja excel
                Iterator<Row> rowIterator = sheet.iterator();
                totReg=sheet.getLastRowNum();
                Row row;
                // se recorre cada fila hasta el final
                while (rowIterator.hasNext()) {
                        newreg ++;
                        row = rowIterator.next();
                               
                    if (newreg==8){
                          //I.1 Activos
                          
                           
                           if (row.getCell(2) != null){
                             inn2= row.getCell(2).getCellType();
                                if (inn2==0){
                                    row.getCell(2).setCellValue(iJudi);
                                }
                           }
                           
                           
                           if (row.getCell(5) != null){
                             inn5= row.getCell(5).getCellType();
                              if (inn5==0){
                                    row.getCell(5).setCellValue(iCali);
                                }
                           }
                           
                            if (row.getCell(8) != null){
                             inn8= row.getCell(8).getCellType();
                              if (inn8==0){
                                    row.getCell(8).setCellValue(iJudi+iCali);
                                }
                           }
                            
                        
                    }  
                    
                     if (newreg==12){
                          //II.1 Por etapas
                           //Calificación
                           if (row.getCell(19) != null){
                             inn19= row.getCell(19).getCellType();
                                if (inn19==0){
                                    row.getCell(19).setCellValue(calFCE);
                                }
                           }
                           
                           
                           if (row.getCell(20) != null){
                             inn20= row.getCell(20).getCellType();
                              if (inn20==0){
                                    row.getCell(20).setCellValue(calMYP);
                                }
                           }
                           
                           
                           if (row.getCell(21) != null){
                             inn21= row.getCell(21).getCellType();
                              if (inn21==0){
                                    row.getCell(21).setCellValue(calPOP);
                                }
                           }
                           
                           
                           if (row.getCell(22) != null){
                             inn22= row.getCell(22).getCellType();
                              if (inn22==0){
                                    row.getCell(22).setCellValue(calFPH);
                                }
                           }
                           
                           if (row.getCell(23) != null){
                             inn23= row.getCell(23).getCellType();
                              if (inn23==0){
                                    row.getCell(23).setCellValue(calFCE+calMYP+calPOP+calFPH);
                                }
                           }
                         
                    } 
                     
                     if (newreg==13){
                          //II.1 Por etapas
                          //Postulatoria
                           if (row.getCell(19) != null){
                             inn19= row.getCell(19).getCellType();
                                if (inn19==0){
                                    row.getCell(19).setCellValue(posFCE);
                                }
                           }
                           
                           
                           if (row.getCell(20) != null){
                             inn20= row.getCell(20).getCellType();
                              if (inn20==0){
                                    row.getCell(20).setCellValue(posMYP);
                                }
                           }
                           
                           
                           if (row.getCell(21) != null){
                             inn21= row.getCell(21).getCellType();
                              if (inn21==0){
                                    row.getCell(21).setCellValue(posPOP);
                                }
                           }
                           
                           
                           if (row.getCell(22) != null){
                             inn22= row.getCell(22).getCellType();
                              if (inn22==0){
                                    row.getCell(22).setCellValue(posFPH);
                                }
                           }
                         
                           if (row.getCell(23) != null){
                             inn23= row.getCell(23).getCellType();
                              if (inn23==0){
                                    row.getCell(23).setCellValue(posFCE+posMYP+posPOP+posFPH);
                                }
                           }
                            
                    } 
                     
                     if (newreg==14){
                          //II.1 Por etapas
                          //Probatoria
                           if (row.getCell(19) != null){
                             inn19= row.getCell(19).getCellType();
                                if (inn19==0){
                                    row.getCell(19).setCellValue(proFCE);
                                }
                           }
                           
                           
                           if (row.getCell(20) != null){
                             inn20= row.getCell(20).getCellType();
                              if (inn20==0){
                                    row.getCell(20).setCellValue(proMYP);
                                }
                           }
                           
                           
                           if (row.getCell(21) != null){
                             inn21= row.getCell(21).getCellType();
                              if (inn21==0){
                                    row.getCell(21).setCellValue(proPOP);
                                }
                           }
                           
                           
                           if (row.getCell(22) != null){
                             inn22= row.getCell(22).getCellType();
                              if (inn22==0){
                                    row.getCell(22).setCellValue(proFPH);
                                }
                           }
                           
                           if (row.getCell(23) != null){
                             inn23= row.getCell(23).getCellType();
                              if (inn23==0){
                                    row.getCell(23).setCellValue(proFCE+proMYP+proPOP+proFPH);
                                }
                           }
                    }
                     
                     
                      if (newreg==15){
                          //II.1 Por etapas
                          //decisoria
                           if (row.getCell(19) != null){
                             inn19= row.getCell(19).getCellType();
                                if (inn19==0){
                                    row.getCell(19).setCellValue(desiFCE);
                                }
                           }
                           
                           
                           if (row.getCell(20) != null){
                             inn20= row.getCell(20).getCellType();
                              if (inn20==0){
                                    row.getCell(20).setCellValue(desiMYP);
                                }
                           }
                           
                           
                           if (row.getCell(21) != null){
                             inn21= row.getCell(21).getCellType();
                              if (inn21==0){
                                    row.getCell(21).setCellValue(desiPOP);
                                }
                           }
                           
                           
                           if (row.getCell(22) != null){
                             inn22= row.getCell(22).getCellType();
                              if (inn22==0){
                                    row.getCell(22).setCellValue(desiFPH);
                                }
                           }
                           
                           if (row.getCell(23) != null){
                             inn23= row.getCell(23).getCellType();
                              if (inn23==0){
                                    row.getCell(23).setCellValue(desiFCE+desiMYP+desiPOP+desiFPH);
                                }
                           }
                         
                    }
                      
                       if (newreg==16){
                          //II.1 Por etapas
                          //inpuganatoria
                           if (row.getCell(19) != null){
                             inn19= row.getCell(19).getCellType();
                                if (inn19==0){
                                    row.getCell(19).setCellValue(impFCE);
                                }
                           }
                           
                           
                           if (row.getCell(20) != null){
                             inn20= row.getCell(20).getCellType();
                              if (inn20==0){
                                    row.getCell(20).setCellValue(impMYP);
                                }
                           }
                           
                           
                           if (row.getCell(21) != null){
                             inn21= row.getCell(21).getCellType();
                              if (inn21==0){
                                    row.getCell(21).setCellValue(impPOP);
                                }
                           }
                           
                           
                           if (row.getCell(22) != null){
                             inn22= row.getCell(22).getCellType();
                              if (inn22==0){
                                    row.getCell(22).setCellValue(impFPH);
                                }
                           }
                           
                            if (row.getCell(23) != null){
                             inn23= row.getCell(23).getCellType();
                              if (inn23==0){
                                    row.getCell(23).setCellValue(impFCE+impMYP+impPOP+impFPH);
                                }
                           }
                         
                    }
                       
                      if (newreg==17){
                          //II.1 Por etapas
                          //ejecucion
                           if (row.getCell(19) != null){
                             inn19= row.getCell(19).getCellType();
                                if (inn19==0){
                                    row.getCell(19).setCellValue(ejeFCE);
                                }
                           }
                           
                           
                           if (row.getCell(20) != null){
                             inn20= row.getCell(20).getCellType();
                              if (inn20==0){
                                    row.getCell(20).setCellValue(ejeMYP);
                                }
                           }
                           
                           
                           if (row.getCell(21) != null){
                             inn21= row.getCell(21).getCellType();
                              if (inn21==0){
                                    row.getCell(21).setCellValue(ejePOP);
                                }
                           }
                           
                           
                           if (row.getCell(22) != null){
                             inn22= row.getCell(22).getCellType();
                              if (inn22==0){
                                    row.getCell(22).setCellValue(ejeFPH);
                                }
                           }
                           
                           if (row.getCell(23) != null){
                             inn23= row.getCell(23).getCellType();
                              if (inn23==0){
                                    row.getCell(23).setCellValue(ejeFCE+ejeMYP+ejePOP+ejeFPH);
                                }
                           }
                         
                    }
                    
                    if (newreg==18){
                        
                           //I.2 Cancelados
                           if (row.getCell(2) != null){
                             inn2= row.getCell(2).getCellType();
                                if (inn2==0){
                                    row.getCell(2).setCellValue(iPC);
                                }
                           }
                           
                           
                           if (row.getCell(5) != null){
                             inn5= row.getCell(5).getCellType();
                              if (inn5==0){
                                    row.getCell(5).setCellValue(iCC);
                                }
                           }
                           
                           
                          if (row.getCell(8) != null){
                             inn8= row.getCell(8).getCellType();
                              if (inn8==0){
                                    row.getCell(8).setCellValue(iPC+iCC);
                                }
                           }
                           
                          //II.1 Por etapas
                          //final
                           if (row.getCell(19) != null){
                             inn19= row.getCell(19).getCellType();
                                if (inn19==0){
                                    row.getCell(19).setCellValue(finFCE);
                                }
                           }
                           
                           
                           if (row.getCell(20) != null){
                             inn20= row.getCell(20).getCellType();
                              if (inn20==0){
                                    row.getCell(20).setCellValue(finMYP);
                                }
                           }
                           
                           
                           if (row.getCell(21) != null){
                             inn21= row.getCell(21).getCellType();
                              if (inn21==0){
                                    row.getCell(21).setCellValue(finPOP);
                                }
                           }
                           
                           
                           if (row.getCell(22) != null){
                             inn22= row.getCell(22).getCellType();
                              if (inn22==0){
                                    row.getCell(22).setCellValue(finFPH);
                                }
                           }
                           
                           if (row.getCell(23) != null){
                             inn23= row.getCell(23).getCellType();
                              if (inn23==0){
                                    row.getCell(23).setCellValue(finFCE+finMYP+finPOP+finFPH);
                                }
                           }
                         
                    }
                     
                    if (newreg==19){
                         
                          //II.1 Por etapas
                          //final
                           if (row.getCell(19) != null){
                             inn19= row.getCell(19).getCellType();
                                if (inn19==0){
                                    row.getCell(19).setCellValue(calFCE+posFCE+proFCE+desiFCE+impFCE+ejeFCE+finFCE);
                                }
                           }
                           
                           
                           if (row.getCell(20) != null){
                             inn20= row.getCell(20).getCellType();
                              if (inn20==0){
                                    row.getCell(20).setCellValue(calMYP+posMYP+proMYP+desiMYP+impMYP+ejeMYP+finMYP);
                                }
                           }
                           
                           
                           if (row.getCell(21) != null){
                             inn21= row.getCell(21).getCellType();
                              if (inn21==0){
                                    row.getCell(21).setCellValue(calPOP+posPOP+proPOP+desiPOP+impPOP+ejePOP+finPOP);
                                }
                           }
                           
                           
                           if (row.getCell(22) != null){
                             inn22= row.getCell(22).getCellType();
                              if (inn22==0){
                                    row.getCell(22).setCellValue(calFPH+posFPH+proFPH+desiFPH+impFPH+ejeFPH+finFPH);
                                }
                           }
                           
                           if (row.getCell(23) != null){
                             inn23= row.getCell(23).getCellType();
                              if (inn23==0){
                                    row.getCell(23).setCellValue(calFCE+calMYP+calPOP+calFPH+posFCE+posMYP+posPOP+posFPH+proFCE+proMYP+proPOP+proFPH+desiFCE+desiMYP+desiPOP+desiFPH+impFCE+impMYP+impPOP+impFPH+ejeFCE+ejeMYP+ejePOP+ejeFPH+finFCE+finMYP+finPOP+finFPH);
                                }
                           }
                         
                    }
                    
                    
                    if (newreg==33){ // calif
                         
                          //II.1 Por etapas
                          //final
                           if (row.getCell(3) != null){
                             inn3= row.getCell(3).getCellType();
                                if (inn3==0){
                                    row.getCell(3).setCellValue(calOpt);
                                }
                           }
                           
                           
                           if (row.getCell(4) != null){
                             inn4= row.getCell(4).getCellType();
                              if (inn4==0){
                                    row.getCell(4).setCellValue(calNor);
                                }
                           }
                           
                           
                           if (row.getCell(5) != null){
                             inn5= row.getCell(5).getCellType();
                              if (inn5==0){
                                    row.getCell(5).setCellValue(calReg);
                                }
                           }
                           
                           
                           if (row.getCell(6) != null){
                             inn6= row.getCell(6).getCellType();
                              if (inn6==0){
                                    row.getCell(6).setCellValue(calDef);
                                }
                           }
                           
                           if (row.getCell(7) != null){
                             inn7= row.getCell(7).getCellType();
                              if (inn7==0){
                                    row.getCell(7).setCellValue(calOpt+calNor+calReg+calDef);
                                }
                           }
 
                           
                           
                          ///III. DETALLE POR DESEMPEÑO DE PROCESOS
                         // III.1 Por Fondo
                          if (row.getCell(19) != null){
                             inn19= row.getCell(19).getCellType();
                                if (inn19==0){
                                    row.getCell(19).setCellValue(FCEOpt);
                                }
                           }
                           
                           
                           if (row.getCell(20) != null){
                             inn20= row.getCell(20).getCellType();
                              if (inn20==0){
                                    row.getCell(20).setCellValue(FCENor);
                                }
                           }
                           
                           
                           if (row.getCell(21) != null){
                             inn21= row.getCell(21).getCellType();
                              if (inn21==0){
                                    row.getCell(21).setCellValue(FCEReg);
                                }
                           }
                           
                           
                           if (row.getCell(22) != null){
                             inn22= row.getCell(22).getCellType();
                              if (inn22==0){
                                    row.getCell(22).setCellValue(FCEDef);
                                }
                           }
                           
                           if (row.getCell(23) != null){
                             inn23= row.getCell(23).getCellType();
                              if (inn23==0){
                                    row.getCell(23).setCellValue(FCEOpt+FCENor+FCEReg+FCEDef);
                                }
                           }
                           
                            if (row.getCell(26) != null){
                             inn23= row.getCell(26).getCellType();
                              if (inn23==0){
                                 row.getCell(26).setCellValue(DFCEOptx);
                                }
                           }
                            
                            if (row.getCell(27) != null){
                             inn23= row.getCell(27).getCellType();
                              if (inn23==0){
                                 row.getCell(27).setCellValue(DFCENorx);
                                }
                           }
                            
                           if (row.getCell(28) != null){
                             inn23= row.getCell(28).getCellType();
                              if (inn23==0){
                                 row.getCell(28).setCellValue(DFCERegx);
                                }
                           }
                           
                           if (row.getCell(29) != null){
                             inn23= row.getCell(29).getCellType();
                              if (inn23==0){
                                 row.getCell(29).setCellValue(DFCEDefx);
                                }
                           }
                           
                           if (row.getCell(30) != null){
                             inn23= row.getCell(30).getCellType();
                              if (inn23==0){
                                 row.getCell(30).setCellValue(DFCEOptx+DFCENorx+DFCERegx+DFCEDefx);
                                }
                           }
                           
                         
                    }
                    
                     if (newreg==34){ // postulatorio
                         
                          //II.1 Por etapas
                          //final
                           if (row.getCell(3) != null){
                             inn3= row.getCell(3).getCellType();
                                if (inn3==0){
                                    row.getCell(3).setCellValue(posOpt);
                                }
                           }
                           
                           
                           if (row.getCell(4) != null){
                             inn4= row.getCell(4).getCellType();
                              if (inn4==0){
                                    row.getCell(4).setCellValue(posNor);
                                }
                           }
                           
                           
                           if (row.getCell(5) != null){
                             inn5= row.getCell(5).getCellType();
                              if (inn5==0){
                                    row.getCell(5).setCellValue(posReg);
                                }
                           }
                           
                           
                           if (row.getCell(6) != null){
                             inn6= row.getCell(6).getCellType();
                              if (inn6==0){
                                    row.getCell(6).setCellValue(posDef);
                                }
                           }
                           
                           if (row.getCell(7) != null){
                             inn7= row.getCell(7).getCellType();
                              if (inn7==0){
                                    row.getCell(7).setCellValue(posOpt+posNor+posReg+posDef);
                                }
                           }
                           
                          
                           if (row.getCell(19) != null){
                             inn19= row.getCell(19).getCellType();
                                if (inn19==0){
                                    row.getCell(19).setCellValue(POPOpt);
                                }
                           }
                           
                           
                           if (row.getCell(20) != null){
                             inn20= row.getCell(20).getCellType();
                              if (inn20==0){
                                    row.getCell(20).setCellValue(POPNor);
                                }
                           }
                           
                           
                           if (row.getCell(21) != null){
                             inn21= row.getCell(21).getCellType();
                              if (inn21==0){
                                    row.getCell(21).setCellValue(POPReg);
                                }
                           }
                           
                           
                           if (row.getCell(22) != null){
                             inn22= row.getCell(22).getCellType();
                              if (inn22==0){
                                    row.getCell(22).setCellValue(POPDef);
                                }
                           }
                           
                           if (row.getCell(23) != null){
                             inn23= row.getCell(23).getCellType();
                              if (inn23==0){
                                    row.getCell(23).setCellValue(POPOpt+POPNor+POPReg+POPDef);
                                }
                           }
                           
                         
                            if (row.getCell(26) != null){
                             inn23= row.getCell(26).getCellType();
                              if (inn23==0){
                                 row.getCell(26).setCellValue(DPOPOptx);
                                }
                           }
                            
                            if (row.getCell(27) != null){
                             inn23= row.getCell(27).getCellType();
                              if (inn23==0){
                                 row.getCell(27).setCellValue(DPOPNorx);
                                }
                           }
                            
                           if (row.getCell(28) != null){
                             inn23= row.getCell(28).getCellType();
                              if (inn23==0){
                                 row.getCell(28).setCellValue(DPOPRegx);
                                }
                           }
                           
                           if (row.getCell(29) != null){
                             inn23= row.getCell(29).getCellType();
                              if (inn23==0){
                                 row.getCell(29).setCellValue(DPOPDefx);
                                }
                           }
                           
                           if (row.getCell(30) != null){
                             inn23= row.getCell(30).getCellType();
                              if (inn23==0){
                                 row.getCell(30).setCellValue(DPOPOptx+DPOPNorx+DPOPRegx+DPOPDefx);
                                }
                           }  
                           
                         
                           
                    }
                     
                     
                      if (newreg==35){ // probatorio
                         
                          //II.1 Por etapas
                          //final
                           if (row.getCell(3) != null){
                             inn3= row.getCell(3).getCellType();
                                if (inn3==0){
                                    row.getCell(3).setCellValue(proOpt);
                                }
                           }
                           
                           
                           if (row.getCell(4) != null){
                             inn4= row.getCell(4).getCellType();
                              if (inn4==0){
                                    row.getCell(4).setCellValue(proNor);
                                }
                           }
                           
                           
                           if (row.getCell(5) != null){
                             inn5= row.getCell(5).getCellType();
                              if (inn5==0){
                                    row.getCell(5).setCellValue(proReg);
                                }
                           }
                           
                           
                           if (row.getCell(6) != null){
                             inn6= row.getCell(6).getCellType();
                              if (inn6==0){
                                    row.getCell(6).setCellValue(proDef);
                                }
                           }
                           
                           if (row.getCell(7) != null){
                             inn7= row.getCell(7).getCellType();
                              if (inn7==0){
                                    row.getCell(7).setCellValue(proOpt+proNor+proReg+proDef);
                                }
                           }
                           
                           
                           
                           
                           if (row.getCell(19) != null){
                             inn19= row.getCell(19).getCellType();
                                if (inn19==0){
                                    row.getCell(19).setCellValue(MYPOpt);
                                }
                           }
                           
                           
                           if (row.getCell(20) != null){
                             inn20= row.getCell(20).getCellType();
                              if (inn20==0){
                                    row.getCell(20).setCellValue(MYPNor);
                                }
                           }
                           
                           
                           if (row.getCell(21) != null){
                             inn21= row.getCell(21).getCellType();
                              if (inn21==0){
                                    row.getCell(21).setCellValue(MYPReg);
                                }
                           }
                           
                           
                           if (row.getCell(22) != null){
                             inn22= row.getCell(22).getCellType();
                              if (inn22==0){
                                    row.getCell(22).setCellValue(MYPDef);
                                }
                           }
                           
                           if (row.getCell(23) != null){
                             inn23= row.getCell(23).getCellType();
                              if (inn23==0){
                                    row.getCell(23).setCellValue(MYPOpt+MYPNor+MYPReg+MYPDef);
                                }
                           }
                           
                           
                           if (row.getCell(26) != null){
                             inn23= row.getCell(26).getCellType();
                              if (inn23==0){
                                 row.getCell(26).setCellValue(DMYPOptx);
                                }
                           }
                            
                            if (row.getCell(27) != null){
                             inn23= row.getCell(27).getCellType();
                              if (inn23==0){
                                 row.getCell(27).setCellValue(DMYPNorx);
                                }
                           }
                            
                           if (row.getCell(28) != null){
                             inn23= row.getCell(28).getCellType();
                              if (inn23==0){
                                 row.getCell(28).setCellValue(DMYPRegx);
                                }
                           }
                           
                           if (row.getCell(29) != null){
                             inn23= row.getCell(29).getCellType();
                              if (inn23==0){
                                 row.getCell(29).setCellValue(DMYPDefx);
                                }
                           }
                           
                           if (row.getCell(30) != null){
                             inn23= row.getCell(30).getCellType();
                              if (inn23==0){
                                 row.getCell(30).setCellValue(DMYPOptx+DMYPNorx+DMYPRegx+DMYPDefx);
                                }
                           }
                           
                         
                    }
                    
                     if (newreg==36){ // Desisorio
                         
                          //II.1 Por etapas
                          //final
                           if (row.getCell(3) != null){
                             inn3= row.getCell(3).getCellType();
                                if (inn3==0){
                                    row.getCell(3).setCellValue(desiOpt);
                                }
                           }
                           
                           
                           if (row.getCell(4) != null){
                             inn4= row.getCell(4).getCellType();
                              if (inn4==0){
                                    row.getCell(4).setCellValue(desiNor);
                                }
                           }
                           
                           
                           if (row.getCell(5) != null){
                             inn5= row.getCell(5).getCellType();
                              if (inn5==0){
                                    row.getCell(5).setCellValue(desiReg);
                                }
                           }
                           
                           
                           if (row.getCell(6) != null){
                             inn6= row.getCell(6).getCellType();
                              if (inn6==0){
                                    row.getCell(6).setCellValue(desiDef);
                                }
                           }
                           
                           if (row.getCell(7) != null){
                             inn7= row.getCell(7).getCellType();
                              if (inn7==0){
                                    row.getCell(7).setCellValue(desiOpt+desiNor+desiReg+desiDef);
                                }
                           }
                           
                            
                           
                             if (row.getCell(19) != null){
                             inn19= row.getCell(19).getCellType();
                                if (inn19==0){
                                    row.getCell(19).setCellValue(PHOpt);
                                }
                           }
                           
                           
                           if (row.getCell(20) != null){
                             inn20= row.getCell(20).getCellType();
                              if (inn20==0){
                                    row.getCell(20).setCellValue(PHNor);
                                }
                           }
                           
                           
                           if (row.getCell(21) != null){
                             inn21= row.getCell(21).getCellType();
                              if (inn21==0){
                                    row.getCell(21).setCellValue(PHReg);
                                }
                           }
                           
                           
                           if (row.getCell(22) != null){
                             inn22= row.getCell(22).getCellType();
                              if (inn22==0){
                                    row.getCell(22).setCellValue(PHDef);
                                }
                           }
                           
                           if (row.getCell(23) != null){
                             inn23= row.getCell(23).getCellType();
                              if (inn23==0){
                                    row.getCell(23).setCellValue(PHOpt+PHNor+PHReg+PHDef);
                                }
                           }
                         
                           
                           if (row.getCell(26) != null){
                             inn23= row.getCell(26).getCellType();
                              if (inn23==0){
                                 row.getCell(26).setCellValue(DPHOptx);
                                }
                           }
                            
                            if (row.getCell(27) != null){
                             inn23= row.getCell(27).getCellType();
                              if (inn23==0){
                                 row.getCell(27).setCellValue(DPHNorx);
                                }
                           }
                            
                           if (row.getCell(28) != null){
                             inn23= row.getCell(28).getCellType();
                              if (inn23==0){
                                 row.getCell(28).setCellValue(DPHRegx);
                                }
                           }
                           
                           if (row.getCell(29) != null){
                             inn23= row.getCell(29).getCellType();
                              if (inn23==0){
                                 row.getCell(29).setCellValue(DPHDefx);
                                }
                           }
                           
                           if (row.getCell(30) != null){
                             inn23= row.getCell(30).getCellType();
                              if (inn23==0){
                                 row.getCell(30).setCellValue(DPHOptx+DPHNorx+DPHRegx+DPHDefx);
                                }
                           }
                           
                    }
                    
                      
                      
                     if (newreg==37){ // Inpuganatorio
                         
                          //II.1 Por etapas
                          //final
                           if (row.getCell(3) != null){
                             inn3= row.getCell(3).getCellType();
                                if (inn3==0){
                                    row.getCell(3).setCellValue(impOpt);
                                }
                           }
                           
                           
                           if (row.getCell(4) != null){
                             inn4= row.getCell(4).getCellType();
                              if (inn4==0){
                                    row.getCell(4).setCellValue(impNor);
                                }
                           }
                           
                           
                           if (row.getCell(5) != null){
                             inn5= row.getCell(5).getCellType();
                              if (inn5==0){
                                    row.getCell(5).setCellValue(impReg);
                                }
                           }
                           
                           
                           if (row.getCell(6) != null){
                             inn6= row.getCell(6).getCellType();
                              if (inn6==0){
                                    row.getCell(6).setCellValue(impDef);
                                }
                           }
                           
                           if (row.getCell(7) != null){
                             inn7= row.getCell(7).getCellType();
                              if (inn7==0){
                                    row.getCell(7).setCellValue(impOpt+impNor+impReg+impDef);
                                }
                           }
                           
                           
                             if (row.getCell(19) != null){
                             inn19= row.getCell(19).getCellType();
                                if (inn19==0){
                                    row.getCell(19).setCellValue(FCEOpt+POPOpt+PHOpt+MYPOpt);
                                }
                           }
                           
                           
                           if (row.getCell(20) != null){
                             inn20= row.getCell(20).getCellType();
                              if (inn20==0){
                                    row.getCell(20).setCellValue(FCENor+POPNor+PHNor+MYPNor);
                                }
                           }
                           
                           
                           if (row.getCell(21) != null){
                             inn21= row.getCell(21).getCellType();
                              if (inn21==0){
                                    row.getCell(21).setCellValue(FCEReg+POPReg+PHReg+MYPReg);
                                }
                           }
                           
                           
                           if (row.getCell(22) != null){
                             inn22= row.getCell(22).getCellType();
                              if (inn22==0){
                                    row.getCell(22).setCellValue(FCEDef+POPDef+PHDef+MYPDef);
                                }
                           }
                           
                           if (row.getCell(23) != null){
                             inn23= row.getCell(23).getCellType();
                              if (inn23==0){
                                    row.getCell(23).setCellValue(FCEOpt+POPOpt+PHOpt+MYPOpt+FCENor+POPNor+PHNor+MYPNor+FCEReg+POPReg+PHReg+MYPReg+FCEDef+POPDef+PHDef+MYPDef);
                                }
                           }
                           
                           
                         
                    }
                     
                     
                       
                     if (newreg==38){ // Ejecucion
                         
                          //II.1 Por etapas
                          //final
                           if (row.getCell(3) != null){
                             inn3= row.getCell(3).getCellType();
                                if (inn3==0){
                                    row.getCell(3).setCellValue(ejeOpt);
                                }
                           }
                           
                           
                           if (row.getCell(4) != null){
                             inn4= row.getCell(4).getCellType();
                              if (inn4==0){
                                    row.getCell(4).setCellValue(ejeNor);
                                }
                           }
                           
                           
                           if (row.getCell(5) != null){
                             inn5= row.getCell(5).getCellType();
                              if (inn5==0){
                                    row.getCell(5).setCellValue(ejeReg);
                                }
                           }
                           
                           
                           if (row.getCell(6) != null){
                             inn6= row.getCell(6).getCellType();
                              if (inn6==0){
                                    row.getCell(6).setCellValue(ejeDef);
                                }
                           }
                           
                           if (row.getCell(7) != null){
                             inn7= row.getCell(7).getCellType();
                              if (inn7==0){
                                    row.getCell(7).setCellValue(ejeOpt+ejeNor+ejeReg+ejeDef);
                                }
                           }
                         
                    }
                     
                     
                     
                      if (newreg==39){ // Final
                         
                          //II.1 Por etapas
                          //final
                          if (row.getCell(3) != null){
                             inn3= row.getCell(3).getCellType();
                                if (inn3==0){
                                    row.getCell(3).setCellValue(finOpt);
                                }
                           }
                           
                           
                           if (row.getCell(4) != null){
                             inn4= row.getCell(4).getCellType();
                              if (inn4==0){
                                    row.getCell(4).setCellValue(finNor);
                                }
                           }
                           
                           
                           if (row.getCell(5) != null){
                             inn5= row.getCell(5).getCellType();
                              if (inn5==0){
                                    row.getCell(5).setCellValue(finReg);
                                }
                           }
                           
                           
                           if (row.getCell(6) != null){
                             inn6= row.getCell(6).getCellType();
                              if (inn6==0){
                                    row.getCell(6).setCellValue(finDef);
                                }
                           }
                           
                           if (row.getCell(7) != null){
                             inn7= row.getCell(7).getCellType();
                              if (inn7==0){
                                    row.getCell(7).setCellValue(finOpt+finNor+finReg+finDef);
                                }
                           }
                         
                    }
                      
                     if (newreg==40){ // sumatorio
                         
                          //II.1 Por etapas
                          //sumatorio
                          
                           
                           
                             if (row.getCell(3) != null){
                             inn3= row.getCell(3).getCellType();
                                if (inn3==0){
                                    row.getCell(3).setCellValue(calOpt+posOpt+proOpt+desiOpt+impOpt+ejeOpt+finOpt);
                                }
                           }
                           
                           
                           if (row.getCell(4) != null){
                             inn4= row.getCell(4).getCellType();
                              if (inn4==0){
                                    row.getCell(4).setCellValue(calNor+posNor+proNor+desiNor+impNor+ejeNor+finNor);
                                }
                           }
                           
                           
                           if (row.getCell(5) != null){
                             inn5= row.getCell(5).getCellType();
                              if (inn5==0){
                                    row.getCell(5).setCellValue(calReg+posReg+proReg+desiReg+impReg+ejeReg+finReg);
                                }
                           }
                           
                           
                           if (row.getCell(6) != null){
                             inn6= row.getCell(6).getCellType();
                              if (inn6==0){
                                    row.getCell(6).setCellValue(calDef+posDef+proDef+desiDef+impDef+ejeDef+finDef);
                                }
                           }
                           
                           if (row.getCell(7) != null){
                             inn7= row.getCell(7).getCellType();
                              if (inn7==0){
                                    row.getCell(7).setCellValue(calOpt+posOpt+proOpt+desiOpt+impOpt+ejeOpt+finOpt+calNor+posNor+proNor+desiNor+impNor+ejeNor+finNor+calReg+posReg+proReg+desiReg+impReg+ejeReg+finReg+calDef+posDef+proDef+desiDef+impDef+ejeDef+finDef);
                                }
                           }
                         
                    }
                 
                 if (newreg>47){
                 
                  if (legalTchnList3!= null){ 
                     if(ll_aux8<xlin8-1){           
                          // II.2 Por Responsable
                          //Asesor/Etapa
                         if (row.getCell(2) != null){
                             inn3= row.getCell(2).getCellType();
                                if (inn3!=0){
                                    row.getCell(2).setCellValue(legalTchnList3.get(ll_aux8).getLsAsesor());
                                }
                           }
                           
                             if (row.getCell(3) != null){
                             inn3= row.getCell(3).getCellType();
                                if (inn3==0){
                                    row.getCell(3).setCellValue(legalTchnList3.get(ll_aux8).getiCalifi());
                                }
                           }
                        
                           
                           if (row.getCell(4) != null){
                             inn4= row.getCell(4).getCellType();
                              if (inn4==0){
                                    row.getCell(4).setCellValue(legalTchnList3.get(ll_aux8).getiPostula());
                                }
                           }
                           
                           
                           if (row.getCell(5) != null){
                             inn5= row.getCell(5).getCellType();
                              if (inn5==0){
                                    row.getCell(5).setCellValue(legalTchnList3.get(ll_aux8).getiProba());
                                }
                           }
                           
                           
                           if (row.getCell(6) != null){
                             inn6= row.getCell(6).getCellType();
                              if (inn6==0){
                                    row.getCell(6).setCellValue(legalTchnList3.get(ll_aux8).getiDesiso());
                                }
                           }
                           
                           if (row.getCell(7) != null){
                             inn7= row.getCell(7).getCellType();
                              if (inn7==0){
                                    row.getCell(7).setCellValue(legalTchnList3.get(ll_aux8).getiImpug());
                                }
                           }
                           
                          if (row.getCell(8) != null){
                             inn7= row.getCell(8).getCellType();
                              if (inn7==0){
                                    row.getCell(8).setCellValue(legalTchnList3.get(ll_aux8).getiEjecu());
                                }
                           }
                             
                          if (row.getCell(9) != null){
                             inn7= row.getCell(9).getCellType();
                              if (inn7==0){
                                    row.getCell(9).setCellValue(legalTchnList3.get(ll_aux8).getiFinal1());
                                }
                           }
                          
                           if (row.getCell(10) != null){
                             inn7= row.getCell(10).getCellType();
                              if (inn7==0){
                                    row.getCell(10).setCellValue(legalTchnList3.get(ll_aux8).getiTotalAse());
                                }
                           }
                    }
                  }
                  
                  if(ll_aux8==Param1){ 
                      if (row.getCell(2) != null){
                             inn3= row.getCell(2).getCellType();
                                if (inn3==4){
                                    row.getCell(2).setCellValue(legalTchnList3.get(legalTchnList3.size()-1).getLsAsesor());
                                }
                           }
                           
                             if (row.getCell(3) != null){
                             inn3= row.getCell(3).getCellType();
                                if (inn3==0){
                                    row.getCell(3).setCellValue(legalTchnList3.get(legalTchnList3.size()-1).getiCalifi());
                                }
                           }
                        
                           
                           if (row.getCell(4) != null){
                             inn4= row.getCell(4).getCellType();
                              if (inn4==0){
                                    row.getCell(4).setCellValue(legalTchnList3.get(legalTchnList3.size()-1).getiPostula());
                                }
                           }
                           
                           
                           if (row.getCell(5) != null){
                             inn5= row.getCell(5).getCellType();
                              if (inn5==0){
                                    row.getCell(5).setCellValue(legalTchnList3.get(legalTchnList3.size()-1).getiProba());
                                }
                           }
                           
                           
                           if (row.getCell(6) != null){
                             inn6= row.getCell(6).getCellType();
                              if (inn6==0){
                                    row.getCell(6).setCellValue(legalTchnList3.get(legalTchnList3.size()-1).getiDesiso());
                                }
                           }
                           
                           if (row.getCell(7) != null){
                             inn7= row.getCell(7).getCellType();
                              if (inn7==0){
                                    row.getCell(7).setCellValue(legalTchnList3.get(legalTchnList3.size()-1).getiImpug());
                                }
                           }
                           
                          if (row.getCell(8) != null){
                             inn7= row.getCell(8).getCellType();
                              if (inn7==0){
                                    row.getCell(8).setCellValue(legalTchnList3.get(legalTchnList3.size()-1).getiEjecu());
                                }
                           }
                             
                          if (row.getCell(9) != null){
                             inn7= row.getCell(9).getCellType();
                              if (inn7==0){
                                    row.getCell(9).setCellValue(legalTchnList3.get(legalTchnList3.size()-1).getiFinal1());
                                }
                           }
                          
                           if (row.getCell(10) != null){
                             inn7= row.getCell(10).getCellType();
                              if (inn7==0){
                                    row.getCell(10).setCellValue(legalTchnList3.get(legalTchnList3.size()-1).getiTotalAse());
                                }
                           }
                   }
                 
                  
                  
                  
                   ll_aux8 = ll_aux8 + 1;  
                  
                   
                   
                   if (legalTchnList5!= null){ 
                  
                        if(ll_aux9<xlin9-1){
                            if (row.getCell(18) != null){
                                 inn19= row.getCell(18).getCellType();
                                    if (inn19!=0){
                                        row.getCell(18).setCellValue(legalTchnList5.get(ll_aux9).getLsAsesor());
                                    }
                               }

                                if (row.getCell(19) != null){
                                 inn19= row.getCell(19).getCellType();
                                    if (inn19==0){
                                        row.getCell(19).setCellValue(legalTchnList5.get(ll_aux9).getiOptimo());
                                    }
                               }


                               if (row.getCell(20) != null){
                                 inn20= row.getCell(20).getCellType();
                                  if (inn20==0){
                                        row.getCell(20).setCellValue(legalTchnList5.get(ll_aux9).getiNormal());
                                    }
                               }


                               if (row.getCell(21) != null){
                                 inn21= row.getCell(21).getCellType();
                                  if (inn21==0){
                                        row.getCell(21).setCellValue(legalTchnList5.get(ll_aux9).getiRegular());
                                    }
                               }


                               if (row.getCell(22) != null){
                                 inn22= row.getCell(22).getCellType();
                                  if (inn22==0){
                                        row.getCell(22).setCellValue(legalTchnList5.get(ll_aux9).getiDeficiente());
                                    }
                               }

                               if (row.getCell(23) != null){
                                 inn23= row.getCell(23).getCellType();
                                  if (inn23==0){
                                        row.getCell(23).setCellValue(legalTchnList5.get(ll_aux9).getiTotalIndi());
                                    }
                               }
                            
                           }      
                         
                        if(ll_aux9==Param1){ 
                            
                             if (row.getCell(18) != null){
                                  inn19= row.getCell(18).getCellType();
                                     if (inn19==4){
                                         row.getCell(18).setCellValue(legalTchnList5.get(legalTchnList5.size()-1).getLsAsesor());
                                     }
                                }

                                 if (row.getCell(19) != null){
                                  inn19= row.getCell(19).getCellType();
                                     if (inn19==0){
                                         row.getCell(19).setCellValue(legalTchnList5.get(legalTchnList5.size()-1).getiOptimo());
                                     }
                                }


                                if (row.getCell(20) != null){
                                  inn20= row.getCell(20).getCellType();
                                   if (inn20==0){
                                         row.getCell(20).setCellValue(legalTchnList5.get(legalTchnList5.size()-1).getiNormal());
                                     }
                                }


                                if (row.getCell(21) != null){
                                  inn21= row.getCell(21).getCellType();
                                   if (inn21==0){
                                         row.getCell(21).setCellValue(legalTchnList5.get(legalTchnList5.size()-1).getiRegular());
                                     }
                                }


                                if (row.getCell(22) != null){
                                  inn22= row.getCell(22).getCellType();
                                   if (inn22==0){
                                         row.getCell(22).setCellValue(legalTchnList5.get(legalTchnList5.size()-1).getiDeficiente());
                                     }
                                }

                                if (row.getCell(23) != null){
                                  inn23= row.getCell(23).getCellType();
                                   if (inn23==0){
                                         row.getCell(23).setCellValue(legalTchnList5.get(legalTchnList5.size()-1).getiTotalIndi());
                                     }
                                }

                            }
                        }
                       ll_aux9= ll_aux9 + 1;
                    }
                      if (newreg>62+Param2){
                           if(ll_aux<xlin-1){
                                if (row.getCell(2) != null){
                                    inn2= row.getCell(2).getCellType();
                                    if (inn2!=0){
                                       row.getCell(2).setCellValue(legalTchnList6.get(ll_aux).getLsAsesor());
                                    }
                                }     
                                  
                            if (row.getCell(3) != null){
                                inn3= row.getCell(3).getCellType();
                                if (inn3==0 || inn3==3){
                                     row.getCell(3).setCellValue(legalTchnList6.get(ll_aux).getiCapital());
                                }
                            }
                            if (row.getCell(4) != null){
                                inn4= row.getCell(4).getCellType();
                                if (inn4==0 || inn4==3){
                                     row.getCell(4).setCellValue(legalTchnList6.get(ll_aux).getiMype());
                                }
                            }
                            
                            if (row.getCell(5) != null){
                                inn5= row.getCell(5).getCellType();
                                if (inn5==0 || inn5==3){
                                     row.getCell(5).setCellValue(legalTchnList6.get(ll_aux).getiPopular());
                                }
                            }
                            
                            if (row.getCell(6) != null){
                                inn5= row.getCell(6).getCellType();
                                if (inn5==0 || inn5==3){
                                     row.getCell(6).setCellValue(legalTchnList6.get(ll_aux).getiPerez());
                                }
                            }
                            
                            if (row.getCell(7) != null){
                                inn5= row.getCell(7).getCellType();
                                if (inn5==0 || inn5==3){
                                     row.getCell(7).setCellValue(legalTchnList6.get(ll_aux).getiTotalAse());
                                }
                            }
                            } 
                            
                            
                            
                            if  (ll_aux==Param3){
                                if (row.getCell(2) != null){
                                    inn2= row.getCell(2).getCellType();
                                    if (inn2!=0){
                                       row.getCell(2).setCellValue(legalTchnList6.get(legalTchnList6.size()-1).getLsAsesor());
                                    }
                                }     
                                  
                                if (row.getCell(3) != null){
                                    inn3= row.getCell(3).getCellType();
                                    if (inn3==0){
                                         row.getCell(3).setCellValue(legalTchnList6.get(legalTchnList6.size()-1).getiCapital());
                                    }
                                }
                                if (row.getCell(4) != null){
                                    inn4= row.getCell(4).getCellType();
                                    if (inn4==0){
                                         row.getCell(4).setCellValue(legalTchnList6.get(legalTchnList6.size()-1).getiMype());
                                    }
                                }

                                if (row.getCell(5) != null){
                                    inn5= row.getCell(5).getCellType();
                                    if (inn5==0){
                                         row.getCell(5).setCellValue(legalTchnList6.get(legalTchnList6.size()-1).getiPopular());
                                    }
                                }

                                if (row.getCell(6) != null){
                                    inn5= row.getCell(6).getCellType();
                                    if (inn5==0){
                                         row.getCell(6).setCellValue(legalTchnList6.get(legalTchnList6.size()-1).getiPerez());
                                    }
                                }

                                if (row.getCell(7) != null){
                                    inn5= row.getCell(7).getCellType();
                                    if (inn5==0){
                                         row.getCell(7).setCellValue(legalTchnList6.get(legalTchnList6.size()-1).getiTotalAse());
                                    }
                                }
                            }
                       
                            if (legalTchnList12!= null){ 
                                  
                                  if(ll_aux<xlin5-1){ 
                                       if (row.getCell(18) != null){
                                        inn18= row.getCell(18).getCellType();
                                           if (inn18!=0){
                                               row.getCell(18).setCellValue(legalTchnList12.get(ll_aux).getLsAsesor());
                                           }
                                       }
                                      
                                      if (row.getCell(19) != null){
                                        inn19= row.getCell(19).getCellType();
                                           if (inn19==0 || inn19==3){
                                               row.getCell(19).setCellValue(legalTchnList12.get(ll_aux).getiCalifi());
                                           }
                                      }


                                      if (row.getCell(20) != null){
                                        inn20= row.getCell(20).getCellType();
                                           if (inn20==0 || inn20==3){
                                               row.getCell(20).setCellValue(legalTchnList12.get(ll_aux).getiPostula());
                                           }
                                      }


                                      if (row.getCell(21) != null){
                                        inn21= row.getCell(21).getCellType();
                                         if (inn21==0 || inn21==3){
                                               row.getCell(21).setCellValue(legalTchnList12.get(ll_aux).getiProba());
                                           }
                                      }


                                      if (row.getCell(22) != null){
                                        inn22= row.getCell(22).getCellType();
                                         if (inn22==0 || inn22==3){
                                               row.getCell(22).setCellValue(legalTchnList12.get(ll_aux).getiDesiso());
                                           }
                                      }

                                      if (row.getCell(23) != null){
                                        inn23= row.getCell(23).getCellType();
                                          if (inn23==0 || inn23==3){
                                                row.getCell(23).setCellValue(legalTchnList12.get(ll_aux).getiImpug());
                                           }
                                      }

                                      if (row.getCell(24) != null){
                                        inn22= row.getCell(24).getCellType();
                                           if (inn22==0 || inn22==3){
                                               row.getCell(24).setCellValue(legalTchnList12.get(ll_aux).getiEjecu());
                                           }
                                      }

                                      if (row.getCell(25) != null){
                                        inn22= row.getCell(25).getCellType();
                                         if (inn22==0 || inn22==3){
                                               row.getCell(25).setCellValue(legalTchnList12.get(ll_aux).getiFinal1());
                                           }
                                      }

                                       if (row.getCell(26) != null){
                                        inn22= row.getCell(26).getCellType();
                                         if (inn22==0 || inn22==3){
                                               row.getCell(26).setCellValue(legalTchnList12.get(ll_aux).getiTotalAse());
                                           }
                                       }
                                   }
                                  
                                  if(ll_aux==Param3){ 
                                       if (row.getCell(18) != null){
                                        inn18= row.getCell(18).getCellType();
                                           if (inn18==1){
                                               row.getCell(18).setCellValue(legalTchnList12.get(legalTchnList12.size()-1).getLsAsesor());
                                           }
                                       }
                                      
                                      if (row.getCell(19) != null){
                                        inn19= row.getCell(19).getCellType();
                                           if (inn19==0){
                                               row.getCell(19).setCellValue(legalTchnList12.get(legalTchnList12.size()-1).getiCalifi());
                                           }
                                      }


                                      if (row.getCell(20) != null){
                                        inn20= row.getCell(20).getCellType();
                                         if (inn20==0){
                                               row.getCell(20).setCellValue(legalTchnList12.get(legalTchnList12.size()-1).getiPostula());
                                           }
                                      }


                                      if (row.getCell(21) != null){
                                        inn21= row.getCell(21).getCellType();
                                         if (inn21==0){
                                               row.getCell(21).setCellValue(legalTchnList12.get(legalTchnList12.size()-1).getiProba());
                                           }
                                      }


                                      if (row.getCell(22) != null){
                                        inn22= row.getCell(22).getCellType();
                                         if (inn22==0){
                                               row.getCell(22).setCellValue(legalTchnList12.get(legalTchnList12.size()-1).getiDesiso());
                                           }
                                      }

                                      if (row.getCell(23) != null){
                                        inn23= row.getCell(23).getCellType();
                                         if (inn23==0){
                                               row.getCell(23).setCellValue(legalTchnList12.get(legalTchnList12.size()-1).getiImpug());
                                           }
                                      }

                                      if (row.getCell(24) != null){
                                        inn22= row.getCell(24).getCellType();
                                         if (inn22==0){
                                               row.getCell(24).setCellValue(legalTchnList12.get(legalTchnList12.size()-1).getiEjecu());
                                           }
                                      }

                                      if (row.getCell(25) != null){
                                        inn22= row.getCell(25).getCellType();
                                         if (inn22==0){
                                               row.getCell(25).setCellValue(legalTchnList12.get(legalTchnList12.size()-1).getiFinal1());
                                           }
                                      }

                                       if (row.getCell(26) != null){
                                        inn22= row.getCell(26).getCellType();
                                         if (inn22==0){
                                               row.getCell(26).setCellValue(legalTchnList12.get(legalTchnList12.size()-1).getiTotalAse());
                                           }
                                       }
                                   }
                                   
                            }
                        
                             ll_aux=ll_aux+1;   
                         
                           
                          if (newreg>75+Param2+Param4){
                                if (ll_aux1<xlin1){ // Fondo Capital Emprendedor
                                   if (legalTchnList7.get(ll_aux1).getMaeInversion().getCInversion()!=null){
                                        if (row.getCell(2) != null){
                                             inn2= row.getCell(2).getCellType();
                                             if (inn2==0 || inn2==3 ){
                                                row.getCell(2).setCellValue(legalTchnList7.get(ll_aux1).getiTotalFondo());
                                             }
                                         }     

                                         if (row.getCell(3) != null){
                                             inn3= row.getCell(3).getCellType();
                                             if (inn3==1){
                                                  row.getCell(3).setCellValue(legalTchnList7.get(ll_aux1).getMaeInversion().getCInversion());
                                             }
                                         }
                                         if (row.getCell(4) != null){
                                             inn4= row.getCell(4).getCellType();
                                             if (inn4==0 || inn4==3){
                                                  row.getCell(4).setCellValue(legalTchnList7.get(ll_aux1).getiTotalAse() );
                                             }
                                         }

                                         if (row.getCell(5) != null){
                                             inn5= row.getCell(5).getCellType();
                                             if (inn5==1){
                                                  row.getCell(5).setCellValue(legalTchnList7.get(ll_aux1).getIdetapa() );
                                             }
                                         }

                                         if (row.getCell(6) != null){
                                             inn5= row.getCell(6).getCellType();
                                             if (inn5!=0){
                                                  row.getCell(6).setCellValue(legalTchnList7.get(ll_aux1).getLsAsesor());
                                             }
                                         }
                                    }     
                                    ll_aux1=ll_aux1+1;
                                }
                               
                                
                                if (ll_aux2<xlin2){ //Fondo Mype-TCHN
                                    if (legalTchnList8.get(ll_aux2).getMaeInversion().getCInversion()!=null){
                                        if (row.getCell(8) != null){
                                             inn2= row.getCell(8).getCellType();
                                             if (inn2==0 || inn2==3){
                                                row.getCell(8).setCellValue(legalTchnList8.get(ll_aux2).getiTotalFondo());
                                             }
                                         }     

                                         if (row.getCell(9) != null){
                                             inn3= row.getCell(9).getCellType();
                                             if (inn3==1){
                                                  row.getCell(9).setCellValue(legalTchnList8.get(ll_aux2).getMaeInversion().getCInversion());
                                             }
                                         }
                                         if (row.getCell(10) != null){
                                             inn4= row.getCell(10).getCellType();
                                             if (inn4==0 || inn4==3){
                                                  row.getCell(10).setCellValue(legalTchnList8.get(ll_aux2).getiTotalAse() );
                                             }
                                         }

                                         if (row.getCell(11) != null){
                                             inn5= row.getCell(11).getCellType();
                                             if (inn5==1){
                                                  row.getCell(11).setCellValue(legalTchnList8.get(ll_aux2).getIdetapa() );
                                             }
                                         }

                                         if (row.getCell(12) != null){
                                             inn5= row.getCell(12).getCellType();
                                             if (inn5==1){
                                                  row.getCell(12).setCellValue(legalTchnList8.get(ll_aux2).getLsAsesor());
                                             }
                                         }
                                     }    
                                    ll_aux2=ll_aux2+1;
                                }
                                 }
                                  if (newreg>76+Param2+Param4){
                                        if(ll_aux6<xlin6-1){ 
                                                if (row.getCell(18) != null){
                                                 inn18= row.getCell(18).getCellType();
                                                 
                                                    if (inn18==1 || inn18==3 ){
                                                        row.getCell(18).setCellValue(legalTchnList13.get(ll_aux6).getLsAsesor());
                                                    }
                                                }

                                               if (row.getCell(19) != null){
                                                 inn19= row.getCell(19).getCellType();
                                                    if (inn19==0 || inn19==3){
                                                        row.getCell(19).setCellValue(legalTchnList13.get(ll_aux6).getiOptimo());
                                                    }
                                               }


                                               if (row.getCell(20) != null){
                                                 inn20= row.getCell(20).getCellType();
                                                  if (inn20==0 || inn20==3){
                                                        row.getCell(20).setCellValue(legalTchnList13.get(ll_aux6).getiNormal());
                                                    }
                                               }


                                               if (row.getCell(21) != null){
                                                 inn21= row.getCell(21).getCellType();
                                                  if (inn21==0 || inn21==3){
                                                        row.getCell(21).setCellValue(legalTchnList13.get(ll_aux6).getiRegular());
                                                    }
                                               }


                                               if (row.getCell(22) != null){
                                                 inn22= row.getCell(22).getCellType();
                                                  if (inn22==0 || inn22==3){
                                                        row.getCell(22).setCellValue(legalTchnList13.get(ll_aux6).getiDeficiente());
                                                    }
                                               }

                                               if (row.getCell(23) != null){
                                                 inn23= row.getCell(23).getCellType();
                                                  if (inn23==0 || inn23==3){
                                                        row.getCell(23).setCellValue(legalTchnList13.get(ll_aux6).getiTotalAse());
                                                    }
                                               }
                                           }
                                          if(ll_aux6==8){ 
                                                if (row.getCell(18) != null){
                                                 inn18= row.getCell(18).getCellType();
                                                    if (inn18==1){
                                                        row.getCell(18).setCellValue(legalTchnList13.get(legalTchnList13.size()-1).getLsAsesor());
                                                    }
                                                }

                                               if (row.getCell(19) != null){
                                                 inn19= row.getCell(19).getCellType();
                                                    if (inn19==0){
                                                        row.getCell(19).setCellValue(legalTchnList13.get(legalTchnList13.size()-1).getiOptimo());
                                                    }
                                               }


                                               if (row.getCell(20) != null){
                                                 inn20= row.getCell(20).getCellType();
                                                  if (inn20==0){
                                                        row.getCell(20).setCellValue(legalTchnList13.get(legalTchnList13.size()-1).getiNormal());
                                                    }
                                               }


                                               if (row.getCell(21) != null){
                                                 inn21= row.getCell(21).getCellType();
                                                  if (inn21==0){
                                                        row.getCell(21).setCellValue(legalTchnList13.get(legalTchnList13.size()-1).getiRegular());
                                                    }
                                               }


                                               if (row.getCell(22) != null){
                                                 inn22= row.getCell(22).getCellType();
                                                  if (inn22==0){
                                                        row.getCell(22).setCellValue(legalTchnList13.get(legalTchnList13.size()-1).getiDeficiente());
                                                    }
                                               }

                                               if (row.getCell(23) != null){
                                                 inn23= row.getCell(23).getCellType();
                                                  if (inn23==0){
                                                        row.getCell(23).setCellValue(legalTchnList13.get(legalTchnList13.size()-1).getiTotalAse());
                                                    }
                                               }
                                           }
                                        ll_aux6=ll_aux6+1; 
                                        
                                  }
                                  
                                 if (newreg>87+Param2+Param4){
                                  if (ll_aux3<xlin3){ // Fondo Popular 1 RM
                                   if (legalTchnList9.get(ll_aux3).getMaeInversion().getCInversion()!=null) {  
                                            if (row.getCell(2) != null){
                                                 inn2= row.getCell(2).getCellType();
                                                 if (inn2==0 || inn2==3){
                                                    row.getCell(2).setCellValue(legalTchnList9.get(ll_aux3).getiTotalFondo());
                                                 }
                                             }     

                                             if (row.getCell(3) != null){
                                                 inn3= row.getCell(3).getCellType();
                                                 if (inn3==1){
                                                      row.getCell(3).setCellValue(legalTchnList9.get(ll_aux3).getMaeInversion().getCInversion());
                                                 }
                                             }
                                             if (row.getCell(4) != null){
                                                 inn4= row.getCell(4).getCellType();
                                                 if (inn4==0 || inn4==3){
                                                      row.getCell(4).setCellValue(legalTchnList9.get(ll_aux3).getiTotalAse() );
                                                 }
                                             }

                                             if (row.getCell(5) != null){
                                                 inn5= row.getCell(5).getCellType();
                                                 if (inn5==1){
                                                      row.getCell(5).setCellValue(legalTchnList9.get(ll_aux3).getIdetapa() );
                                                 }
                                             }

                                             if (row.getCell(6) != null){
                                                 inn5= row.getCell(6).getCellType();
                                                 if (inn5==1){
                                                      row.getCell(6).setCellValue(legalTchnList9.get(ll_aux3).getLsAsesor());
                                                 }
                                             }
                                     }        
                                    ll_aux3=ll_aux3+1;
                                }
                                
                                
                                if (ll_aux4<xlin4){ //Fondo Pérez Hidalgo
                                   if (legalTchnList10.get(ll_aux4).getMaeInversion().getCInversion()!=null){ 
                                        if (row.getCell(8) != null){
                                             inn2= row.getCell(8).getCellType();
                                             if (inn2==0 || inn2==3){
                                                row.getCell(8).setCellValue(legalTchnList10.get(ll_aux4).getiTotalFondo());
                                             }
                                         }     

                                         if (row.getCell(9) != null){
                                             inn3= row.getCell(9).getCellType();
                                             if (inn3==1){
                                                  row.getCell(9).setCellValue(legalTchnList10.get(ll_aux4).getMaeInversion().getCInversion());
                                             }
                                         }
                                         if (row.getCell(10) != null){
                                             inn4= row.getCell(10).getCellType();
                                             if (inn4==0 || inn4==3){
                                                  row.getCell(10).setCellValue(legalTchnList10.get(ll_aux4).getiTotalAse() );
                                             }
                                         }

                                         if (row.getCell(11) != null){
                                             inn5= row.getCell(11).getCellType();
                                             if (inn5==1){
                                                  row.getCell(11).setCellValue(legalTchnList10.get(ll_aux4).getIdetapa() );
                                             }
                                         }

                                         if (row.getCell(12) != null){
                                             inn5= row.getCell(12).getCellType();
                                             if (inn5==1){
                                                  row.getCell(12).setCellValue(legalTchnList10.get(ll_aux4).getLsAsesor());
                                             }
                                         }
                                    }     
                                    ll_aux4=ll_aux4+1;
                                }
                               }
                               if (newreg>99+Param2+Param4){
                                  if (ll_aux7<=xlin7){ // Menos Visitados
                                   if (legalTchnList11.get(ll_aux7).getMaeInversion().getCInversion()!=null) {  
                                            if (row.getCell(2) != null){
                                                 inn2= row.getCell(2).getCellType();
                                                 if (inn2==0 || inn2==3){
                                                    row.getCell(2).setCellValue(legalTchnList11.get(ll_aux7).getiTotalIndi() );
                                                 }
                                             }     

                                             if (row.getCell(3) != null){
                                                 inn3= row.getCell(3).getCellType();
                                                 if (inn3==1){
                                                      row.getCell(3).setCellValue(legalTchnList11.get(ll_aux7).getDdescripcion());
                                                 }
                                             }
                                    
                                             if (row.getCell(4) != null){
                                                 inn3= row.getCell(4).getCellType();
                                                 if (inn3==1){
                                                      row.getCell(4).setCellValue(legalTchnList11.get(ll_aux7).getMaeInversion().getCInversion());
                                                 }
                                             }
                                              
                                             if (row.getCell(5) != null){
                                                 inn4= row.getCell(5).getCellType();
                                                 if (inn4==1){
                                                      row.getCell(5).setCellValue(legalTchnList11.get(ll_aux7).getEtapa());
                                                 }
                                             }

                                             if (row.getCell(6) != null){
                                                 inn5= row.getCell(6).getCellType();
                                                 if (inn5==0 || inn5==3){
                                                      row.getCell(6).setCellValue(legalTchnList11.get(ll_aux7).getLnDiastra());
                                                 }
                                             }

                                             if (row.getCell(7) != null){
                                                 inn5= row.getCell(7).getCellType();
                                                 if (inn5==0 || inn5==3){
                                                      row.getCell(7).setCellValue(legalTchnList11.get(ll_aux7).getiTotalFondo());
                                                 }
                                             }
                                     }        
                                    ll_aux7=ll_aux7+1;
                                   }
                                } 
                   }      
                }
                FileOutputStream salida = new FileOutputStream(archivo);
                
            
               
                
                worbook.write(salida);
                worbook.close();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    
                   

                    byte[] asss = Files.readAllBytes(new File(rutaArchivo1).toPath());
               
                    
                    baos.write(asss);

                    
                    FacesContext context = FacesContext.getCurrentInstance();
                    Object response = context.getExternalContext().getResponse();

                    if (response instanceof HttpServletResponse) {
                        HttpServletResponse hsr = (HttpServletResponse) response;
                        hsr.setContentType("application/vnd.ms-excel");
                        hsr.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");

                        hsr.setHeader("Content-disposition", "attachment;filename=Dashboard_Legal.xlsx");
                        hsr.setContentLength(baos.size());
                        try {
                            ServletOutputStream out = hsr.getOutputStream();
                            baos.writeTo(out);

                            out.flush();
                        } catch (IOException ex) {
                            System.out.println("Error:  " + ex.getMessage());
                        }
                        context.responseComplete();
                    }
                    
                     
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("The file not exists (No se encontró el fichero): " + fileNotFoundException);
        } catch (IOException ex) {
            System.out.println("Error in file procesing (Error al procesar el fichero): " + ex);
        } finally {
      
     
      }   
     } 
    }       

    public void filtrarPeriodo(String sMes,String sAnio){    
      
      try {
        
          if (sMes.length()>0 && sAnio.length()>0){
                legalTchnList6 = legalServ.listar_LegalImpxfondo(sMes,sAnio);
                legalTchnList7 = legalServ.lista_TopVisiCap(sMes,sAnio);
                legalTchnList8 = legalServ.lista_TopVisiMyp(sMes,sAnio);
                legalTchnList9 = legalServ.lista_TopVisiPop(sMes,sAnio);
                legalTchnList10 = legalServ.lista_TopVisiPrh(sMes,sAnio);
                 List<List<Object>> listImpxFondo   = legalServ.cargarDashboard_Impxfondo();
                 legalTchnList11 = legalServ.Lista_ImpTopneg(sMes,sAnio);
                  data5A=listImpxFondo.get(0).toString();
                  data5B=listImpxFondo.get(1).toString();
                  data5C=listImpxFondo.get(2).toString();
                  data5D=listImpxFondo.get(3).toString();
                  data50=listImpxFondo.get(5).toString();
                  Ase1=listImpxFondo.get(4).get(0).toString();
                  Ase2=listImpxFondo.get(4).get(1).toString();
                  Ase3=listImpxFondo.get(4).get(2).toString();
                  Ase4=listImpxFondo.get(4).get(4).toString();
                  Ase5=listImpxFondo.get(4).get(5).toString();
                  Ase6=listImpxFondo.get(4).get(6).toString();
                  Ase7=listImpxFondo.get(4).get(7).toString();

                 if (listImpxFondo.get(0).size()>0 ) {
                    Ase1Cap= listImpxFondo.get(0).get(1).toString() ;
                    Ase2Cap= listImpxFondo.get(0).get(2).toString() ;
                    Ase3Cap= listImpxFondo.get(0).get(3).toString() ;
                    Ase4Cap= listImpxFondo.get(0).get(4).toString() ;
                    Ase5Cap= listImpxFondo.get(0).get(5).toString() ;
                    Ase6Cap= listImpxFondo.get(0).get(6).toString() ;
                    Ase7Cap= listImpxFondo.get(0).get(7).toString() ;
                 }

                 if (listImpxFondo.get(1).size()>0 ) {
                    Ase1Pop= listImpxFondo.get(1).get(1).toString() ;
                    Ase2Pop= listImpxFondo.get(1).get(2).toString() ;
                    Ase3Pop= listImpxFondo.get(1).get(3).toString() ;
                    Ase4Pop= listImpxFondo.get(1).get(4).toString() ;
                    Ase5Pop= listImpxFondo.get(1).get(5).toString() ;
                    Ase6Pop= listImpxFondo.get(1).get(6).toString() ;
                    Ase7Pop= listImpxFondo.get(1).get(7).toString() ;
                 }

                 if (listImpxFondo.get(2).size()>0 ) {
                    Ase1Myp= listImpxFondo.get(2).get(1).toString() ;
                    Ase2Myp= listImpxFondo.get(2).get(2).toString() ;
                    Ase3Myp= listImpxFondo.get(2).get(3).toString() ;
                    Ase4Myp= listImpxFondo.get(2).get(4).toString() ;
                    Ase5Myp= listImpxFondo.get(2).get(5).toString() ;
                    Ase6Myp= listImpxFondo.get(2).get(6).toString() ;
                    Ase7Myp= listImpxFondo.get(2).get(7).toString() ;
                 }

                if (listImpxFondo.get(3).size()>0 ) {
                    Ase1Prh= listImpxFondo.get(3).get(1).toString() ;
                    Ase2Prh= listImpxFondo.get(3).get(2).toString() ;
                    Ase3Prh= listImpxFondo.get(3).get(3).toString() ;
                    Ase4Prh= listImpxFondo.get(3).get(4).toString() ;
                    Ase5Prh= listImpxFondo.get(3).get(5).toString() ;
                    Ase6Prh= listImpxFondo.get(3).get(6).toString() ;
                    Ase7Prh= listImpxFondo.get(3).get(7).toString() ;
          } 
          }   
              
            //System.out.println(" cantidad de Fondos -> " + maeFondoList.size());
        } catch (Exception e) {

        }
    } 
    
    
    public void filtrarxDesemEtapa(String sMes,String sAnio){    
      try {
           if ( sMes.length()>0 && sAnio.length()>0){
                legalTchnList12 = legalServ.Lista_xAseEtapa(sMes,sAnio);
                legalTchnList13 = legalServ.Lista_xAseIndicador(sMes,sAnio);

                List<List<Object>> list5 = legalServ.Dashboard_xAseIndicador() ;
                data7A=list5.get(0).toString();
                data7B=list5.get(1).toString();
                data7C=list5.get(2).toString();
                data7D=list5.get(3).toString();
                data70=list5.get(4).toString();
          } 
        } catch (Exception e) {

        }
    }
    
    public void ListarGastosJudiciales(LegalTchn olegTchn) {
        try {
            mostrarListaGastosJudiciales=true;
            System.out.println("call listarGastosJudiciales");
            //seteando valores
            LegGastoJudicial legGastoJudicial = new LegGastoJudicial();
            System.out.println("fondo");
            System.out.println(olegTchn.getMaeInversion().getMaeFondo().getCFondoId());
            legGastoJudicial.setFondo(olegTchn.getMaeInversion().getMaeFondo().getCFondoId().trim());
            System.out.println("codigoTCHN");
            System.out.println(olegTchn.getMaeInversion().getCInversion().trim());
            legGastoJudicial.setCodigoTCHN(olegTchn.getMaeInversion().getCInversion().trim());
            System.out.println("NroDocumento");
            System.out.println(olegTchn.getMaeInversion().getcPersonaId().getANroDocumento().trim());
            legGastoJudicial.setNumeroDocumento(olegTchn.getMaeInversion().getcPersonaId().getANroDocumento().trim());
            this.codigoTCHN = olegTchn.getMaeInversion().getCInversion().trim();
            this.fondo = olegTchn.getMaeInversion().getMaeFondo().getCFondoId().trim();
            System.out.println("nroExpediente");
            if(olegTchn.getNroExpediente()==null || olegTchn.getNroExpediente().trim().equals(""))
            {
                this.nroExpediente = "";
            }
            else
            {
                System.out.println(olegTchn.getNroExpediente().trim());
                this.nroExpediente = olegTchn.getNroExpediente().trim();
            }
            
            System.out.println("nroDocumento");
            System.out.println(olegTchn.getMaeInversion().getcPersonaId().getANroDocumento().trim());
            this.nroDocumento = olegTchn.getMaeInversion().getcPersonaId().getANroDocumento().trim();
            legGastoJudicialList = legalServ.listarGastosJudiciales(legGastoJudicial);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
   public void modificargastojudicial(LegGastoJudicial oLegGastoJudicial){
        try {
            System.out.println("modificargastojudicial");
            mostrarModificaGastoJudicial=true;
            
            this.idLegGastoJudicial= Integer.toString(oLegGastoJudicial.getIdGastoJudicial());
            ArrayList<LegGastoJudicial> lstGasto = new ArrayList<>();
            LegGastoJudicial legGastoJudicialNEW=new LegGastoJudicial();
            System.out.println("tchn:"+ this.getCodigoTCHN());
            System.out.println("fondo:"+ this.getFondo());
            System.out.println("Nroexpediente:"+ this.getNroExpediente());
            
            legGastoJudicialNEW.setCodigoTCHN(this.getCodigoTCHN().trim());
            legGastoJudicialNEW.setFondo(this.getFondo().trim());
            legGastoJudicialNEW.setNroexpediente(this.getNroExpediente().trim());
            legGastoJudicialNEW.setcUsuarioAdd(SessionUtils.getUserName());
            
            System.out.println("etapa:"+oLegGastoJudicial.getEtapa());
            this.etapa=oLegGastoJudicial.getEtapa().trim();
            System.out.println("fechaOperacion:"+oLegGastoJudicial.getFecha().toString());
            this.fechaOperacion=oLegGastoJudicial.getFecha();
            this.comprobanteNumero=oLegGastoJudicial.getNroComprobante();
            this.tipoGastoJudicial=oLegGastoJudicial.getTipoGasto();
            this.montoGastoJudicial=Double.toString(oLegGastoJudicial.getMonto());
            this.comentario=oLegGastoJudicial.getComentario();
            
            lstGasto.add(legGastoJudicialNEW);
             
            legGastoJudicialList2=lstGasto;
         } catch (Exception e) {
        }
    }
    
    public void nuevoGastoJudicial(LegalTchn olegTchn2 ){
        try {
            mostrarAddGastoJudicial=true;
            
            ArrayList<LegGastoJudicial> lstGasto = new ArrayList<>();
            LegGastoJudicial legGastoJudicialNEW=new LegGastoJudicial();
            System.out.println("tchn:"+ this.getCodigoTCHN());
            System.out.println("fondo:"+ this.getFondo());
            System.out.println("Nroexpediente:"+ this.getNroExpediente());
            
            legGastoJudicialNEW.setCodigoTCHN(this.getCodigoTCHN().trim());
            legGastoJudicialNEW.setFondo(this.getFondo().trim());
            legGastoJudicialNEW.setNroexpediente(this.getNroExpediente().trim());
            legGastoJudicialNEW.setcUsuarioAdd(SessionUtils.getUserName());
            
            this.etapa="";
            this.fechaOperacion=null;
            this.comprobanteNumero="";
            this.tipoGastoJudicial="";
            this.montoGastoJudicial="";
            this.comentario="";
            
            lstGasto.add(legGastoJudicialNEW);
             
            legGastoJudicialList2=lstGasto;
        } catch (Exception e) {
            
        }
     }   
    
    public void listarEtapas() {
        try {

            if (getLegEtapaList() == null) {
                setLegEtapaList(getLegalServ().listarEtapas());
            }
        } catch (Exception e) {

        }
    }    
    
    public void listarTiposGastosJudiciales() {
        try {

            if (getLegTiposGastosJudiList() == null) {
                setLegTiposGastosJudiList(getLegalServ().listarTiposGastosJudiciales());
            }
        } catch (Exception e) {

        }
    }       
    
    public void insertarGastoJudicial(LegGastoJudicial olegGastoJudi){
        int rep=0;
        showMsg = true;
        System.out.println("insertarGastoJudicial");
        try {
            System.out.println("setcUsuarioAdd");
            olegGastoJudi.setcUsuarioAdd(SessionUtils.getUserName());
            
            System.out.println("etapa:"+this.etapa.trim());
            System.out.println("comprobanteNumero:"+this.comprobanteNumero.trim());
            System.out.println("tipoGastoJudicial:"+this.tipoGastoJudicial.trim());
            System.out.println("montoGastoJudicial:"+this.montoGastoJudicial.trim());
            String sComentario="";
            if(this.comentario==null || this.comentario.trim().equals(""))
            {
                sComentario="";
            }
            else
            {
                sComentario= this.comentario.trim();
            }
            System.out.println("comentario"+sComentario);
            
            if (this.etapa.trim().length()>0 && this.comprobanteNumero.trim().length()>0 && this.tipoGastoJudicial.trim().length()>0
                    && this.montoGastoJudicial.trim().length()>0){
               System.out.println("seteando valores a olegGastoJudi");
               olegGastoJudi.setCodigoTCHN(this.codigoTCHN);
               olegGastoJudi.setFondo(this.fondo);
               olegGastoJudi.setNroexpediente(this.nroExpediente);
               olegGastoJudi.setEtapa(this.etapa.trim());
               olegGastoJudi.setFecha(this.getFechaOperacion());
               olegGastoJudi.setNroComprobante(this.comprobanteNumero.trim());
               olegGastoJudi.setTipoGasto(this.tipoGastoJudicial.trim());
               System.out.println("montoGastoJudicial");
               olegGastoJudi.setMonto(Double.parseDouble(this.montoGastoJudicial.trim()));
               olegGastoJudi.setComentario(sComentario);
               olegGastoJudi.setcUsuarioAdd(SessionUtils.getUserName());
               System.out.println(olegGastoJudi.toString());
               
               rep = legalServ.insertarGastoJudicial(olegGastoJudi);
            }
            System.out.println("rep:"+rep);
            if (rep == 0) {
                tipoMsj = "error";
                mensaje = "Error no se grabo Gasto judicial";
            } else {
                tipoMsj = "success";
                mensaje = "Se grabo Gasto judicial";
                rep =0;
                // se setea la pantalla de acuerdo a la respuesta
                LegGastoJudicial legGastoJudicial = new LegGastoJudicial();
                legGastoJudicial.setFondo(this.fondo.trim());
                legGastoJudicial.setCodigoTCHN(this.codigoTCHN.trim());
                legGastoJudicial.setNumeroDocumento(this.nroDocumento);
                System.out.println("refresca legGastoJudicialList");
                legGastoJudicialList = legalServ.listarGastosJudiciales(legGastoJudicial);
                nuevoGastoJudicial(null);
                setMostrarAddGastoJudicial(false);
                System.out.println("oculta mostrarAddGastoJudicial");
            }
        } catch (Exception e) {
            showMsg = true;
            tipoMsj = "error";
            mensaje = "Error al insertar el Gasto Judicial";
            System.out.println(e);
        }
     }    
    
    public void modificarGastoJudicial(LegGastoJudicial olegGastoJudi){
        int rep=0;
        showMsg = true;
        System.out.println("modificarGastoJudicial");
        try {
            System.out.println("etapa:"+this.etapa.trim());
            System.out.println("comprobanteNumero:"+this.comprobanteNumero.trim());
            System.out.println("tipoGastoJudicial:"+this.tipoGastoJudicial.trim());
            System.out.println("montoGastoJudicial:"+this.montoGastoJudicial.trim());
            System.out.println("comentario"+this.comentario.trim());
            
            if (this.etapa.trim().length()>0 && this.comprobanteNumero.trim().length()>0 && this.tipoGastoJudicial.trim().length()>0
                    && this.montoGastoJudicial.trim().length()>0 && this.comentario.trim().length()>0){
               System.out.println("seteando valores a olegGastoJudi");
               olegGastoJudi.setIdGastoJudicial(Integer.parseInt(this.idLegGastoJudicial));//PARA IDENTIFICAR QUE REGISTRO SE TIENE QUE ACTUALIZAR
               olegGastoJudi.setCodigoTCHN(this.codigoTCHN);
               olegGastoJudi.setFondo(this.fondo);
               olegGastoJudi.setNroexpediente(this.nroExpediente);
               olegGastoJudi.setEtapa(this.etapa.trim());
               olegGastoJudi.setFecha(this.getFechaOperacion());
               olegGastoJudi.setNroComprobante(this.comprobanteNumero.trim());
               olegGastoJudi.setTipoGasto(this.tipoGastoJudicial.trim());
               System.out.println("montoGastoJudicial");
               olegGastoJudi.setMonto(Double.parseDouble(this.montoGastoJudicial.trim()));
               olegGastoJudi.setComentario(this.comentario.trim());
               olegGastoJudi.setcUsuarioMod(SessionUtils.getUserName());
               System.out.println(olegGastoJudi.toString());
               
               rep = legalServ.modificarGastoJudicial(olegGastoJudi);
            }
            System.out.println("rep:"+rep);
            if (rep == 0) {
                tipoMsj = "error";
                mensaje = "Error no se grabo Gasto judicial";
            } else {
                tipoMsj = "success";
                mensaje = "Se grabo Gasto judicial";
                rep =0;
                // se setea la pantalla de acuerdo a la respuesta
                LegGastoJudicial legGastoJudicial = new LegGastoJudicial();
                legGastoJudicial.setFondo(this.fondo.trim());
                legGastoJudicial.setCodigoTCHN(this.codigoTCHN.trim());
                legGastoJudicial.setNumeroDocumento(this.nroDocumento);
                System.out.println("refresca legGastoJudicialList");
                legGastoJudicialList = legalServ.listarGastosJudiciales(legGastoJudicial);
                setMostrarModificaGastoJudicial(false);
                System.out.println("oculta MostrarModificaGastoJudicial");
            }
        } catch (Exception e) {
            showMsg = true;
            tipoMsj = "error";
            mensaje = "Error al insertar el Gasto Judicial";
            System.out.println(e);
        }
     }        

   public void buscarConsultaGastos() {
        try {
            mensajeRpta="";
            int numeroNulos=0;
            if(this.codigoTCHN==null || this.codigoTCHN.trim().equals(""))
            {
                System.out.println("codigoTCHN, null");
                ++numeroNulos;
            }
            if(this.fondo==null || this.fondo.trim().equals(""))
            {
                System.out.println("fondo, null");
                ++numeroNulos;
            }
            if(this.nroDocumento==null || this.nroDocumento.trim().equals(""))
            {
                System.out.println("nroDocumento, null");
                ++numeroNulos;
            }
            
            //evaluando cuando hacer la consulta
            if(numeroNulos>2)
            {
                mensajeRpta="Debe seleccionar al menos un filtro";
                legGastoJudicialList.clear();
            }
            else if(!(this.fondo==null || this.fondo.trim().equals("")))
            {
                if(numeroNulos>=2)
                {
                    mensajeRpta="Debe seleccionar al menos un filtro mas";
                    legGastoJudicialList.clear();
                }
                else
                {
                    LegGastoJudicial legGastoJudicial= new LegGastoJudicial();
                    legGastoJudicial.setFondo(this.fondo);
                    legGastoJudicial.setCodigoTCHN(this.codigoTCHN);
                    legGastoJudicial.setNumeroDocumento(this.nroDocumento);
                    legGastoJudicialList = legalServ.listarConsultaGastos(legGastoJudicial);
                }
            }
            else
            {
                LegGastoJudicial legGastoJudicial= new LegGastoJudicial();
                legGastoJudicial.setFondo(this.fondo);
                legGastoJudicial.setCodigoTCHN(this.codigoTCHN);
                legGastoJudicial.setNumeroDocumento(this.nroDocumento);
                legGastoJudicialList = legalServ.listarConsultaGastos(legGastoJudicial);
            }
        } catch (Exception e) {

        }
    }
    
    public void cerrarPantallaInsertarGasto(){
 
        try {
            this.setMostrarAddGastoJudicial(false);
        } catch (Exception e) {
                 
        }
       
    }
    
    public void cerrarPantallaModificarGasto(){
 
        try {
            this.setMostrarModificaGastoJudicial(false);
            this.setMostrarAddGastoJudicial(false);
        } catch (Exception e) {
                 
        }
       
    }    
    
   public void listarMateriaOtrosProcesos() {
        try {
            if (legMateriaOtroProcesoList == null) {
                legMateriaOtroProcesoList = getLegalServ().listarMateriaOtrosProcesos();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
   
   public void listarTipoOtrosProcesos() {
        try {
            if (legTipoOtroProcesoList == null) {
                legTipoOtroProcesoList = getLegalServ().listarTipoOtrosProcesos();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }   

    public void buscarOtrosProcesos() {
        try {
            mensajeRpta="";
            this.setMostrarAddOtroProceso(false);
            this.setMostrarModiOtroProceso(false);
            mostrarAddSgtoOtroProceso=false;
            mostrarVerSgtosOtroProceso=false;
            
            int numeroNulos=0;
            String numeroTCHN="";
            if(this.getMaeInversion().getCInversion()==null || this.getMaeInversion().getCInversion().trim().equals(""))
            {
                System.out.println("getCInversion, null");
                ++numeroNulos;
            }
            else numeroTCHN=this.getMaeInversion().getCInversion().trim();
            
            String fondo="";
            if(this.getMaeFondo().getCFondoId()==null || this.getMaeFondo().getCFondoId().trim().equals(""))
            {
                System.out.println("getCFondoId, null");
                ++numeroNulos;
            }else fondo=this.getMaeFondo().getCFondoId().trim();
            
            String numeroDocumento="";
            if(this.getMaeInversion().getcPersonaId().getANroDocumento()==null || this.getMaeInversion().getcPersonaId().getANroDocumento().trim().equals(""))
            {
                System.out.println("getANroDocumento, null");
                ++numeroNulos;
            }else numeroDocumento=this.getMaeInversion().getcPersonaId().getANroDocumento().trim();
            
                        
            //evaluando cuando hacer la consulta
            if(numeroNulos>2)
            {
                System.out.println("buscarOtrosProcesos 1");
                mensajeRpta="Debe seleccionar al menos un filtro";
                legalOtroProcesoList.clear();
            }
            else if(!(maeFondo.getCFondoId()==null || maeFondo.getCFondoId().trim().equals("")))
            {
                if(numeroNulos>=2)
                {
                    System.out.println("buscarOtrosProcesos 2");
                    mensajeRpta="Debe seleccionar al menos un filtro mas";
                    legalOtroProcesoList.clear();
                }
                else
                {
                    System.out.println("buscarOtrosProcesos 3");
                    LegOtroProceso oLegOtroProceso=new LegOtroProceso();
                    oLegOtroProceso.setCodigoTCHN(this.getMaeInversion().getCInversion().trim());
                    oLegOtroProceso.setFondo(this.getMaeFondo().getCFondoId().trim());
                    oLegOtroProceso.setNroDocumento(this.getMaeInversion().getcPersonaId().getANroDocumento().trim());
                    legalOtroProcesoList = legalServ.listarOtrosProcesos(oLegOtroProceso);
                }
            }
            else
            {
                System.out.println("buscarOtrosProcesos 4");
                LegOtroProceso oLegOtroProceso=new LegOtroProceso();
                oLegOtroProceso.setCodigoTCHN(numeroTCHN);
                oLegOtroProceso.setFondo(fondo);
                oLegOtroProceso.setNroDocumento(numeroDocumento);
                legalOtroProcesoList = legalServ.listarOtrosProcesos(oLegOtroProceso);
            }
        } catch (Exception e) {
            
        }
    }
    
    public void nuevoOtroProceso(LegOtroProceso oLegOtroProceso){
        try {
            mostrarAddOtroProceso=true;
            mostrarAddSgtoOtroProceso=false;
            mostrarVerSgtosOtroProceso=false;
                                   
            this.demandado=oLegOtroProceso.getDescFondo();
            this.demandante=oLegOtroProceso.getApellidoPat().trim()+" "+oLegOtroProceso.getApellidoMat().trim()+", "+oLegOtroProceso.getNombres();
            this.codigoTCHN=oLegOtroProceso.getCodigoTCHN();
            this.fondo=oLegOtroProceso.getFondo();
            this.materia ="";
            this.tipoOtroProceso="";
            this.nroExpediente = "";
            this.organoCompetente="";
            this.especialista="";
            this.fechaOperacion=null;
            this.descripcionOtroProceso="";
            this.usuario=SessionUtils.getUserName();
            
        } catch (Exception e) {
            
        }
     }       
   
    public void cerrarPantallaInsertarOtroProd(){
 
        try {
            this.setMostrarAddOtroProceso(false);
            this.setMostrarModiOtroProceso(false);
            mostrarAddSgtoOtroProceso=false;
            mostrarVerSgtosOtroProceso=false;
        } catch (Exception e) {
                 
        }
    }    
    
    public void insertarOtroProceso(){
        int rep=0;
        showMsg = true;
        System.out.println("insertarOtroProceso");
        try {
            System.out.println("setcUsuarioAdd");
            LegOtroProceso oLegOtroProceso = new LegOtroProceso();
            oLegOtroProceso.setCodigoTCHN(this.codigoTCHN);
            oLegOtroProceso.setFondo(this.fondo);
            oLegOtroProceso.setcUsuarioAdd(SessionUtils.getUserName());
            oLegOtroProceso.setEstado("01");//01:NUEVO
            oLegOtroProceso.setMateria(this.materia);
            oLegOtroProceso.setTipo(this.tipoOtroProceso);
            oLegOtroProceso.setNroexp(this.nroExpediente);
            oLegOtroProceso.setOrganocompetente(this.organoCompetente);
            oLegOtroProceso.setEspecialista(this.especialista);
            oLegOtroProceso.setFecha(this.fechaOperacion);
            oLegOtroProceso.setDescripcion(this.descripcionOtroProceso);
            
            String sDescripcion="";
            if(this.descripcionOtroProceso==null || this.descripcionOtroProceso.trim().equals(""))
            {
                sDescripcion="";
            }
            else
            {
                sDescripcion=this.descripcionOtroProceso.trim();
            }
            System.out.println("descripcionOtroProceso"+sDescripcion);
            
            if (this.materia.trim().length()>0 && this.tipoOtroProceso.trim().length()>0 && this.nroExpediente.trim().length()>0
                    && this.organoCompetente.trim().length()>0 && this.especialista.trim().length()>0 && this.descripcionOtroProceso.trim().length()>0){
               System.out.println("seteando valores a oLegOtroProceso");
               System.out.println(oLegOtroProceso.toString());
               
               rep = legalServ.insertarOtroProceso(oLegOtroProceso);
            }
            System.out.println("rep:"+rep);
            if (rep == 0) {
                tipoMsj = "error";
                mensaje = "Error no se grabo Otro proceso judicial";
            } else {
                tipoMsj = "success";
                mensaje = "Se grabo Otro proceso judicial";
                rep =0;
                // se setea la pantalla de acuerdo a la respuesta
                
                /*LegOtroProceso oLegOtroProceso2=new LegOtroProceso();
                oLegOtroProceso2.setCodigoTCHN(this.getMaeInversion().getCInversion().trim());
                oLegOtroProceso2.setFondo(this.getMaeFondo().getCFondoId().trim());
                oLegOtroProceso2.setNroDocumento(this.getMaeInversion().getcPersonaId().getANroDocumento().trim());
                System.out.println("refresca legalOtroProcesoList");
                legalOtroProcesoList = legalServ.listarOtrosProcesos(oLegOtroProceso2);*/
                buscarOtrosProcesos();
                
                nuevoOtroProceso(oLegOtroProceso);
                setMostrarAddOtroProceso(false);
                setMostrarAddSgtoOtroProceso(false);
                mostrarAddSgtoOtroProceso=false;
                mostrarVerSgtosOtroProceso=false;
                System.out.println("oculta setMostrarAddOtroProceso");
            }
        } catch (Exception e) {
            showMsg = true;
            tipoMsj = "error";
            mensaje = "Error al insertar Otro proceso judicial";
            System.out.println(e);
        }
     }
    
    public void modificarOtroProceso(){
         int rep=0;
        showMsg = true;
        System.out.println("modificarOtroProceso");
        try {
            LegOtroProceso oLegOtroProceso = new LegOtroProceso();
            oLegOtroProceso.setIdLegOtroProceso(this.idOtroProceso);
            oLegOtroProceso.setCodigoTCHN(this.codigoTCHN);
            oLegOtroProceso.setFondo(this.fondo);
            oLegOtroProceso.setcUsuarioMod(SessionUtils.getUserName());
            oLegOtroProceso.setMateria(this.materia);
            oLegOtroProceso.setTipo(this.tipoOtroProceso);
            oLegOtroProceso.setNroexp(this.nroExpediente);
            oLegOtroProceso.setOrganocompetente(this.organoCompetente);
            oLegOtroProceso.setEspecialista(this.especialista);
            oLegOtroProceso.setFecha(this.fechaOperacion);
            oLegOtroProceso.setDescripcion(this.descripcionOtroProceso);
            
            String sDescripcion="";
            if(this.descripcionOtroProceso==null || this.descripcionOtroProceso.trim().equals(""))
            {
                sDescripcion="";
            }
            else
            {
                sDescripcion=this.descripcionOtroProceso.trim();
            }
            System.out.println("descripcionOtroProceso"+sDescripcion);
            
            if (this.materia.trim().length()>0 && this.tipoOtroProceso.trim().length()>0 && this.nroExpediente.trim().length()>0
                    && this.organoCompetente.trim().length()>0 && this.especialista.trim().length()>0 && this.descripcionOtroProceso.trim().length()>0){
               System.out.println("seteando valores a oLegOtroProceso");
               System.out.println(oLegOtroProceso.toString());
               
               rep = legalServ.modificarOtroProceso(oLegOtroProceso);
            }
            System.out.println("rep:"+rep);
            if (rep == 0) {
                tipoMsj = "error";
                mensaje = "Error no se modificó Otro proceso judicial";
            } else {
                tipoMsj = "success";
                mensaje = "Se mdificó Otro proceso judicial";
                rep =0;
                // se setea la pantalla de acuerdo a la respuesta
                /*LegOtroProceso oLegOtroProceso2=new LegOtroProceso();
                oLegOtroProceso2.setCodigoTCHN(this.getMaeInversion().getCInversion().trim());
                oLegOtroProceso2.setFondo(this.getMaeFondo().getCFondoId().trim());
                oLegOtroProceso2.setNroDocumento(this.getMaeInversion().getcPersonaId().getANroDocumento().trim());
                System.out.println("refresca legalOtroProcesoList");
                legalOtroProcesoList = legalServ.listarOtrosProcesos(oLegOtroProceso2);*/
                buscarOtrosProcesos();
                nuevoOtroProceso(oLegOtroProceso);
                setMostrarAddOtroProceso(false);
                setMostrarModiOtroProceso(false);
                mostrarAddSgtoOtroProceso=false;
                mostrarVerSgtosOtroProceso=false;
                System.out.println("oculta setMostrarAddOtroProceso");
            }
        } catch (Exception e) {
            showMsg = true;
            tipoMsj = "error";
            mensaje = "Error al modificar Otro proceso judicial";
            System.out.println(e);
        }
     }     
    
    public void modificaOtroProceso(LegOtroProceso oLegOtroProceso){
        try {
            mostrarAddOtroProceso=false;
            mostrarModiOtroProceso=true;
            mostrarAddSgtoOtroProceso=false;
            mostrarVerSgtosOtroProceso=false;
            
            this.demandado=oLegOtroProceso.getDescFondo();
            this.demandante=oLegOtroProceso.getApellidoPat().trim()+" "+oLegOtroProceso.getApellidoMat().trim()+", "+oLegOtroProceso.getNombres();
            this.codigoTCHN=oLegOtroProceso.getCodigoTCHN();
            this.fondo=oLegOtroProceso.getFondo();
            this.idOtroProceso=oLegOtroProceso.getIdLegOtroProceso();
            this.materia =oLegOtroProceso.getMateria();
            this.tipoOtroProceso=oLegOtroProceso.getTipo();
            this.nroExpediente = oLegOtroProceso.getNroexp();
            this.organoCompetente=oLegOtroProceso.getOrganocompetente();
            this.especialista=oLegOtroProceso.getEspecialista();
            this.fechaOperacion=oLegOtroProceso.getFecha();
            this.descripcionOtroProceso=oLegOtroProceso.getDescripcion();
            this.usuario=SessionUtils.getUserName();
            
        } catch (Exception e) {
            
        }
     }          
    
    public void cerrarPantallaModificarOtroProd(){
 
        try {
            this.setMostrarAddOtroProceso(false);
            this.setMostrarModiOtroProceso(false);
            mostrarAddSgtoOtroProceso=false;
            mostrarVerSgtosOtroProceso=false;
        } catch (Exception e) {
                 
        }
    }
    
    public void nuevoSgtoOtroProceso(LegOtroProceso oLegOtroProceso){
        try {
            System.out.println("nuevoSgtoOtroProceso");
            mostrarAddOtroProceso=false;
            mostrarModiOtroProceso=false;
            mostrarAddSgtoOtroProceso=false;
            mostrarVerSgtosOtroProceso=true;
            
            this.fechaOperacion=null;
            this.descripcionOtroProceso="";
            this.usuario=SessionUtils.getUserName();
            
            this.idOtroProceso=oLegOtroProceso.getIdLegOtroProceso();
            System.out.println("idLegOtroProceso:"+oLegOtroProceso.getIdLegOtroProceso());
            listarSeguimientosOtros(oLegOtroProceso);
            
        } catch (Exception e) {
            
        }
     }
    
   public void listarSeguimientosOtros(LegOtroProceso oLegOtroProceso) {
        try {
            System.out.println("listarSeguimientosOtros");
            mostrarAddOtroProceso=false;
            mostrarModiOtroProceso=false;
            mostrarAddSgtoOtroProceso=false;
            mostrarVerSgtosOtroProceso=true;
            System.out.println("idLegOtroProceso:"+oLegOtroProceso.getIdLegOtroProceso());
            legalSgtoOtroProcesoList = null;
            LegSgtoOtroProceso oLegSgtoOtroProceso=new LegSgtoOtroProceso();
            oLegSgtoOtroProceso.setIdLegOtroProceso(oLegOtroProceso.getIdLegOtroProceso());
            this.idOtroProceso=oLegOtroProceso.getIdLegOtroProceso();
            legalSgtoOtroProcesoList = getLegalServ().listarSeguimientosOtros(oLegSgtoOtroProceso);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
   
    public void insertarSgtoOtroProceso(){
        int rep=0;
        showMsg = true;
        System.out.println("insertarSgtoOtroProceso");
        try {
            LegSgtoOtroProceso oLegSgtoOtroProceso = new LegSgtoOtroProceso();
            oLegSgtoOtroProceso.setFecha(this.fechaOperacion);
            oLegSgtoOtroProceso.setDescripcion(this.descripcionOtroProceso);
            
            String sDescripcion="";
            if(this.descripcionOtroProceso==null || this.descripcionOtroProceso.trim().equals(""))
            {
                sDescripcion="";
            }
            else
            {
                sDescripcion=this.descripcionOtroProceso.trim();
            }
            System.out.println("descripcionOtroProceso"+sDescripcion);
            
            if (this.descripcionOtroProceso.trim().length()>0){
               System.out.println("seteando valores a oLegSgtoOtroProceso");
               System.out.println(oLegSgtoOtroProceso.toString());
               oLegSgtoOtroProceso.setIdLegOtroProceso(this.idOtroProceso);
               oLegSgtoOtroProceso.setcUsuarioAdd(SessionUtils.getUserName());
               rep = legalServ.insertarSgtoOtroProceso(oLegSgtoOtroProceso);
            }
            System.out.println("rep:"+rep);
            if (rep == 0) {
                tipoMsj = "error";
                mensaje = "Error no se grabo Seguimiento de otro proceso judicial";
            } else {
                tipoMsj = "success";
                mensaje = "Se grabo Seguimiento de otro proceso judicial";
                rep =0;
                // se setea la pantalla de acuerdo a la respuesta
                LegOtroProceso oLegOtroProceso=new LegOtroProceso();
                oLegOtroProceso.setIdLegOtroProceso(this.idOtroProceso);
                listarSeguimientosOtros(oLegOtroProceso);
                
                setMostrarAddOtroProceso(false);
                setMostrarAddSgtoOtroProceso(false);
                mostrarAddSgtoOtroProceso=false;
                mostrarVerSgtosOtroProceso=true;
            }
        } catch (Exception e) {
            showMsg = true;
            tipoMsj = "error";
            mensaje = "Error al insertar seguimiento de Otro proceso judicial";
            System.out.println(e);
        }
     }
    
    public void nuevoSgtoOtroProceso(){
        try {
            mostrarAddOtroProceso=false;
            mostrarModiOtroProceso=false;
            mostrarAddSgtoOtroProceso=true;
            mostrarVerSgtosOtroProceso=true;
            
            this.fechaOperacion=null;
            this.descripcionOtroProceso="";
            this.usuario=SessionUtils.getUserName();
              
        } catch (Exception e) {
            
        }
     }
    
    public void cerrarPantallaInsertarSgtoOtroProd(){
 
        try {
            mostrarAddSgtoOtroProceso=false;
        } catch (Exception e) {
                 
        }
    }        
    
    public void cerrarPantallaModiSgtoOtroProd(){
 
        try {
            mostrarModiSgtoOtroProceso=false;
        } catch (Exception e) {
                 
        }
    }
    
    public void modificaSgtoOtroProceso(LegSgtoOtroProceso oLegSgtoOtroProceso){
        try {
            System.out.println("modificaSgtoOtroProceso");
            mostrarAddOtroProceso=false;
            mostrarModiOtroProceso=false;
            mostrarAddSgtoOtroProceso=false;
            mostrarVerSgtosOtroProceso=true;
            mostrarModiSgtoOtroProceso=true;
            
            this.fechaOperacion=oLegSgtoOtroProceso.getFecha();
            this.descripcionOtroProceso=oLegSgtoOtroProceso.getDescripcion();
            this.usuario=SessionUtils.getUserName();
            this.idOtroProceso=oLegSgtoOtroProceso.getIdLegOtroProceso();
            this.idSegOtroProceso = oLegSgtoOtroProceso.getIdLegSeguimiento();
            
            System.out.println("idSegOtroProceso:"+oLegSgtoOtroProceso.getIdLegSeguimiento());
        } catch (Exception e) {
            
        }
     }    
    
    public void modificarSgtoOtroProceso(){
        int rep=0;
        showMsg = true;
        System.out.println("modificarSgtoOtroProceso");
        try {
            LegSgtoOtroProceso oLegSgtoOtroProceso = new LegSgtoOtroProceso();
            oLegSgtoOtroProceso.setIdLegSeguimiento(this.idSegOtroProceso);
            oLegSgtoOtroProceso.setFecha(this.fechaOperacion);
            oLegSgtoOtroProceso.setDescripcion(this.descripcionOtroProceso);
            oLegSgtoOtroProceso.setcUsuarioMod(SessionUtils.getUserName());
            
            String sDescripcion="";
            if(this.descripcionOtroProceso==null || this.descripcionOtroProceso.trim().equals(""))
            {
                sDescripcion="";
            }
            else
            {
                sDescripcion=this.descripcionOtroProceso.trim();
            }
            System.out.println("descripcionOtroProceso"+sDescripcion);
            
            if (this.descripcionOtroProceso.trim().length()>0){
               System.out.println("seteando valores a oLegSgtoOtroProceso");
               System.out.println(oLegSgtoOtroProceso.toString());
               rep = legalServ.modificarSgtoOtroProceso(oLegSgtoOtroProceso);
            }
            System.out.println("rep:"+rep);
            if (rep == 0) {
                tipoMsj = "error";
                mensaje = "Error no se grabo Seguimiento de otro proceso judicial";
            } else {
                tipoMsj = "success";
                mensaje = "Se grabo Seguimiento de otro proceso judicial";
                rep =0;
                // se setea la pantalla de acuerdo a la respuesta
                LegOtroProceso oLegOtroProceso=new LegOtroProceso();
                oLegOtroProceso.setIdLegOtroProceso(this.idOtroProceso);
                listarSeguimientosOtros(oLegOtroProceso);
                
                setMostrarAddOtroProceso(false);
                setMostrarAddSgtoOtroProceso(false);
                mostrarAddSgtoOtroProceso=false;
                mostrarVerSgtosOtroProceso=true;
                mostrarModiSgtoOtroProceso=false;
            }
        } catch (Exception e) {
            showMsg = true;
            tipoMsj = "error";
            mensaje = "Error al modificar seguimiento de Otro proceso judicial";
            System.out.println(e);
        }
    }    
    
   public void consultaSeguimientosOtros(LegOtroProceso oLegOtroProceso) {
        try {
            System.out.println("consultaSeguimientosOtros");
            mostrarAddOtroProceso=false;
            mostrarModiOtroProceso=false;
            mostrarAddSgtoOtroProceso=false;
            mostrarVerSgtosOtroProceso=true;
            System.out.println("idLegOtroProceso:"+oLegOtroProceso.getIdLegOtroProceso());
            legalSgtoOtroProcesoList = null;
            LegSgtoOtroProceso oLegSgtoOtroProceso=new LegSgtoOtroProceso();
            oLegSgtoOtroProceso.setIdLegOtroProceso(oLegOtroProceso.getIdLegOtroProceso());
            this.idOtroProceso=oLegOtroProceso.getIdLegOtroProceso();
            legalSgtoOtroProcesoList = getLegalServ().consultaSeguimientosOtros(oLegSgtoOtroProceso);
        } catch (Exception e) {
            System.out.println(e);
        }
    }    
    
    public MaePersona getMaePersona() {
        return maePersona;
    }

    public void setMaePersona(MaePersona maePersona) {
        this.maePersona = maePersona;
    }

    public MaeInversion getMaeInversion() {
        return maeInversion;
    }

    public void setMaeInversion(MaeInversion maeInversion) {
        this.maeInversion = maeInversion;
    }

    public MaeFondo getMaeFondo() {
        return maeFondo;
    }

    public void setMaeFondo(MaeFondo maeFondo) {
        this.maeFondo = maeFondo;
    }

    public MaeEstadoCuenta getMaeEstadoCuenta() {
        return maeEstadoCuenta;
    }

    public void setMaeEstadoCuenta(MaeEstadoCuenta maeEstadoCuenta) {
        this.maeEstadoCuenta = maeEstadoCuenta;
    }

    public List<MaeEstadoCuenta> getMaeEstadoCuentaList() {
        return maeEstadoCuentaList;
    }

    public void setMaeEstadoCuentaList(List<MaeEstadoCuenta> maeEstadoCuentaList) {
        this.maeEstadoCuentaList = maeEstadoCuentaList;
    }

    public EstadoCuentaServ getEstadoCuentaServ() {
        return estadoCuentaServ;
    }

    public void setEstadoCuentaServ(EstadoCuentaServ estadoCuentaServ) {
        this.estadoCuentaServ = estadoCuentaServ;
    }

    public List<MaeEstadoCuenta> getMaeEstadoCuentaBList() {
        return maeEstadoCuentaBList;
    }

    public void setMaeEstadoCuentaBList(List<MaeEstadoCuenta> maeEstadoCuentaBList) {
        this.maeEstadoCuentaBList = maeEstadoCuentaBList;
    }

    public List<MaeDeposito> getMaeEstadoCuentaCList() {
        return maeEstadoCuentaCList;
    }

    public void setMaeEstadoCuentaCList(List<MaeDeposito> maeEstadoCuentaCList) {
        this.maeEstadoCuentaCList = maeEstadoCuentaCList;
    }

    public IRepEstadoCuentaServ getRepEstadoCuentaServ() {
        return repEstadoCuentaServ;
    }

    public void setRepEstadoCuentaServ(IRepEstadoCuentaServ repEstadoCuentaServ) {
        this.repEstadoCuentaServ = repEstadoCuentaServ;
    }

    public MaeInversion getMaeInversionP() {
        return maeInversionP;
    }

    public void setMaeInversionP(MaeInversion maeInversionP) {
        this.maeInversionP = maeInversionP;
    }

    public ICorreoServ getCorreoServ() {
        return correoServ;
    }

    public void setCorreoServ(ICorreoServ correoServ) {
        this.correoServ = correoServ;
    }

    public IEmailServ getEmailServ() {
        return emailServ;
    }

    public void setEmailServ(IEmailServ emailServ) {
        this.emailServ = emailServ;
    }

    public IRepSaldoDeudorServ getRepSaldoDeudorServ() {
        return repSaldoDeudorServ;
    }

    public void setRepSaldoDeudorServ(IRepSaldoDeudorServ repSaldoDeudorServ) {
        this.repSaldoDeudorServ = repSaldoDeudorServ;
    }

    public IInversionServ getInversionServ() {
        return inversionServ;
    }

    public void setInversionServ(IInversionServ inversionServ) {
        this.inversionServ = inversionServ;
    }

    public MaeUbigeo getMaeUbigeo() {
        return maeUbigeo;
    }

    public void setMaeUbigeo(MaeUbigeo maeUbigeo) {
        this.maeUbigeo = maeUbigeo;
    }

    public MaeUbigeo getMaeUbigeoP() {
        return maeUbigeoP;
    }

    public void setMaeUbigeoP(MaeUbigeo maeUbigeoP) {
        this.maeUbigeoP = maeUbigeoP;
    }

    public MaeUbigeo getMaeUbigeoD() {
        return maeUbigeoD;
    }

    public void setMaeUbigeoD(MaeUbigeo maeUbigeoD) {
        this.maeUbigeoD = maeUbigeoD;
    }

    public MaeInmueble getMaeInmueble() {
        return maeInmueble;
    }

    public void setMaeInmueble(MaeInmueble maeInmueble) {
        this.maeInmueble = maeInmueble;
    }

    public List<MaeFondo> getMaeFondoList() {
        return maeFondoList;
    }

    public void setMaeFondoList(List<MaeFondo> maeFondoList) {
        this.maeFondoList = maeFondoList;
    }

    public IFondoServ getFondoServ() {
        return fondoServ;
    }

    public void setFondoServ(IFondoServ fondoServ) {
        this.fondoServ = fondoServ;
    }

    /**
     * @return the iShowButtonsLiqJudic
     */
    public int getiShowButtonsLiqJudic() {
        return iShowButtonsLiqJudic;
    }

    /**
     * @param iShowButtonsLiqJudic the iShowButtonsLiqJudic to set
     */
    public void setiShowButtonsLiqJudic(int iShowButtonsLiqJudic) {
        this.iShowButtonsLiqJudic = iShowButtonsLiqJudic;
    }
    
 
    public List<LegalTchn> getLegalTchnList() {
        return legalTchnList;
    }

    public void setLegalTchnList(List<LegalTchn> legalTchnList) {
        this.legalTchnList = legalTchnList;
    }
 
    public LegalTchn getLegTchn() {
        return legTchn;
    }

    public void setLegTchn(LegalTchn legTchn) {
        this.legTchn = legTchn;
    } 
    
    public LegDemanTchn getLegDemanTchn() {
        return legDemanTchn;
    }

    public void setLegDemanTchn(LegDemanTchn legDemanTchn) {
        this.legDemanTchn = legDemanTchn;
    }

    public List<LegDemanTchn> getLegDemanTchnList() {
        return legDemanTchnList;
    }

    public void setLegDemanTchnList(List<LegDemanTchn> legDemanTchnList) {
        this.legDemanTchnList = legDemanTchnList;
    }
    
     public List<LegalTchn> getLegalTchnList2() {
        return legalTchnList2;
    }

    public void setLegalTchnList2(List<LegalTchn> legalTchnList2) {
        this.legalTchnList2 = legalTchnList2;
    }   
    
    
    public List<LegSeguiTchn> getLegSeguiTchnList() {
        return legSeguiTchnList;
    }

    public void setLegSeguiTchnList(List<LegSeguiTchn> legSeguiTchnList) {
        this.legSeguiTchnList = legSeguiTchnList;
    }
    
     public LegSeguiTchn getLegSeguiTchn() {
        return legSeguiTchn;
    }

    public void setLegSeguiTchn(LegSeguiTchn legSeguiTchn) {
        this.legSeguiTchn = legSeguiTchn;
    }
    
    public List<LegSeguiTchn> getLegSeguiTchnList2() {
        return legSeguiTchnList2;
    }

    public void setLegSeguiTchnList2(List<LegSeguiTchn> legSeguiTchnList2) {
        this.legSeguiTchnList2 = legSeguiTchnList2;
    }
     
    public MaeEstadoLegal getMaeEstadoLegal() {
        return maeEstadoLegal;
    }

    public void setMaeEstadoLegal(MaeEstadoLegal maeEstadoLegal) {
        this.maeEstadoLegal = maeEstadoLegal;
    }
    
 public List<MaeEstadoLegal> getmaeEstadoLegalList() {
        return maeEstadoLegalList;
    }

    public void setMaeEstadoLegalList(List<MaeEstadoLegal> maeEstadoLegalList) {
        this.maeEstadoLegalList = maeEstadoLegalList;
    }
 
    
     public boolean isShowMsg() {
        return showMsg;
    }

    public void setShowMsg(boolean showMsg) {
        this.showMsg = showMsg;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipoMsj() {
        return tipoMsj;
    }

    public void setTipoMsj(String tipoMsj) {
        this.tipoMsj = tipoMsj;
    }
    
    public List<LegalTchn> getLegalTchnList3() {
        return legalTchnList3;
    }

    public void setLegalTchnList3(List<LegalTchn> legalTchnList3) {
        this.legalTchnList3 = legalTchnList3;
    }
    
    
    public List<MaeEstadoLegal> getMaeJuzgadoLegalList() {
        return maeJuzgadoLegalList;
    }

    public void setMaeJuzgadoLegalList(List<MaeEstadoLegal> maeJuzgadoLegalList) {
        this.maeJuzgadoLegalList = maeJuzgadoLegalList;
    }

    public List<MaeEstadoLegal> getMaeEspecialLegalList() {
        return maeEspecialLegalList;
    }

    public void setMaeEspecialLegalList(List<MaeEstadoLegal> maeEspecialLegalList) {
        this.maeEspecialLegalList = maeEspecialLegalList;
    }
    
    
    public List<LegalTchn> getLegalTchnListEt3() {
        return legalTchnListEt3;
    }

    public void setLegalTchnListEt3(List<LegalTchn> legalTchnListEt3) {
        this.legalTchnListEt3 = legalTchnListEt3;
    }
 
    public List<MaeEstadoLegal> getMaeEtapaLegalList() {
        return maeEtapaLegalList;
    }

    public void setMaeEtapaLegalList(List<MaeEstadoLegal> maeEtapaLegalList) {
        this.maeEtapaLegalList = maeEtapaLegalList;
    }
      
  public String getData0() {
        return data0;
    }

    public void setData0(String data0) {
        this.data0 = data0;
    }

    public String getDataA() {
        return dataA;
    }

    public void setDataA(String dataA) {
        this.dataA = dataA;
    }

    public String getDataB() {
        return dataB;
    }

    public void setDataB(String dataB) {
        this.dataB = dataB;
    }

    public String getDataC() {
        return dataC;
    }

    public void setDataC(String dataC) {
        this.dataC = dataC;
    }

    public String getDataD() {
        return dataD;
    }

    public void setDataD(String dataD) {
        this.dataD = dataD;
    }   
 
    public int getiJudi() {
        return iJudi;
    }

    public void setiJudi(int iJudi) {
        this.iJudi = iJudi;
    }

    public int getiCali() {
        return iCali;
    }

    public void setiCali(int iCali) {
        this.iCali = iCali;
    }

    public int getiTot() {
        return iTot;
    }

    public void setiTot(int iTot) {
        this.iTot = iTot;
    }

    public int getiPC() {
        return iPC;
    }

    public void setiPC(int iPC) {
        this.iPC = iPC;
    }

    public int getiCC() {
        return iCC;
    }

    public void setiCC(int iCC) {
        this.iCC = iCC;
    }

    public int getiTOTC() {
        return iTOTC;
    }

    public void setiTOTC(int iTOTC) {
        this.iTOTC = iTOTC;
    }
 
    public String getDataA1() {
        return dataA1;
    }

    public void setDataA1(String dataA1) {
        this.dataA1 = dataA1;
    }
    
    
     public String getData10() {
        return data10;
    }

    public void setData10(String data10) {
        this.data10 = data10;
    }

    public String getData1A() {
        return data1A;
    }

    public void setData1A(String data1A) {
        this.data1A = data1A;
    }

    public String getData1B() {
        return data1B;
    }

    public void setData1B(String data1B) {
        this.data1B = data1B;
    }

    public String getData1C() {
        return data1C;
    }

    public void setData1C(String data1C) {
        this.data1C = data1C;
    }

    public String getData1D() {
        return data1D;
    }

    public void setData1D(String data1D) {
        this.data1D = data1D;
    }

  public String getData20() {
        return data20;
    }

    public void setData20(String data20) {
        this.data20 = data20;
    }

    public String getData2A() {
        return data2A;
    }

    public void setData2A(String data2A) {
        this.data2A = data2A;
    }

    public String getData2B() {
        return data2B;
    }

    public void setData2B(String data2B) {
        this.data2B = data2B;
    }

    public String getData2C() {
        return data2C;
    }

    public void setData2C(String data2C) {
        this.data2C = data2C;
    }

    public String getData2D() {
        return data2D;
    }

    public void setData2D(String data2D) {
        this.data2D = data2D;
    }  

    
    public List<LegalTchn> getLegalTchnList4() {
        return legalTchnList4;
    }

    public void setLegalTchnList4(List<LegalTchn> legalTchnList4) {
        this.legalTchnList4 = legalTchnList4;
    }
    
    
     public String getData30() {
        return data30;
    }

    public void setData30(String data30) {
        this.data30 = data30;
    }

    public String getData3A() {
        return data3A;
    }

    public void setData3A(String data3A) {
        this.data3A = data3A;
    }

    public String getData3B() {
        return data3B;
    }

    public void setData3B(String data3B) {
        this.data3B = data3B;
    }

    public String getData3C() {
        return data3C;
    }

    public void setData3C(String data3C) {
        this.data3C = data3C;
    }

    public String getData3D() {
        return data3D;
    }

    public void setData3D(String data3D) {
        this.data3D = data3D;
    }
    
    public List<LegalTchn> getLegalTchnList5() {
        return legalTchnList5;
    }

    public void setLegalTchnList5(List<LegalTchn> legalTchnList5) {
        this.legalTchnList5 = legalTchnList5;
    }

    public LegalServ getLegalServ() {
        return legalServ;
    }

    public void setLegalServ(LegalServ legalServ) {
        this.legalServ = legalServ;
    }
    
    public String getData40() {
        return data40;
    }

    public void setData40(String data40) {
        this.data40 = data40;
    }

    public String getData4A() {
        return data4A;
    }

    public void setData4A(String data4A) {
        this.data4A = data4A;
    }
    
    
    public String getData4B() {
        return data4B;
    }

    public void setData4B(String data4B) {
        this.data4B = data4B;
    }

    public String getData4C() {
        return data4C;
    }

    public void setData4C(String data4C) {
        this.data4C = data4C;
    }

    public String getData4D() {
        return data4D;
    }

    public void setData4D(String data4D) {
        this.data4D = data4D;
    }

    
    public String getData4E() {
        return data4E;
    }

    public void setData4E(String data4E) {
        this.data4E = data4E;
    }
 
    
    public MaeAnio getMaeAnio() {
        return maeAnio;
    }

    public void setMaeAnio(MaeAnio maeAnio) {
        this.maeAnio = maeAnio;
    }

    public MaeMes getMaeMes() {
        return maeMes;
    }

    public void setMaeMes(MaeMes maeMes) {
        this.maeMes = maeMes;
    }

    public List<MaeAnio> getMaeAnioList() {
        return maeAnioList;
    }

    public void setMaeAnioList(List<MaeAnio> maeAnioList) {
        this.maeAnioList = maeAnioList;
    }

    public List<MaeMes> getMaeMesList() {
        return maeMesList;
    }

    public void setMaeMesList(List<MaeMes> maeMesList) {
        this.maeMesList = maeMesList;
    }
    
    
    public List<LegalTchn> getLegalTchnList6() {
        return legalTchnList6;
    }

    public void setLegalTchnList6(List<LegalTchn> legalTchnList6) {
        this.legalTchnList6 = legalTchnList6;
    }

    public List<LegalTchn> getLegalTchnList7() {
        return legalTchnList7;
    }

    public void setLegalTchnList7(List<LegalTchn> legalTchnList7) {
        this.legalTchnList7 = legalTchnList7;
    }

    public List<LegalTchn> getLegalTchnList8() {
        return legalTchnList8;
    }

    public void setLegalTchnList8(List<LegalTchn> legalTchnList8) {
        this.legalTchnList8 = legalTchnList8;
    }

    public List<LegalTchn> getLegalTchnList9() {
        return legalTchnList9;
    }

    public void setLegalTchnList9(List<LegalTchn> legalTchnList9) {
        this.legalTchnList9 = legalTchnList9;
    }

    public List<LegalTchn> getLegalTchnList10() {
        return legalTchnList10;
    }

    public void setLegalTchnList10(List<LegalTchn> legalTchnList10) {
        this.legalTchnList10 = legalTchnList10;
    }

    public List<LegalTchn> getLegalTchnList11() {
        return legalTchnList11;
    }

    public void setLegalTchnList11(List<LegalTchn> legalTchnList11) {
        this.legalTchnList11 = legalTchnList11;
    }

    public List<LegalTchn> getLegalTchnList12() {
        return legalTchnList12;
    }

    public void setLegalTchnList12(List<LegalTchn> legalTchnList12) {
        this.legalTchnList12 = legalTchnList12;
    }

  public String getData50() {
        return data50;
    }

    public void setData50(String data50) {
        this.data50 = data50;
    }

    public String getData5A() {
        return data5A;
    }

    public void setData5A(String data5A) {
        this.data5A = data5A;
    }

    public String getData5B() {
        return data5B;
    }

    public void setData5B(String data5B) {
        this.data5B = data5B;
    }

    public String getData5C() {
        return data5C;
    }

    public void setData5C(String data5C) {
        this.data5C = data5C;
    }

    public String getData5D() {
        return data5D;
    }

    public void setData5D(String data5D) {
        this.data5D = data5D;
    }    
    
    public String getAse1() {
        return Ase1;
    }

    public void setAse1(String Ase1) {
        this.Ase1 = Ase1;
    }

    public String getAse2() {
        return Ase2;
    }

    public void setAse2(String Ase2) {
        this.Ase2 = Ase2;
    }

    public String getAse3() {
        return Ase3;
    }

    public void setAse3(String Ase3) {
        this.Ase3 = Ase3;
    }

    public String getAse4() {
        return Ase4;
    }

    public void setAse4(String Ase4) {
        this.Ase4 = Ase4;
    }

    public String getAse5() {
        return Ase5;
    }

    public void setAse5(String Ase5) {
        this.Ase5 = Ase5;
    }

    public String getAse6() {
        return Ase6;
    }

    public void setAse6(String Ase6) {
        this.Ase6 = Ase6;
    }

    public String getAse7() {
        return Ase7;
    }

    public void setAse7(String Ase7) {
        this.Ase7 = Ase7;
    }

    public String getAse8() {
        return Ase8;
    }

    public void setAse8(String Ase8) {
        this.Ase8 = Ase8;
    }

    public String getAse9() {
        return Ase9;
    }

    public void setAse9(String Ase9) {
        this.Ase9 = Ase9;
    }

    public String getAse10() {
        return Ase10;
    }

    public void setAse10(String Ase10) {
        this.Ase10 = Ase10;
    }

    public String getAse11() {
        return Ase11;
    }

    public void setAse11(String Ase11) {
        this.Ase11 = Ase11;
    }
    
    public List<LegalTchn> getLegalTchnList13() {
        return legalTchnList13;
    }

    public void setLegalTchnList13(List<LegalTchn> legalTchnList13) {
        this.legalTchnList13 = legalTchnList13;
    }
    
    public String getData70() {
        return data70;
    }

    public void setData70(String data70) {
        this.data70 = data70;
    }

    public String getData7A() {
        return data7A;
    }

    public void setData7A(String data7A) {
        this.data7A = data7A;
    }

    public String getData7B() {
        return data7B;
    }

    public void setData7B(String data7B) {
        this.data7B = data7B;
    }

    public String getData7C() {
        return data7C;
    }

    public void setData7C(String data7C) {
        this.data7C = data7C;
    }

    public String getData7D() {
        return data7D;
    }

    public void setData7D(String data7D) {
        this.data7D = data7D;
    }
    
    public List<CobTablas> getCobTablasEstInvList() {
        return cobTablasEstInvList;
    }

    public void setCobTablasEstInvList(List<CobTablas> cobTablasEstInvList) {
        this.cobTablasEstInvList = cobTablasEstInvList;
    }

 public void obtenerParam() {
        try {
       
             if (cobTablasEstInvList == null) {
                cobTablasEstInv.setCtablaId("0620");
                cobTablasEstInvList = iCobTablasServ.listarTablas(cobTablasEstInv);
            }
            //System.out.println(" cantidad de Fondos -> " + maeFondoList.size());
        } catch (Exception e) {

        }
     }   
 
     public CobTablas getCobTablasEstInv() {
        return cobTablasEstInv;
    }

    public void setCobTablasEstInv(CobTablas cobTablasEstInv) {
        this.cobTablasEstInv = cobTablasEstInv;
    }

    public void listarAsesores() {
        try {
            if (legalAsesorList == null) {
                legalAsesorList = getLegalServ().listaAsesores(new LegalAsesor());
            }
        } catch (Exception e) {
        }
    }
    
    public List<LegalAsesor> getLegalAsesorList() {
        return legalAsesorList;
    }

    public void setLegalAsesorList(List<LegalAsesor> legalAsesorList) {
        this.legalAsesorList = legalAsesorList;
    }    

    /**
     * @return the mensajeRpta
     */
    public String getMensajeRpta() {
        return mensajeRpta;
    }

    /**
     * @param mensajeRpta the mensajeRpta to set
     */
    public void setMensajeRpta(String mensajeRpta) {
        this.mensajeRpta = mensajeRpta;
    }

    /**
     * @return the mostrarListaGastosJudiciales
     */
    public boolean isMostrarListaGastosJudiciales() {
        return mostrarListaGastosJudiciales;
    }

    /**
     * @param mostrarListaGastosJudiciales the mostrarListaGastosJudiciales to set
     */
    public void setMostrarListaGastosJudiciales(boolean mostrarListaGastosJudiciales) {
        this.mostrarListaGastosJudiciales = mostrarListaGastosJudiciales;
    }

    /**
     * @return the legGastoJudicialList
     */
    public List<LegGastoJudicial> getLegGastoJudicialList() {
        return legGastoJudicialList;
    }

    /**
     * @param legGastoJudicialList the legGastoJudicialList to set
     */
    public void setLegGastoJudicialList(List<LegGastoJudicial> legGastoJudicialList) {
        this.legGastoJudicialList = legGastoJudicialList;
    }

    /**
     * @return the mostrarAddGastoJudicial
     */
    public boolean isMostrarAddGastoJudicial() {
        return mostrarAddGastoJudicial;
    }

    /**
     * @param mostrarAddGastoJudicial the mostrarAddGastoJudicial to set
     */
    public void setMostrarAddGastoJudicial(boolean mostrarAddGastoJudicial) {
        this.mostrarAddGastoJudicial = mostrarAddGastoJudicial;
    }

    /**
     * @return the legGastoJudicialList2
     */
    public List<LegGastoJudicial> getLegGastoJudicialList2() {
        return legGastoJudicialList2;
    }

    /**
     * @param legGastoJudicialList2 the legGastoJudicialList2 to set
     */
    public void setLegGastoJudicialList2(List<LegGastoJudicial> legGastoJudicialList2) {
        this.legGastoJudicialList2 = legGastoJudicialList2;
    }

    /**
     * @return the etapa
     */
    public String getEtapa() {
        return etapa;
    }

    /**
     * @param etapa the etapa to set
     */
    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }
    
    /**
     * @return the legTiposGastosJudiList
     */
    public List<LegTabla> getLegTiposGastosJudiList() {
        return legTiposGastosJudiList;
    }

    /**
     * @param legTiposGastosJudiList the legTiposGastosJudiList to set
     */
    public void setLegTiposGastosJudiList(List<LegTabla> legTiposGastosJudiList) {
        this.legTiposGastosJudiList = legTiposGastosJudiList;
    }

    /**
     * @return the legEtapaList
     */
    public List<LegEtapa> getLegEtapaList() {
        return legEtapaList;
    }

    /**
     * @param legEtapaList the legEtapaList to set
     */
    public void setLegEtapaList(List<LegEtapa> legEtapaList) {
        this.legEtapaList = legEtapaList;
    }

    /**
     * @return the tipoGastoJudicial
     */
    public String getTipoGastoJudicial() {
        return tipoGastoJudicial;
    }

    /**
     * @param tipoGastoJudicial the tipoGastoJudicial to set
     */
    public void setTipoGastoJudicial(String tipoGastoJudicial) {
        this.tipoGastoJudicial = tipoGastoJudicial;
    }

    /**
     * @return the comentario
     */
    public String getComentario() {
        return comentario;
    }

    /**
     * @param comentario the comentario to set
     */
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    /**
     * @return the comprobanteNumero
     */
    public String getComprobanteNumero() {
        return comprobanteNumero;
    }

    /**
     * @param comprobanteNumero the comprobanteNumero to set
     */
    public void setComprobanteNumero(String comprobanteNumero) {
        this.comprobanteNumero = comprobanteNumero;
    }

    /**
     * @return the montoGastoJudicial
     */
    public String getMontoGastoJudicial() {
        return montoGastoJudicial;
    }

    /**
     * @param montoGastoJudicial the montoGastoJudicial to set
     */
    public void setMontoGastoJudicial(String montoGastoJudicial) {
        this.montoGastoJudicial = montoGastoJudicial;
    }

    /**
     * @return the fechaOperacion
     */
    public Date getFechaOperacion() {
        return fechaOperacion;
    }

    /**
     * @param fechaOperacion the fechaOperacion to set
     */
    public void setFechaOperacion(Date fechaOperacion) {
        this.fechaOperacion = fechaOperacion;
    }

    /**
     * @return the codigoTCHN
     */
    public String getCodigoTCHN() {
        return codigoTCHN;
    }

    /**
     * @param codigoTCHN the codigoTCHN to set
     */
    public void setCodigoTCHN(String codigoTCHN) {
        this.codigoTCHN = codigoTCHN;
    }

    /**
     * @return the fondo
     */
    public String getFondo() {
        return fondo;
    }

    /**
     * @param fondo the fondo to set
     */
    public void setFondo(String fondo) {
        this.fondo = fondo;
    }

    /**
     * @return the nroExpediente
     */
    public String getNroExpediente() {
        return nroExpediente;
    }

    /**
     * @param nroExpediente the nroExpediente to set
     */
    public void setNroExpediente(String nroExpediente) {
        this.nroExpediente = nroExpediente;
    }

    /**
     * @return the nroDocumento
     */
    public String getNroDocumento() {
        return nroDocumento;
    }

    /**
     * @param nroDocumento the nroDocumento to set
     */
    public void setNroDocumento(String nroDocumento) {
        this.nroDocumento = nroDocumento;
    }

    /**
     * @return the mostrarModificaGastoJudicial
     */
    public boolean isMostrarModificaGastoJudicial() {
        return mostrarModificaGastoJudicial;
    }

    /**
     * @param mostrarModificaGastoJudicial the mostrarModificaGastoJudicial to set
     */
    public void setMostrarModificaGastoJudicial(boolean mostrarModificaGastoJudicial) {
        this.mostrarModificaGastoJudicial = mostrarModificaGastoJudicial;
    }

    /**
     * @return the idLegGastoJudicial
     */
    public String getIdLegGastoJudicial() {
        return idLegGastoJudicial;
    }

    /**
     * @param idLegGastoJudicial the idLegGastoJudicial to set
     */
    public void setIdLegGastoJudicial(String idLegGastoJudicial) {
        this.idLegGastoJudicial = idLegGastoJudicial;
    }

    /**
     * @return the legMateriaOtroProcesoList
     */
    public List<LegTabla> getLegMateriaOtroProcesoList() {
        return legMateriaOtroProcesoList;
    }

    /**
     * @param legMateriaOtroProcesoList the legMateriaOtroProcesoList to set
     */
    public void setLegMateriaOtroProcesoList(List<LegTabla> legMateriaOtroProcesoList) {
        this.legMateriaOtroProcesoList = legMateriaOtroProcesoList;
    }

    /**
     * @return the legTipoOtroProcesoList
     */
    public List<LegTabla> getLegTipoOtroProcesoList() {
        return legTipoOtroProcesoList;
    }

    /**
     * @param legTipoOtroProcesoList the legTipoOtroProcesoList to set
     */
    public void setLegTipoOtroProcesoList(List<LegTabla> legTipoOtroProcesoList) {
        this.legTipoOtroProcesoList = legTipoOtroProcesoList;
    }

    /**
     * @return the legalOtroProcesoList
     */
    public List<LegOtroProceso> getLegalOtroProcesoList() {
        return legalOtroProcesoList;
    }

    /**
     * @param legalOtroProcesoList the legalOtroProcesoList to set
     */
    public void setLegalOtroProcesoList(List<LegOtroProceso> legalOtroProcesoList) {
        this.legalOtroProcesoList = legalOtroProcesoList;
    }

    /**
     * @return the mostrarAddOtroProceso
     */
    public boolean isMostrarAddOtroProceso() {
        return mostrarAddOtroProceso;
    }

    /**
     * @param mostrarAddOtroProceso the mostrarAddOtroProceso to set
     */
    public void setMostrarAddOtroProceso(boolean mostrarAddOtroProceso) {
        this.mostrarAddOtroProceso = mostrarAddOtroProceso;
    }

    /**
     * @return the demandado
     */
    public String getDemandado() {
        return demandado;
    }

    /**
     * @param demandado the demandado to set
     */
    public void setDemandado(String demandado) {
        this.demandado = demandado;
    }

    /**
     * @return the demandante
     */
    public String getDemandante() {
        return demandante;
    }

    /**
     * @param demandante the demandante to set
     */
    public void setDemandante(String demandante) {
        this.demandante = demandante;
    }

    /**
     * @return the materia
     */
    public String getMateria() {
        return materia;
    }

    /**
     * @param materia the materia to set
     */
    public void setMateria(String materia) {
        this.materia = materia;
    }

    /**
     * @return the tipoOtroProceso
     */
    public String getTipoOtroProceso() {
        return tipoOtroProceso;
    }

    /**
     * @param tipoOtroProceso the tipoOtroProceso to set
     */
    public void setTipoOtroProceso(String tipoOtroProceso) {
        this.tipoOtroProceso = tipoOtroProceso;
    }

    /**
     * @return the organoCompetente
     */
    public String getOrganoCompetente() {
        return organoCompetente;
    }

    /**
     * @param organoCompetente the organoCompetente to set
     */
    public void setOrganoCompetente(String organoCompetente) {
        this.organoCompetente = organoCompetente;
    }

    /**
     * @return the especialista
     */
    public String getEspecialista() {
        return especialista;
    }

    /**
     * @param especialista the especialista to set
     */
    public void setEspecialista(String especialista) {
        this.especialista = especialista;
    }

    /**
     * @return the descripcionOtroProceso
     */
    public String getDescripcionOtroProceso() {
        return descripcionOtroProceso;
    }

    /**
     * @param descripcionOtroProceso the descripcionOtroProceso to set
     */
    public void setDescripcionOtroProceso(String descripcionOtroProceso) {
        this.descripcionOtroProceso = descripcionOtroProceso;
    }

    /**
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the mostrarModiOtroProceso
     */
    public boolean isMostrarModiOtroProceso() {
        return mostrarModiOtroProceso;
    }

    /**
     * @param mostrarModiOtroProceso the mostrarModiOtroProceso to set
     */
    public void setMostrarModiOtroProceso(boolean mostrarModiOtroProceso) {
        this.mostrarModiOtroProceso = mostrarModiOtroProceso;
    }

    /**
     * @return the idOtroProceso
     */
    public int getIdOtroProceso() {
        return idOtroProceso;
    }

    /**
     * @param idOtroProceso the idOtroProceso to set
     */
    public void setIdOtroProceso(int idOtroProceso) {
        this.idOtroProceso = idOtroProceso;
    }

    /**
     * @return the mostrarAddSgtoOtroProceso
     */
    public boolean isMostrarAddSgtoOtroProceso() {
        return mostrarAddSgtoOtroProceso;
    }

    /**
     * @param mostrarAddSgtoOtroProceso the mostrarAddSgtoOtroProceso to set
     */
    public void setMostrarAddSgtoOtroProceso(boolean mostrarAddSgtoOtroProceso) {
        this.mostrarAddSgtoOtroProceso = mostrarAddSgtoOtroProceso;
    }

    /**
     * @return the mostrarVerSgtosOtroProceso
     */
    public boolean isMostrarVerSgtosOtroProceso() {
        return mostrarVerSgtosOtroProceso;
    }

    /**
     * @param mostrarVerSgtosOtroProceso the mostrarVerSgtosOtroProceso to set
     */
    public void setMostrarVerSgtosOtroProceso(boolean mostrarVerSgtosOtroProceso) {
        this.mostrarVerSgtosOtroProceso = mostrarVerSgtosOtroProceso;
    }

    /**
     * @return the legalSgtoOtroProcesoList
     */
    public List<LegSgtoOtroProceso> getLegalSgtoOtroProcesoList() {
        return legalSgtoOtroProcesoList;
    }

    /**
     * @param legalSgtoOtroProcesoList the legalSgtoOtroProcesoList to set
     */
    public void setLegalSgtoOtroProcesoList(List<LegSgtoOtroProceso> legalSgtoOtroProcesoList) {
        this.legalSgtoOtroProcesoList = legalSgtoOtroProcesoList;
    }

    /**
     * @return the mostrarModiSgtoOtroProceso
     */
    public boolean isMostrarModiSgtoOtroProceso() {
        return mostrarModiSgtoOtroProceso;
    }

    /**
     * @param mostrarModiSgtoOtroProceso the mostrarModiSgtoOtroProceso to set
     */
    public void setMostrarModiSgtoOtroProceso(boolean mostrarModiSgtoOtroProceso) {
        this.mostrarModiSgtoOtroProceso = mostrarModiSgtoOtroProceso;
    }

    /**
     * @return the idSegOtroProceso
     */
    public int getIdSegOtroProceso() {
        return idSegOtroProceso;
    }

    /**
     * @param idSegOtroProceso the idSegOtroProceso to set
     */
    public void setIdSegOtroProceso(int idSegOtroProceso) {
        this.idSegOtroProceso = idSegOtroProceso;
    }

}
