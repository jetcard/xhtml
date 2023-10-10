/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pop.webcobranzas.iface;

import java.util.List;
import pop.comun.dominio.MaeInversion;
import pop.comun.dominio.reporte.RepSaldoDeudor;

/**
 *
 * @author Jyoverar
 */
public interface IInversionServ {

    List<MaeInversion> listarResumen(Number oMaeInversion) throws Exception;
    
    List<MaeInversion> listar(MaeInversion oMaeInversion) throws Exception;
    
    List<MaeInversion> listarLegal(MaeInversion oMaeInversion) throws Exception;
    
    List<MaeInversion> listarDeudor(MaeInversion oMaeInversion) throws Exception;

    List<MaeInversion> listarRedInversion(MaeInversion oMaeInversion) throws Exception;
    
    List<MaeInversion> listarInmueble(MaeInversion oMaeInversion) throws Exception;
    
    List<MaeInversion> listarCronoEstado(MaeInversion oMaeInversion) throws Exception;

    RepSaldoDeudor reporteSaldoDeudor(MaeInversion oMaeInversion) throws Exception;

    MaeInversion inversionDocumento(MaeInversion oMaeInversion) throws Exception;

    List<MaeInversion> listarJudi(MaeInversion oMaeInversion) throws Exception;
}
