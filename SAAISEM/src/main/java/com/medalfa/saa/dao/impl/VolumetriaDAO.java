/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.medalfa.saa.dao.impl;

import com.medalfa.saa.model.Volumetria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP-MEDALFA
 */
public class VolumetriaDAO {

    Connection c;

    public VolumetriaDAO(Connection c) {
        this.c = c;
    }

    public boolean update(Volumetria v) {
        try {
            String queryString = "UPDATE volumetria "
                    + "SET "
                    + "pesoPieza = ?, "
                    + "unidadPesoPieza = ?, "
                    + "pesoCaja = ?, "
                    + "unidadPesoCaja = ?, "
                    + "pesoConcentrada = ?, "
                    + "unidadPesoConcentrada = ?, "
                    + "pesoTarima = ?, "
                    + "unidadPesoTarima = ?, "
                    + "altoPieza = ?, "
                    + "anchoPieza = ?, "
                    + "largoPieza = ?, "
                    + "unidadVolPieza = ?, "
                    + "altoCaja = ?, "
                    + "anchoCaja = ?, "
                    + "largoCaja = ?, "
                    + "unidadVolCaja = ?, "
                    + "altoConcentrada = ?, "
                    + "anchoConcentrada = ?, "
                    + "largoConcentrada = ?, "
                    + "unidadVolConcentrada = ?, "
                    + "altoTarima = ?, "
                    + "anchoTarima = ?, "
                    + "largoTarima = ?, "
                    + "unidadVolTarima = ? "
                    + "WHERE id = ?;";
            PreparedStatement ps = this.c.prepareStatement(queryString);
            ps.setDouble(1, v.getPesoPieza());
            ps.setString(2, v.getUnidadPesoPieza());
            ps.setDouble(3, v.getPesoCaja());
            ps.setString(4, v.getUnidadPesoCaja());
            ps.setDouble(5, v.getPesoConcentrada());
            ps.setString(6, v.getUnidadPesoConcentrada());
            ps.setDouble(7, v.getPesoTarima());
            ps.setString(8, v.getUnidadPesoTarima());
            ps.setDouble(9, v.getAltoPieza());
            ps.setDouble(10, v.getAnchoPieza());
            ps.setDouble(11, v.getLargoPieza());
            ps.setString(12, v.getUnidadVolPieza());
            ps.setDouble(13, v.getAltoCaja());
            ps.setDouble(14, v.getAnchoCaja());
            ps.setDouble(15, v.getLargoCaja());
            ps.setString(16, v.getUnidadVolCaja());
            ps.setDouble(17, v.getAltoConcentrada());
            ps.setDouble(18, v.getAnchoConcentrada());
            ps.setDouble(19, v.getLargoConcentrada());
            ps.setString(20, v.getUnidadVolConcentrada());
            ps.setDouble(21, v.getAltoTarima());
            ps.setDouble(22, v.getAnchoTarima());
            ps.setDouble(23, v.getLargoTarima());
            ps.setString(24, v.getUnidadVolTarima());
            ps.setInt(25, v.getId());
            System.out.println(ps);
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(VolumetriaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Volumetria save(Volumetria v) {
        try {
            String queryString = "INSERT INTO volumetria values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement ps = this.c.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, v.getId());
            ps.setDouble(2, v.getPesoPieza());
            ps.setString(3, v.getUnidadPesoPieza());
            ps.setDouble(4, v.getPesoCaja());
            ps.setString(5, v.getUnidadPesoCaja());
            ps.setDouble(6, v.getPesoConcentrada());
            ps.setString(7, v.getUnidadPesoConcentrada());
            ps.setDouble(8, v.getPesoTarima());
            ps.setString(9, v.getUnidadPesoTarima());
            ps.setDouble(10, v.getAltoPieza());
            ps.setDouble(11, v.getAnchoPieza());
            ps.setDouble(12, v.getLargoPieza());
            ps.setString(13, v.getUnidadVolPieza());
            ps.setDouble(14, v.getAltoCaja());
            ps.setDouble(15, v.getAnchoCaja());
            ps.setDouble(16, v.getLargoCaja());
            ps.setString(17, v.getUnidadVolCaja());
            ps.setDouble(18, v.getAltoConcentrada());
            ps.setDouble(19, v.getAnchoConcentrada());
            ps.setDouble(20, v.getLargoConcentrada());
            ps.setString(21, v.getUnidadVolConcentrada());
            ps.setDouble(22, v.getAltoTarima());
            ps.setDouble(23, v.getAnchoTarima());
            ps.setDouble(24, v.getLargoTarima());
            ps.setString(25, v.getUnidadVolTarima());

            Integer id = ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                v.setId(rs.getInt(1));
            }
            return v;
        } catch (SQLException ex) {
            Logger.getLogger(VolumetriaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Volumetria findById(Integer idVolumetria) {
        try {
            String queryString = "SELECT * from volumetria where id = ?";
            PreparedStatement ps = this.c.prepareStatement(queryString);
            ps.setInt(1, idVolumetria);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return this.buildEntity(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(VolumetriaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private Volumetria buildEntity(ResultSet rs) {
        try {
            Volumetria v = new Volumetria();
            v.setId(rs.getInt("id"));
            v.setPesoPieza(rs.getDouble("pesoPieza"));
            v.setUnidadPesoPieza(rs.getString("unidadPesoPieza"));
            v.setPesoCaja(rs.getDouble("pesoCaja"));
            v.setUnidadPesoCaja(rs.getString("unidadPesoCaja"));
            v.setPesoConcentrada(rs.getDouble("pesoConcentrada"));
            v.setUnidadPesoConcentrada(rs.getString("unidadPesoConcentrada"));
            v.setPesoTarima(rs.getDouble("pesoTarima"));
            v.setUnidadPesoTarima(rs.getString("unidadPesoTarima"));
            v.setAltoPieza(rs.getDouble("altoPieza"));
            v.setAnchoPieza(rs.getDouble("anchoPieza"));
            v.setLargoPieza(rs.getDouble("largoPieza"));
            v.setUnidadVolPieza(rs.getString("unidadVolPieza"));
            v.setAnchoCaja(rs.getDouble("anchoCaja"));
            v.setAltoCaja(rs.getDouble("altoCaja"));
            v.setLargoCaja(rs.getDouble("largoCaja"));
            v.setUnidadVolCaja(rs.getString("unidadVolCaja"));
            v.setAltoConcentrada(rs.getDouble("altoConcentrada"));
            v.setAnchoConcentrada(rs.getDouble("anchoConcentrada"));
            v.setLargoConcentrada(rs.getDouble("largoConcentrada"));
            v.setUnidadVolConcentrada(rs.getString("unidadVolConcentrada"));
            v.setAltoTarima(rs.getDouble("altoTarima"));
            v.setAnchoTarima(rs.getDouble("anchoTarima"));
            v.setLargoTarima(rs.getDouble("largoTarima"));
            v.setUnidadVolTarima(rs.getString("unidadVolTarima"));

            return v;
        } catch (SQLException ex) {
            Logger.getLogger(VolumetriaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
